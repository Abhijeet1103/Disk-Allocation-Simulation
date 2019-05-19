// Nomaan Khan
// CS 4348-001
// Project 3
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Scanner;

public class MainProgram {
	
	public static AbsFileAlloc fs;

    public static void main (String [] args) throws Exception {
    	
    	int option = 0;
        String fsMethod = args[0];
        
        if (fsMethod.equals("contiguous"))
            fs = new ContiguousFA();
        else if (fsMethod.equals("chained"))
            fs = new ChainedFA();
        else
            fs = new IndexedFA();
        
        do {
            System.out.println("1) Display a file.");
            System.out.println("2) Display the file table.");
            System.out.println("3) Display the free space bitmap.");
            System.out.println("4) Display a disk block.");
            System.out.println("5) Copy a file from the simulation to a file on the real system.");
            System.out.println("6) Copy a file from the real system to a file in the simulation.");
            System.out.println("7) Delete a file.");
            System.out.println("8) Exit.");
            
            @SuppressWarnings("resource")
    		Scanner s = new Scanner(System.in);
            String arg2 = s.next();
            option = Integer.parseInt(arg2);

            switch (option) {
            
                case 1: // Display a file.
                	Scanner s5 = new Scanner(System.in);
                    String diskFile2 = s5.nextLine();
                    displayFile(diskFile2);
                    break;
                    
                case 2: // Display the file table.
                	fs.displayBlock(0);
                    break;
                    
                case 3: // Display bitmap.
                	fs.displayBlock(1);
                    break;
                    
                case 4: // Display a disk block.
                    String arg3 = s.next();
                    int blockNumber = Integer.parseInt(arg3);
                    fs.displayBlock(blockNumber);
                    break;
                    
                case 5: // Copy a file from the simulation to a file on the real system.
                	System.out.print("Copy from: ");
                	Scanner s2 = new Scanner(System.in);
                    diskFile2 = s2.nextLine();
                    System.out.print("Copy to: ");
                    String realSysFile2 = s2.nextLine();
                    copyToRealSystem(diskFile2, realSysFile2);
                    System.out.println(diskFile2 + " copied.");
                    break;
                    
                case 6: // Copy a file from the real system to a file in the simulation.
                    String realSysFile = "";
                    String diskFile = "";
                    File file = null;
                    Scanner s3 = new Scanner(System.in);
                    do {
                        System.out.print("Copy From: ");
                        realSysFile = s3.nextLine().trim();
                        file = new File(realSysFile);
                        System.out.println(file.getName());
                        
                        System.out.print("Copy to: ");
                        diskFile = s3.nextLine().trim();
                    }
                    while (file.getName().length() > 8 || diskFile.length() > 8);

                    if (fs.isFull())
                    	System.out.print("File table is full.");
                    else if (doesFileExist(realSysFile) || doesFileExist(diskFile))
                    	System.out.print("File already exists.");
                    else {
                    	copyFileToDisk(realSysFile, diskFile);
                    	System.out.println(realSysFile + " copied.");
                    }
                    break;
                    
                case 7: // Delete a file.
                    System.out.print("File name: ");
                    Scanner s4 = new Scanner(System.in);
                    diskFile2 = s4.nextLine();
                    deleteFile(diskFile2);
                    break;
                }
        }
        while (option != 8);
    }
    
    public static boolean doesFileExist(String fName) { // Does file exist.
    	
        if (fs instanceof ContiguousFA || fs instanceof ChainedFA)
            return ((ContiguousChainedFileTable)fs.getBlock(0)).fileExists(fName);
        
        else if (fs instanceof IndexedFA) {
            IndexedFileTable indexedFileTable = (IndexedFileTable)fs.getBlock(0);
            return indexedFileTable.fileExists(fName);
        }
        else
            System.out.println();
        
        return true;
    }
    
    
    public static void copyFileToDisk(String realSysFile, String diskFile) throws Exception { // Copy file to disk.
        
    	int firstBlock = 0;
    	byte[] fileData = Files.readAllBytes(new File(realSysFile).toPath()); 
        int blockLen = 0;
        byte[] arr = new byte[508];
        int c = 0;

        if (fs instanceof ContiguousFA) { // For contiguous file allocation.
        	if (fileData.length > 5120)
                throw new Exception("The file is too big for the memory.");
            
            else {
                
                blockLen = (int) Math.ceil((double)fileData.length / 512); // Block length.

                firstBlock = ((Bitmap)(fs.getBlock(1))).getFirstBlock(blockLen); // Getting empty blocks.
                System.out.println("The file is written from block " + firstBlock);

                for (int i = 1; i <= fileData.length; i++) { // Writing to blocks.
                    int j = i % 512;

                    if (j % 512 == 0 || i == (fileData.length - 1)){
                        arr = Arrays.copyOfRange(fileData, 0, 513);

                        if (c <= blockLen) {
                            fs.writeToBlock(firstBlock + c, arr);
                            c++;
                        }
                    }
                }

                ((ContiguousChainedFileTable)fs.getBlock(0)).addEntryToFileTable(realSysFile, firstBlock, blockLen); // Adding entry to the file table.
            }
        }
        
        else if (fs instanceof ChainedFA) { // For chained file system.
            
            ChainedDataBlock chainedBlock;
            int [] emptyBlocks;
            
            int blockNum = 0;
            if (fileData.length > 5080)
                throw new Exception("The file is too big for the memory.");
            
            else {
            	blockLen = (int) Math.ceil((double)fileData.length / 508); // Block length.

                emptyBlocks = ((Bitmap)fs.getBlock(1)).emptyBlocks(blockLen); // Getting empty block.
                firstBlock = emptyBlocks[0];

                for (int i = 1; i <= fileData.length; i++) { // Writing to blocks. 
                    
                	int j = i % 508;

                    if (j % 508 == 0 || i == (fileData.length - 1)) {
                        
                    	arr = Arrays.copyOfRange(fileData, 0, 509);
                        if (c < blockLen) {
                            
                            if (c != (blockLen - 1)) {
                                blockNum = emptyBlocks[c];
                                chainedBlock = ((ChainedDataBlock)fs.getBlock(blockNum));
                                chainedBlock.writeToBlock(arr, emptyBlocks[c + 1]);
                                ((Bitmap)(fs.getBlock(1))).bitmapAllocBlock(blockNum);
                            }
                            else if (c == blockLen - 1){
                                blockNum = emptyBlocks[c];
                                ((ChainedDataBlock)fs.getBlock(blockNum)).writeToBlock(arr, -1);
                                ((Bitmap)(fs.getBlock(1))).bitmapAllocBlock(blockNum);
                            }
                            c++;
                        }
                    }
                }
                
                ((ContiguousChainedFileTable)fs.getBlock(0)).addEntryToFileTable(realSysFile, firstBlock, blockLen); // Adding entry to the file table.
            }

        }
        
        else { // For indexed file allocation.
        	int [] emptyBlocks;
        	int indexedBlock = 0;
            int blockNum = 0;

            if (fileData.length > 5120)
                throw new Exception("The file is too big for the memory.");
            
            else {
                blockLen = (int) Math.ceil((double)fileData.length / 508); // Block length.
                emptyBlocks = ((Bitmap)fs.getBlock(1)).emptyBlocks(blockLen + 1); // Getting empty blocks.

                indexedBlock = emptyBlocks[0]; // First block.
                ((IndexedFA) fs).writeToIndexedBlock(indexedBlock, emptyBlocks);

                for (int i = 1; i <= fileData.length; i++) { // Writing to block.
                    
                	int j = i % 512;
                    if (j % 512 == 0 || i == (fileData.length - 1)){
                        arr = Arrays.copyOfRange(fileData, 0, 513);

                        if (c <= blockLen) {
                            if (c != blockLen) {
                                blockNum = emptyBlocks[c + 1];
                                (fs).writeToBlock(blockNum, arr);
                            }
                            else
                                break;
                            
                            c++;
                        }
                    }
                }

                ((IndexedFileTable)fs.getBlock(0)).addEntryToFileTable(realSysFile, indexedBlock); // Adding entry to file table.
            }

        }
    }

    public static void displayFile(String fileName) {
        if (fs instanceof ContiguousFA) { // For contiguous file allocation.
        	
            int firstBlock = 0; // First Block.
            int fileLen = 0; // File length.

            firstBlock = ((ContiguousChainedFileTable)fs.getBlock(0)).getFileFirstBlock(fileName);
            fileLen = ((ContiguousChainedFileTable)fs.getBlock(0)).getFileLen(fileName);

            for (int i = firstBlock; i < (firstBlock + fileLen); i++) { // Displaying file.
                fs.displayBlock(i);
                System.out.println();
            }
        }
        
        else if (fs instanceof ChainedFA) { // For chained file allocation.
            int firstBlock = ((ContiguousChainedFileTable)fs.getBlock(0)).getFileFirstBlock(fileName); // First Block.
            int fileLen = ((ContiguousChainedFileTable)fs.getBlock(0)).getFileLen(fileName); // File length.
            
            int nextBlock = firstBlock;

            for (int i = firstBlock; i < (firstBlock + fileLen); i++) {
                if (nextBlock != -1) {
                    ((ChainedDataBlock)fs.getBlock(nextBlock)).displayCI();
                    nextBlock = ((ChainedDataBlock)fs.getBlock(nextBlock)).nextBlock;
                }
                else 
                    break;
            }
        }
        
        else { // For indexed file allocation.
            IndexedFileTable indexedBlockFileTable = ((IndexedFileTable)fs.getBlock(0));

            int indexedBlockNum = indexedBlockFileTable.getIndexedFileBlock(fileName);

            IndexedBlock indexedBlock = ((IndexedBlock)fs.getBlock(indexedBlockNum)); 
            int [] blockList = indexedBlock.getIndexBlocks(); // List of blocks.

            ContiguousIndexedDataBlock contiguousIndexedDataBlock;
            for (int i = 0; i < blockList.length; i++) { // Displaying the block.
                int blockNum = blockList[i];
                contiguousIndexedDataBlock = ((ContiguousIndexedDataBlock)fs.getBlock(blockNum));

                contiguousIndexedDataBlock.displayCI();

                System.out.println();
            }
        }
    }
    
    public static void copyToRealSystem(String diskFile, String realSysFile) throws Exception, IOException { // Copy file to real system.
    	
    	byte [] fileData = null;
        
    	if (fs instanceof ContiguousFA) { // For contiguous file allocation.
            
    		int fileLen = ((ContiguousChainedFileTable)fs.getBlock(0)).getFileLen(diskFile);
            int firstBlock = ((ContiguousChainedFileTable)fs.getBlock(0)).getFileFirstBlock(diskFile);

            fileData = new byte[(512 * fileLen)];

            int c = 0;
            for (int i = firstBlock; i < (firstBlock + fileLen); i++) { // Copying data.
                
            	if (c == fileLen)
                    break;

                byte [] arr = ((ContiguousIndexedDataBlock)fs.getBlock(i)).data;
                System.arraycopy(arr, 0, fileData, c * 512, 512);
                c++;
            }

    	}
    	
        else if (fs instanceof ChainedFA) { // For chained file allocation method.
        	
        	int fileLen = ((ContiguousChainedFileTable)fs.getBlock(0)).getFileLen(diskFile); // File length.
            int firstBlock = ((ContiguousChainedFileTable)fs.getBlock(0)).getFileFirstBlock(diskFile); // First block.
            int nextBlock = firstBlock;

            fileData = new byte[(508 * fileLen)];

            int c = 0;
            for (int i = firstBlock; i < (firstBlock + fileLen); i++) {  // Copying data.
            	
                if (nextBlock != -1) {
                    byte [] array = ((ChainedDataBlock)fs.getBlock(nextBlock)).data;
                    System.arraycopy(array, 0, fileData, c * 508, 508);
                    nextBlock = ((ChainedDataBlock)fs.getBlock(nextBlock)).nextBlock;
                    c++;
                }
                else 
                    break;   
            }
        }
    	
        else { // For indexed file allocation method.
        	
        	IndexedFileTable indexedFileTable = ((IndexedFileTable)fs.getBlock(0));
            int indexedBlockNumber = indexedFileTable.getIndexedFileBlock(diskFile);

            IndexedBlock indexedBlock = ((IndexedBlock)fs.getBlock(indexedBlockNumber));
            int [] blockList = indexedBlock.getIndexBlocks();
            int length = blockList.length;

            fileData = new byte[(512 * length)];

            int c = 0;
            for (int i = 0; i < length; i++) {  // Copying data.
                int blockNum = blockList[i];
                if (c == length)
                    break;
                byte [] array = ((ContiguousIndexedDataBlock)fs.getBlock(blockNum)).data;
                System.arraycopy(array, 0, fileData, c * 512, 512);
                c++;
            }

        }
    	// Writing to file on system.
        File file = new File(realSysFile);
        FileOutputStream fos = new FileOutputStream(file.getAbsolutePath());
        try {
            fos.write(fileData);
        } 
        finally {
            fos.close();
        }
    	       
    }

    static void deleteFile(String fileName) {
        if (fs instanceof ContiguousFA) { // For contiguous file allocation.
        	
        	int fileLen = ((ContiguousChainedFileTable)fs.getBlock(0)).getFileLen(fileName); // File length.
            int firstBlock = ((ContiguousChainedFileTable)fs.getBlock(0)).getFileFirstBlock(fileName); // First block.

            for (int i = firstBlock; i < (firstBlock + fileLen); i++) { // Deleting file.
                ((ContiguousIndexedDataBlock)fs.getBlock(i)).deleteCIBlock();
                ((Bitmap)fs.getBlock(1)).bitmapDeleteBlock(i); // Updating bitmap.
            }

            ((ContiguousChainedFileTable)fs.getBlock(0)).deleteFileEntry(fileName); // Deleting from file table.
        }
        else if (fs instanceof ChainedFA) { // For chained file allocation.
            int fileLen = ((ContiguousChainedFileTable)fs.getBlock(0)).getFileLen(fileName); // File length.
            int firstBlock = ((ContiguousChainedFileTable)fs.getBlock(0)).getFileFirstBlock(fileName); // First block of file.
            int nextBlock = firstBlock;
            
            int tempBlock;
            for (int i = firstBlock; i < (firstBlock + fileLen); i++) { // Deleting blocks.
                if (nextBlock != -1) {
                    tempBlock = ((ChainedDataBlock)fs.getBlock(nextBlock)).nextBlock;
                    ((ChainedDataBlock)fs.getBlock(nextBlock)).deleteBlock();
                    ((Bitmap)fs.getBlock(1)).bitmapDeleteBlock(nextBlock);
                    nextBlock = tempBlock;
                }
                else 
                    break;  
            }

            ((ContiguousChainedFileTable)fs.getBlock(0)).deleteFileEntry(fileName); // Deleting entry from file table.

        }
        
        else { // For indexed file allocation.
            
            IndexedFileTable indexedFileTable = ((IndexedFileTable)fs.getBlock(0));
            int indexedBlockNum = indexedFileTable.getIndexedFileBlock(fileName);

            // get the list of block
            IndexedBlock indexedBlock = ((IndexedBlock)fs.getBlock(indexedBlockNum));
            int [] blockList = indexedBlock.getIndexBlocks();

            indexedBlock.deleteIndexedBlock(); // Deleting from index table.
            ((Bitmap)fs.getBlock(1)).bitmapDeleteBlock(indexedBlockNum); // Updating bitmap.

            ContiguousIndexedDataBlock contiguousIndexedDataBlock;
            for (int i = 0; i < blockList.length; i++) { // Deleting blocks.
                int blockNum = blockList[i];
                contiguousIndexedDataBlock = ((ContiguousIndexedDataBlock) fs.getBlock(blockNum));
                contiguousIndexedDataBlock.deleteCIBlock();
                ((Bitmap)fs.getBlock(1)).bitmapDeleteBlock(blockNum);
            }

            ((IndexedFileTable)fs.getBlock(0)).deleteFileTableEntry(fileName); // Deleting from the file table.

        }
    }

}