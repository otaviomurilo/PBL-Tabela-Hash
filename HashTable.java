abstract class HashTable {
    // atributos
    int capacity; // capacidade da tabela (32)
    int size;
    String[] table; // array para armazenar os nomes
    int collisions; // contador de colisoes
  
    // métodos
    abstract boolean insert(String name);
    abstract boolean search(String name);
    abstract boolean remove(String name);
    
    int getCollisions() {
        return collisions;
    }
    
    void printStatistics() {
    }
    
    abstract int hash(String key);
}