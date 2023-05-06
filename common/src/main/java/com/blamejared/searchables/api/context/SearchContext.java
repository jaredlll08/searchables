package com.blamejared.searchables.api.context;

import com.blamejared.searchables.api.SearchableType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class SearchContext<T> {
    
    private final List<SearchLiteral<T>> literals;
    private final List<SearchComponent<T>> components;
    
    public SearchContext() {
        
        this.literals = new ArrayList<>();
        this.components = new ArrayList<>();
    }
    
    public Predicate<T> createPredicate(final SearchableType<T> type) {
        
        return Stream.<SearchPredicate<T>> concat(literals.stream(), components.stream())
                .map(tSearchPredicate -> tSearchPredicate.predicateFrom(type))
                .reduce(t -> true, Predicate::and);
    }
    
    public void add(final SearchLiteral<T> literal) {
        
        this.literals.add(literal);
    }
    
    public void add(final SearchComponent<T> component) {
        
        this.components.add(component);
    }
    
}