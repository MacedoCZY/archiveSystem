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
    public short[] sectorSize = new short[2];
    public byte sectorReserv;
    public byte numFAT;
    public byte entriesRootDir;
    public int[] totalSectors = new int[4];
    public byte[] ocpSectors = new byte[2];

    public short[] getSectorSize() {
        this.sectorSize[0] = 0;
        this.sectorSize[1] = 8;
        
        return sectorSize;
    }

    public short getSectorReserv() {
        return 1;
    }

    public short getNumFAT() {
        return 1;
    }

    public short getEntriesRootDir() {
        return 4;
    }

    public int[] getTotalSectors() {
        this.totalSectors[0] = 40;
        this.totalSectors[1] = 00;
        this.totalSectors[2] = 01;
        
        return totalSectors;
    }

    public short getOcpSectors() {
        return 66;
    }
    
    
}
