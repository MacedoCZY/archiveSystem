/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package archivesystem;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

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
                    System.out.print("Nome do arquivo a ser copiado :");
                    String archCopy = read.nextLine();
                    if(!archCopy.contains(".")){
                        if(archCopy.length() <= 21){
                            RandomAccessFile copyFile = new RandomAccessFile(archCopy, "r"); 
                            copyArch(acsFile, copyFile, archCopy);
                        }else{
                            System.out.println("O tamanho do nome do arquivo e maior que 21");
                        }
                    }else{
                        char[] charName = archCopy.toCharArray();
                        byte tam = 0;
                        boolean ps = false;
                        
                        for(int k = 0; k < archCopy.length(); k++){
                            if(ps){
                                tam++;
                            }
                            if(charName[k] == '.'){
                                ps = true;
                            }
                        }
                        System.out.println(tam);
                        char[] auxExt = new char[tam];
                        
                        ps = false;
                        for(int i = 0, j = 0; i < charName.length; i++){
                            if(ps){
                                auxExt[j] = charName[i];
                                j++;
                            }
   
                            if(charName[i] == '.'){
                                ps = true;
                            }
                        }

                        if(auxExt.length <= 4 && auxExt.length > 0){
                            RandomAccessFile copyFile = new RandomAccessFile(archCopy, "r");
                            copyArch(acsFile, copyFile, archCopy);
                        }else{
                            System.out.println("Extensão < 0 ou maior que 4");
                        }
                    }
                }else if("3".equals(readed)){
                    System.out.println("Exited!");
                    break;
                }else{
                    System.out.println("Comand not found!");
                }
            } catch (FileNotFoundException ex) {
                System.out.println("Error at main");
            }
            
        }
    }
    
    public static void ls(RandomAccessFile acsFile){
        try {
            bootRecord record = new bootRecord();
            acsFile.seek(record.getSectorSize()*record.getReservSector()+record.getSectorSize());
            while(true){ 
                if(acsFile.readByte() != 0X00 && acsFile.readByte() != 0XE5){
                    acsFile.seek(acsFile.getFilePointer()-1);
                    byte tam = 0;
                    for(int i = 0; i < 20; i++){
                        if(acsFile.readByte() != 0X00){
                            tam++;
                        }else{
                            break;
                        }
                    }
                    char[] nameC = new char[tam];
                    acsFile.seek(acsFile.getFilePointer()-tam);

                    for(int i = 0; i < tam; i++){
                        acsFile.seek(acsFile.getFilePointer()-1);
                        nameC[i] = acsFile.readChar();
                    }
                    String name = new String(nameC);
                    System.out.println("----------------------------------------------------");
                    System.out.println("Name: "+name);
                    System.out.println("----------------------------------------------------");
                    
                    acsFile.seek(acsFile.getFilePointer()+21);
                    
                    byte tamEx = 0;
                    for(int i = 0; i < 4; i++){
                        if(acsFile.readByte() != 0X00){
                            tamEx++;
                        }else{
                            break;
                        }
                    }
                    char[] nameEx = new char[tamEx];
                    acsFile.seek(acsFile.getFilePointer()-tamEx);

                    for(int i = 0; i < tamEx; i++){
                        acsFile.seek(acsFile.getFilePointer()-1);
                        nameEx[i] = acsFile.readChar();
                    }
                    String nameExt = new String(nameEx);
                    System.out.println("----------------------------------------------------");
                    System.out.println("Extension: "+nameExt);
                    System.out.println("----------------------------------------------------");
                    
                    
                }else{
                    acsFile.seek(acsFile.getFilePointer()+31);
                }
            }
        } catch (IOException ex) {
            System.out.println("Error at ls");
        }
    }
    
    public static void copyArch(RandomAccessFile acsFile, RandomAccessFile copyFile, String archCopy){
        try {
            bootRecord record = new bootRecord();
            int quantSectPerFile = 0;
            if(copyFile.length()%record.getSectorSize() != 0){
                quantSectPerFile = (int)((copyFile.length()/record.getSectorSize())+1);
            }else{
                quantSectPerFile = (int)((copyFile.length()/record.getSectorSize()));    
            }
            System.out.println(quantSectPerFile);
            //preciso de um contador no primeiro while, para saber em que posição está vazio
            //System.out.println("sector Size "+record.getSectorSize());
            acsFile.seek(record.getSectorSize()+4);
            short localSet = 2;
            short firstSector = 0;
            while(true){
                //System.out.println("first pointer "+Long.toHexString(acsFile.getFilePointer()));
                //System.out.println("valor no setor lido "+acsFile.readShort());
                //System.out.println("local set "+localSet);
                boolean setorClean = false;
                //inicio da FAT + 4 bytes dos 2 setores reservados
                byte[] read = new byte[2];
                //System.out.println("poniter entrada "+Long.toHexString(acsFile.getFilePointer()));
                for(int i = 1; i >= 0; i--){
                    read[i] = acsFile.readByte();
                    //acsFile.seek(acsFile.getFilePointer()+1);
                }
                //System.out.println("poniter afeter 1 for "+Long.toHexString(acsFile.getFilePointer()));
                acsFile.seek(acsFile.getFilePointer()-2);
                //System.out.println("poniter dps e -2 : "+Long.toHexString(acsFile.getFilePointer()));
                short readed = read[0];
                readed <<= 8;
                readed |= read[1];
                /*System.out.println(read[0]);
                System.out.println(read[1]);
                System.out.println(readed);*/
                if(readed == 0x0000){
                    setorClean = true;
                }
                
                if(setorClean){
                    //System.out.println("entro no setor clean");
                    firstSector = localSet;
                    short convert = firstSector;
                    convert >>= 8;
                    byte conv2 = (byte) firstSector;
                    firstSector = 0;
                    firstSector = conv2;
                    firstSector <<= 8;
                    firstSector |= convert;

                    if(quantSectPerFile == 1){
                        acsFile.writeShort(0xFEFF);
                        break;
                    }else if(quantSectPerFile > 1){
                        short aux = localSet;
                        aux++;
                        //System.out.println("local set dentro "+aux);
                        acsFile.seek(acsFile.getFilePointer()+2);
                        
                        for(int j = 0; j < quantSectPerFile;){
                            //System.out.println("quant sector per file "+ quantSectPerFile);
                            short auxL = 0;
                            byte auxB = 0;
                            short auxI = 0;
                            
                            for(int i = 1; i >= 0; i--){
                                read[i] = acsFile.readByte();
                                //acsFile.seek(acsFile.getFilePointer()+1);
                            }
                            //System.out.println("pointer dentro after for "+Long.toHexString(acsFile.getFilePointer()));
                            acsFile.seek(acsFile.getFilePointer()-2);
                            //System.out.println("pointer dentro -2 : "+Long.toHexString(acsFile.getFilePointer()));
                            short readed1 = read[0];
                            readed1 <<= 8;
                            readed1 |= read[1];
                            
                            //System.out.println("aqui se esta livre >>> "+readed1);
                            
                            if(readed1 == 0X0000 && (j+1 != quantSectPerFile)){
                                //voltando para anterior para falar que o proximo e o encontrado
                                acsFile.seek(record.getSectorSize()+(localSet*2));
                                //System.out.println("suposto local que deveria gravar o ponteiro "+(record.getSectorSize()+(localSet*2)));
                                //System.out.println("local set * 2 = "+localSet*2);
                                auxI = aux;
                                auxL = auxI;
                                auxL >>= 8;
                                auxB = (byte) auxI;
                                auxI = auxB;
                                auxI <<= 8;
                                auxI |= auxL;
                                acsFile.writeShort(auxI);
                                //System.out.println("ponteiro pos escrita "+Long.toHexString(acsFile.getFilePointer()));
                                acsFile.seek(acsFile.getFilePointer()-2);
                                //System.out.println("ponteiro pos escrita ajustado -2 : "+Long.toHexString(acsFile.getFilePointer()));
                                //System.out.println("ponteiro para proximo setor gravado "+Integer.toHexString(Short.toUnsignedInt(auxI)));
                                j++;
                                localSet = aux;
                                aux++;
                                acsFile.seek(record.getSectorSize()+(aux*2));
                                //System.out.println("ponteiro para proximo passo que irei olhar "+(record.getSectorSize()+(aux*2)));
                            }else if(readed1 == 0X0000 && (j+1 == quantSectPerFile)){
                                acsFile.seek(record.getSectorSize()+(localSet*2));
                                acsFile.writeShort(0xFEFF);
                                j++;
                            }else{
                                //System.out.println("entroooooooooooooooooooooooooooooooo");
                                acsFile.seek(acsFile.getFilePointer()+2);
                                //System.out.println("proximo passo analise pos erro "+(acsFile.getFilePointer()+2));
                                aux++;
                            }
                        }
                    }
                    break;
                }
                localSet++;
                acsFile.seek(acsFile.getFilePointer()+2);
            }
            

            acsFile.seek(record.getSectorSize()*record.getSectorPerFat()+record.getSectorSize());
            //System.out.println("pos ::"+(record.getSectorSize()*record.getSectorPerFat()+record.getSectorSize()));
            while(true){
                if(acsFile.readByte() == 0x00){
                    //System.out.println(" entero");
                    acsFile.seek(acsFile.getFilePointer()-1);
                    if(!archCopy.contains(".")){
                        acsFile.writeChars(archCopy);
                        acsFile.seek(acsFile.getFilePointer()+(21-archCopy.length()));

                        acsFile.seek(acsFile.getFilePointer()+4);
                        Path test = Paths.get(archCopy);
                        if(Files.isDirectory(test)){
                            acsFile.writeByte(10);
                        }else{
                            acsFile.writeByte(20);
                        }

                        acsFile.writeShort(firstSector);

                        acsFile.writeInt((int) copyFile.length());

                    }else if(archCopy.contains(".")){       
                        char[] charName = archCopy.toCharArray();
                        boolean ps = false;
                        byte tamN = 0;
                        byte tamE = 0;
                        for(int i = 0, j = 0; i < charName.length; i++){
                            if(ps){
                                tamE++;
                            }else if(!ps && charName[i] != '.'){
                                tamN++;
                            }

                            if(charName[i] == '.'){
                                ps = true;
                            }
                        }

                        char[] auxName = new char[tamN];
                        char[] auxExt = new char[tamE];

                        ps = false;
                        for(int i = 0, j = 0; i < charName.length; i++){
                            if(ps){
                                auxExt[j] = charName[i];
                                j++;
                            }else if(!ps && charName[i] != '.'){
                                auxName[i] = charName[i];
                            }

                            if(charName[i] == '.'){
                                ps = true;
                            }
                        }
                        String wrtName = new String(auxName);
                        System.out.println(wrtName);
                        String wrtExt = new String(auxExt);
                        System.out.println(wrtExt);
                        acsFile.writeBytes(wrtName);
                        acsFile.seek(acsFile.getFilePointer()+(21-auxName.length));

                        acsFile.writeBytes(wrtExt);

                        acsFile.seek(acsFile.getFilePointer()+(4-auxExt.length));

                        acsFile.writeByte(0x20);

                        acsFile.writeShort(firstSector);
                        //tem que converter isso para little endian *********

                        acsFile.writeInt((int) copyFile.length());
                    }
                    break;
                }else{
                    acsFile.seek(acsFile.getFilePointer()+31);
                }
            }
            
            acsFile.seek(record.getSectorSize()*record.getSectorPerFat()+record.getSectorSize()+26);
            short actSector = acsFile.readShort();
            short aux = actSector;
            aux >>= 8;
            byte aux1 = (byte) actSector;
            actSector = 0;
            actSector = aux1;
            actSector <<= 8;
            actSector |= aux;
            
            for(int k = 0; k < quantSectPerFile; k++){
                //System.out.println("quantPerfile ="+quantSectPerFile);
                acsFile.seek(record.getSectorSize()*record.getSectorPerFat()+(2*record.getSectorSize())+(((actSector-2)*record.getSectorSize())));
                //System.out.println("merda aqui >>>"+(record.getSectorSize()*record.getSectorPerFat()+(2*record.getSectorSize())+(((actSector-2)*record.getSectorSize()))));
                if(k+1 < quantSectPerFile){
                    for(int i = 0; i < record.getSectorSize(); i++){
                        acsFile.writeByte(copyFile.readByte());
                    }
                }else if(k+1 == quantSectPerFile){
                    for(int i = 0; i < (copyFile.length()-((quantSectPerFile-1)*record.getSectorSize())); i++){
                        acsFile.writeByte(copyFile.readByte());
                    }
                }
                //System.out.println("actSector ="+actSector);
                acsFile.seek(record.getSectorSize()+(actSector*2));
                //System.out.println("pos poniter ="+(record.getSectorSize()+(actSector*2)));
                short next = acsFile.readShort();
                //System.out.println("next ="+next);
                short convg = next;
                convg >>= 8;
                byte convh = (byte) next;
                next = 0;
                next = convh;
                next <<= 8;
                next |= convg;
                //System.out.println("next after ="+next);
                
                if(next < 0XFFFE){
                    actSector = next;
                }else if(next == 0xFFF8){
                    break;
                }
                //System.out.println("passouu >>>>>>>>>>>>>>>>.");
            }
            
        } catch (IOException ex) {
            System.out.println("Error at copyArch");
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
            acsFile.writeShort(0xFEFF);
            acsFile.seek(record.getSectorSize()+2);
            acsFile.writeShort(0xFEFF);
            
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
            System.out.println("Error at formatDisc");
        }
    
    }
    
}
