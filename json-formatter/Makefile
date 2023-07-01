install:
	mkdir -p bin
	rm -f bin/*
	mvn clean package -DskipTests
	mvn install dependency:copy-dependencies
	cp -f target/json-formatter.jar ./bin
	cp -f src/main/resources/json-formatter.cmd ./bin
	rsync -avh target/dependency/* ./bin

install2: install
	rsync -avh ./bin/* /mnt/c/bigdata-utils/json-formatter

