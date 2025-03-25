import java.util.List;

public class Lox {

        static boolean hadError = false;
        static boolean hadRuntimeError = false;
        private static final LoxInterpreter interpreter = new LoxInterpreter();

        public Expr parse(String source) {

                List<LoxToken> tokens = scan(source);

                LoxParser parser = new LoxParser(tokens);
                Expr expression = parser.parse();

                if (hadError)
                        return null;

                return expression;
        }

        public List<LoxToken> scan(String source) {
                hadError = false;

                LoxScanner scanner = new LoxScanner(source);

                return scanner.scanTokens();
        }

        public String interpret(String source) {

                Expr expr = parse(source);
                if (expr == null)
                        return "";
                return interpreter.interpret(expr);
        }

        public String run(String source) {

                List<LoxToken> tokens = scan(source);

                LoxParser parser = new LoxParser(tokens);
                List<Stmt> statements = parser.parseStatement();
                Resolver resolver = new Resolver(interpreter);
                resolver.resolve(statements);
                if (hadError)
                        return "";

                return interpreter.run(statements);

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

        public static void runtimeError(RuntimeError e) {
                System.err.printf("%s\n[line %s]", e.getMessage(), e.token.line);
                hadRuntimeError = true;

        }
}
