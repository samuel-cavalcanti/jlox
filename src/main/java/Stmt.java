import java.util.List;

abstract class Stmt {
        abstract <R> R accept(Visitor<R> v);

        interface Visitor<R> {
                R visitExpression(Expression expression);

                R visitBlock(Block block);

                R visitClassStmt(ClassStmt classstmt);

                R visitPrint(Print print);

                R visitIfStmt(IfStmt ifstmt);

                R visitFunction(Function function);

                R visitWhileStmt(WhileStmt whilestmt);

                R visitVar(Var var);

                R visitReturnStmt(ReturnStmt returnstmt);
        }

        static class Expression extends Stmt {
                Expression(Expr expression) {
                        this.expression = expression;
                }

                @Override
                <R> R accept(Visitor<R> v) {
                        return v.visitExpression(this);
                }

                final Expr expression;
        }

        static class Block extends Stmt {
                Block(List<Stmt> statements) {
                        this.statements = statements;
                }

                @Override
                <R> R accept(Visitor<R> v) {
                        return v.visitBlock(this);
                }

                final List<Stmt> statements;
        }

        static class ClassStmt extends Stmt {
                ClassStmt(LoxToken name, List<Stmt.Function> methods) {
                        this.name = name;
                        this.methods = methods;
                }

                @Override
                <R> R accept(Visitor<R> v) {
                        return v.visitClassStmt(this);
                }

                final LoxToken name;
                final List<Stmt.Function> methods;
        }

        static class Print extends Stmt {
                Print(Expr expression) {
                        this.expression = expression;
                }

                @Override
                <R> R accept(Visitor<R> v) {
                        return v.visitPrint(this);
                }

                final Expr expression;
        }

        static class IfStmt extends Stmt {
                IfStmt(Expr expression, Stmt thenBranch, Stmt elseBranch) {
                        this.expression = expression;
                        this.thenBranch = thenBranch;
                        this.elseBranch = elseBranch;
                }

                @Override
                <R> R accept(Visitor<R> v) {
                        return v.visitIfStmt(this);
                }

                final Expr expression;
                final Stmt thenBranch;
                final Stmt elseBranch;
        }

        static class Function extends Stmt {
                Function(LoxToken name, List<LoxToken> params, Stmt body) {
                        this.name = name;
                        this.params = params;
                        this.body = body;
                }

                @Override
                <R> R accept(Visitor<R> v) {
                        return v.visitFunction(this);
                }

                final LoxToken name;
                final List<LoxToken> params;
                final Stmt body;
        }

        static class WhileStmt extends Stmt {
                WhileStmt(Expr condition, Stmt body) {
                        this.condition = condition;
                        this.body = body;
                }

                @Override
                <R> R accept(Visitor<R> v) {
                        return v.visitWhileStmt(this);
                }

                final Expr condition;
                final Stmt body;
        }

        static class Var extends Stmt {
                Var(LoxToken name, Expr initializer) {
                        this.name = name;
                        this.initializer = initializer;
                }

                @Override
                <R> R accept(Visitor<R> v) {
                        return v.visitVar(this);
                }

                final LoxToken name;
                final Expr initializer;
        }

        static class ReturnStmt extends Stmt {
                ReturnStmt(LoxToken keyword, Expr value) {
                        this.keyword = keyword;
                        this.value = value;
                }

                @Override
                <R> R accept(Visitor<R> v) {
                        return v.visitReturnStmt(this);
                }

                final LoxToken keyword;
                final Expr value;
        }
}
