// Nomaan Khan
// CS 4348-001
// Project 3
public class IndexedFA extends AbsFileAlloc {

    public IndexedFA () {
    	
        mainDataArray[0] = new IndexedFileTable();
        mainDataArray[1] = new Bitmap();
        for (int i = 2; i < 256; i++) // Initialising data array.
            mainDataArray[i] = null;
    }

    public void displayBlock(int blockNumber) { // Displaying block.
    	
        if (mainDataArray[blockNumber] == null)
            System.out.println("NULL");
        else if (mainDataArray[blockNumber] instanceof IndexedBlock)
            ((IndexedBlock)mainDataArray[blockNumber]).displayCI();
        else 
            mainDataArray[blockNumber].displayCI();   
    }

    public void writeToBlock(int blockNum, byte[] array) { // Writing to block.
    	
        if (mainDataArray[blockNum] == null) {
            mainDataArray[blockNum] = new ContiguousIndexedDataBlock();
            ((ContiguousIndexedDataBlock)mainDataArray[blockNum]).writeToBlock(array);
        }
        else
            ((ContiguousIndexedDataBlock)mainDataArray[blockNum]).writeToBlock(array);

        ((Bitmap)mainDataArray[1]).bitmapAllocBlock(blockNum); // Updating bitmap.
    }

    public void writeToIndexedBlock(int indexedBlockNum, int [] blocksList) { // Writing to block.
    	
        if (mainDataArray[indexedBlockNum] == null) {
            mainDataArray[indexedBlockNum] = new IndexedBlock();
            ((IndexedBlock)mainDataArray[indexedBlockNum]).writeToIndexedBlock(blocksList);
        }
        else 
            ((IndexedBlock)mainDataArray[indexedBlockNum]).writeToIndexedBlock(blocksList);
        
        ((Bitmap)mainDataArray[1]).bitmapAllocBlock(indexedBlockNum);  // Updating bitmap.
    }

}