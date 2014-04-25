#!/bin/bash
./../Datasets/create.sh
mysqlimport  --ignore-lines=1 --fields-terminated-by=, --local -u penguin -p Bizeli ../Datasets/prices.csv 
