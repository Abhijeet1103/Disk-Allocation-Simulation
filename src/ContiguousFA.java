// Nomaan Khan
// CS 4348-001
// Project 3
public class ContiguousFA extends AbsFileAlloc {

    public ContiguousFA() {
        mainDataArray[0] = new ContiguousChainedFileTable();
        mainDataArray[1] = new Bitmap();
        for (int i = 2; i < 256; i++)
            mainDataArray[i] = new ContiguousIndexedDataBlock();
    }

    public void displayBlock (int blockNumber) { // Displaying block.
        mainDataArray[blockNumber].displayCI();
    }
    
    public void writeToBlock (int blockNumber, byte[] array) {
        ((ContiguousIndexedDataBlock)mainDataArray[blockNumber]).writeToBlock(array); // Writing to contiguous or indexed block.
        ((Bitmap)mainDataArray[1]).bitmapAllocBlock(blockNumber); // Updating bit map.
    }

}