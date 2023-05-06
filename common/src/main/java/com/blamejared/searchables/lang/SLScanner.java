package com.blamejared.searchables.lang;

import java.util.ArrayList;
import java.util.List;

public class SLScanner {
    
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    
    public SLScanner(final String source) {
        
        this.source = source;
    }
    
    public List<Token> scanTokens() {
        
        while(!isAtEnd()) {
            // We are at the beginning of the next lexeme.
            start = current;
            scanToken();
        }
        
        tokens.add(new Token(TokenType.EOL, "", "", start, current));
        return tokens;
    }
    
    private void scanToken() {
        
        char c = advance();
        switch(c) {
            case ' ' -> space();
            case ':' -> addToken(TokenType.COLON, ":");
            case '"' -> string('"');
            case '\'' -> string('\'');
            case '`' -> string('`');
            default -> identifier();
        }
    }
    
    private void space() {
        
        addToken(TokenType.SPACE, " ");
    }
    
    private void string(final char quote) {
        
        while(peek() != quote && !isAtEnd()) {
            advance();
        }
        
        if(isAtEnd()) {
            String value = source.substring(start + 1, current);
            // Trim the surrounding quotes.
            addToken(TokenType.STRING, value);
        } else {
            // The closing ".
            advance();
            String value = source.substring(start + 1, current - 1);
            // Trim the surrounding quotes.
            addToken(TokenType.STRING, value);
        }
    }
    
    private void identifier() {
        
        while(!isAtEnd() && peek() != ' ' && peek() != ':') {
            advance();
        }
        String value = source.substring(start, current);
        addToken(TokenType.IDENTIFIER, value);
    }
    
    private char peek() {
        
        if(isAtEnd()) {
            return '\0';
        }
        return source.charAt(current);
    }
    
    private char advance() {
        
        return source.charAt(current++);
    }
    
    private void addToken(final TokenType type, final String literal) {
        
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, start, current));
    }
    
    private boolean isAtEnd() {
        
        return current >= source.length();
    }
    
}
