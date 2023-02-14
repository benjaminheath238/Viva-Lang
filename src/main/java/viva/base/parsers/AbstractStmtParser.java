package viva.base.parsers;

import viva.base.common.Environment;
import viva.base.common.Token;
import viva.base.common.TokenType;
import viva.base.common.ast.Node;

public abstract class AbstractStmtParser {
    protected AbstractParser parser;

    protected AbstractStmtParser(AbstractParser parser) {
        this.parser = parser;
    }

    public abstract Node.Stmt parseStmt();
    
    public Node.Expr parseExpr() {
        return parser.parseExpr();
    }

    protected boolean eos(int lookahead) {
        return parser.eos(lookahead);
    }

    protected boolean eos() {
        return eos(0);
    }

    protected void next() {
        parser.next();
    }

    protected Token read(int lookahead) {
        return parser.read(lookahead);
    }

    protected Token read() {
        return read(0);
    }

    protected boolean matches(int lookahead, TokenType... types) {
        return parser.matches(lookahead, types);
    }

    protected boolean matches(TokenType... types) {
        return matches(0, types);
    }

    protected void expected(int lookahead, TokenType... types) {
        parser.expected(lookahead, types);
    }

    protected void expected(TokenType... types) {
        expected(0, types);
    }

    protected void expects(int lookahead, TokenType... types) {
        parser.expects(lookahead, types);
    }
    
    protected void expects(TokenType... types) {
        expects(0, types);
    }

    public void enterScope() {
        parser.enterScope();
    }

    public void closeScope() {
        parser.closeScope();
    }

    protected Environment getLocalEnv() {
        return parser.getLocalEnv();
    }

    protected Environment getGlobalEnv() {
        return parser.getGlobalEnv();
    }
}
