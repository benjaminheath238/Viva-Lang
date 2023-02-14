package viva.utils;

import viva.base.common.ast.Node;

public class DebugFormatter extends Formatter {

    public DebugFormatter() {
        super();
    }
    @Override
    public String visitProgram(Node.Program node) {
        StringBuilder builder = new StringBuilder();
        for (Node.Stmt stmt : node.body) {
            builder.append(indent(visit(stmt)));
            builder.append("\n\n");
        }
        return "env:" + node.env + "\n" + builder.toString();
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
        return "{\n" + "env:" + node.env + "\n" + builder + indent("}");
    }
}
