#!/bin/bash
#This script should clean all translation files in a given directory
#removing all non-translated keys, and any legacy keys in the translation files.
isNotSet() {
    if [[ ! ${!1} && ${!1-_} ]]
    then
        return 1
    fi
}

if [[ $# -lt 1 ]]; then
  echo "Usage: $0  directory"
  exit 1
fi

DIR=$1 #The directory should be the first argument

# failsafe - fall back to current directory
[ "$DIR" == "" ] && DIR="."

# save and change IFS
OLDIFS=$IFS
IFS=$'\n'

fileArray=($(find $DIR -type f -name "i18n_*_*.properties"))

# restore it
IFS=$OLDIFS

# get length of an array
tLen=${#fileArray[@]}

#Find the template file
TEMPLATE=($(find $DIR -name 'i18n*.properties' -type f | grep -P "i18n_(module|global).properties"))

grep -Ev '^#' $TEMPLATE | sed '/^\r/d' | grep -Ev '^$'| sort > tmpfile1
declare -A array1
while IFS='=' read -r key val; do
        [[ $key = '#'* ]] && continue
        array1["$key"]="$val"
done < tmpfile1

for (( i=0; i<${tLen}; i++ ));
	do

	declare -A array2
	PROP_FILE=${fileArray[$i]}

	cp $PROP_FILE $PROP_FILE.bak
	grep -Ev '^#' $PROP_FILE | sed '/^\r/d' | grep -Ev '^$'| sort > tmpfile2

	while IFS='=' read -r key val; do
        [[ $key = '#'* ]] && continue
        array2["$key"]="$val"
	done < tmpfile2

	echo -n "" > $PROP_FILE

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

rm tmpfile2
unset array2
echo ${#array2[@]}
done

rm tmpfile1
