package com.blamejared.searchables.api.formatter;

import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;

class FormattingConstants {
    
    static final Style INVALID = Style.EMPTY.withColor(TextColor.fromRgb(0xFF0000)).withUnderlined(true);
    static final Style KEY = Style.EMPTY.withColor(TextColor.fromRgb(0x669BBC));
    static final Style TERM = Style.EMPTY.withColor(TextColor.fromRgb(0xEECC77));
    
}
