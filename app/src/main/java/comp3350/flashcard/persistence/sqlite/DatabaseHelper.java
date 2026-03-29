package comp3350.flashcard.persistence.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import comp3350.flashcard.constants.DatabaseConstants;

public class DatabaseHelper extends SQLiteOpenHelper {

    public DatabaseHelper(Context context) {
        super(context, DatabaseConstants.DATABASE_NAME, null, DatabaseConstants.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createDecksTable = "CREATE TABLE " + DatabaseConstants.TABLE_DECKS + " (" +
                DatabaseConstants.DECK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DatabaseConstants.DECK_NAME + " TEXT NOT NULL UNIQUE, " +
                DatabaseConstants.DECK_DESCRIPTION + " TEXT, " +
                DatabaseConstants.DECK_CREATED_AT + " INTEGER NOT NULL, " +
                DatabaseConstants.DECK_LAST_STUDIED_AT + " INTEGER DEFAULT 0" +
                ");";
        db.execSQL(createDecksTable);

        String createFlashcardsTable = "CREATE TABLE " + DatabaseConstants.TABLE_FLASHCARDS + " (" +
                DatabaseConstants.FLASHCARD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DatabaseConstants.FLASHCARD_FRONT + " TEXT NOT NULL, " +
                DatabaseConstants.FLASHCARD_BACK + " TEXT NOT NULL, " +
                DatabaseConstants.FLASHCARD_DECK_ID + " INTEGER NOT NULL, " +
                DatabaseConstants.FLASHCARD_CREATED_AT + " INTEGER NOT NULL, " +
                DatabaseConstants.FLASHCARD_IS_KNOWN + " INTEGER DEFAULT 0, " +
                "FOREIGN KEY(" + DatabaseConstants.FLASHCARD_DECK_ID + ") REFERENCES " +
                DatabaseConstants.TABLE_DECKS + "(" + DatabaseConstants.DECK_ID + ") ON DELETE CASCADE" +
                ");";
        db.execSQL(createFlashcardsTable);

        db.execSQL("CREATE INDEX idx_flashcard_deck_id ON " +
                DatabaseConstants.TABLE_FLASHCARDS + "(" + DatabaseConstants.FLASHCARD_DECK_ID + ");");

        insertSampleData(db);
    }

    private void insertSampleData(SQLiteDatabase db) {
        long now = System.currentTimeMillis();

        db.execSQL("INSERT INTO " + DatabaseConstants.TABLE_DECKS + " VALUES " +
                "(1, 'Spanish Vocabulary', " +
                "'Common Spanish words and phrases for beginners', " +
                (now - 86400000) + ", " + (now - 3600000) + ");");

        db.execSQL("INSERT INTO " + DatabaseConstants.TABLE_DECKS + " VALUES " +
                "(2, 'Java Basics', " +
                "'Fundamental Java programming concepts', " +
                (now - 172800000) + ", " + (now - 7200000) + ");");

        db.execSQL("INSERT INTO " + DatabaseConstants.TABLE_DECKS + " VALUES " +
                "(3, 'World Capitals', " +
                "'Capital cities of countries around the world', " +
                (now - 259200000) + ", 0);");

        db.execSQL("INSERT INTO " + DatabaseConstants.TABLE_FLASHCARDS + " (id, front, back, deck_id, created_at, is_known) VALUES " +
                "(1, 'Hello', 'Hola', 1, " + now + ", 0);");
        db.execSQL("INSERT INTO " + DatabaseConstants.TABLE_FLASHCARDS + " (id, front, back, deck_id, created_at, is_known) VALUES " +
                "(2, 'Goodbye', 'Adiós', 1, " + now + ", 0);");
        db.execSQL("INSERT INTO " + DatabaseConstants.TABLE_FLASHCARDS + " (id, front, back, deck_id, created_at, is_known) VALUES " +
                "(3, 'Thank you', 'Gracias', 1, " + now + ", 0);");

        db.execSQL("INSERT INTO " + DatabaseConstants.TABLE_FLASHCARDS + " (id, front, back, deck_id, created_at, is_known) VALUES " +
                "(4, 'What is a class?', 'A blueprint for creating objects', 2, " + now + ", 0);");
        db.execSQL("INSERT INTO " + DatabaseConstants.TABLE_FLASHCARDS + " (id, front, back, deck_id, created_at, is_known) VALUES " +
                "(5, 'What is inheritance?', 'A mechanism where a class inherits properties from another class', 2, " + now + ", 0);");

        db.execSQL("INSERT INTO " + DatabaseConstants.TABLE_FLASHCARDS + " (id, front, back, deck_id, created_at, is_known) VALUES " +
                "(6, 'Capital of France', 'Paris', 3, " + now + ", 0);");
        db.execSQL("INSERT INTO " + DatabaseConstants.TABLE_FLASHCARDS + " (id, front, back, deck_id, created_at, is_known) VALUES " +
                "(7, 'Capital of Japan', 'Tokyo', 3, " + now + ", 0);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseConstants.TABLE_FLASHCARDS);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseConstants.TABLE_DECKS);
        onCreate(db);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }
}
