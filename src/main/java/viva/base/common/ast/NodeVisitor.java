package viva.base.common.ast;

public interface NodeVisitor<T> {
    public default T visit(Node node) {
        return node.accept(this);
    }

    public T visitProgram(Node.Program node);

    public T visitStmt(Node.Stmt node);

    public T visitBlockStmt(Node.Stmt.Block node);

    public T visitDefineStmt(Node.Stmt.Define node);

    public T visitDefineProcStmt(Node.Stmt.Define.Proc node);

    public T visitDefineTypeStmt(Node.Stmt.Define.Type node);

    public T visitDefineVariableStmt(Node.Stmt.Define.Variable node);

    public T visitReturnStmt(Node.Stmt.Return node);

    public T visitContinueStmt(Node.Stmt.Continue node);

    public T visitBreakStmt(Node.Stmt.Break node);

    public T visitWhileStmt(Node.Stmt.While node);

    public T visitUntilStmt(Node.Stmt.Until node);

    public T visitIfStmt(Node.Stmt.If node);

    public T visitExprStmt(Node.Stmt.Expr node);

    public T visitExpr(Node.Expr node);

    public T visitBinaryExpr(Node.Expr.Binary node);

    public T visitUnaryExpr(Node.Expr.Unary node);

    public T visitLiteralExpr(Node.Expr.Literal node);

    public T visitVariableExpr(Node.Expr.Variable node);

    public T visitProcCallExpr(Node.Expr.ProcCall node);

    public T visitAccessExpr(Node.Expr.Access node);

    public T visitReferenceExpr(Node.Expr.Reference node);

    public T visitBlockExpr(Node.Expr.Block node);

    public T visitWhileExpr(Node.Expr.While node);
    
    public T visitUntilExpr(Node.Expr.Until node);

    public T visitIfExpr(Node.Expr.If node);
}
