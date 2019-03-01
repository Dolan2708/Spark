#!/bin/bash

base="../../../../"
output_file="${base}out.csv"
male="${base}male"
female="${base}female"

count=$(expr $(ls -l ${male} | wc -l) - 1)
for file in $(ls ${male}); do
	echo "Logging ${file}. ${count} files left..."
	java ClassLogger "${male}/${file}" "1" "${output_file}"
	count=$(expr ${count} - 1)
done

count=$(expr $(ls -l ${female} | wc -l) - 1)
for file in $(ls ${female}); do
	echo "Logging ${file}. ${count} files left..."
	java ClassLogger "${female}/${file}" "0" "${output_file}"
	count=$(expr ${count} - 1)
done
