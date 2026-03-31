package comp3350.flashcard.logic.validators;

import comp3350.flashcard.constants.ValidationConstants;
import comp3350.flashcard.persistence.DeckPersistence;
import comp3350.flashcard.utils.StringUtils;

public class DeckValidator implements IDeckValidator {

    private final DeckPersistence deckPersistence;

    /**
     * Constructor with dependency injection
     *
     * @param deckPersistence used to perform the uniqueness check
     */
    public DeckValidator(DeckPersistence deckPersistence) {
        this.deckPersistence = deckPersistence;
    }

    /**
     * Validates a deck name against all rules.
     *
     * @param name          the deck name to validate
     * @param excludeDeckId the deck ID to exclude from uniqueness checks, use
     *                      ValidationConstants.INVALID_ID for new decks
     * @return a ValidationResult indicating success or describing the first
     *         validation error
     */
    public ValidationResult validate(String name, int excludeDeckId) {
        if (StringUtils.isNullOrEmpty(name)) {
            return ValidationResult.error("Deck name cannot be empty");
        }

        if (StringUtils.exceedsLength(name, ValidationConstants.MAX_DECK_NAME_LENGTH)) {
            return ValidationResult.error("Deck name cannot exceed " +
                    ValidationConstants.MAX_DECK_NAME_LENGTH + " characters");
        }

        if (deckPersistence.deckNameExists(name, excludeDeckId)) {
            return ValidationResult.error("A deck with this name already exists");
        }

        return ValidationResult.success();
    }
}
