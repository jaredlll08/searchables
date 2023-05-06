package com.blamejared.searchables.api;

import com.blamejared.searchables.api.autcomplete.CompletionSuggestion;
import com.blamejared.searchables.api.context.ContextVisitor;
import com.blamejared.searchables.api.context.SearchContext;
import com.blamejared.searchables.lang.StringSearcher;
import com.google.common.base.CharMatcher;
import com.google.common.collect.ImmutableMap;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public final class SearchableType<T> {
    
    private final Map<String, SearchableComponent<T>> components;
    @Nullable
    private final SearchableComponent<T> defaultComponent;
    
    private SearchableType(final Map<String, SearchableComponent<T>> components, @Nullable final SearchableComponent<T> defaultComponent) {
        
        this.components = components;
        this.defaultComponent = defaultComponent;
    }
    
    public Map<String, SearchableComponent<T>> components() {
        
        return components;
    }
    
    public Optional<SearchableComponent<T>> component(final String key) {
        
        return Optional.ofNullable(components.get(key));
    }
    
    public Optional<SearchableComponent<T>> defaultComponent() {
        
        return Optional.ofNullable(defaultComponent);
    }
    
    public List<CompletionSuggestion> getSuggestionsFor(final List<T> entries, final String currentToken, final int position, final TokenRange replacementRange) {
        
        final TokenRange suggestionRange = replacementRange.rangeAtPosition(position);
        final String suggestionFrom = suggestionRange.substring(currentToken, position);
        final int suggestionIndex = replacementRange.rangeIndexAtPosition(position);
        return switch(suggestionIndex) {
            case 0 -> getSuggestionsForComponent(
                    suggestionFrom,
                    replacementRange.simplify());
            case 1 -> getSuggestionsForTerm(
                    entries,
                    replacementRange.range(0).substring(currentToken),
                    "",
                    replacementRange.simplify());
            case 2 -> getSuggestionsForTerm(
                    entries,
                    replacementRange.range(0).substring(currentToken),
                    suggestionFrom,
                    replacementRange.simplify());
            default -> List.of();
        };
    }
    
    public List<CompletionSuggestion> getSuggestionsForComponent(final String componentName, final TokenRange replacementRange) {
        
        return this.components()
                .keySet()
                .stream()
                .filter(s -> StringUtils.startsWithIgnoreCase(s, componentName))
                .sorted(Comparator.naturalOrder())
                .map(s -> new CompletionSuggestion(s, Component.literal(s), ":", replacementRange))
                .distinct()
                .collect(Collectors.toList());
    }
    
    public List<CompletionSuggestion> getSuggestionsForTerm(final List<T> entries, final String componentName, final String current, final TokenRange replacementRange) {
        
        final Function<T, Optional<String>> mapper = this.component(componentName)
                .map(SearchableComponent::getToString)
                .orElseGet(() -> t -> Optional.empty());
        final boolean startsWithQuote = !current.isEmpty() && CharMatcher.anyOf("`'\"").matches(current.charAt(0));
        String termString = startsWithQuote ? current.substring(1) : current;
        return entries.stream()
                .map(mapper)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(s -> StringUtils.startsWithIgnoreCase(s, termString))
                .sorted(Comparator.naturalOrder())
                .map(SearchableComponent.QUOTE)
                .map(s -> new CompletionSuggestion(componentName + ":" + s, Component.literal(s), " ", replacementRange))
                .distinct()
                .collect(Collectors.toList());
        
    }
    
    public List<T> filterEntries(final List<T> entries, final String search) {
        
        return filterEntries(entries, search, t -> true);
    }
    
    public List<T> filterEntries(final List<T> entries, final String search, final Predicate<T> extraPredicate) {
        
        Optional<SearchContext<T>> context = StringSearcher.search(search, new ContextVisitor<>());
        return entries.stream()
                .filter(context.map(tSearchContext -> tSearchContext.createPredicate(this))
                        .orElse(t -> true)
                        .and(extraPredicate))
                .toList();
    }
    
    public static class Builder<T> {
        
        private final ImmutableMap.Builder<String, SearchableComponent<T>> components;
        
        @Nullable
        private SearchableComponent<T> defaultComponent;
        
        public Builder() {
            
            this.components = ImmutableMap.builder();
            this.defaultComponent = null;
        }
        
        public Builder<T> component(final SearchableComponent<T> component) {
            
            return component(component.key(), component);
        }
        
        public Builder<T> component(final String key, final SearchableComponent<T> component) {
            
            components.put(key, component);
            return this;
        }
        
        public Builder<T> defaultComponent(final SearchableComponent<T> component) {
            
            return defaultComponent(component.key(), component);
        }
        
        public Builder<T> defaultComponent(final String key, final SearchableComponent<T> component) {
            
            if(defaultComponent != null) {
                throw new IllegalStateException("Cannot mark multiple components as a default component!");
            }
            components.put(key, component);
            defaultComponent = component;
            return this;
        }
        
        public SearchableType<T> build() {
            //TODO An event could be fired here to allow 3rd parties to add new components
            return new SearchableType<>(components.buildOrThrow(), defaultComponent);
        }
        
    }
    
}
