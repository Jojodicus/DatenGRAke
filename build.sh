#! /bin/bash

mkdir -p build
cd src
javac -verbose -d ../build *.java
cd ..
jar cvfe DatenGRAke.jar MainServer -C build .
echo "done"
