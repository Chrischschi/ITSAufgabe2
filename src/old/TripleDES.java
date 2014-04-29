package old;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

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


    /**
     * Die args besteht aus 4 parametern:
         * "Kommandozeilen‐Parameter:
         1.  Dateiname einer zu verschlüsselnden/entschlüsselnden Datei
         2.  Dateiname einer Schlüssel‐Datei mit folgendem Inhalt:
         Byte 1‐24: 24 Schlüsselbytes (3 DES‐Schlüssel à 8 Byte, wobei von jedem 
                 Byte jeweils 7 Bit verwendet werden)
         Byte 25‐32: 8 Bytes für den Initialisierungsvektor zum Betrieb im  
                 CFB ‐ Modus
         3.  Dateiname der Ausgabedatei
         4.  Status‐String zur Angabe der gewünschten Operation: 
         encrypt – Verschlüsselung der Datei
         decrypt – Entschlüsselung der Datei
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
        //3. bereite das oeffnen der ausgabe-datei vor
        Path outputFilePath = Paths.get(outputFileName);
        outputFilePath = outputFilePath.toAbsolutePath();

        //4. plaintext einlesen und in bloecke aufteilen
        List<byte[]> plaintextInBlocks = null;
        try {
            plaintextInBlocks = toBlocks(encryptOrDecryptFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

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

        return result;
    }

    private static void encrypt(List<byte[]> blocks, Path outputFilePath) {


    }

    private static void decrypt(List<byte[]> blocks, Path outputFilePath) {
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
