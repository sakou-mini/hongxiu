#!/bin/bash
sudo yum remove docker \
                  docker-client \
                  docker-client-latest \
                  docker-common \
                  docker-latest \
                  docker-latest-logrotate \
                  docker-logrotate \
                  docker-engine
sudo yum install -y yum-utils &&
sudo yum-config-manager \
    --add-repo \
    https://download.docker.com/linux/centos/docker-ce.repo &&
sudo yum install -y docker-ce docker-ce-cli containerd.io &&
sudo systemctl start docker
mkdir /home/root
mkdir ~/.ssh 
touch ~/.ssh/authorized_keys 
echo "{cert text}" >> ~/.ssh/authorized_keys #用客户端证书文本替换{cert text},或改为从文件中读取
sudo curl -L "https://github.com/docker/compose/releases/download/1.28.5/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose