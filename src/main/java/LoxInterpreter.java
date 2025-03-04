public class LoxInterpreter implements Expr.Visitor<Object> {

        String interpret(Expr expression) {
                try {
                        Object value = evaluate(expression);
                        return stringify(value);
                } catch (RuntimeError e) {
                        Lox.runtimeError(e);
                        return "";
                }

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

}
