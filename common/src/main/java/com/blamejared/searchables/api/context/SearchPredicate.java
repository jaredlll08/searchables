package com.blamejared.searchables.api.context;

import com.blamejared.searchables.api.SearchableType;

import java.util.function.Predicate;

interface SearchPredicate<T> {
    
    Predicate<T> predicateFrom(final SearchableType<T> type);
    
}