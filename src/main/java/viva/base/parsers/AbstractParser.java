package viva.base.parsers;

import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import viva.base.common.Environment;
import viva.base.common.Token;
import viva.base.common.TokenType;
import viva.base.common.ast.Node;

public abstract class AbstractParser {
    private final List<Token> input;

    private int index;

    @Getter
    private Node output;

    @Getter
    private int errors;

    private Environment global;
    private Environment local;

    private AbstractExprParser exprParser;
    private AbstractStmtParser stmtParser;

    protected AbstractParser(List<Token> input, AbstractExprParser exprParser, AbstractStmtParser stmtParser) {
        this.input = input;

        this.index = 0;

        this.errors = 0;

        this.global = new Environment(null);
        this.local = global;

        this.exprParser = exprParser;
        this.stmtParser = stmtParser;

        this.exprParser.parser = this;
        this.stmtParser.parser = this;
    }

    protected boolean eos(int lookahead) {
        return index + lookahead >= input.size();
    }

    protected boolean eos() {
        return eos(0);
    }

    protected void next() {
        index++;
    }

    protected Token read(int lookahead) {
        if (eos(lookahead)) {
            return null;
        } else {
            return input.get(index + lookahead);
        }
    }

    protected Token read() {
        return read(0);
    }

    protected boolean matches(int lookahead, TokenType... types) {
        for (TokenType type : types) {
            if (!eos(lookahead) && read(lookahead).type == type) {
                return true;
            }
        }

        return false;
    }

    protected boolean matches(TokenType... types) {
        return matches(0, types);
    }

    protected void expected(int lookahead, TokenType... types) {
        errors++;
        System.out.printf("[%s:%s] Expected on of %s but got %s%n",
            read(lookahead).line,
            read(lookahead).column,
            Arrays.toString(types),
            read(lookahead).type);
    }

    protected void expected(TokenType... types) {
        expected(0, types);
    }

    protected void expects(int lookahead, TokenType... types) {
        if (matches(lookahead, types)) {
            next();
        } else {
            expected(lookahead, types);
        }
    }
    
    protected void expects(TokenType... types) {
        expects(0, types);
    }

    protected void setOutput(Node output) {
        this.output = output;
    }

    protected void enterScope() {
        this.local = new Environment(this.local);
    }

    protected void closeScope() {
        this.local = this.local.getParent();
    }

    protected Environment getLocalEnv() {
        return local;
    }

    protected Environment getGlobalEnv() {
        return global;
    }

    public Node.Expr parseExpr() {
        return exprParser.parseExpr();
    }

    public Node.Stmt parseStmt() {
        return stmtParser.parseStmt();
    }

    public abstract void parse();
}
