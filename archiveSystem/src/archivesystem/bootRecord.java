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
    public short sectorReserv;
    public byte numFAT;
    public short entriesRootDir;
    public int totalSectors;
    public short ocpSectors;

    public short getSectorSize() {
        this.sectorSize = 2048;
        
        return sectorSize;
    }

    public short getSectorReserv() {
        this.sectorReserv = 1;
        
        return sectorReserv;
    }

    public byte getNumFAT() {
        return 1;
    }

    public short getEntriesRootDir() {
        this.entriesRootDir = 64;
        
        return entriesRootDir;
    }

    public int getTotalSectors() {
        this.totalSectors = 65600;
        
        return totalSectors;
    }

    public short getOcpSectors() {
        this.ocpSectors = 66;
        
        return ocpSectors;
    }
    
    
}
