package vertx.mongodb.effect.functions;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import jsonvalues.JsObj;
import vertx.effect.Lambdac;
import vertx.effect.VIO;
import vertx.mongodb.effect.UpdateMessage;

import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;
import static vertx.mongodb.effect.MongoConverters.jsObj2Bson;


public class FindOneAndUpdate implements Lambdac<UpdateMessage, JsObj> {

    private static final FindOneAndUpdateOptions DEFAULT_OPTIONS = new FindOneAndUpdateOptions();
    private final FindOneAndUpdateOptions options;
    private final Supplier<MongoCollection<JsObj>> collectionSupplier;


    public FindOneAndUpdate(final Supplier<MongoCollection<JsObj>> collectionSupplier) {
        this(collectionSupplier,
             DEFAULT_OPTIONS
            );
    }

    public FindOneAndUpdate(final Supplier<MongoCollection<JsObj>> collectionSupplier,
                            final FindOneAndUpdateOptions options
                           ) {
        this.collectionSupplier = requireNonNull(collectionSupplier);
        this.options = requireNonNull(options);
    }

    @Override
    public VIO<JsObj> apply(final MultiMap context,
                            final UpdateMessage message
                           ) {
        if (message == null) return VIO.fail(new IllegalArgumentException("message is null"));


        return VIO.effect(() -> {
            try {
                var collection = this.collectionSupplier.get();

                return Future.succeededFuture(collection
                                                      .findOneAndUpdate(jsObj2Bson.apply(message.filter),
                                                                        jsObj2Bson.apply(message.update),
                                                                        options
                                                                       ));
            } catch (Exception exc) {
                return Future.failedFuture(Functions.toMongoValExc.apply(exc));
            }
        });
    }
}
