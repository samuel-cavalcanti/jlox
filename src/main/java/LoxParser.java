import java.util.ArrayList;
import java.util.Arrays;
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

        Expr parse() {
                try {
                        return expression();
                } catch (ParseError e) {
                        return null;
                }

        }

        List<Stmt> parseStatement() {

                List<Stmt> statements = new ArrayList<>();
                try {
                        while (!isAtEnd()) {
                                statements.add(declaration());
                        }
                        return statements;
                } catch (ParseError e) {
                        return statements;
                }

        }

        private Stmt declaration() {
                try {
                        if (match(TokenType.CLASS))
                                return classDeclaration();
                        if (match(TokenType.VAR))
                                return varDeclaration();
                        if (match(TokenType.FUN))
                                return functionDeclaration();

                        return statement();
                } catch (ParseError error) {
                        synchronize();

                        return null;
                }

        }

        private Stmt classDeclaration() {
                LoxToken className = consume(TokenType.IDENTIFIER, "Expected a class name after 'class'");
                consume(TokenType.LEFT_BRACE, String.format("Expected '{' after class %s", className.lexeme));

                List<Stmt.Function> methods = new ArrayList<>();
                while (!check(TokenType.RIGHT_BRACE) && !isAtEnd()) {
                        methods.add((Stmt.Function) functionDeclaration());
                }

                consume(TokenType.RIGHT_BRACE, String.format("Expected '}' after class %s body", className.lexeme));

                return new Stmt.ClassStmt(className, methods);
        }

        private Stmt functionDeclaration() {
                LoxToken funName = consume(TokenType.IDENTIFIER, "Expected function name");
                consume(TokenType.LEFT_PAREN, String.format("Expected '(' before function %s", funName.lexeme));
                List<LoxToken> args = new ArrayList<>();
                while (match(TokenType.IDENTIFIER)) {
                        if (args.size() >= 255)
                                error(peek(), "Can't have more than 255 parameters");

                        args.add(previus());
                        if (check(TokenType.RIGHT_PAREN))
                                break;
                        consume(TokenType.COMMA, "Expected ',' after function parameter");
                        // fn com(a,b,c,}
                }
                consume(TokenType.RIGHT_PAREN, "Expected ')' after function parameters");
                consume(TokenType.LEFT_BRACE, "Expected  '{' before function body");
                Stmt body = block();

                return new Stmt.Function(funName, args, body);

        }

        private Stmt varDeclaration() {
                LoxToken t = consume(TokenType.IDENTIFIER, "Expect variable name.");
                Expr init = null;

                if (match(TokenType.EQUAL))
                        init = expression();

                consume(TokenType.SEMICOLON, "Expect ';' after variable declaration.");
                return new Stmt.Var(t, init);
        }

        private Stmt statement() {

                if (match(TokenType.PRINT))
                        return printStatement();

                if (match(TokenType.LEFT_BRACE))
                        return block();
                if (match(TokenType.IF))
                        return ifStmt();
                if (match(TokenType.WHILE))
                        return whileStmt();

                if (match(TokenType.FOR))
                        return forStmt();
                if (match(TokenType.RETURN))
                        return returnStmt();

                return expressionStatement();

        }

        private Stmt returnStmt() {
                LoxToken keyword = previus();
                Expr e = check(TokenType.SEMICOLON) ? null : expression();
                consume(TokenType.SEMICOLON, "Expect ';' after return value");
                return new Stmt.ReturnStmt(keyword, e);
        }

        private Stmt forInitializer() {

                if (match(TokenType.SEMICOLON)) {
                        return null;
                } else if (match(TokenType.VAR))
                        return varDeclaration();
                else
                        return expressionStatement();
        }

        private Stmt forStmt() {

                consume(TokenType.LEFT_PAREN, "expect '(' after 'for'");

                Stmt initializer = forInitializer();

                Expr condition = check(TokenType.SEMICOLON) ? new Expr.Literal(true) : expression();

                consume(TokenType.SEMICOLON, "expect ';' after 'for' condition");

                Stmt increment = check(TokenType.RIGHT_PAREN) ? null : new Stmt.Expression(expression());

                consume(TokenType.RIGHT_PAREN, "expect ')' before 'for' condition");

                Stmt forbody = statement();

                if (increment != null) // adding increment at the end of loop
                        forbody = new Stmt.Block(Arrays.asList(forbody, increment));

                Stmt loop = new Stmt.WhileStmt(condition, forbody);

                if (initializer != null) {
                        return new Stmt.Block(Arrays.asList(initializer, loop));
                }

                return loop;

        }

        private Stmt whileStmt() {
                consume(TokenType.LEFT_PAREN, "expect '(' after 'while'");
                Expr condition = expression();
                consume(TokenType.RIGHT_PAREN, "expect ')' before 'while' condition");
                Stmt body = statement();

                return new Stmt.WhileStmt(condition, body);

        }

        private Stmt block() {

                List<Stmt> stms = new ArrayList<>();

                while (!check(TokenType.RIGHT_BRACE) && !isAtEnd()) {
                        stms.add(declaration());
                }

                consume(TokenType.RIGHT_BRACE, "Expect '}' after block");

                return new Stmt.Block(stms);

        }

        private Stmt ifStmt() {
                consume(TokenType.LEFT_PAREN, "Expected '(' after if");
                Expr e = expression();
                consume(TokenType.RIGHT_PAREN, "Expected ')' after expression");
                Stmt thenStatement = statement();
                Stmt elseStatement = null;
                if (match(TokenType.ELSE)) {
                        elseStatement = statement();
                }

                return new Stmt.IfStmt(e, thenStatement, elseStatement);

        }

        private Stmt printStatement() {
                Expr e = expression();
                consume(TokenType.SEMICOLON, "Expected ; after expression");
                return new Stmt.Print(e);
        }

        private Stmt expressionStatement() {
                Expr e = expression();
                consume(TokenType.SEMICOLON, "Expected ; after expression " + e.toString());
                return new Stmt.Expression(e);
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

        private LoxToken consume(TokenType t, String msg) {
                if (check(t)) {
                        LoxToken token = peek();
                        advance();
                        return token;
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

        private Expr expression() {
                return assignment();

        }

        private Expr assignment() {
                Expr expr = logicOr();

                if (match(TokenType.EQUAL)) {
                        LoxToken equal = previus();
                        Expr value = assignment();

                        if (expr instanceof Expr.Variable) {
                                LoxToken name = ((Expr.Variable) expr).name;
                                return new Expr.Assign(name, value);
                        }
                        if (expr instanceof Expr.Get) {
                                Expr.Get get = (Expr.Get) expr;
                                return new Expr.Set(get.object, get.name, value);
                        }

                        error(equal, "Invalid assignment target");

                }

                return expr;

        }

        private Expr logicOr() {
                Expr left = logicAnd();

                while (match(TokenType.OR)) {
                        LoxToken operator = previus();
                        Expr right = logicOr();
                        left = new Expr.Logical(left, operator, right);
                }

                return left;
        }

        private Expr logicAnd() {

                Expr left = equality();

                while (match(TokenType.AND)) {
                        LoxToken operator = previus();
                        Expr right = logicAnd();
                        left = new Expr.Logical(left, operator, right);
                }
                return left;
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

                        return call();

                }
        }

        private Expr call() {

                Expr expr = primary();

                while (true) {
                        if (match(TokenType.LEFT_PAREN)) {
                                expr = finishCall(expr);
                        } else if (match(TokenType.DOT)) {
                                LoxToken name = consume(TokenType.IDENTIFIER,
                                                "Expected method or property name after '.'");
                                expr = new Expr.Get(expr, name);
                        } else {
                                break;
                        }
                }

                return expr;
        }

        private Expr finishCall(Expr callee) {
                List<Expr> arguments = new ArrayList<>();
                if (!check(TokenType.RIGHT_PAREN)) {
                        do {
                                if (arguments.size() >= 255)
                                        error(peek(), "Can't have more than 255 arguments,");

                                arguments.add(expression());
                        } while (match(TokenType.COMMA));
                }

                LoxToken paren = consume(TokenType.RIGHT_PAREN,
                                "Expect ')' after arguments.");

                return new Expr.Call(callee, paren, arguments);
        }

        private Expr primary() {

                if (match(TokenType.TRUE))
                        return new Expr.Literal(true);
                if (match(TokenType.FALSE))
                        return new Expr.Literal(false);
                if (match(TokenType.NIL))
                        return new Expr.Literal(null);
                if (match(TokenType.THIS))
                        return new Expr.ThisExpr(previus());
                if (match(TokenType.NUMBER, TokenType.STRING)) {
                        return new Expr.Literal(previus().literal);
                }

                if (match(TokenType.IDENTIFIER)) {
                        return new Expr.Variable(previus());
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
