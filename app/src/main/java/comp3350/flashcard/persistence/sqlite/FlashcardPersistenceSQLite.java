package comp3350.flashcard.persistence.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;
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
                    DatabaseHelper.TABLE_FLASHCARDS,
                    null,
                    null, null, null, null,
                    DatabaseHelper.FLASHCARD_CREATED_AT + " DESC"
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
                    DatabaseHelper.TABLE_FLASHCARDS,
                    null,
                    DatabaseHelper.FLASHCARD_DECK_ID + " = ?",
                    new String[]{String.valueOf(deckId)},
                    null, null,
                    DatabaseHelper.FLASHCARD_CREATED_AT + " ASC"
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
                    DatabaseHelper.TABLE_FLASHCARDS,
                    null,
                    DatabaseHelper.FLASHCARD_ID + " = ?",
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
            values.put(DatabaseHelper.FLASHCARD_FRONT, flashcard.getFront());
            values.put(DatabaseHelper.FLASHCARD_BACK, flashcard.getBack());
            values.put(DatabaseHelper.FLASHCARD_DECK_ID, flashcard.getDeckId());
            values.put(DatabaseHelper.FLASHCARD_CREATED_AT, System.currentTimeMillis());

            long id = db.insert(DatabaseHelper.TABLE_FLASHCARDS, null, values);

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
            values.put(DatabaseHelper.FLASHCARD_FRONT, flashcard.getFront());
            values.put(DatabaseHelper.FLASHCARD_BACK, flashcard.getBack());
            values.put(DatabaseHelper.FLASHCARD_DECK_ID, flashcard.getDeckId());

            int rows = db.update(
                    DatabaseHelper.TABLE_FLASHCARDS,
                    values,
                    DatabaseHelper.FLASHCARD_ID + " = ?",
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
                    DatabaseHelper.TABLE_FLASHCARDS,
                    DatabaseHelper.FLASHCARD_ID + " = ?",
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
                    DatabaseHelper.TABLE_FLASHCARDS,
                    DatabaseHelper.FLASHCARD_DECK_ID + " = ?",
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
                    "SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_FLASHCARDS +
                            " WHERE " + DatabaseHelper.FLASHCARD_DECK_ID + " = ?",
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
            db.delete(DatabaseHelper.TABLE_FLASHCARDS, null, null);
        } catch (Exception e) {
            throw new PersistenceException("Error clearing all flashcards", e);
        } finally {
            if (db != null) db.close();
        }
    }

    private Flashcard flashcardFromCursor(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.FLASHCARD_ID));
        String front = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.FLASHCARD_FRONT));
        String back = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.FLASHCARD_BACK));
        int deckId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.FLASHCARD_DECK_ID));
        long createdAt = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.FLASHCARD_CREATED_AT));

        return new Flashcard(id, front, back, deckId, createdAt);
    }
}
