cd ./sport_server
docker network create sport-network
docker run --rm --name sport-maven \
-v $PWD:/usr/src/app \
-v /var/app/run/m2:/root/.m2 \
-w /usr/src/app maven:3.6.3-adoptopenjdk-11 \
mvn clean install -pl live_module -am "-Dmaven.test.skip=true"

docker stop sport_live_module
docker rm sport_live_module
cd ../
docker build -t sport_live_image -f DockerFileLiveModule .

docker run -d -e TZ=Asia/Shanghai \
--env-file env_dev.env \
-v $PWD/sport_server/live_module/package/live_module:/usr/src/app/ \
-v $PWD/sport_server/live_module/logs:/usr/src/app/log \
-w /usr/src/app \
--network sport-network --name sport_live_module sport_live_image \
--spring.profiles.active=dev
