package comp3350.flashcard.objects;

import java.io.Serializable;
import java.util.Objects;

import comp3350.flashcard.constants.ValidationConstants;

/**
 * Represents a single flashcard with a front (question) and back (answer) side.
 * Flashcards belong to a specific deck identified by deckId.
 *
 * This class follows immutability principles for core fields (id, deckId, createdAt).
 * Use factory methods to create instances and withUpdatedContent() to modify.
 */
public class Flashcard implements Serializable {

    private static final long serialVersionUID = 1L;

    private final int id;
    private String front;
    private String back;
    private final int deckId;
    private final long createdAt;
    private boolean isKnown;

    /**
     * Package-private constructor - only persistence layer can call directly.
     * Use factory methods createNew() or fromPersistence() instead.
     *
     * @param id        The unique identifier for this card
     * @param front     The question or front side of the card
     * @param back      The answer or back side of the card
     * @param deckId    The ID of the deck this card belongs to
     * @param createdAt The timestamp when this card was created
     * @param isKnown   Whether this card is marked as known
     */
    Flashcard(int id, String front, String back, int deckId, long createdAt, boolean isKnown) {
        this.id = id;
        this.front = front;
        this.back = back;
        this.deckId = deckId;
        this.createdAt = createdAt;
        this.isKnown = isKnown;
    }

    /**
     * Public factory for creating new (unpersisted) flashcards.
     * ID will be INVALID_ID until persisted.
     *
     * @param front  The question or front side of the card
     * @param back   The answer or back side of the card
     * @param deckId The ID of the deck this card belongs to
     * @return A new Flashcard instance ready to be persisted
     */
    public static Flashcard createNew(String front, String back, int deckId) {
        return new Flashcard(
            ValidationConstants.INVALID_ID,
            front,
            back,
            deckId,
            System.currentTimeMillis(),
            false
        );
    }

    /**
     * Public factory for reconstructing flashcards from persistence.
     * Only the persistence layer should call this method.
     *
     * @param id        The unique identifier for this card
     * @param front     The question or front side of the card
     * @param back      The answer or back side of the card
     * @param deckId    The ID of the deck this card belongs to
     * @param createdAt The timestamp when this card was created
     * @param isKnown   Whether this card is marked as known
     * @return A Flashcard instance loaded from persistence
     */
    public static Flashcard fromPersistence(int id, String front, String back,
                                            int deckId, long createdAt, boolean isKnown) {
        return new Flashcard(id, front, back, deckId, createdAt, isKnown);
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

    // ==================== Setters (Limited - prefer immutable updates) ====================

    public void setFront(String front) {
        this.front = front;
    }

    public void setBack(String back) {
        this.back = back;
    }

    public void setIsKnown(boolean isKnown) {
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
     * Creates a copy of this flashcard with updated content (immutable update).
     *
     * @param newFront The new front side content
     * @param newBack  The new back side content
     * @return A new Flashcard instance with updated content
     */
    public Flashcard withUpdatedContent(String newFront, String newBack) {
        return new Flashcard(this.id, newFront, newBack, this.deckId, this.createdAt, this.isKnown);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
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
