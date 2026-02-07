#!/bin/bash -v

export GRAALVM_HOME=/soft/graalvm/25

export PATH=$GRAALVM_HOME/bin:/soft/maven/3.9.12/bin:$PATH

export CLASSPATH=$GRAALVM_HOME/lib

cd bin

native-image -jar json-lines-selector.jar --no-fallback

mv -f json-lines-selector ../release/