#!/bin/bash

LIST=`grep -v -P '^#' list.txt`

dub build -b debug && ./d-to-java -I ~/dmd2/src/druntime/import \
    -I vendor/dmd/src -J vendor/dmd --out ../src/main/java/org/dlang/dmd/ \
    $LIST
