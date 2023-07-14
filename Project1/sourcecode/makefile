.PHONY: default build clean

default: build

build:	
	g++ -O2 matrixproduct.cpp -o matrix -lpapi
	javac MatrixProduct.java

setup:
	sudo sh -c 'echo -1 >/proc/sys/kernel/perf_event_paranoid'
	
clean:
	rm -rf matrix
	rm -rf MatrixProd.class
