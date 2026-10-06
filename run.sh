#!/bin/bash

mvn clean package -DskipTests

VERSION=$(grep -m1 '<version>' pom.xml | sed -E 's/.*<version>(.*)<\/version>.*/\1/')
JMETER_HOME="/Users/naveenkumar/Tools/apache-jmeter-5.6.3"

rm -f "$JMETER_HOME/lib/ext/jmeter-agent-"*.jar

cp "target/jmeter-agent-${VERSION}.jar" "$JMETER_HOME/lib/ext"

