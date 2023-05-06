package com.blamejared.searchables.api.autcomplete;

import com.blamejared.searchables.api.TokenRange;
import com.blamejared.searchables.lang.StringSearcher;
import com.blamejared.searchables.lang.expression.type.*;
import com.blamejared.searchables.lang.expression.visitor.Visitor;

import java.util.*;
import java.util.function.Consumer;

/**
 * Generates a list of TokenRanges that can be used to split a given string into parts.
 * Mainly used to split strings for completion purposes.
 */
public class CompletionVisitor implements Visitor<TokenRange>, Consumer<String> {
    
    private final List<TokenRange> tokens = new ArrayList<>();
    private TokenRange lastRange = TokenRange.EMPTY;
    
    /**
     * Resets this visitor to a state that allows it to run again.
     */
    public void reset() {
        
        tokens.clear();
        lastRange = TokenRange.EMPTY;
    }
    
    /**
     * Reduces the tokens into their outermost parts.
     * For example the string {@code "shape:square color:red"} will be split into:
     * {@code [
     * TokenRange(0, 12, [TokenRange(0, 5), TokenRange(5, 6), TokenRange(6, 12)]),
     * TokenRange(13, 22, [TokenRange(13, 18), TokenRange(18, 19), TokenRange(19, 22)])
     * ]}
     */
    protected void reduceTokens() {
        // Can this be done while visiting?
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
    
    /**
     * Gets the tokens in this visitor.
     *
     * @return The tokens in this visitor.
     */
    public List<TokenRange> tokens() {
        
        return tokens;
    }
    
    /**
     * Gets the {@link Optional<TokenRange>} at the given position.
     *
     * @param position The current cursor position.
     *
     * @return An {@link Optional<TokenRange>} at the given range, or an empty optional if out of bounds.
     */
    public Optional<TokenRange> tokenAt(final int position) {
        
        return tokens.stream()
                .filter(range -> range.contains(position))
                .findFirst();
    }
    
    /**
     * Gets the {@link TokenRange} at the given position, or {@link TokenRange#EMPTY} if out of bounds.
     *
     * @param position The current cursor position.
     *
     * @return An {@link TokenRange} at the given range, or {@link TokenRange#EMPTY} if out of bounds.
     */
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
    
    /**
     * Resets this visitor and compiles a list of {@link TokenRange} from the given String
     *
     * @param search The string to search
     */
    @Override
    public void accept(final String search) {
        
        reset();
        StringSearcher.search(search, this);
    }
    
    @Override
    public TokenRange postVisit(final TokenRange obj) {
        
        this.reduceTokens();
        return Visitor.super.postVisit(obj);
    }
    
}
