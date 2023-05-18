package com.blamejared.searchables.api.autcomplete;

import com.blamejared.searchables.api.*;
import com.blamejared.searchables.api.formatter.FormattingVisitor;
import com.blamejared.searchables.mixin.AccessEditBox;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.*;
import java.util.function.*;

public class AutoCompletingEditBox<T> extends EditBox {
    
    private final FormattingVisitor formattingVisitor;
    private final CompletionVisitor completionVisitor;
    private final DelegatingConsumers<String> responders = new DelegatingConsumers<>();
    private final AutoComplete<T> autoComplete;
    
    public AutoCompletingEditBox(Font font, int x, int y, int width, int height, Component message, SearchableType<T> type, Supplier<List<T>> entries) {
        
        this(font, x, y, width, height, null, message, type, entries);
    }
    
    public AutoCompletingEditBox(Font font, int x, int y, int width, int height, @Nullable EditBox thisBox, Component message, SearchableType<T> type, Supplier<List<T>> entries) {
        
        super(font, x, y, width, height, thisBox, message);
        this.setMaxLength(Integer.MAX_VALUE);
        this.formattingVisitor = new FormattingVisitor(type);
        this.completionVisitor = new CompletionVisitor();
        this.autoComplete = new AutoComplete<>(type, this, entries, x, y + 2 + height, width, font.lineHeight + 2);
        this.setFormatter(this.formattingVisitor);
        this.setResponder(this.responders);
        addResponder(this.formattingVisitor);
        addResponder(this.completionVisitor);
        addResponder(this.autoComplete);
    }
    
    @Override
    public boolean mouseClicked(double xpos, double ypos, int button) {
        
        if(this.isFocused() && autoComplete.mouseClicked(xpos, ypos, button)) {
            return true;
        }
        if((isMouseOver(xpos, ypos) || autoComplete().isMouseOver(xpos, ypos)) && button == GLFW.GLFW_MOUSE_BUTTON_2) {
            this.setValue("");
            return true;
        }
        return super.mouseClicked(xpos, ypos, button);
    }
    
    @Override
    public boolean keyPressed(int key, int scancode, int mods) {
        
        switch(key) {
            case (GLFW.GLFW_KEY_PAGE_DOWN) -> {
                this.autoComplete.scrollDown(this.autoComplete().maxSuggestions());
                return true;
            }
            case (GLFW.GLFW_KEY_DOWN) -> {
                this.autoComplete().scrollDown();
                return true;
            }
            case (GLFW.GLFW_KEY_PAGE_UP) -> {
                this.autoComplete.scrollUp(this.autoComplete().maxSuggestions());
                return true;
            }
            case (GLFW.GLFW_KEY_UP) -> {
                this.autoComplete().scrollUp();
                return true;
            }
            case (GLFW.GLFW_KEY_ENTER) -> {
                this.autoComplete().insertSuggestion();
                return true;
            }
        }
        return super.keyPressed(key, scancode, mods);
    }
    
    /**
     * Deletes the characters as the given {@link TokenRange}.
     *
     * @param range The range to delete characters from
     */
    public void deleteChars(TokenRange range) {
        
        if(!this.getValue().isEmpty()) {
            if(!range.isEmpty()) {
                String newValue = range.delete(this.getValue());
                if(this.getFilter().test(newValue)) {
                    this.setValue(newValue);
                    this.moveCursorTo(range.start());
                }
            }
        }
    }
    
    public Predicate<String> getFilter() {
        
        return ((AccessEditBox) this).searchables$getFilter();
    }
    
    @Nullable
    public Consumer<String> getResponder() {
        
        return ((AccessEditBox) this).searchables$getResponder();
    }
    
    /**
     * Should not be used, use {@link AutoCompletingEditBox#addResponder(Consumer)} instead
     */
    @SuppressWarnings("DeprecatedIsStillUsed")
    @Override
    @Deprecated
    public void setResponder(Consumer<String> responder) {
        
        if(this.getResponder() == null) {
            super.setResponder(responders);
        } else {
            this.addResponder(responder);
        }
    }
    
    public void addResponder(Consumer<String> responder) {
        
        this.responders.addConsumer(responder);
    }
    
    public FormattingVisitor formattingVisitor() {
        
        return formattingVisitor;
    }
    
    public CompletionVisitor completionVisitor() {
        
        return completionVisitor;
    }
    
    public AutoComplete<T> autoComplete() {
        
        return autoComplete;
    }
    
    private static class DelegatingConsumers<T> implements Consumer<T> {
        
        private final List<Consumer<T>> consumers = new ArrayList<>();
        
        @Override
        public void accept(T t) {
            
            consumers.forEach(tConsumer -> tConsumer.accept(t));
        }
        
        public void addConsumer(Consumer<T> consumer) {
            
            this.consumers.add(consumer);
        }
        
    }
    
}
