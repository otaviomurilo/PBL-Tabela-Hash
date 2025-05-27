public abstract class HashTableBase {
    protected int capacity;      
    protected int size;           // número de elementos atualmente na tabela
    protected String[] table;     // array que armazena as chaves
    protected long totalInsertTime; 
    protected long totalSearchTime; 
    protected int collisions;    
    protected int searchOperations; 
    protected static final String DELETED = "<deleted>"; // marcador para posições removidas

    // construtor: inicializa a tabela com uma capacidade fixa
    public HashTableBase(int capacity) {
        this.capacity = capacity;
        this.size = 0;
        this.table = new String[capacity];
        this.collisions = 0;
        this.searchOperations = 0;
    }


    public abstract int hash(String key); //função desenvolvida nas classes filhas

    public abstract String getHashType(); //

    public boolean insert(String key) {
        if (key == null || key.isEmpty()) return false; // chave inválida

        long startTime = System.nanoTime();

        if (size >= capacity) {
            System.err.println("Erro: Tabela cheia.");
            totalInsertTime += System.nanoTime() - startTime;
            return false;
        }

        int index = hash(key); // calcula o índice inicial
        int initialIndex = index;
        int attempt = 0;

        // procura uma posição vazia ou marcada como removida
        while (table[index] != null && !table[index].equals(DELETED)) {
            if (table[index].equals(key)) {
                System.err.println("Erro: Chave duplicada.");
                totalInsertTime += System.nanoTime() - startTime;
                return false;
            }
            if (attempt == 0) collisions++; // conta colisões apenas no primeiro deslocamento
            attempt++;
            index = (index + 1) % capacity; 
            if (index == initialIndex) {
                System.err.println("Erro: Loop completo na inserção.");
                totalInsertTime += System.nanoTime() - startTime;
                return false;
            }
        }

        table[index] = key; // insere a chave
        size++;
        totalInsertTime += System.nanoTime() - startTime;
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