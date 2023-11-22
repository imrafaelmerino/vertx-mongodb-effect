package vertx.mongodb.effect.functions;

import com.mongodb.client.MongoCollection;
import vertx.mongodb.effect.MongoConverters;
import jsonvalues.JsArray;
import jsonvalues.JsObj;

import java.util.function.Supplier;


public class FindAll extends Find<JsArray> {

    public FindAll(final Supplier<MongoCollection<JsObj>> collectionSupplier) {
        super(collectionSupplier,
              MongoConverters.findIterable2JsArray
             );
    }


}
