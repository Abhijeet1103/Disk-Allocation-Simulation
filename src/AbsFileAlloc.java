// Nomaan Khan
// CS 4348-001
// Project 3
abstract public class AbsFileAlloc { //  Abstract class for file allocation classes.
    AbsBlock[] mainDataArray = new AbsBlock[256]; // Initialising main data array.

    abstract public void displayBlock(int blockNumber); // Displaying block.
    
    public AbsBlock getBlock(int blockNum) {  // Returns block.
        return mainDataArray[blockNum];
    }

    public boolean isFull() {
        return ((FileTable)mainDataArray[0]).fileTableFull();
    }
    
    abstract public void writeToBlock (int blockNum, byte[] array); // Writing to block.

}