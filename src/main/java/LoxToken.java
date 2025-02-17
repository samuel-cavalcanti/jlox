import java.util.Objects;

public class LoxToken {

        final String lexeme;
        final Object literal;
        final int line;
        final TokenType type;

        LoxToken(String lexeme, Object literal, int line, TokenType type) {
                this.lexeme = lexeme;
                this.literal = literal;
                this.line = line;
                this.type = type;
        }

        @Override
        public String toString() {
                return this.type + " " + this.lexeme + " " + this.literal;
        }

        @Override
        public int hashCode() {

                return Objects.hash(type, lexeme);
        }

        @Override
        public boolean equals(Object obj) {
                boolean sameMemory = obj == this;
                if (sameMemory) {
                        return true;
                }
                if (obj == null) {
                        return false;

                }

                boolean sameclass = obj.getClass() == this.getClass();
                if (sameclass == false) {
                        return false;
                }

                LoxToken token = (LoxToken) obj;

                boolean sameLex = token.lexeme.equals(this.lexeme);
                boolean sameType = token.type == this.type;

                return sameLex && sameType;

        }

}

enum TokenType {
        // Single-character tokens.
        LEFT_PAREN, RIGHT_PAREN, LEFT_BRACE, RIGHT_BRACE,
        COMMA, DOT, MINUS, PLUS, SEMICOLON, SLASH, STAR,

        // One or two character tokens.
        BANG, BANG_EQUAL,
        EQUAL, EQUAL_EQUAL,
        GREATER, GREATER_EQUAL,
        LESS, LESS_EQUAL,

        // Literals.
        IDENTIFIER, STRING, NUMBER,

        // Keywords.
        AND, CLASS, ELSE, FALSE, FUN, FOR, IF, NIL, OR,
        PRINT, RETURN, SUPER, THIS, TRUE, VAR, WHILE,

        EOF

}
