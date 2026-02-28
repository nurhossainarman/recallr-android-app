package comp3350.flashcard.persistence.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;
import comp3350.flashcard.objects.Deck;
import comp3350.flashcard.persistence.DeckPersistence;
import comp3350.flashcard.persistence.PersistenceException;

public class DeckPersistenceSQLite implements DeckPersistence {
    private final DatabaseHelper dbHelper;

    public DeckPersistenceSQLite(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    @Override
    public List<Deck> getAllDecks() {
        List<Deck> decks = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();
            cursor = db.query(
                    DatabaseHelper.TABLE_DECKS,
                    null,
                    null, null, null, null,
                    DatabaseHelper.DECK_CREATED_AT + " DESC"
            );

            while (cursor.moveToNext()) {
                decks.add(deckFromCursor(cursor));
            }
        } catch (Exception e) {
            throw new PersistenceException("Error getting all decks", e);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }

        return decks;
    }

    @Override
    public Deck getDeckById(int deckId) {
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();
            cursor = db.query(
                    DatabaseHelper.TABLE_DECKS,
                    null,
                    DatabaseHelper.DECK_ID + " = ?",
                    new String[]{String.valueOf(deckId)},
                    null, null, null
            );

            if (cursor.moveToFirst()) {
                return deckFromCursor(cursor);
            }
            return null;
        } catch (Exception e) {
            throw new PersistenceException("Error getting deck by ID: " + deckId, e);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }
    }

    @Override
    public Deck getDeckByName(String name) {
        if (name == null) return null;

        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();
            cursor = db.query(
                    DatabaseHelper.TABLE_DECKS,
                    null,
                    "LOWER(" + DatabaseHelper.DECK_NAME + ") = LOWER(?)",
                    new String[]{name.trim()},
                    null, null, null
            );

            if (cursor.moveToFirst()) {
                return deckFromCursor(cursor);
            }
            return null;
        } catch (Exception e) {
            throw new PersistenceException("Error getting deck by name: " + name, e);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }
    }

    @Override
    public Deck insertDeck(Deck deck) {
        SQLiteDatabase db = null;

        try {
            db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.DECK_NAME, deck.getName());
            values.put(DatabaseHelper.DECK_DESCRIPTION, deck.getDescription());
            values.put(DatabaseHelper.DECK_CREATED_AT, System.currentTimeMillis());
            values.put(DatabaseHelper.DECK_LAST_STUDIED_AT, 0);

            long id = db.insert(DatabaseHelper.TABLE_DECKS, null, values);

            if (id == -1) {
                throw new PersistenceException("Failed to insert deck");
            }

            return getDeckById((int) id);
        } catch (Exception e) {
            throw new PersistenceException("Error inserting deck", e);
        } finally {
            if (db != null) db.close();
        }
    }

    @Override
    public boolean updateDeck(Deck deck) {
        SQLiteDatabase db = null;

        try {
            db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.DECK_NAME, deck.getName());
            values.put(DatabaseHelper.DECK_DESCRIPTION, deck.getDescription());
            values.put(DatabaseHelper.DECK_LAST_STUDIED_AT, deck.getLastStudiedAt());

            int rows = db.update(
                    DatabaseHelper.TABLE_DECKS,
                    values,
                    DatabaseHelper.DECK_ID + " = ?",
                    new String[]{String.valueOf(deck.getId())}
            );

            return rows > 0;
        } catch (Exception e) {
            throw new PersistenceException("Error updating deck", e);
        } finally {
            if (db != null) db.close();
        }
    }

    @Override
    public boolean deleteDeck(int deckId) {
        SQLiteDatabase db = null;

        try {
            db = dbHelper.getWritableDatabase();
            int rows = db.delete(
                    DatabaseHelper.TABLE_DECKS,
                    DatabaseHelper.DECK_ID + " = ?",
                    new String[]{String.valueOf(deckId)}
            );

            return rows > 0;
        } catch (Exception e) {
            throw new PersistenceException("Error deleting deck", e);
        } finally {
            if (db != null) db.close();
        }
    }

    @Override
    public int getDeckCount() {
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();
            cursor = db.rawQuery(
                    "SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_DECKS,
                    null
            );

            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
            return 0;
        } catch (Exception e) {
            throw new PersistenceException("Error getting deck count", e);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }
    }

    @Override
    public boolean deckExists(int deckId) {
        return getDeckById(deckId) != null;
    }

    @Override
    public boolean deckNameExists(String name, int excludeId) {
        if (name == null) return false;

        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();
            cursor = db.query(
                    DatabaseHelper.TABLE_DECKS,
                    new String[]{DatabaseHelper.DECK_ID},
                    "LOWER(" + DatabaseHelper.DECK_NAME + ") = LOWER(?) AND " +
                            DatabaseHelper.DECK_ID + " != ?",
                    new String[]{name.trim(), String.valueOf(excludeId)},
                    null, null, null
            );

            return cursor.getCount() > 0;
        } catch (Exception e) {
            throw new PersistenceException("Error checking deck name existence", e);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }
    }

    @Override
    public void clearAll() {
        SQLiteDatabase db = null;

        try {
            db = dbHelper.getWritableDatabase();
            db.delete(DatabaseHelper.TABLE_DECKS, null, null);
        } catch (Exception e) {
            throw new PersistenceException("Error clearing all decks", e);
        } finally {
            if (db != null) db.close();
        }
    }

    private Deck deckFromCursor(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.DECK_ID));
        String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.DECK_NAME));
        String description = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.DECK_DESCRIPTION));
        long createdAt = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.DECK_CREATED_AT));
        long lastStudiedAt = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.DECK_LAST_STUDIED_AT));

        return new Deck(id, name, description, createdAt, lastStudiedAt);
    }
}
