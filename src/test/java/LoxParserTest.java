import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

public class LoxParserTest {

        @Test
        void testArithmetic() {

                assertExpressions("2+3", "(+ 2.0 3.0)");
                assertExpressions("2 - 3", "(- 2.0 3.0)");
                assertExpressions("2  *  3", "(* 2.0 3.0)");
                assertExpressions("2/  3", "(/ 2.0 3.0)");
        }

        @Test
        void testWhile() {
                assertExpressions("2/  3", "(/ 2.0 3.0)");

        }

        @Test
        void testReservedwords() {
                assertExpressions("false", "false");
                assertExpressions("true", "true");
                assertExpressions("nil", "nil");
                // assertExpressions("for", "for");
                // assertExpressions("fun", "fun");
                // assertExpressions("if", "if");
                // assertExpressions("or", "or");
                // assertExpressions("print", "print");
                // assertExpressions("return", "return");
                // assertExpressions("super", "super");
                // assertExpressions("this", "this");
                // assertExpressions("while", "while");
                // assertExpressions("and", "and");
                // assertExpressions("class", "class");
                // assertExpressions("else", "else");
        }

        void assertExpressions(String source, String expression) {

                List<LoxToken> tokens = new LoxScanner(source).scanTokens();

                Expr e = new LoxParser(tokens).parse();
                if (e == null) {
                        System.err.println(source + " Token: " + tokens.get(0));
                }

                assertEquals(new AstPrinter().print(e), expression);

        }
}
