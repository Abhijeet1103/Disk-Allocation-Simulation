// Nomaan Khan
// CS 4348-001
// Project 3
public class IndexedBlock extends AbsBlock {
	
    public int [] indexBlocks = new int[128];

    public void displayCI() { // Displaying index block.
        for (int i = 0; i < 128; i++)
            if (indexBlocks[i] != 0) 
                System.out.print(indexBlocks[i] + "	");
        System.out.println();
    }

    public void writeToIndexedBlock(int [] arrays) { // Writing to indexed block.
        for (int i = 1; i < arrays.length; i++)
            indexBlocks[i - 1] = arrays[i];
    }

    private int indexedBlockLen() { // Indexed block length.
        int len = 0;
        for (int i = 0; i < 128; i++)
            if (indexBlocks[i] != 0)
                len++;
        return len;
    }


    public int [] getIndexBlocks() { // Returns array of index blocks.
        int length = indexedBlockLen();
        int [] idxblocks = new int[length];
        for (int i = 0; i < 128; i++)
            if (indexBlocks[i] != 0)
                idxblocks[i] = indexBlocks[i];
        return idxblocks;
    }

    public void deleteIndexedBlock () { // Deleting block.
        for (int i = 0; i < 128; i++) 
            indexBlocks[i] = 0;
    }
}