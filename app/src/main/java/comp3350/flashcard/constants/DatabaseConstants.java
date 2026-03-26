package comp3350.flashcard.constants;

/**
 * Constants for database configuration, table names, and column names.
 * Centralized to maintain consistency and ease of maintenance.
 */
public final class DatabaseConstants {

    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private DatabaseConstants() {
        throw new AssertionError("Cannot instantiate constants class");
    }

    // Database Configuration
    /**
     * Name of the SQLite database file.
     */
    public static final String DATABASE_NAME = "flashcard.db";

    /**
     * Current version of the database schema.
     * Increment this when making schema changes.
     */
    public static final int DATABASE_VERSION = 2;

    // Table Names
    /**
     * Name of the decks table.
     */
    public static final String TABLE_DECKS = "decks";

    /**
     * Name of the flashcards table.
     */
    public static final String TABLE_FLASHCARDS = "flashcards";

    // Deck Table Columns
    /**
     * Primary key column for decks table.
     */
    public static final String DECK_ID = "id";

    /**
     * Name column for decks table.
     */
    public static final String DECK_NAME = "name";

    /**
     * Description column for decks table.
     */
    public static final String DECK_DESCRIPTION = "description";

    /**
     * Creation timestamp column for decks table.
     */
    public static final String DECK_CREATED_AT = "created_at";

    /**
     * Last studied timestamp column for decks table.
     */
    public static final String DECK_LAST_STUDIED_AT = "last_studied_at";

    // Flashcard Table Columns
    /**
     * Primary key column for flashcards table.
     */
    public static final String FLASHCARD_ID = "id";

    /**
     * Front content column for flashcards table.
     */
    public static final String FLASHCARD_FRONT = "front";

    /**
     * Back content column for flashcards table.
     */
    public static final String FLASHCARD_BACK = "back";

    /**
     * Foreign key column linking flashcard to deck.
     */
    public static final String FLASHCARD_DECK_ID = "deck_id";

    /**
     * Creation timestamp column for flashcards table.
     */
    public static final String FLASHCARD_CREATED_AT = "created_at";

    /**
     * Boolean column indicating if flashcard is marked as known.
     */
    public static final String FLASHCARD_IS_KNOWN = "is_known";
}
