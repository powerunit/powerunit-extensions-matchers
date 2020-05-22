#!/bin/sh

if test "$TRAVIS_JDK_VERSION" = openjdk8 ; then 
	mvn org.apache.maven.plugins:maven-dependency-plugin:copy -Dartifact=com.codacy:codacy-coverage-reporter:5.0.310:jar:assembly -Dmdep.stripVersion=true -Dmdep.stripClassifier=true -Dtransitive;
else
	mvn org.apache.maven.plugins:maven-dependency-plugin:copy -Dartifact=com.codacy:codacy-coverage-reporter:6.0.0:jar:assembly -Dmdep.stripVersion=true -Dmdep.stripClassifier=true -Dtransitive;
fi;

cp target/dependency/codacy-coverage-reporter.jar "$HOME/.script"
