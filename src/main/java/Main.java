import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {

        static String readFile(String path) {

                try {
                        return Files.readString(Path.of(path));

                } catch (IOException e) {
                        System.err.println("Error reading file: " + e.getMessage());
                        return "";
                }

        }

        public static void main(String[] args) {
                // You can use print statements as follows for debugging, they'll be visible
                // when running tests.
                System.err.println("Logs from your program will appear here!");

                if (args.length < 2) {
                        System.err.println("Usage: ./your_program.sh tokenize <filename>");
                        System.exit(1);
                }

                String command = args[0];
                String filename = args[1];

                if (!command.equals("tokenize")) {
                        System.err.println("Unknown command: " + command);
                        System.exit(1);
                }

                String fileContents = readFile(filename);

                Lox engine = new Lox();
                engine.run(fileContents);

                if (Lox.hadError) {
                        System.exit(65);
                }

        }
}
