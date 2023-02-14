package viva.core.parsers;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import viva.base.common.Environment;
import viva.base.common.TokenType;
import viva.base.common.ast.Node;
import viva.base.parsers.AbstractExprParser;
import viva.utils.Couple;

public class VivaExprParser extends AbstractExprParser {
    private static final Map<TokenType, Integer> UNARY_OPERATOR_PRECEDENCE = new EnumMap<>(TokenType.class);
    private static final Map<TokenType, Integer> BINARY_OPERATOR_PRECEDENCE = new EnumMap<>(TokenType.class);
    
    private static final boolean RASSOC = true;
    private static final boolean LASSOC = false;

    private static final Map<TokenType, Boolean> UNARY_OPERATOR_ASSOCIATIVITY = new EnumMap<>(TokenType.class);
    private static final Map<TokenType, Boolean> BINARY_OPERATOR_ASSOCIATIVITY = new EnumMap<>(TokenType.class);

    private static final TokenType[] LITERAL_TYPES = {
        TokenType.INTEGER_LITERAL,
        TokenType.FLOAT_LITERAL,
        TokenType.BOOLEAN_LITERAL,
        TokenType.CHARACTER_LITERAL,
        TokenType.STRING_LITERAL};

    private static final TokenType[] UNARY_OPERATORS = {
        TokenType.LOGICAL_NOT,
        TokenType.BITWISE_NOT,
        TokenType.ADD,
        TokenType.SUB};        

    private static final TokenType[] BINARY_OPERATORS = {
        TokenType.ADD,
        TokenType.SUB,
        TokenType.MUL,
        TokenType.DIV,
        TokenType.ASSIGN,
        TokenType.EQUAL,
        TokenType.NOT_EQUAL,
        TokenType.MORE_THAN,
        TokenType.LESS_THAN,
        TokenType.MORE_THAN_EQUAL,
        TokenType.LESS_THAN_EQUAL,
        TokenType.LOGICAL_AND,
        TokenType.LOGICAL_XOR,
        TokenType.LOGICAL_XOR,
        TokenType.BITWISE_AND,
        TokenType.BITWISE_NOT,
        TokenType.BITWISE_XOR,
        TokenType.BITWISE_OR};

    private static final TokenType[] PRIMARY_FIRSTS = {
        TokenType.IDENTIFIER,
        TokenType.INTEGER_LITERAL,
        TokenType.FLOAT_LITERAL,
        TokenType.BOOLEAN_LITERAL,
        TokenType.CHARACTER_LITERAL,
        TokenType.STRING_LITERAL,
        TokenType.LOGICAL_NOT,
        TokenType.BITWISE_NOT,
        TokenType.ADD,
        TokenType.SUB,
        TokenType.WHILE,
        TokenType.DO,
        TokenType.IF,
        TokenType.LBRACE,
        TokenType.LPAREN};

    public VivaExprParser(VivaParser parser) {
        super(parser);
    }

    private static boolean isUnaryRightAssociative(TokenType type) {
        return UNARY_OPERATOR_ASSOCIATIVITY.getOrDefault(type, false);
    }

    private static boolean isBinaryRightAssociative(TokenType type) {
        return BINARY_OPERATOR_ASSOCIATIVITY.getOrDefault(type, false);
    }

    private static int unaryPrecedenceOf(TokenType type) {
        return UNARY_OPERATOR_PRECEDENCE.getOrDefault(type, -1);
    }
    
    private static int binaryPrecedenceOf(TokenType type) {
        return BINARY_OPERATOR_PRECEDENCE.getOrDefault(type, -1);
    }

    @Override
    public Node.Expr parseExpr() {
        return parseExpr(0);
    }
    
    private Node.Expr parsePrimary() {
        if (matches(LITERAL_TYPES)) {
            next();
            return new Node.Expr.Literal(read(-1));
        } else if (matches(TokenType.IDENTIFIER)) {
            next();
            return new Node.Expr.Variable(read(-1));
        } else if (matches(UNARY_OPERATORS)) {
            next();
            if (isUnaryRightAssociative(read(-1).type)) {
                return new Node.Expr.Unary(read(-1), parseExpr(unaryPrecedenceOf(read(-1).type)));
            } else {
                return new Node.Expr.Unary(read(-1), parseExpr(unaryPrecedenceOf(read(-1).type) + 1));
            }
        } else if (matches(TokenType.WHILE)) {
            return parseWhileExpr();
        } else if (matches(TokenType.DO)) {
            return parseUntilExpr();
        } else if (matches(TokenType.IF)) {
            return parseIfExpr();
        } else if (matches(TokenType.LBRACE)) {
            return parseBlockExpr();
        } else if (matches(TokenType.LPAREN)) {
            return parseExpr(0);
        } else {
            expected(PRIMARY_FIRSTS);
            return null;
        }
    }
    
    private Node.Expr parseExpr(int precedence) {
        Node.Expr lhs = parsePrimary();

        while (!eos() && matches(BINARY_OPERATORS) && binaryPrecedenceOf(read().type) >= precedence) {
            next();

            if (isBinaryRightAssociative(read(-1).type)) {
                lhs = new Node.Expr.Binary(lhs, read(-1), parseExpr(binaryPrecedenceOf(read(-1).type)));
            } else {
                lhs = new Node.Expr.Binary(lhs, read(-1), parseExpr(binaryPrecedenceOf(read(-1).type) + 1));
            }
        }
        return lhs;
    }

    private Node.Expr.Block parseBlockExpr() {
        List<Node.Stmt> body = new ArrayList<>();
        
        enterScope();
        expects(TokenType.LBRACE);
        while (!matches(TokenType.RBRACE)) {
            body.add(parseStmt());
        }
        expects(TokenType.RBRACE);

        Environment env = getLocalEnv();
        closeScope();

        return new Node.Expr.Block(env, body);
    }

    private Node.Expr.While parseWhileExpr() {
        expects(TokenType.WHILE);
        expects(TokenType.LPAREN);
        
        Node.Expr condition = parseExpr();
        
        expects(TokenType.RPAREN);
        
        Node.Stmt body = parseStmt();
         
        return new Node.Expr.While(condition, body);
    }
    
    private Node.Expr.Until parseUntilExpr() {
        expects(TokenType.DO);
        
        Node.Stmt body = parseStmt();

        expects(TokenType.UNTIL);

        expects(TokenType.LPAREN);

        Node.Expr condition = parseExpr();
                            
        expects(TokenType.RPAREN);                    
                        
        return new Node.Expr.Until(condition, body);
    }

    private Node.Expr.If parseIfExpr() {
        expects(TokenType.IF);
        expects(TokenType.LPAREN);
        
        Node.Expr condition = parseExpr();
        
        expects(TokenType.RPAREN);
        
        Node.Expr body = parseExpr();
        
        List<Couple<Node.Expr, Node.Expr>> elifs = new ArrayList<>();
        while (matches(TokenType.ELIF)) { 
            expects(TokenType.ELIF);
            expects(TokenType.LPAREN);
            
            Node.Expr elifCondition = parseExpr();
            
            expects(TokenType.RPAREN);

            Node.Expr elifBody = parseExpr();

            elifs.add(new Couple<>(elifCondition, elifBody));
        }
        
        Node.Expr elseBody = null;
        
        if (matches(TokenType.ELSE)) {
            next();
            
            elseBody = parseExpr();
        }

        return new Node.Expr.If(condition, body, elifs, elseBody);
    }

    static {
        UNARY_OPERATOR_PRECEDENCE.put(TokenType.ADD,                12);
        UNARY_OPERATOR_PRECEDENCE.put(TokenType.SUB,                12);
        UNARY_OPERATOR_PRECEDENCE.put(TokenType.LOGICAL_NOT,        12);
        UNARY_OPERATOR_PRECEDENCE.put(TokenType.BITWISE_NOT,        12);

        BINARY_OPERATOR_PRECEDENCE.put(TokenType.MUL,               11);
        BINARY_OPERATOR_PRECEDENCE.put(TokenType.DIV,               11);
        BINARY_OPERATOR_PRECEDENCE.put(TokenType.ADD,               10);
        BINARY_OPERATOR_PRECEDENCE.put(TokenType.SUB,               10);
        BINARY_OPERATOR_PRECEDENCE.put(TokenType.MORE_THAN,         8);
        BINARY_OPERATOR_PRECEDENCE.put(TokenType.MORE_THAN_EQUAL,   8);
        BINARY_OPERATOR_PRECEDENCE.put(TokenType.LESS_THAN,         8);
        BINARY_OPERATOR_PRECEDENCE.put(TokenType.LESS_THAN_EQUAL,   8);
        BINARY_OPERATOR_PRECEDENCE.put(TokenType.EQUAL,             7);
        BINARY_OPERATOR_PRECEDENCE.put(TokenType.NOT_EQUAL,         7);
        BINARY_OPERATOR_PRECEDENCE.put(TokenType.BITWISE_AND,       6);
        BINARY_OPERATOR_PRECEDENCE.put(TokenType.BITWISE_XOR,       5);
        BINARY_OPERATOR_PRECEDENCE.put(TokenType.BITWISE_OR,        4);
        BINARY_OPERATOR_PRECEDENCE.put(TokenType.LOGICAL_AND,       3);
        BINARY_OPERATOR_PRECEDENCE.put(TokenType.LOGICAL_XOR,       2);
        BINARY_OPERATOR_PRECEDENCE.put(TokenType.LOGICAL_OR,        1);
        BINARY_OPERATOR_PRECEDENCE.put(TokenType.ASSIGN,            0);

        UNARY_OPERATOR_ASSOCIATIVITY.put(TokenType.ADD,                 LASSOC);
        UNARY_OPERATOR_ASSOCIATIVITY.put(TokenType.SUB,                 LASSOC);
        UNARY_OPERATOR_ASSOCIATIVITY.put(TokenType.LOGICAL_NOT,         LASSOC);
        UNARY_OPERATOR_ASSOCIATIVITY.put(TokenType.BITWISE_NOT,         LASSOC);

        BINARY_OPERATOR_ASSOCIATIVITY.put(TokenType.MUL,                LASSOC);
        BINARY_OPERATOR_ASSOCIATIVITY.put(TokenType.DIV,                LASSOC);
        BINARY_OPERATOR_ASSOCIATIVITY.put(TokenType.ADD,                LASSOC);
        BINARY_OPERATOR_ASSOCIATIVITY.put(TokenType.SUB,                LASSOC);
        BINARY_OPERATOR_ASSOCIATIVITY.put(TokenType.MORE_THAN,          LASSOC);
        BINARY_OPERATOR_ASSOCIATIVITY.put(TokenType.MORE_THAN_EQUAL,    LASSOC);
        BINARY_OPERATOR_ASSOCIATIVITY.put(TokenType.LESS_THAN,          LASSOC);
        BINARY_OPERATOR_ASSOCIATIVITY.put(TokenType.LESS_THAN_EQUAL,    LASSOC);
        BINARY_OPERATOR_ASSOCIATIVITY.put(TokenType.EQUAL,              LASSOC);
        BINARY_OPERATOR_ASSOCIATIVITY.put(TokenType.NOT_EQUAL,          LASSOC);
        BINARY_OPERATOR_ASSOCIATIVITY.put(TokenType.BITWISE_AND,        LASSOC);
        BINARY_OPERATOR_ASSOCIATIVITY.put(TokenType.BITWISE_XOR,        LASSOC);
        BINARY_OPERATOR_ASSOCIATIVITY.put(TokenType.BITWISE_OR,         LASSOC);
        BINARY_OPERATOR_ASSOCIATIVITY.put(TokenType.LOGICAL_AND,        LASSOC);
        BINARY_OPERATOR_ASSOCIATIVITY.put(TokenType.LOGICAL_XOR,        LASSOC);
        BINARY_OPERATOR_ASSOCIATIVITY.put(TokenType.LOGICAL_OR,         LASSOC);
        BINARY_OPERATOR_ASSOCIATIVITY.put(TokenType.ASSIGN,             RASSOC);
    }
}
