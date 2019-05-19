// Nomaan Khan
// CS 4348-001
// Project 3
public class IndexedFileTable extends FileTable {
    
	private class Entry { // For file table entries.
        String fileName;
        int indexedBlock;
        Entry(String fileName, int indexedBlock){
            this.indexedBlock = indexedBlock;
            this.fileName = fileName;
        }
    }

    private Entry[] fileTable;

    IndexedFileTable() { // Initialising file table.
        fileTable = new Entry[14];
    }

    private boolean fileTableEmpty() { // Checking if file table is empty.
        for (int i = 0; i < 12; i++)
            if (fileTable[i] != null)
                return false;
        
        return true;
    }
    
    public void displayCI() { // Displaying file table.
        if (fileTableEmpty())
            System.out.println("The file table is empty.");
        
        else 
            for (int i = 0; i < 14; i++)
                if (fileTable[i] != null)
                    System.out.println(fileTable[i].fileName + "	" + fileTable[i].indexedBlock);
                
    }

    public boolean fileExists(String fileName) { // Checking if file exists.
        for (int i = 0; i < 12; i++)
            if (fileTable[i] != null && fileTable[i].fileName.equals(fileName))
                return true;
        return false;
    }

    public boolean fileTableFull() { // Checking if file table is full.
        for (int i = 0; i < 12; i++)
            if (fileTable[i] == null)
                return false;
        
        return true;
    }

    public void addEntryToFileTable(String fileName, int indexedBlock) { // Adding entry to the file table.
        for (int i = 0; i < 12; i++)
            if (fileTable[i] == null) {
                fileTable[i] = new Entry(fileName, indexedBlock);
                break;
            }
    }

    public int getIndexedFileBlock(String fileName) { // Returns indexed file block.
        for (int i = 0; i < 12; i++) 
            if (fileTable[i] != null && fileTable[i].fileName.equals(fileName))
                return fileTable[i].indexedBlock;
        
        return 0;
    }

    public void deleteFileTableEntry(String fileName) { // Deleting file table entry.
        for (int i = 0; i < 12; i++)
            if (fileTable[i].fileName.equals(fileName)) {
                fileTable[i] = null;
                break;
            }
    }
}