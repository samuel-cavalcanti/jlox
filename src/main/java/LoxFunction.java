import java.util.List;

public class LoxFunction implements LoxCallable {
        private final Stmt.Function declaration;
        private final Environment closure;

        LoxFunction(Stmt.Function declaration, Environment e) {
                this.declaration = declaration;
                this.closure = e;

        }

        @Override
        public Object call(LoxInterpreter interpreter, List<Object> arguments) {
                Environment env = new Environment(this.closure);
                for (int i = 0; i < declaration.params.size(); i++) {
                        LoxToken argName = declaration.params.get(i);
                        Object argValue = arguments.get(i);
                        env.define(argName.lexeme, argValue);
                }

                List<Stmt> stmts = ((Stmt.Block) declaration.body).statements;

                try {
                        interpreter.runWithEnv(stmts, env);
                } catch (Return r) {
                        return r.value;
                }

                return null;
        }

        @Override
        public int arity() {
                return declaration.params.size();
        }

        @Override
        public String toString() {
                return "<fn " + declaration.name.lexeme + ">";
        }

}
