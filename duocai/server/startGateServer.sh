cd ./live_server
docker network create duocai-network
docker run --rm --name duocai-maven \
-v $PWD:/usr/src/app \
-v /var/app/run/m2:/root/.m2 \
-w /usr/src/app maven:3.6.3-adoptopenjdk-11 \
mvn clean install -pl gate_module -am "-Dmaven.test.skip=true"

docker stop duocai_gate_module
docker rm duocai_gate_module
cd ../
docker build -t duocai_live_image -f DockerFileGateModule .

docker run -d -p 7006:7006 -e TZ=Asia/Shanghai \
--env-file env_dev.env \
-v $PWD/live_server/gate_module/package/gate_module:/usr/src/app/ \
-v $PWD/live_server/gate_module/logs:/usr/src/app/log \
-w /usr/src/app \
--network duocai-network --name duocai_gate_module duocai_live_image \
--spring.profiles.active=dev
