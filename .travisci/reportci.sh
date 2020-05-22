#!/bin/sh

python - --name "OS = $TRAVIS_OS_NAME - JDK = $TRAVIS_JDK_VERSION - PR = $TRAVIS_PULL_REQUEST" --tool java --input maven.log < "$HOME/.script/annotate.py";

python - --name "OS = $TRAVIS_OS_NAME - JDK = $TRAVIS_JDK_VERSION - PR = $TRAVIS_PULL_REQUEST" --include='*.xml' --framework=xunit < "$HOME/.script/upload.py";

for name in target/it/*/build.log ; do 
	python - --name "OS = $TRAVIS_OS_NAME - JDK = $TRAVIS_JDK_VERSION - PR = $TRAVIS_PULL_REQUEST" --tool java --input "$name" < "$HOME/.script/annotate.py";
done;
	
