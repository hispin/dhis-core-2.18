#!/bin/bash
isNotSet() {
    if [[ ! ${!1} && ${!1-_} ]]
    then
        return 1
    fi
}


if [[ $# -lt 1 ]]; then
  echo "Usage: $0 original-properties-file translation-properties-file"
  exit 1
fi

declare -A array1
declare -A array2
PROP_FILE=$2

cp $2 $2.bak
grep -Ev '^#' $1 | sed '/^\r/d' | grep -Ev '^$'| sort > tmpfile1
grep -Ev '^#' $2 | sed '/^\r/d' | grep -Ev '^$'| sort > tmpfile2

while IFS='=' read -r key val; do
        [[ $key = '#'* ]] && continue
        array1["$key"]="$val"
done < tmpfile1


while IFS='=' read -r key val; do
        [[ $key = '#'* ]] && continue
        array2["$key"]="$val"
done < tmpfile2

echo -n "" > $2

for key in "${!array1[@]}"; do

isNotSet array2[${key}]

if [ $? -ne 1  ];then

value2=${array2[$key]}
value1=${array1[$key]}

if [[ *"$value1"* != *"$value2"* ]];then
echo "$key=${array2[$key]}" >> ${PROP_FILE};
fi
fi
done

rm tmpfile1
rm tmpfile2

