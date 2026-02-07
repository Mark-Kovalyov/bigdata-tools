set GRAALVM_HOME=c:\graalvm\25

set PATH=%GRAALVM_HOME%\bin;c:\maven\3.9.12\bin;%PATH%

set CLASSPATH=%GRAALVM_HOME%\lib

cd bin

native-image -jar json-lines-selector.jar --no-fallback

mv json-lines-selector.exe ..\release\
