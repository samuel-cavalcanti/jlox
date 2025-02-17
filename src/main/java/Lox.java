import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Lox {

        static boolean hadError = false;

        public void runFile(String path) throws IOException {
                String fileContents = Files.readString(Path.of(path));
                hadError = false;
                run(fileContents);
        }

        public void run(String source) {

                LoxScanner scanner = new LoxScanner(source);

                List<LoxToken> tokens = scanner.scanTokens();

                for (LoxToken token : tokens) {

                        System.out.println(token);

                }

        }

        public static void error(int line, String where, String message) {

                System.err.printf("[line %i] Error%s: %s", line, where, message);
                hadError = true;
        }
}
