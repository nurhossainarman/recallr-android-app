package comp3350.flashcard.utils;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for StringUtils utility class.
 * Tests all string validation and manipulation methods.
 */
public class StringUtilsTest {

    @Test
    public void testIsNullOrEmpty_withNull() {
        assertTrue("Null string should return true", StringUtils.isNullOrEmpty(null));
    }

    @Test
    public void testIsNullOrEmpty_withEmptyString() {
        assertTrue("Empty string should return true", StringUtils.isNullOrEmpty(""));
    }

    @Test
    public void testIsNullOrEmpty_withWhitespaceOnly() {
        assertTrue("Whitespace-only string should return true", StringUtils.isNullOrEmpty("   "));
        assertTrue("Tab string should return true", StringUtils.isNullOrEmpty("\t"));
        assertTrue("Newline string should return true", StringUtils.isNullOrEmpty("\n"));
        assertTrue("Mixed whitespace should return true", StringUtils.isNullOrEmpty("  \t\n  "));
    }

    @Test
    public void testIsNullOrEmpty_withValidString() {
        assertFalse("Valid string should return false", StringUtils.isNullOrEmpty("hello"));
        assertFalse("String with leading/trailing spaces should return false",
                StringUtils.isNullOrEmpty("  hello  "));
    }

    @Test
    public void testExceedsLength_withNull() {
        assertFalse("Null string should not exceed length", StringUtils.exceedsLength(null, 10));
    }

    @Test
    public void testExceedsLength_withinLimit() {
        assertFalse("String within limit should return false",
                StringUtils.exceedsLength("hello", 10));
        assertFalse("String exactly at limit should return false",
                StringUtils.exceedsLength("hello", 5));
    }

    @Test
    public void testExceedsLength_exceedingLimit() {
        assertTrue("String exceeding limit should return true",
                StringUtils.exceedsLength("hello world", 5));
        assertTrue("String with 101 chars should exceed 100",
                StringUtils.exceedsLength("a".repeat(101), 100));
    }

    @Test
    public void testExceedsLength_withWhitespace() {
        // "  hello  " has 9 chars, but trimmed it's 5
        assertFalse("Trimmed string within limit should return false",
                StringUtils.exceedsLength("  hello  ", 10));
        assertTrue("Trimmed string exceeding limit should return true",
                StringUtils.exceedsLength("  hello  ", 4));
    }

    @Test
    public void testTrimSafely_withNull() {
        assertEquals("Null should return empty string", "", StringUtils.trimSafely(null));
    }

    @Test
    public void testTrimSafely_withEmptyString() {
        assertEquals("Empty string should return empty string", "", StringUtils.trimSafely(""));
    }

    @Test
    public void testTrimSafely_withWhitespace() {
        assertEquals("Whitespace should be trimmed", "", StringUtils.trimSafely("   "));
        assertEquals("Leading/trailing whitespace should be trimmed",
                "hello", StringUtils.trimSafely("  hello  "));
    }

    @Test
    public void testTrimSafely_withValidString() {
        assertEquals("Valid string should be returned as is", "hello", StringUtils.trimSafely("hello"));
        assertEquals("String with internal spaces should preserve them",
                "hello world", StringUtils.trimSafely("hello world"));
    }

    @Test
    public void testTrimSafely_withMixedWhitespace() {
        assertEquals("Mixed whitespace should be trimmed", "test",
                StringUtils.trimSafely("\t\n  test  \n\t"));
    }
}
