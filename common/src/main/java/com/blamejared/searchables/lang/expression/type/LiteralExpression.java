package com.blamejared.searchables.lang.expression.type;

import com.blamejared.searchables.lang.expression.Expression;
import com.blamejared.searchables.lang.expression.visitor.ContextAwareVisitor;
import com.blamejared.searchables.lang.expression.visitor.Visitor;

public class LiteralExpression extends Expression {
    
    private final String value;
    private final String displayValue;
    
    public LiteralExpression(final String value, final String displayValue) {
        
        this.value = value;
        this.displayValue = displayValue;
    }
    
    @Override
    public <R> R accept(final Visitor<R> visitor) {
        
        return visitor.visitLiteral(this);
    }
    
    @Override
    public <R, C> R accept(final ContextAwareVisitor<R, C> visitor, final C context) {
        
        return visitor.visitLiteral(this, context);
    }
    
    public String value() {
        
        return value;
    }
    
    public String displayValue() {
        
        return displayValue;
    }
    
    @Override
    public String toString() {
        
        return value;
    }
    
}
