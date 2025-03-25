import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Main {

        static String readFile(String path) {

                try {
                        return Files.readString(Path.of(path));

                } catch (IOException e) {
                        System.err.println("Error reading file: " + e.getMessage());
                        return "";
                }

        }

        public static void main(String[] args) {
                // You can use print statements as follows for debugging, they'll be visible
                // when running tests.
                System.err.println("Logs from your program will appear here!");

                if (args.length == 1) {
                        try {
                                GenerateAst.run(args[0]);
                        } catch (IOException e) {
                                System.err.println("Unable do generate AST");
                                System.err.println(e);
                                System.exit(1);
                        }
                }

                if (args.length < 2) {
                        System.err.println("Usage: ./your_program.sh tokenize|parse <filename>");
                        System.exit(1);
                }

                String command = args[0];
                String filename = args[1];
                Lox engine = new Lox();
                String fileContents = readFile(filename);
                System.err.println("CONTENTS: " + fileContents);

                if (command.equals("tokenize")) {
                        List<LoxToken> tokens = engine.scan(fileContents);
                        for (LoxToken t : tokens)
                                System.out.println(t);
                } else if (command.equals("parse")) {
                        Expr e = engine.parse(fileContents);
                        if (e != null)
                                System.out.println(new AstPrinter().print(e));

                } else if (command.equals("evaluate")) {
                        String result = engine.interpret(fileContents);
                        System.out.println(result);

                } else if (command.equals("run")) {
                        engine.run(fileContents);
                }

                else {

                        System.err.println("Unknown command: " + command);
                        System.exit(1);
                }

                if (Lox.hadError) {
                        System.exit(65);
                }
                if (Lox.hadRuntimeError) {

                        System.exit(70);
                }

        }
}
