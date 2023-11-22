package vertx.mongodb.effect.functions;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;
import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import jsonvalues.JsObj;
import vertx.effect.Lambdac;
import vertx.effect.VIO;
import vertx.mongodb.effect.UpdateMessage;

import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;
import static vertx.mongodb.effect.MongoConverters.jsObj2Bson;


public class UpdateOne<O> implements Lambdac<UpdateMessage, O> {

    private static final UpdateOptions DEFAULT_OPTIONS = new UpdateOptions();
    public final Supplier<MongoCollection<JsObj>> collectionSupplier;
    public final Function<UpdateResult, O> resultConverter;
    public final UpdateOptions options;

    public UpdateOne(final Supplier<MongoCollection<JsObj>> collectionSupplier,
                     final Function<UpdateResult, O> resultConverter
                    ) {
        this.collectionSupplier = requireNonNull(collectionSupplier);
        this.resultConverter = requireNonNull(resultConverter);
        this.options = DEFAULT_OPTIONS;
    }

    public UpdateOne(final Supplier<MongoCollection<JsObj>> collectionSupplier,
                     final Function<UpdateResult, O> resultConverter,
                     final UpdateOptions options
                    ) {
        this.collectionSupplier = requireNonNull(collectionSupplier);
        this.resultConverter = requireNonNull(resultConverter);
        this.options = requireNonNull(options);
    }


    @Override
    public VIO<O> apply(final MultiMap context,
                        final UpdateMessage message
                       ) {
        if (message == null) return VIO.fail(new IllegalArgumentException("message is null"));

        return VIO.effect(() -> {
            try {
                var collection = requireNonNull(this.collectionSupplier.get());
                return Future.succeededFuture(resultConverter.apply(collection.updateOne(jsObj2Bson.apply(message.filter),
                                                                                         jsObj2Bson.apply(message.update),
                                                                                         options
                                                                                        )
                                                                   ));

            } catch (Exception exc) {
                return Future.failedFuture(Functions.toMongoValExc.apply(exc));
            }
        });
    }
}
