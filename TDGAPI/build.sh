#!/bin/bash
set -e

find src -name *.java -print >javafiles
if [ ! -d bin ]; then
    mkdir bin
fi
javac --release 8 -d bin @javafiles
cp -R src/* bin
jar -cf tdg-api.jar -C bin/ .

