sh startAccountServer.sh
echo "startAccountServer Success============"
sh startGateServer.sh
echo "startGateServer Success============"
#sh startLiveServer.sh
#echo "startLiveServer Success============"
sh startQueueServer.sh
echo "startQueueServer Success============"
sh startBlogServer.sh
echo "startBlogServer Success============"
sh startWebServer.sh
echo "startWebServer Success============"
