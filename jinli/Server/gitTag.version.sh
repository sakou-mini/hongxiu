#!/bin/bash
cd /data/git/jinli/server
svn update
echo -n "svn update finish!"
git add --all
git commit -m "update resource and code"
git push
echo -n "git push finish!"
echo -n "enter tag version:"
read tagName
git tag -a $tagName -m "add new tag $tagName"
git push origin $tagName