package viva.base.lexers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import viva.base.common.Token;
import viva.base.common.TokenType;

public abstract class AbstractLexer {
    private final char[] input;

    private int index;
    private int start;
    
    protected int line;
    protected int column;

    @Getter
    private List<Token> output;

    protected AbstractLexer(String input) {
        this.input = input.toCharArray();

        this.index = 0;
        this.start = 0;

        this.line = 1;
        this.column = 1;

        this.output = new ArrayList<>();
    }

    protected boolean eos(int lookahead) {
        return index + lookahead >= input.length;
    }

    protected boolean eos() {
        return eos(0);
    }

    protected void reset() {
        start = index;
    }

    protected char read(int lookahead) {
        if (eos(lookahead)) {
            return '\0';
        } else {
            return input[index + lookahead];
        }
    }

    protected char read() {
        return read(0);
    }

    protected void nextColumn() {
        index++;
        column++;
    }

    protected void nextLine() {
        index++;
        line++;
        column = 1;
    }

    protected String lexeme() {
        return new String(Arrays.copyOfRange(input, start, index));
    }

    protected Token token(TokenType type, String lexeme, int columnOffset) {
        return new Token(lexeme, type, line, column + columnOffset);
    }

    protected void add(Token token) {
        output.add(token);
    }

    protected void addChars(TokenType type, int width) {
        for (int i = 0; i < width; i++) {
            nextColumn();
        }
            
        add(token(type, lexeme(), -width));
    }

    protected void addChars(TokenType type) {
        addChars(type, 1);
    }

    protected abstract void addIdentifier(String lexeme);

    protected void addIdentifier() {
        addIdentifier(lexeme());
    }

    protected void addLiteral(TokenType type, String lexeme) {
        add(token(type, lexeme, -lexeme.length()));
    }

    protected void addLiteral(TokenType type) {
        addLiteral(type, lexeme());
    }

    protected void error(int lookahead) {
        System.out.printf("[%s:%s] Unexpected character '%s'%n", line, column, read(lookahead));
    }

    protected void error() {
        error(0);
    }

    public abstract void tokenize();
}
