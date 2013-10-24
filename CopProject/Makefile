all: java_jar

java_jar: lib_so
	rm -rf lib
	mv libs lib
	mkdir -p Cop/lib
	zip -r Cop/lib/protocol.jar lib

lib_so: java_headers jni/jniprotocol.cpp jni/protocol.h jni/protocol.c jni/Android.mk
	$(NDK_HOME)/ndk-build

java_headers: java_class_files
	javah -jni -o jni/jniprotocol.h -classpath bin pro.kornev.kcar.protocol.Protocol

java_class_files: Cop/src/main/java/pro/kornev/kcar/protocol/Protocol.java Cop/src/main/java/pro/kornev/kcar/protocol/Data.java
	mkdir -p bin
	javac -d bin -cp bin Cop/src/main/java/pro/kornev/kcar/protocol/Data.java
	javac -d bin -cp bin Cop/src/main/java/pro/kornev/kcar/protocol/Protocol.java

clean:
	rm -rf bin
	rm -rf obj
	rm -rf lib
	rm -rf libs

mrproper: clean
	rm -f Cop/lib/protocol.jar