package comp3350.flashcard.logic;

import comp3350.flashcard.application.Services;

/**
 * Helper class for StudySessionManager to handle internal logic and formatting.
 */
public class StudySessionHelper {

    /**
     * Formats the progress text for display.
     * @param currentIndex index of current card
     * @param totalCards the total number of cards
     * @return a formatted string like "Card 1 of 10"
     */
    public static String formatProgressText(int currentIndex, int totalCards) {
        if (totalCards == 0) {
            return "No cards to display";
        }
        int displayPos = Math.min(currentIndex + 1, totalCards);
        return "Card " + displayPos + " of " + totalCards;
    }

    public static boolean isFinished(int currentIndex, int totalCards) {
        return totalCards == 0 || currentIndex >= totalCards;
    }

}
