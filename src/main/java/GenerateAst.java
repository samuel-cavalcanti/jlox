import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

public class GenerateAst {

        public static void run(String outputDir) throws IOException {

                defineAst(outputDir, "Expr", Arrays.asList(
                                "Assign   : LoxToken name, Expr value",
                                "Binary   : Expr left, LoxToken operator, Expr right",
                                "Call     : Expr callee, LoxToken paren, List<Expr> arguments",
                                "Get      : Expr object, LoxToken name",
                                "Set      : Expr object, LoxToken name, Expr value",
                                "Grouping : Expr expression",
                                "Literal  : Object value",
                                "Logical  : Expr left, LoxToken operator, Expr right",
                                "Unary    : LoxToken operator, Expr right",
                                "ThisExpr : LoxToken keyword",
                                "Variable : LoxToken name"));

                defineAst(outputDir, "Stmt", Arrays.asList(
                                "Expression : Expr expression",
                                "Block      : List<Stmt> statements",
                                "ClassStmt      : LoxToken name, List<Stmt.Function> methods",
                                "Print      : Expr expression",
                                "IfStmt     : Expr expression, Stmt thenBranch, Stmt elseBranch",
                                "Function   : LoxToken name, List<LoxToken> params, Stmt body",
                                "WhileStmt  : Expr condition, Stmt body",
                                "Var        : LoxToken name, Expr initializer",
                                "ReturnStmt : LoxToken keyword, Expr value"

                ));
        }

        private static void defineAst(
                        String outputDir, String baseName, List<String> types)
                        throws IOException {
                String path = outputDir + "/" + baseName + ".java";
                PrintWriter writer = new PrintWriter(path, "UTF-8");

                // writer.println("package com.craftinginterpreters.lox;");
                // writer.println();
                writer.println("import java.util.List;");
                writer.println();
                writer.println("abstract class " + baseName + " {");
                writer.println("  abstract <R> R accept(Visitor<R> v);");
                defineVisitor(writer, baseName, types);

                for (String type : types) {
                        String[] split = type.split(":");
                        String className = split[0].trim();
                        String fieldsString = split[1];
                        String[] fields = fieldsString.split(",");

                        writer.println("\tstatic class " + className + " extends " + baseName + " {");
                        defineConstructor(writer, className, baseName, fieldsString, fields);
                        defineAccept(writer, className);
                        defineFields(writer, fields);

                        writer.println("\t}");
                }

                writer.println("}");
                writer.close();
        }

        private static void defineVisitor(PrintWriter writer, String baseName, List<String> types) {
                writer.println("\tinterface Visitor<R> {");
                for (String type : types) {
                        String typeName = type.split(":")[0].trim();
                        writer.println(String.format("\t\tR visit%s(%s %s);", typeName, typeName,
                                        typeName.toLowerCase()));

                }
                writer.println("\t}");

        }

        private static void defineConstructor(PrintWriter writer, String className, String baseName,
                        String fieldsString, String[] fields) {

                writer.println("\t\t" + className + "(" + fieldsString + "){");
                for (String field : fields) {
                        String fieldName = field.split(" ")[2].trim();
                        writer.println("\t\t\tthis." + fieldName + "=" + fieldName + ";");
                }
                writer.println("\t\t}");
        }

        private static void defineFields(PrintWriter writer, String[] fields) {
                for (String field : fields) {
                        writer.println("\t\tfinal " + field.trim() + ";");
                }

        }

        private static void defineAccept(PrintWriter writer, String classNae) {

                writer.println("\t\t@Override");
                writer.println("\t\t<R> R accept(Visitor<R> v) {");
                writer.println("\t\t\treturn v.visit" + classNae.trim() + "(this);");
                writer.println("\t\t}");

        }

}
