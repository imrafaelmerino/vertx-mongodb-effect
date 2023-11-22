package vertx.mongodb.effect.codecs;


import io.vertx.core.buffer.Buffer;
import jsonvalues.JsInt;
import jsonvalues.JsObj;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import vertx.mongodb.effect.FindMessage;
import vertx.mongodb.effect.FindMessageBuilder;
import vertx.mongodb.effect.UpdateMessage;
import vertx.mongodb.effect.codecs.FindMessageCodec;
import vertx.mongodb.effect.codecs.UpdateMessageCodec;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static jsonvalues.JsBool.TRUE;

public class TestCodecs {


    @Test
    public void test_encode_decode_update_message() {

        FindMessage message =
                new FindMessageBuilder().batchSize(1)
                                        .comment("")
                                        .filter(JsObj.empty())
                                        .hint(JsObj.empty())
                                        .hintString("")
                                        .limit(1)
                                        .max(JsObj.empty())
                                        .maxAwaitTime(1,
                                                      TimeUnit.SECONDS
                                                     )
                                        .maxTime(1,
                                                 TimeUnit.SECONDS
                                                )
                                        .min(JsObj.empty())
                                        .noCursorTimeout(true)
                                        .partial(true)
                                        .projection(JsObj.empty())
                                        .returnKey(true)
                                        .showRecordId(true)
                                        .skip(1)
                                        .sort(JsObj.empty())
                                        .create();

        Function<FindMessage, FindMessage> identity =
                m -> {
                    Buffer buffer = Buffer.buffer();
                    FindMessageCodec.INSTANCE.encodeToWire(buffer,
                                                           m
                                                          );
                    return FindMessageCodec.INSTANCE.decodeFromWire(0,
                                                                    buffer);

                };

        Assertions.assertEquals(message,
                                identity.apply(message));

        FindMessage a = FindMessage.ofFilter(JsObj.empty());

        Assertions.assertEquals(a,
                                identity.apply(a));

        FindMessage b = FindMessage.ofFilter(JsObj.empty(),
                                             JsObj.empty());
        Assertions.assertEquals(b,
                                identity.apply(b));

        FindMessage c = FindMessage.ofFilter(JsObj.empty(),
                                             JsObj.empty(),
                                             JsObj.empty());
        Assertions.assertEquals(c,
                                identity.apply(c));

    }

    @Test
    public void test_encode_decode_updatemessage() {

        Function<UpdateMessage, UpdateMessage> identity =
                m -> {
                    Buffer buffer = Buffer.buffer();
                    UpdateMessageCodec.INSTANCE.encodeToWire(buffer,
                                                             m
                                                            );
                    return UpdateMessageCodec.INSTANCE.decodeFromWire(0,
                                                                      buffer);

                };

        UpdateMessage a = new UpdateMessage(JsObj.empty(),
                                            JsObj.empty());

        Assertions.assertEquals(a,
                                identity.apply(a));

        UpdateMessage b = new UpdateMessage(JsObj.of("a",
                                                     JsInt.of(1)
                                                    ),
                                            JsObj.of("a",
                                                     TRUE)
        );

        Assertions.assertEquals(b,
                                identity.apply(b));


    }

    @Test
    public void test_aggregate(){


    }

}



