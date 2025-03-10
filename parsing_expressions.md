
## Parsing Expressions

```
expression     → literal
               | unary
               | binary
               | grouping ;

literal        → NUMBER | STRING | "true" | "false" | "nil" ;
grouping       → "(" expression ")" ;
unary          → ( "-" | "!" ) expression ;
binary         → expression operator expression ;
operator       → "==" | "!=" | "<" | "<=" | ">" | ">="
               | "+"  | "-"  | "*" | "/" ;
```

After removing the ambiguity

```
expression     → equality ;
equality       → comparison ( ( "!=" | "==" ) comparison )* ;
comparison     → term ( ( ">" | ">=" | "<" | "<=" ) term )* ;
term           → factor ( ( "-" | "+" ) factor )* ;
factor         → unary ( ( "/" | "*" ) unary )* ;
unary          → ( "!" | "-" ) unary
               | primary ;
primary        → NUMBER | STRING | "true" | "false" | "nil"
               | "(" expression ")" ;
```

## Statements

```
program        → statement* EOF ;

statement      → exprStmt
               | printStmt ;

exprStmt       → expression ";" ;
printStmt      → "print" expression ";" ;
```


```java

        List<Stmt> parseStatement() {

                List<Stmt> statements = new ArrayList<>();
                try {
                        while (!isAtEnd()) {
                                statements.add(statement());
                        }
                        return statements;
                } catch (ParseError e) {
                        return statements;
                }

        }

        private Stmt statement() {

                if (match(TokenType.PRINT))
                        return printStatement();

                return expressionStatement();

        }

        private Stmt printStatement() {
                Expr e = expression();
                consume(TokenType.SEMICOLON, "Expected ; after expression");
                return new Stmt.Print(e);
        }

        private Stmt expressionStatement() {
                Expr e = expression();
                consume(TokenType.SEMICOLON, "Expected ; after expression");
                return new Stmt.Expression(e);
        }

```
