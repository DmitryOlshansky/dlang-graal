#!/bin/bash -e


FILES=`cat std.txt`

mkdir -p tests/d
mkdir -p tests/graal

mvn package
time java -Doutdir=tests/graal -jar target/lexer-jar-with-dependencies.jar $FILES

cd dlex
dub build 
cd ..

time dlex/dlex --outdir=tests/d $FILES

for f in tests/d/*.tk ; do
    name=${f#tests/d/}
    f2=tests/graal/$name
    echo $name
    diff $f $f2
done