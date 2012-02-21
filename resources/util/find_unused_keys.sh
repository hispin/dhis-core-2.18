#!/bin/bash

# Lists all property keys which are not used in source code.

# Run this program from root of a web project after cleaning target dir.

# First argument is the properties file to investigate.

if [[ $# -lt 1 ]]; then
    echo "Usage: $0 properties-file"
    exit 1
fi

echo "Keys not in use in file: $1"
echo

for i in $( cat $1 ); do
  if [[ $i =~ (^.*)(=| =).* ]]; then
    KEY=${BASH_REMATCH[1]}
    MATCH=`find . -name "*.java" -o -name "*.vm" -o -name "*.js" -o -name "*.html" | xargs grep $KEY`
    LENGTH_OF_MATCH=${#MATCH}
    if [[ ${LENGTH_OF_MATCH} == 0 ]] && [[ ${KEY} != intro_* ]]; then
      echo ${KEY}
    fi
  fi
done

echo "- Done -"
