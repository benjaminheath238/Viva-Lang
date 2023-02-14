package viva.core.parsers;

import java.util.ArrayList;
import java.util.List;

import viva.base.common.Token;
import viva.base.common.ast.Node;
import viva.base.parsers.AbstractParser;

public class VivaParser extends AbstractParser {
    public VivaParser(List<Token> input) {
        super(input, new VivaExprParser(null), new VivaStmtParser(null));
    }

    private Node.Program parseProg() {
        List<Node.Stmt> body = new ArrayList<>();
        
        while (!eos()) {
            body.add(parseStmt());
        }

        return new Node.Program(getGlobalEnv(), body);
    }

    @Override
    public void parse() {
        setOutput(parseProg());
    }
}
