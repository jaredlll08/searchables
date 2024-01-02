package com.blamejared.searchables.mixin;

import net.minecraft.client.gui.widget.TextFieldWidget;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.function.Consumer;
import java.util.function.Predicate;

@Mixin(TextFieldWidget.class)
public interface AccessTextFieldWidget {
    
    @Accessor("filter")
    Predicate<String> searchables$getFilter();

    @Nullable
    @Accessor("responder")
    Consumer<String> searchables$getResponder();
    
}
