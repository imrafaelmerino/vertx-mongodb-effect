<img src="./logo/package_twitter_swe2n4mg/black/full/coverphoto/black_logo_white_background.png" alt="vertx-mongodb-effect"/>

- [Introduction](#introduction)
- [Supported types](#types)
- [Supported operations](#operations)
- [Defining modules](#defmodules)
- [Deploying modules](#depmodules)
- [Publishing events](#events)
- [Java Flight Recorder support](#jfr)
- [Requirements](#requirements)
- [Installation](#installation)
- [Release process](#release)

[![Maven](https://img.shields.io/maven-central/v/com.github.imrafaelmerino/vertx.mongodb.effect/2.0.0)](https://search.maven.org/artifact/com.github.imrafaelmerino/vertx.mongodb.effect/2.0.0/jar)


## <a name="introduction"><a/> Introduction

**vertx-mongodb-effect** allows us to work with **MongoDB** following a purely functional and reactive style.
It requires to be familiar with [vertx-effect](https://github.com/imrafaelmerino/vertx-effect). Both
**vertx-effect** and **vertx-mongo-effect** use the immutable and persistent Json from 
[json-values](https://github.com/imrafaelmerino/json-values). **Jsons travel across the event bus, 
from verticle to verticle, back and forth, without being neither copied nor converted to BSON**.
The vertx codecs to send the Json from json-values to the event bus are in [vertx-values](https://github.com/imrafaelmerino/vertx-values),
which is a dependency of vertx-effect. 
 
## <a name="types"><a/> Supported types
**json-values** supports the standard Json types: string, number, null, object, array; 
There are five number specializations: int, long, double, decimal, and BigInteger. 
**json-values adds support for instants and binary data**. It serializes Instants into 
its string representation according to ISO-8601, and the binary type into a string encoded in base 64. 

**vertx-mongodb-effect** uses [mongo-values](https://github.com/imrafaelmerino/mongo-values). 
It abstracts the processes of encoding to BSON and decoding from BSON. 	
Please find below the BSON types supported and their equivalent types in json-values.

```code    

Map<BsonType, Class<?>> map = new HashMap<>();
map.put(BsonType.NULL, JsNull.class);
map.put(BsonType.ARRAY, JsArray.class);
map.put(BsonType.BINARY, JsBinary.class);
map.put(BsonType.BOOLEAN, JsBool.class);
map.put(BsonType.DATE_TIME, JsInstant.class);
map.put(BsonType.DOCUMENT, JsObj.class);
map.put(BsonType.DOUBLE, JsDouble.class);
map.put(BsonType.INT32, JsInt.class);
map.put(BsonType.INT64, JsLong.class);
map.put(BsonType.DECIMAL128, JsBigDec.class);
map.put(BsonType.STRING, JsStr.class);

```

When defining the mongodb settings, **you have to specify the codec registry _JsValuesRegistry_ from mongo-values**:

```code
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import mongovalues.JsValuesRegistry;

MongoClientSettings  settings =
             MongoClientSettings.builder()
                                .applyConnectionString(connString)
                                .codecRegistry(JsValuesRegistry.INSTANCE)
                                .build();

``` 



## <a name="operations"><a/> Supported operations 
**Every method of the MongoDB driver has an associated lambda**.  

Since **vertx-mongodb-effect** uses the driver API directly, it can benefit from all its features and methods. 
**It's an advantage over the official vertx-mongodb-client**.

Please find below the types and constructors of the most essentials operations:

**Count :: Lambdac<JsObj, Long>**

```code
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.CountOptions;

public Count(Supplier<MongoCollection<JsObj>> collectionSupplier,
             CountOptions options
            )
```

**DeleteMany :: Lambdac<JsObj, O>**
 
```code
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.DeleteOptions;
import com.mongodb.client.result.DeleteResult;

public DeleteMany(Supplier<MongoCollection<JsObj>> collectionSupplier,
                  Function<DeleteResult, O> resultConverter,
                  DeleteOptions options 
                 )
                      
```   
    
**DeleteOne :: Lambdac<JsObj, O>**
    
```code
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.DeleteOptions;
import com.mongodb.client.result.DeleteResult;

public DeleteOne(Supplier<MongoCollection<JsObj>> collectionSupplier,
                 Function<DeleteResult, O> resultConverter,
                 DeleteOptions options 
                )
                      
```

**FindAll :: Lambdac<FindMessage, JsArray>**

    
```code
import com.mongodb.client.MongoCollection;
import com.mongodb.client.FindIterable;

public FindAll(Supplier<MongoCollection<JsObj>> collectionSupplier,
               Function<FindIterable<JsObj>, JsArray> converter 
              )
```    

**FindOne :: Lambdac<FindMessage, JsObj>**
    
```code
import com.mongodb.client.MongoCollection;
import com.mongodb.client.FindIterable;

public FindOne(Supplier<MongoCollection<JsObj>> collectionSupplier,
               Function<FindIterable<JsObj>, JsObj> converter 
              )                 
```    

**FindOneAndDelete :: Lambdac<JsObj, JsObj>**
    
```code
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.FindOneAndDeleteOptions;

public FindOneAndDelete(Supplier<MongoCollection<JsObj>> collectionSupplier,
                        FindOneAndDeleteOptions options
                       ) 
```   

**FindOneAndReplace :: Lambdac<UpdateMessage, JsObj>**
    
```code
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.FindOneAndReplaceOptions;

public FindOneAndReplace(Supplier<MongoCollection<JsObj>> collectionSupplier,
                         FindOneAndReplaceOptions options
                        )   
```    

**FindOneAndUpdate :: Lambdac<UpdateMessage, JsObj>**

```code
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.FindOneAndUpdateOptions;

public FindOneAndUpdate(Supplier<MongoCollection<JsObj>> collectionSupplier,
                        FindOneAndUpdateOptions options
                       )
```    

**InsertMany :: Lambdac<JsArray, R>**

```code
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.InsertManyResult;
import com.mongodb.client.model.InsertManyOptions;

public InsertMany(Supplier<MongoCollection<JsObj>> collectionSupplier,
                  Function<InsertManyResult, R> resultConverter,
                  InsertManyOptions options
                 )   
```    

**InsertOne :: Lambdac<JsObj, R>**

```code
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.model.InsertOneOptions;

public InsertOne(Supplier<MongoCollection<JsObj>> collectionSupplier,
                 Function<InsertOneResult, R> resultConverter,
                 InsertOneOptions options
                )    
```    

**ReplaceOne :: Lambdac<UpdateMessage, O>**

```code
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.client.model.ReplaceOptions;

public ReplaceOne(Supplier<MongoCollection<JsObj>> collectionSupplier,
                  Function<UpdateResult, O> resultConverter,
                  ReplaceOptions options
                 )   
```    
**UpdateMany :: Lambdac<UpdateMessage, O>**

```code
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.client.model.UpdateOptions

public UpdateMany(Supplier<MongoCollection<JsObj>> collectionSupplier,
                  Function<UpdateResult, O> resultConverter,
                  UpdateOptions options
                 )
```    

**UpdateOne :: Lambdac<UpdateMessage, O>**

```code
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.client.model.UpdateOptions

public UpdateOne(Supplier<MongoCollection<JsObj>> collectionSupplier,
                 Function<UpdateResult, O> resultConverter,
                 UpdateOptions options
                )
```    


## <a name="defmodules"><a/> Defining modules
Like with vertx-effect, [modules](https://vertx.effect.imrafaelmerino.dev/#modules) deploys
verticles and expose lambdas to communicate with them.
The typical scenario is to create a module per collection. We can deploy or spawn verticles. 

The following modules are just a couple of examples.


We create a module where all the lambdas make read operations and spawn verticles to reach a significant level of parallelization:

```code
import vertx.mongodb.effect.MongoModule;
import vertx.effect.Lambdac;

public class ReadModule extends MongoModule {

    public MyCollectionModule(final Supplier<MongoCollection<JsObj>> collection) {
        super(collection);
    }

    public static Lambdac<FindMessage, Optional<JsObj>> findOne;
    public static Lambdac<FindMessage, JsArray> findAll;
    public static Lambdac<JsObj, Long> count;
    public static Lambdac<JsArray, JsArray> aggregate;

    @Override
    protected void deploy() {}  

    @Override
    protected void initialize() {
        Lambdac<FindMessage, JsObj> findOneLambda = vertxRef.spawn("find_one",
                                                             new FindOne(collection)
                                                            );
        this.findOne = (context,message) -> findOneLambda.apply(context,message)
                                                         .map(Optional::ofNullable);
        this.findAll = vertxRef.spawn("find_all",
                                      new FindAll(collection)
                                     );
        this.count = vertxRef.spawn("count",
                                    new Count(collection)
                                   );

        this.aggregate = vertxRef.spawn("aggregate",
                                        new Aggregate<>(collection,
                                                        Converters.aggregateResult2JsArray
                                                       )
                                       );
    }
}
```

We create a module where all the lambdas make delete, insert and update operations, and deploy only one
instance per verticle. 


```code
import vertx.mongodb.effect.MongoModule;
import vertx.effect.Lambdac;

public class MyCollectionModule extends MongoModule {

    public MyCollectionModule(final Supplier<MongoCollection<JsObj>> collection) {
        super(collection);
    }

    public static Lambdac<JsObj, String> insertOne;
    public static Lambdac<JsObj, JsObj> deleteOne;
    public static Lambdac<UpdateMessage, JsObj> replaceOne;
    public static Lambdac<UpdateMessage, JsObj> updateOne;

    @Override
    protected void deploy() {
        this.deploy(INSERT_ONE_ADDRESS,
                    new InsertOne<>(collection,
                                    Converters.insertOneResult2HexId
                                   ),
                   );
        this.deploy(DELETE_ONE_ADDRESS,
                    new DeleteOne<>(collection,
                                    Converters.deleteResult2JsObj
                                   )
                   );

        this.deploy(REPLACE_ONE_ADDRESS,
                    new ReplaceOne<>(collection,
                                     Converters.updateResult2JsObj
                                    )
                   );
        this.deploy(UPDATE_ONE_ADDRESS,
                    new UpdateOne<>(collection,
                                    Converters.updateResult2JsObj
                                   )
                   );
    }  

    @Override
    protected void initialize() {
        this.insertOne = this.trace(INSERT_ONE_ADDRESS);
        this.deleteOne = this.trace(DELETE_ONE_ADDRESS);
        this.replaceOne = this.trace(REPLACE_ONE_ADDRESS);
        this.updateOne = this.trace(UPDATE_ONE_ADDRESS);
    }

    private static final String DELETE_ONE_ADDRESS = "delete_one";   
    private static final String UPDATE_ONE_ADDRESS = "update_one";
    private static final String REPLACE_ONE_ADDRESS = "replace_one";
    private static final String INSERT_ONE_ADDRESS = "insert_one";
    private static final String INSERT_MANY_ADDRESS = "insert_all";
    private static final String DELETE_MANY_ADDRESS = "delete_all";

}

``` 
## <a name="depmodules"><a/> Deploying modules 

The verticles _RegisterMongoEffectCodecs_ and _RegisterJsValuesCodecs_ need to be deployed to register the vertx message codecs.
Remember that you can't send any message to the event bus. If a message is not supported by Vertx you have to
create a _MessageCodec_.


```code
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import vertx.mongodb.effect.MongoVertxClient;
import vertx.effect.VertxRef;

// define every timeout if you wanna be reactive
int connectTimeoutMS =  ???;
int socketTimeoutMS = ???;
int serverSelectionTimeoutMS = ???;

String connectionUrl = 
     String.format("mongodb://localhost:27017/?connectTimeoutMS=%s&socketTimeoutMS=%s&serverSelectionTimeoutMS=%s",
                   connectTimeoutMS,
                   socketTimeoutMS,
                   serverSelectionTimeoutMS 
                  );
 
ConnectionString connString = new ConnectionString(connectionUrl);

MongoClientSettings  settings =
             MongoClientSettings.builder()
                                .applyConnectionString(connString)
                                .codecRegistry(JsValuesRegistry.INSTANCE)
                                .build();

// one vertx client per database connection 
MongoVertxClient mongoClient = new MongoVertxClient(settings);

String database = ???;
String collection = ???; 
MyCollectionModule collectionModule = 
          new MyCollectionModule(mongoClient.getCollection(database,
                                                           collection
                                                          )
                                );

VertxRef vertxRef = new VertxRef(vertx);

Quadruple.sequential(vertxRef.deployVerticle(new RegisterJsValuesCodecs()),
                     vertxRef.deployVerticle(new RegisterMongoEffectCodecs()),
                     vertxRef.deployVerticle(mongoClient),
                     vertxRef.deployVerticle(collectionModule)
                     ) 
         .get();
```


Once everything is up and running, enjoy your lambdas!

```code

BiFunction<Integer,String,Val<Optional<JsObj>>> findByCode = (attempts,code) ->
          MyCollectionModule.findOne
                            .apply(FindMessage.ofFilter(JsObj.of("code",
                                                                 JsStr.of(code)
                                                                )
                                                        ) 
                                   )                    
                            .retry(e -> Failures.anyOf(MONGO_CONNECT_TIMEOUT_CODE,
                                                       MONGO_READ_TIMEOUT_CODE
                                                      ),
                                   attempts
                                  )
                            .recoverWith(e -> Val.succeed(Optional.empty()));
```
## <a name="events"><a/> Publishing events

Since **vertx-effect** publishes the most critical events into the address **vertx-effect-events**, 
it' possible to register consumers to explode that information. You can disable this feature 
with the Java system property **-Dpublish.events=false**. Thanks to Lambdac, it's possible to correlate
different events that belongs to the same transaction.
Go to the [vertx-effect doc](https://vertx.effect.imrafaelmerino.dev/#events) for further details.

## <a name="jfr"><a/> JFR support
Since vertx-effect supports JFR, all the verticle messages have an associated event and can be visualized using
Java Mission Control. 

Fields of a verticle message event:

    - address: Address of the Verticle where the message is sent to
    - result: SUCCESS OR FAILURE, dependening on what the caller receives
    - failure code: In case the failure is a ReplyException, it's the failure code
    - failure type: In case the failure is a ReplyException, it's the failure type
    - failure message: In case the failure is a ReplyException, it's the failure message
    - exception class: In case the failure is not a ReplyException, it's the exception class name
    - exception message: In case the failure is not a ReplyException, it's the exception message
    - duration: time since the message is sent until the response is received

## <a name="requirements"><a/> Requirements 

   -  Java 17 or greater
   -  [vertx-effect](https://github.com/imrafaelmerino/vertx-effect)
   -  [mongo driver sync](https://mongodb.github.io/mongo-java-driver/4.1/whats-new/)
   -  [Mongo values](https://github.com/imrafaelmerino/mongo-values)

## <a name="installation"><a/> Installation 

```xml

<dependency>
   <groupId>com.github.imrafaelmerino</groupId>
   <artifactId>vertx-mongodb-effect</artifactId>
   <version>2.0.0</version>
</dependency>

```





