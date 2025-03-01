import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Lox {

        static boolean hadError = false;

        public Expr parse(String source) {
                hadError = false;

                LoxScanner scanner = new LoxScanner(source);

                List<LoxToken> tokens = scanner.scanTokens();

                LoxParser parser = new LoxParser(tokens);
                Expr expression = parser.parse();

                if (hadError)
                        return null;


                return expression;
        }

        public List<LoxToken> tokens(String source) {
                hadError = false;

                LoxScanner scanner = new LoxScanner(source);

                return scanner.scanTokens();
        }

        public static void error(int line, String where, String message) {

                System.err.printf("[line %s] Error%s: %s\n", line, where, message);
                hadError = true;
        }

        public static void error(LoxToken t, String msg) {
                if (t.type == TokenType.EOF) {
                        error(t.line, " at end", msg);
                } else {
                        error(t.line, " at '" + t.lexeme + "'", msg);
                }
                hadError = true;

        }
}
