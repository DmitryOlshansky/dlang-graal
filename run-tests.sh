#!/bin/bash -e


FILES=`find tests -name '*.d'`

mkdir -p tests/d
mkdir -p tests/graal

mvn package
java -Doutdir=tests/graal -jar target/lexer-jar-with-dependencies.jar $FILES

cd dlex
dub build 
cd ..

dlex/dlex --outdir=tests/d $FILES
