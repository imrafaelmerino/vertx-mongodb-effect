package vertx.mongodb.effect.functions;


import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.InsertOneOptions;
import com.mongodb.client.result.InsertOneResult;
import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import jsonvalues.JsObj;
import vertx.effect.Lambdac;
import vertx.effect.VIO;

import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

public class InsertOne<R> implements Lambdac<JsObj, R> {


    private static final InsertOneOptions DEFAULT_OPTIONS = new InsertOneOptions();
    private final Supplier<MongoCollection<JsObj>> collectionSupplier;
    private final InsertOneOptions options;
    private final Function<InsertOneResult, R> resultConverter;


    public InsertOne(final Supplier<MongoCollection<JsObj>> collectionSupplier,
                     final Function<InsertOneResult, R> resultConverter
                    ) {
        this(collectionSupplier,
             resultConverter,
             DEFAULT_OPTIONS
            );
    }

    public InsertOne(final Supplier<MongoCollection<JsObj>> collectionSupplier,
                     final Function<InsertOneResult, R> resultConverter,
                     final InsertOneOptions options
                    ) {
        this.collectionSupplier = requireNonNull(collectionSupplier);
        this.options = requireNonNull(options);
        this.resultConverter = requireNonNull(resultConverter);
    }

    @Override
    public VIO<R> apply(final MultiMap context,
                        final JsObj message
                       ) {
        if (message == null) return VIO.fail(new IllegalArgumentException("message is null"));

        return VIO.effect(() -> {
            try {
                var collection = requireNonNull(this.collectionSupplier.get());
                return Future.succeededFuture(resultConverter.apply(collection
                                                                            .insertOne(message,
                                                                                       options
                                                                                      )
                                                                   ));

            } catch (Exception exc) {
                return Future.failedFuture(Functions.toMongoValExc.apply(exc));

            }
        });
    }
}
