cd ./live_server
docker network create hongxiu-network
docker run --rm --name hongxiu-maven \
-v $PWD:/usr/src/app \
-v /var/app/run/m2:/root/.m2 \
-w /usr/src/app maven:3.6.3-adoptopenjdk-11 \
mvn clean install -pl blog_module -am "-Dmaven.test.skip=true"

docker stop hongxiu_blog_module
docker rm hongxiu_blog_module
cd ../
docker build -t hongxiu_blog_image -f DockerFileBlogModule .

docker run -d -p 5600:5600 -e TZ=Asia/Shanghai \
--env-file env_dev.env \
-v $PWD/live_server/blog_module/package/blog_module:/usr/src/app/ \
-v $PWD/live_server/blog_module/logs:/usr/src/app/log \
-w /usr/src/app \
--network hongxiu-network --name hongxiu_blog_module hongxiu_blog_image \
--spring.profiles.active=dev
