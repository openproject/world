#!/bin/bash
basedir=$(cd "$(dirname "$0")";pwd)
cd $basedir
for file in ./*
do
if test -d $file
then
    cd $basedir/$file
    if test -f build.sh
    then
        echo found build.sh in project $file.
        echo start building project $file ...
        ./build.sh
    fi
    cd $basedir
fi
done
