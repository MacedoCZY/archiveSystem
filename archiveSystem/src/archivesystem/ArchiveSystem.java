/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package archivesystem;
import java.io.*;

/**
 *
 * @author macedo
 */
public class ArchiveSystem {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here

    }

    public void formatDisc(String args) throws FileNotFoundException, IOException{
        //abrir o arquivo em modo leitura e escrita, com base no args passado
        RandomAccessFile acsFile = new RandomAccessFile(args, "rw");
        
        //instanciar a classe boot record
        bootRecord btRec = new bootRecord();
        
        //definir o ponteiro do arquivo para o inicio
        acsFile.seek(0);
        
        acsFile.writeShort(btRec.getSectorSize()[0]);
        
        acsFile.seek(1);
 
        acsFile.writeShort(btRec.getSectorSize()[1]);

        
    }
    
}
