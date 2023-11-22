package vertx.mongodb.effect;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertManyResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import jsonvalues.*;
import jsonvalues.spec.JsSpecs;
import mongovalues.JsValuesRegistry;
import org.bson.BsonDocumentWrapper;
import org.bson.BsonValue;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class MongoConverters {


    public static final Function<JsObj, Bson> jsObj2Bson = obj ->
            new BsonDocumentWrapper<>(obj,
                                      JsValuesRegistry.INSTANCE.get(JsObj.class)
            );
    public static final Function<JsArray, List<JsObj>> jsArray2ListOfJsObj =
            array -> {
                var errors = JsSpecs.arrayOfObj().test(array);
                if (!errors.isEmpty()) throw new IllegalArgumentException(errors.toString());

                List<JsObj> list = new ArrayList<>();
                array.iterator()
                     .forEachRemaining(it -> list.add(it.toJsObj()));
                return list;
            };
    public static final Function<JsArray, List<Bson>> jsArray2ListOfBson =
            jsArray2ListOfJsObj.andThen(list -> list.stream()
                                                    .map(it -> jsObj2Bson.apply(it.toJsObj()))
                                                    .collect(Collectors.toList()));
    public static final Function<BsonValue, String> objectId2Hex =
            bsonValue -> bsonValue.asObjectId()
                                  .getValue()
                                  .toHexString();
    public static final Function<InsertOneResult, String> insertOneResult2HexId =
            result -> objectId2Hex.apply(result.getInsertedId());
    public static final Function<InsertOneResult, JsObj> insertOneResult2JsObj =
            result -> JsObj.of("insertedId",
                               JsStr.of(insertOneResult2HexId.apply(result)),
                               "wasAcknowledged",
                               JsBool.of(result.wasAcknowledged()),
                               "type",
                               JsStr.of(result.getClass()
                                              .getSimpleName()
                                       )
                              );
    public static final Function<UpdateResult, Optional<String>> updateResult2OptHexId = it -> {
        var upsertedId = it.getUpsertedId();
        if (upsertedId == null) return Optional.empty();
        return Optional.of(objectId2Hex.apply(upsertedId));
    };
    public static final Function<UpdateResult, JsObj> updateResult2JsObj = result -> {
        var optStr = updateResult2OptHexId.apply(result);
        return JsObj.of("upsertedId",
                        optStr.isPresent() ? JsStr.of(optStr.get()) : JsNull.NULL,
                        "matchedCount",
                        JsLong.of(result.getMatchedCount()),
                        "modifiedCount",
                        JsLong.of(result.getModifiedCount()),
                        "wasAcknowledged",
                        JsBool.of(result.wasAcknowledged()),
                        "type",
                        JsStr.of(result.getClass()
                                       .getSimpleName()
                                )
                       );
    };
    public static final Function<FindIterable<JsObj>, JsObj> findIterableHead = MongoIterable::first;
    public static final Function<FindIterable<JsObj>, JsArray> findIterable2JsArray = JsArray::ofIterable;
    public static final Function<String, JsObj> str2Oid = id -> JsObj.of("_id",
                                                                         JsObj.of("$oid",
                                                                                  JsStr.of(id)
                                                                                 )
                                                                        );
    public static final Function<InsertManyResult, JsArray> insertManyResult2JsArrayOfHexIds =
            result -> {
                var map = result.getInsertedIds();
                var array = JsArray.empty();
                for (var e : map.entrySet()) {
                    array = array.append(JsStr.of(objectId2Hex.apply(e.getValue())));
                }
                return array;
            };
    public static final Function<DeleteResult, JsObj> deleteResult2JsObj =
            result -> JsObj.of("deleted_count",
                               JsLong.of(result.getDeletedCount()),
                               "was_acknowledged",
                               JsBool.of(result.wasAcknowledged())
                              );
    public static final Function<AggregateIterable<JsObj>, JsArray> aggregateResult2JsArray =
            iter -> {
                var result = JsArray.empty();
                for (var obj : iter) {
                    result = result.append(obj);
                }
                return result;
            };


    private MongoConverters() {
    }
}
