import java.util.Arrays;

//Nomaan Khan
//CS 4348-001
//Project 3
public class ContiguousIndexedDataBlock extends DataBlock {
    public byte[] data;

    ContiguousIndexedDataBlock() { // Initialising data block.
        data = new byte[512];
    }
    
    public void deleteCIBlock() { // Deleting block.
        for (int i = 0; i < 256; i++)
            data[i] = 0;
    }

    public void displayCI() { // Displaying block.
        for (int i = 1; i <= 512; i++){
            System.out.print(data[i-1]);
            if (i % 32 == 0)
                System.out.print("\n");
        }
        System.out.println();
    }

    public void writeToBlock(byte[] dataArray) {  // Writing to block.
        data = Arrays.copyOf(dataArray, dataArray.length);
    }

}