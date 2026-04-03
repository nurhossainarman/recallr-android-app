package comp3350.flashcard.logic.validators;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

import comp3350.flashcard.constants.ValidationConstants;
import comp3350.flashcard.persistence.DeckPersistence;
import comp3350.flashcard.persistence.stubs.DeckPersistenceStub;

public class DeckValidatorTest {

    private DeckValidator validator;
    private DeckPersistence deckPersistence;

    @Before
    public void setUp() {
        deckPersistence = new DeckPersistenceStub();
        deckPersistence.clearAll();
        validator = new DeckValidator(deckPersistence);
    }

    @After
    public void tearDown() {
        validator = null;
        deckPersistence = null;
    }

    // ---------------- null name validation ----------------

    @Test
    public void validate_nullName_returnsError() {
        ValidationResult result = validator.validate(null, ValidationConstants.INVALID_ID);
        assertFalse(result.isValid());
        assertNotNull(result.getErrorMessage());
    }

    // ---------------- empty name validation ----------------

    @Test
    public void validate_emptyName_returnsError() {
        ValidationResult result = validator.validate("", ValidationConstants.INVALID_ID);
        assertFalse(result.isValid());
        assertNotNull(result.getErrorMessage());
    }

    @Test
    public void validate_whitespaceOnlyName_returnsError() {
        ValidationResult result = validator.validate("   ", ValidationConstants.INVALID_ID);
        assertFalse(result.isValid());
        assertNotNull(result.getErrorMessage());
    }

    // ---------------- length validation ----------------

    @Test
    public void validate_nameTooLong_returnsError() {
        String longName = "a".repeat(101);
        ValidationResult result = validator.validate(longName, ValidationConstants.INVALID_ID);
        assertFalse(result.isValid());
        assertNotNull(result.getErrorMessage());
    }

    @Test
    public void validate_nameExactlyMaxLength_returnsSuccess() {
        String maxName = "a".repeat(100);
        ValidationResult result = validator.validate(maxName, ValidationConstants.INVALID_ID);
        assertTrue(result.isValid());
        assertNull(result.getErrorMessage());
    }

    // ---------------- uniqueness validation ----------------

    @Test
    public void validate_duplicateName_returnsError() {
        // Insert a deck so uniqueness check has something to find
        deckPersistence.insertDeck(comp3350.flashcard.objects.Deck.createNew("Existing Deck", ""));

        ValidationResult result = validator.validate("Existing Deck", ValidationConstants.INVALID_ID);
        assertFalse(result.isValid());
        assertNotNull(result.getErrorMessage());
    }

    @Test
    public void validate_sameName_excludedDeck_returnsSuccess() {
        // Deck renaming to its own name should be allowed
        comp3350.flashcard.objects.Deck deck = deckPersistence.insertDeck(
                comp3350.flashcard.objects.Deck.createNew("My Deck", ""));

        ValidationResult result = validator.validate("My Deck", deck.getId());
        assertTrue(result.isValid());
    }

    // ---------------- valid deck passes ----------------

    @Test
    public void validate_validName_returnsSuccess() {
        ValidationResult result = validator.validate("Spanish Vocabulary", ValidationConstants.INVALID_ID);
        assertTrue(result.isValid());
        assertNull(result.getErrorMessage());
    }

    // ---------------- error messages are descriptive ----------------

    @Test
    public void validate_emptyName_errorMessageDescribesIssue() {
        ValidationResult result = validator.validate("", ValidationConstants.INVALID_ID);
        assertFalse(result.isValid());
        assertTrue("Error message should mention empty", result.getErrorMessage().toLowerCase().contains("empty"));
    }

    @Test
    public void validate_nameTooLong_errorMessageDescribesIssue() {
        ValidationResult result = validator.validate("a".repeat(101), ValidationConstants.INVALID_ID);
        assertFalse(result.isValid());
        assertTrue("Error message should mention exceed", result.getErrorMessage().toLowerCase().contains("exceed"));
    }

    @Test
    public void validate_duplicateName_errorMessageDescribesIssue() {
        deckPersistence.insertDeck(comp3350.flashcard.objects.Deck.createNew("Taken Name", ""));

        ValidationResult result = validator.validate("Taken Name", ValidationConstants.INVALID_ID);
        assertFalse(result.isValid());
        assertTrue("Error message should mention already exists",
                result.getErrorMessage().toLowerCase().contains("already exists"));
    }
}
