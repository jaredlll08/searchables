package com.blamejared.searchables;

import com.blamejared.searchables.api.SearchableComponent;
import com.blamejared.searchables.api.SearchableType;

import java.util.List;
import java.util.Optional;

public class TestConstants {
    
    public static class Shapes {
        
        public static final Shape ZERO = new Shape("zero", "sq uare", "purple");
        public static final Shape ONE = new Shape("one", "sq'uare", "red");
        public static final Shape TWO = new Shape("two", "sq\"uare", "blue");
        public static final Shape THREE = new Shape("three", "sq`uare", "green");
        public static final Shape FOUR = new Shape("four", "square", "yellow");
        public static final Shape FIVE = new Shape("five", "circle", "red");
        public static final Shape SIX = new Shape("six", "circle", "blue");
        public static final Shape SEVEN = new Shape("seven", "circle", "green");
        public static final Shape EIGHT = new Shape("eight", "circle", "yellow");
        public static final Shape NINE = new Shape("nine", "triangle", "red");
        public static final Shape TEN = new Shape("ten", "triangle", "blue");
        public static final Shape ELEVEN = new Shape("eleven", "triangle", "green");
        public static final Shape TWELVE = new Shape("twelve", "triangle", "yellow");
        
    }
    
    public static final List<Shape> SHAPES = List.of(
            Shapes.ZERO,
            Shapes.ONE,
            Shapes.TWO,
            Shapes.THREE,
            Shapes.FOUR,
            Shapes.FIVE,
            Shapes.SIX,
            Shapes.SEVEN,
            Shapes.EIGHT,
            Shapes.NINE,
            Shapes.TEN,
            Shapes.ELEVEN,
            Shapes.TWELVE
    );
    
    public static final SearchableType<Shape> SHAPE = new SearchableType.Builder<Shape>()
            .defaultComponent(SearchableComponent.create("name", shape -> Optional.of(shape.name())))
            .component(SearchableComponent.create("type", shape -> Optional.of(shape.type())))
            .component(SearchableComponent.create("colour", shape -> Optional.of(shape.colour())))
            .build();
    
    
    public record Shape(String name, String type, String colour) {}
    
}
