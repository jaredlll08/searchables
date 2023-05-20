package com.blamejared.searchables.api.autcomplete;

import com.blamejared.searchables.api.TokenRange;
import net.minecraft.network.chat.Component;

import java.util.Objects;

/**
 * Represents a suggestion to be shown in {@link AutoComplete}
 */
public final class CompletionSuggestion {
    
    private final String suggestion;
    private final Component display;
    private final String suffix;
    private final TokenRange replacementRange;
    
    /**
     * @param suggestion       The suggestion to insert.
     * @param display          The component to display to the user.
     * @param suffix           The text to insert of the {@code suggestion}. Generally either {@literal ":"} or {@literal " "}
     * @param replacementRange The range of the current String to replace with this suggestion. Given the string {@code "sha"} and suggestion of {@code "shape"}, the range will be {@code TokenRange(0, 5)}
     */
    public CompletionSuggestion(String suggestion, Component display, String suffix, TokenRange replacementRange) {
        
        this.suggestion = suggestion;
        this.display = display;
        this.suffix = suffix;
        this.replacementRange = replacementRange;
    }
    
    public String suggestion() {return suggestion;}
    
    public Component display() {return display;}
    
    public String suffix() {return suffix;}
    
    public TokenRange replacementRange() {return replacementRange;}
    
    @Override
    public boolean equals(Object obj) {
        
        if(obj == this) {
            return true;
        }
        if(obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        CompletionSuggestion that = (CompletionSuggestion) obj;
        return Objects.equals(this.suggestion, that.suggestion) &&
                Objects.equals(this.display, that.display) &&
                Objects.equals(this.suffix, that.suffix) &&
                Objects.equals(this.replacementRange, that.replacementRange);
    }
    
    @Override
    public int hashCode() {
        
        return Objects.hash(suggestion, display, suffix, replacementRange);
    }
    
    @Override
    public String toString() {
        
        return "CompletionSuggestion[" +
                "suggestion=" + suggestion + ", " +
                "display=" + display + ", " +
                "suffix=" + suffix + ", " +
                "replacementRange=" + replacementRange + ']';
    }
    
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
