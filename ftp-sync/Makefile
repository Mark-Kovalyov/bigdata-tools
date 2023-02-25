DOTTY_VERSION=3.2.1
SCALA_2_VERSION=2.13.10

CURRENT_MAVEN_REPO=~/.m2/repository
CURRENT_RELASE_FOLDER=/bigdata/tools/ftp-sync
TARGET_SCALA=target/scala-$(DOTTY_VERSION)
SCALA_HOME_LIB=$(SCALA_HOME)/lib

run:
	sbt test

release: build
	mkdir -p $(CURRENT_RELASE_FOLDER)logs
	mkdir -p $(CURRENT_RELASE_FOLDER)lib
	cp -f $(TARGET_SCALA)/ftp-sync_3-0.1.0-SNAPSHOT.jar $(CURRENT_RELASE_FOLDER)
	cp -f $(TARGET_SCALA)/*.jar $(CURRENT_RELASE_FOLDER)lib/
	rm -f $(TARGET_SCALA)/*.jar $(CURRENT_RELASE_FOLDER)lib/ftp-sync_3-0.1.0-SNAPSHOT.jar
	cp -f *.properties $(CURRENT_RELASE_FOLDER)
	cp -f $(TARGET_SCALA)/*.cmd $(CURRENT_RELASE_FOLDER)
	cp -f $(TARGET_SCALA)/*.sh $(CURRENT_RELASE_FOLDER)
	cp -f logback.xml $(CURRENT_RELASE_FOLDER)

build:
	sbt package
	cp -f src/main/resources/* $(TARGET_SCALA)
	cp -f $(CURRENT_MAVEN_REPO)/net/i2p/crypto/eddsa/0.3.0/eddsa-0.3.0.jar $(TARGET_SCALA)
	cp -f $(CURRENT_MAVEN_REPO)/org/bouncycastle/bcpkix-jdk15on/1.68/bcpkix-jdk15on-1.68.jar $(TARGET_SCALA)
	cp -f $(CURRENT_MAVEN_REPO)/org/bouncycastle/bcprov-jdk15on/1.68/bcprov-jdk15on-1.68.jar $(TARGET_SCALA)
	cp -f $(CURRENT_MAVEN_REPO)/org/slf4j/slf4j-api/2.0.3/slf4j-api-2.0.3.jar $(TARGET_SCALA)
	cp -f $(CURRENT_MAVEN_REPO)/org/slf4j/slf4j-ext/2.0.3/slf4j-ext-2.0.3.jar $(TARGET_SCALA)
	cp -f $(CURRENT_MAVEN_REPO)/com/hierynomus/sshj/0.31.0/sshj-0.31.0.jar $(TARGET_SCALA)
	cp -f $(CURRENT_MAVEN_REPO)/com/github/mwiede/jsch/0.2.0/jsch-0.2.0.jar $(TARGET_SCALA)
	cp -f $(CURRENT_MAVEN_REPO)/org/apache/commons/commons-csv/1.9.0/commons-csv-1.9.0.jar $(TARGET_SCALA)
	cp -f $(CURRENT_MAVEN_REPO)/ch/qos/logback/logback-classic/1.4.4/logback-classic-1.4.4.jar $(TARGET_SCALA)
	cp -f $(CURRENT_MAVEN_REPO)/ch/qos/logback/logback-core/1.4.4/logback-core-1.4.4.jar $(TARGET_SCALA)
	cp -f $(CURRENT_MAVEN_REPO)/commons-io/commons-io/2.11.0/commons-io-2.11.0.jar $(TARGET_SCALA)
	cp -f $(SCALA_HOME_LIB)/scala3-library_3-$(DOTTY_VERSION).jar $(TARGET_SCALA)
	cp -f $(SCALA_HOME_LIB)/scala3-compiler_3-$(DOTTY_VERSION).jar $(TARGET_SCALA)
	cp -f $(SCALA_HOME_LIB)/scala3-interfaces-$(DOTTY_VERSION).jar $(TARGET_SCALA)
	cp -f $(SCALA_HOME_LIB)/scala3-staging_3-$(DOTTY_VERSION).jar $(TARGET_SCALA)
	cp -f $(SCALA_HOME_LIB)/scala3-tasty-inspector_3-$(DOTTY_VERSION).jar $(TARGET_SCALA)
	cp -f $(SCALA_HOME_LIB)/scala-library-$(SCALA_2_VERSION).jar $(TARGET_SCALA)
