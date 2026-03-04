package comp3350.flashcard.persistence.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "flashcard.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_DECKS = "decks";
    public static final String TABLE_FLASHCARDS = "flashcards";

    public static final String DECK_ID = "id";
    public static final String DECK_NAME = "name";
    public static final String DECK_DESCRIPTION = "description";
    public static final String DECK_CREATED_AT = "created_at";
    public static final String DECK_LAST_STUDIED_AT = "last_studied_at";

    public static final String FLASHCARD_ID = "id";
    public static final String FLASHCARD_FRONT = "front";
    public static final String FLASHCARD_BACK = "back";
    public static final String FLASHCARD_DECK_ID = "deck_id";
    public static final String FLASHCARD_CREATED_AT = "created_at";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createDecksTable = "CREATE TABLE " + TABLE_DECKS + " (" +
                DECK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DECK_NAME + " TEXT NOT NULL UNIQUE, " +
                DECK_DESCRIPTION + " TEXT, " +
                DECK_CREATED_AT + " INTEGER NOT NULL, " +
                DECK_LAST_STUDIED_AT + " INTEGER DEFAULT 0" +
                ");";
        db.execSQL(createDecksTable);

        String createFlashcardsTable = "CREATE TABLE " + TABLE_FLASHCARDS + " (" +
                FLASHCARD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                FLASHCARD_FRONT + " TEXT NOT NULL, " +
                FLASHCARD_BACK + " TEXT NOT NULL, " +
                FLASHCARD_DECK_ID + " INTEGER NOT NULL, " +
                FLASHCARD_CREATED_AT + " INTEGER NOT NULL, " +
                "FOREIGN KEY(" + FLASHCARD_DECK_ID + ") REFERENCES " +
                TABLE_DECKS + "(" + DECK_ID + ") ON DELETE CASCADE" +
                ");";
        db.execSQL(createFlashcardsTable);

        db.execSQL("CREATE INDEX idx_flashcard_deck_id ON " +
                TABLE_FLASHCARDS + "(" + FLASHCARD_DECK_ID + ");");

        insertSampleData(db);
    }

    private void insertSampleData(SQLiteDatabase db) {
        long now = System.currentTimeMillis();

        db.execSQL("INSERT INTO " + TABLE_DECKS + " VALUES " +
                "(1, 'Spanish Vocabulary', " +
                "'Common Spanish words and phrases for beginners', " +
                (now - 86400000) + ", " + (now - 3600000) + ");");

        db.execSQL("INSERT INTO " + TABLE_DECKS + " VALUES " +
                "(2, 'Java Basics', " +
                "'Fundamental Java programming concepts', " +
                (now - 172800000) + ", " + (now - 7200000) + ");");

        db.execSQL("INSERT INTO " + TABLE_DECKS + " VALUES " +
                "(3, 'World Capitals', " +
                "'Capital cities of countries around the world', " +
                (now - 259200000) + ", 0);");

        db.execSQL("INSERT INTO " + TABLE_FLASHCARDS + " VALUES " +
                "(1, 'Hello', 'Hola', 1, " + now + ");");
        db.execSQL("INSERT INTO " + TABLE_FLASHCARDS + " VALUES " +
                "(2, 'Goodbye', 'Adiós', 1, " + now + ");");
        db.execSQL("INSERT INTO " + TABLE_FLASHCARDS + " VALUES " +
                "(3, 'Thank you', 'Gracias', 1, " + now + ");");

        db.execSQL("INSERT INTO " + TABLE_FLASHCARDS + " VALUES " +
                "(4, 'What is a class?', 'A blueprint for creating objects', 2, " + now + ");");
        db.execSQL("INSERT INTO " + TABLE_FLASHCARDS + " VALUES " +
                "(5, 'What is inheritance?', 'A mechanism where a class inherits properties from another class', 2, " + now + ");");

        db.execSQL("INSERT INTO " + TABLE_FLASHCARDS + " VALUES " +
                "(6, 'Capital of France', 'Paris', 3, " + now + ");");
        db.execSQL("INSERT INTO " + TABLE_FLASHCARDS + " VALUES " +
                "(7, 'Capital of Japan', 'Tokyo', 3, " + now + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FLASHCARDS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DECKS);
        onCreate(db);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }
}
