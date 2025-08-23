build:
	mvn clean package
	mvn install dependency:copy-dependencies
	mkdir -p bin/
	cp -f target/jdbc-export.jar bin/
	cp -fr target/dependency/* bin/

clean:
	rm -f bin/*

build-graal: build
	cd bin && native-image -jar jdbc-export.jar --no-fallback


