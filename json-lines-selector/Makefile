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
