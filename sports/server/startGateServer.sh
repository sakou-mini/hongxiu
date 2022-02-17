cd ./sport_server
docker network create sport-network
docker run --rm --name sport-maven \
-v $PWD:/usr/src/app \
-v /var/app/run/m2:/root/.m2 \
-w /usr/src/app maven:3.6.3-adoptopenjdk-11 \
mvn clean install -pl gate_module -am "-Dmaven.test.skip=true"

docker stop sport_gate_module
docker rm sport_gate_module
cd ../
docker build -t sport_gate_image -f DockerFileGateModule .

docker run -d -p 7007:7007 -e TZ=Asia/Shanghai \
--env-file env_dev.env \
-v $PWD/sport_server/gate_module/package/gate_module:/usr/src/app/ \
-v $PWD/sport_server/gate_module/logs:/usr/src/app/log \
-w /usr/src/app \
--network sport-network --name sport_gate_module sport_gate_image \
--spring.profiles.active=dev
