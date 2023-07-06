/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package archivesystem;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Scanner;

/**
 *
 * @author macedo
 */
public class ArchiveSystem {

    /**
     * @throws java.io.IOException
     */
    public static Scanner read = new Scanner(System.in);
    
    public static void main(String[] args){
        // TODO code application logic here
        while(true){
            try {
                System.out.println("1.format\n2.copy\n3.exit");
                System.out.print("What are you need : ");
                String readed = read.nextLine();
                readed = readed.toLowerCase();
                
                RandomAccessFile acsFile = new RandomAccessFile(args[0], "rw");
                
                if("1".equals(readed)){
                    formatDisc(acsFile);
                
                }else if("2".equals(readed)){
                    System.out.println("Caminho do arquivo a ser copiado >>");
                    String archCopy = read.nextLine();
                    RandomAccessFile copyFile = new RandomAccessFile(archCopy, "rw");
                    copyArch(acsFile, copyFile);
                }else if("3".equals(readed)){
                    System.out.println("Exited!");
                    break;
                }else{
                    System.out.println("COmand not found!");
                }
            } catch (FileNotFoundException ex) {
                System.out.println("Erro at main");
            }
            
        }
    }
    
    public static void copyArch(RandomAccessFile acsFile, RandomAccessFile copyFile){
        try {
            bootRecord record = new bootRecord();
            long quantSectPerFile = Math.round(copyFile.length()/record.getSectorSize());
            
            //preciso de um contador no primeiro while, para saber em que posição está vazio
            acsFile.seek(record.getSectorSize()+4);
            while(true){
                boolean setorClean = true;
                //inicio da FAT + 4 bytes dos 2 setores reservados
                byte[] read = new byte[2];
                    
                for(int i = 2; i > 0; i--){
                    read[i] = acsFile.readByte();
                    acsFile.seek(acsFile.getFilePointer()+1);
                }
                
                acsFile.seek(acsFile.getFilePointer()-2);
                
                short readed = read[0];
                readed <<= 8;
                readed |= read[1];
                
                if(readed >= 0xFFF8){
                    setorClean = false;
                }
                
                if(setorClean){
                    if(quantSectPerFile == 1){
                        acsFile.writeShort(0xF8FF);
                        break;
                    }else if(quantSectPerFile > 1){
                        short aux = 3, auxL = 0;
                        byte auxB = 0;
                        for(int j = 0; j <= quantSectPerFile;){
                            short auxI = 0;
                            acsFile.seek(acsFile.getFilePointer()+2);
                            
                            for(int i = 2; i > 0; i--){
                                read[i] = acsFile.readByte();
                                acsFile.seek(acsFile.getFilePointer()+1);
                            }

                            acsFile.seek(acsFile.getFilePointer()-2);

                            short readed1 = read[0];
                            readed1 <<= 8;
                            readed1 |= read[1];
                            
                            if(readed1 >= 0XFFF8){
                                acsFile.seek(acsFile.getFilePointer()+2);
                                aux++;
                            }else{
                                acsFile.seek(acsFile.getFilePointer()-2);
                                auxI = aux;
                                auxL = auxI;
                                auxL >>= 8;
                                auxB = (byte) auxI;
                                auxI = auxB;
                                auxI <<= 8;
                                auxI |= auxL;
                                acsFile.writeShort(auxI);
                            }
                        }
                    }
                }
                
                acsFile.seek(acsFile.getFilePointer()+2);
            }
            
            
        } catch (IOException ex) {
            System.out.println("Erro at copyArch");
        }
        
    }

    
    public static void formatDisc(RandomAccessFile acsFile){
        try {
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
            
            //formatando o inicio da FAT 2 setores ocupados
            acsFile.seek(record.getSectorSize());
            acsFile.writeShort(0xF8FF);
            acsFile.seek(record.getSectorSize()+2);
            acsFile.writeShort(0xF8FF);
            
            //zerando os restantes dos bytes da FAT
            ByteBuffer fatBuffer = ByteBuffer.allocate(record.getSectorSize()*record.getSectorPerFat()-4);
            byte[] fatData = new byte[record.getSectorSize()*record.getSectorPerFat()-4];
            fatBuffer.put(fatData);
            
            acsFile.seek(record.getSectorSize()+4);
            
            acsFile.write(fatBuffer.array());
            
            //zerando o root dir e toda a area de dados
            ByteBuffer rootAndDataBuffer = ByteBuffer.allocate(record.getSectorSize()*record.getQuantSectorData()+record.getSectorSize());
            byte[] rootAndDataDT = new byte[record.getSectorSize()*record.getQuantSectorData()+record.getSectorSize()];
            rootAndDataBuffer.put(rootAndDataDT);
            
            acsFile.seek(record.getSectorSize()+record.getSectorPerFat()*record.getSectorSize());
            
            acsFile.write(rootAndDataBuffer.array());
            
            //fechando o arquivo
            acsFile.close();
            
        } catch (IOException ex) {
            System.out.println("Erro at formatDisc");
        }
    
    }
    
}
