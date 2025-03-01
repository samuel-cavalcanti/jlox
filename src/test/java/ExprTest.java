import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExprTest {

        @Test
        void testExpressions() {
                Expr expression = new Expr.Binary(
                                new Expr.Unary(
                                                new LoxToken("-", null, 1, TokenType.MINUS),
                                                new Expr.Literal(123)),
                                new LoxToken("*", null, 1, TokenType.STAR),
                                new Expr.Grouping(
                                                new Expr.Literal(45.67)));

                assertEquals(new AstPrinter().print(expression), "(* (- 123) (group 45.67))");

        }
}
