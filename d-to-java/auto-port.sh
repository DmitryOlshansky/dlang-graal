#!/bin/bash

LIST=`grep -v -P '^#' list.txt`


dub build -b debug && ./d-to-java -I ~/dmd2/src/druntime/import \
    -I vendor/dmd/src -J vendor/dmd --out ../src/main/java/org/dlang/dmd/ \
    $LIST

if [ "x$1" == "x--patch" ] ; then

echo
echo "==== APPLYING PATCHES ===="
cd ..
for p in d-to-java/all.patch ; do
    echo "Applying patch $p"
    patch -u -R -l -p 1 < $p
done

elif [ "x$1" != "x" ] ; then
   echo "Usage ./auto-port.sh [--patch]"
   
fi
