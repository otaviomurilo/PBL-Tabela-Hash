import java.util.Collections;
import java.util.List;
import utils.FileReader;

public class Main {
    public static void main(String[] args) {
        try {
            List<String> names = FileReader.readNames("resources/female_names.txt");
            Collections.shuffle(names); // Embaralhar a lista

            HashTablePolynomial table = new HashTablePolynomial(32);


            System.out.println("=== Inserindo Nomes ===");
            int insertCount = Math.min(32, names.size()); //variavel que define quantos nomes serao inseridos na lista
            for (int i = 0; i < insertCount; i++) {
                String name = names.get(i);
                boolean inserted = table.insert(name);
                System.out.printf("Inserindo %s: %s\n", name, inserted ? "Sucesso" : "Falha");
            }

            table.printStatistics();

        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
        }
    }
}
