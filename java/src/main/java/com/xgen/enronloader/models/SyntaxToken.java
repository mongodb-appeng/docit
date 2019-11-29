package com.xgen.enronloader.models;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

@Getter
@EqualsAndHashCode
@Builder(toBuilder = true)
public class SyntaxToken {
  private final String _text;
  private Integer _tokenId;
  private final Integer _beginOffset;
  private final Integer _endOffset;

  private final PartOfSpeech _partOfSpeech;

  @BsonCreator
  public SyntaxToken(
      @BsonProperty(FieldDefs.TEXT) final String pText,
      @BsonProperty(FieldDefs.TOKEN_ID) final Integer pTokenId,
      @BsonProperty(FieldDefs.BEGIN_OFFSET) final Integer pBeginOffset,
      @BsonProperty(FieldDefs.END_OFFSET) final Integer pEndOffset,
      @BsonProperty(FieldDefs.PART_OF_SPEECH) final PartOfSpeech pPartOfSpeech) {
    _text = pText;
    _tokenId = pTokenId;
    _beginOffset = pBeginOffset;
    _endOffset = pEndOffset;
    _partOfSpeech = pPartOfSpeech;
  }

  public static final class FieldDefs {
    public static final String TEXT = "text";
    public static final String TOKEN_ID = "tokenId";
    public static final String BEGIN_OFFSET = "beginOffset";
    public static final String END_OFFSET = "endOffset";
    public static final String PART_OF_SPEECH = "partOfSpeech";
  }

  @Getter
  @EqualsAndHashCode
  @Builder(toBuilder = true)
  public static class PartOfSpeech {
    private String _tag;
    private Float _score;

    public PartOfSpeech(
        @BsonProperty(FieldDefs.TAG) final String pTag,
        @BsonProperty(FieldDefs.SCORE) final Float pScore) {
      _tag = pTag;
      _score = pScore;
    }

    public static final class FieldDefs {
      public static final String TAG = "tag";
      public static final String SCORE = "score";
    }
  }
}
