package com.blamejared.searchables.api;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public final class TokenRange implements Comparable<TokenRange> {
    
    public static final TokenRange EMPTY = at(0);
    
    public static TokenRange at(final int position) {
        
        return new TokenRange(position, position);
    }
    
    public static TokenRange between(final int start, final int end) {
        
        return new TokenRange(start, end);
    }
    
    public static TokenRange encompassing(final TokenRange first, final TokenRange second) {
        
        return new TokenRange(Math.min(first.start(), second.start()), Math.max(first.end(), second.end()));
    }
    
    private final int start;
    private final int end;
    
    private final SortedSet<TokenRange> subRanges;
    
    public TokenRange(final int start, final int end) {
        
        this.start = start;
        this.end = end;
        this.subRanges = new TreeSet<>();
    }
    
    public void addRange(final TokenRange range) {
        
        this.subRanges.add(range);
        //                flattenRanges();
    }
    
    public void addRanges(final Collection<TokenRange> ranges) {
        
        this.subRanges.addAll(ranges);
    }
    
    public Set<TokenRange> subRanges() {
        
        return subRanges;
    }
    
    public TokenRange range(final int index) {
        
        return subRanges().stream().skip(index).findFirst().orElseThrow();
    }
    
    public int rangeIndexAtPosition(final int position) {
        
        int i = 0;
        for(TokenRange subRange : subRanges()) {
            if(!subRange.contains(position)) {
                i++;
                continue;
            }
            break;
        }
        return i;
    }
    
    public TokenRange rangeAtPosition(final int position) {
        
        //TODO if a range is empty, there are issues, need to find a repro case
        return subRanges.stream()
                .filter(tokenRange -> tokenRange.contains(position))
                .findFirst()
                .orElse(this);
    }
    
    public TokenRange simplify() {
        
        return TokenRange.between(this.start(), this.end());
    }
    
    public boolean contains(final TokenRange other) {
        
        return this.start() <= other.start() && other.end() <= this.end();
    }
    
    // Fully covers
    public boolean covers(final TokenRange other) {
        
        return this.start() <= other.start() && other.end() <= this.end();
    }
    
    public boolean contains(final int position) {
        
        return (this.start() <= position && position <= this.end());
    }
    
    public String substring(final String of) {
        
        return of.substring(this.start(), this.end());
    }
    
    public String substring(final String of, final int end) {
        
        return of.substring(this.start(), end);
    }
    
    public String delete(final String from) {
        
        return new StringBuilder(from).delete(this.start(), this.end()).toString();
    }
    
    public String insert(final String to, final String toInsert) {
        
        return new StringBuilder(to).insert(this.start(), toInsert).toString();
    }
    
    public String replace(final String into, final String toInsert) {
        
        return insert(delete(into), toInsert);
    }
    
    public boolean isEmpty() {
        
        return this.start() == this.end();
    }
    
    public int length() {
        
        return this.end() - this.start();
    }
    
    public int start() {
        
        return start;
    }
    
    public int end() {
        
        return end;
    }
    
    public TokenRange recalculate() {
        
        if(this.subRanges().isEmpty()) {
            return this;
        }
        int start = this.subRanges()
                .stream()
                .min(Comparator.comparing(TokenRange::end))
                .map(TokenRange::start)
                .orElse(this.start());
        int end = this.subRanges()
                .stream()
                .max(Comparator.comparing(TokenRange::end))
                .map(TokenRange::end)
                .orElse(this.end());
        
        if(start == this.start() && end == this.end()) {
            return this;
        }
        
        TokenRange newRange = TokenRange.between(start, end);
        newRange.subRanges().addAll(this.subRanges());
        return newRange;
    }
    
    @Override
    public boolean equals(final Object o) {
        
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()) {
            return false;
        }
        
        TokenRange that = (TokenRange) o;
        
        if(start != that.start) {
            return false;
        }
        if(end != that.end) {
            return false;
        }
        return Objects.equals(subRanges, that.subRanges);
    }
    
    @Override
    public int hashCode() {
        
        int result = start;
        result = 31 * result + end;
        result = 31 * result + subRanges.hashCode();
        return result;
    }
    
    @Override
    public String toString() {
        
        return "TokenRange{" +
                "start=" + start +
                ", end=" + end +
                ", subRanges=" + subRanges +
                '}';
    }
    
    @Override
    public int compareTo(@NotNull final TokenRange o) {
        
        return Integer.compare(this.start(), o.start());
    }
    
}
