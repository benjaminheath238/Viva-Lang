package viva.utils;

import java.util.Map.Entry;

import viva.base.common.Token;
import viva.base.common.ast.Node;
import viva.base.common.ast.NodeVisitor;

public class Formatter implements NodeVisitor<String> {
    private int indent;

    public Formatter() {
        indent = 0;
    }

    protected void indent() {
        indent++;
    }

    protected void dedent() {
        indent--;
    }

    protected String indent(String input) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < indent; i++) {
            builder.append("\t");
        }

        builder.append(input);

        return builder.toString();
    }

    @Override
    public String visitProgram(Node.Program node) {
        StringBuilder builder = new StringBuilder();
        for (Node.Stmt stmt : node.body) {
            builder.append(indent(visit(stmt)));
            builder.append("\n\n");
        }
        return builder.toString();
    }

    @Override
    public String visitStmt(Node.Stmt node) {
        return "Statement\n";
    }

    @Override
    public String visitBlockStmt(Node.Stmt.Block node) {
        StringBuilder builder = new StringBuilder();
        indent();
        for (Node.Stmt stmt : node.body) {
            builder.append(indent(visit(stmt)));
            builder.append("\n");
        }
        dedent();
        return "{\n" + builder + indent("}");
    }
    
    @Override
    public String visitDefineStmt(Node.Stmt.Define node) {
        return "Define\n";
    }
    
    @Override
    public String visitDefineProcStmt(Node.Stmt.Define.Proc node) {
        StringBuilder builder = new StringBuilder();
        for (Entry<Token, Token> entry : node.parameters.entrySet()) {
            builder.append(entry.getKey().lexeme + ": " + entry.getValue().lexeme);
        }
        return "let " + node.identifier.lexeme + ": proc (" + builder + ") => " + node.returnType.lexeme + (node.body == null ? ";" : " = " + visit(node.body));
    }

    @Override
    public String visitDefineTypeStmt(Node.Stmt.Define.Type node) {
        return "let " + node.identifier.lexeme + ": " + node.kind + " = " + visit(node.body);
    }

    @Override
    public String visitDefineVariableStmt(Node.Stmt.Define.Variable node) {
        return "let " + node.identifier.lexeme + ": " + node.type.lexeme + (node.value == null ? ";" : "= " + visit(node.value));
    }

    @Override
    public String visitReturnStmt(Node.Stmt.Return node) {
        return "return " + visit(node.value) + ";";
    }

    @Override
    public String visitContinueStmt(Node.Stmt.Continue node) {
        return "continue " + node.identifier.lexeme + ";";
    }

    @Override
    public String visitBreakStmt(Node.Stmt.Break node) {
        return "break " + node.identifier.lexeme + ";";
    }

    @Override
    public String visitWhileStmt(Node.Stmt.While node) {
        return "while (" + visit(node.condition) + ") " + visit(node.body);
    }

    @Override
    public String visitUntilStmt(Node.Stmt.Until node) {
        return "do " + visit(node.body) + " until (" + visit(node.condition) + ");";
    }

    @Override
    public String visitIfStmt(Node.Stmt.If node) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < node.elifs.size(); i++) {
            builder.append(" elif (" + visit(node.elifs.get(i).a) + ") " + visit(node.elifs.get(i).b));
        }
        return "if (" + visit(node.condition) + ") " + visit(node.body) + builder + (node.elseBody == null ? "" : " else " + visit(node.elseBody));
    }

    @Override
    public String visitExprStmt(Node.Stmt.Expr node) {
        return visit(node.value) + ";";
    }

    @Override
    public String visitExpr(Node.Expr node) {
        return "Expression\n";
    }

    @Override
    public String visitBinaryExpr(Node.Expr.Binary node) {
        return visit(node.operandLeft) + node.operator.lexeme + visit(node.operandRight);
    }

    @Override
    public String visitUnaryExpr(Node.Expr.Unary node) {
        return node.operator.lexeme + visit(node.operandRight);
    }

    @Override
    public String visitLiteralExpr(Node.Expr.Literal node) {
        return node.value.lexeme;
    }

    @Override
    public String visitVariableExpr(Node.Expr.Variable node) {
        return node.identifier.lexeme;
    }

    @Override
    public String visitProcCallExpr(Node.Expr.ProcCall node) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < node.arguments.size(); i++) {
            builder.append(visit(node.arguments.get(i)));
            
            if (i < node.arguments.size() - 1) {
                builder.append(",");
            }
        }

        return node.identifier.lexeme + builder;
    }

    @Override
    public String visitAccessExpr(Node.Expr.Access node) {
        return node.identifier.lexeme + "[" + visit(node.index) + "]";
    }
    
    @Override
    public String visitReferenceExpr(Node.Expr.Reference node) {
        return node.variable.lexeme + "." + node.identifier.lexeme;
    }

    @Override
    public String visitBlockExpr(Node.Expr.Block node) {
        StringBuilder builder = new StringBuilder();
        indent();
        for (Node.Stmt stmt : node.body) {
            builder.append(indent(visit(stmt)));
            builder.append("\n");
        }
        dedent();
        return "{\n" + builder + indent("}");
    }

    @Override
    public String visitWhileExpr(Node.Expr.While node) {
        return "while (" + visit(node.condition) + ") " + visit(node.body);
    }

    @Override
    public String visitUntilExpr(Node.Expr.Until node) {
        return "do " + visit(node.body) + " until (" + visit(node.condition) + ")";
    }

    @Override
    public String visitIfExpr(Node.Expr.If node) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < node.elifs.size(); i++) {
            builder.append(" elif (" + visit(node.elifs.get(i).a) + ") " + visit(node.elifs.get(i).b));
        }
        return "if (" + visit(node.condition) + ") " + visit(node.body) + builder + (node.elseBody == null ? "" : " else " + visit(node.elseBody));
    }
}
