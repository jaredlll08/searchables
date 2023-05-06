package com.blamejared.searchables.mixin;

import net.minecraft.client.gui.components.EditBox;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.function.*;

@Mixin(EditBox.class)
public interface AccessEditBox {
    
    @Accessor("filter")
    Predicate<String> searchables$getFilter();
    
    @Nullable
    @Accessor("responder")
    Consumer<String> searchables$getResponder();
    
}
