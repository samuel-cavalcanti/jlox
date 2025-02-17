import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Lox {

        public void runFile(String path) throws IOException {
                String fileContents = Files.readString(Path.of(path));
        }

        public void run(String source) {

                LoxScanner scanner = new LoxScanner(source);

                List<LoxToken> tokens = scanner.scanTokens();

                for (LoxToken token : tokens) {

                        System.out.println(token);

                }

        }

        private void report(int line, String where, String message) {

                System.err.printf("[line %i] Error %s: %s", line, where, message);
        }
}
