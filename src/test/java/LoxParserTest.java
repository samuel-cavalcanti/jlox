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
                assertExpressions("16 * 38 / 58", "(/ (* 16.0 38.0) 58.0)");
                assertExpressions("52 + 80 - 94", "(- (+ 52.0 80.0) 94.0)");
        }

        @Test
        void testComparasion() {
                assertExpressions("83 < 99 < 115", "(< (< 83.0 99.0) 115.0)");
                assertExpressions("\"baz\" == \"baz\"", "(== baz baz)");

        }

        @Test
        void numbers() {

                assertExpressions("1", "1.0");
                assertExpressions("1.2", "1.2");
                assertExpressions("45", "45.0");
        }

        @Test
        void strings() {
                assertExpressions("\"1\"", "1");
                assertExpressions("\"hello\"", "hello");
                assertExpressions("\"world\"", "world");
                assertExpressions("\"'hello world'\"", "'hello world'");

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
