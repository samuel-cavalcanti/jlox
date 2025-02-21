import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

public class LoxScanner {
        final String source;
        private int start = 0;
        private int current = 0;
        private int line = 1;

        private static final Map<String, TokenType> keywords;

        static {
                keywords = new HashMap<>();
                keywords.put("and", TokenType.AND);
                keywords.put("class", TokenType.CLASS);
                keywords.put("else", TokenType.ELSE);
                keywords.put("false", TokenType.FALSE);
                keywords.put("for", TokenType.FOR);
                keywords.put("fun", TokenType.FUN);
                keywords.put("if", TokenType.IF);
                keywords.put("nil", TokenType.NIL);
                keywords.put("or", TokenType.OR);
                keywords.put("print", TokenType.PRINT);
                keywords.put("return", TokenType.RETURN);
                keywords.put("super", TokenType.SUPER);
                keywords.put("this", TokenType.THIS);
                keywords.put("true", TokenType.TRUE);
                keywords.put("var", TokenType.VAR);
                keywords.put("while", TokenType.WHILE);

        }

        LoxScanner(String source) {
                this.source = source;

        }

        public List<LoxToken> scanTokens() {

                ArrayList<LoxToken> tokens = new ArrayList<LoxToken>();

                while (isAtEnd() == false) {
                        LoxToken token = singleChar();
                        if (token != null) {
                                tokens.add(token);
                        }

                }

                tokens.add(new LoxToken("", null, line, TokenType.EOF));

                return tokens;

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
                        case '!':
                                return addToken(match('=') ? TokenType.BANG_EQUAL : TokenType.BANG);
                        case '=':
                                return addToken(match('=') ? TokenType.EQUAL_EQUAL : TokenType.EQUAL);
                        case '<':
                                return addToken(match('=') ? TokenType.LESS_EQUAL : TokenType.LESS);
                        case '>':
                                return addToken(match('=') ? TokenType.GREATER_EQUAL : TokenType.GREATER);
                        case '/':
                                if (match('/'))
                                        return readAComment();
                                else
                                        return addToken(TokenType.SLASH);
                        case ' ':
                        case '\r':
                        case '\t':
                                return null;

                        case '\n':
                                line++;

                                return null;
                        case '"':
                                return string();

                }

                if (isDigit(c))
                        return number();

                if (isAlpha(c))
                        return identifier();

                Lox.error(line, "", "Unexpected character: " + c);
                // start = current;

                return null;

        }

        private LoxToken identifier() {
                while (isAlpahNumeric(peek()))
                        advance();
                String text = source.substring(start, current);
                TokenType t = keywords.get(text);

                if (t == null)
                        t = TokenType.IDENTIFIER;

                return addToken(t);

        }

        private boolean isDigit(char c) {
                return c >= '0' && c <= '9';

        }

        private boolean isAlpha(char c) {
                return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
        }

        private boolean isAlpahNumeric(char c) {
                return isAlpha(c) || isDigit(c);
        }

        private LoxToken number() {

                while (isDigit(peek()))
                        advance();

                if (peek() == '.' && isDigit(peekNext())) {
                        advance();
                }

                while (isDigit(peek()))
                        advance();

                String number = source.substring(start, current);
                return addToken(TokenType.NUMBER, Double.parseDouble(number));

        }

        private LoxToken readAComment() {

                char c = peek();
                while (!isNewLine(c) && c != '\0') {
                        c = peek();
                        advance();
                }

                return null; // addToken(TokenType.COMMENT);
        }

        private LoxToken string() {

                char c = peek();
                while (c != '"' && !isAtEnd()) {
                        isNewLine(c);
                        advance();
                        c = peek();
                }

                if (isAtEnd()) {
                        Lox.error(line, "", "Unterminated string.");
                        return null;
                }

                advance();

                String lexeme = source.substring(start + 1, current - 1);

                return addToken(TokenType.STRING, lexeme);

        }

        private boolean match(char expected) {

                char secondChar = peek();
                if (secondChar == '\0')
                        return false;

                boolean isMatched = secondChar == expected;
                if (isMatched) {
                        current++;
                }

                return isMatched;

        }


        private char digest() {
                start = current;
                char c = peek();
                advance();
                return c;
        }

        private boolean isNewLine(char c) {
                boolean b = c == '\n';
                if (b)
                        line++;

                return b;

        }

        private char peek() {
                if (isAtEnd())
                        return '\0';
                return source.charAt(current);
        }

        private char peekNext() {

                if (current + 1 >= source.length())
                        return '\0';

                return source.charAt(current + 1);
        }

        private void advance() {
                current++;
        }

        private boolean isAtEnd() {
                return current >= source.length();
        }

        private LoxToken addToken(TokenType type) {
                return addToken(type, null);

        }

        private LoxToken addToken(TokenType type, Object literal) {
                String lexeme = source.substring(start, current);

                return new LoxToken(lexeme, literal, line, type);

        }

}
