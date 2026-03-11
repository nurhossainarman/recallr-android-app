package comp3350.flashcard.objects;

import java.io.Serializable;
import java.util.Objects;

/**
 * Represents a single flashcard with a front (question) and back (answer) side.
 * Flashcards belong to a specific deck identified by deckId.
 */
public class Flashcard implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private int id;
    private String front;
    private String back;
    private int deckId;
    private long createdAt;
    private boolean isKnown;
    
    // Future fields need to be implemented for Iteration 2 and more
    // private int timesReviewed;
    // private long lastReviewedAt;
    // private int repetitionInterval;
    // private double easeFactor;
    // private long nextReviewDate;

    /**
     * Default constructor required for certain frameworks and testing.
     */
    public Flashcard() {
        this.createdAt = System.currentTimeMillis();
    }

    /**
     * Creates a new flashcard without an ID (for new cards before persistence).
     *
     * @param front  The question or front side of the card
     * @param back   The answer or back side of the card
     * @param deckId The ID of the deck this card belongs to
     */
    public Flashcard(String front, String back, int deckId) {
        this();
        setFront(front);
        setBack(back);
        setDeckId(deckId);
        setIsKnown(false);
    }

    /**
     * Creates a flashcard with all fields specified (for loading from persistence).
     *
     * @param id        The unique identifier for this card
     * @param front     The question or front side of the card
     * @param back      The answer or back side of the card
     * @param deckId    The ID of the deck this card belongs to
     * @param createdAt The timestamp when this card was created
     */
    public Flashcard(int id, String front, String back, int deckId, long createdAt) {
        this.id = id;
        setFront(front);
        setBack(back);
        setDeckId(deckId);
        this.createdAt = createdAt;
    }

    // ==================== Getters ====================

    public int getId() {
        return id;
    }

    public String getFront() {
        return front;
    }

    public String getBack() {
        return back;
    }

    public int getDeckId() {
        return deckId;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public boolean getIsKnown() {
        return isKnown;
    }

    // ==================== Setters ====================

    public void setId(int id) {
        this.id = id;
    }

    public void setFront(String front) {
        if (front == null || front.trim().isEmpty()) {
            throw new IllegalArgumentException("Front side of flashcard cannot be null or empty");
        }
        this.front = front.trim();
    }

    public void setBack(String back) {
        if (back == null || back.trim().isEmpty()) {
            throw new IllegalArgumentException("Back side of flashcard cannot be null or empty");
        }
        this.back = back.trim();
    }

    public void setDeckId(int deckId) {
        if (deckId < 0) {
            throw new IllegalArgumentException("Deck ID cannot be negative");
        }
        this.deckId = deckId;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public void setIsKnown( boolean isKnown) {
        this.isKnown = isKnown;
    }

    // ==================== Utility Methods ====================

    /**
     * Checks if this flashcard has been persisted (has a valid ID).
     *
     * @return true if the flashcard has been saved to persistence
     */
    public boolean isPersisted() {
        return id > 0;
    }

    /**
     * Creates a copy of this flashcard with updated content.
     *
     * @param newFront The new front side content
     * @param newBack  The new back side content
     * @return A new Flashcard instance with updated content
     */
    public Flashcard withUpdatedContent(String newFront, String newBack) {
        return new Flashcard(this.id, newFront, newBack, this.deckId, this.createdAt);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Flashcard flashcard = (Flashcard) o;
        return id == flashcard.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Flashcard{" +
                "id=" + id +
                ", front='" + front + '\'' +
                ", back='" + back + '\'' +
                ", deckId=" + deckId +
                ", createdAt=" + createdAt +
                ", isKnown=" + isKnown +
                '}';
    }
}
