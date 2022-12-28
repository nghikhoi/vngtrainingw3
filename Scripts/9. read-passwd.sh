#!/usr/bin/zsh

printf "%-32s %-8s %-5s %-5s %-49s %-49s %-49s\n" "Username" "Password" "UID" "GID" "GECOS" "Home" "Shell" 

tmp=$(mktemp)

echo -e "$(cat /etc/passwd | tr ':' '\n')" > $tmp 

cat $tmp | xargs -d $'\n' -n7 printf "%-32s %-8s %-5s %-5s %-49s %-49s %-49s\n"

rm -f $tmp