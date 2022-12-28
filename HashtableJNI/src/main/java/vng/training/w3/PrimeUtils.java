package vng.training.w3;

import static java.lang.Math.floor;
import static java.lang.Math.sqrt;

public class PrimeUtils {

    public static int isPrime(int x) {
        if (x < 2) { return -1; }
        if (x < 4) { return 1; }
        if ((x % 2) == 0) { return 0; }
        for (int i = 3; i <= floor(sqrt(x)); i += 2) {
            if ((x % i) == 0) {
                return 0;
            }
        }
        return 1;
    }

    public static int nextPrime(int x) {
        while (isPrime(x) != 1) {
            x++;
        }
        return x;
    }

}
