package com.blamejared.searchables.api.context;

import com.blamejared.searchables.lang.expression.type.ComponentExpression;
import com.blamejared.searchables.lang.expression.type.GroupingExpression;
import com.blamejared.searchables.lang.expression.type.LiteralExpression;
import com.blamejared.searchables.lang.expression.type.PairedExpression;
import com.blamejared.searchables.lang.expression.visitor.Visitor;

public class ContextVisitor<T> implements Visitor<SearchContext<T>> {
    
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