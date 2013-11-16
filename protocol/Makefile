all: lib_so ndk_jar
	
ndk_jar: ndk_lib_so
	rm -rf jar
	mv libs lib
	mkdir -p jar
	mv lib jar/
	cp -r classes/* jar/
	mkdir -p build
	cd jar/ && zip -r ../build/protocol.jar .

ndk_lib_so: java_headers jni/jniprotocol.cpp jni/protocol.h jni/protocol.c jni/Android.mk
	$(NDK_HOME)/ndk-build

lib_so: c_obj
	mkdir -p build
	g++ -o build/libprotocol.so -shared bin/libprotocol.o bin/protocol.o

c_obj: jni/jniprotocol.cpp jni/protocol.c java_headers
	mkdir -p bin
	g++ -o bin/libprotocol.o -I"/usr/lib/jvm/default-java/include" -I"/usr/lib/jvm/default-java/include/linux" -fpic -c jni/jniprotocol.cpp
	g++ -fPIC -o bin/protocol.o -c jni/protocol.c

java_headers: java_class_files
	javah -jni -o jni/jniprotocol.h -classpath classes pro.kornev.kcar.protocol.Protocol

java_class_files: src/main/java/pro/kornev/kcar/protocol/Protocol.java src/main/java/pro/kornev/kcar/protocol/Data.java
	mkdir -p classes
	javac -d classes -cp classes src/main/java/pro/kornev/kcar/protocol/Data.java
	javac -d classes -cp classes src/main/java/pro/kornev/kcar/protocol/Protocol.java

install: build/libprotocol.so
	cp build/libprotocol.so /usr/lib

clean:
	rm -rf bin
	rm -rf obj
	rm -rf lib
	rm -rf libs
	rm -rf classes
	rm -rf jar

mrproper: clean
	rm -rf build

test:
	g++ jni/test.c jni/protocol.c -o test
	./test
	rm test
