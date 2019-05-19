// Nomaan Khan
// CS 4348-001
// Project 3
public class ChainedFA extends AbsFileAlloc {

    public ChainedFA() { // Initialising file system.
        mainDataArray[0] = new ContiguousChainedFileTable();
        mainDataArray[1] = new Bitmap();
        for (int i = 2; i < 256; i++)
            mainDataArray[i] = new ChainedDataBlock();
        
    }

    public void displayBlock(int blockNumber) { // Displaying block.
        mainDataArray[blockNumber].displayCI();
    }
    
    public void writeToBlock(int blockNum, byte[] array) { // Writing to block.
    	((ChainedDataBlock)mainDataArray[blockNum]).writeToBlock(array);
    }

}