package vertx.mongodb.effect.functions;


import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import jsonvalues.JsArray;
import jsonvalues.JsObj;
import vertx.effect.Lambdac;
import vertx.effect.VIO;
import vertx.mongodb.effect.MongoConverters;

import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;


public final class Aggregate<O> implements Lambdac<JsArray, O> {

    public final Function<AggregateIterable<JsObj>, O> resultConverter;
    public final Supplier<MongoCollection<JsObj>> collectionSupplier;

    public Aggregate(final Supplier<MongoCollection<JsObj>> collectionSupplier,
                     final Function<AggregateIterable<JsObj>, O> resultConverter
                    ) {
        this.resultConverter = resultConverter;
        this.collectionSupplier = collectionSupplier;
    }

    @Override
    public VIO<O> apply(final MultiMap context,
                        final JsArray m
                       ) {
        return VIO.effect(() -> {
            try {
                var pipeline = MongoConverters.jsArray2ListOfBson.apply(m);
                var collection = requireNonNull(this.collectionSupplier.get());
                return Future.succeededFuture(resultConverter.apply(collection.aggregate(pipeline)));
            } catch (Exception exc) {
                return Future.failedFuture(Functions.toMongoValExc.apply(exc));
            }
        });

    }
}
