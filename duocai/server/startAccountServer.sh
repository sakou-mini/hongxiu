cd ./live_server
docker network create duocai-network
docker run --rm --name duocai-maven \
-v $PWD:/usr/src/app \
-v /var/app/run/m2:/root/.m2 \
-w /usr/src/app maven:3.6.3-adoptopenjdk-11 \
mvn clean install -pl account_module -am "-Dmaven.test.skip=true"

docker stop duocai_account_module
docker rm duocai_account_module
cd ../
docker build -t duocai_account_image -f DockerFileAccountModule .

docker run -d -e TZ=Asia/Shanghai \
--env-file env_dev.env \
-v $PWD/live_server/account_module/package/account_module:/usr/src/app/ \
-v $PWD/live_server/account_module/logs:/usr/src/app/log \
-w /usr/src/app \
--network duocai-network --name duocai_account_module duocai_account_image \
--spring.profiles.active=dev
