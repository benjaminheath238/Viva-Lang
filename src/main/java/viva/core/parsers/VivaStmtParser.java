package viva.core.parsers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import viva.base.common.Environment;
import viva.base.common.Token;
import viva.base.common.TokenType;
import viva.base.common.ast.Node;
import viva.base.parsers.AbstractStmtParser;
import viva.utils.Couple;

public class VivaStmtParser extends AbstractStmtParser {
    public VivaStmtParser(VivaParser parser) {
        super(parser);
    }

    public Node.Stmt parseStmt() {
        switch (read().type) {
            case LBRACE: {
                return parseBlockStmt();           
            } case LET: {
                return parseDefinitionStmt();
            } case RETURN: {
                return parseReturnStmt();        
            } case CONTINUE: {
                return parseContinueStmt();
            } case BREAK: {
                return parseBreakStmt();
            } case WHILE: {
                return parseWhileStmt();
            } case DO: {
                return parseUntilStmt();
            } case IF: {
                return parseIfStmt();
            } case IDENTIFIER: {
                return new Node.Stmt.Expr(parseExpr());
            } default: {
                expected(
                    TokenType.LBRACE,
                    TokenType.LET,
                    TokenType.RETURN,
                    TokenType.CONTINUE,
                    TokenType.BREAK,
                    TokenType.WHILE,
                    TokenType.DO,
                    TokenType.IF,
                    TokenType.IDENTIFIER);
                next();
                return null;
            }
        }
    }

    private Node.Stmt.Block parseBlockStmt() {
        List<Node.Stmt> body = new ArrayList<>();
        
        enterScope();
        expects(TokenType.LBRACE);
        while (!matches(TokenType.RBRACE)) {
            body.add(parseStmt());
        }
        expects(TokenType.RBRACE);

        Environment env = getLocalEnv();
        closeScope();

        return new Node.Stmt.Block(env, body);
    }
    
    private Node.Stmt.Return parseReturnStmt() {
        expects(TokenType.RETURN);
                
        Node.Expr value = parseExpr();

        expects(TokenType.SEMI);
                
        return new Node.Stmt.Return(value);
    }

    private Node.Stmt.Continue parseContinueStmt() {
        expects(TokenType.CONTINUE);
        expects(TokenType.IDENTIFIER);
        expects(TokenType.SEMI);
        
        return new Node.Stmt.Continue(read(-2));
    }

    private Node.Stmt.Break parseBreakStmt() {
        expects(TokenType.BREAK);
        expects(TokenType.IDENTIFIER);
        expects(TokenType.SEMI);
        
        return new Node.Stmt.Break(read(-2));
    }

    private Node.Stmt.While parseWhileStmt() {
        expects(TokenType.WHILE);
        expects(TokenType.LPAREN);
        
        Node.Expr condition = parseExpr();
        
        expects(TokenType.RPAREN);
        
        Node.Stmt body = parseStmt();
         
        return new Node.Stmt.While(condition, body);
    }
    
    private Node.Stmt.Until parseUntilStmt() {
        expects(TokenType.DO);
        
        Node.Stmt body = parseStmt();

        expects(TokenType.UNTIL);

        expects(TokenType.LPAREN);

        Node.Expr condition = parseExpr();

        expects(TokenType.RPAREN);                    
        expects(TokenType.SEMI);                    
                        
        return new Node.Stmt.Until(condition, body);
    }

    private Node.Stmt.If parseIfStmt() {
        expects(TokenType.IF);
        expects(TokenType.LPAREN);
        
        Node.Expr condition = parseExpr();
        
        expects(TokenType.RPAREN);
        
        Node.Stmt body = parseStmt();
        
        List<Couple<Node.Expr, Node.Stmt>> elifs = new ArrayList<>();
        while (matches(TokenType.ELIF)) { 
            expects(TokenType.ELIF);
            expects(TokenType.LPAREN);
            
            Node.Expr elifCondition = parseExpr();
            
            expects(TokenType.RPAREN);

            Node.Stmt elifBody = parseStmt();

            elifs.add(new Couple<>(elifCondition, elifBody));
        }
        
        Node.Stmt elseBody = null;
        
        if (matches(TokenType.ELSE)) {
            next();
            
            elseBody = parseStmt();
        }

        return new Node.Stmt.If(condition, body, elifs, elseBody);
    }

    private Node.Stmt.Define parseType(Token identifier) {
        switch (read().type) {
            case PROC: {
                expects(TokenType.PROC);

                Map<Token, Token> parameters = new HashMap<>();
                
                expects(TokenType.LPAREN);
                
                while (!matches(TokenType.RPAREN)) {
                    expects(TokenType.IDENTIFIER);
                    
                    Token param = read(-1);
                    
                    expects(TokenType.COLON);
                    expects(TokenType.IDENTIFIER);
                    
                    parameters.put(param, read(-1));
                    
                    if (!matches(TokenType.RPAREN)) {
                        expects(TokenType.COMMA);
                    }
                }
                
                expects(TokenType.RPAREN);
                expects(TokenType.WIDE_ARROW);
                expects(TokenType.IDENTIFIER);
                
                Token returnType = read(-1);

                if (!matches(TokenType.ASSIGN)) {
                    expects(TokenType.SEMI);
                    return new Node.Stmt.Define.Proc(identifier, returnType, parameters, null);
                } else {
                    next();
                    return new Node.Stmt.Define.Proc(identifier, returnType, parameters, parseExpr());
                }
            } case CLASS: {
                expects(TokenType.CLASS);
                expects(TokenType.ASSIGN);
                
                return new Node.Stmt.Define.Type("class", identifier, parseExpr());
            } case STRUCT: {
                expects(TokenType.STRUCT);
                expects(TokenType.ASSIGN);

                return new Node.Stmt.Define.Type("struct", identifier, parseExpr());
            } case IDENTIFIER: {
                expects(TokenType.IDENTIFIER);

                Token type = read(-1);

                if (!matches(TokenType.ASSIGN)) {
                    expects(TokenType.SEMI);
                    return new Node.Stmt.Define.Variable(type, identifier, null);
                } else {
                    next();
                    return new Node.Stmt.Define.Variable(type, identifier, parseExpr());
                }
            } default: {
                expected(TokenType.PROC, TokenType.CLASS, TokenType.STRUCT, TokenType.IDENTIFIER);
                return null;
            }
        }
    }

    private Node.Stmt.Define parseDefinitionStmt() {
        expects(TokenType.LET);
        expects(TokenType.IDENTIFIER);
        
        Token identifier = read(-1);

        expects(TokenType.COLON);

        return parseType(identifier);
    }
}
