/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package archivesystem;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 *
 * @author macedo
 */
public class ArchiveSystem {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    
    public static void main(String[] args) throws IOException {
        // TODO code application logic here
        RandomAccessFile acsFile = new RandomAccessFile(args[0], "rw");

        formatDisc(acsFile);

    }
    
    private static final int SECTOR_SIZE = 2048;
    private static final int RESERVED_SECTORS = 1;
    private static final int FAT_COUNT = 1;
    private static final int ROOT_ENTRY_COUNT = 64;
    
    public static void formatDisc(RandomAccessFile acsFile) throws IOException{
        bootRecord record = new bootRecord();
        ByteBuffer bootRecordBuffer = ByteBuffer.allocate(SECTOR_SIZE);
        
        bootRecordBuffer.order(ByteOrder.LITTLE_ENDIAN);
        
        bootRecordBuffer.putShort(record.getSectorSize());
        bootRecordBuffer.putShort(record.getSectorReserv());
        bootRecordBuffer.put(record.getNumFAT());
        bootRecordBuffer.putShort(record.getEntriesRootDir());
        bootRecordBuffer.putInt(record.getTotalSectors());
        bootRecordBuffer.putShort(record.getOcpSectors());
      
        acsFile.seek(0);
        
        acsFile.write(bootRecordBuffer.array());
        
        
        //instanciar a classe boot record
        /*bootRecord btRec = new bootRecord();
        
        // define o pontiero para o inicio do arquivo
        acsFile.seek(0);
        
        acsFile.write(btRec.getSectorSize()[0]);
        
        acsFile.write(btRec.getSectorSize()[1]);
        
        acsFile.seek(2);
        
        acsFile.write(btRec.getSectorReserv()[0]);
        
        acsFile.write(btRec.getSectorReserv()[1]);
        
        acsFile.seek(4);
        
        acsFile.writeByte(btRec.getNumFAT());
        
        acsFile.seek(5);
        
        acsFile.write(btRec.getEntriesRootDir()[0]);
        
        acsFile.write(btRec.getEntriesRootDir()[1]);
        
        acsFile.seek(7);
        
        acsFile.write(btRec.getTotalSectors()[0]);
        
        acsFile.write(btRec.getTotalSectors()[1]);
        
        acsFile.write(btRec.getTotalSectors()[2]);
        
        acsFile.write(btRec.getTotalSectors()[3]);
        
        acsFile.seek(11);
        
        acsFile.write(btRec.getOcpSectors()[0]);
        
        acsFile.write(btRec.getOcpSectors()[1]);
        
        acsFile.seek(2048);
        
        acsFile.write();
        */
        acsFile.close();
    
    }
    
}
