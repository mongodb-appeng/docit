package com.xgen.enronloader.models;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

@Getter
@EqualsAndHashCode
@Builder(toBuilder = true)
public class Sentiment {
  private final String _sentiment;
  private final SentimentScore _sentimantScore;

  @BsonCreator
  public Sentiment(
      @BsonProperty(FieldDefs.SENTIMENT) final String pSentiment,
      @BsonProperty(FieldDefs.SENTIMENT_SCORE) final SentimentScore pSentimentScore) {
    _sentiment = pSentiment;
    _sentimantScore = pSentimentScore;
  }

  public static final class FieldDefs {
    public static final String SENTIMENT = "sentiment";
    public static final String SENTIMENT_SCORE = "sentimentScore";
  }

  @Getter
  @EqualsAndHashCode
  @Builder(toBuilder = true)
  public static class SentimentScore {
    private Float _mixed;
    private Float _positive;
    private Float _neutral;
    private Float _negative;

    @BsonCreator
    public SentimentScore(
        @BsonProperty(FieldDefs.MIXED) final Float pMixed,
        @BsonProperty(FieldDefs.POSITIVE) final Float pPositive,
        @BsonProperty(FieldDefs.NEUTRAL) final Float pNeutral,
        @BsonProperty(FieldDefs.NEGATIVE) final Float pNegative) {
      _mixed = pMixed;
      _positive = pPositive;
      _neutral = pNeutral;
      _negative = pNegative;
    }

    public static final class FieldDefs {
      public static final String MIXED = "mixed";
      public static final String POSITIVE = "positive";
      public static final String NEUTRAL = "neutral";
      public static final String NEGATIVE = "negative";
    }
  }
}
