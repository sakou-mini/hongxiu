cd ./live_server
docker network create hongxiu-network
docker run --rm --name hongxiu-maven \
-v $PWD:/usr/src/app \
-v /var/app/run/m2:/root/.m2 \
-w /usr/src/app maven:3.6.3-adoptopenjdk-11 \
mvn clean install -pl web_duocai_module -am "-Dmaven.test.skip=true"

docker stop duocai_web_module
docker rm duocai_web_module
cd ../
docker build -t duocai_web_image -f DuoCaiDockerFileWebModule .

docker run -d -p 8005:8005 -e TZ=Asia/Shanghai \
--env-file env_dev.env \
-v $PWD/live_server/web_duocai_module/package/web_duocai_module:/usr/src/app/ \
-v $PWD/live_server/web_duocai_module/logs:/usr/src/app/log \
-w /usr/src/app \
--network hongxiu-network --name duocai_web_module duocai_web_image \
--spring.profiles.active=dev
