docker network create jinli-network

docker run --rm --name jinli-maven -v %cd%:/usr/src/app -v %cd%/run/.m2:/root/.m2 -w /usr/src/app maven:3.6.3-adoptopenjdk-11 mvn clean install "-Dmaven.test.skip=true"
docker build -t jinli-image -f Dockerfile .

docker run -d -p 7001:7001 -p 8080:8080 -e TZ=Asia/Shanghai --env-file env_test.env -v %cd%/config:/usr/src/app/config -v %cd%/logs:/var/log/jinli -w /usr/src/app --network jinli-network --name jinli-game jinli-image --spring.profiles.active=functional
docker ps -a