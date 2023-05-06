package com.blamejared.searchables.api;

import net.minecraft.Util;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;
import java.util.StringJoiner;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

public class SearchableComponent<T> {
    
    private static final String STRING_CHARACTERS = "'\"`";
    // A user can't search for a term that contains at-least one of each string character,
    // as it will be impossible to tokenize as the engine doesn't have escaped quotes (yet), so lets filter them out.
    public static final Predicate<String> VALID_SUGGESTION = s -> {
        int quoteCount = 0;
        for(int i = 0; i < STRING_CHARACTERS.length(); i++) {
            if(StringUtils.contains(s, STRING_CHARACTERS.charAt(i))) {
                quoteCount++;
            }
        }
        return quoteCount < 3;
    };
    
    // If a term contains a quote string or a space, we should quote it for accuracy.
    public static final Function<String, String> QUOTE = Util.memoize(s -> {
        if(StringUtils.containsNone(s, STRING_CHARACTERS + " ")) {
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
    
    private SearchableComponent(String key, Function<T, Optional<String>> toString, BiPredicate<T, String> filter) {
        
        this.key = key;
        this.toString = toString.andThen(s -> s.filter(VALID_SUGGESTION));
        this.filter = filter;
    }
    
    public static <T> SearchableComponent<T> create(String key, BiPredicate<T, String> filter) {
        
        return create(key, t -> Optional.empty(), filter);
    }
    
    public static <T> SearchableComponent<T> create(String key, Function<T, Optional<String>> toString, BiPredicate<T, String> filter) {
        
        return new SearchableComponent<>(key, toString, filter);
    }
    
    public static <T> SearchableComponent<T> create(String key, Function<T, Optional<String>> toString) {
        
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
