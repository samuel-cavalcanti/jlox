abstract class Stmt {
        abstract <R> R accept(Visitor<R> v);

        interface Visitor<R> {
                R visitExpression(Expression expression);

                R visitPrint(Print print);
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
}
