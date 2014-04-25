#!/bin/bash
read -s -p "I hate artichokes, so tell me your password: " mypassword
mysql -u penguin -p$mypassword < drop.sql
mysql -u penguin -p$mypassword < create.sql
./gencsv.sh
echo -e "\nTable dropped and recreated, inserting tuples from all .csv in Dataset folder, this may take a while..\n"
mysqlimport  --ignore-lines=1 --fields-terminated-by=, --local -u penguin -p$mypassword Bizeli ../Datasets/prices.csv 
java -cp . RemoveDuplicates $mypassword
