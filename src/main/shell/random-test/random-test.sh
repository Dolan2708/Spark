#!/bin/bash

base="../../../.."

males=($(ls "${base}/male" | shuf -n "1"))
females=($(ls "${base}/female" | shuf -n "1"))

rand=$(expr ${RANDOM} % 2)
test_file=""
if [ "${rand}" -eq 1 ]; then
	test_file="${males[0]}"
else
	test_file="${females[0]}"
fi

mkdir -p "${base}/test"

echo "$(grep "${test_file}" "${base}/out.csv")" >> "${base}/test/test.csv"
