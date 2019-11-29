package com.xgen.enronloader.dao;

import com.xgen.enronloader.enums.ComprehendAnalysisType;
import com.xgen.enronloader.models.Analysis;
import com.xgen.enronloader.models.Comprehend;
import com.xgen.enronloader.models.Comprehend.FieldDefs;
import com.xgen.enronloader.models.Document;
import com.xgen.enronloader.models.Entity;
import com.xgen.enronloader.models.KeyPhrase;
import com.xgen.enronloader.models.Sentiment;
import com.xgen.enronloader.models.SyntaxToken;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.bson.types.ObjectId;

public class DocumentDao extends BaseTDao<Document> {
  private static final String DB_NAME = "docrehend";
  private static final String COLLECTION_NAME = "documents";

  public DocumentDao() {
    super(DB_NAME, COLLECTION_NAME);
  }

  public Optional<Document> findById(final ObjectId pId) {
    return Optional.ofNullable(
        getCollection().find(new org.bson.Document(Document.FieldDefs.ID, pId)).first());
  }

  public List<Document> getDocumentsPendingAnalysis(
      final ComprehendAnalysisType pComprehendAnalysisType, final Integer pLimit) {
    final org.bson.Document query =
        new org.bson.Document()
            .append(
                getAnalysisFieldName(pComprehendAnalysisType),
                new org.bson.Document("$exists", false));

    final org.bson.Document projection =
        new org.bson.Document()
            .append(Document.FieldDefs.ID, 1)
            .append(Document.FieldDefs.CONTENT, 1);
    return getCollection()
        .find(query)
        .projection(projection)
        .limit(Optional.ofNullable(pLimit).orElse(25))
        .into(new ArrayList<>());
  }

  private String getAnalysisFieldName(final ComprehendAnalysisType pComprehendAnalysisType) {
    final String fieldName;
    fieldName = getComprehendFieldName(pComprehendAnalysisType);

    return joinFields(Document.FieldDefs.ANALYSIS, Analysis.FieldDefs.COMPREHEND, fieldName);
  }

  private String getComprehendFieldName(ComprehendAnalysisType pComprehendAnalysisType) {
    String fieldName;
    if (pComprehendAnalysisType == ComprehendAnalysisType.DetectEntities) {
      fieldName = Comprehend.FieldDefs.ENTITIES;
    } else if (pComprehendAnalysisType == ComprehendAnalysisType.KeyPhrases) {
      fieldName = Comprehend.FieldDefs.KEY_PHRASES;
    } else if (pComprehendAnalysisType == ComprehendAnalysisType.Sentiment) {
      fieldName = FieldDefs.SENTIMENT;
    } else if (pComprehendAnalysisType == ComprehendAnalysisType.Syntax) {
      fieldName = FieldDefs.SYNTAX_TOKENS;
    } else {
      throw new IllegalArgumentException(
          String.format("Unsupported comprehend type: %s", pComprehendAnalysisType));
    }
    return fieldName;
  }

  public void saveComprehendEntities(final ObjectId pDocumentId, final List<Entity> pEntities) {
    final org.bson.Document setQuery =
        new org.bson.Document(
            SET,
            new org.bson.Document(
                getAnalysisFieldName(ComprehendAnalysisType.DetectEntities), pEntities));
    getCollection().updateOne(new org.bson.Document(Document.FieldDefs.ID, pDocumentId), setQuery);
  }

  public void saveComprehendKeyPhrases(
      final ObjectId pDocumentId, final List<KeyPhrase> pKeyPhrases) {
    final org.bson.Document setQuery =
        new org.bson.Document(
            SET,
            new org.bson.Document(
                getAnalysisFieldName(ComprehendAnalysisType.KeyPhrases), pKeyPhrases));
    getCollection().updateOne(new org.bson.Document(Document.FieldDefs.ID, pDocumentId), setQuery);
  }

  public void saveComprehendSentiment(final ObjectId pDocumentId, final Sentiment pSentiment) {
    final org.bson.Document setQuery =
        new org.bson.Document(
            SET,
            new org.bson.Document(
                getAnalysisFieldName(ComprehendAnalysisType.Sentiment), pSentiment));
    getCollection().updateOne(new org.bson.Document(Document.FieldDefs.ID, pDocumentId), setQuery);
  }

  public void saveComprehendSyntaxTokens(
      final ObjectId pDocumentId, final List<SyntaxToken> pSyntaxTokens) {
    final org.bson.Document setQuery =
        new org.bson.Document(
            SET,
            new org.bson.Document(
                getAnalysisFieldName(ComprehendAnalysisType.Syntax), pSyntaxTokens));
    getCollection().updateOne(new org.bson.Document(Document.FieldDefs.ID, pDocumentId), setQuery);
  }
}
