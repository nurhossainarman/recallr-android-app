package comp3350.flashcard.objects;

import java.io.Serializable;
import java.util.Objects;

import comp3350.flashcard.constants.ValidationConstants;

/**
 * Represents a collection of flashcards organized by subject or topic.
 * Each deck has a unique name and contains zero or more flashcards.
 *
 * This class follows immutability principles for core fields (id, createdAt).
 * Use factory methods to create instances and withUpdated*() methods to modify.
 */
public class Deck implements Serializable {

    private static final long serialVersionUID = 1L;

    private final int id;
    private String name;
    private String description;
    private final long createdAt;
    private long lastStudiedAt;

    // Future fields for Iteration 2 and more
    // private int knownCardCount;
    // private int studySessionCount;
    // private long totalStudyTime;

    /**
     * Package-private constructor - only persistence layer can call directly.
     * Use factory methods createNew() or fromPersistence() instead.
     *
     * @param id            The unique identifier for this deck
     * @param name          The name of the deck
     * @param description   A brief description of the deck's content
     * @param createdAt     The timestamp when this deck was created
     * @param lastStudiedAt The timestamp when this deck was last studied
     */
    Deck(int id, String name, String description, long createdAt, long lastStudiedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
        this.lastStudiedAt = lastStudiedAt;
    }

    /**
     * Public factory for creating new (unpersisted) decks.
     * ID will be INVALID_ID until persisted.
     *
     * @param name        The name of the deck
     * @param description A brief description of the deck's content
     * @return A new Deck instance ready to be persisted
     */
    public static Deck createNew(String name, String description) {
        return new Deck(
            ValidationConstants.INVALID_ID,
            name,
            description != null ? description : "",
            System.currentTimeMillis(),
            0
        );
    }

    /**
     * Public factory for reconstructing decks from persistence.
     * Only the persistence layer should call this method.
     *
     * @param id            The unique identifier for this deck
     * @param name          The name of the deck
     * @param description   A brief description of the deck's content
     * @param createdAt     The timestamp when this deck was created
     * @param lastStudiedAt The timestamp when this deck was last studied
     * @return A Deck instance loaded from persistence
     */
    public static Deck fromPersistence(int id, String name, String description,
                                       long createdAt, long lastStudiedAt) {
        return new Deck(id, name, description, createdAt, lastStudiedAt);
    }

    // ==================== Getters ====================

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getLastStudiedAt() {
        return lastStudiedAt;
    }

    // ==================== Setters (Limited - prefer immutable updates) ====================

    /**
     * Sets the name. Only use for internal updates.
     * Prefer withUpdatedName() for creating modified copies.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the description. Only use for internal updates.
     * Prefer withUpdatedDescription() for creating modified copies.
     */
    public void setDescription(String description) {
        this.description = description != null ? description : "";
    }

    /**
     * Updates the last studied timestamp.
     */
    public void setLastStudiedAt(long lastStudiedAt) {
        this.lastStudiedAt = lastStudiedAt;
    }

    // ==================== Utility Methods ====================

    /**
     * Checks if this deck has been persisted (has a valid ID).
     *
     * @return true if the deck has been saved to persistence
     */
    public boolean isPersisted() {
        return id > 0;
    }

    /**
     * Checks if this deck has ever been studied.
     *
     * @return true if the deck has been studied at least once
     */
    public boolean hasBeenStudied() {
        return lastStudiedAt > 0;
    }

    /**
     * Updates the last studied timestamp to now.
     */
    public void markAsStudied() {
        this.lastStudiedAt = System.currentTimeMillis();
    }

    /**
     * Creates a copy of this deck with an updated name (immutable update).
     *
     * @param newName The new name for the deck
     * @return A new Deck instance with the updated name
     */
    public Deck withUpdatedName(String newName) {
        return new Deck(this.id, newName, this.description, this.createdAt, this.lastStudiedAt);
    }

    /**
     * Creates a copy of this deck with an updated description (immutable update).
     *
     * @param newDescription The new description for the deck
     * @return A new Deck instance with the updated description
     */
    public Deck withUpdatedDescription(String newDescription) {
        return new Deck(this.id, this.name, newDescription, this.createdAt, this.lastStudiedAt);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Deck deck = (Deck) o;
        return id == deck.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Deck{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", createdAt=" + createdAt +
                ", lastStudiedAt=" + lastStudiedAt +
                '}';
    }
}
