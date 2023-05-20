package com.blamejared.searchables.api.context;

import com.blamejared.searchables.lang.expression.Expression;
import com.blamejared.searchables.lang.expression.type.ComponentExpression;
import com.blamejared.searchables.lang.expression.type.GroupingExpression;
import com.blamejared.searchables.lang.expression.type.LiteralExpression;
import com.blamejared.searchables.lang.expression.type.PairedExpression;
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
        
        Expression left = expr.left();
        Expression right = expr.right();
        if(left instanceof LiteralExpression && right instanceof LiteralExpression) {
            LiteralExpression leftLit = ((LiteralExpression) left);
            LiteralExpression rightLit = ((LiteralExpression) right);
            
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