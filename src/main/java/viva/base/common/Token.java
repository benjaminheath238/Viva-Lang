package viva.base.common;

import lombok.AllArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@ToString
public class Token {
    public final String lexeme;

    public final TokenType type;

    public final int line;
    public final int column;
}
