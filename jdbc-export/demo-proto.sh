#!/bin/bash

java -jar bin/jdbc-export.jar \
     -u jdbc:sqlite:/bigdata/sqlite/tpb/tpb \
     --query "SELECT * FROM BOOKS" \
     --format avro \
     --namespace torrents \
     --recordname book \
     --outputfile /bigdata/tmp/tpb.pb
