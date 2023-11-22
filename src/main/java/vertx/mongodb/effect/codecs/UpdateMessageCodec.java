package vertx.mongodb.effect.codecs;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import jsonvalues.JsObj;
import vertx.mongodb.effect.UpdateMessage;

final class UpdateMessageCodec implements MessageCodec<UpdateMessage, UpdateMessage> {

    public static final UpdateMessageCodec INSTANCE = new UpdateMessageCodec();

    private UpdateMessageCodec() {
    }

    @Override
    public void encodeToWire(final Buffer buffer,
                             final UpdateMessage updateMessage
                            ) {
        var filter = updateMessage.filter.serialize();
        var update = updateMessage.update.serialize();
        buffer.appendInt(filter.length);
        buffer.appendInt(update.length);
        buffer.appendBytes(filter);
        buffer.appendBytes(update);
    }

    @Override
    public UpdateMessage decodeFromWire(int pos,
                                        final Buffer buffer
                                       ) {
        var filterLength = buffer.getInt(pos);
        pos += 4;
        var updateLength = buffer.getInt(pos);
        pos += 4;
        var filter = buffer.getString(pos,
                                      pos + filterLength
                                     );
        pos += filterLength;
        var update = buffer.getString(pos,
                                      pos + updateLength
                                     );
        return new UpdateMessage(JsObj.parse(filter),
                                 JsObj.parse(update));
    }

    @Override
    public UpdateMessage transform(final UpdateMessage message) {
        return message;
    }

    @Override
    public String name() {
        return "mongo-update-docs";
    }

    @Override
    public byte systemCodecID() {
        return -1;
    }
}
