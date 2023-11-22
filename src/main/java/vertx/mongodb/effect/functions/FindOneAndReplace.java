package vertx.mongodb.effect.functions;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import jsonvalues.JsObj;
import vertx.effect.Lambdac;
import vertx.effect.VIO;
import vertx.mongodb.effect.UpdateMessage;

import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;
import static vertx.mongodb.effect.MongoConverters.jsObj2Bson;


public class FindOneAndReplace implements Lambdac<UpdateMessage, JsObj> {

    private static final FindOneAndReplaceOptions DEFAULT_OPTIONS = new FindOneAndReplaceOptions();
    private final FindOneAndReplaceOptions options;
    private final Supplier<MongoCollection<JsObj>> collectionSupplier;


    public FindOneAndReplace(final Supplier<MongoCollection<JsObj>> collectionSupplier,
                             final FindOneAndReplaceOptions options
                            ) {
        this.collectionSupplier = requireNonNull(collectionSupplier);
        this.options = requireNonNull(options);
    }

    public FindOneAndReplace(final Supplier<MongoCollection<JsObj>> collectionSupplier) {
        this(collectionSupplier,
             DEFAULT_OPTIONS
            );
    }


    @Override
    public VIO<JsObj> apply(final MultiMap context,
                            final UpdateMessage message
                           ) {
        if (message == null) return VIO.fail(new IllegalArgumentException("message is null"));


        return VIO.effect(() -> {
            try {
                var collection = requireNonNull(this.collectionSupplier.get());
                return Future.succeededFuture(collection
                                                      .findOneAndReplace(jsObj2Bson.apply(message.filter),
                                                                         message.update,
                                                                         options
                                                                        ));

            } catch (Exception exc) {
                return Future.failedFuture(Functions.toMongoValExc.apply(exc));
            }


        });
    }
}
