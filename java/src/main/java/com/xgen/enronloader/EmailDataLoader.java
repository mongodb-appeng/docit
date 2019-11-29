package com.xgen.enronloader;

import com.mongodb.MongoWriteException;
import com.xgen.enronloader.dao.DocumentDao;
import com.xgen.enronloader.models.Document;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmailDataLoader {
  private static Logger LOG = LoggerFactory.getLogger(EmailDataLoader.class.getName());
  private final File _rootDir;
  private final File _fileToProcess;
  private final DocumentDao _documentDao;
  private final File _resumeAfterFile;
  private int _documentCount;
  private boolean _resumeAfterFileFound = false;

  public EmailDataLoader(
      final File pRootDir, final File pFileToProcess, final File pResumeAfterFile) {
    _rootDir = pRootDir;
    _fileToProcess = pFileToProcess;
    _documentDao = new DocumentDao();
    _resumeAfterFile = pResumeAfterFile;
    _resumeAfterFileFound = _resumeAfterFile == null;
  }

  public void run() throws ParseException {
    processFile(_fileToProcess);
    LOG.info("Processed {} documents", _documentCount);
  }

  private void processFile(final File pFile) throws ParseException {
    _resumeAfterFileFound =
        _resumeAfterFileFound || _resumeAfterFile == null || _resumeAfterFile.equals(pFile);
    LOG.debug("Processing file: {}.  Reusme file found: {}", pFile, _resumeAfterFileFound);

    if (pFile.isDirectory()) {
      final File[] files = pFile.listFiles();
      if (files != null) {
        for (final File file : files) {
          processFile(file);
        }
      }
    }

    if (!_resumeAfterFileFound || pFile.equals(_resumeAfterFile)) {
      _documentCount++;
      return;
    }

    if (!pFile.getName().endsWith(".")) {
      return;
    }

    final String body;
    try (final FileInputStream in = new FileInputStream(pFile)) {
      body = IOUtils.toString(in, "utf-8");
    } catch (final IOException pE) {
      LOG.warn(
          "IOException while attempting to read email file {}.  File will be skipped", pFile, pE);
      return;
    }

    final String username = parseUsername(pFile);
    final String subject = parseField(body, EmailField.Subject).orElse(null);
    final Optional<String> dateString = parseField(body, EmailField.Date);
    final Date date = dateString.isPresent() ? parseDate(dateString.get()) : null;
    final EmailData emailData =
        EmailData.builder()
            .from(parseField(body, EmailField.From).orElse(null))
            .dateSent(date)
            .to(parseListField(body, EmailField.To))
            .subject(subject)
            .cc(parseListField(body, EmailField.Cc))
            .bcc(parseListField(body, EmailField.Bcc))
            .relativeFilePath(pFile.getPath().replace(_rootDir.getPath() + File.separator, ""))
            .folderPath(
                pFile
                    .getParent()
                    .replace(_rootDir.getPath() + File.separator + username + File.separator, ""))
            .username(username)
            .build();
    final Document document =
        Document.builder()
            .date(date)
            .author(username)
            .name(subject)
            .content(body)
            .emailData(emailData)
            .dataset("Enron Email Corpus")
            .build();

    try {
      _documentDao.getCollection().insertOne(document);
      LOG.debug("Inserted document: {}", _documentDao.toJson(document));
    } catch (final MongoWriteException pE) {
      if (pE.getMessage().contains("E11000 duplicate key error")) {
        LOG.info("Document already exists.  Skipping.");
      } else {
        throw pE;
      }
    }

    _documentCount++;
  }

  private List<String> parseListField(final String pBody, final EmailField pEmailField) {
    return parseField(pBody, pEmailField)
        .map(
            field ->
                Arrays.stream(field.split(","))
                    .map(s -> s.replace("\n", ""))
                    .map(s -> s.replace("\r", ""))
                    .map(String::trim)
                    .collect(Collectors.toList()))
        .orElse(List.of());
  }

  private Optional<String> parseField(final String pBody, final EmailField pEmailField) {
    final String emailFieldName = pEmailField.getFieldKey();
    final int origMessageIndex = pBody.indexOf("-----Original Message-----");

    final int startIndex = pBody.indexOf(emailFieldName);
    if (startIndex < 0) {
      return Optional.empty();
    }

    if (origMessageIndex > -1 && startIndex > origMessageIndex) {
      return Optional.empty();
    }

    final Optional<String> endFieldName =
        IntStream.range(0, EmailField.values().length)
            .filter(i -> i > pEmailField.ordinal())
            .mapToObj(i -> EmailField.values()[i])
            .map(EmailField::getFieldKey)
            .filter(ef -> pBody.contains(ef))
            .filter(ef -> origMessageIndex == -1 || pBody.indexOf(ef) < origMessageIndex)
            .findFirst();

    if (!endFieldName.isPresent()) {
      throw new IllegalStateException(
          String.format("Unable to find the end field name for field: %s", pEmailField));
    }

    final int endIndex = pBody.indexOf(endFieldName.get());
    if (startIndex >= endIndex) {
      return Optional.empty();
    }

    return Optional.of(pBody.substring(startIndex + emailFieldName.length(), endIndex).trim());
  }

  private String parseUsername(final File pFile) {
    return pFile
        .getPath()
        .replace(_rootDir.getPath() + File.separator, "")
        .split(File.separator)[0];
  }

  private Date parseDate(final String pDateString) throws ParseException {
    final SimpleDateFormat dateFormat =
        new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.US);

    return dateFormat.parse(pDateString);
  }

  private enum EmailField {
    Date("\nDate:"),
    From("\nFrom:"),
    To("\nTo:"),
    Subject("\nSubject:"),
    Cc("\nCc:"),
    MimeVersion("\nMime-Version:"),
    Bcc("\nBcc:"),
    XFrom("\nX-From:");

    private String _fieldKey;

    private EmailField(final String pFieldKey) {
      _fieldKey = pFieldKey;
    }

    public String getFieldKey() {
      return _fieldKey;
    }
  }

  public static void main(String[] args) {
    final File rootDir = new File(args[0]);
    final File fileToProcess = args.length == 1 ? rootDir : new File(args[1]);
    final File resumeAtFile = args.length > 2 ? new File(args[2]) : null;

    try {
      new EmailDataLoader(rootDir, fileToProcess, resumeAtFile).run();
    } catch (final Exception pE) {
      LOG.error("Exception parsing file.", pE);
    }
  }
}
