import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class Resolver implements Expr.Visitor<Void>, Stmt.Visitor<Void> {

        private final LoxInterpreter interpreter;
        private final Stack<Map<String, Boolean>> scopes = new Stack<>();

        private enum FunctionType {
                NONE,
                FUNCTION,
                METHOD,
                INITIALIZER
        }

        private enum ClassType {
                NONE,
                CLASS,
                SUBCLASS,
        }

        private FunctionType currentFunction = FunctionType.NONE;
        private ClassType currentClass = ClassType.NONE;

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

                resolveFunction(stmt, FunctionType.FUNCTION);
                return null;
        }

        private void resolveFunction(Stmt.Function function, FunctionType type) {
                beginScope();
                FunctionType temp = currentFunction;
                currentFunction = type;
                for (LoxToken param : function.params) {
                        declare(param);
                        define(param);
                }
                resolve(((Stmt.Block) function.body).statements);
                currentFunction = temp;
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
                declare(stmt.name);
                if (stmt.initializer != null) {
                        resolve(stmt.initializer);
                }
                define(stmt.name);
                return null;
        }

        @Override
        public Void visitReturnStmt(Stmt.ReturnStmt returnstmt) {
                if (currentFunction == Resolver.FunctionType.NONE)
                        Lox.error(returnstmt.keyword, "Can't return from top-level code.");
                if (returnstmt.value != null) {
                        if (currentFunction == Resolver.FunctionType.INITIALIZER)
                                Lox.error(returnstmt.keyword, "Can't return value from an initializer.");

                        resolve(returnstmt.value);
                }

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

        @Override
        public Void visitClassStmt(Stmt.ClassStmt classstmt) {
                declare(classstmt.name);
                define(classstmt.name);
                if (classstmt.superclass != null) {
                        if (classstmt.superclass.name.lexeme.equals(classstmt.name.lexeme))
                                Lox.error(classstmt.name, String.format("A class can't inherit from itself: '%s < %s'",
                                                classstmt.name.lexeme, classstmt.superclass.name.lexeme));
                        currentClass = ClassType.SUBCLASS;
                        resolve(classstmt.superclass);
                }
                ClassType enclosingClass = currentClass;
                if (classstmt.superclass == null)
                        currentClass = ClassType.CLASS;
                if (classstmt.superclass != null) {
                        beginScope();
                        scopes.peek().put("super", true);
                }

                beginScope();
                scopes.peek().put("this", true);
                for (Stmt.Function method : classstmt.methods) {
                        FunctionType t = method.name.lexeme.equals("init") ? FunctionType.INITIALIZER
                                        : FunctionType.METHOD;
                        resolveFunction(method, t);
                }
                endScope();
                if (classstmt.superclass != null)
                        endScope();
                currentClass = enclosingClass;
                return null;
        }

        @Override
        public Void visitGet(Expr.Get get) {
                resolve(get.object);

                return null;
        }

        @Override
        public Void visitSet(Expr.Set set) {
                resolve(set.value);
                resolve(set.object);
                return null;
        }

        @Override
        public Void visitThisExpr(Expr.ThisExpr thisexpr) {
                if (currentClass == ClassType.NONE)
                        Lox.error(thisexpr.keyword, "Can't use 'this' outside of a class.");
                resolveLocal(thisexpr, thisexpr.keyword);
                return null;
        }

        @Override
        public Void visitSuperExpr(Expr.SuperExpr superexpr) {
                if (currentClass == ClassType.NONE)
                        Lox.error(superexpr.keyword, "Can't use 'super' outside of a class");
                if (currentClass != ClassType.SUBCLASS)
                        Lox.error(superexpr.keyword, "Can't use 'super' with no superclass");
                resolveLocal(superexpr, superexpr.keyword);
                return null;
        }

}
