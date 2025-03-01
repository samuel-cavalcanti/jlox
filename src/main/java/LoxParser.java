import java.util.List;

public class LoxParser {

        private static class ParseError extends RuntimeException {
        }

        private final List<LoxToken> tokens;
        private int currentIndex;

        LoxParser(List<LoxToken> tokens) {
                this.tokens = tokens;
                currentIndex = 0;
        }

        private boolean match(TokenType... types) {
                for (TokenType e : types) {
                        if (check(e)) {
                                advance();
                                return true;

                        }

                }
                return false;

        }

        private boolean check(TokenType type) {
                if (isAtEnd())
                        return false;

                return peek().type == type;

        }

        private void consume(TokenType t, String msg) {
                if (check(t)) {
                        advance();
                        return;
                }
                throw error(peek(), msg);

        }

        private ParseError error(LoxToken t, String msg) {

                Lox.error(t, msg);
                return new ParseError();
        }

        private boolean isAtEnd() {
                return currentIndex >= tokens.size() || peek().type == TokenType.EOF;
        }

        private LoxToken peek() {
                return this.tokens.get(currentIndex);
        }

        private LoxToken previus() {
                return tokens.get(currentIndex - 1);
        }

        private void advance() {
                if (!isAtEnd())
                        currentIndex++;
        }

        Expr parse() {
                try {
                        return expression();
                } catch (ParseError e) {
                        return null;
                }

        }

        private Expr expression() {
                return equality();

        }

        private Expr equality() {

                Expr e = comparison();

                while (match(TokenType.BANG_EQUAL, TokenType.EQUAL_EQUAL)) {
                        LoxToken operator = previus();
                        e = new Expr.Binary(e, operator, comparison());
                }
                return e;
        }

        private Expr comparison() {

                Expr e = term();

                while (match(TokenType.GREATER, TokenType.GREATER_EQUAL, TokenType.LESS, TokenType.LESS_EQUAL)) {
                        LoxToken operator = previus();

                        e = new Expr.Binary(e, operator, term());
                }

                return e;
        }

        private Expr term() {
                Expr e = factor();

                while (match(TokenType.MINUS, TokenType.PLUS)) {
                        LoxToken operator = previus();
                        e = new Expr.Binary(e, operator, factor());

                }

                return e;
        }

        private Expr factor() {

                Expr e = unary();

                while (match(TokenType.SLASH, TokenType.STAR)) {
                        LoxToken operator = previus();

                        e = new Expr.Binary(e, operator, unary());
                }

                return e;

        }

        private Expr unary() {

                if (match(TokenType.BANG, TokenType.MINUS)) {
                        LoxToken operator = previus();
                        return new Expr.Unary(operator, unary());

                } else {

                        return primary();

                }
        }

        private Expr primary() {

                if (match(TokenType.TRUE))
                        return new Expr.Literal(true);
                if (match(TokenType.FALSE))
                        return new Expr.Literal(false);
                if (match(TokenType.NIL))
                        return new Expr.Literal(null);

                if (match(TokenType.NUMBER, TokenType.STRING)) {
                        return new Expr.Literal(previus().literal);
                }

                if (match(TokenType.LEFT_PAREN)) {

                        Expr e = expression();
                        consume(TokenType.RIGHT_PAREN, "Expect ) after expression");

                        return new Expr.Grouping(e);
                }
                throw error(peek(), "Expect Expression");

        }

        private void synchronize() {

                advance();

                while (!isAtEnd()) {
                        if (previus().type == TokenType.SEMICOLON)
                                return;

                        switch (peek().type) {
                                case CLASS:
                                case FUN:
                                case VAR:
                                case FOR:
                                case IF:
                                case WHILE:
                                case PRINT:
                                case RETURN:
                                        return;
                                default:
                                        advance();

                        }

                }

        }

}
