package com.xgen.enronloader;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.comprehend.AmazonComprehend;
import com.amazonaws.services.comprehend.AmazonComprehendClientBuilder;
import com.amazonaws.services.comprehend.model.DetectEntitiesRequest;
import com.amazonaws.services.comprehend.model.DetectEntitiesResult;
import com.amazonaws.services.comprehend.model.DetectKeyPhrasesRequest;
import com.amazonaws.services.comprehend.model.DetectKeyPhrasesResult;
import com.amazonaws.services.comprehend.model.DetectSentimentRequest;
import com.amazonaws.services.comprehend.model.DetectSentimentResult;
import com.amazonaws.services.comprehend.model.DetectSyntaxRequest;
import com.amazonaws.services.comprehend.model.DetectSyntaxResult;
import com.amazonaws.services.comprehend.model.Entity;
import com.amazonaws.services.comprehend.model.KeyPhrase;
import com.amazonaws.services.comprehend.model.SentimentScore;
import com.amazonaws.services.comprehend.model.SyntaxToken;
import com.xgen.enronloader.dao.DocumentDao;
import com.xgen.enronloader.enums.ComprehendAnalysisType;
import com.xgen.enronloader.models.Document;
import com.xgen.enronloader.models.Sentiment;
import com.xgen.enronloader.models.SyntaxToken.PartOfSpeech;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AWSComprehendDataLoader {
  private static Logger LOG = LoggerFactory.getLogger(AWSComprehendDataLoader.class.getName());
  private final AWSCredentialsProvider _awsCredentialsProvider;
  private final DocumentDao _documentDao;
  private final AmazonComprehend _comprehendClient;

  public AWSComprehendDataLoader() {
    final AWSCredentials awsCredentials =
        new BasicAWSCredentials(
            System.getProperty("aws.accessKey"), System.getProperty("aws.secretKey"));
    _awsCredentialsProvider = new AWSStaticCredentialsProvider(awsCredentials);
    _documentDao = new DocumentDao();
    _comprehendClient =
        AmazonComprehendClientBuilder.standard()
            .withCredentials(_awsCredentialsProvider)
            .withRegion("us-east-1")
            .build();
  }

  private String cleanText(final String pText) {
    final String emailBody;
    final int headerEndIndex = pText.indexOf("\nX-FileName");
    if (headerEndIndex > -1) {
      final int headerLineEndIndex = pText.indexOf("\n", headerEndIndex + 1);
      emailBody = pText.substring(headerLineEndIndex, pText.length());
    } else {
      emailBody = pText;
    }

    final String cleanText = emailBody.replace("\r\n", " ").replace("\n", " ").replace("\r", " ");
    if (cleanText.length() > 5000) {
      return cleanText.substring(0, 5000);
    }

    return cleanText;
  }

  public void run(
      final List<ComprehendAnalysisType> pComprehendAnalysisTypes,
      final int pBatchSize,
      final int pLimit) {
    int documentCount = 0;
    for (final ComprehendAnalysisType t : pComprehendAnalysisTypes) {
      List<Document> documents;
      while ((documents = _documentDao.getDocumentsPendingAnalysis(t, pBatchSize)).size() > 0) {
        for (final Document document : documents) {
          if (documentCount >= pLimit) {
            LOG.info("Batch limit of {} reached.  Exiting.", pLimit);
            return;
          }

          run(document, t);
          documentCount++;
        }
      }
    }
  }

  public void run(
      final ObjectId pDocumentId, final List<ComprehendAnalysisType> pComprehendAnalysisType) {
    final Document document =
        _documentDao
            .findById(pDocumentId)
            .orElseThrow(
                () ->
                    new IllegalArgumentException(
                        String.format("Document not found for documentId: %s", pDocumentId)));

    pComprehendAnalysisType.forEach(t -> run(document, t));
  }

  private void run(final Document pDocument, final ComprehendAnalysisType pComprehendAnalysisType) {
    LOG.info(
        "Processing document {} with analysis type: {}",
        pDocument.getId(),
        pComprehendAnalysisType);
    if (pComprehendAnalysisType == ComprehendAnalysisType.DetectEntities) {
      addEntityData(pDocument);
    } else if (pComprehendAnalysisType == ComprehendAnalysisType.KeyPhrases) {
      addKeyPhrasesData(pDocument);
    } else if (pComprehendAnalysisType == ComprehendAnalysisType.Sentiment) {
      addSentimentData(pDocument);
    } else if (pComprehendAnalysisType == ComprehendAnalysisType.Syntax) {
      addSyntaxData(pDocument);
    } else {
      throw new IllegalArgumentException(
          String.format("Unsupported analysis type: %s", pComprehendAnalysisType));
    }
  }

  private void addEntityData(final Document pDocument) {
    LOG.debug("Calling DetectEntities");
    final DetectEntitiesRequest detectEntitiesRequest =
        new DetectEntitiesRequest()
            .withText(cleanText(pDocument.getContent()))
            .withLanguageCode("en");
    final DetectEntitiesResult detectEntitiesResult =
        _comprehendClient.detectEntities(detectEntitiesRequest);

    final List<Entity> comprehendEntities = detectEntitiesResult.getEntities();
    LOG.debug("Returned {} entities", comprehendEntities.size());

    final List<com.xgen.enronloader.models.Entity> entities =
        comprehendEntities.stream()
            .map(
                e ->
                    com.xgen.enronloader.models.Entity.builder()
                        .score(e.getScore())
                        .text(e.getText())
                        .type(e.getType())
                        .beginOffset(e.getBeginOffset())
                        .endOffset(e.getEndOffset())
                        .build())
            .collect(Collectors.toList());

    _documentDao.saveComprehendEntities(pDocument.getId(), entities);
  }

  private void addKeyPhrasesData(final Document pDocument) {
    LOG.debug("Calling DetectKeyPhrases");
    final DetectKeyPhrasesRequest detectKeyPhrasesRequest =
        new DetectKeyPhrasesRequest()
            .withText(cleanText(pDocument.getContent()))
            .withLanguageCode("en");
    final DetectKeyPhrasesResult detectKeyPhrasesResult =
        _comprehendClient.detectKeyPhrases(detectKeyPhrasesRequest);

    final List<KeyPhrase> comprehendKeyPhrases = detectKeyPhrasesResult.getKeyPhrases();
    LOG.debug("Returned {} keyPhrases", comprehendKeyPhrases.size());

    final List<com.xgen.enronloader.models.KeyPhrase> keyPhrases =
        comprehendKeyPhrases.stream()
            .map(
                k ->
                    com.xgen.enronloader.models.KeyPhrase.builder()
                        .score(k.getScore())
                        .text(k.getText())
                        .beginOffset(k.getBeginOffset())
                        .endOffset(k.getEndOffset())
                        .build())
            .collect(Collectors.toList());

    _documentDao.saveComprehendKeyPhrases(pDocument.getId(), keyPhrases);
  }

  private void addSentimentData(final Document pDocument) {
    LOG.debug("Calling DetectSentiment");
    final DetectSentimentRequest detectSentimentRequest =
        new DetectSentimentRequest()
            .withText(cleanText(pDocument.getContent()))
            .withLanguageCode("en");
    final DetectSentimentResult detectSentimentResult =
        _comprehendClient.detectSentiment(detectSentimentRequest);

    final String comprehendSentiment = detectSentimentResult.getSentiment();
    final SentimentScore comprehendSentimentScore = detectSentimentResult.getSentimentScore();
    LOG.debug("Returned {} sentiment", comprehendSentiment);

    final Sentiment sentiment =
        Sentiment.builder()
            .sentiment(comprehendSentiment)
            .sentimantScore(
                Sentiment.SentimentScore.builder()
                    .mixed(comprehendSentimentScore.getMixed())
                    .positive(comprehendSentimentScore.getPositive())
                    .neutral(comprehendSentimentScore.getNeutral())
                    .negative(comprehendSentimentScore.getNegative())
                    .build())
            .build();

    _documentDao.saveComprehendSentiment(pDocument.getId(), sentiment);
  }

  private void addSyntaxData(final Document pDocument) {
    LOG.debug("Calling DetectSyntax");
    final DetectSyntaxRequest detectSyntaxRequest =
        new DetectSyntaxRequest()
            .withText(cleanText(pDocument.getContent()))
            .withLanguageCode("en");
    final DetectSyntaxResult detectSyntaxResult =
        _comprehendClient.detectSyntax(detectSyntaxRequest);

    final List<SyntaxToken> comprehendSyntaxTokens = detectSyntaxResult.getSyntaxTokens();

    final List<com.xgen.enronloader.models.SyntaxToken> syntaxTokens =
        comprehendSyntaxTokens.stream()
            .map(
                k ->
                    com.xgen.enronloader.models.SyntaxToken.builder()
                        .text(k.getText())
                        .beginOffset(k.getBeginOffset())
                        .endOffset(k.getEndOffset())
                        .partOfSpeech(
                            PartOfSpeech.builder()
                                .tag(k.getPartOfSpeech().getTag())
                                .score(k.getPartOfSpeech().getScore())
                                .build())
                        .build())
            .collect(Collectors.toList());

    _documentDao.saveComprehendSyntaxTokens(pDocument.getId(), syntaxTokens);
  }

  public static void main(String[] args) {
    final List<ComprehendAnalysisType> pComprehendAnalysisTypes =
        Arrays.stream(args[1].split(","))
            .map(String::trim)
            .map(ComprehendAnalysisType::valueOf)
            .collect(Collectors.toList());

    final String documentId = args[0];
    if (!"[all]".equals(documentId)) {
      new AWSComprehendDataLoader().run(new ObjectId(args[0]), pComprehendAnalysisTypes);
    } else {
      final int batchSize = Integer.parseInt(args[2]);
      final int limit = Integer.parseInt(args[3]);
      new AWSComprehendDataLoader().run(pComprehendAnalysisTypes, batchSize, limit);
    }
  }
}
