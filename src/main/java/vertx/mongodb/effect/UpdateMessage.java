package vertx.mongodb.effect;

import jsonvalues.JsObj;

import java.util.Objects;

import static java.util.Objects.requireNonNull;


public final class UpdateMessage {
    public final JsObj filter;
    public final JsObj update;

    public UpdateMessage(final JsObj filter,
                         final JsObj update
                        ) {
        this.filter = requireNonNull(filter);
        this.update = requireNonNull(update);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final var that = (UpdateMessage) o;
        return Objects.equals(filter,
                              that.filter
                             ) &&
               Objects.equals(update,
                              that.update
                             );
    }

    @Override
    public int hashCode() {
        return Objects.hash(filter,
                            update
                           );
    }
}
