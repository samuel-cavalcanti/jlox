import java.util.List;

abstract class Expr {
        abstract <R> R accept(Visitor<R> v);

        interface Visitor<R> {
                R visitAssign(Assign assign);

                R visitBinary(Binary binary);

                R visitCall(Call call);

                R visitGet(Get get);

                R visitSet(Set set);

                R visitSuperExpr(SuperExpr superexpr);

                R visitGrouping(Grouping grouping);

                R visitLiteral(Literal literal);

                R visitLogical(Logical logical);

                R visitUnary(Unary unary);

                R visitThisExpr(ThisExpr thisexpr);

                R visitVariable(Variable variable);
        }

        static class Assign extends Expr {
                Assign(LoxToken name, Expr value) {
                        this.name = name;
                        this.value = value;
                }

                @Override
                <R> R accept(Visitor<R> v) {
                        return v.visitAssign(this);
                }

                final LoxToken name;
                final Expr value;
        }

        static class Binary extends Expr {
                Binary(Expr left, LoxToken operator, Expr right) {
                        this.left = left;
                        this.operator = operator;
                        this.right = right;
                }

                @Override
                <R> R accept(Visitor<R> v) {
                        return v.visitBinary(this);
                }

                final Expr left;
                final LoxToken operator;
                final Expr right;
        }

        static class Call extends Expr {
                Call(Expr callee, LoxToken paren, List<Expr> arguments) {
                        this.callee = callee;
                        this.paren = paren;
                        this.arguments = arguments;
                }

                @Override
                <R> R accept(Visitor<R> v) {
                        return v.visitCall(this);
                }

                final Expr callee;
                final LoxToken paren;
                final List<Expr> arguments;
        }

        static class Get extends Expr {
                Get(Expr object, LoxToken name) {
                        this.object = object;
                        this.name = name;
                }

                @Override
                <R> R accept(Visitor<R> v) {
                        return v.visitGet(this);
                }

                final Expr object;
                final LoxToken name;
        }

        static class Set extends Expr {
                Set(Expr object, LoxToken name, Expr value) {
                        this.object = object;
                        this.name = name;
                        this.value = value;
                }

                @Override
                <R> R accept(Visitor<R> v) {
                        return v.visitSet(this);
                }

                final Expr object;
                final LoxToken name;
                final Expr value;
        }

        static class SuperExpr extends Expr {
                SuperExpr(LoxToken keyword, LoxToken method) {
                        this.keyword = keyword;
                        this.method = method;
                }

                @Override
                <R> R accept(Visitor<R> v) {
                        return v.visitSuperExpr(this);
                }

                final LoxToken keyword;
                final LoxToken method;
        }

        static class Grouping extends Expr {
                Grouping(Expr expression) {
                        this.expression = expression;
                }

                @Override
                <R> R accept(Visitor<R> v) {
                        return v.visitGrouping(this);
                }

                final Expr expression;
        }

        static class Literal extends Expr {
                Literal(Object value) {
                        this.value = value;
                }

                @Override
                <R> R accept(Visitor<R> v) {
                        return v.visitLiteral(this);
                }

                final Object value;
        }

        static class Logical extends Expr {
                Logical(Expr left, LoxToken operator, Expr right) {
                        this.left = left;
                        this.operator = operator;
                        this.right = right;
                }

                @Override
                <R> R accept(Visitor<R> v) {
                        return v.visitLogical(this);
                }

                final Expr left;
                final LoxToken operator;
                final Expr right;
        }

        static class Unary extends Expr {
                Unary(LoxToken operator, Expr right) {
                        this.operator = operator;
                        this.right = right;
                }

                @Override
                <R> R accept(Visitor<R> v) {
                        return v.visitUnary(this);
                }

                final LoxToken operator;
                final Expr right;
        }

        static class ThisExpr extends Expr {
                ThisExpr(LoxToken keyword) {
                        this.keyword = keyword;
                }

                @Override
                <R> R accept(Visitor<R> v) {
                        return v.visitThisExpr(this);
                }

                final LoxToken keyword;
        }

        static class Variable extends Expr {
                Variable(LoxToken name) {
                        this.name = name;
                }

                @Override
                <R> R accept(Visitor<R> v) {
                        return v.visitVariable(this);
                }

                final LoxToken name;
        }
}
