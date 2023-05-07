package com.blamejared.searchables.api;

import net.minecraft.Util;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.function.*;

/**
 * A component of a {@link SearchableType<T>}.
 */
public class SearchableComponent<T> {
    
    // A user can't search for a term that contains at-least one of each string character,
    // as it will be impossible to tokenize as the engine doesn't have escaped quotes (yet), so lets filter them out.
    public static final Predicate<String> VALID_SUGGESTION = s -> {
        int quoteCount = 0;
        for(int i = 0; i < SearchablesConstants.STRING_CHARACTERS.length(); i++) {
            if(StringUtils.contains(s, SearchablesConstants.STRING_CHARACTERS.charAt(i))) {
                quoteCount++;
            }
        }
        return quoteCount < 3;
    };
    
    // If a term contains a string character or a space, we should quote it for searchability.
    public static final Function<String, String> QUOTE = Util.memoize(s -> {
        if(StringUtils.containsNone(s, SearchablesConstants.STRING_CHARACTERS + " ")) {
            return s;
        }
        char quoteChar = '"';
        while(StringUtils.contains(s, quoteChar)) {
            quoteChar = switch(quoteChar) {
                case '"' -> '\'';
                case '\'' -> '`';
                default ->
                        throw new IllegalStateException("Unable to nicely wrap {" + s + "}! Make sure to filter Strings through 'SearchableComponent#VALID_SUGGESTION'!");
            };
        }
        return StringUtils.wrap(s, quoteChar);
    });
    
    private final String key;
    
    private final Function<T, Optional<String>> toString;
    
    private final BiPredicate<T, String> filter;
    
    private SearchableComponent(final String key, final Function<T, Optional<String>> toString, final BiPredicate<T, String> filter) {
        
        this.key = key;
        this.toString = toString.andThen(s -> s.filter(VALID_SUGGESTION));
        this.filter = filter;
    }
    
    /**
     * Creates a component that will not show in auto-complete, but will still be able to be filtered
     *
     * @param key    The key for this term.
     * @param filter a {@link BiPredicate} to filter the element ({@code T}) and the given search String
     * @param <T>    The type of element that this {@link SearchableComponent<T>} handles.
     *
     * @return a new {@link SearchableComponent<T>} from the given values.
     */
    public static <T> SearchableComponent<T> create(final String key, final BiPredicate<T, String> filter) {
        
        return create(key, t -> Optional.empty(), filter);
    }
    
    /**
     * Creates a component from the given values.
     *
     * @param key      The key for this term.
     * @param toString a {@link Function} to convert a given {@code T} to an {@link Optional<String>}, used to display the "name" of an element for auto-complete
     * @param filter   a {@link BiPredicate} to filter the element ({@code T}) and the given search String
     * @param <T>      The type of element that this {@link SearchableComponent<T>} handles.
     *
     * @return a new {@link SearchableComponent<T>} from the given values.
     */
    public static <T> SearchableComponent<T> create(final String key, final Function<T, Optional<String>> toString, final BiPredicate<T, String> filter) {
        
        return new SearchableComponent<>(key, toString, filter);
    }
    
    /**
     * Creates a component from the given values, that uses the {@code toString} function to filter based on if the name of the element contains the given search string (case-insensitive)
     *
     * @param key      The key for this term.
     * @param toString a {@link Function} to convert a given {@code T} to an {@link Optional<String>}, used to display the "name" of an element for auto-complete and for filtering.
     * @param <T>      The type of element that this {@link SearchableComponent<T>} handles.
     *
     * @return a new {@link SearchableComponent<T>} from the given values.
     */
    public static <T> SearchableComponent<T> create(final String key, final Function<T, Optional<String>> toString) {
        
        return new SearchableComponent<>(key, toString, (t, search) -> toString.apply(t)
                .map(tStr -> StringUtils.containsIgnoreCase(tStr, search))
                .orElse(false));
    }
    
    public String key() {
        
        return key;
    }
    
    public BiPredicate<T, String> filter() {
        
        return filter;
    }
    
    public Function<T, Optional<String>> getToString() {
        
        return toString;
    }
    
    @Override
    public String toString() {
        
        return new StringJoiner(", ", SearchableComponent.class.getSimpleName() + "[", "]").add("key='" + key + "'")
                .toString();
    }
    
}
