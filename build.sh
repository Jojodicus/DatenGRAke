#! /bin/bash

mkdir -p build
cd src
javac -verbose -d ../build *.java
cd ..
jar cvf DatenGRAke.jar -C build .
echo "done"
