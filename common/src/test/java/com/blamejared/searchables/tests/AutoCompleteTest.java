package com.blamejared.searchables.tests;

import com.blamejared.searchables.TestConstants;
import com.blamejared.searchables.api.TokenRange;
import com.blamejared.searchables.api.autcomplete.CompletionSuggestion;
import com.blamejared.searchables.api.autcomplete.CompletionVisitor;
import com.blamejared.searchables.lang.StringSearcher;
import net.minecraft.network.chat.Component;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

public class AutoCompleteTest {
    
    @Test
    public void testNoResults() {
        
        String search = "invalid";
        int position = 2;
        
        CompletionVisitor visitor = new CompletionVisitor();
        StringSearcher.search(search, visitor);
        TokenRange replacementRange = visitor.rangeAt(position);
        List<CompletionSuggestion> suggestions = TestConstants.SHAPE.getSuggestionsFor(TestConstants.SHAPES, search, position, replacementRange);
        assertThat(suggestions, empty());
    }
    
    @Test
    public void testType() {
        
        String search = "typ";
        int position = 2;
        
        CompletionVisitor visitor = new CompletionVisitor();
        StringSearcher.search(search, visitor);
        TokenRange replacementRange = visitor.rangeAt(position);
        List<CompletionSuggestion> suggestions = TestConstants.SHAPE.getSuggestionsFor(TestConstants.SHAPES, search, position, replacementRange);
        assertThat(suggestions, hasSize(1));
        assertThat(suggestions, contains(suggestion("type", "type", ":", 0, 3)));
        assertThat(suggestions.get(0).replaceIn(search), is("type:"));
    }
    
    @Test
    public void testTerm() {
        
        String search = "type:sq";
        int position = 6;
        
        CompletionVisitor visitor = new CompletionVisitor();
        StringSearcher.search(search, visitor);
        TokenRange replacementRange = visitor.rangeAt(position);
        List<CompletionSuggestion> suggestions = TestConstants.SHAPE.getSuggestionsFor(TestConstants.SHAPES, search, position, replacementRange);
        assertThat(suggestions, hasSize(5));
        assertThat(suggestions, hasItems(
                suggestion("type:square", "square", " ", 0, 7),
                suggestion("type:'sq\"uare'", "'sq\"uare'", " ", 0, 7),
                suggestion("type:\"sq`uare\"", "\"sq`uare\"", " ", 0, 7),
                suggestion("type:\"sq'uare\"", "\"sq'uare\"", " ", 0, 7),
                suggestion("type:\"sq uare\"", "\"sq uare\"", " ", 0, 7)
        ));
        assertThat(suggestions.get(0).replaceIn(search), is("type:\"sq uare\" "));
        assertThat(suggestions.get(1).replaceIn(search), is("type:'sq\"uare' "));
        assertThat(suggestions.get(2).replaceIn(search), is("type:\"sq'uare\" "));
        assertThat(suggestions.get(3).replaceIn(search), is("type:\"sq`uare\" "));
        assertThat(suggestions.get(4).replaceIn(search), is("type:square "));
        
    }
    
    @Test
    public void testTypeAtStart() {
        
        String search = "typ name:one";
        int position = 2;
        
        CompletionVisitor visitor = new CompletionVisitor();
        StringSearcher.search(search, visitor);
        TokenRange replacementRange = visitor.rangeAt(position);
        List<CompletionSuggestion> suggestions = TestConstants.SHAPE.getSuggestionsFor(TestConstants.SHAPES, search, position, replacementRange);
        assertThat(suggestions, hasSize(1));
        assertThat(suggestions, contains(
                suggestion("type", "type", ":", 0, 3))
        );
        assertThat(suggestions.get(0).replaceIn(search), is("type: name:one"));
    }
    
    @Test
    public void testTermAtStart() {
        
        String search = "type:sq name:one";
        int position = 6;
        
        CompletionVisitor visitor = new CompletionVisitor();
        StringSearcher.search(search, visitor);
        TokenRange replacementRange = visitor.rangeAt(position);
        List<CompletionSuggestion> suggestions = TestConstants.SHAPE.getSuggestionsFor(TestConstants.SHAPES, search, position, replacementRange);
        assertThat(suggestions, hasSize(5));
        assertThat(suggestions, hasItems(
                suggestion("type:square", "square", " ", 0, 7),
                suggestion("type:'sq\"uare'", "'sq\"uare'", " ", 0, 7),
                suggestion("type:\"sq`uare\"", "\"sq`uare\"", " ", 0, 7),
                suggestion("type:\"sq'uare\"", "\"sq'uare\"", " ", 0, 7),
                suggestion("type:\"sq uare\"", "\"sq uare\"", " ", 0, 7)
        ));
        // Is the extra space actually correct?
        assertThat(suggestions.get(0).replaceIn(search), is("type:\"sq uare\"  name:one"));
        assertThat(suggestions.get(1).replaceIn(search), is("type:'sq\"uare'  name:one"));
        assertThat(suggestions.get(2).replaceIn(search), is("type:\"sq'uare\"  name:one"));
        assertThat(suggestions.get(3).replaceIn(search), is("type:\"sq`uare\"  name:one"));
        assertThat(suggestions.get(4).replaceIn(search), is("type:square  name:one"));
    }
    
    @Test
    public void testTypeAtEnd() {
        
        String search = "name:one typ";
        int position = 10;
        
        CompletionVisitor visitor = new CompletionVisitor();
        StringSearcher.search(search, visitor);
        TokenRange replacementRange = visitor.rangeAt(position);
        List<CompletionSuggestion> suggestions = TestConstants.SHAPE.getSuggestionsFor(TestConstants.SHAPES, search, position, replacementRange);
        assertThat(suggestions, hasSize(1));
        assertThat(suggestions, contains(
                suggestion("type", "type", ":", 9, 12))
        );
        assertThat(suggestions.get(0).replaceIn(search), is("name:one type:"));
    }
    
    @Test
    public void testTermAtEnd() {
        
        String search = "name:one type:s";
        int position = 15;
        
        CompletionVisitor visitor = new CompletionVisitor();
        StringSearcher.search(search, visitor);
        TokenRange replacementRange = visitor.rangeAt(position);
        List<CompletionSuggestion> suggestions = TestConstants.SHAPE.getSuggestionsFor(TestConstants.SHAPES, search, position, replacementRange);
        assertThat(suggestions, hasSize(5));
        assertThat(suggestions, hasItems(
                suggestion("type:square", "square", " ", 9, 15),
                suggestion("type:'sq\"uare'", "'sq\"uare'", " ", 9, 15),
                suggestion("type:\"sq`uare\"", "\"sq`uare\"", " ", 9, 15),
                suggestion("type:\"sq'uare\"", "\"sq'uare\"", " ", 9, 15),
                suggestion("type:\"sq uare\"", "\"sq uare\"", " ", 9, 15)
        ));
        assertThat(suggestions.get(0).replaceIn(search), is("name:one type:\"sq uare\" "));
        assertThat(suggestions.get(1).replaceIn(search), is("name:one type:'sq\"uare' "));
        assertThat(suggestions.get(2).replaceIn(search), is("name:one type:\"sq'uare\" "));
        assertThat(suggestions.get(3).replaceIn(search), is("name:one type:\"sq`uare\" "));
        assertThat(suggestions.get(4).replaceIn(search), is("name:one type:square "));
    }
    
    @Test
    public void testTermAtColon() {
        
        String search = "name:";
        int position = 5;
        
        CompletionVisitor visitor = new CompletionVisitor();
        StringSearcher.search(search, visitor);
        TokenRange replacementRange = visitor.rangeAt(position);
        List<CompletionSuggestion> suggestions = TestConstants.SHAPE.getSuggestionsFor(TestConstants.SHAPES, search, position, replacementRange);
        assertThat(suggestions, hasSize(13));
        assertThat(suggestions, hasItems(
                suggestion("name:zero", "zero", " ", 0, 5),
                suggestion("name:one", "one", " ", 0, 5),
                suggestion("name:two", "two", " ", 0, 5),
                suggestion("name:three", "three", " ", 0, 5),
                suggestion("name:four", "four", " ", 0, 5),
                suggestion("name:five", "five", " ", 0, 5),
                suggestion("name:six", "six", " ", 0, 5),
                suggestion("name:seven", "seven", " ", 0, 5),
                suggestion("name:eight", "eight", " ", 0, 5),
                suggestion("name:nine", "nine", " ", 0, 5),
                suggestion("name:ten", "ten", " ", 0, 5),
                suggestion("name:eleven", "eleven", " ", 0, 5),
                suggestion("name:twelve", "twelve", " ", 0, 5)
        ));
    }
    
    
    @Test
    public void testTypeAtStartReplacingTerm() {
        
        String search = "name:one type:square";
        int position = 2;
        
        CompletionVisitor visitor = new CompletionVisitor();
        StringSearcher.search(search, visitor);
        TokenRange replacementRange = visitor.rangeAt(position);
        List<CompletionSuggestion> suggestions = TestConstants.SHAPE.getSuggestionsFor(TestConstants.SHAPES, search, position, replacementRange);
        assertThat(suggestions, hasSize(1));
        assertThat(suggestions, contains(
                suggestion("name", "name", ":", 0, 8))
        );
        assertThat(suggestions.get(0).replaceIn(search), is("name: type:square"));
    }
    
    @Test
    public void testTypeAtEndReplacingTerm() {
        
        String search = "name:one type:square";
        int position = 10;
        
        CompletionVisitor visitor = new CompletionVisitor();
        StringSearcher.search(search, visitor);
        TokenRange replacementRange = visitor.rangeAt(position);
        List<CompletionSuggestion> suggestions = TestConstants.SHAPE.getSuggestionsFor(TestConstants.SHAPES, search, position, replacementRange);
        assertThat(suggestions, hasSize(1));
        assertThat(suggestions, contains(
                suggestion("type", "type", ":", 9, 20))
        );
        assertThat(suggestions.get(0).replaceIn(search), is("name:one type:"));
    }
    
    @Test
    public void testTypeInMiddle() {
        
        String search = "colour:red name: type:square";
        int position = 12;
        
        CompletionVisitor visitor = new CompletionVisitor();
        StringSearcher.search(search, visitor);
        TokenRange replacementRange = visitor.rangeAt(position);
        List<CompletionSuggestion> suggestions = TestConstants.SHAPE.getSuggestionsFor(TestConstants.SHAPES, search, position, replacementRange);
        assertThat(suggestions, hasSize(1));
        assertThat(suggestions, contains(
                suggestion("name", "name", ":", 11, 16))
        );
        assertThat(suggestions.get(0).replaceIn(search), is("colour:red name: type:square"));
    }
    
    @Test
    public void testTermInMiddle() {
        
        String search = "colour:red name:o type:square";
        int position = 17;
        
        CompletionVisitor visitor = new CompletionVisitor();
        StringSearcher.search(search, visitor);
        TokenRange replacementRange = visitor.rangeAt(position);
        List<CompletionSuggestion> suggestions = TestConstants.SHAPE.getSuggestionsFor(TestConstants.SHAPES, search, position, replacementRange);
        assertThat(suggestions, hasSize(1));
        assertThat(suggestions, contains(
                suggestion("name:one", "one", " ", 11, 17))
        );
        assertThat(suggestions.get(0).replaceIn(search), is("colour:red name:one  type:square"));
    }
    
    @Test
    public void testTypeInMiddleReplacingTerm() {
        
        String search = "colour:red name:one type:square";
        int position = 12;
        
        CompletionVisitor visitor = new CompletionVisitor();
        StringSearcher.search(search, visitor);
        TokenRange replacementRange = visitor.rangeAt(position);
        List<CompletionSuggestion> suggestions = TestConstants.SHAPE.getSuggestionsFor(TestConstants.SHAPES, search, position, replacementRange);
        assertThat(suggestions, hasSize(1));
        assertThat(suggestions, contains(
                suggestion("name", "name", ":", 11, 19))
        );
        assertThat(suggestions.get(0).replaceIn(search), is("colour:red name: type:square"));
    }
    
    @Test
    public void testStringTerm() {
        
        String search = "type:`sq";
        int position = 7;
        
        CompletionVisitor visitor = new CompletionVisitor();
        StringSearcher.search(search, visitor);
        TokenRange replacementRange = visitor.rangeAt(position);
        List<CompletionSuggestion> suggestions = TestConstants.SHAPE.getSuggestionsFor(TestConstants.SHAPES, search, position, replacementRange);
        assertThat(suggestions, hasSize(5));
        assertThat(suggestions, hasItems(
                suggestion("type:square", "square", " ", 0, 8),
                suggestion("type:'sq\"uare'", "'sq\"uare'", " ", 0, 8),
                suggestion("type:\"sq`uare\"", "\"sq`uare\"", " ", 0, 8),
                suggestion("type:\"sq'uare\"", "\"sq'uare\"", " ", 0, 8),
                suggestion("type:\"sq uare\"", "\"sq uare\"", " ", 0, 8)
        ));
        // Is the extra space actually correct?
        assertThat(suggestions.get(0).replaceIn(search), is("type:\"sq uare\" "));
        assertThat(suggestions.get(1).replaceIn(search), is("type:'sq\"uare' "));
        assertThat(suggestions.get(2).replaceIn(search), is("type:\"sq'uare\" "));
        assertThat(suggestions.get(3).replaceIn(search), is("type:\"sq`uare\" "));
        assertThat(suggestions.get(4).replaceIn(search), is("type:square "));
    }
    
    @Test
    public void testStringsTerm() {
        
        String search = "type:`";
        int position = 6;
        
        CompletionVisitor visitor = new CompletionVisitor();
        StringSearcher.search(search, visitor);
        TokenRange replacementRange = visitor.rangeAt(position);
        List<CompletionSuggestion> suggestions = TestConstants.SHAPE.getSuggestionsFor(TestConstants.SHAPES, search, position, replacementRange);
        assertThat(suggestions, hasSize(7));
        assertThat(suggestions, hasItems(
                suggestion("type:triangle", "triangle", " ", 0, 6),
                suggestion("type:square", "square", " ", 0, 6),
                suggestion("type:circle", "circle", " ", 0, 6),
                suggestion("type:'sq\"uare'", "'sq\"uare'", " ", 0, 6),
                suggestion("type:\"sq`uare\"", "\"sq`uare\"", " ", 0, 6),
                suggestion("type:\"sq'uare\"", "\"sq'uare\"", " ", 0, 6),
                suggestion("type:\"sq uare\"", "\"sq uare\"", " ", 0, 6)
        ));
        assertThat(suggestions.get(0).replaceIn(search), is("type:circle "));
        assertThat(suggestions.get(1).replaceIn(search), is("type:\"sq uare\" "));
        assertThat(suggestions.get(2).replaceIn(search), is("type:'sq\"uare' "));
        assertThat(suggestions.get(3).replaceIn(search), is("type:\"sq'uare\" "));
        assertThat(suggestions.get(4).replaceIn(search), is("type:\"sq`uare\" "));
        assertThat(suggestions.get(5).replaceIn(search), is("type:square "));
        assertThat(suggestions.get(6).replaceIn(search), is("type:triangle "));
    }
    
    
    private CompletionSuggestion suggestion(String suggestion, String display, String suffix, int rangeStart, int rangeEnd) {
        
        return new CompletionSuggestion(suggestion, Component.literal(display), suffix, TokenRange.between(rangeStart, rangeEnd));
    }
    
}
