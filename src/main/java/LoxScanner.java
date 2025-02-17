import java.util.List;
import java.util.ArrayList;

public class LoxScanner {
        final String source;
        private int start = 0;
        private int current = 0;
        private int line = 1;

        private boolean isAtEnd() {
                return current >= source.length();
        }

        LoxScanner(String source) {
                this.source = source;

        }

        private LoxToken singleChar() {

                char c = digest();

                switch (c) {
                        case '(':
                                return addToken(TokenType.LEFT_PAREN);
                        case ')':
                                return addToken(TokenType.RIGHT_PAREN);
                        case '{':
                                return addToken(TokenType.LEFT_BRACE);
                        case '}':
                                return addToken(TokenType.RIGHT_BRACE);
                        case ',':
                                return addToken(TokenType.COMMA);
                        case '.':
                                return addToken(TokenType.DOT);
                        case '-':
                                return addToken(TokenType.MINUS);
                        case '+':
                                return addToken(TokenType.PLUS);
                        case ';':
                                return addToken(TokenType.SEMICOLON);
                        case '*':
                                return addToken(TokenType.STAR);

                }

                return null;

        }

        public List<LoxToken> scanTokens() {

                ArrayList<LoxToken> tokens = new ArrayList<LoxToken>();

                while (isAtEnd() == false) {
                        LoxToken token = singleChar();
                        if (token != null) {
                                tokens.add(token);
                                start = current;
                        }
                }

                tokens.add(new LoxToken("", null, line, TokenType.EOF));

                return tokens;

        }

        private char digest() {

                char c = source.charAt(current);
                current++;

                return c;
        }

        private LoxToken addToken(TokenType type) {
                String lexeme = source.substring(start, current);

                return new LoxToken(lexeme, null, line, type);

        }

}
