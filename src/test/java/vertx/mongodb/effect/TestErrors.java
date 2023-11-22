package vertx.mongodb.effect;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import jsonvalues.JsInt;
import jsonvalues.JsObj;
import jsonvalues.JsStr;
import mongovalues.JsValuesRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import vertx.effect.Failures;
import vertx.effect.VIO;
import vertx.mongodb.effect.functions.FindOne;

import java.util.function.Supplier;

import static vertx.mongodb.effect.MongoFailures.MONGO_CONNECT_TIMEOUT_CODE;
import static vertx.mongodb.effect.MongoFailures.MONGO_READ_TIMEOUT_CODE;

@ExtendWith(VertxExtension.class)
public class TestErrors {


    private static FindOne findOne;

    private static Supplier<MongoCollection<JsObj>> getMongoCollectionSupplier(String connectionString) {
        ConnectionString connString = new ConnectionString(connectionString);

        MongoClientSettings  settings =
                MongoClientSettings.builder()
                                   .applyConnectionString(connString)
                                   .codecRegistry(JsValuesRegistry.INSTANCE)
                                   .build();


        MongoClient mongoClient = MongoClients.create(settings);

        Supplier<MongoCollection<JsObj>> collection =
                () -> mongoClient.getDatabase("test")
                                 .getCollection("Data",
                                                JsObj.class
                                               );
        return collection;
    }

    @Test
    public void test_Socket_Timeout_One_MilliSecond(VertxTestContext context) {
        String connection = "mongodb://localhost:27017/?connectTimeoutMS=10000&socketTimeoutMS=1&serverSelectionTimeoutMS=10000";
        Supplier<MongoCollection<JsObj>> collection = getMongoCollectionSupplier(connection);

        findOne = new FindOne(collection);

        JsObj obj = JsObj.of("a",
                             JsStr.of("a"),
                             "b",
                             JsInt.of(1)
                            );
        //"java.util.concurrent.CompletionException: jio.JioFailure: Timeout while receiving message"
        findOne.apply(FindMessage.ofFilter(obj))
               .then(o -> VIO.FALSE,
                     e -> VIO.succeed(Failures.anyOf(MONGO_READ_TIMEOUT_CODE)
                                              .test(e)
                                     )
                    ).get()
               .onComplete(Verifiers.pipeTo(context));

    }

    /**
     * Se produce el timeout por connection timeout y se espera serverSelectionTimeoutMS (en este caso 10ms) antes de
     * dar la exception
     */
    @Test
    public void test_Connect_Timeout_One_MilliSecond(VertxTestContext context) {
        String connection = "mongodb://localhost:27017,localhost:27018,localhost:27019/?replicaSet=rs0?connectTimeoutMS=1&socketTimeoutMS=10000&serverSelectionTimeoutMS=10";
        Supplier<MongoCollection<JsObj>> collection = getMongoCollectionSupplier(connection);

        findOne = new FindOne(collection);
        JsObj obj = JsObj.of("a",
                             JsStr.of("a"),
                             "b",
                             JsInt.of(1)
                            );
//        "Timed out after 10 ms while waiting to connect. Client view of cluster state is {type=UNKNOWN, " +
//                "servers=[{address=localhost:27017, type=UNKNOWN, " +
//                "state=CONNECTING, exception={com.mongodb.MongoSocketReadTimeoutException: " +
//                "Timeout while receiving message}, caused by {java.net.SocketTimeoutException: Read timed out}}]
        findOne.apply(FindMessage.ofFilter(obj))
               .then(o -> VIO.TRUE,
                     e -> VIO.succeed(Failures.anyOf(MONGO_CONNECT_TIMEOUT_CODE)
                                              .test(e))
                    ).get().onComplete(Verifiers.pipeTo(context));

    }
}
