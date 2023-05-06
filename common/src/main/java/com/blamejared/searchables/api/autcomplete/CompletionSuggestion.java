package com.blamejared.searchables.api.autcomplete;

import com.blamejared.searchables.api.TokenRange;
import net.minecraft.network.chat.Component;

public record CompletionSuggestion(String suggestion, Component display, String suffix, TokenRange replacementRange) {
    
    public String replaceIn(final String into) {
        
        return replacementRange.replace(into, toInsert());
    }
    
    public String toInsert() {
        
        return suggestion + suffix;
    }
    
    
}
