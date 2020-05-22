#!/bin/sh

python - --name "annotate$TRAVIS_OS_NAME$TRAVIS_JDK_VERSION" --tool java --input maven.log < "$HOME/.script/annotate.py"

python - --name "upload$TRAVIS_OS_NAME$TRAVIS_JDK_VERSION" --include='*.xml' --framework=junit < "$HOME/.script/upload.py"

(cd target/it && 
	for name in * ; do 
		python - --name "$name-$TRAVIS_OS_NAME$TRAVIS_JDK_VERSION" --tool java --input "$name/build.log" < "$HOME/.script/annotate.py" || echo 'NOT FOUND'; 
	done;)
	
