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
        
        ByteBuffer bootRecordBuffer = ByteBuffer.allocate(record.getSectorSize());
        
        bootRecordBuffer.order(ByteOrder.LITTLE_ENDIAN);
        
        //formatando boot record
        bootRecordBuffer.putShort(record.getSectorSize());
        bootRecordBuffer.putShort(record.getReservSector());
        bootRecordBuffer.put(record.getNumFAT());
        bootRecordBuffer.putShort(record.getEntriesRootDir());
        bootRecordBuffer.putInt(record.getTotalSectors());
        bootRecordBuffer.putShort(record.getSectorPerFat());
        
        //zerando bytes restantes do setor utilizado pelo boot record
        int remainingBytes = record.getSectorSize() - bootRecordBuffer.position();
        byte[] zeros = new byte[remainingBytes];
        bootRecordBuffer.put(zeros);
        
        acsFile.seek(0);
        
        acsFile.write(bootRecordBuffer.array());

        ByteBuffer fatBuffer = ByteBuffer.allocate(record.getSectorSize()*record.getSectorPerFat()+record.getSectorSize());
        byte[] fatData = new byte[record.getSectorSize()*record.getSectorPerFat()+record.getSectorSize()];
        fatBuffer.put(fatData);
        
        acsFile.seek(record.getSectorSize());
        
        acsFile.write(fatBuffer.array());

        acsFile.close();
    
    }
    
}
