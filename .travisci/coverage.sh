#!/bin/sh

mvn jacoco:report coveralls:report -DrepoToken="${repoToken}"
mvn jacoco:report-integration
mvn jacoco:merge -Djacoco.destFile=target/jacoco-aggregate.exe
mvn jacoco:report -Djacoco.dataFile=target/jacoco-aggregate.exe

(java -jar "$HOME/.script/codacy-coverage-reporter.jar" report -l Java -r target/site/jacoco/jacoco.xml)
"$HOME/.script/codecov.bash" -F"$TRAVIS_OS_NAME$TRAVIS_JDK_VERSION"
