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

        @Test
        void testBlock() {

                String expected = "";
                for (int i = 0; i < 6; i++)
                        expected += "nil\n";

                expected += "inner a\n";
                expected += "outer b\n";
                expected += "global c\n";

                expected += "outer a\n";
                expected += "outer b\n";
                expected += "global c\n";

                expected += "global a\n";
                expected += "global b\n";
                expected += "global c\n";

                testRun("""
                                var a = "global a";
                                var b = "global b";
                                var c = "global c";
                                {
                                  var a = "outer a";
                                  var b = "outer b";
                                  {
                                    var a = "inner a";
                                    print a;
                                    print b;
                                    print c;
                                  }
                                  print a;
                                  print b;
                                  print c;
                                }
                                print a;
                                print b;
                                print c;
                                """, expected);

                testRun("""
                                var a  = 1;
                                {
                                        var b = a + 2;
                                        print b;
                                }
                                """, "nil\nnil\n3\n");
                testRun("""
                                var first = "Samuel";
                                var last = 17;
                                print "Hi, " + first + " " + last + "!";
                                """, "nil\nnil\nHi, Samuel 17.0!\n");

                testRun("""
                                var a  = 1;
                                {
                                        var a = a + 2;
                                        print a;
                                }
                                """, "");

        }

        @Test
        void testDeclareVariables() {

                testRun("var a = 2;", "nil\n");
                testRun("print a;", "2\n");
                testRun("print a;", "2\n");
                testRun("a = \"foo\";", "foo\n");
                testRun("print a;", "foo\n");
                testRun("var a = 2; print a;", "nil\n2\n");

        }

        @Test
        void testLogicOperators() {

                testRun("""
                                print "hi" or 2;
                                """, "hi\n");

                testRun("""
                                print nil or "yes";
                                """, "yes\n");

                testRun("""
                                print nil and "yes";
                                """, "nil\n");
                testRun("""
                                print "no" and "yes";
                                """, "yes\n");

        }

        @Test
        void condicionalStms() {

                testRun("""
                                if ( 1 == 1)
                                        print "yes";
                                """, "yes\n");
                testRun("""
                                if ( 2 < 1)
                                        print "yes";
                                else
                                    print "false";
                                """, "false\n");

        }

        @Test
        void testInvalidReturns() {

                testRun("""
                                return "at top level";""", "");

                testRun("""
                                class Foo {
                                          init() {
                                            return "error";
                                          }
                                        }""", "");
        }

        @Test
        void testForLoop() {
                String expected = "nil\n";
                for (int a = 0; a < 3; a++) {
                        String printOutput = Integer.toString(a) + "\n";
                        String assignoutput = Integer.toString(a + 1) + "\n";
                        expected += printOutput + assignoutput;
                }

                testRun("for (var i = 0; i < 3; i = i + 1) print i;", expected);
        }

        @Test
        void testWhileLoop() {

                String expected = "nil\n";
                for (int a = 0; a < 3; a++) {
                        String printOutput = Integer.toString(a) + "\n";
                        String assignoutput = Integer.toString(a + 1) + "\n";
                        expected += printOutput + assignoutput;
                }
                testRun("""
                                var a = 0;
                                while ( a < 3){
                                        print a; // a
                                        a = a + 1;// a +1
                                }
                                """, expected);

        }

        @Test
        void testFunction() {

                testRun("""
                                fun sayHi(first, last) {
                                  print "Hi, " + first + " " + last + "!";
                                }

                                sayHi("Dear", "Reader");""", "<fn sayHi>\nnil\n");

                testRun("""
                                fun sayHello(first, last) {
                                  return "Hi, " + first + " " + last + "!";
                                }

                                print sayHello("Dear", "Reader");""", "<fn sayHello>\nHi, Dear Reader!\n");

                testRun("""
                                fun fib(n) {
                                        if(n < 2) return n;
                                        return fib(n-2) + fib(n-1);
                                }

                                print fib(10);
                                """,

                                "<fn fib>\n55\n");
        }

        @Test
        void testSameName() {

                testRun("""
                                fun bad() {
                                  var bad_val = "first";
                                  var bad_val = "second";
                                }
                                bad();""", "");

        }

        @Test
        void testInvalidUsesOfThis() {

                testRun("""
                                fun bad() {
                                        print this;
                                }
                                bad();""", "");

                testRun("""
                                print this;""", "");

        }

        @Test
        void testInvalidUsesOfSuper() {

                testRun("""
                                fun bad() {
                                        print super.foo();
                                }
                                bad();""", "");

                testRun("""
                                print super.foo();""", "");
                testRun("""
                                print super.foo;""", "");

                testRun("""
                                class P{
                                         init(){
                                           super.init();
                                         }
                                }
                                 """, "");

        }

        @Test
        void testClosures() {

                testRun("""
                                fun makeCounter() {
                                  var i = 0;
                                  fun count() {
                                    i = i + 1;
                                    print "make counter " + i;
                                    return i;
                                  }

                                  return count;
                                }

                                var counter = makeCounter();
                                print counter(); // "1".
                                print counter(); // "2".
                                                                """, "<fn makeCounter>\nnil\n1\n2\n");

                testRun("""
                                var a = "global";
                                {
                                  fun showA() {
                                    print a;
                                    return a;
                                  }

                                  showA();
                                  var a = "block";
                                  showA();
                                }""", "nil\n<fn showA>\nglobal\nnil\nglobal\n");

        }

        @Test
        void testClass() {

                testRun("""
                                class Cake {
                                  taste() {
                                    var adjective = "delicious";
                                    var msg = "The " + this.flavor + " cake is " + adjective + "!";
                                    print msg ;
                                    return msg;

                                  }
                                }

                                var cake = Cake();
                                cake.flavor = "German chocolate";

                                cake.taste();
                                """, "Cake\nnil\nGerman chocolate\nThe German chocolate cake is delicious!\n");

                testRun("""
                                class Person {
                                  sayName() {
                                    print this.name;
                                    return this.name;
                                  }
                                }

                                var jane = Person();
                                jane.name = "Jane";

                                var method = jane.sayName;
                                method();""", "Person\nnil\nJane\nnil\nJane\n");

                testRun("""
                                class Person {
                                  sayName() {
                                    print this.name;
                                    return this.name;
                                  }
                                }

                                var jane = Person();
                                jane.name = "Jane";

                                var bill = Person();
                                bill.name = "Bill";

                                bill.sayName = jane.sayName;
                                bill.sayName();""", "Person\nnil\nJane\nnil\nBill\n<fn sayName>\nJane\n");

                testRun("""
                                class Foo {
                                  init() {
                                    return;
                                  }
                                }
                                Foo();""", "Foo\nFoo instance\n");

                testRun("""
                                class Counter {
                                  init(startValue) {
                                    if (startValue < 0) {
                                      print "startValue can't be negative";
                                      this.count = 0;
                                    } else {
                                      this.count = startValue;
                                    }
                                  }
                                }

                                // constructor is called automatically here
                                var instance = Counter(-10);
                                print instance.count;

                                // it should be possible to call the constructor
                                // on a class instance as well
                                print instance.init(10).count;""", "Counter\nnil\n0\n10\n");

                testRun("""
                                class Doughnut {
                                  cook() {
                                    var msg = "Fry until golden brown.";
                                    print msg;
                                    return msg;
                                  }
                                }

                                class BostonCream < Doughnut {
                                  cook() {
                                    var msg = "Pipe full of custard and coat with chocolate.";
                                    print msg;
                                    return msg;
                                  }
                                }

                                BostonCream().cook();""",
                                "Doughnut\nBostonCream\nPipe full of custard and coat with chocolate.\n");



                testRun("""
                                class Doughnut {
                                  cook() {
                                    var msg = "Fry until golden brown.";
                                    print msg;
                                    return msg;
                                  }
                                }

                                class BostonCream < Doughnut {
                                  cook() {
                                        return super.cook();
                                  }
                                }

                                BostonCream().cook();""",
                                "Doughnut\nBostonCream\nFry until golden brown.\n");

        }

        void testInterpret(String source, String expected) {

                Lox l = new Lox();
                String result = l.interpret(source);
                assertEquals(expected, result);

        }

        void testRun(String source, String expected) {

                Lox l = new Lox();
                String result = l.run(source);

                assertEquals(expected, result);

        }
}
