mkdir bin
javac -d bin -cp bin Cop/src/main/java/pro/kornev/kcar/protocol/Data.java
javac -d bin -cp bin Cop/src/main/java/pro/kornev/kcar/protocol/Protocol.java
javah -jni -o jni/jniprotocol.h -classpath bin pro.kornev.kcar.protocol.Protocol
ndk-build > x
del lib
move libs lib
mkdir ./Cop/lib
7z a -r -y ./Cop/lib/protocol.zip ./lib
cd ./Cop/lib
rename protocol.zip protocol.jar
cd ../../

del /P/Q bin
del /P/Q obj
del /P/Q lib
del /P/Q libs
