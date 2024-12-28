#!/bin/bash

SharedCP="./src/*.java"
ClientCP="./CROSSClient/src/*.java"
ServerCP="./CROSSServer/src/*.java"

javac -d build/client $ClientCP $SharedCP
cd ./build/client
jar cfe ../../Client.jar ClientMain *.class

cd ../../

javac -d build/server $ServerCP $SharedCP
cd ./build/server
jar cfe ../../Server.jar ServerMain *.class
