package vertx.mongodb.effect;

public final class MongoFailures {


    private MongoFailures(){}


    /**
     * Error that happens when the domain can't be resolved: wrong name or there is no internet connection.
     */
    public static final int MONGO_CONNECT_TIMEOUT_CODE = 5000;
    public static final int MONGO_READ_TIMEOUT_CODE = 5001;
    public static final int MONGO_FAILURE_CODE = 5999;


}
