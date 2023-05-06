package com.blamejared.searchables.api.formatter;

import net.minecraft.network.chat.Style;

public record FormattingContext(boolean isKey, Style style, boolean valid) {
    
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