package com.blamejared.searchables.api.autcomplete;

import com.blamejared.searchables.api.*;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.joml.Vector2d;

import java.util.*;
import java.util.function.*;

public class AutoComplete<T> extends AbstractWidget implements Consumer<String> {
    
    private final SearchableType<T> type;
    private final AutoCompletingEditBox<T> editBox;
    private final Supplier<List<T>> entries;
    private final int suggestionHeight;
    private final int maxSuggestions;
    private List<CompletionSuggestion> suggestions;
    // The index of the element at the top of the displayed list
    private int displayOffset;
    // The actual selectedIndex, -1 means none selected, range is 0 -> maxSuggestions
    private int selectedIndex;
    private final Vector2d lastMousePosition;
    private int lastCursorPosition;
    
    public AutoComplete(SearchableType<T> type, AutoCompletingEditBox<T> editBox, Supplier<List<T>> entries, int x, int y, int width, int suggestionHeight) {
        
        this(type, editBox, entries, x, y, width, suggestionHeight, 7);
    }
    
    public AutoComplete(SearchableType<T> type, AutoCompletingEditBox<T> editBox, Supplier<List<T>> entries, int x, int y, int width, int suggestionHeight, int maxSuggestions) {
        
        super(x, y, width, suggestionHeight * maxSuggestions, Component.empty());
        this.type = type;
        this.editBox = editBox;
        this.entries = entries;
        this.suggestionHeight = suggestionHeight;
        this.maxSuggestions = maxSuggestions;
        this.suggestions = List.of();
        this.displayOffset = 0;
        this.selectedIndex = -1;
        this.lastMousePosition = new Vector2d(0, 0);
        this.lastCursorPosition = -1;
    }
    
    @Override
    public void accept(String value) {
        
        int position = this.editBox().getCursorPosition();
        if(lastCursorPosition != position) {
            displayOffset = 0;
            selectedIndex = 0;
            TokenRange replacementRange = this.editBox().completionVisitor().rangeAt(position);
            suggestions = type.getSuggestionsFor(entries.get(), value, position, replacementRange);
        }
        lastCursorPosition = position;
    }
    
    // For some reason mojang has these as 2 methods...
    @Override
    protected boolean clicked(double xpos, double ypos) {
        
        return super.clicked(xpos, ypos) && ypos < this.getY() + (suggestionHeight * shownSuggestions());
    }
    
    @Override
    public boolean isMouseOver(double xpos, double ypos) {
        
        return super.isMouseOver(xpos, ypos) && ypos < this.getY() + (suggestionHeight * shownSuggestions());
    }
    
    @Override
    public boolean mouseScrolled(double xpos, double ypos, double delta) {
        
        if(isMouseOver(xpos, ypos) || this.editBox().isMouseOver(xpos, ypos)) {
            displayOffset = (int) Mth.clamp(displayOffset - delta, 0, Math.max(this.suggestions.size() - maxSuggestions, 0));
            lastMousePosition.set(0);
            return true;
        }
        
        return false;
    }
    
    @Override
    public boolean mouseClicked(double mx, double my, int mb) {
        
        if(super.mouseClicked(mx, my, mb)) {
            updateHoveringState(mx, my);
            if(selectedIndex != -1) {
                insertSuggestion();
            }
            return true;
        }
        return false;
    }
    
    public void insertSuggestion() {
        
        CompletionSuggestion suggestion = this.suggestions.get(displayOffset + selectedIndex);
        this.editBox.deleteChars(suggestion.replacementRange());
        this.editBox.insertText(suggestion.toInsert());
    }
    
    @Override
    public void renderWidget(PoseStack pose, int mx, int my, float partial) {
        
        if(!editBox.isFocused()) {
            return;
        }
        updateHoveringState(mx, my);
        
        for(int i = displayOffset; i < Math.min(displayOffset + maxSuggestions, suggestions.size()); i++) {
            CompletionSuggestion suggestion = suggestions.get(i);
            int minX = this.getX() + 2;
            int minY = this.getY() + (suggestionHeight * (i - displayOffset));
            int maxY = minY + suggestionHeight;
            boolean hovered = selectedIndex != -1 && displayOffset + selectedIndex == i;
            fill(pose, this.getX(), minY, this.getX() + this.getWidth(), maxY, hovered ? 0xe0111111 : 0xe0000000);
            Minecraft.getInstance().font.drawShadow(pose, suggestion.display(), minX, minY + 1, hovered ? Objects.requireNonNull(ChatFormatting.YELLOW.getColor()) : 0xFFFFFFFF);
        }
        this.lastMousePosition.set(mx, my);
    }
    
    public void updateHoveringState(double xpos, double ypos) {
        
        if(!lastMousePosition.equals(xpos, ypos)) {
            selectedIndex = -1;
            if(isMouseOver(xpos, ypos)) {
                int minY = this.getY();
                for(int i = 0; i < shownSuggestions(); i++) {
                    int maxY = minY + suggestionHeight;
                    if(xpos >= getX() && xpos <= this.getX() + this.getWidth() && ypos >= minY && ypos < maxY) {
                        selectedIndex = i;
                    }
                    minY = maxY;
                }
            }
        }
    }
    
    public void scrollUp() {
        
        this.scrollUp(1);
    }
    
    public void scrollUp(int amount) {
        
        this.offsetDisplay(this.selectedIndex - amount);
    }
    
    public void scrollDown() {
        
        this.scrollDown(1);
    }
    
    public void scrollDown(int amount) {
        
        this.offsetDisplay(this.selectedIndex + amount);
    }
    
    public void offsetDisplay(int offset) {
        
        offset = Mth.clamp(offset, 0, shownSuggestions() - 1);
        final int halfSuggestions = Math.floorDiv(maxSuggestions, 2);
        int currentItem = this.displayOffset + offset;
        if(currentItem < this.displayOffset + halfSuggestions) {
            this.displayOffset = Math.max(currentItem - halfSuggestions, 0);
        } else if(currentItem > this.displayOffset + halfSuggestions) {
            this.displayOffset = Math.min(currentItem - halfSuggestions, Math.max(this.suggestions.size() - maxSuggestions, 0));
        }
        this.selectedIndex = currentItem - this.displayOffset;
    }
    
    public int shownSuggestions() {
        
        return Math.min(maxSuggestions, suggestions.size());
    }
    
    public int maxSuggestions() {
        
        return maxSuggestions;
    }
    
    public AutoCompletingEditBox<T> editBox() {
        
        return editBox;
    }
    
    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
    
    }
    
}
