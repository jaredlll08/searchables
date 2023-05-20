package com.blamejared.searchables.api.formatter;

import net.minecraft.util.text.Style;

import java.util.Objects;

/**
 * Extra context used by the {@link FormattingVisitor}.
 */
public final class FormattingContext {
    
    private final boolean isKey;
    private final Style style;
    private final boolean valid;
    
    /**
     * @param isKey Is the current expression a key
     * @param style The style to apply to the current expression
     * @param valid If the current expression is valid.
     */
    FormattingContext(boolean isKey, Style style, boolean valid) {
        
        this.isKey = isKey;
        this.style = style;
        this.valid = valid;
    }
    
    public boolean isKey() {return isKey;}
    
    public Style style() {return style;}
    
    public boolean valid() {return valid;}
    
    @Override
    public boolean equals(Object obj) {
        
        if(obj == this) {
            return true;
        }
        if(obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        FormattingContext that = (FormattingContext) obj;
        return this.isKey == that.isKey &&
                Objects.equals(this.style, that.style) &&
                this.valid == that.valid;
    }
    
    @Override
    public int hashCode() {
        
        return Objects.hash(isKey, style, valid);
    }
    
    @Override
    public String toString() {
        
        return "FormattingContext[" +
                "isKey=" + isKey + ", " +
                "style=" + style + ", " +
                "valid=" + valid + ']';
    }
    
    public static FormattingContext empty() {
        
        return new FormattingContext(false, Style.EMPTY, true);
    }
    
    public static FormattingContext key(Style validStyle, boolean valid) {
        
        return new FormattingContext(true, valid ? validStyle : FormattingConstants.INVALID, valid);
    }
    
    public static FormattingContext literal(Style validStyle, boolean valid) {
        
        return new FormattingContext(false, valid ? validStyle : FormattingConstants.INVALID, valid);
    }
    
    public Style style(boolean valid) {
        
        return valid ? style() : FormattingConstants.INVALID;
    }
    
}