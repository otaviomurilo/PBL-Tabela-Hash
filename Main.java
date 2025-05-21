import java.util.List;

import utils.FileReader;

public class Main {
    public static void main(String[] args) {
        try {
            List<String> names = FileReader.readNames("resources/female_names.txt");

            HashTablePolynomial table = new HashTablePolynomial(32);
            
            for (String name : names) { //inserção
                table.insert(name);
            }
            

            for (int i = 0; i < 10; i++) { //busca
                String name = names.get(i);
                table.search(name);
                table.search("NOME_INEXISTENTE_" + i);
            }

            table.printStatistics(); //relatório console
            
        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
        }
    }
}