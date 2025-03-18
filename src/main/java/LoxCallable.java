import java.util.List;

interface LoxCallable {
        Object call(LoxInterpreter interpreter, List<Object> arguments);

}
