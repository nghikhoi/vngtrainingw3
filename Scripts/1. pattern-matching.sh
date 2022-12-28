#!/usr/bin/zsh
cat /var/log/auth.log | grep -E [0-9]{4}+ | wc -l

cat /var/log/auth.log | grep -E systemd-logind\[[0-9]+\] | grep -E "New|Removed"

grep -E -w 'warning|error|critical' /var/log/messages