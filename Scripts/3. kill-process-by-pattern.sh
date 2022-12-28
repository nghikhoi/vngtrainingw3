#!/usr/bin/zsh
ps auxw  | grep bash | awk '{print$2}' | xargs kill -9