package comp3350.flashcard.presentation.viewmodel;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import comp3350.flashcard.objects.Deck;

/**
 * ViewModel for displaying Deck information in the UI.
 * Wraps a Deck object and provides formatted data for presentation.
 */
public class DeckViewModel {
    private final Deck deck;
    private final int cardCount;

    public DeckViewModel(Deck deck, int cardCount) {
        this.deck = deck;
        this.cardCount = cardCount;
    }

    public int getId() {
        return deck.getId();
    }

    public String getName() {
        return deck.getName();
    }

    public String getDescription() {
        return deck.getDescription();
    }

    public int getCardCount() {
        return cardCount;
    }

    /**
     * Returns a human-readable string of the creation date.
     *
     * @return Formatted date string (e.g., "Jan 1, 2024")
     */
    public String getFormattedCreatedDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
                .withZone(ZoneId.systemDefault());
        return formatter.format(Instant.ofEpochMilli(deck.getCreatedAt()));
    }

    /**
     * Checks if the deck has any cards.
     *
     * @return true if the deck contains one or more cards
     */
    public boolean hasCards() {

        return cardCount > 0;
    }

    /**
     * Checks if the deck has been studied.
     *
     * @return true if the deck has a last studied timestamp
     */
    public boolean hasBeenStudied() {

        return deck.hasBeenStudied();
    }
}
