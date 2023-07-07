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
                System.out.println("1.format\n2.copy\n3.ls\n4.delete\n5.cat\n6.copy outside\n7.exit");
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
                        //System.out.println(tam);
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
                    ls(acsFile);
                }else if("4".contains(readed)){
                    System.out.print("Nome do arquivo a ser deletado :");
                    String archName = read.nextLine();
                    delete(acsFile, archName);
                }else if("5".contains(readed)){
                    System.out.print("Nome do arquivo que deseja olhar :");
                    String archName = read.nextLine();
                    cat(acsFile, archName);
                }else if("6".contains(readed)){
                    System.out.print("Nome do arquivo que deseja copiar para fora :");
                    String archName = read.nextLine();
                    copyOutside(acsFile, archName);
                }else if("7".contains(readed)){
                    System.out.println("Exited!");
                    break;
                }else{
                    System.out.println("Comand not found!");
                }
            } catch (FileNotFoundException ex) {
                System.out.println("Error at main");
                Logger.getLogger(ArchiveSystem.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
    }
    private static int lltEndInt(long numero) {
	ByteBuffer bb = ByteBuffer.allocate(4);
	bb.order(ByteOrder.LITTLE_ENDIAN);
	bb.putInt((int) numero);
	return bb.wrap(bb.array()).getInt();
    }
    
    private static short lltEndShort(long numero) {
	ByteBuffer bb = ByteBuffer.allocate(2);
	bb.order(ByteOrder.LITTLE_ENDIAN);
	bb.putShort((short) numero);
	return bb.wrap(bb.array()).getShort();
    }
    
    public static void copyOutside(RandomAccessFile acsFile, String archName){
        try {
            bootRecord record = new bootRecord();
            RandomAccessFile copyFile = new RandomAccessFile("copia-".concat(archName), "rws");
            acsFile.seek(record.getSectorSize()*record.getSectorPerFat()+record.getSectorSize());
            while(true){
                if(acsFile.readByte() != 0X00 && acsFile.readByte() != 0XE5){
                    acsFile.seek(acsFile.getFilePointer()-2);
                    byte tam = 0;
                    for(int i = 0; i < 20; i++){
                        if(acsFile.read() != 0X00){
                            tam++;
                        }else{
                            break;
                        }
                    }

                    byte[] nameC = new byte[tam];
                    acsFile.seek(acsFile.getFilePointer()-(tam+1));
                    for(int i = 0; i < tam; i++){
                        nameC[i] = acsFile.readByte();
                    }
                    acsFile.seek(acsFile.getFilePointer()-tam);
                    acsFile.seek(acsFile.getFilePointer()+21);
                    
                    byte tamEx = 0;
                    for(int i = 0; i < 4; i++){
                        if(acsFile.readByte() != 0X00){
                            tamEx++;
                        }else{
                            break;
                        }
                    }
                    byte[] nameEx = new byte[tamEx];
                    acsFile.seek(acsFile.getFilePointer()-(tamEx+1));

                    for(int i = 0; i < tamEx; i++){
                        nameEx[i] = acsFile.readByte();
                    }
                    String nameExt = new String(nameEx);

                    String finalName = new String(nameC).concat(".").concat(nameExt);

                    acsFile.seek(acsFile.getFilePointer()-(21+tamEx));

                    if(archName.equalsIgnoreCase(finalName)){
                        acsFile.seek(acsFile.getFilePointer()+26); 
                        
                        int actSector = lltEndShort(acsFile.readShort());
                        actSector &= 0x0000FFFF;
                        
                        int archSize = lltEndInt(acsFile.readInt());
                        int quantSectPerFile = archSize%record.getSectorSize();
                        if(quantSectPerFile != 0){
                            quantSectPerFile += 1;
                        }
                        
                        if(quantSectPerFile >= 1 && archSize%record.getSectorSize() == 0){
                            for(int k = 0; k < quantSectPerFile; k++){
                                acsFile.seek(record.getSectorSize()*record.getSectorPerFat()+(2*record.getSectorSize())+(((actSector-2)*record.getSectorSize())));
                                if(k+1 < quantSectPerFile){
                                    for(int i = 0; i < record.getSectorSize(); i++){
                                        copyFile.write(acsFile.readByte());
                                    }
                                }
                                acsFile.seek(record.getSectorSize()+(actSector*2));
                                int next = lltEndShort(acsFile.readShort());
                                next &= 0x0000FFFF;
                                actSector = next;

                                if(next < 0XFFFE){
                                    actSector = next;
                                }else if(next >= 0xFFFE){
                                    break;
                                }
                            }
                        }else if(quantSectPerFile >= 1 && archSize%record.getSectorSize() != 0){
                            for(int k = 0; k < quantSectPerFile; k++){
                                acsFile.seek(record.getSectorSize()*record.getSectorPerFat()+(2*record.getSectorSize())+(((actSector-2)*record.getSectorSize())));
                                if(k+1 < quantSectPerFile){
                                    for(int i = 0; i < record.getSectorSize(); i++){
                                        copyFile.write(acsFile.readByte());
                                    }
                                }else if(k+1 == quantSectPerFile){
                                    for(int i = 0; i < (archSize-((quantSectPerFile-1)*record.getSectorSize())); i++){
                                        copyFile.write(acsFile.readByte());
                                    }
                                }
                                acsFile.seek(record.getSectorSize()+(actSector*2));
                                int next = lltEndShort(acsFile.readShort());
                                next &= 0x0000FFFF;
                                actSector = next;
                                if(next < 0XFFFE){
                                    actSector = next;
                                }else if(next >= 0xFFFE){
                                    break;
                                }
                            }
                        }
                    }
                    break;
                }else if(acsFile.readByte() == 0X00){
                    System.out.println("Error, archive not found");
                    break;
                }
                acsFile.seek(acsFile.getFilePointer()+32);
                System.out.println(acsFile.getFilePointer());

            }
            
        } catch (IOException ex) {
            System.out.println("Error at delete");
            Logger.getLogger(ArchiveSystem.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void cat(RandomAccessFile acsFile, String archName){
        try {
            bootRecord record = new bootRecord();
            acsFile.seek(record.getSectorSize()*record.getSectorPerFat()+record.getSectorSize());
            while(true){
                if(acsFile.readByte() != 0X00 && acsFile.readByte() != 0XE5){
                    acsFile.seek(acsFile.getFilePointer()-2);
                    byte tam = 0;
                    for(int i = 0; i < 20; i++){
                        if(acsFile.read() != 0X00){
                            tam++;
                        }else{
                            break;
                        }
                    }

                    byte[] nameC = new byte[tam];
                    acsFile.seek(acsFile.getFilePointer()-(tam+1));
                    for(int i = 0; i < tam; i++){
                        nameC[i] = acsFile.readByte();
                    }
                    acsFile.seek(acsFile.getFilePointer()-tam);
                    acsFile.seek(acsFile.getFilePointer()+21);
                    
                    byte tamEx = 0;
                    for(int i = 0; i < 4; i++){
                        if(acsFile.readByte() != 0X00){
                            tamEx++;
                        }else{
                            break;
                        }
                    }
                    byte[] nameEx = new byte[tamEx];
                    acsFile.seek(acsFile.getFilePointer()-(tamEx+1));

                    for(int i = 0; i < tamEx; i++){
                        nameEx[i] = acsFile.readByte();
                    }
                    String nameExt = new String(nameEx);

                    String finalName = new String(nameC).concat(".").concat(nameExt);

                    acsFile.seek(acsFile.getFilePointer()-(21+tamEx));

                    if(archName.equalsIgnoreCase(finalName)){
                        acsFile.seek(acsFile.getFilePointer()+26); 
                        
                        int actSector = lltEndShort(acsFile.readShort());
                        actSector &= 0x0000FFFF;
                        
                        int archSize = lltEndInt(acsFile.readInt());
                        int quantSectPerFile = archSize%record.getSectorSize();
                        if(quantSectPerFile != 0){
                            quantSectPerFile += 1;
                        }
                        
                        if(quantSectPerFile >= 1 && archSize%record.getSectorSize() == 0){
                            for(int k = 0; k < quantSectPerFile; k++){
                                acsFile.seek(record.getSectorSize()*record.getSectorPerFat()+(2*record.getSectorSize())+(((actSector-2)*record.getSectorSize())));
                                if(k+1 < quantSectPerFile){
                                    for(int i = 0; i < record.getSectorSize(); i++){
                                        System.out.print((char)acsFile.readByte());
                                    }
                                }
                                acsFile.seek(record.getSectorSize()+(actSector*2));
                                int next = lltEndShort(acsFile.readShort());
                                next &= 0x0000FFFF;
                                actSector = next;

                                if(next < 0XFFFE){
                                    actSector = next;
                                }else if(next >= 0xFFFE){
                                    break;
                                }
                            }
                        }else if(quantSectPerFile >= 1 && archSize%record.getSectorSize() != 0){
                            for(int k = 0; k < quantSectPerFile; k++){
                                acsFile.seek(record.getSectorSize()*record.getSectorPerFat()+(2*record.getSectorSize())+(((actSector-2)*record.getSectorSize())));
                                if(k+1 < quantSectPerFile){
                                    for(int i = 0; i < record.getSectorSize(); i++){
                                        System.out.print((char)acsFile.readByte());
                                    }
                                }else if(k+1 == quantSectPerFile){
                                    for(int i = 0; i < (archSize-((quantSectPerFile-1)*record.getSectorSize())); i++){
                                        System.out.print((char)acsFile.readByte());
                                    }
                                }
                                acsFile.seek(record.getSectorSize()+(actSector*2));
                                int next = lltEndShort(acsFile.readShort());
                                next &= 0x0000FFFF;
                                actSector = next;
                                if(next < 0XFFFE){
                                    actSector = next;
                                }else if(next >= 0xFFFE){
                                    break;
                                }
                            }
                        }
                    }
                    break;
                }else if(acsFile.readByte() == 0X00){
                    System.out.println("Error, archive not found");
                    break;
                }
                acsFile.seek(acsFile.getFilePointer()+32);
                System.out.println(acsFile.getFilePointer());

            }
            
        } catch (IOException ex) {
            System.out.println("Error at delete");
            Logger.getLogger(ArchiveSystem.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    public static void delete(RandomAccessFile acsFile, String archName){
        try {
            bootRecord record = new bootRecord();
            acsFile.seek(record.getSectorSize()*record.getSectorPerFat()+record.getSectorSize());
            while(true){
                if(acsFile.readByte() != 0X00 && acsFile.readByte() != 0XE5){
                    acsFile.seek(acsFile.getFilePointer()-2);
                    byte tam = 0;
                    for(int i = 0; i < 20; i++){
                        if(acsFile.read() != 0X00){
                            tam++;
                        }else{
                            break;
                        }
                    }

                    byte[] nameC = new byte[tam];
                    acsFile.seek(acsFile.getFilePointer()-(tam+1));
                    for(int i = 0; i < tam; i++){
                        nameC[i] = acsFile.readByte();
                    }
                    acsFile.seek(acsFile.getFilePointer()-tam);
                    acsFile.seek(acsFile.getFilePointer()+21);
                    
                    byte tamEx = 0;
                    for(int i = 0; i < 4; i++){
                        if(acsFile.readByte() != 0X00){
                            tamEx++;
                        }else{
                            break;
                        }
                    }
                    byte[] nameEx = new byte[tamEx];
                    acsFile.seek(acsFile.getFilePointer()-(tamEx+1));

                    for(int i = 0; i < tamEx; i++){
                        nameEx[i] = acsFile.readByte();
                    }
                    String nameExt = new String(nameEx);

                    String finalName = new String(nameC).concat(".").concat(nameExt);

                    acsFile.seek(acsFile.getFilePointer()-(21+tamEx));

                    if(archName.equalsIgnoreCase(finalName)){
                        acsFile.writeByte(0xE5);
                        //seria 26 porem ja andou 1 escrevendo ai em cima
                        acsFile.seek(acsFile.getFilePointer()+25); 
                        
                        int fisrtSector = lltEndShort(acsFile.readShort());
                        fisrtSector &= 0x0000FFFF;
                        
                        acsFile.seek(record.getSectorSize()+(fisrtSector*2));
                        while(true){
                            int readSector = lltEndShort(acsFile.readShort());
                            readSector &= 0x0000FFFF;
                            acsFile.seek(acsFile.getFilePointer()-2);
                            if(readSector >= 0xFFFE){
                                acsFile.writeShort(0x0000);
                                break;
                            }else if(readSector < 0xFFFE){
                                while(true){
                                   int readNew = lltEndShort(acsFile.readShort());
                                   readNew &= 0x0000FFFF;
                                   acsFile.seek(acsFile.getFilePointer()-2);

                                   acsFile.writeShort(0x0000);

                                   acsFile.seek(record.getSectorSize()+(readNew*2));

                                   readNew = lltEndShort(acsFile.readShort());
                                   readNew &= 0X0000FFFF;

                                   acsFile.seek(acsFile.getFilePointer()-2);

                                   if(readNew >= 0xFFF8){
                                       acsFile.writeShort(0x0000);
                                       break;
                                   }
                                }
                                break;
                            }
                        }
                        
                        break;
                    }
                }else if(acsFile.readByte() == 0X00){
                    System.out.println("Error, archive not found");
                    break;
                }
                acsFile.seek(acsFile.getFilePointer()+32);
                System.out.println(acsFile.getFilePointer());

            }
            
        } catch (IOException ex) {
            System.out.println("Error at delete");
            Logger.getLogger(ArchiveSystem.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void ls(RandomAccessFile acsFile){
        try {
            bootRecord record = new bootRecord();
            acsFile.seek(record.getSectorSize()*record.getSectorPerFat()+record.getSectorSize());
            int tamSect = 0;
            int sll = 0;
            while(true){
                short compar = acsFile.readByte();
                compar &= 0x00FF;
                
                acsFile.seek(acsFile.getFilePointer()-1);
                if(compar != 0X00 && compar != 0xE5){
                    byte tam = 0;
                    for(int i = 0; i < 20; i++){
                        if(acsFile.read() != 0X00){
                            tam++;
                        }else{
                            break;
                        }
                    }

                    byte[] nameC = new byte[tam];
                    acsFile.seek(acsFile.getFilePointer()-(tam+1));
                    for(int i = 0; i < tam; i++){
                        nameC[i] = acsFile.readByte();
                    }
                    String name = new String(nameC);
                    System.out.println("=============================================================================================================================");
                    System.out.print("Name: "+name);
                    acsFile.seek(acsFile.getFilePointer()-tam);
                    acsFile.seek(acsFile.getFilePointer()+21);
                    
                    byte tamEx = 0;
                    for(int i = 0; i < 4; i++){
                        if(acsFile.readByte() != 0X00){
                            tamEx++;
                        }else{
                            break;
                        }
                    }
                    byte[] nameEx = new byte[tamEx];
                    //o +1 é do ultima compração que anda o ponteiro para frente
                    acsFile.seek(acsFile.getFilePointer()-(tamEx+1));

                    for(int i = 0; i < tamEx; i++){
                        nameEx[i] = acsFile.readByte();
                    }
                    String nameExt = new String(nameEx);
                    System.out.print(" | Extension: "+nameExt);
                    
                    acsFile.seek(acsFile.getFilePointer()-tamEx);
                    acsFile.seek(acsFile.getFilePointer()+4);
                    byte test = acsFile.readByte();
                    if(test == 16){
                        acsFile.seek(acsFile.getFilePointer()-1);
                        System.out.print(" | Type: 0x"+Integer.toHexString(acsFile.readByte())+" (diretorio)");
                    }else if(test == 32){
                        acsFile.seek(acsFile.getFilePointer()-1);
                        System.out.print(" | Type: 0x"+Integer.toHexString((short)acsFile.readByte())+" (arquivo)");
                    }
                    
                    short fr = acsFile.readShort();
                    
                    System.out.print(" | First Sector: 0x"+lltEndShort(fr));
                    
                    int length = acsFile.readInt();
                    
                    System.out.print(" | Size: 0x"+lltEndInt(length)+"\n");
                    System.out.println("=============================================================================================================================");
                    sll++;
                    tamSect += 32;
                }else if(compar == 0X00){
                    break;
                }else if(compar == 0XE5){
                    acsFile.seek(acsFile.getFilePointer()+32);
                    tamSect += 32;
                }else if(compar != 0XE5 && tamSect <= record.getSectorSize()){
                    tamSect += 32;
                    sll++;
                }
            }
            if(sll == 0){
                System.out.println("=============================================================================================================================");
                System.out.println("empty");
                System.out.println("=============================================================================================================================");
            }
        } catch (IOException ex) {
            System.out.println("Error at ls");
            Logger.getLogger(ArchiveSystem.class.getName()).log(Level.SEVERE, null, ex);
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
            acsFile.seek(record.getSectorSize()+4);
            int localSet = 2;
            int firstSector = 0;
            while(true){
                boolean setorClean = false;

                int readed = lltEndShort(acsFile.readShort());
                readed &= 0x0000FFFF;
                
                acsFile.seek(acsFile.getFilePointer()-2);
                if(readed == 0x0000){
                    setorClean = true;
                }
                
                if(setorClean){
                    firstSector = lltEndShort(localSet);
                    firstSector &= 0x0000FFFF;
                    
                    if(quantSectPerFile == 1){
                        acsFile.writeShort(0xFEFF);
                        break;
                    }else if(quantSectPerFile > 1){
                        int aux = localSet;
                        aux++;
                        acsFile.seek(acsFile.getFilePointer()+2);
                        
                        for(int j = 0; j < quantSectPerFile;){
                            int readed1 = lltEndShort(acsFile.readShort());
                            readed1 &= 0x0000FFFF;
                
                            acsFile.seek(acsFile.getFilePointer()-2);
                            
                            if(readed1 == 0X0000 && (j+1 != quantSectPerFile)){
                                //voltando para anterior para falar que o proximo e o encontrado
                                acsFile.seek(record.getSectorSize()+(localSet*2));

                                acsFile.writeShort(lltEndShort(aux));

                                acsFile.seek(acsFile.getFilePointer()-2);

                                j++;
                                localSet = aux;
                                aux++;
                                acsFile.seek(record.getSectorSize()+(aux*2));
                            }else if(readed1 == 0X0000 && (j+1 == quantSectPerFile)){
                                acsFile.seek(record.getSectorSize()+(localSet*2));
                                acsFile.writeShort(0xFEFF);
                                j++;
                                break;
                            }else{
                                acsFile.seek(acsFile.getFilePointer()+2);
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
            
            while(true){
                if(acsFile.readByte() == 0x00 || acsFile.readByte() == 0xE5){
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
                        
                        int length = (int) copyFile.length();
                        
                        acsFile.writeInt(lltEndInt(length));

                    }else if(archCopy.contains(".")){  
                        char[] charName = archCopy.toCharArray();
                        boolean ps = false;
                        byte tamN = 0;
                        byte tamE = 0;
                        for(int i = 0; i < charName.length; i++){
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
                        String wrtExt = new String(auxExt);
                        acsFile.writeBytes(wrtName);
                        acsFile.seek(acsFile.getFilePointer()+(21-auxName.length));

                        acsFile.writeBytes(wrtExt);

                        acsFile.seek(acsFile.getFilePointer()+(4-auxExt.length));

                        acsFile.writeByte(0x20);

                        acsFile.writeShort(firstSector);

                        int length = (int) copyFile.length();
                        acsFile.writeInt(lltEndInt(length));
                    }
                    break;
                }else{
                    acsFile.seek(acsFile.getFilePointer()+30);
                }
            }
            acsFile.seek(acsFile.getFilePointer()-32+26);
            int actSector = lltEndShort(acsFile.readShort());
            actSector &= 0x0000FFFF;
            
            for(int k = 0; k < quantSectPerFile; k++){
                acsFile.seek(record.getSectorSize()*record.getSectorPerFat()+(2*record.getSectorSize())+(((actSector-2)*record.getSectorSize())));
                if(k+1 < quantSectPerFile){
                    for(int i = 0; i < record.getSectorSize(); i++){
                        acsFile.writeByte(copyFile.readByte());
                    }
                }else if(k+1 == quantSectPerFile){
                    for(int i = 0; i < (copyFile.length()-((quantSectPerFile-1)*record.getSectorSize())); i++){
                        acsFile.writeByte(copyFile.readByte());
                    }
                }
                acsFile.seek(record.getSectorSize()+(actSector*2));
                int next = lltEndShort(acsFile.readShort());
                next &= 0x0000FFFF;
                actSector = next;
                
                if(next < 0XFFFE){
                    actSector = next;
                }else if(next >= 0xFFFE){
                    break;
                }
            }
            
        } catch (IOException ex) {
            System.out.println("Error at copyArch");
            Logger.getLogger(ArchiveSystem.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(ArchiveSystem.class.getName()).log(Level.SEVERE, null, ex);
        }
    }  
}
