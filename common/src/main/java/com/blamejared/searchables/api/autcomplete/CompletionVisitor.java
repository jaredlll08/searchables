package com.blamejared.searchables.api.autcomplete;

import com.blamejared.searchables.api.TokenRange;
import com.blamejared.searchables.lang.StringSearcher;
import com.blamejared.searchables.lang.expression.type.*;
import com.blamejared.searchables.lang.expression.visitor.Visitor;

import java.util.*;
import java.util.function.Consumer;

public class CompletionVisitor implements Visitor<TokenRange>, Consumer<String> {
    
    private final List<TokenRange> tokens = new ArrayList<>();
    private TokenRange lastRange = TokenRange.EMPTY;
    
    public void reset() {
        
        tokens.clear();
        lastRange = TokenRange.EMPTY;
    }
    
    public List<TokenRange> tokens() {
        
        return tokens;
    }
    
    public void reduceTokens() {
        
        //TODO look into if this is even needed or if it can be done while visiting
        
        ListIterator<TokenRange> iterator = tokens.listIterator(tokens.size());
        TokenRange lastRange = null;
        while(iterator.hasPrevious()) {
            TokenRange previous = iterator.previous();
            if(lastRange == null) {
                lastRange = previous;
            } else {
                if(lastRange.covers(previous)) {
                    lastRange.addRange(previous);
                    iterator.remove();
                } else {
                    lastRange = previous;
                }
            }
        }
    }
    
    public Optional<TokenRange> tokenAt(final int position) {
        
        return tokens.stream()
                .filter(range -> range.contains(position))
                .findFirst();
    }
    
    public TokenRange rangeAt(final int position) {
        
        return tokenAt(position).orElse(TokenRange.EMPTY);
    }
    
    @Override
    public TokenRange visitGrouping(final GroupingExpression expr) {
        
        TokenRange leftRange = expr.left().accept(this);
        getAndPushRange();
        TokenRange rightRange = expr.right().accept(this);
        return TokenRange.encompassing(leftRange, rightRange);
    }
    
    @Override
    public TokenRange visitComponent(final ComponentExpression expr) {
        
        TokenRange leftRange = expr.left().accept(this);
        addToken(getAndPushRange());
        TokenRange rightRange = expr.right().accept(this);
        return addToken(TokenRange.encompassing(leftRange, rightRange));
    }
    
    @Override
    public TokenRange visitLiteral(final LiteralExpression expr) {
        
        return addToken(getAndPushRange(expr.displayValue().length()));
    }
    
    @Override
    public TokenRange visitPaired(final PairedExpression expr) {
        
        TokenRange leftRange = addToken(expr.first().accept(this));
        TokenRange rightRange = addToken(expr.second().accept(this));
        return addToken(TokenRange.encompassing(leftRange, rightRange));
    }
    
    private TokenRange addToken(final TokenRange range) {
        
        this.tokens.add(range.recalculate());
        return range;
    }
    
    private TokenRange getAndPushRange() {
        
        return getAndPushRange(1);
    }
    
    private TokenRange getAndPushRange(final int end) {
        
        TokenRange oldRange = lastRange;
        lastRange = TokenRange.between(lastRange.end(), lastRange.end() + end);
        return TokenRange.between(oldRange.end(), oldRange.end() + end);
    }
    
    @Override
    public void accept(final String search) {
        
        reset();
        StringSearcher.search(search, this);
    }
    
    @Override
    public TokenRange postVisit(TokenRange obj) {
        
        this.reduceTokens();
        return Visitor.super.postVisit(obj);
    }
    
}
