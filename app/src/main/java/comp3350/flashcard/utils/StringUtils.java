package comp3350.flashcard.utils;

/**
 * Utility class for common string operations and validations.
 * Provides reusable methods to avoid code duplication.
 */
public final class StringUtils {

    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private StringUtils() {
        throw new AssertionError("Cannot instantiate utility class");
    }

    /**
     * Checks if a string is null or empty (after trimming whitespace).
     *
     * @param str the string to check
     * @return true if the string is null or empty/whitespace-only, false otherwise
     */
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * Checks if a string exceeds a specified maximum length (after trimming).
     *
     * @param str the string to check
     * @param maxLength the maximum allowed length
     * @return true if the string exceeds the maximum length, false otherwise
     */
    public static boolean exceedsLength(String str, int maxLength) {
        return str != null && str.trim().length() > maxLength;
    }

    /**
     * Safely trims a string, returning an empty string if the input is null.
     *
     * @param str the string to trim
     * @return the trimmed string, or an empty string if input was null
     */
    public static String trimSafely(String str) {
        return str == null ? "" : str.trim();
    }
}
