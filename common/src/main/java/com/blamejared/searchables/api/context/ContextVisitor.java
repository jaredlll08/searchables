package com.blamejared.searchables.api.context;

import com.blamejared.searchables.lang.expression.type.*;
import com.blamejared.searchables.lang.expression.visitor.Visitor;

/**
 * Compiles a {@link SearchContext<T>}, which holds {@link SearchLiteral<T>}({@code "orange"}) and {@link SearchComponent<T>}({@code "color:orange"})
 */
public final class ContextVisitor<T> implements Visitor<SearchContext<T>> {
    
    private final SearchContext<T> context = new SearchContext<>();
    
    @Override
    public SearchContext<T> visitGrouping(final GroupingExpression expr) {
        
        expr.left().accept(this);
        expr.right().accept(this);
        return context;
    }
    
    @Override
    public SearchContext<T> visitComponent(final ComponentExpression expr) {
        
        if(expr.left() instanceof LiteralExpression leftLit && expr.right() instanceof LiteralExpression rightLit) {
            context.add(new SearchComponent<>(leftLit.value(), rightLit.value()));
        } else {
            //TODO Do we want to handle a case of 'first:second:third'?
            //This *should* be an error case, but it can also be put into a component
        }
        return context;
    }
    
    @Override
    public SearchContext<T> visitLiteral(final LiteralExpression expr) {
        
        context.add(new SearchLiteral<>(expr.value()));
        return context;
    }
    
    @Override
    public SearchContext<T> visitPaired(final PairedExpression expr) {
        
        expr.first().accept(this);
        expr.second().accept(this);
        return context;
    }
    
}