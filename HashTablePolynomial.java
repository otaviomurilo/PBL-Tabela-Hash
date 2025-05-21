public class HashTablePolynomial extends HashTable {
    private long totalInsertTime;
    private long totalSearchTime;
    private int collisions;
    private int searchOperations;

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
        
        for (int i = 0; i < key.length(); i++) {                        
            hash = (hash + (key.charAt(i) * power)) % capacity;  // valor hash += (caractere * potencia atual) % capacidade (garante que fique dentro da capacidade da tabela)
            power = (power * p) % capacity;  //atualiza a potencia e aplica o modulo da capacidade para evitar overflow
        }
        return Math.abs(hash); //evita indice negativo
    }

    @Override
    boolean insert(String name) {
        long startTime = System.nanoTime(); //inicia a contagem do tempo de execução
        
        if (size >= capacity) { //verifica se a tabela está cheia
            totalInsertTime += System.nanoTime() - startTime;
            return false; //falha
        }
    
        int index = hash(name); //calcula a posição inicial
        int initIndex = index; //guarda a posição inicial --> capaz de detectar um loop completo
        int attempt = 0;
    
        while (table[index] != null) { //tratamento com endereçamento aberto
            if (table[index].equals(name)) { 
                totalInsertTime += System.nanoTime() - startTime;
                return false; //chave duplicada
            }
    
            if (attempt == 0) collisions++;  // Contabiliza colisão apenas na primeira sondagem
    
            attempt++; //incrementa tentativa e calcula uma nova posição (sondagem linear)
            index = (index + 1) % capacity;
    
            if (index == initIndex) { // volta completa na tabela
                totalInsertTime += System.nanoTime() - startTime;
                return false; //tabela cheia
            }
        }
    
        table[index] = name; //elemento inserido na posição encontrada
        size++;
        totalInsertTime += System.nanoTime() - startTime;
        return true;
    }

    @Override
    boolean search(String name) {
        long startTime = System.nanoTime();
        boolean found = false;
        
        int index = hash(name);
        int initIndex = index;
        int attempt = 0;
    
        while (table[index] != null) {
            if (table[index].equals(name)) {
                found = true; //encontrou a chave
                break;
            }
    
            attempt++;
            index = (index + 1) % capacity;
    
            if (index == initIndex) break; //volta completa na tabela --> elemento não encontrado
        }
    
        totalSearchTime += System.nanoTime() - startTime;
        searchOperations++;
        return found;
    }

    @Override
    boolean remove(String name) { //a fazer
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