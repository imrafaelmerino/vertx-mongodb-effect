package vertx.mongodb.effect;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Assertions;
import vertx.effect.VIO;

import java.util.function.BiConsumer;
import java.util.function.Predicate;

//todo migrate to verifiers instead of TestFns
public final class Verifiers {

    private Verifiers() {
    }


    public static <T> Handler<AsyncResult<T>> pipeTo(final VertxTestContext context) {
        return it -> {
            if (it.succeeded()) context.completeNow();
            else context.failNow(it.cause());
        };
    }

    public static <T> BiConsumer<VIO<T>, VertxTestContext> verifySuccess() {
        return verifySuccess(it -> true);
    }

    public static <T> BiConsumer<VIO<T>, VertxTestContext> verifySuccess(Predicate<T> predicate) {
        return verifySuccess(predicate,
                             ""
                            );
    }


    public static <T> BiConsumer<VIO<T>, VertxTestContext> verifySuccess(Predicate<T> predicate,
                                                                         String errorMessage
                                                                        ) {
        return (val, context) -> {
            var unused = val.onComplete(r -> {
                                if (r.failed()) {
                                    context.failNow(r.cause());
                                } else {
                                    context.verify(() -> Assertions.assertTrue(predicate.test(r.result()),
                                                                               errorMessage
                                                                              ));
                                    context.completeNow();
                                }
                            })
                            .get();
        };
    }


    public static <T> BiConsumer<VIO<T>, VertxTestContext> verifyFailure(Predicate<Throwable> predicate,
                                                                         String errorMessage
                                                                        ) {
        return (val, context) -> {
            var unused = val.onComplete(r -> {
                                if (r.succeeded()) {
                                    context.failNow(new RuntimeException(String.format("The val was supposed to fail, and it succeeded returning %s",
                                                                                       r.result()
                                                                                      )
                                                    )
                                                   );
                                } else {
                                    context.verify(() -> Assertions.assertTrue(predicate.test(r.cause()),
                                                                               errorMessage
                                                                              )
                                                  );
                                    context.completeNow();
                                }
                            })
                            .get();
        };
    }

    public static <T> BiConsumer<VIO<T>, VertxTestContext> verifyFailure(Predicate<Throwable> predicate) {
        return verifyFailure(predicate,
                             ""
                            );
    }

    public static <T> BiConsumer<VIO<T>, VertxTestContext> verifyFailure() {
        return Verifiers.verifyFailure(it -> true,
                                       ""
                                      );
    }
}
