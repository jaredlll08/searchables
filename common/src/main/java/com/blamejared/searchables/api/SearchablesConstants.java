package com.blamejared.searchables.api;

import net.minecraft.resources.ResourceLocation;

public class SearchablesConstants {
    
    public static final String MOD_ID = "searchables";
    
    public static ResourceLocation rl(final String path) {
        
        return new ResourceLocation(MOD_ID, path);
    }
    
}
