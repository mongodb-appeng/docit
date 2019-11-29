package com.xgen.enronloader.models;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

@Getter
@EqualsAndHashCode
@Builder(toBuilder = true)
public class Entity {
  private final Float _score;
  private final String _type;
  private final String _text;
  private final Integer _beginOffset;
  private final Integer _endOffset;

  @BsonCreator
  public Entity(
      @BsonProperty(FieldDefs.SCORE) final Float pScore,
      @BsonProperty(FieldDefs.TYPE) final String pType,
      @BsonProperty(FieldDefs.TEXT) final String pText,
      @BsonProperty(FieldDefs.BEGIN_OFFSET) final Integer pBeginOffset,
      @BsonProperty(FieldDefs.END_OFFSET) final Integer pEndOffset) {
    _score = pScore;
    _type = pType;
    _text = pText;
    _beginOffset = pBeginOffset;
    _endOffset = pEndOffset;
  }

  public static final class FieldDefs {
    public static final String SCORE = "score";
    public static final String TYPE = "type";
    public static final String TEXT = "text";
    public static final String BEGIN_OFFSET = "beginOffset";
    public static final String END_OFFSET = "endOffset";
  }
}
