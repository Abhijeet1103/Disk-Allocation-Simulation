# Disk-Allocation-Simulation

This project simulates the following disk allocation methods: Contiguous, Chained and Indexed. 
All three disk allocation methods support the following functionalities:
1. Display a file
2. Display the file table
3. Display the free space bitmap
4. Display a disk block
5. Copy a file from the simulation to a file on the real system
6. Copy a file from the real system to a file in the simulation
7. Delete a file

List of files:

AbsBlock.java
AbsFileAlloc.java
Bitmap.java
ChainedDataBlock.java
ChainedFA.java
ContiguousChainedFileTable.java
ContiguousFA.java
ContiguousIndexedDataBlock.java
DataBlock.java
FileTable.java
IndexedBlock.java
IndexedFA.java
IndexedFileTable.java
MainProgram.java


To Compile:

javac MainProgram.java AbsBlock.java AbsFileAlloc.java Bitmap.java ChainedDataBlock.java ChainedFA.java ContiguousChainedFileTable.java ContiguousFA.java ContiguousIndexedDataBlock.java DataBlock.java FileTable.java IndexedBlock.java IndexedFA.java IndexedFileTable.java


To Execute:

java MainProgram contiguous
or
java MainProgram chained
or
java MainProgram indexed

