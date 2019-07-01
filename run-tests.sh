#!/bin/bash -e


FILES=`cat std.txt`

mkdir -p tests/d
mkdir -p tests/graal


dub --single dtool.d
./dtool --tool=lex --outdir=tests/d $FILES
./dtool --tool=lispy --outdir=tests/d tests/simple.d

mvn package
time java -Doutdir=tests/graal -Dtool=lex -jar target/lexer-jar-with-dependencies.jar $FILES
time java -Doutdir=tests/graal -Dtool=lispy -jar target/lexer-jar-with-dependencies.jar tests/simple.d

for f in tests/d/*.tk tests/d/*.ast; do
    name=${f#tests/d/}
    f2=tests/graal/$name
    # echo $name
    diff $f $f2
done