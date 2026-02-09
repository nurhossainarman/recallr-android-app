package comp3350.flashcard.presentation;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;

import comp3350.flashcard.R;
import comp3350.flashcard.application.Services;
import comp3350.flashcard.objects.Deck;
import comp3350.flashcard.persistence.DeckPersistence;
import comp3350.flashcard.persistence.FlashcardPersistence;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvDecks;
    private DeckAdapter deckAdapter;
    private DeckPersistence deckPersistence;
    private FlashcardPersistence flashcardPersistence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        deckPersistence = Services.getDeckPersistence();
        flashcardPersistence = Services.getFlashcardPersistence();

        initUI();
    }

    private void initUI() {
        rvDecks = findViewById(R.id.rvDecks);
        rvDecks.setLayoutManager(new LinearLayoutManager(this));

        FloatingActionButton fabAddDeck = findViewById(R.id.fabAddDeck);
        fabAddDeck.setOnClickListener(v -> startActivity(new Intent(this, EditDeckActivity.class)));

        loadDecks();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDecks();
    }

    private void loadDecks() {
        List<Deck> decks = deckPersistence.getAllDecks();
        
        // Update card counts
        for (Deck deck : decks) {
            deck.setCardCount(flashcardPersistence.getFlashcardCountByDeckId(deck.getId()));
        }

        if (deckAdapter == null) {
            deckAdapter = new DeckAdapter(decks, createDeckClickListener());
            rvDecks.setAdapter(deckAdapter);
        } else {
            deckAdapter.setDecks(decks);
            deckAdapter.notifyDataSetChanged();
        }
    }

    private DeckAdapter.DeckClickListener createDeckClickListener() {
        return new DeckAdapter.DeckClickListener() {
            @Override
            public void onEditClick(Deck deck) {
                Intent intent = new Intent(MainActivity.this, EditDeckActivity.class);
                intent.putExtra("DECK_ID", deck.getId());
                startActivity(intent);
            }

            @Override
            public void onDeleteClick(Deck deck) {
                deckPersistence.deleteDeck(deck.getId());
                loadDecks();
            }

            @Override
            public void onItemClick(Deck deck) {
                Intent intent = new Intent(MainActivity.this, DeckDetailActivity.class);
                intent.putExtra("DECK_ID", deck.getId());
                startActivity(intent);
            }
        };
    }
}
