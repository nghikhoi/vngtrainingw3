#!/usr/bin/zsh

for ((n=60001; n<63000; n+=2))
do
	res=true
	for((i=2; i<=$n/2; i++))
	do
	  ans=$(( n%i ))
	  if [ $ans -eq 0 ]
	  then
	  	res=false
	    break
	  fi
	done
	if [ "$res" = true ]
	then
		printf "%-5s" $n
	fi
done