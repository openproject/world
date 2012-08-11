#!/bin/bash
basedir=$(cd "$(dirname "$0")";pwd)
cd $basedir

markets="google gfan official dev"
for market in $markets
do
    echo packaging healthworld_1.0_$market.apk ...
    sed -i "s/\(android:value=\)\"\(.*\)\"\( android:name=\"UMENG_CHANNEL\"\)/\1\"$market\"\3/g" AndroidManifest.xml
    ant -Dapk-name=healthworld -Dapk-version=v1.0_alpha -Dapk-market=$market
done
