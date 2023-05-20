package com.blamejared.searchables.api.util;

import com.google.common.collect.Maps;

import java.util.Map;
import java.util.function.Function;

public class SearchablesUtil {
    
    public static <T, R> Function<T, R> memoize(final Function<T, R> func) {
        
        return new Function<T, R>() {
            private final Map<T, R> cache = Maps.newHashMap();
            
            public R apply(T param0x) {
                
                return this.cache.computeIfAbsent(param0x, func);
            }
            
            public String toString() {
                
                return "memoize/1[function=" + func + ", size=" + this.cache.size() + "]";
            }
        };
    }
    
}
