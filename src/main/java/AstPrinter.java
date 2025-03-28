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

        @Override
        public String visitVariable(Expr.Variable variable) {
                return variable.name.lexeme;
        }

        @Override
        public String visitAssign(Expr.Assign assign) {
                return assign.name.lexeme;
        }

        @Override
        public String visitLogical(Expr.Logical logical) {
                return logical.operator.lexeme + " " + logical.left.accept(this) + " " + logical.right.accept(this);
        }

        @Override
        public String visitCall(Expr.Call call) {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'visitCall'");
        }

        @Override
        public String visitGet(Expr.Get get) {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'visitGet'");
        }

        @Override
        public String visitSet(Expr.Set set) {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'visitSet'");
        }

        @Override
        public String visitThisExpr(Expr.ThisExpr thisexpr) {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'visitThisExpr'");
        }

        @Override
        public String visitSuperExpr(Expr.SuperExpr superexpr) {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'visitSuperExpr'");
        }

}
