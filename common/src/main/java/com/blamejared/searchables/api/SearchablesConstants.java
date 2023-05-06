package com.blamejared.searchables.api;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class SearchablesConstants {
    
    public static final String MOD_ID = "searchables";
    
    public static final Component COMPONENT_SEARCH = Component.translatable("options.search");
    
    public static ResourceLocation rl(final String path) {
        
        return new ResourceLocation(MOD_ID, path);
    }
    
}
