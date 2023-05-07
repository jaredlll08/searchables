package com.blamejared.searchables.api;

import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * A range denoting the start and end of a Token, as well as any tokens inside that range.
 */
public final class TokenRange implements Comparable<TokenRange>, Iterable<TokenRange> {
    
    public static final TokenRange EMPTY = at(0);
    
    /**
     * Creates a new {@link TokenRange} at the given position
     *
     * @param position The position of the token.
     *
     * @return a new {@link TokenRange} at the given position
     */
    public static TokenRange at(final int position) {
        
        return new TokenRange(position, position);
    }
    
    /**
     * Creates a new {@link TokenRange} with the given start and end.
     *
     * @param start The start of the range.
     * @param end   The end of the range.
     *
     * @return A new {@link TokenRange} at the given start and end.
     */
    public static TokenRange between(final int start, final int end) {
        
        return new TokenRange(start, end);
    }
    
    /**
     * Creates a new {@link TokenRange} that covers both the first and second token.
     *
     * @param first  The first token to cover.
     * @param second The second token to cover.
     *
     * @return A new {@link TokenRange} that covers both the first and second token.
     */
    public static TokenRange encompassing(final TokenRange first, final TokenRange second) {
        
        return new TokenRange(Math.min(first.start(), second.start()), Math.max(first.end(), second.end()));
    }
    
    private final int start;
    private final int end;
    
    private final SortedSet<TokenRange> subRanges;
    
    private TokenRange(final int start, final int end) {
        
        this.start = start;
        this.end = end;
        this.subRanges = new TreeSet<>();
    }
    
    /**
     * Adds a range as a subrange to this range.
     *
     * @param range The range to add.
     */
    public void addRange(final TokenRange range) {
        
        this.subRanges.add(range);
    }
    
    /**
     * Adds a collection of ranges as subranges to this range.
     *
     * @param ranges The ranges to add.
     */
    public void addRanges(final Collection<TokenRange> ranges) {
        
        this.subRanges.addAll(ranges);
    }
    
    public Set<TokenRange> subRanges() {
        
        return subRanges;
    }
    
    /**
     * Gets the subrange at the given index.
     *
     * @param index The index to get.
     *
     * @return The range at the given index
     *
     * @throws IndexOutOfBoundsException if index is outside the bounds of subranges.
     */
    public TokenRange range(final int index) {
        
        return subRanges().stream().skip(index).findFirst().orElseThrow(IndexOutOfBoundsException::new);
    }
    
    /**
     * Gets the index of the range at the given position.
     * If there are no sub ranges, this will return {@code 0}
     *
     * @param position The position to get.
     *
     * @return The index of the range at the given position, or 0 if
     *
     * @throws IndexOutOfBoundsException if the position is not within this range's bounds.
     */
    public int rangeIndexAtPosition(final int position) {
        
        if(!this.contains(position)) {
            throw new IndexOutOfBoundsException();
        }
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
        
        //TODO if a range is empty, it may have issues getting, but I need a repro case to test and fix.
        return subRanges.stream()
                .filter(tokenRange -> tokenRange.contains(position))
                .findFirst()
                .orElse(this);
    }
    
    /**
     * Creates a new {@link TokenRange} at this range's start and end, without any subranges.
     *
     * @return
     */
    public TokenRange simplify() {
        
        return TokenRange.between(this.start(), this.end());
    }
    
    
    /**
     * Checks if this range fully covers the other range.
     *
     * @param other The range to see if this range covers.
     *
     * @return true if this range covers the other range, false otherwise.
     */
    public boolean covers(final TokenRange other) {
        
        return this.start() <= other.start() && other.end() <= this.end();
    }
    
    /**
     * Checks if this range contains the given position.
     *
     * @param position The position to check.
     *
     * @return true if it contains, false otherwise.
     */
    public boolean contains(final int position) {
        
        return (this.start() <= position && position <= this.end());
    }
    
    /**
     * Performs a substring on the given string using this range's start and end.
     *
     * @param of The string to substring.
     *
     * @return The substring of the given string using this range's start and end.
     */
    public String substring(final String of) {
        
        return of.substring(this.start(), this.end());
    }
    
    /**
     * Performs a substring on the given string using this range's start and the given end.
     *
     * @param of  The string to substring.
     * @param end The end position.
     *
     * @return The substring of the given string using this range's start and the given end.
     */
    public String substring(final String of, final int end) {
        
        return of.substring(this.start(), end);
    }
    
    /**
     * Deletes the content of the given string that this range covers.
     *
     * @param from The string to delete from.
     *
     * @return The string with the contents inside this range's position removed.
     */
    public String delete(final String from) {
        
        return new StringBuilder(from).delete(this.start(), this.end()).toString();
    }
    
    /**
     * Inserts the {@code toInsert} into the given string at this range's start position.
     *
     * @param to       The string to insert into.
     * @param toInsert The string to insert.
     *
     * @return The given string with the insertion added at this range's start position.
     */
    public String insert(final String to, final String toInsert) {
        
        return new StringBuilder(to).insert(this.start(), toInsert).toString();
    }
    
    /**
     * Replaces the contents of the string at this range's positions with the given {@code toInsert}
     *
     * @param into     The string to insert into
     * @param toInsert The string to insert.
     *
     * @return The given string with the insertion added at this range's position.
     */
    public String replace(final String into, final String toInsert) {
        
        return insert(delete(into), toInsert);
    }
    
    /**
     * Checks if this range is empty.
     *
     * @return true if empty, false otherwise.
     */
    public boolean isEmpty() {
        
        return this.start() == this.end();
    }
    
    /**
     * Gets the length of this range.
     *
     * @return the length of the range.
     */
    public int length() {
        
        return this.end() - this.start();
    }
    
    public int start() {
        
        return start;
    }
    
    public int end() {
        
        return end;
    }
    
    /**
     * Creates a new range that fully covers the subranges starts and ends.
     *
     * @return a new range that fully covers the subranges starts and ends.
     */
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
    
    @NotNull
    @Override
    public Iterator<TokenRange> iterator() {
        
        return this.subRanges().iterator();
    }
    
}
