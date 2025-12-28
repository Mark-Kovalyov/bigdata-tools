#!/bin/bash

java -jar bin/jdbc-export.jar \
     -u jdbc:sqlite:/bigdata/sqlite/tpb/tpb \
     --query "SELECT * FROM BOOKS" \
     --format parquet \
     --namespace torrents \
     --recordname book \
     --compression snappy \
     --outputfile /bigdata/tmp/tpb.snappy.parquet
