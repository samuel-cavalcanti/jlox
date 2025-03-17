import java.util.HashMap;
import java.util.Map;

public class Environment {
        private final Map<String, Object> values = new HashMap<>();
        final Environment enclosing;

        Environment() {
                enclosing = null;
        }

        Environment(Environment e) {
                enclosing = e;
        }

        void define(String name, Object value) {
                values.put(name, value);
        }

        Object get(LoxToken name) {

                if (values.containsKey(name.lexeme)) {
                        return values.get(name.lexeme);
                }

                if (enclosing != null)
                        return enclosing.get(name);

                throw new RuntimeError(name, undefinedVariableMsg(name.lexeme));

        }

        void assign(LoxToken name, Object value) {

                if (values.containsKey(name.lexeme)) {
                        values.put(name.lexeme, value);

                } else if (enclosing != null) {
                        enclosing.assign(name, value);
                } else {
                        throw new RuntimeError(name, undefinedVariableMsg(name.lexeme));
                }

        }

        private String undefinedVariableMsg(String varName) {
                return "Undefined variable '" + varName + "'";
        }
}
