package viva.core.lexers;

import java.util.HashMap;
import java.util.Map;

import viva.base.common.TokenType;
import viva.base.lexers.AbstractLexer;
import viva.utils.LexerUtils;

public class VivaLexer extends AbstractLexer {
    private static final Map<String, TokenType> RESERVED_WORDS = new HashMap<>();

    public VivaLexer(String input) {
        super(input);            
    }

    @Override
    protected void addIdentifier(String lexeme) {
        add(token(RESERVED_WORDS.getOrDefault(lexeme, TokenType.IDENTIFIER), lexeme, -lexeme.length()));
    }

    @Override
    public void tokenize() {
        while (!eos()) {
            reset();

            switch (read()) {
                case '\t': {
                    nextColumn();
                    break;
                } case ' ': {
                    nextColumn();
                    break;
                } case '\r': {
                    nextColumn();
                    break;
                } case '\n': {
                    nextLine();
                    break;
                } case '~': {
                    addChars(TokenType.BITWISE_NOT);
                    break;
                } case '&':{
                    addChars(TokenType.BITWISE_AND);
                    break;
                } case '^': {
                    addChars(TokenType.BITWISE_XOR);
                    break;
                } case '|': {
                    addChars(TokenType.BITWISE_OR);
                    break;
                } case ':':{
                    addChars(TokenType.COLON);
                    break;
                } case ';': {
                    addChars(TokenType.SEMI);
                    break;
                } case ',': {
                    addChars(TokenType.COMMA);
                    break;
                } case '{': {
                    addChars(TokenType.LBRACE);
                    break;
                } case '}':{
                    addChars(TokenType.RBRACE);
                    break;
                } case '(': {
                    addChars(TokenType.LPAREN);
                    break;
                } case ')': {
                    addChars(TokenType.RPAREN);
                    break;
                } case '[': {
                    addChars(TokenType.LBRACK);
                    break;
                } case ']': {
                    addChars(TokenType.RBRACK);
                    break;
                } case '=': {
                    switch (read(1)) {
                        case '=': {
                            addChars(TokenType.EQUAL, 2);
                            break;
                        } case '>': {
                            addChars(TokenType.WIDE_ARROW, 2);
                            break;
                        } default: {
                            addChars(TokenType.ASSIGN);
                            break;
                        }
                    }
                    break;
                } case '!': {
                    switch (read(1)) {
                        case '=': {
                            addChars(TokenType.NOT_EQUAL, 2);
                            break;
                        } default: {
                            error();
                            nextColumn();
                            break;
                        }
                    }
                    break;
                } case '>': {
                    switch (read(1)) {
                        case '=': {
                            addChars(TokenType.MORE_THAN_EQUAL, 2);
                            break;
                        } default: {
                            addChars(TokenType.MORE_THAN);
                            break;
                        }
                    }
                    break;
                } case '<': {
                    switch (read(1)) {
                        case '=': {
                            addChars(TokenType.LESS_THAN_EQUAL, 2);
                            break;
                        } default: {
                            addChars(TokenType.LESS_THAN);
                            break;
                        }
                    }
                    break;
                } case '+': {
                    addChars(TokenType.ADD);
                    break;
                } case '-': {
                    switch (read(1)) {
                        case '>': {
                            addChars(TokenType.THIN_ARROW, 2);
                            break;
                        } default: {
                            addChars(TokenType.SUB);
                            break;
                        }
                    }
                    break;
                } case '*': {
                    addChars(TokenType.MUL);
                    break;
                } case '/': {
                    addChars(TokenType.DIV);
                    break;
                } case '\"': {
                    nextColumn();
                    while (!eos() && LexerUtils.isValidString(read())) {
                        nextColumn();
                    }
                    nextColumn();
                    
                    addLiteral(TokenType.STRING_LITERAL);
                    break;
                } case '\'': {
                    nextColumn();
                    while (!eos() && LexerUtils.isValidChar(read())) {
                        nextColumn();
                    }
                    nextColumn();

                    addLiteral(TokenType.CHARACTER_LITERAL);
                    break;
                } default: {
                    if (LexerUtils.isDigit(read())) {
                        while (!eos() && LexerUtils.isDigit(read())) {
                            nextColumn();
                        }

                        if (read() == '.') {
                            while (!eos() && LexerUtils.isDigit(read())) {
                                nextColumn();
                            }

                            addLiteral(TokenType.FLOAT_LITERAL);
                        } else {
                            addLiteral(TokenType.INTEGER_LITERAL);
                        }
                    } else if (LexerUtils.isIdentHead(read())) {
                        while (!eos() && LexerUtils.isIdentBody(read())) {
                            nextColumn();
                        }

                        addIdentifier();
                    } else {
                        error();
                        nextColumn();
                    }
                    break;
                }
            }
        }
    }

    static {
        RESERVED_WORDS.put("proc",        TokenType.PROC);
        RESERVED_WORDS.put("class",       TokenType.CLASS);
        RESERVED_WORDS.put("struct",      TokenType.STRUCT);
        RESERVED_WORDS.put("let",         TokenType.LET);
        RESERVED_WORDS.put("return",      TokenType.RETURN);
        RESERVED_WORDS.put("continue",    TokenType.CONTINUE);
        RESERVED_WORDS.put("break",       TokenType.BREAK);
        RESERVED_WORDS.put("while",       TokenType.WHILE);
        RESERVED_WORDS.put("until",       TokenType.UNTIL);
        RESERVED_WORDS.put("do",          TokenType.DO);
        RESERVED_WORDS.put("if",          TokenType.IF);
        RESERVED_WORDS.put("elif",        TokenType.ELIF);
        RESERVED_WORDS.put("else",        TokenType.ELSE);
        RESERVED_WORDS.put("true",        TokenType.BOOLEAN_LITERAL);
        RESERVED_WORDS.put("false",       TokenType.BOOLEAN_LITERAL);
        RESERVED_WORDS.put("not",         TokenType.LOGICAL_NOT);
        RESERVED_WORDS.put("and",         TokenType.LOGICAL_AND);
        RESERVED_WORDS.put("xor",         TokenType.LOGICAL_XOR);
        RESERVED_WORDS.put("or",          TokenType.LOGICAL_OR);
    }
}
