package com.xgen.enronloader.dao;

import static org.bson.codecs.configuration.CodecRegistries.fromCodecs;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.io.StringWriter;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import org.bson.codecs.Codec;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.ClassModelBuilder;
import org.bson.codecs.pojo.Convention;
import org.bson.codecs.pojo.Conventions;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.codecs.pojo.PropertySerialization;
import org.bson.json.JsonWriter;

public abstract class BaseTDao<T> {
  private final CodecRegistry CODEC_REGISTRY = initializeCodecRegistry();
  private static final EncoderContext ENCODER_CONTEXT = EncoderContext.builder().build();
  protected static final String SET = "$set";

  private final MongoClient _mongoClient;
  private final String _dbName;
  private final String _collectionName;
  private final Class<T> _encoderClass;

  protected MongoClient getMongoClient() {
    return _mongoClient;
  }

  protected MongoDatabase getDatabase(final String pDBName) {
    return getMongoClient().getDatabase(pDBName);
  }

  protected String getDBName() {
    return _dbName;
  }

  protected String getCollectionName() {
    return _collectionName;
  }

  public MongoCollection<T> getCollection() {
    return getDatabase(getDBName())
        .withCodecRegistry(getCodecRegistry())
        .getCollection(getCollectionName(), getEncoderClass());
  }

  private Class<T> getEncoderClass() {
    return _encoderClass;
  }

  @SuppressWarnings("unchecked")
  protected BaseTDao(final String pDbName, final String pCollectionName) {
    _mongoClient =
        new MongoClient(
            new MongoClientURI(
                System.getProperty(
                    "db.uri", "mongodb+srv://localhost/admin?retryWrites=true&w=majority")));

    _dbName = pDbName;
    _collectionName = pCollectionName;

    // unchecked
    // Note: this will only work for direct descendents of BaseTDao
    _encoderClass =
        (Class<T>)
            ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
  }

  public CodecRegistry getCodecRegistry() {
    return CODEC_REGISTRY;
  }

  protected CodecRegistry initializeCodecRegistry() {
    final List<Convention> conventions = getPojoCodecConventions();

    return fromRegistries(
        MongoClient.getDefaultCodecRegistry(),
        fromCodecs(List.of()),
        // PojoCodecProvider should be the last register in the list
        fromProviders(
            PojoCodecProvider.builder().automatic(true).conventions(conventions).build()));
  }

  protected List<Convention> getPojoCodecConventions() {
    final ArrayList<Convention> conventions = new ArrayList<>(Conventions.DEFAULT_CONVENTIONS);
    conventions.add(new SerializeAllConvention());
    return conventions;
  }

  private static class SerializeAllConvention implements Convention {
    @Override
    @SuppressWarnings("unchecked")
    public void apply(final ClassModelBuilder<?> classModelBuilder) {
      classModelBuilder
          .getPropertyModelBuilders()
          .forEach(pmb -> pmb.propertySerialization((PropertySerialization) value -> true));
    }
  }

  @SuppressWarnings("unchecked")
  public String toJson(final T pDocument) {
    final Codec<T> codec = getCodecRegistry().get((Class<T>) pDocument.getClass());

    final StringWriter writer = new StringWriter();
    codec.encode(new JsonWriter(writer), pDocument, ENCODER_CONTEXT);

    return writer.toString();
  }

  protected String joinFields(final String... pFields) {
    return String.join(".", pFields);
  }
}
