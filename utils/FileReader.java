package utils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class FileReader {
    public static List<String> readNames(String filename) throws IOException {
        return Files.readAllLines(Paths.get(filename));
    }
}