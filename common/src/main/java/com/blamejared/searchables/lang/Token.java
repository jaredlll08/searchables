package com.blamejared.searchables.lang;

@SuppressWarnings("ClassCanBeRecord")
public class Token {
    
    private final TokenType type;
    private final String lexeme;
    private final String literal;
    
    private final int start;
    private final int end;
    
    public Token(final TokenType type, final String lexeme, final String literal, final int start, final int end) {
        
        this.type = type;
        this.lexeme = lexeme;
        this.literal = literal;
        this.start = start;
        this.end = end;
    }
    
    @Override
    public String toString() {
        
        return "Token{" +
                "type=" + type +
                ", lexeme='" + lexeme + '\'' +
                ", literal='" + literal + '\'' +
                ", start=" + start +
                ", end=" + end +
                '}';
    }
    
    public TokenType type() {
        
        return type;
    }
    
    public String lexeme() {
        
        return lexeme;
    }
    
    public int start() {
        
        return start;
    }
    
    public int end() {
        
        return end;
    }
    
    public String literal() {
        
        return literal;
    }
    
}
