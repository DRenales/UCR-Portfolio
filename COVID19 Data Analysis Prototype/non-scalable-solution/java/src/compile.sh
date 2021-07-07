#!/bin/sh

# Export classpath with the postgressql driver
export CLASSPATH=$CLASSPATH:$PWD/pg73jdbc3.jar

# compile the java program
javac  -Xlint:deprecation my179G.java

#run the java program
#Use your database name, port number and login
java my179G $USER"_DB" $PGPORT $USER
