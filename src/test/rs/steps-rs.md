```shell
#remove containers if necessary
docker rm $(docker ps -qa)

docker run -d -p 27017:27017 \
-v /Users/rmerino/Projects/vertx-mongo-effect/src/test/rs/conf.yml:/etc/conf.yml \
--name mongo1 mongo --config "/etc/conf.yml"

docker run -d -p 27018:27017 \
-v /Users/rmerino/Projects/vertx-mongo-effect/src/test/rs/conf.yml:/etc/conf.yml \
--name mongo2 mongo --config "/etc/conf.yml"

docker run -d -p 27019:27017 \
-v /Users/rmerino/Projects/vertx-mongo-effect/src/test/rs/conf.yml:/etc/conf.yml \
--name mongo3 mongo --config "/etc/conf.yml"


docker exec -it mongo1 mongosh
```

```javascript
rs.initiate(
       {
         _id: "rs0",
         members: [
           { _id: 0, host: "192.168.1.64:27017" },
           { _id: 1, host: "192.168.1.64:27018" },
           { _id: 2, host: "192.168.1.64:27019" }
         ]
       }
     )
```


