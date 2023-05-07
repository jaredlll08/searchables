package com.blamejared.searchables.lang;

import com.blamejared.searchables.lang.expression.Expression;
import com.blamejared.searchables.lang.expression.visitor.ContextAwareVisitor;
import com.blamejared.searchables.lang.expression.visitor.Visitor;

import java.util.Optional;

public class StringSearcher {
    
    /**
     * Parses the string and visits the given visitor.
     *
     * @param search  The string to search.
     * @param visitor The visitor to visit.
     *
     * @return The optional result of the visitor.
     */
    public static <T> Optional<T> search(final String search, final Visitor<T> visitor) {
        
        SLParser slParser = new SLParser(new SLScanner(search).scanTokens());
        return slParser.parse().map(expression -> expression.accept(visitor)).map(visitor::postVisit);
    }
    
    /**
     * Parses the string and visits the given visitor with context.
     *
     * @param search  The string to search.
     * @param visitor The visitor to visit.
     * @param context The extra context for the visitor.
     *
     * @return The optional result of the visitor.
     */
    public static <T, C> Optional<T> search(final String search, final ContextAwareVisitor<T, C> visitor, final C context) {
        
        SLParser slParser = new SLParser(new SLScanner(search).scanTokens());
        return slParser.parse()
                .map(expression -> expression.accept(visitor, context))
                .map(t -> visitor.postVisit(t, context));
    }
    
    /**
     * Parses the string and returns and optional {@link Expression}
     *
     * @param search The string to search.
     *
     * @return The string as an optional {@link Expression}
     */
    public static Optional<Expression> expression(final String search) {
        
        SLParser slParser = new SLParser(new SLScanner(search).scanTokens());
        return slParser.parse();
    }
    
}
