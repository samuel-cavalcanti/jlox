class AstPrinter implements Expr.Visitor<String> {

        String print(Expr e) {
                return e.accept(this);
        }

        @Override
        public String visitBinary(Expr.Binary binary) {

                return parenthesize(binary.operator.lexeme, binary.left, binary.right);
        }

        @Override
        public String visitGrouping(Expr.Grouping grouping) {
                return parenthesize("group", grouping.expression);
        }

        @Override
        public String visitLiteral(Expr.Literal literal) {
                return literal.value == null ? "nil" : literal.value.toString();
        }

        @Override
        public String visitUnary(Expr.Unary unary) {
                return parenthesize(unary.operator.lexeme, unary.right);
        }

        private String parenthesize(String name, Expr... exprs) {
                StringBuilder builder = new StringBuilder();

                builder.append("(").append(name);
                for (Expr expr : exprs) {
                        builder.append(" ");
                        builder.append(expr.accept(this));
                }
                builder.append(")");

                return builder.toString();
        }

}
