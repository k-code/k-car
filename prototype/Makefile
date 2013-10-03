all: lib_so
	
lib_so: c_obj
	g++ -o bin/libjkcp.so -shared bin/libjkcp.o bin/protocol.o

c_obj: cpp_src/jkcp.cpp cpp_src/protocol.c java_headers
	g++ -o bin/libjkcp.o -I"/usr/lib/jvm/default-java/include" -I"/usr/lib/jvm/default-java/include/linux" -fpic -c cpp_src/jkcp.cpp
	g++ -o bin/protocol.o -c cpp_src/protocol.c

java_headers: java_class_files
	javah -jni -o cpp_src/jkcp.h -classpath bin pro.kornev.kcar.prototype.Protocol

java_class_files: src/main/java/pro/kornev/kcar/prototype/Protocol.java src/main/java/pro/kornev/kcar/prototype/Data.java
	mkdir -p bin
	javac -d bin -cp bin src/main/java/pro/kornev/kcar/prototype/Data.java
	javac -d bin -cp bin src/main/java/pro/kornev/kcar/prototype/Protocol.java

install: all
	sudo cp bin/*.so /usr/lib

clean:
	rm -rf bin/*