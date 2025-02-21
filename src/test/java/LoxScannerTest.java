import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

public class LoxScannerTest {

        @Test
        void testSingleChar() {

                testSource("())", Arrays.asList(new LoxToken[] {

                                new LoxToken("(", null, 1, TokenType.LEFT_PAREN),
                                new LoxToken(")", null, 1, TokenType.RIGHT_PAREN),
                                new LoxToken(")", null, 1, TokenType.RIGHT_PAREN),
                                new LoxToken("", null, 1, TokenType.EOF),
                }));

                testSource("({*.,+*})", Arrays.asList(new LoxToken[] {

                                new LoxToken("(", null, 1, TokenType.LEFT_PAREN),
                                new LoxToken("{", null, 1, TokenType.LEFT_BRACE),
                                new LoxToken("*", null, 1, TokenType.STAR),
                                new LoxToken(".", null, 1, TokenType.DOT),
                                new LoxToken(",", null, 1, TokenType.COMMA),
                                new LoxToken("+", null, 1, TokenType.PLUS),
                                new LoxToken("*", null, 1, TokenType.STAR),
                                new LoxToken("}", null, 1, TokenType.RIGHT_BRACE),
                                new LoxToken(")", null, 1, TokenType.RIGHT_PAREN),
                                new LoxToken("", null, 1, TokenType.EOF),

                }));
        }

        @Test
        void testSingleCharError() {

                testSource(",.$(#", Arrays.asList(new LoxToken[] {

                                new LoxToken(",", null, 1, TokenType.COMMA),
                                new LoxToken(".", null, 1, TokenType.DOT),
                                new LoxToken("(", null, 1, TokenType.LEFT_PAREN),
                                new LoxToken("", null, 1, TokenType.EOF),

                }));
                testSource("@", Arrays.asList(new LoxToken[] {
                                new LoxToken("", null, 1, TokenType.EOF),
                }));

        }

        @Test
        void testEmptySource() {
                testSource("", Arrays.asList(new LoxToken[] {
                                new LoxToken("", null, 1, TokenType.EOF),
                }));

        }

        @Test
        void testOperatorsAndComment() {

                String loxTokens = """
                                          // this is a comment
                                          (( )){} // grouping stuff
                                          !*+-/=<> <= == // operators
                                """;
                testSource(loxTokens, Arrays.asList(new LoxToken[] {
                                new LoxToken("(", null, 2, TokenType.LEFT_PAREN),
                                new LoxToken("(", null, 2, TokenType.LEFT_PAREN),
                                new LoxToken(")", null, 2, TokenType.RIGHT_PAREN),
                                new LoxToken(")", null, 2, TokenType.RIGHT_PAREN),
                                new LoxToken("{", null, 2, TokenType.LEFT_BRACE),
                                new LoxToken("}", null, 2, TokenType.RIGHT_BRACE),
                                new LoxToken("!", null, 3, TokenType.BANG),
                                new LoxToken("*", null, 3, TokenType.STAR),
                                new LoxToken("+", null, 3, TokenType.PLUS),
                                new LoxToken("-", null, 3, TokenType.MINUS),
                                new LoxToken("/", null, 3, TokenType.SLASH),
                                new LoxToken("=", null, 3, TokenType.EQUAL),
                                new LoxToken("<", null, 3, TokenType.LESS),
                                new LoxToken(">", null, 3, TokenType.GREATER),
                                new LoxToken("<=", null, 3, TokenType.LESS_EQUAL),
                                new LoxToken("==", null, 3, TokenType.EQUAL_EQUAL),
                                new LoxToken("", null, 3, TokenType.EOF),
                }));

        }

        @Test
        void testStringLiteral() {

                // testSource("\"Foo Bazz\" \"hello world\"", Arrays.asList(new LoxToken[] {
                //                 new LoxToken("Foo Bazz", null, 1, TokenType.STRING),
                //                 new LoxToken("hello world", null, 2, TokenType.STRING),
                //                 new LoxToken("", null, 1, TokenType.EOF),
                // }));

        }

        void testSource(String source, List<LoxToken> expectedTokens) {

                LoxScanner scanner = new LoxScanner(source);

                List<LoxToken> tokens = scanner.scanTokens();

                assertEquals(tokens.size(), expectedTokens.size());

                for (int i = 0; i < tokens.size(); i++) {
                        LoxToken t1 = tokens.get(i);
                        LoxToken t2 = expectedTokens.get(i);

                        assertEquals(t1, t2);

                }

        }

}
