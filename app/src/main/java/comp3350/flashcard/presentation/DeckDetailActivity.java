package comp3350.flashcard.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;

import comp3350.flashcard.R;
import comp3350.flashcard.objects.Deck;
import comp3350.flashcard.objects.Flashcard;
import comp3350.flashcard.persistence.DeckPersistence;
import comp3350.flashcard.persistence.FlashcardPersistence;
import comp3350.flashcard.persistence.stubs.DeckPersistenceStub;
import comp3350.flashcard.persistence.stubs.FlashcardPersistenceStub;

public class DeckDetailActivity extends AppCompatActivity {

    private RecyclerView rvCards;
    private CardAdapter cardAdapter;
    private DeckPersistence deckPersistence;
    private FlashcardPersistence flashcardPersistence;
    private int deckId = -1;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deck_detail);

        // Ideally these would come from a Services class to share the same data instances
        deckPersistence = new DeckPersistenceStub();
        flashcardPersistence = new FlashcardPersistenceStub();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        rvCards = findViewById(R.id.rvCards);
        rvCards.setLayoutManager(new LinearLayoutManager(this));

        if (getIntent().hasExtra("DECK_ID")) {
            deckId = getIntent().getIntExtra("DECK_ID", -1);
            Deck deck = deckPersistence.getDeckById(deckId);
            if (deck != null) {
                toolbar.setTitle(deck.getName());
            }
        }

        FloatingActionButton fabAddCard = findViewById(R.id.fabAddCard);
        fabAddCard.setOnClickListener(v -> {
            Intent intent = new Intent(DeckDetailActivity.this, EditCardActivity.class);
            intent.putExtra("DECK_ID", deckId);
            startActivity(intent);
        });

        ExtendedFloatingActionButton btnStudy = findViewById(R.id.btnStudy);
        btnStudy.setOnClickListener(v -> {
            Toast.makeText(this, "Study session starting...", Toast.LENGTH_SHORT).show();
            // Future implementation: Intent to StudyActivity
        });

        loadCards();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCards();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void loadCards() {
        List<Flashcard> cards = flashcardPersistence.getFlashcardsByDeckId(deckId);
        if (cardAdapter == null) {
            cardAdapter = new CardAdapter(cards, new CardAdapter.CardClickListener() {
                @Override
                public void onEditClick(Flashcard card) {
                    Intent intent = new Intent(DeckDetailActivity.this, EditCardActivity.class);
                    intent.putExtra("CARD_ID", card.getId());
                    startActivity(intent);
                }

                @Override
                public void onDeleteClick(Flashcard card) {
                    flashcardPersistence.deleteFlashcard(card.getId());
                    loadCards();
                }
            });
            rvCards.setAdapter(cardAdapter);
        } else {
            cardAdapter.setCards(cards);
            cardAdapter.notifyDataSetChanged();
        }
    }
}
