cd ./sport_server
docker network create sport-network
docker run --rm --name sport-maven \
-v $PWD:/usr/src/app \
-v /var/app/run/m2:/root/.m2 \
-w /usr/src/app maven:3.6.3-adoptopenjdk-11 \
mvn clean install -pl queue_module -am "-Dmaven.test.skip=true"

docker stop sport_queue_module
docker rm sport_queue_module
cd ../
docker build -t sport_queue_image -f DockerFileQueueModule .

docker run -d -e TZ=Asia/Shanghai \
--env-file env_dev.env \
-v $PWD/sport_server/queue_module/package/queue_module:/usr/src/app/ \
-v $PWD/sport_server/queue_module/logs:/usr/src/app/log \
-w /usr/src/app \
--network sport-network --name sport_queue_module sport_queue_image \
--spring.profiles.active=dev
