import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
* Created by Christian on 28.04.2014.
*
* IT-sicherheit aufgabe 3
*
* Implementierung des Triple-DES verfahrens
*
*
*/
public class TripleDES {
    /**
* Konstanten
*/
    private static final String ENCRYPT_MODE = "encrypt";
    private static final String DECRYPT_MODE = "decrypt";

    private static final int BLOCK_LENGTH = 8; //bytes, entspricht 64 bit
    static final int DES_KEY_LENGTH = 56; //bits
    static final int TRIPLE_DES_KEY_LENGTH = 3 * DES_KEY_LENGTH;

    //Variablen
    static byte[] desKey1,desKey2,desKey3,initVector;
    
    static DES encryptWithk1, decryptWithk2, encryptWithk3;


    /**
* Die args besteht aus 4 parametern:
* "Kommandozeilen‐Parameter:
1. Dateiname einer zu verschlüsselnden/entschlüsselnden Datei
2. Dateiname einer Schlüssel‐Datei mit folgendem Inhalt:
Byte 1‐24: 24 Schlüsselbytes (3 DES‐Schlüssel à 8 Byte, wobei von jedem 
        Byte jeweils 7 Bit verwendet werden)
Byte 25‐32: 8 Bytes für den Initialisierungsvektor zum Betrieb im  
        CFB ‐ Modus
3. Dateiname der Ausgabedatei
4. Status‐String zur Angabe der gewünschten Operation: 
encrypt – Verschlüsselung der Datei
decrypt – Entschlüsselung der Datei
*/
    public static void main(String[] args) {
        //argumente vom argument-vector auslesen
        String encryptOrDecryptFileName = args[0];
        String keyFileName = args[1];
        String outputFileName = args[2];
        String mode = args[3]; //mode darf nur "encrypt" oder "decrypt" sein

        /*
if ((mode != ENCRYPT_MODE && mode != DECRYPT_MODE)) {
System.err.println("Invalid mode parameter: " +mode+" I only accept \"encrypt\" or \"decrypt\" as parameters for the mode!");
System.exit(1);
}
*/

      
        //1. bereite das oeffnen der eingabe-datei vor
        Path encryptOrDecryptFilePath = Paths.get(encryptOrDecryptFileName);
        encryptOrDecryptFilePath = encryptOrDecryptFilePath.toAbsolutePath();

        //2. extrahiere key und initialisierungsvektor
        //erstelle die arrays für die schlüssel
        desKey1 = new byte[BLOCK_LENGTH];
        desKey2 = new byte[BLOCK_LENGTH];
        desKey3 = new byte[BLOCK_LENGTH];

        //erstelle das array für den initialisierungsvektor
        initVector = new byte[BLOCK_LENGTH];

        try {
            extractBytesFrom32ByteFileTo4Arrays(keyFileName, desKey1, desKey2, desKey3, initVector);
        } catch (FileNotFoundException f) {
            f.printStackTrace();
            System.exit(1);
        }
        
        encryptWithk1 = new DES(desKey1);
    	decryptWithk2 = new DES(desKey2);
    	encryptWithk3 = new DES(desKey3);
        //3. bereite das oeffnen der ausgabe-datei vor
        Path outputFilePath = Paths.get(outputFileName);
        outputFilePath = outputFilePath.toAbsolutePath();

        //4. plaintext einlesen und in bloecke aufteilen //TODO List entfernen
        List<byte[]> plaintextInBlocks = null;
        try {
            plaintextInBlocks = toBlocks(encryptOrDecryptFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        System.out.println("Anzahl 64-bit blöcke: " + plaintextInBlocks.size());

        //5. entschlüssele/verschlüssele
        switch (mode) {
            case ENCRYPT_MODE: encrypt(plaintextInBlocks,outputFilePath); break;
            case DECRYPT_MODE: decrypt(plaintextInBlocks,outputFilePath); break;
            default: {
                System.err.println("mode parameter wasn't decrypt or encrypt!");
                System.exit(1);
            }
        }


    }

    private static List<byte[]> toBlocks(Path pathToPlaintext) throws IOException {
    	//FIXME dieses vorgehen ist falsch
        List<byte[]> result = new LinkedList<>();
        FileInputStream in = null;
        try {
            in = new FileInputStream(pathToPlaintext.toFile());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        int len;
        do {
            byte[] block = new byte[BLOCK_LENGTH];
            len = in.read(block);
            if (len > 0) result.add(block); //unschön, aber mir fällt gerade nichts besseres ein
        } while(len > 0);
        in.close();
        return result;
    }

    private static void encrypt(List<byte[]> blocks, Path outputFilePath) {


    }

    private static void decrypt(List<byte[]> blocks, Path outputFilePath) {
    	//FIXME nicht vom ende der datei beginnen und nicht datei als liste von blöcken speichern
    	// sondern einfach fileinputstream und fileoutputstream verwenden 
    	ListIterator<byte[]> blocksIterator = blocks.listIterator(blocks.size() - 1);
    	
    	LinkedList<byte[]> decryptedBlocks = new LinkedList<>();
    	
    	while(blocksIterator.hasPrevious()) {
    		byte[] cipherTextBlock = blocksIterator.previous(); 
    		byte[] cipherTextBlockOrInitVector = blocksIterator.hasPrevious() ? blocksIterator.previous() : initVector;
    		
    		
    		byte[] plainTextBlock = xorEach(cipherTextBlock,encryptBlock(cipherTextBlockOrInitVector));
    		
    		decryptedBlocks.addFirst(plainTextBlock);
    		
    		if(blocksIterator.hasPrevious()) {
    		//nach vorne setzen, da jetzt chipherTextBlockOrInitVector als cipherTextBlock drankommen soll
    		blocksIterator.next();
    		}
    	}
    	
    	//Entschlüsselte blöcke in eine datei schreiben 
    	FileOutputStream out = null;
    	try {
			out = new FileOutputStream(outputFilePath.toFile());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	for(byte[] decryptedBlock : decryptedBlocks) {
    		try {
				out.write(decryptedBlock);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	
    	//fertig geschrieben
    	try {
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	
    
    	
    }

    //returns an 8-byte array that is the result of the two input arrays xor-ed
    private static byte[] xorEach(byte[] cipherTextBlock, byte[] encryptBlock) {
    	assert(cipherTextBlock.length == BLOCK_LENGTH && encryptBlock.length == BLOCK_LENGTH);
    	byte[] result = new byte[BLOCK_LENGTH];
    	for(int i = 0; i < BLOCK_LENGTH; i++) {
    		result[i] =  (byte) (cipherTextBlock[i] ^ encryptBlock[i]); //moegliche probleme
    	}
    	
    	return result;
	
	}

	private static byte[] encryptBlock(byte[] aBlock) {
    	byte[] encryptBlock = new byte[8]; 
    	
    	
    	encryptWithk1.encrypt(aBlock, 0, encryptBlock, 0);
    	decryptWithk2.decrypt(encryptBlock, 0, encryptBlock, 0);
    	encryptWithk3.encrypt(encryptBlock, 0, encryptBlock, 0);
    	
    	return encryptBlock;
		
	}

	private static void extractBytesFrom32ByteFileTo4Arrays(String fileName,byte[] arr1,byte[] arr2,
                                                            byte[] arr3, byte[] arr4) throws FileNotFoundException {
        Path inFileName = Paths.get(fileName);
        FileInputStream in = new FileInputStream(inFileName.toFile());

        //alle 4 arrays befuellen
        try {
            in.read(arr1,0, BLOCK_LENGTH);
            in.read(arr2,0, BLOCK_LENGTH);
            in.read(arr3,0, BLOCK_LENGTH);
            in.read(arr4,0, BLOCK_LENGTH);
        } catch (IOException e) {
            System.err.println("read from key file to arrays failed");
            e.printStackTrace();
        }

        try { //input stream schließen
            in.close();
        } catch (IOException e) {
            System.err.println("closing inputStream for key file failed");
            e.printStackTrace();
        }
    }
}

