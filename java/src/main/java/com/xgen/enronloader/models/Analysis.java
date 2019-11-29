package com.xgen.enronloader.models;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

@Getter
@EqualsAndHashCode
@Builder(toBuilder = true)
public class Analysis {

  private final Comprehend _comprehend;

  @BsonCreator
  public Analysis(@BsonProperty(FieldDefs.COMPREHEND) final Comprehend pComprehend) {
    _comprehend = pComprehend;
  }

  public static class FieldDefs {
    public static final String COMPREHEND = "comprehend";
  }
}
