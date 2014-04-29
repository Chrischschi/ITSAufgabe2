
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;

//HAW©\Chiffre 1
public class HC1 {

	public void encrypt(long startValue, String in, String out) throws IOException {
		
		LCG numberGenerator = new LCG(startValue);
		FileOutputStream output = null;
		FileInputStream input = null;
		long key;
		byte[] bytes = new byte[8];
		

		try {

			input = new FileInputStream(in);
			output = new FileOutputStream(out);
			//Read ¨¹bergibt den InputStream in ein Byte-Array
			input.read(bytes);

			for (int i = 0; i < bytes.length - 1; i++) {
				//Zufallszahl wird generiert
				key = numberGenerator.nextValue();
				//Jedes Byte mittels XOR verkn¨¹pfen
				bytes[i] = (byte) (bytes[i] ^ key);

			}

			output.write(bytes);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			input.close();
			output.close();
		}

	}

	public void decode(long startValue, String in, String out) throws IOException {
		encrypt(startValue, in, out);
	}

	public static void main(String[] args) throws IOException {
		HC1 stromChiffre = new HC1();
		
		int seed = Integer.parseInt(args[0]); 
		String input = args[1];
		String output = args[2];
		
		System.out.println(Paths.get(input).toAbsolutePath());

		stromChiffre.encrypt(seed, input, output);
		//stromChiffre.decode(seed, input, output);
	}

}
