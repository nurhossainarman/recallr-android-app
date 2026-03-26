package comp3350.flashcard.application;

import android.content.Context;
import comp3350.flashcard.logic.FlashcardManager;
import comp3350.flashcard.logic.DeckManager;
import comp3350.flashcard.logic.IDeckManager;
import comp3350.flashcard.logic.IFlashcardManager;
import comp3350.flashcard.logic.IStudySession;
import comp3350.flashcard.logic.StudySessionManager;
import comp3350.flashcard.persistence.FlashcardPersistence;
import comp3350.flashcard.persistence.DeckPersistence;
import comp3350.flashcard.persistence.sqlite.DeckPersistenceSQLite;
import comp3350.flashcard.persistence.sqlite.FlashcardPersistenceSQLite;
import comp3350.flashcard.persistence.stubs.DeckPersistenceStub;
import comp3350.flashcard.persistence.stubs.FlashcardPersistenceStub;

/**
 * Services class - manages application-wide service instances
 * Implements a simple service locator pattern for dependency management
 */
public class Services {
    private static IFlashcardManager flashcardManager;
    private static IDeckManager deckManager;
    private static IStudySession studySession;
    private static FlashcardPersistence flashcardPersistence;
    private static DeckPersistence deckPersistence;
    private static Context appContext;

    public static void setContext(Context context) {
        appContext = context.getApplicationContext();
    }

    public static FlashcardPersistence getFlashcardPersistence() {
        if (flashcardPersistence == null) {
            if (appContext == null) {
                flashcardPersistence = new FlashcardPersistenceStub();
            } else {
                flashcardPersistence = new FlashcardPersistenceSQLite(appContext);
            }
        }
        return flashcardPersistence;
    }

    public static DeckPersistence getDeckPersistence() {
        if (deckPersistence == null) {
            if (appContext == null) {
                deckPersistence = new DeckPersistenceStub();
            } else {
                deckPersistence = new DeckPersistenceSQLite(appContext);
            }
        }
        return deckPersistence;
    }

    public static IFlashcardManager getFlashcardManager() {
        if (flashcardManager == null) {
            flashcardManager = new FlashcardManager(getFlashcardPersistence());
        }
        return flashcardManager;
    }

    public static IDeckManager getDeckManager() {
        if (deckManager == null) {
            deckManager = new DeckManager(getDeckPersistence(), getFlashcardPersistence());
        }
        return deckManager;
    }

    public static IStudySession getStudySession() {
        if (studySession == null) {
            studySession = new StudySessionManager(getFlashcardPersistence());
        }
        return studySession;
    }

    public static void initialize(DeckPersistence deckPersist, FlashcardPersistence flashcardPersist) {
        deckPersistence = deckPersist;
        flashcardPersistence = flashcardPersist;
        deckManager = new DeckManager(deckPersist, flashcardPersist);
        flashcardManager = new FlashcardManager(flashcardPersist);
        studySession = new StudySessionManager(flashcardPersist);
    }

    public static void cleanup() {
        flashcardManager = null;
        deckManager = null;
        studySession = null;
        flashcardPersistence = null;
        deckPersistence = null;
    }
}
