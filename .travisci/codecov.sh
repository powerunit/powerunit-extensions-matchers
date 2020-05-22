#!/bin/sh

mvn jacoco:report
"$HOME/.script/codecov.bash" -F"unit$TRAVIS_OS_NAME$TRAVIS_JDK_VERSION"
mvn jacoco:report-integration
"$HOME/.script/codecov.bash" -F"compiler$TRAVIS_OS_NAME$TRAVIS_JDK_VERSION"

(cd target/it && 
	for name in * ; do 
		(cd "$name" && test -e "build.log" && 
			(
				mvn jacoco:report && 
				"$HOME/.script/codecov.bash" -s . -F"unit$(echo "$name" | sed 's/[+-]//g')$TRAVIS_OS_NAME$TRAVIS_JDK_VERSION"  || echo 'NOT POSSIBLE';
				mvn jacoco:report-integration && 
				"$HOME/.script/codecov.bash" -s . -F"compiler$(echo "$name" | sed 's/[+-]//g')$TRAVIS_OS_NAME$TRAVIS_JDK_VERSION" || echo 'NOT POSSIBLE';
			)
		);			
	 done;)
