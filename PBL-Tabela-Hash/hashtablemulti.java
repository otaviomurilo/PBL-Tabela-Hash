public class hashtablemulti extends HashTable {
    private long totalInsertTime;
    private long totalSearchTime;
    private int collisions;
    private int searchOperations;
    private static final String DELETED = "<deleted>";

    public hashtablemulti(int capacity) {
        this.capacity = capacity;
        this.size = 0;
        this.table = new String[capacity];
        this.collisions = 0;
    }

    @Override
    int hash(String key) {
        int k = key.hashCode();
        k = k & 0x7FFFFFFF;
        double A = (Math.sqrt(5) - 1) / 2;
        double frac = (k * A) % 1;
        return (int) Math.floor(capacity * frac);
    }

    @Override
    boolean insert(String key) {
        if (key == null || key.isEmpty()) return false;

        long startTime = System.nanoTime();

        if (size >= capacity) {
            System.err.println("Erro: Tabela cheia.");
            totalInsertTime += System.nanoTime() - startTime;
            return false;
        }

        int index = hash(key);
        int initialIndex = index;
        int attempt = 0;

        while (table[index] != null && !table[index].equals(DELETED)) {
            if (table[index].equals(key)) {
                System.err.println("Erro: Chave duplicada.");
                totalInsertTime += System.nanoTime() - startTime;
                return false;
            }
            if (attempt == 0) collisions++;
            attempt++;
            index = (index + 1) % capacity;
            if (index == initialIndex) {
                System.err.println("Erro: Loop completo na inserção.");
                totalInsertTime += System.nanoTime() - startTime;
                return false;
            }
        }

        table[index] = key;
        size++;
        totalInsertTime += System.nanoTime() - startTime;
        return true;
    }

    @Override
    boolean search(String key) {
        if (key == null || key.isEmpty()) return false;

        long startTime = System.nanoTime();
        int index = hash(key);
        int initialIndex = index;

        while (table[index] != null) {
            if (!table[index].equals(DELETED) && table[index].equals(key)) {
                totalSearchTime += System.nanoTime() - startTime;
                searchOperations++;
                return true;
            }
            index = (index + 1) % capacity;
            if (index == initialIndex) break;
        }

        totalSearchTime += System.nanoTime() - startTime;
        searchOperations++;
        return false;
    }

    @Override
    boolean remove(String key) {
        if (key == null || key.isEmpty()) return false;

        int index = hash(key);
        int initialIndex = index;

        while (table[index] != null) {
            if (table[index].equals(key)) {
                table[index] = DELETED;
                size--;
                return true;
            }
            index = (index + 1) % capacity;
            if (index == initialIndex) break;
        }

        System.err.println("Erro: Elemento não encontrado.");
        return false;
    }

    // Getters de estatísticas
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
            distribution[i] = (table[i] != null && !table[i].equals(DELETED)) ? 1 : 0;
        }
        return distribution;
    }

    public void printStatistics() {
        System.out.println("\n=== Estatísticas da Tabela Hash (Multiplicativa) ===");
        System.out.println("Capacidade: " + capacity);
        System.out.println("Itens inseridos: " + size);
        System.out.println("Colisões totais: " + collisions);
        System.out.printf("Tempo médio de inserção: %.3f ns\n", getAverageInsertTime());
        System.out.printf("Tempo médio de busca: %.3f ns\n", getAverageSearchTime());

        int[] distribution = getKeyDistribution();
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
