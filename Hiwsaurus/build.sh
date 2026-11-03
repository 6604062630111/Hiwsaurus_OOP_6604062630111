#!/bin/sh
cd "$(dirname "$0")"
rm -rf out
mkdir out
javac -d out src/*.java
printf 'Main-Class: Main\n' > manifest.txt
jar cfm Hiwsaurus.jar manifest.txt -C out . -C . assets
rm manifest.txt
