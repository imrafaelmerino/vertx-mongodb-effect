package vertx.mongodb.effect.codecs;

import vertx.mongodb.effect.UpdateMessage;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import vertx.mongodb.effect.FindMessage;

public final class RegisterMongoEffectCodecs extends AbstractVerticle {

    @Override
    public void start(final Promise<Void> startPromise)  {
        try {
            vertx.eventBus()
                 .registerDefaultCodec(UpdateMessage.class,
                                       UpdateMessageCodec.INSTANCE
                                      );
            vertx.eventBus()
                 .registerDefaultCodec(FindMessage.class,
                                       FindMessageCodec.INSTANCE
                                      );
            startPromise.complete();
        } catch (Exception e) {
           startPromise.fail(e);
        }


    }
}
