/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package archivesystem;

/**
 *
 * @author macedo
 */
public class bootRecord {
    public short sectorSize;
    public short reservSector;
    public byte numFAT;
    public short entriesRootDir;
    public int totalSectors;
    public short sectorPerFat;

    public short getSectorSize() {
        this.sectorSize = 2048;
        
        return sectorSize;
    }

    public short getReservSector() {
        this.reservSector = 1;
        
        return reservSector;
    }

    public byte getNumFAT() {
        return 1;
    }

    public short getEntriesRootDir() {
        this.entriesRootDir = 32;
        
        return entriesRootDir;
    }

    public int getTotalSectors() {
        this.totalSectors = 65600;
        
        return totalSectors;
    }

    public short getSectorPerFat() {
        this.sectorPerFat = 64;
        
        return sectorPerFat;
    }
    
    
}
