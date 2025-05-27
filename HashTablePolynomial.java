public class HashTablePolynomial extends HashTableBase {
    public HashTablePolynomial(int capacity) {
        super(capacity);
    }

    @Override
    public int hash(String key) {
        int hash = 0;
        int p = 31; // Base polinomial (primo)
        int power = 1;
        int length = key.length();
        int salt = 0x9E3779B9; // Número mágico
        
        for (int i = 0; i < length; i++) {                        
            hash = (hash + salt + (key.charAt(i) * power)) % capacity;
            power = (power * p) % capacity;
        }
        return Math.abs(hash);
    }

    @Override
    public String getHashType() {
        return "Polinomial";
    }
    
}