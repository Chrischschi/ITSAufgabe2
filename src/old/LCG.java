package old;

import java.util.Random;

/**
 * Created by Christian on 26.04.2014.
 *
 * IT-Sicherheit Praktikum 2 Aufgabe 1
 * Implementierung eines Linearen Kongruenzgenerators
 *
 * Die parameter der fortschaltfunktion sind dem
 * computer-algebra-system DERIVE nachempfunden.
 *
 */
public class LCG {
    //Konstanten

    //für die Lineare kongruenzmethode
    private static final long A = 3141592653L;
    private static final long B = 1L;

    private static final long M = 4294967296L; // Soll 2 ** 32 sein

    //sonstige
    //ein möglicher startwert
    public static final long DEFAULT_SEED = 42 + 23;

    //Der seed bzw später der wert für die fortschaltfunktion aus der fortschaltfunktion
    private long x;

    public LCG(long startValue) {
        this.x = startValue;
    }


    /** gibt eine zufällige 64-bit zahl zurück
     *
     * @return a random long integer
     */
    public long nextValue() {
        //x(k+1) berechnen
        long nextX = (A * x + B) % M;

        //x ersetzen
        this.x = nextX;

        //x zurückgeben;
        return this.x;
    }

    public static void main(String[] args) {
        LCG mine = new LCG(DEFAULT_SEED);
        Random java = new Random(DEFAULT_SEED);
        for(int i = 0; i < 10; i++) {
            System.out.println("Erzeugte Zufallszahl von LCG: " + mine.nextValue() + " von java.util.Random: " + java.nextInt());
        }
    }

}
