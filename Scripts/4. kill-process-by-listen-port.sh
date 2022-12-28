#!/usr/bin/zsh
netstat -tulpn 2>/dev/null | grep LISTEN | grep :$1 | awk '{print$7}' | awk -F/ '{print $1}' | xargs kill -9