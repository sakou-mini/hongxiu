docker stop jinli-game
docker rm jinli-game
sed 's/%cd%/$PWD/g' docker_start_game.bat > docker_start_game.sh
sh docker_start_game.sh
rm docker_start_game.sh