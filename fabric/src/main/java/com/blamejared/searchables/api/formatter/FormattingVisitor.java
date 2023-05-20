package com.blamejared.searchables.api.formatter;

import com.blamejared.searchables.api.SearchableType;
import com.blamejared.searchables.api.TokenRange;
import com.blamejared.searchables.lang.StringSearcher;
import com.blamejared.searchables.lang.expression.type.ComponentExpression;
import com.blamejared.searchables.lang.expression.type.GroupingExpression;
import com.blamejared.searchables.lang.expression.type.LiteralExpression;
import com.blamejared.searchables.lang.expression.type.PairedExpression;
import com.blamejared.searchables.lang.expression.visitor.ContextAwareVisitor;
import com.mojang.datafixers.util.Pair;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * Applies style formatting for an {@link net.minecraft.client.gui.components.EditBox}, intended to be passed into {@link net.minecraft.client.gui.components.EditBox#setFormatter(BiFunction)}.
 */
public class FormattingVisitor implements ContextAwareVisitor<TokenRange, FormattingContext>, Consumer<String>, BiFunction<String, Integer, FormattedCharSequence> {
    
    private final SearchableType<?> type;
    
    private final List<Pair<TokenRange, Style>> tokens = new ArrayList<>();
    private TokenRange lastRange = TokenRange.at(0);
    
    public FormattingVisitor(final SearchableType<?> type) {
        
        this.type = type;
    }
    
    /**
     * Resets this visitor to a state that allows it to run again.
     */
    public void reset() {
        
        tokens.clear();
        lastRange = TokenRange.at(0);
    }
    
    public List<Pair<TokenRange, Style>> tokens() {
        
        return tokens;
    }
    
    /**
     * Gets the {@link Optional<Pair>} of {@link TokenRange} and {@link Style} at the given position.
     *
     * @param position The current cursor position.
     *
     * @return An {@link Optional<Pair>} of {@link TokenRange} and {@link Style} at the given position, or an empty optional if out of bounds.
     */
    public Optional<Pair<TokenRange, Style>> tokenAt(final int position) {
        
        return tokens.stream()
                .filter(range -> range.getFirst().contains(position))
                .findFirst();
    }
    
    @Override
    public TokenRange visitGrouping(final GroupingExpression expr, final FormattingContext context) {
        
        TokenRange leftRange = expr.left().accept(this, context);
        tokens.add(Pair.of(getAndPushRange(), context.style()));
        TokenRange rightRange = expr.right().accept(this, context);
        return TokenRange.encompassing(leftRange, rightRange);
    }
    
    @Override
    public TokenRange visitComponent(final ComponentExpression expr, final FormattingContext context) {
        
        boolean valid = context.valid() && expr.left() instanceof LiteralExpression && expr.right() instanceof LiteralExpression;
        TokenRange leftRange = expr.left().accept(this, FormattingContext.key(FormattingConstants.KEY, valid));
        tokens.add(Pair.of(getAndPushRange(), context.style(valid)));
        TokenRange rightRange = expr.right().accept(this, FormattingContext.literal(FormattingConstants.TERM, valid));
        return TokenRange.encompassing(leftRange, rightRange);
    }
    
    @Override
    public TokenRange visitLiteral(final LiteralExpression expr, final FormattingContext context) {
        
        Style style = context.style();
        if(!context.valid() || context.isKey() && !type.components().containsKey(expr.value())) {
            style = FormattingConstants.INVALID;
        }
        TokenRange range = getAndPushRange(expr.displayValue().length());
        tokens.add(Pair.of(range, style));
        return range;
    }
    
    @Override
    public TokenRange visitPaired(final PairedExpression expr, final FormattingContext context) {
        
        TokenRange leftRange = expr.first().accept(this, context);
        TokenRange rightRange = expr.second().accept(this, context);
        return TokenRange.encompassing(leftRange, rightRange);
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
        StringSearcher.search(search, this, FormattingContext.empty());
    }
    
    @Override
    public FormattedCharSequence apply(final String currentString, final Integer offset) {
        
        List<FormattedCharSequence> sequences = new ArrayList<>();
        int index = 0;
        for(Pair<TokenRange, Style> token : tokens) {
            TokenRange range = token.getFirst();
            int subEnd = Math.max(range.start() - offset, 0);
            if(subEnd >= currentString.length()) {
                break;
            }
            
            int subStart = Math.min(range.end() - offset, currentString.length());
            if(subStart > 0) {
                sequences.add(FormattedCharSequence.forward(currentString.substring(index, subEnd), token.getSecond()));
                sequences.add(FormattedCharSequence.forward(currentString.substring(subEnd, subStart), token.getSecond()));
                index = subStart;
            }
        }
        sequences.add(FormattedCharSequence.forward(currentString.substring(index), Style.EMPTY));
        
        return FormattedCharSequence.composite(sequences);
    }
    
    
}
