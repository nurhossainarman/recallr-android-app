package comp3350.flashcard.persistence.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;
import comp3350.flashcard.constants.DatabaseConstants;
import comp3350.flashcard.objects.Flashcard;
import comp3350.flashcard.persistence.FlashcardPersistence;
import comp3350.flashcard.persistence.PersistenceException;

public class FlashcardPersistenceSQLite implements FlashcardPersistence {
    private final DatabaseHelper dbHelper;

    public FlashcardPersistenceSQLite(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    @Override
    public List<Flashcard> getAllFlashcards() {
        List<Flashcard> flashcards = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();
            cursor = db.query(
                    DatabaseConstants.TABLE_FLASHCARDS,
                    null,
                    null, null, null, null,
                    DatabaseConstants.FLASHCARD_CREATED_AT + " DESC"
            );

            while (cursor.moveToNext()) {
                flashcards.add(flashcardFromCursor(cursor));
            }
        } catch (Exception e) {
            throw new PersistenceException("Error getting all flashcards", e);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }

        return flashcards;
    }

    @Override
    public List<Flashcard> getFlashcardsByDeckId(int deckId) {
        List<Flashcard> flashcards = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();
            cursor = db.query(
                    DatabaseConstants.TABLE_FLASHCARDS,
                    null,
                    DatabaseConstants.FLASHCARD_DECK_ID + " = ?",
                    new String[]{String.valueOf(deckId)},
                    null, null,
                    DatabaseConstants.FLASHCARD_CREATED_AT + " ASC"
            );

            while (cursor.moveToNext()) {
                flashcards.add(flashcardFromCursor(cursor));
            }
        } catch (Exception e) {
            throw new PersistenceException("Error getting flashcards for deck: " + deckId, e);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }

        return flashcards;
    }

    @Override
    public Flashcard getFlashcardById(int flashcardId) {
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();
            cursor = db.query(
                    DatabaseConstants.TABLE_FLASHCARDS,
                    null,
                    DatabaseConstants.FLASHCARD_ID + " = ?",
                    new String[]{String.valueOf(flashcardId)},
                    null, null, null
            );

            if (cursor.moveToFirst()) {
                return flashcardFromCursor(cursor);
            }
            return null;
        } catch (Exception e) {
            throw new PersistenceException("Error getting flashcard by ID: " + flashcardId, e);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }
    }

    @Override
    public Flashcard insertFlashcard(Flashcard flashcard) {
        SQLiteDatabase db = null;

        try {
            db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DatabaseConstants.FLASHCARD_FRONT, flashcard.getFront());
            values.put(DatabaseConstants.FLASHCARD_BACK, flashcard.getBack());
            values.put(DatabaseConstants.FLASHCARD_DECK_ID, flashcard.getDeckId());
            values.put(DatabaseConstants.FLASHCARD_CREATED_AT, System.currentTimeMillis());
            values.put(DatabaseConstants.FLASHCARD_IS_KNOWN, flashcard.getIsKnown() ? 1 : 0);

            long id = db.insert(DatabaseConstants.TABLE_FLASHCARDS, null, values);

            if (id == -1) {
                throw new PersistenceException("Failed to insert flashcard");
            }

            return getFlashcardById((int) id);
        } catch (Exception e) {
            throw new PersistenceException("Error inserting flashcard", e);
        } finally {
            if (db != null) db.close();
        }
    }

    @Override
    public boolean updateFlashcard(Flashcard flashcard) {
        SQLiteDatabase db = null;

        try {
            db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DatabaseConstants.FLASHCARD_FRONT, flashcard.getFront());
            values.put(DatabaseConstants.FLASHCARD_BACK, flashcard.getBack());
            values.put(DatabaseConstants.FLASHCARD_DECK_ID, flashcard.getDeckId());
            values.put(DatabaseConstants.FLASHCARD_IS_KNOWN, flashcard.getIsKnown() ? 1 : 0);

            int rows = db.update(
                    DatabaseConstants.TABLE_FLASHCARDS,
                    values,
                    DatabaseConstants.FLASHCARD_ID + " = ?",
                    new String[]{String.valueOf(flashcard.getId())}
            );

            return rows > 0;
        } catch (Exception e) {
            throw new PersistenceException("Error updating flashcard", e);
        } finally {
            if (db != null) db.close();
        }
    }

    @Override
    public boolean deleteFlashcard(int flashcardId) {
        SQLiteDatabase db = null;

        try {
            db = dbHelper.getWritableDatabase();
            int rows = db.delete(
                    DatabaseConstants.TABLE_FLASHCARDS,
                    DatabaseConstants.FLASHCARD_ID + " = ?",
                    new String[]{String.valueOf(flashcardId)}
            );

            return rows > 0;
        } catch (Exception e) {
            throw new PersistenceException("Error deleting flashcard", e);
        } finally {
            if (db != null) db.close();
        }
    }

    @Override
    public int deleteFlashcardsByDeckId(int deckId) {
        SQLiteDatabase db = null;

        try {
            db = dbHelper.getWritableDatabase();
            int rows = db.delete(
                    DatabaseConstants.TABLE_FLASHCARDS,
                    DatabaseConstants.FLASHCARD_DECK_ID + " = ?",
                    new String[]{String.valueOf(deckId)}
            );

            return rows;
        } catch (Exception e) {
            throw new PersistenceException("Error deleting flashcards for deck: " + deckId, e);
        } finally {
            if (db != null) db.close();
        }
    }

    @Override
    public int getFlashcardCountByDeckId(int deckId) {
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();
            cursor = db.rawQuery(
                    "SELECT COUNT(*) FROM " + DatabaseConstants.TABLE_FLASHCARDS +
                            " WHERE " + DatabaseConstants.FLASHCARD_DECK_ID + " = ?",
                    new String[]{String.valueOf(deckId)}
            );

            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
            return 0;
        } catch (Exception e) {
            throw new PersistenceException("Error getting flashcard count for deck: " + deckId, e);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }
    }

    @Override
    public boolean flashcardExists(int flashcardId) {
        return getFlashcardById(flashcardId) != null;
    }

    @Override
    public void clearAll() {
        SQLiteDatabase db = null;

        try {
            db = dbHelper.getWritableDatabase();
            db.delete(DatabaseConstants.TABLE_FLASHCARDS, null, null);
        } catch (Exception e) {
            throw new PersistenceException("Error clearing all flashcards", e);
        } finally {
            if (db != null) db.close();
        }
    }

    private Flashcard flashcardFromCursor(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseConstants.FLASHCARD_ID));
        String front = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseConstants.FLASHCARD_FRONT));
        String back = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseConstants.FLASHCARD_BACK));
        int deckId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseConstants.FLASHCARD_DECK_ID));
        long createdAt = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseConstants.FLASHCARD_CREATED_AT));
        boolean isKnown = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseConstants.FLASHCARD_IS_KNOWN)) == 1;

        Flashcard flashcard = new Flashcard(id, front, back, deckId, createdAt);
        flashcard.setIsKnown(isKnown); //TODO:
        return flashcard;
    }
}
