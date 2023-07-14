# CPD Project 1
_________________________________________________________________________________________

## Description:

A directory with the source code and results we used for project 1 of the CPD course.

The goal of this project is to observe the effect of the memory hierarchy on the processor performance when accessing large amounts of data. This observation should be done regarding the multiplication of two matrices, using the Performance API (PAPI) to collect relevant performance indicators of the program execution in c++, while also using a second language for execution time comparissons, in this case, JAVA.

You can find all the results in the 'results' folder and the code in the 'sourcecode' folder.
_________________________________________________________________________________________

## How it works:

### C++:

This is a C++ program that performs matrix multiplication using three different algorithms: row-by-column, line-by-line, and block-by-block.
The program uses the PAPI library to measure the performance of each algorithm in terms of execution time and to acess the DCM (Data Caches Misses for the first two levels). 
It consists of three main functions OnMult(), OnMultLine(), and OnMultBlock().  When running the program, choosing the desired algorithm and one of the options with the stipulated values, you will be able to obtain the execution time, the matrix output and the attributes provided by PAPI.


### JAVA:

The program runs the algorithms to be timed and then proceeds to output a txt file for each one, with the size of the matrix and the corresponding time it took to run.

_________________________________________________________________________________________

## How to use:

### To run the c++ code:
- Open the terminal on the same directory as the matrixproduct.cpp (it can be found in the 'sourcecode' directory of this project).
- Then proceed to run the command:
```g++ -O2 matrixproduct.cpp -o matrix -lpapi```
- Once compiled, just run the command:
```./matrix```

Or

- Open the terminal on the same directory as the matrixproduct.cpp (it can be found in the 'sourcecode' directory of this project).
- Run the command : 
```make```
- Once compiled, just run the command:
```./matrix```





### To run the java code:
- Open the terminal on the same directory as the MatrixMultiplication.java (it can be found in the 'sourcecode' directory of this project).
- Then proceed to run the command:
```java MatrixMultiplication.java```

Or

- Open the terminal on the same directory as the MatrixMultiplication.java (it can be found in the 'sourcecode' directory of this project).
- Run the command : 
```make```
- Once compiled, just run the comman:
``` java MatrixMultiplication```

_________________________________________________________________________________________

## Important Notes:

C++ results are stored in seconds, while JAVA results and stored in miliseconds

Only the JAVA program has output files after running.

We saved the JAVA results as .txt files to facilitate the import of the data into excel.
_________________________________________________________________________________________

## Requirements

- c++ framework
- java framework
- papi
