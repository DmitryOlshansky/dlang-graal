#!/bin/bash

LIST=`grep -v -P '^#' list.txt`


dub build -b debug && cd .. && d-to-java/d-to-java -I ~/dmd2/src/druntime/import \
    -I vendor/dmd/src -J vendor/dmd -J vendor/dmd/res --out src/main/java/org/dlang/dmd/ \
    $LIST

if [ "x$1" == "x--patch" ] ; then

echo
echo "==== APPLYING PATCHES ===="
for p in d-to-java/all.patch ; do
    echo "Applying patch $p"
    patch -u -R -l -p 1 < $p
done

elif [ "x$1" != "x" ] ; then
   echo "Usage ./auto-port.sh [--patch]"
   
fi
