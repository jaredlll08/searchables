
package com.blamejared.searchables.api.context;

import com.blamejared.searchables.api.SearchableComponent;
import com.blamejared.searchables.api.SearchableType;

import java.util.Objects;
import java.util.function.Predicate;

final class SearchComponent<T> implements SearchPredicate<T> {
    
    private final String key;
    private final String value;
    
    public SearchComponent(String key, String value) {
        
        this.key = key;
        this.value = value;
    }
    
    @Override
    public Predicate<T> predicateFrom(final SearchableType<T> type) {
        
        return type.component(key())
                .map(SearchableComponent::filter)
                .<Predicate<T>> map(filter -> t -> filter.test(t, value()))
                .orElse(t -> true);
    }
    
    @Override
    public boolean equals(Object o) {
        
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()) {
            return false;
        }
        SearchComponent<?> that = (SearchComponent<?>) o;
        return Objects.equals(key, that.key) && Objects.equals(value, that.value);
    }
    
    @Override
    public int hashCode() {
        
        return Objects.hash(key, value);
    }
    
    @Override
    public String toString() {
        
        return "SearchComponent{" + "key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
    
    public String key() {
        
        return key;
    }
    
    public String value() {
        
        return value;
    }
    
}