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

                testSource("\"Foo Bazz\" \"hello world\"", Arrays.asList(new LoxToken[] {
                                new LoxToken("\"Foo Bazz\"", "Foo Bazz", 1, TokenType.STRING),
                                new LoxToken("\"hello world\"", "hello world", 1, TokenType.STRING),
                                new LoxToken("", null, 1, TokenType.EOF),
                }));

        }

        @Test
        void testNumberLiteral() {
                testSource("123 123.4 .123 123.", Arrays.asList(new LoxToken[] {
                                new LoxToken("123", 123, 1, TokenType.NUMBER),
                                new LoxToken("123.4", 123.4, 1, TokenType.NUMBER),
                                new LoxToken(".", null, 1, TokenType.DOT),
                                new LoxToken("123", 123, 1, TokenType.NUMBER),
                                new LoxToken("123", 123, 1, TokenType.NUMBER),
                                new LoxToken(".", null, 1, TokenType.DOT),
                                new LoxToken("", null, 1, TokenType.EOF),
                }));

        }

        @Test
        void testIdentifiers() {

                testSource("foo bar _hello", Arrays.asList(new LoxToken[] {
                                new LoxToken("foo", null, 1, TokenType.IDENTIFIER),
                                new LoxToken("bar", null, 1, TokenType.IDENTIFIER),
                                new LoxToken("_hello", null, 1, TokenType.IDENTIFIER),
                                new LoxToken("", null, 1, TokenType.EOF),
                }));
        }

        @Test
        void testReservedWords() {

                testSource("and class else false for fun if nil or print return super this true var while",
                                Arrays.asList(new LoxToken[] {
                                                new LoxToken("and", null, 1, TokenType.AND),
                                                new LoxToken("class", null, 1, TokenType.CLASS),
                                                new LoxToken("else", null, 1, TokenType.ELSE),
                                                new LoxToken("false", null, 1, TokenType.FALSE),
                                                new LoxToken("for", null, 1, TokenType.FOR),
                                                new LoxToken("fun", null, 1, TokenType.FUN),
                                                new LoxToken("if", null, 1, TokenType.IF),
                                                new LoxToken("nil", null, 1, TokenType.NIL),
                                                new LoxToken("or", null, 1, TokenType.OR),
                                                new LoxToken("print", null, 1, TokenType.PRINT),
                                                new LoxToken("return", null, 1, TokenType.RETURN),
                                                new LoxToken("super", null, 1, TokenType.SUPER),
                                                new LoxToken("this", null, 1, TokenType.THIS),
                                                new LoxToken("true", null, 1, TokenType.TRUE),
                                                new LoxToken("var", null, 1, TokenType.VAR),
                                                new LoxToken("while", null, 1, TokenType.WHILE),
                                                new LoxToken("", null, 1, TokenType.EOF),
                                }));

        }

        void testSource(String source, List<LoxToken> expectedTokens) {

                LoxScanner scanner = new LoxScanner(source);

                List<LoxToken> tokens = scanner.scanTokens();
                // System.err.println(tokens);

                assertEquals(tokens.size(), expectedTokens.size());

                for (int i = 0; i < tokens.size(); i++) {
                        LoxToken t1 = tokens.get(i);
                        LoxToken t2 = expectedTokens.get(i);

                        assertEquals(t1, t2);

                }

        }

}
