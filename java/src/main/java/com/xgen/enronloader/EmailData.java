package com.xgen.enronloader;

import java.util.Date;
import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

@Getter
@EqualsAndHashCode
@Builder(toBuilder = true)
public class EmailData {
  private String _username;
  private Date _dateSent;
  private final String _from;
  private final List<String> _to;
  private final String _subject;
  private final List<String> _cc;
  private final List<String> _bcc;
  private final String _folderPath;
  private final String _relativeFilePath;

  @BsonCreator
  public EmailData(
      @BsonProperty(FieldDefs.USERNAME) final String pUsername,
      @BsonProperty(FieldDefs.DATE) final Date pDate,
      @BsonProperty(FieldDefs.FROM) final String pFrom,
      @BsonProperty(FieldDefs.TO) final List<String> pTo,
      @BsonProperty(FieldDefs.SUBJECT) final String pSubject,
      @BsonProperty(FieldDefs.CC) final List<String> pCC,
      @BsonProperty(FieldDefs.BCC) final List<String> pBCC,
      @BsonProperty(FieldDefs.FOLDER_PATH) final String pFolderPath,
      @BsonProperty(FieldDefs.RELATIVE_FILE_PATH) final String pRelativeFilePath) {
    _username = pUsername;
    _from = pFrom;
    _dateSent = pDate;
    _to = pTo;
    _subject = pSubject;
    _cc = pCC;
    _bcc = pBCC;
    _folderPath = pFolderPath;
    _relativeFilePath = pRelativeFilePath;
  }

  public static class FieldDefs {
    public static final String USERNAME = "username";
    public static final String DATE = "date";
    public static final String FROM = "from";
    public static final String TO = "to";
    public static final String SUBJECT = "subject";
    public static final String CC = "cc";
    public static final String BCC = "bcc";
    public static final String FOLDER_PATH = "folderPath";
    public static final String RELATIVE_FILE_PATH = "relativeFilePath";
  }
}
