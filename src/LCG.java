


//Pseudozufallszahlgenerator
public class LCG {

	
	private long x;

	public LCG(long seed) {
		this.x = seed;
	}

	public long nextValue() {
		//Variablen a,b,m sind feste Werte aus PDF!
		long a = 2147001325;
		long b = 715136305;
		long m = (long) Math.pow(2,32);
		
		//Berechnet Zufallszahl und benutzt diese um neue zu berechnen.
		x = (a * x + b) % m;

		return x;

	}

}
