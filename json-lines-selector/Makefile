install:
	mkdir -p bin
	rm -f bin/*
	mvn clean package -DskipTests
	mvn install dependency:copy-dependencies
	cp -f target/json-lines-selector.jar ./bin
	cp -f src/main/resources/json-lines-selector* ./bin
	rsync -avh target/dependency/* ./bin --delete

run:
	mvn clean compile exec:java -Dexec.mainClass=mayton.json.JsonLinesSelector -Dexec.args="/bigdata/db/tcpdump/dump-002.json $$.* /dev/null"

test:
	mvn clean compile
	mvn exec:java -Dexec.mainClass=mayton.json.JsonLinesSelector -Dexec.args="src/test/resources/scott.json $$.emp.* src/test/resources/emp.jsonl"
	mvn exec:java -Dexec.mainClass=mayton.json.JsonLinesSelector -Dexec.args="src/test/resources/scott.json $$.dept.* src/test/resources/dept.jsonl"
	mvn exec:java -Dexec.mainClass=mayton.json.JsonLinesSelector -Dexec.args="src/test/resources/scott.json $$.emp[?(@.job == 'SALESMAN')] src/test/resources/emp-filtered.jsonl"

