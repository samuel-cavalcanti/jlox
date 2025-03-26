import java.util.List;

public class LoxFunction implements LoxCallable {
        private final Stmt.Function declaration;
        private final Environment closure;
        private final boolean isInitializer;

        LoxFunction(Stmt.Function declaration, Environment e, boolean isInitializer) {
                this.declaration = declaration;
                this.closure = e;
                this.isInitializer = isInitializer;

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
                        if (isInitializer)
                                return closure.getAt(0, "this");

                        return r.value;
                }
                if (isInitializer)
                        return closure.getAt(0, "this");

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

        public LoxFunction bind(LoxInstance instance) {
                Environment environment = new Environment(closure);
                environment.define("this", instance);

                return new LoxFunction(declaration, environment, false);
        }

}
