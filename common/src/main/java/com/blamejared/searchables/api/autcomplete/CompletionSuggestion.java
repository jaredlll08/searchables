package com.blamejared.searchables.api.autcomplete;

import com.blamejared.searchables.api.TokenRange;
import net.minecraft.network.chat.Component;

/**
 * Represents a suggestion to be shown in {@link AutoComplete}
 *
 * @param suggestion       The suggestion to insert.
 * @param display          The component to display to the user.
 * @param suffix           The text to insert of the {@code suggestion}. Generally either {@literal ":"} or {@literal " "}
 * @param replacementRange The range of the current String to replace with this suggestion. Given the string {@code "sha"} and suggestion of {@code "shape"}, the range will be {@code TokenRange(0, 5)}
 */
public record CompletionSuggestion(String suggestion, Component display, String suffix, TokenRange replacementRange) {
    
    /**
     * Inserts the suggestion into the given string.
     *
     * @param into The string to insert into.
     *
     * @return The given string with this suggestion inserted into it.
     */
    public String replaceIn(final String into) {
        
        return replacementRange.replace(into, toInsert());
    }
    
    /**
     * Gets the string to insert, combining the suggestion and the suffix.
     *
     * @return The string to insert.
     */
    public String toInsert() {
        
        return suggestion + suffix;
    }
    
    
}
