package vertx.mongodb.effect;

import jsonvalues.*;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

public final class FindMessage {

    public static final String NO_CURSOR_TIMEOUT = "noCursorTimeout";
    public static final String HINT_STRING = "hintString";
    public static final String HINT = "hint";
    public static final String MAX = "max";
    public static final String MIN = "min";
    public static final String MAX_TIME = "maxTime";
    public static final String SKIP = "skip";
    public static final String LIMIT = "limit";
    public static final String COMMENT = "comment";
    public static final String FILTER = "filter";
    public static final String BATCH_SIZE = "batchSize";
    public static final String SORT = "sort";
    public static final String PROJECTION = "projection";
    public static final String MAX_AWAIT_TIME = "maxAwaitTime";
    public static final String SHOW_RECORD_ID = "showRecordId";
    public static final String PARTIAL = "partial";
    public static final String RETURN_KEY = "returnKey";
    public final JsObj filter;
    public final JsObj sort;
    public final JsObj projection;
    public final JsObj hint;
    public final JsObj max;
    public final JsObj min;
    public final String hintString;
    public final int skip;
    public final int limit;
    public final boolean showRecordId;
    public final boolean returnKey;
    public final String comment;
    public final boolean noCursorTimeout;
    public final boolean partial;
    public final int batchSize;
    public final long maxAwaitTime;
    public final long maxTime;


    FindMessage(final JsObj filter,
                final JsObj sort,
                final JsObj projection,
                final JsObj hint,
                final JsObj max,
                final JsObj min,
                final String hintString,
                final int skip,
                final int limit,
                final boolean showRecordId,
                final boolean returnKey,
                final String comment,
                final boolean noCursorTimeout,
                final boolean partial,
                final int batchSize,
                final long maxAwaitTime,
                final long maxTime
               ) {
        this.filter = requireNonNull(filter);
        this.sort = sort;
        this.projection = projection;
        this.hint = hint;
        this.max = max;
        this.min = min;
        this.hintString = hintString;
        this.skip = skip;
        this.limit = limit;
        this.showRecordId = showRecordId;
        this.returnKey = returnKey;
        this.comment = comment;
        this.noCursorTimeout = noCursorTimeout;
        this.partial = partial;
        this.batchSize = batchSize;
        this.maxAwaitTime = maxAwaitTime;
        this.maxTime = maxTime;
    }

    public static FindMessage ofFilter(final JsObj filter) {
        return new FindMessageBuilder().filter(requireNonNull(filter))
                                       .create();
    }

    public static FindMessage ofFilter(final JsObj filter,
                                       final JsObj projection
                                      ) {
        return new FindMessageBuilder().filter(requireNonNull(filter))
                                       .projection(requireNonNull(projection))
                                       .create();
    }

    public static FindMessage ofFilter(final JsObj filter,
                                       final JsObj projection,
                                       final JsObj sort
                                      ) {
        return new FindMessageBuilder().filter(requireNonNull(filter))
                                       .projection(requireNonNull(projection))
                                       .sort(requireNonNull(sort))
                                       .create();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final var that = (FindMessage) o;
        return skip == that.skip &&
               limit == that.limit &&
               showRecordId == that.showRecordId &&
               returnKey == that.returnKey &&
               noCursorTimeout == that.noCursorTimeout &&
               partial == that.partial &&
               batchSize == that.batchSize &&
               maxAwaitTime == that.maxAwaitTime &&
               maxTime == that.maxTime &&
               filter.equals(that.filter) &&
               Objects.equals(sort,
                              that.sort
                             ) &&
               Objects.equals(projection,
                              that.projection
                             ) &&
               Objects.equals(hint,
                              that.hint
                             ) &&
               Objects.equals(max,
                              that.max
                             ) &&
               Objects.equals(min,
                              that.min
                             ) &&
               Objects.equals(hintString,
                              that.hintString
                             ) &&
               Objects.equals(comment,
                              that.comment
                             );
    }

    @Override
    public int hashCode() {
        return Objects.hash(filter,
                            sort,
                            projection,
                            hint,
                            max,
                            min,
                            hintString,
                            skip,
                            limit,
                            showRecordId,
                            returnKey,
                            comment,
                            noCursorTimeout,
                            partial,
                            batchSize,
                            maxAwaitTime,
                            maxTime
                           );
    }

    public JsObj toJsObj() {
        var options = JsObj.empty();

        options = options.set(FILTER,
                              this.filter
                             );

        options.set(LIMIT,
                    JsInt.of(this.limit)
                   );

        options = options.set(BATCH_SIZE,
                              JsInt.of(this.batchSize)
                             );

        options = options.set(MAX_AWAIT_TIME,
                              JsLong.of(this.maxAwaitTime)
                             );

        options = options.set(MAX_TIME,
                              JsLong.of(this.maxTime)
                             );

        options = options.set(SKIP,
                              JsInt.of(this.skip)
                             );
        options = options.set(LIMIT,
                              JsInt.of(this.limit)
                             );
        options = options.set(NO_CURSOR_TIMEOUT,
                              JsBool.of(this.noCursorTimeout)
                             );
        options = options.set(SHOW_RECORD_ID,
                              JsBool.of(this.showRecordId)
                             );
        options = options.set(PARTIAL,
                              JsBool.of(this.partial)
                             );
        options = options.set(RETURN_KEY,
                              JsBool.of(this.returnKey)
                             );

        if (hintString != null) options = options.set(HINT_STRING,
                                                      JsStr.of(hintString)
                                                     );
        if (hint != null) options = options.set(HINT,
                                                hint
                                               );
        if (projection != null) options = options.set(PROJECTION,
                                                      projection
                                                     );
        if (sort != null) options = options.set(SORT,
                                                sort
                                               );

        if (comment != null) options = options.set(COMMENT,
                                                   JsStr.of(comment)
                                                  );
        if (max != null) options = options.set(MAX,
                                               max
                                              );

        if (min != null) options = options.set(MIN,
                                               min
                                              );
        return options;
    }
}
