import java.lang.RuntimeException;

public class RuntimeError extends RuntimeException {
        final LoxToken token;

        RuntimeError(LoxToken token, String msg) {
                super(msg);
                this.token = token;
        }

}
