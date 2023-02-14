package viva.utils;

public class LexerUtils {
    private LexerUtils() {}

    public static boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    public static boolean isAlpha(char c) {
        return c >= 'a' && c <= 'z'
            || c >= 'A' && c <= 'Z';
    }

    public static boolean isAlphaDigit(char c) {
        return isDigit(c) || isAlpha(c);
    }

    public static boolean isIdentHead(char c) {
        return isAlpha(c) || c == '$' || c == '_';
    }

    public static boolean isIdentBody(char c) {
        return isAlphaDigit(c) || c == '$' || c == '_';
    }

    public static boolean isValidString(char c) {
        return c != '\n' && c > '\31' && c != '\127' && c != '\"';
    }

    public static boolean isValidChar(char c) {
        return c != '\n' && c > '\31' && c != '\127' && c != '\'';
    }
}
