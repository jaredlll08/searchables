package com.blamejared.searchables.api.formatter;


import net.minecraft.util.text.Color;
import net.minecraft.util.text.Style;

class FormattingConstants {
    
    static final Style INVALID = Style.EMPTY.withColor(Color.fromRgb(0xFF0000)).withUnderlined(true);
    static final Style KEY = Style.EMPTY.withColor(Color.fromRgb(0x669BBC));
    static final Style TERM = Style.EMPTY.withColor(Color.fromRgb(0xEECC77));
    
}
