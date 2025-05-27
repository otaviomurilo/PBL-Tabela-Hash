public abstract class HashTableBase {
    protected int capacity;      
    protected int size;           // número de elementos atualmente na tabela
    protected String[] table;     // array que armazena as chaves
    protected long totalInsertTime; 
    protected long totalSearchTime; 
    protected int collisions;    
    protected int searchOperations; 
    protected static final String DELETED = "<deleted>"; // marcador para posições removidas
    protected static final double MAX_LOAD_FACTOR = 0.7; // Fator de carga máximo --> 70%

    public HashTableBase(int initialCapacity) {
        this.capacity = initialCapacity;
        this.size = 0;
        this.table = new String[capacity];
        this.collisions = 0;
        this.searchOperations = 0;
    }

    // Métodos abstratos (implementados nas classes filhas)
    public abstract int hash(String key);
    public abstract String getHashType();

    public boolean insert(String key) {
        if (key == null || key.isEmpty()) return false;

        long startTime = System.nanoTime();

        if ((double) size / capacity >= MAX_LOAD_FACTOR) {
            resize();
        }

        int index = hash(key); // índice inicial
        int initialIndex = index;
        int attempt = 0;

        while (table[index] != null && !table[index].equals(DELETED)) { // procura uma posição vazia ou marcada como removida
            if (table[index].equals(key)) {
                System.err.println("Erro: Chave duplicada.");
                totalInsertTime += System.nanoTime() - startTime;
                return false;
            }
            if (attempt == 0) collisions++; // Conta colisões apenas no primeiro deslocamento
            attempt++;
            index = (index + 1) % capacity; // Sondagem linear
            if (index == initialIndex) {
                System.err.println("Erro: Loop completo na inserção.");
                totalInsertTime += System.nanoTime() - startTime;
                return false;
            }
        }

        table[index] = key; // Insere a chave
        size++;
        totalInsertTime += System.nanoTime() - startTime;
        return true;
    }

    private void resize() {
        int newCapacity = nextPrime(capacity * 2); // Dobra o tamanho (usa número primo)
        String[] oldTable = table;
        table = new String[newCapacity];
        int oldCapacity = capacity;
        capacity = newCapacity;
        size = 0; // reinicia o contador 
        collisions = 0; 

        // Reinsere todos os elementos válidos da tabela antiga (mantém consistência no rehash)
        for (int i = 0; i < oldCapacity; i++) {
            String key = oldTable[i];
            if (key != null && !key.equals(DELETED)) {
                insert(key); 
            }
        }
    }


    private int nextPrime(int number) { //otimiza a distribuicao procurando o prox número primo
        while (!isPrime(number)) {
            number++;
        }
        return number;
    }


    private boolean isPrime(int number) { 
        if (number <= 1) return false;
        if (number == 2) return true;
        if (number % 2 == 0) return false;
        for (int i = 3; i * i <= number; i += 2) {
            if (number % i == 0) return false;
        }
        return true;
    }

    public boolean search(String key) {
        if (key == null || key.isEmpty()) return false;

        long startTime = System.nanoTime();
        int index = hash(key);
        int initialIndex = index;

        // percorre a tabela até encontrar a chave ou uma posição vazia
        while (table[index] != null) {
            if (!table[index].equals(DELETED) && table[index].equals(key)) {
                totalSearchTime += System.nanoTime() - startTime;
                searchOperations++;
                return true;
            }
            index = (index + 1) % capacity; // linear probing
            if (index == initialIndex) break; // evitando loop infinito
        }

        totalSearchTime += System.nanoTime() - startTime;
        searchOperations++;
        return false;
    }

    public boolean remove(String key) {
        if (key == null || key.isEmpty()) return false;

        int index = hash(key);
        int initialIndex = index;

        // procura a chave para remoção
        while (table[index] != null) {
            if (table[index].equals(key)) {
                table[index] = DELETED; //marca com a flag "deleted"
                size--;
                return true;
            }
            index = (index + 1) % capacity;
            if (index == initialIndex) break;
        }

        System.err.println("Erro: Elemento não encontrado.");
        return false;
    }


    public int getCollisions() {
        return collisions; 
    }

    public double getAverageInsertTime() {
        return (size == 0) ? 0 : (double) totalInsertTime / size;
    }

    public double getAverageSearchTime() {
        return (searchOperations == 0) ? 0 : (double) totalSearchTime / searchOperations;
    }

    // retorna um array indicando quais posições estão ocupadas (1) ou vazias (0)
    public int[] getKeyDistribution() {
        int[] distribution = new int[capacity];
        for (int i = 0; i < capacity; i++) {
            distribution[i] = (table[i] != null && !table[i].equals(DELETED)) ? 1 : 0;
        }
        return distribution;
    }

    //estatísticas da tabela
    public void printStatistics() {
        System.out.println("\n=== Estatísticas da Tabela " + getHashType() + " ===");
        System.out.println("Capacidade: " + capacity);
        System.out.println("Itens inseridos: " + size);
        System.out.println("Colisões totais: " + collisions);
        System.out.printf("Tempo médio de inserção: %.3f ns\n", getAverageInsertTime());
        System.out.printf("Tempo médio de busca: %.3f ns\n", getAverageSearchTime());

        int[] distribution = getKeyDistribution();
        int clusters = 0;
        int maxClusterSize = 0;
        int currentCluster = 0;

        // conta clusters (grupos contíguos de posições ocupadas)
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
        System.out.println("- Tamanho do maior cluster: " + maxClusterSize);
        System.out.println("=========================================");
    }
}