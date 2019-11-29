package com.xgen.enronloader.models;

import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

@Getter
@EqualsAndHashCode
@Builder(toBuilder = true)
public class Comprehend {

  public final List<Entity> _entities;
  public final List<KeyPhrase> _keyPhrases;
  public final Sentiment _sentiment;
  private final List<SyntaxToken> _syntaxTokens;

  @BsonCreator
  public Comprehend(
      @BsonProperty(FieldDefs.ENTITIES) final List<Entity> pEntities,
      @BsonProperty(FieldDefs.KEY_PHRASES) final List<KeyPhrase> pKeyPhrases,
      @BsonProperty(FieldDefs.SENTIMENT) final Sentiment pSentiment,
      @BsonProperty(FieldDefs.SYNTAX_TOKENS) final List<SyntaxToken> pSyntaxTokens) {
    _entities = pEntities;
    _keyPhrases = pKeyPhrases;
    _sentiment = pSentiment;
    _syntaxTokens = pSyntaxTokens;
  }

  public static class FieldDefs {
    public static final String ENTITIES = "entities";
    public static final String KEY_PHRASES = "keyPhrases";
    public static final String SENTIMENT = "sentiment";
    public static final String SYNTAX_TOKENS = "syntaxTokens";
  }
}
