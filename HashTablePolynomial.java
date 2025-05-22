public class HashTablePolynomial extends HashTable {
    private long totalInsertTime;
    private long totalSearchTime;
    private int collisions;
    private int searchOperations;
    private static final String DELETED = "<deleted>"; //flag de exclusão


    public HashTablePolynomial(int capacity) {
        this.capacity = capacity;
        this.size = 0;
        this.table = new String[capacity];
        this.collisions = 0;
    }

    @Override
    int hash(String key) {
        int hash = 0;   //valor hash inicial
        int p = 31; // Base polinomial (núm. primo)
        int power = 1; //potência inicial
        int length = key.length(); 
        int salt = 0x9E3779B9; //núm mágico
        
        for (int i = 0; i < length; i++) {                        
            hash = (hash + salt + (key.charAt(i) * power)) % capacity;  // valor hash += salt + (caractere * potencia atual) % capacidade (garante que fique dentro da capacidade da tabela)
            power = (power * p) % capacity;  //atualiza a potencia e aplica o modulo da capacidade para evitar overflow
        }
        return Math.abs(hash); //evita indice negativo
    }

    @Override
    boolean insert(String name) {
        if (isKeyInvalid(name)) {
            return false;
        }
    
        long startTime = System.nanoTime();
    
        if (size >= capacity) {
            System.err.println("Erro: A tabela está cheia.");
            totalInsertTime += System.nanoTime() - startTime;
            return false;
        }
    
        int index = hash(name);
        int initIndex = index;
        int attempt = 0;
    
        while (table[index] != null && !table[index].equals(DELETED)) {
            if (table[index].equals(name)) {
                System.err.println("Erro: Chave duplicada. O elemento já existe na tabela.");
                totalInsertTime += System.nanoTime() - startTime;
                return false;
            }
            if (attempt == 0) collisions++;
            attempt++;
            index = (index + 1) % capacity;
    
            if (index == initIndex) {
                System.err.println("Erro: Não foi possível inserir. Loop completo detectado.");
                totalInsertTime += System.nanoTime() - startTime;
                return false;
            }
        }
    
        table[index] = name;
        size++;
        totalInsertTime += System.nanoTime() - startTime;
        return true;
    }
    

    @Override
    boolean search(String name) {
        if (isKeyInvalid(name)) {
            return false;
        }
    
        long startTime = System.nanoTime();
        boolean found = false;
    
        int index = hash(name);
        int initIndex = index;
    
        while (table[index] != null) {
            if (!table[index].equals(DELETED) && table[index].equals(name)) { 
                found = true;
                break;
            }
            index = (index + 1) % capacity;
    
            if (index == initIndex) break;
        }
    
        totalSearchTime += System.nanoTime() - startTime;
        searchOperations++;
        return found;
    }
    

    @Override
    boolean remove(String name) {
        if (isKeyInvalid(name)) {
            return false;
        }
    
        int index = hash(name);
        int initIndex = index;
    
        while (table[index] != null) {
            if (table[index].equals(name)) {
                table[index] = DELETED; // Marca o índice como removido
                size--;
                return true;
            }
            index = (index + 1) % capacity;
    
            if (index == initIndex) break;
        }
    
        System.err.println("Erro: Elemento não encontrado para remoção.");
        return false;
    }

    private boolean isKeyInvalid(String key) {
        if (key == null || key.isEmpty()) {
            System.err.println("Erro: Chave inválida (nula ou vazia) fornecida.");
            return true;
        }
        return false;
    }

    //métodos para análise do relatório
    public int getCollisions() {
        return collisions;
    }

    public double getAverageInsertTime() {
        return (size == 0) ? 0 : (double) totalInsertTime / size;
    }

    public double getAverageSearchTime() {
        return (searchOperations == 0) ? 0 : (double) totalSearchTime / searchOperations;
    }

    public int[] getKeyDistribution() {
        int[] distribution = new int[capacity];
        for (int i = 0; i < capacity; i++) {
            distribution[i] = (table[i] != null) ? 1 : 0;
        }
        return distribution;
    }

    public void printStatistics() {
        System.out.println("\n=== Estatísticas da Tabela Hash (Polinomial) ===");
        System.out.println("Capacidade: " + capacity);
        System.out.println("Itens inseridos: " + size);
        System.out.println("Colisões totais: " + collisions);
        System.out.printf("Tempo médio de inserção: %.3f ns\n", getAverageInsertTime());
        System.out.printf("Tempo médio de busca: %.3f ns\n", getAverageSearchTime());

        int[] distribution = getKeyDistribution(); // Distribuição das chaves (clusterização)
        int clusters = 0;
        int maxClusterSize = 0;
        int currentCluster = 0;
        
        for (int i = 0; i < capacity; i++) {
            if (distribution[i] == 1) {
                currentCluster++;
                if (i == capacity - 1 || distribution[i + 1] == 0) {
                    clusters++;
                    if (currentCluster > maxClusterSize) maxClusterSize = currentCluster;
                    currentCluster = 0;
                }
            }
        }
        
        System.out.println("\nDistribuição das chaves:");
        System.out.println("- Posições ocupadas: " + size + "/" + capacity);
        System.out.println("- Número de clusters: " + clusters);
        System.out.println("=========================================");
    }
}