package vertx.mongodb.effect.functions;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.FindOneAndDeleteOptions;
import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import jsonvalues.JsObj;
import vertx.effect.Lambdac;
import vertx.effect.VIO;

import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;
import static vertx.mongodb.effect.MongoConverters.jsObj2Bson;


public class FindOneAndDelete implements Lambdac<JsObj, JsObj> {

    private static final FindOneAndDeleteOptions DEFAULT_OPTIONS = new FindOneAndDeleteOptions();
    private final Supplier<MongoCollection<JsObj>> collectionSupplier;
    private final FindOneAndDeleteOptions options;

    public FindOneAndDelete(final Supplier<MongoCollection<JsObj>> collectionSupplier) {
        this(collectionSupplier,
             DEFAULT_OPTIONS
            );
    }

    public FindOneAndDelete(final Supplier<MongoCollection<JsObj>> collectionSupplier,
                            final FindOneAndDeleteOptions options
                           ) {
        this.options = requireNonNull(options);
        this.collectionSupplier = requireNonNull(collectionSupplier);
    }


    @Override
    public VIO<JsObj> apply(final MultiMap context,
                            final JsObj query
                           ) {
        if (query == null) return VIO.fail(new IllegalArgumentException("query is null"));
        return VIO.effect(() -> {
            try {
                var collection = this.collectionSupplier.get();
                return Future.succeededFuture(collection.findOneAndDelete(jsObj2Bson.apply(query),
                                                                          options
                                                                         ));
            } catch (Exception exc) {
                return Future.failedFuture(Functions.toMongoValExc.apply(exc));

            }
        });

    }
}
