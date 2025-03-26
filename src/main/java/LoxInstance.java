import java.util.HashMap;
import java.util.Map;

public class LoxInstance {
        private final LoxClass loxClass;
        private final Map<String, Object> fields;

        LoxInstance(LoxClass lClass) {
                loxClass = lClass;
                fields = new HashMap<>();
        }

        @Override
        public String toString() {
                return loxClass.toString() + " instance";
        }

        Object get(LoxToken name) {

                if (fields.containsKey(name.lexeme))
                        return fields.get(name.lexeme);

                LoxFunction fun = loxClass.findMethod(name.lexeme);
                if (fun != null)
                        return fun.bind(this);

                throw new RuntimeError(name, String.format("Undefined property '%s.%s'", loxClass.name, name.lexeme));
        }

        void set(LoxToken name, Object value) {
                fields.put(name.lexeme, value);
        }

}
