package vertx.mongodb.effect.functions;

import io.vertx.core.eventbus.ReplyException;
import vertx.mongodb.effect.MongoFailures;

import java.util.Arrays;
import java.util.function.Function;

import static io.vertx.core.eventbus.ReplyFailure.RECIPIENT_FAILURE;

class Functions {
    static final Function<Throwable, ReplyException> toMongoValExc =
            exc -> {
                switch (exc.getClass()
                           .getSimpleName()) {

                    case "MongoSocketReadTimeoutException":
                        return new ReplyException(RECIPIENT_FAILURE,
                                                  MongoFailures.MONGO_READ_TIMEOUT_CODE,
                                                  getMessage(exc)
                        );
                    case "MongoTimeoutException":
                        return new ReplyException(RECIPIENT_FAILURE,
                                                  MongoFailures.MONGO_CONNECT_TIMEOUT_CODE,
                                                  getMessage(exc)
                        );


                    default:
                        return new ReplyException(RECIPIENT_FAILURE,
                                                  MongoFailures.MONGO_FAILURE_CODE,
                                                  getMessage(exc)
                        );
                }
            };

    private static String getMessage(final Throwable e) {
        return e.getStackTrace().length == 0 ?
               e.toString() :
               e.toString() + "@" + Arrays.toString(e.getStackTrace());
    }
}
