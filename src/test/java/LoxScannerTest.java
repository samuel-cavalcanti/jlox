import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
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
