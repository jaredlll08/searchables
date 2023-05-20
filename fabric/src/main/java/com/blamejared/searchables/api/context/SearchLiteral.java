package com.blamejared.searchables.api.context;

import com.blamejared.searchables.api.SearchableComponent;
import com.blamejared.searchables.api.SearchableType;

import java.util.Objects;
import java.util.function.Predicate;

final class SearchLiteral<T> implements SearchPredicate<T> {
    
    private final String value;
    
    public SearchLiteral(String value) {
        
        this.value = value;
    }
    
    @Override
    public Predicate<T> predicateFrom(final SearchableType<T> type) {
        
        return type.defaultComponent()
                .map(SearchableComponent::filter)
                .<Predicate<T>> map(filter -> t -> filter.test(t, value()))
                .orElse(t -> true);
    }
    
    
    public String value() {
        
        return value;
    }
    
    @Override
    public boolean equals(Object o) {
        
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()) {
            return false;
        }
        SearchLiteral<?> that = (SearchLiteral<?>) o;
        return Objects.equals(value, that.value);
    }
    
    @Override
    public int hashCode() {
        
        return Objects.hash(value);
    }
    
    @Override
    public String toString() {
        
        return "SearchLiteral{" + "value='" + value + '\'' +
                '}';
    }
    
}