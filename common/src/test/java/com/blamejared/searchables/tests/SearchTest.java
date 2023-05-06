package com.blamejared.searchables.tests;

import com.blamejared.searchables.TestConstants;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;

public class SearchTest {
    
    @Test
    public void testNoResultsDefaultSearch() {
        
        List<TestConstants.Shape> shapes = TestConstants.SHAPE.filterEntries(TestConstants.SHAPES, "negative");
        assertThat(shapes, empty());
    }
    
    @Test
    public void testNoResultsComponentSearch() {
        
        List<TestConstants.Shape> shapes = TestConstants.SHAPE.filterEntries(TestConstants.SHAPES, "name:invalid");
        assertThat(shapes, empty());
    }
    
    @Test
    public void testNoResultsFullSearch() {
        
        List<TestConstants.Shape> shapes = TestConstants.SHAPE.filterEntries(TestConstants.SHAPES, "name:invalid type:square colour:blue");
        assertThat(shapes, empty());
    }
    
    @Test
    public void testDefaultSearch() {
        
        List<TestConstants.Shape> shapes = TestConstants.SHAPE.filterEntries(TestConstants.SHAPES, "five");
        assertThat(shapes, hasSize(1));
        assertThat(shapes, contains(TestConstants.Shapes.FIVE));
    }
    
    @Test
    public void testComponentSearch() {
        
        List<TestConstants.Shape> shapes = TestConstants.SHAPE.filterEntries(TestConstants.SHAPES, "type:triangle");
        assertThat(shapes, hasSize(4));
        assertThat(shapes, contains(TestConstants.Shapes.NINE, TestConstants.Shapes.TEN, TestConstants.Shapes.ELEVEN, TestConstants.Shapes.TWELVE));
    }
    
    @Test
    public void testComponentStringSearch() {
        
        List<TestConstants.Shape> shapes = TestConstants.SHAPE.filterEntries(TestConstants.SHAPES, "type:`sq'uare`");
        assertThat(shapes, hasSize(1));
        assertThat(shapes, contains(TestConstants.Shapes.ONE));
    }
    
    @Test
    public void testComponentStringWithSpaceSearch() {
        
        List<TestConstants.Shape> shapes = TestConstants.SHAPE.filterEntries(TestConstants.SHAPES, "type:`sq ");
        assertThat(shapes, hasSize(1));
        assertThat(shapes, contains(TestConstants.Shapes.ZERO));
    }
    
    @Test
    public void testDefaultAndComponentSearchOrderOne() {
        
        List<TestConstants.Shape> shapes = TestConstants.SHAPE.filterEntries(TestConstants.SHAPES, "four type:square");
        assertThat(shapes, hasSize(1));
        assertThat(shapes, contains(TestConstants.Shapes.FOUR));
    }
    
    @Test
    public void testDefaultAndComponentSearchOrderTwo() {
        
        List<TestConstants.Shape> shapes = TestConstants.SHAPE.filterEntries(TestConstants.SHAPES, "type:square four");
        assertThat(shapes, hasSize(1));
        assertThat(shapes, contains(TestConstants.Shapes.FOUR));
    }
    
    
    @Test
    public void testFullSearch() {
        
        List<TestConstants.Shape> shapes = TestConstants.SHAPE.filterEntries(TestConstants.SHAPES, "name:eight type:circle colour:yellow");
        assertThat(shapes, hasSize(1));
        assertThat(shapes, contains(TestConstants.Shapes.EIGHT));
    }
    
    @Test
    public void testEmptySearch() {
        
        List<TestConstants.Shape> shapes = TestConstants.SHAPE.filterEntries(TestConstants.SHAPES, "");
        assertThat(shapes, contains(TestConstants.SHAPES.toArray(TestConstants.Shape[]::new)));
    }
    
    @Test
    public void testEmptyComponentSearch() {
        
        List<TestConstants.Shape> shapes = TestConstants.SHAPE.filterEntries(TestConstants.SHAPES, "name:");
        assertThat(shapes, contains(TestConstants.SHAPES.toArray(TestConstants.Shape[]::new)));
    }
    
    @Test
    public void testOneEmptyComponentSearchOrderOne() {
        
        List<TestConstants.Shape> shapes = TestConstants.SHAPE.filterEntries(TestConstants.SHAPES, "colour:red name:");
        assertThat(shapes, contains(TestConstants.Shapes.ONE, TestConstants.Shapes.FIVE, TestConstants.Shapes.NINE));
    }
    
    @Test
    public void testOneEmptyComponentSearchOrderTwo() {
        
        List<TestConstants.Shape> shapes = TestConstants.SHAPE.filterEntries(TestConstants.SHAPES, "name: colour:red");
        assertThat(shapes, contains(TestConstants.Shapes.ONE, TestConstants.Shapes.FIVE, TestConstants.Shapes.NINE));
    }
    
}
