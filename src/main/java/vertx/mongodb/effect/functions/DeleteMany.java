package vertx.mongodb.effect.functions;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.DeleteOptions;
import com.mongodb.client.result.DeleteResult;
import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import jsonvalues.JsObj;
import vertx.effect.Lambdac;
import vertx.effect.VIO;

import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;
import static vertx.mongodb.effect.MongoConverters.jsObj2Bson;


public final class DeleteMany<O> implements Lambdac<JsObj, O> {

    private static final DeleteOptions DEFAULT_OPTIONS = new DeleteOptions();
    private final Supplier<MongoCollection<JsObj>> collectionSupplier;
    private final Function<DeleteResult, O> resultConverter;
    private final DeleteOptions options;


    public DeleteMany(final Supplier<MongoCollection<JsObj>> collectionSupplier,
                      final Function<DeleteResult, O> resultConverter,
                      final DeleteOptions options
                     ) {
        this.collectionSupplier = requireNonNull(collectionSupplier);
        this.resultConverter = requireNonNull(resultConverter);
        this.options = requireNonNull(options);
    }

    public DeleteMany(final Supplier<MongoCollection<JsObj>> collectionSupplier,
                      final Function<DeleteResult, O> resultConverter
                     ) {
        this(collectionSupplier,
             resultConverter,
             DEFAULT_OPTIONS
            );
    }

    @Override
    public VIO<O> apply(final MultiMap context,
                        final JsObj query
                       ) {
        if (query == null) return VIO.fail(new IllegalArgumentException("query is null"));

        return VIO.effect(() -> {
                              try {
                                  var collection = requireNonNull(this.collectionSupplier.get());
                                  return Future.succeededFuture(resultConverter.apply(collection.deleteMany(jsObj2Bson.apply(query),
                                                                                                            options)));
                              } catch (Exception exc) {
                                  return Future.failedFuture(exc);
                              }
                          }
                         );

    }
}
