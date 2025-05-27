import java.util.Collections;
import java.util.List;
import utils.FileReader;

public class Main {
    public static void main(String[] args) {
        try {
            List<String> names = FileReader.readNames("resources/female_names.txt");
            Collections.shuffle(names);
            
            HashTablePolynomial tablePolynomial = new HashTablePolynomial(32);
            HashTableMultiplier tableMulti = new HashTableMultiplier(32);

            System.out.println("Inserindo 40 nomes aleatórios na table");
            for (int i = 0; i < 40; i++) {
                tablePolynomial.insert(names.get(i));
                tableMulti.insert(names.get(i));
            }


            //testes das operações insert, search e remove
            String target = "Alice";
            System.out.println("Inserção direta do nome: " + target);
            System.out.println("Inserção Manual: Polynomial=" + tablePolynomial.insert(target) 
            + ", Multiplier=" + tableMulti.insert(target));
            System.out.println("Busca inicial: Polynomial=" + tablePolynomial.search(target) 
                             + ", Multiplier=" + tableMulti.search(target));
            
            System.out.println("Removendo nome existente: Polynomial=" + tablePolynomial.remove(target) 
                             + ", Multiplier=" + tableMulti.remove(target));
            
            System.out.println("Busca pós-remoção: Polynomial=" + tablePolynomial.search(target) 
                             + ", Multiplier=" + tableMulti.search(target));
                

            System.out.println("\n===== Resultados =====");
            tablePolynomial.printStatistics();
            tableMulti.printStatistics();

        } catch (Exception e) {
            System.err.println("Erro durante a execução: " + e.getMessage());
            e.printStackTrace();
        }
    }
}