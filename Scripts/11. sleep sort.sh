#!/usr/bin/zsh -m
setopt monitorC

tmp=$(mktemp)

argc=${#@[@]}

for i in $@; do
  printf "sleep %de-1; echo %d\n" $i $i >> $tmp
done

cat $tmp
{
cat $tmp | xargs -P $argc -I {} sh -c 'eval "$1"' - {}
} &

pid=$!

# Script execution continues here while `xargs` is running
# in the background.
echo "Waiting for commands to finish..."

# Wait for `xargs` to finish, via special variable $!, which contains
# the PID of the most recently started background process.
wait


rm -f $tmp