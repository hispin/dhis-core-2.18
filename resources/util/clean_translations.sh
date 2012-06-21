#!/bin/bash

if [[ $# -lt 1 ]]; then
  echo "Usage: $0 original-properties-file translation-properties-file"
  exit 1
fi

declare -A array1
declare -A array2
PROP_FILE="cleaned.properties"

while IFS='=' read -r key val; do
        [[ $key = '#'* ]] && continue
        array1["$key"]="$val"       
done < $1


while IFS='=' read -r key val; do
        [[ $key = '#'* ]] && continue
        array2["$key"]="$val"
done < $2

for key in "${!array1[@]}"; do
        echo "$key=${array2[$key]}" >> ${PROP_FILE}
done
