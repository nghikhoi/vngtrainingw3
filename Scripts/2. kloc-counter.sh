#!/usr/bin/zsh
find $1 -name "*.h" -o -name "*.c" -o -name "*.cpp" -type f -exec cat {} + | grep -vE '^$'

tempFile=$(mktemp)

find $1 -name "*.h" -o -name "*.c" -o -name "*.cpp" -type f -exec cat {} + | grep -vE '^$|^//' > $tempFile

cat $tempFile

wc -l $tempFile

sed -i '/\/\*/,/\*\//d' $tempFile;

cat $tempFile
wc -l $tempFile