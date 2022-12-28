#!/usr/bin/zsh
printf "%-10s %-10s\n" "Port" "User ID" && netstat -tulpne 2>/dev/null | grep LISTEN | awk '{print$4,$7}' | rev | cut -d : -f1 | rev | xargs -n2 printf '%-10s  %-10s\n'