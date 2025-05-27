public class HashTableMultiplier extends HashTableBase {
    public HashTableMultiplier(int capacity) {
        super(capacity);
    }

    @Override
    public int hash(String key) {
        int k = key.hashCode();
        k = k & 0x7FFFFFFF; // Garante positivo
        double A = (Math.sqrt(5) - 1) / 2;
        double frac = (k * A) % 1;
        return (int) Math.floor(capacity * frac);
    }

    @Override
    public String getHashType() {
        return "Multiplicativa";
    }

    
}