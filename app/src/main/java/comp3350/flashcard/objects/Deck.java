package comp3350.flashcard.objects;

import java.io.Serializable;
import java.util.Objects;

/**
 * Represents a collection of flashcards organized by subject or topic.
 * Each deck has a unique name and contains zero or more flashcards.
 */
public class Deck implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private int id;
    private String name;
    private String description;
    private long createdAt;
    private long lastStudiedAt;
    
    // These are computed fields, not stored directly
    // Will be populated by the logic layer when needed
    private transient int cardCount;
    
    // Future fields for Iteration 2 and more 
    // private int knownCardCount;
    // private int studySessionCount;
    // private long totalStudyTime;

    /**
     * Default constructor required for certain frameworks and testing.
     */
    public Deck() {
        this.createdAt = System.currentTimeMillis();
        this.lastStudiedAt = 0;
        this.cardCount = 0;
        this.description = "";
    }

    /**
     * Creates a new deck with just a name (for new decks before persistence).
     *
     * @param name The name of the deck
     */
    public Deck(String name) {
        this();
        setName(name);
    }

    /**
     * Creates a new deck with name and description.
     *
     * @param name        The name of the deck
     * @param description A brief description of the deck's content
     */
    public Deck(String name, String description) {
        this(name);
        setDescription(description);
    }

    /**
     * Creates a deck with all fields specified (for loading from persistence).
     *
     * @param id            The unique identifier for this deck
     * @param name          The name of the deck
     * @param description   A brief description of the deck's content
     * @param createdAt     The timestamp when this deck was created
     * @param lastStudiedAt The timestamp when this deck was last studied
     */
    public Deck(int id, String name, String description, long createdAt, long lastStudiedAt) {
        this.id = id;
        setName(name);
        setDescription(description);
        this.createdAt = createdAt;
        this.lastStudiedAt = lastStudiedAt;
        this.cardCount = 0;
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

    public int getCardCount() {
        return cardCount;
    }

    // ==================== Setters ====================

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Deck name cannot be null or empty");
        }
        if (name.trim().length() > 100) {
            throw new IllegalArgumentException("Deck name cannot exceed 100 characters");
        }
        this.name = name.trim();
    }

    public void setDescription(String description) {
        if (description != null && description.length() > 500) {
            throw new IllegalArgumentException("Deck description cannot exceed 500 characters");
        }
        this.description = description != null ? description.trim() : "";
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public void setLastStudiedAt(long lastStudiedAt) {
        this.lastStudiedAt = lastStudiedAt;
    }

    public void setCardCount(int cardCount) {
        if (cardCount < 0) {
            throw new IllegalArgumentException("Card count cannot be negative");
        }
        this.cardCount = cardCount;
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
     * Checks if this deck has any flashcards.
     *
     * @return true if the deck contains at least one flashcard
     */
    public boolean hasCards() {
        return cardCount > 0;
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
     * Creates a copy of this deck with a new name.
     *
     * @param newName The new name for the deck
     * @return A new Deck instance with the updated name
     */
    public Deck withUpdatedName(String newName) {
        Deck updated = new Deck(this.id, newName, this.description, this.createdAt, this.lastStudiedAt);
        updated.setCardCount(this.cardCount);
        return updated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
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
                ", cardCount=" + cardCount +
                ", createdAt=" + createdAt +
                ", lastStudiedAt=" + lastStudiedAt +
                '}';
    }
}
