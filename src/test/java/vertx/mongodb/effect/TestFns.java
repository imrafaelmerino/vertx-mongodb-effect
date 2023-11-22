package vertx.mongodb.effect;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.junit5.VertxTestContext;

import java.util.function.Consumer;

public class TestFns {


    public static <T> Handler<AsyncResult<T>> pipeTo(final Consumer<T> successConsumer,
                                                     final VertxTestContext testContext) {
        return it -> {
            if (it.succeeded()) {
                successConsumer.accept(it.result());
                testContext.completeNow();
            }
            else testContext.failNow(it.cause());
        };
    }

    public static <T> Handler<AsyncResult<T>> pipeTo(final VertxTestContext testContext) {
        return it -> {
            if (it.succeeded()) testContext.completeNow();
            else testContext.failNow(it.cause());
        };
    }


}
