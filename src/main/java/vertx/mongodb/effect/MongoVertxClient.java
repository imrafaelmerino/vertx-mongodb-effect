package vertx.mongodb.effect;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import jsonvalues.JsObj;
import mongovalues.JsValuesRegistry;

import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

public final class MongoVertxClient extends AbstractVerticle {


    private final MongoClientSettings settings;
    public Function<String, MongoDatabase> getDatabase;
    private volatile MongoClient mongoClient;

    private Function<MongoDatabase, Function<String, MongoCollection<JsObj>>> getCollectionFromMongoDB;

    public MongoVertxClient(final MongoClientSettings settings) {
        this.settings = requireNonNull(settings);
    }

    public MongoVertxClient(final String connection) {

        ConnectionString connString = new ConnectionString(requireNonNull(connection));

        this.settings = MongoClientSettings.builder()
                                           .applyConnectionString(connString)
                                           .codecRegistry(JsValuesRegistry.INSTANCE)
                                           .build();
    }

    public Supplier<MongoCollection<JsObj>> getCollection(String db,
                                                          String collectionName
                                                         ) {
        return () -> {
            if (getDatabase == null)
                throw new NullPointerException("getDatabase function is null. Did you deploy the MongoVertxClient verticle?!");
            MongoDatabase database = getDatabase.apply(requireNonNull(db));
            return requireNonNull(this.getCollectionFromMongoDB)
                    .apply(database)
                    .apply(requireNonNull(collectionName));
        };
    }

    @Override
    public void start(final Promise<Void> startPromise) {
        MongoClient result = mongoClient;
        if (result == null) {
            synchronized (MongoVertxClient.class) {
                if (mongoClient == null) {
                    try {
                        mongoClient = result = MongoClients.create(requireNonNull(settings));
                        getDatabase = name -> mongoClient.getDatabase(requireNonNull(name));
                        getCollectionFromMongoDB =
                                db -> name -> requireNonNull(db).getCollection(requireNonNull(name),
                                                                               JsObj.class
                                                                              );
                        startPromise.complete();
                    } catch (Exception error) {
                        startPromise.fail(error);
                    }
                } else {
                    result = mongoClient;
                    startPromise.complete();
                }
            }
        } else startPromise.complete();
    }


}
