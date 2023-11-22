package vertx.mongodb.effect;


import com.mongodb.client.ChangeStreamIterable;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import jsonvalues.JsObj;

import java.util.function.Consumer;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

public final class Watcher extends AbstractVerticle {

    public final Supplier<MongoCollection<JsObj>> collectionSupplier;
    public final Consumer<ChangeStreamIterable<JsObj>> consumer;
    private ClientSession session;

    public Watcher(final Supplier<MongoCollection<JsObj>> collectionSupplier,
                   final Consumer<ChangeStreamIterable<JsObj>> consumer
                  ) {
        this.collectionSupplier = requireNonNull(collectionSupplier);
        this.consumer = requireNonNull(consumer);
    }

    public Watcher(final Supplier<MongoCollection<JsObj>> collectionSupplier,
                   final Consumer<ChangeStreamIterable<JsObj>> consumer,
                   final ClientSession session
                  ) {
        this(collectionSupplier,
             consumer
            );
        this.session = session;
    }

    @Override
    public void start(final Promise<Void> promise) {
        try {
            if (session != null)
                consumer.accept(collectionSupplier.get()
                                                  .watch(session));
            else
                consumer.accept(collectionSupplier.get()
                                                  .watch());
            promise.complete();
        } catch (Exception e) {
            promise.fail(e);
        }
    }


}
