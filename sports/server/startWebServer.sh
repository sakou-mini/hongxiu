cd ./sport_server
docker network create sport-network
docker run --rm --name sport-maven \
-v $PWD:/usr/src/app \
-v /var/app/run/m2:/root/.m2 \
-w /usr/src/app maven:3.6.3-adoptopenjdk-11 \
mvn clean install -pl web_module -am "-Dmaven.test.skip=true"

docker stop sport_web_module
docker rm sport_web_module
cd ../
docker build -t sport_web_image -f DockerFileWebModule .

docker run -d -p 8009:8009 -e TZ=Asia/Shanghai \
--env-file env_dev.env \
-v $PWD/sport_server/web_module/package/web_module:/usr/src/app/ \
-v $PWD/sport_server/web_module/logs:/usr/src/app/log \
-w /usr/src/app \
--network sport-network --name sport_web_module sport_web_image \
--spring.profiles.active=dev
