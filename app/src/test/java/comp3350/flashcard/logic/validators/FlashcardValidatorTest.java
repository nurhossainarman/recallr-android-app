package comp3350.flashcard.logic.validators;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

public class FlashcardValidatorTest {

    private FlashcardValidator validator;

    @Before
    public void setUp() {
        validator = new FlashcardValidator();
    }

    @After
    public void tearDown() {
        validator = null;
    }

    // ---------------- front null/empty validation ----------------

    @Test
    public void validate_nullFront_returnsError() {
        ValidationResult result = validator.validate(null, "Answer");
        assertFalse(result.isValid());
        assertNotNull(result.getErrorMessage());
    }

    @Test
    public void validate_emptyFront_returnsError() {
        ValidationResult result = validator.validate("", "Answer");
        assertFalse(result.isValid());
        assertNotNull(result.getErrorMessage());
    }

    @Test
    public void validate_whitespaceOnlyFront_returnsError() {
        ValidationResult result = validator.validate("   ", "Answer");
        assertFalse(result.isValid());
        assertNotNull(result.getErrorMessage());
    }

    // ---------------- back null/empty validation ----------------

    @Test
    public void validate_nullBack_returnsError() {
        ValidationResult result = validator.validate("Question", null);
        assertFalse(result.isValid());
        assertNotNull(result.getErrorMessage());
    }

    @Test
    public void validate_emptyBack_returnsError() {
        ValidationResult result = validator.validate("Question", "");
        assertFalse(result.isValid());
        assertNotNull(result.getErrorMessage());
    }

    @Test
    public void validate_whitespaceOnlyBack_returnsError() {
        ValidationResult result = validator.validate("Question", "   ");
        assertFalse(result.isValid());
        assertNotNull(result.getErrorMessage());
    }

    // ---------------- valid flashcard passes ----------------

    @Test
    public void validate_validFrontAndBack_returnsSuccess() {
        ValidationResult result = validator.validate("What is Java?", "A programming language");
        assertTrue(result.isValid());
        assertNull(result.getErrorMessage());
    }

    @Test
    public void validate_frontAndBackWithWhitespace_returnsSuccess() {
        // Non-empty strings with surrounding whitespace are still valid content
        ValidationResult result = validator.validate("  Question  ", "  Answer  ");
        assertTrue(result.isValid());
    }

    // ---------------- error messages are descriptive ----------------

    @Test
    public void validate_emptyFront_errorMessageDescribesIssue() {
        ValidationResult result = validator.validate("", "Answer");
        assertFalse(result.isValid());
        assertTrue("Error message should mention front",
                result.getErrorMessage().toLowerCase().contains("front"));
    }

    @Test
    public void validate_emptyBack_errorMessageDescribesIssue() {
        ValidationResult result = validator.validate("Question", "");
        assertFalse(result.isValid());
        assertTrue("Error message should mention back",
                result.getErrorMessage().toLowerCase().contains("back"));
    }
}
