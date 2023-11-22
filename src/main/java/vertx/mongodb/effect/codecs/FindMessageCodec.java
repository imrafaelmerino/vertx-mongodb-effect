package vertx.mongodb.effect.codecs;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import jsonvalues.JsObj;
import vertx.mongodb.effect.FindMessage;
import vertx.mongodb.effect.FindMessageBuilder;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import static vertx.mongodb.effect.FindMessage.*;

 final class FindMessageCodec implements MessageCodec<FindMessage, FindMessage> {

    public static final FindMessageCodec INSTANCE = new FindMessageCodec();

    private FindMessageCodec() {
    }


    @Override
    public void encodeToWire(final Buffer buffer,
                             final FindMessage findMessage) {
        var obj   = findMessage.toJsObj();
        var bytes = obj.serialize();

        buffer.appendInt(bytes.length);
        buffer.appendBytes(bytes);

    }


    @Override
    public FindMessage decodeFromWire(int pos,
                                      final Buffer buffer) {
        var length = buffer.getInt(pos);
        pos = pos + 4;
        var bytes = buffer.getBytes(pos,
                                    pos + length
                                   );
        var options = JsObj.parse(new String(bytes, StandardCharsets.UTF_8));

        var noCursorTimeout = options.getBool(NO_CURSOR_TIMEOUT);
        var partial         = options.getBool(PARTIAL);
        var returnKey       = options.getBool(RETURN_KEY);
        var showRecordId    = options.getBool(SHOW_RECORD_ID);
        var maxTime         = options.getInt(MAX_TIME);
        var maxAwaitTime    = options.getInt(MAX_AWAIT_TIME);
        var builder = new FindMessageBuilder().batchSize(options.getInt(BATCH_SIZE))
                                              .comment(options.getStr(COMMENT))
                                              .filter(options.getObj(FILTER))
                                              .hint(options.getObj(HINT))
                                              .limit(options.getInt(LIMIT))
                                              .hintString(options.getStr(HINT_STRING))
                                              .max(options.getObj(MAX))
                                              .min(options.getObj(MIN))
                                              .noCursorTimeout(Boolean.TRUE.equals(noCursorTimeout))
                                              .partial(Boolean.TRUE.equals(partial))
                                              .projection(options.getObj(PROJECTION))
                                              .returnKey(Boolean.TRUE.equals(returnKey))
                                              .showRecordId(Boolean.TRUE.equals(showRecordId))
                                              .skip(options.getInt(SKIP))
                                              .sort(options.getObj(SORT))
                                              .maxTime(maxTime,
                                                       TimeUnit.MILLISECONDS
                                                      )
                                              .maxAwaitTime(maxAwaitTime,
                                                            TimeUnit.MILLISECONDS
                                                           );

        return builder.create();

    }

    @Override
    public FindMessage transform(final FindMessage findMessage) {
        return findMessage;
    }

    @Override
    public String name() {
        return "mongo-find-message";
    }

    @Override
    public byte systemCodecID() {
        return -1;
    }


}
