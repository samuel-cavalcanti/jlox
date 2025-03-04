import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class LoxInterpreterTest {

        @Test
        void testArithmeticOperators() {
                testInterpret("5 + 3", "8");
                testInterpret("5 - 3", "2");
                testInterpret("2.5 - 3", "-0.5");
                testInterpret("2.5 * 2", "5");
                testInterpret("5 / 2", "2.5");
                testInterpret("(5 / 2) * 2 + 4", "9");
        }

        @Test
        void testBoolean() {

                testInterpret("2.5 < 5", "true");
                testInterpret("2.5 > 5", "false");
                testInterpret("2.5 == 2.5", "true");
                testInterpret("5 > 1", "true");
                testInterpret("5 < 1", "false");
                testInterpret("5 >= 5", "true");
                testInterpret("5 <= 5", "true");
                // testinterpret("true == true", "true");
                // testInterpret("true == false", "false");
                // testInterpret("false == true", "false");
                // testInterpret("false == false", "true");
        }

        @Test
        void testStringOperators() {

                testInterpret("\"bar\" == \"bar\"", "true");
                testInterpret("\"foo\"== \"bar\"", "false");
                testInterpret("\"foo\" + \"bar\"", "foobar");
                testInterpret("\"foo\" - \"bar\"", "");

        }

        void testInterpret(String source, String expected) {

                Lox l = new Lox();
                String result = l.interpret(source);
                assertEquals(result, expected);

        }
}
