import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class Resolver implements Expr.Visitor<Void>, Stmt.Visitor<Void> {

        private final LoxInterpreter interpreter;
        private final Stack<Map<String, Boolean>> scopes = new Stack<>();

        Resolver(LoxInterpreter i) {
                interpreter = i;
        }

        void resolve(List<Stmt> statements) {

                for (Stmt s : statements)
                        resolve(s);

        }

        void resolve(Expr e) {
                if (e == null)
                        return;
                e.accept(this);

        }

        void resolve(Stmt s) {
                if (s == null)
                        return;

                s.accept(this);
        }

        void beginScope() {
                scopes.push(new HashMap<>());

        }

        void endScope() {
                scopes.pop();
        }

        private void declare(LoxToken name) {
                if (scopes.isEmpty())
                        return;

                Map<String, Boolean> scope = scopes.peek();

                if (scope.containsKey(name.lexeme))
                        Lox.error(name, "Already a variable with this name in this scope.");
                scope.put(name.lexeme, false);
        }

        private void define(LoxToken name) {
                if (scopes.isEmpty())
                        return;
                scopes.peek().put(name.lexeme, true);
        }

        @Override
        public Void visitExpression(Stmt.Expression stmt) {
                resolve(stmt.expression);
                return null;
        }

        @Override
        public Void visitBlock(Stmt.Block block) {
                beginScope();
                resolve(block.statements);
                endScope();
                return null;
        }

        @Override
        public Void visitPrint(Stmt.Print print) {
                resolve(print.expression);
                return null;
        }

        @Override
        public Void visitIfStmt(Stmt.IfStmt stmt) {
                resolve(stmt.expression);
                resolve(stmt.thenBranch);
                if (stmt.elseBranch != null)
                        resolve(stmt.elseBranch);
                return null;
        }

        @Override
        public Void visitFunction(Stmt.Function stmt) {
                declare(stmt.name);
                define(stmt.name);

                resolveFunction(stmt);
                return null;
        }

        private void resolveFunction(Stmt.Function function) {
                beginScope();
                for (LoxToken param : function.params) {
                        declare(param);
                        define(param);
                }
                resolve(((Stmt.Block) function.body).statements);
                endScope();
        }

        @Override
        public Void visitWhileStmt(Stmt.WhileStmt whilestmt) {
                resolve(whilestmt.condition);
                resolve(whilestmt.body);
                return null;
        }

        @Override
        public Void visitVar(Stmt.Var stmt) {
                if (stmt.initializer != null) {
                        resolve(stmt.initializer);
                }
                declare(stmt.name);
                define(stmt.name);
                return null;
        }

        @Override
        public Void visitReturnStmt(Stmt.ReturnStmt returnstmt) {
                if (returnstmt.value != null)
                        resolve(returnstmt.value);

                return null;
        }

        @Override
        public Void visitAssign(Expr.Assign expr) {
                resolve(expr.value);
                resolveLocal(expr, expr.name);
                return null;
        }

        @Override
        public Void visitBinary(Expr.Binary binary) {
                resolve(binary.left);
                resolve(binary.right);
                return null;
        }

        @Override
        public Void visitCall(Expr.Call call) {
                resolve(call.callee);
                for (Expr e : call.arguments)
                        resolve(e);
                return null;
        }

        @Override
        public Void visitGrouping(Expr.Grouping grouping) {
                resolve(grouping.expression);
                return null;
        }

        @Override
        public Void visitLiteral(Expr.Literal literal) {
                return null;
        }

        @Override
        public Void visitLogical(Expr.Logical logical) {
                resolve(logical.left);
                resolve(logical.right);
                return null;
        }

        @Override
        public Void visitUnary(Expr.Unary unary) {
                resolve(unary.right);
                return null;
        }

        @Override
        public Void visitVariable(Expr.Variable expr) {
                if (!scopes.isEmpty() &&
                                scopes.peek().get(expr.name.lexeme) == Boolean.FALSE) {
                        Lox.error(expr.name,
                                        "Can't read local variable in its own initializer.");
                }

                resolveLocal(expr, expr.name);
                return null;
        }

        private void resolveLocal(Expr expr, LoxToken name) {
                int lastIndex = scopes.size() - 1;
                for (int i = lastIndex; i >= 0; i--) {
                        if (scopes.get(i).containsKey(name.lexeme)) {
                                interpreter.resolve(expr, lastIndex - i);
                                return;
                        }
                }
        }

}
