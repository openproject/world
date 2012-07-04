#!/bin/bash
basedir=$(cd "$(dirname "$0")";pwd)
cd $basedir

markets="dev google gfan"
for market in $markets
do
    echo packaging floworld_1.0_$market.apk ...
    sed -i "s/\(android:value=\)\"\(.*\)\"\( android:name=\"UMENG_CHANNEL\"\)/\1\"$market\"\3/g" AndroidManifest.xml
    ant -Dapk-name=floworld -Dapk-version=1.0 -Dapk-market=$market
done
