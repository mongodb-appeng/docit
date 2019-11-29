package com.xgen.enronloader.models;

import com.xgen.enronloader.EmailData;
import java.util.Date;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

@Getter
@EqualsAndHashCode
@Builder(toBuilder = true)
public class Document {

  @EqualsAndHashCode.Exclude final ObjectId _id;
  private final EmailData _emailData;
  private final String _author;
  private final String _name;
  private final String _content;
  private final Date _date;
  private final String _dataset;

  private final Analysis _analysis;

  @BsonCreator
  public Document(
      @BsonId final ObjectId pId,
      @BsonProperty(FieldDefs.EMAIL_DATA) final EmailData pEmailData,
      @BsonProperty(FieldDefs.AUTHOR) final String pAuthor,
      @BsonProperty(FieldDefs.NAME) final String pName,
      @BsonProperty(FieldDefs.CONTENT) final String pContent,
      @BsonProperty(FieldDefs.DATE) final Date pDate,
      @BsonProperty(FieldDefs.DATASET) final String pDataset,
      @BsonProperty(FieldDefs.ANALYSIS) final Analysis pAnalysis) {
    _id = pId;
    _emailData = pEmailData;
    _author = pAuthor;
    _name = pName;
    _content = pContent;
    _date = pDate;
    _dataset = pDataset;

    _analysis = pAnalysis;
  }

  public static class FieldDefs {
    public static final String ID = "_id";
    public static final String EMAIL_DATA = "emailData";
    public static final String AUTHOR = "author";
    public static final String NAME = "name";
    public static final String CONTENT = "content";
    public static final String DATE = "date";
    public static final String DATASET = "dataset";
    public static final String ANALYSIS = "analysis";
  }
}
