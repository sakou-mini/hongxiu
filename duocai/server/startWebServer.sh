cd ./live_server
docker network create duocai-network
docker run --rm --name duocai-maven \
-v $PWD:/usr/src/app \
-v /var/app/run/m2:/root/.m2 \
-w /usr/src/app maven:3.6.3-adoptopenjdk-11 \
mvn clean install -pl web_module -am "-Dmaven.test.skip=true"

docker stop duocai_web_module
docker rm duocai_web_module
cd ../
docker build -t duocai_web_image -f DockerFileWebModule .

docker run -d -p 8008:8008 -e TZ=Asia/Shanghai \
--env-file env_dev.env \
-v $PWD/live_server/web_module/package/web_module:/usr/src/app/ \
-v $PWD/live_server/web_module/logs:/usr/src/app/log \
-w /usr/src/app \
--network duocai-network --name duocai_web_module duocai_web_image \
--spring.profiles.active=dev
