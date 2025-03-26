import java.util.List;
import java.util.Map;

public class LoxClass implements LoxCallable {

        final String name;
        final Map<String, LoxFunction> methods;

        LoxClass(String name, Map<String, LoxFunction> methods) {
                this.name = name;
                this.methods = methods;
        }

        @Override
        public String toString() {
                return name;
        }

        @Override
        public Object call(LoxInterpreter interpreter, List<Object> arguments) {

                LoxFunction init = findMethod("init");
                LoxInstance instance = new LoxInstance(this);
                if (init != null)
                        init.bind(instance).call(interpreter, arguments);

                return instance;
        }

        public LoxFunction findMethod(String method) {

                if (methods.containsKey(method))
                        return methods.get(method);

                return null;
        }

        @Override
        public int arity() {

                LoxFunction init = findMethod("init");
                if (init == null)
                        return 0;
                else
                        return init.arity();
        }
}
