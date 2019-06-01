#!/bin/bash
./gradlew clean

./gradlew release || exit 1

git reset --hard HEAD~1
./publish-npm.sh
git pull

./gradlew build
git commit -a --amend --no-edit
git push -f

