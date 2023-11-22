package vertx.mongodb.effect;

import com.mongodb.client.MongoCollection;
import io.vertx.core.DeploymentOptions;
import jsonvalues.JsArray;
import jsonvalues.JsObj;
import vertx.effect.Lambdac;
import vertx.mongodb.effect.functions.*;

import java.util.Optional;
import java.util.function.Supplier;

import static vertx.mongodb.effect.MongoConverters.*;

public final class DataCollectionModule extends MongoModule {

    private static final String DELETE_ONE_ADDRESS = "delete_one";
    private static final String UPDATE_ONE_ADDRESS = "update_one";
    private static final String REPLACE_ONE_ADDRESS = "replace_one";
    private static final String INSERT_ONE_ADDRESS = "insert_one";
    private static final String INSERT_MANY_ADDRESS = "insert_all";
    private static final String DELETE_MANY_ADDRESS = "delete_all";

    public Lambdac<JsObj, String> insertOne;
    public Lambdac<JsObj, JsObj> deleteOne;
    public Lambdac<JsArray, JsArray> insertMany;
    public Lambdac<JsObj, JsObj> deleteMany;
    public Lambdac<FindMessage, Optional<JsObj>> findOne;
    public Lambdac<FindMessage, JsArray> findAll;
    public Lambdac<UpdateMessage, JsObj> findOneAndReplace;
    public Lambdac<UpdateMessage, JsObj> replaceOne;
    public Lambdac<UpdateMessage, JsObj> updateOne;
    public Lambdac<JsObj, Long> count;
    public Lambdac<UpdateMessage, JsObj> updateMany;
    public Lambdac<JsArray, JsArray> aggregate;
    public Lambdac<JsObj, JsObj> findOneAndDelete;
    public Lambdac<UpdateMessage, JsObj> findOneAndUpdate;

    public DataCollectionModule(final Supplier<MongoCollection<JsObj>> collection) {
        super(collection);
    }


    @Override
    protected void initialize() {
        insertOne = this.trace(INSERT_ONE_ADDRESS);
        insertMany = this.trace(INSERT_MANY_ADDRESS);
        deleteMany = this.trace(DELETE_MANY_ADDRESS);
        Lambdac<FindMessage, JsObj> findOneLambda = vertxRef.spawn("findOne",
                                                              new FindOne(collectionSupplier)
                                                             );
        findOne = (context, message) -> findOneLambda.apply(context,
                                                            message)
                                                     .map(Optional::ofNullable);
        findAll = vertxRef.spawn("findAll",
                                 new FindAll(collectionSupplier)
                                );
        count = vertxRef.spawn("count",
                               new Count(collectionSupplier)
                              );
        deleteOne = this.trace(DELETE_ONE_ADDRESS);
        replaceOne = this.trace(REPLACE_ONE_ADDRESS);
        updateOne = this.trace(UPDATE_ONE_ADDRESS);
        updateMany = vertxRef.spawn("updateMany",
                                    new UpdateMany<>(collectionSupplier,
                                                     updateResult2JsObj
                                    )
                                   );
        findOneAndReplace = vertxRef.spawn("findAndReplace",
                                           new FindOneAndReplace(collectionSupplier)
                                          );
        findOneAndDelete = vertxRef.spawn("findOneAndDelete",
                                          new FindOneAndDelete(collectionSupplier)
                                         );
        findOneAndUpdate = vertxRef.spawn("findOneAndUpdate",
                                          new FindOneAndUpdate(collectionSupplier)
                                         );
        aggregate = vertxRef.spawn("aggregate",
                                   new Aggregate<>(collectionSupplier,
                                                   aggregateResult2JsArray
                                   )
                                  );


    }


    @Override
    protected void deploy() {
        this.deploy(INSERT_ONE_ADDRESS,
                    new InsertOne<>(collectionSupplier,
                                    insertOneResult2HexId
                    ),
                    new DeploymentOptions().setInstances(4)
                   );
        this.deploy(INSERT_MANY_ADDRESS,
                    new InsertMany<>(collectionSupplier,
                                     insertManyResult2JsArrayOfHexIds
                    )
                   );
        this.deploy(DELETE_MANY_ADDRESS,
                    new DeleteMany<>(collectionSupplier,
                                     deleteResult2JsObj
                    )
                   );
        this.deploy(DELETE_ONE_ADDRESS,
                    new DeleteOne<>(collectionSupplier,
                                    deleteResult2JsObj
                    )
                   );

        this.deploy(REPLACE_ONE_ADDRESS,
                    new ReplaceOne<>(collectionSupplier,
                                     updateResult2JsObj
                    )
                   );
        this.deploy(UPDATE_ONE_ADDRESS,
                    new UpdateOne<>(collectionSupplier,
                                    updateResult2JsObj
                    )
                   );

    }
}
