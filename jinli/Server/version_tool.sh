#!/bin/bash
targetVersion=$(cat $PWD/version)
echo $targetVersion
gitVersion=$(git describe --tags `git rev-list --tags --max-count=1`)
if [ $targetVersion \> $gitVersion ];
then
  echo "start create new tag'$targetVersion'"
    git tag -a $targetVersion -m "auto git tag"
    git push origin $targetVersion
else
  echo " not greater than gitVersion ,not push tag"
fi
