package com.blamejared.searchables.lang.expression.type;

import com.blamejared.searchables.lang.expression.Expression;
import com.blamejared.searchables.lang.expression.visitor.ContextAwareVisitor;
import com.blamejared.searchables.lang.expression.visitor.Visitor;

public class PairedExpression extends Expression {
    
    private final Expression first;
    private final Expression second;
    
    public PairedExpression(final Expression first, final Expression second) {
        
        this.first = first;
        this.second = second;
    }
    
    public Expression first() {
        
        return first;
    }
    
    public Expression second() {
        
        return second;
    }
    
    @Override
    public <R> R accept(final Visitor<R> visitor) {
        
        return visitor.visitPaired(this);
    }
    
    @Override
    public <R, C> R accept(final ContextAwareVisitor<R, C> visitor, final C context) {
        
        return visitor.visitPaired(this, context);
    }
    
}
