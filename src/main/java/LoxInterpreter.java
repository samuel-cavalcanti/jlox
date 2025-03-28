import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoxInterpreter implements Expr.Visitor<Object>, Stmt.Visitor<String> {
        final Environment globals = new Environment();
        private final Map<Expr, Integer> locals = new HashMap<>();

        Environment environment = globals;

        LoxInterpreter() {

                globals.define("clock", new LoxCallable() {

                        @Override
                        public Object call(LoxInterpreter interpreter, List<Object> arguments) {
                                return (double) System.currentTimeMillis() / 1000.0;

                        }

                        @Override
                        public int arity() {
                                return 0;
                        }

                        @Override
                        public String toString() {
                                return "<native fn>";
                        }

                });

        }

        void resolve(Expr expr, int depth) {
                locals.put(expr, depth);
        }

        String interpret(Expr expression) {
                try {
                        Object value = evaluate(expression);
                        return stringify(value);
                } catch (RuntimeError e) {
                        Lox.runtimeError(e);
                        return "";
                }

        }

        String run(List<Stmt> statements) {
                return executeStmts(statements);
        }

        private String execute(Stmt e) {
                if (e == null)
                        return "";
                String out = e.accept(this);
                if (out.endsWith("\n"))
                        return out;
                else
                        return out + "\n";
        }

        private String executeStmts(List<Stmt> stmts) {
                String output = "";

                try {

                        for (Stmt e : stmts) {
                                output += execute(e);
                        }

                } catch (RuntimeError e) {
                        Lox.runtimeError(e);
                }

                return output;

        }

        private String stringify(Object value) {
                if (value == null)
                        return "nil";
                if (value instanceof Double) {
                        String text = value.toString();
                        if (text.endsWith(".0")) {
                                return text.substring(0, text.length() - 2);

                        }
                        return text;

                }

                return value.toString();

        }

        @Override
        public Object visitBinary(Expr.Binary binary) {

                Object left = evaluate(binary.left);
                Object right = evaluate(binary.right);

                switch (binary.operator.type) {
                        case TokenType.MINUS:
                                checkNumberOperant(binary.operator, left, right);
                                return (double) left - (double) right;
                        case TokenType.SLASH:
                                checkNumberOperant(binary.operator, left, right);
                                return (double) left / (double) right;
                        case TokenType.STAR:
                                checkNumberOperant(binary.operator, left, right);
                                return (double) left * (double) right;
                        case TokenType.GREATER:
                                checkNumberOperant(binary.operator, left, right);
                                return (double) left > (double) right;
                        case TokenType.GREATER_EQUAL:
                                checkNumberOperant(binary.operator, left, right);
                                return (double) left >= (double) right;
                        case TokenType.LESS:
                                checkNumberOperant(binary.operator, left, right);
                                return (double) left < (double) right;
                        case TokenType.LESS_EQUAL:
                                checkNumberOperant(binary.operator, left, right);
                                return (double) left <= (double) right;
                        case TokenType.EQUAL_EQUAL:
                                return isEqual(left, right);
                        case TokenType.BANG_EQUAL:
                                return !isEqual(left, right);

                        case TokenType.PLUS:
                                if (left instanceof Double && right instanceof Double)
                                        return (double) left + (double) right;

                                if (left instanceof String && right instanceof String)
                                        return (String) left + (String) right;
                                if (left instanceof String && right instanceof Double)
                                        return (String) left + String.valueOf(right);
                                throw new RuntimeError(binary.operator,
                                                "Operators must be two numbers or two strings");

                        default:
                                throw new UnsupportedOperationException(
                                                "Unreachable Binary operator Right: " + right + "  left: " + left
                                                                + " operator: " + binary.operator);

                }

        }

        private boolean isEqual(Object a, Object b) {

                boolean aIsNull = a == null;
                boolean bIsNull = b == null;

                if (aIsNull && bIsNull)
                        return true;
                if (aIsNull)
                        return false;

                return a.equals(b);

        }

        @Override
        public Object visitGrouping(Expr.Grouping grouping) {
                return evaluate(grouping.expression);
        }

        @Override
        public Object visitLiteral(Expr.Literal literal) {
                return literal.value;
        }

        @Override
        public Object visitUnary(Expr.Unary unary) {
                Object right = evaluate(unary.right);

                switch (unary.operator.type) {

                        case TokenType.MINUS:
                                checkNumberOperant(unary.operator, right);
                                return -(double) right;
                        case TokenType.BANG:
                                return !(boolean) isTruthy(right);

                        default:
                                throw new UnsupportedOperationException(
                                                "Unreachable Unary operator Right: " + right + " operator: "
                                                                + unary.operator);

                }
        }

        private Object evaluate(Expr e) {
                return e.accept(this);
        }

        private boolean isTruthy(Object o) {
                if (o == null)
                        return false;
                if (o instanceof Boolean)
                        return (boolean) o;

                return true;

        }

        private void checkNumberOperant(LoxToken operator, Object... operands) {

                for (Object o : operands) {

                        if (!(o instanceof Double))
                                throw new RuntimeError(operator, "Operant must be a number");

                }

        }

        @Override
        public String visitExpression(Stmt.Expression expression) {
                Object value = evaluate(expression.expression);
                return stringify(value);
        }

        @Override
        public String visitPrint(Stmt.Print print) {
                Object value = evaluate(print.expression);
                String output = stringify(value);
                System.out.println(output);

                return output;
        }

        @Override
        public String visitVar(Stmt.Var var) {
                Object value = null;
                if (var.initializer != null)
                        value = evaluate(var.initializer);

                environment.define(var.name.lexeme, value);
                return stringify(null);
        }

        @Override
        public Object visitVariable(Expr.Variable variable) {
                return lookUpVariable(variable.name, variable);
        }

        private Object lookUpVariable(LoxToken name, Expr expr) {
                Integer distance = locals.get(expr);
                if (distance != null) {
                        return environment.getAt(distance, name.lexeme);
                } else {
                        return globals.get(name);
                }
        }

        @Override
        public Object visitAssign(Expr.Assign assign) {

                Object value = evaluate(assign.value);
                environment.assign(assign.name, value);
                Integer distance = locals.get(assign);
                if (distance != null) {
                        environment.assignAt(distance, assign.name, value);
                } else {
                        globals.assign(assign.name, value);
                }

                return value;

        }

        @Override
        public String visitBlock(Stmt.Block block) {

                Environment env = new Environment(this.environment);

                return runWithEnv(block.statements, env);

        }

        String runWithEnv(List<Stmt> statements, Environment env) {
                Environment previus = this.environment;
                this.environment = env;
                String output = "";

                try {
                        output = executeStmts(statements);
                } finally {
                        this.environment = previus;
                }

                return output;

        }

        @Override
        public String visitIfStmt(Stmt.IfStmt ifstmt) {
                Object value = evaluate(ifstmt.expression);
                if (isTruthy(value)) {
                        return execute(ifstmt.thenBranch);
                } else {
                        if (ifstmt.elseBranch != null) {
                                return execute(ifstmt.elseBranch);
                        } else {
                                return "";
                        }

                }

        }

        @Override
        public Object visitLogical(Expr.Logical logical) {
                Object left = evaluate(logical.left);

                boolean isLeftTrue = isTruthy(left);

                if (logical.operator.type == TokenType.OR && isLeftTrue)
                        return left;

                if (logical.operator.type == TokenType.AND && !isLeftTrue)
                        return left;

                return evaluate(logical.right);

        }

        @Override
        public String visitWhileStmt(Stmt.WhileStmt whilestmt) {

                String output = "";
                while (isTruthy(evaluate(whilestmt.condition))) {
                        output += execute(whilestmt.body);

                }
                return output;
        }

        @Override
        public Object visitCall(Expr.Call call) {
                Object callee = evaluate(call.callee);

                if (!(callee instanceof LoxCallable))
                        throw new RuntimeError(call.paren, "Can only call function and classes");

                LoxCallable function = (LoxCallable) callee;

                if (function.arity() != call.arguments.size())
                        throw new RuntimeError(call.paren,
                                        function.toString() + " Expected " + function.arity()
                                                        + " arguments but got " + call.arguments.size());

                List<Object> args = new ArrayList<>(call.arguments.size());
                for (Expr e : call.arguments)
                        args.add(evaluate(e));

                return function.call(this, args);

        }

        @Override
        public String visitFunction(Stmt.Function function) {
                LoxFunction fun = new LoxFunction(function, this.environment, false);

                environment.define(function.name.lexeme, fun);
                return fun.toString();
        }

        @Override
        public String visitReturnStmt(Stmt.ReturnStmt returnstmt) {
                Object value = returnstmt.value == null ? null : evaluate(returnstmt.value);
                throw new Return(value);
        }

        @Override
        public String visitClassStmt(Stmt.ClassStmt classstmt) {
                environment.define(classstmt.name.lexeme, null);
                LoxClass superClass = evaluateSuperClass(classstmt.superclass);
                if (superClass != null) {
                        environment = new Environment(environment);
                        environment.define("super", superClass);
                }

                Map<String, LoxFunction> methods = new HashMap<>();
                for (Stmt.Function method : classstmt.methods) {
                        LoxFunction fun = new LoxFunction(method, environment, method.name.lexeme.equals("init"));
                        methods.put(method.name.lexeme, fun);
                }

                LoxClass klass = new LoxClass(classstmt.name.lexeme, superClass, methods);
                if (superClass != null) {
                        environment = environment.enclosing;
                }
                environment.assign(classstmt.name, klass);

                return classstmt.name.lexeme;
        }

        private LoxClass evaluateSuperClass(Expr.Variable superclass) {
                if (superclass == null)
                        return null;

                Object superClass = evaluate(superclass);
                if (!(superClass instanceof LoxClass))
                        throw new RuntimeError(superclass.name, String.format(
                                        "super class %s must be a class", superclass.name.lexeme));
                return (LoxClass) superClass;
        }

        @Override
        public Object visitGet(Expr.Get get) {
                Object obj = evaluate(get.object);
                if (obj instanceof LoxInstance) {
                        return ((LoxInstance) obj).get(get.name);
                }

                throw new RuntimeError(get.name, "Only instances have properties");
        }

        @Override
        public Object visitSet(Expr.Set set) {
                Object obj = evaluate(set.object);
                if (!(obj instanceof LoxInstance)) {
                        throw new RuntimeError(set.name, "Only instances have fields");
                }
                Object value = evaluate(set.value);
                LoxInstance i = (LoxInstance) obj;
                i.set(set.name, value);

                return value;

        }

        @Override
        public Object visitThisExpr(Expr.ThisExpr thisexpr) {
                return lookUpVariable(thisexpr.keyword, thisexpr);
        }

        @Override
        public Object visitSuperExpr(Expr.SuperExpr superexpr) {
                int distance = locals.get(superexpr);
                LoxClass superclass = (LoxClass) environment.getAt(
                                distance, "super");

                LoxInstance obj = (LoxInstance) environment.getAt(distance - 1, "this");
                LoxFunction method = superclass.findMethod(superexpr.method.lexeme);
                if (method == null)
                        throw new RuntimeError(superexpr.method, String.format("Undefined method: %s in the class %s",
                                        superexpr.method.lexeme, superclass.name));

                return method.bind(obj);
        }

}
