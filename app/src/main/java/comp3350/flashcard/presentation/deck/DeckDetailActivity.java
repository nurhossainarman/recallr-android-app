package comp3350.flashcard.presentation.deck;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.chip.ChipGroup;
import java.util.List;
import comp3350.flashcard.R;
import comp3350.flashcard.application.Services;
import comp3350.flashcard.constants.ValidationConstants;
import comp3350.flashcard.logic.IDeckManager;
import comp3350.flashcard.logic.FilterMode;
import comp3350.flashcard.logic.IFlashcardManager;
import comp3350.flashcard.logic.IStudySession;
import comp3350.flashcard.logic.exceptions.StudySessionException;
import comp3350.flashcard.objects.Deck;
import comp3350.flashcard.objects.Flashcard;
import comp3350.flashcard.presentation.Adapter;
import comp3350.flashcard.presentation.Messages;
import comp3350.flashcard.presentation.card.EditCardActivity;
import comp3350.flashcard.presentation.study.StudyActivity;

/**
 * DeckDetailActivity - Displays all cards inside a specific deck.
 */
public class DeckDetailActivity extends AppCompatActivity {

    private RecyclerView rvCards;
    private Adapter<Flashcard> adapter;
    private IDeckManager deckManager;
    private IFlashcardManager flashcardManager;
    private IStudySession studySession;
    private int deckId = ValidationConstants.INVALID_ID;
    private Toolbar toolbar;
    private CheckBox cbShuffle;
    private ChipGroup rgFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deck_detail);

        deckManager = Services.getDeckManager();
        flashcardManager = Services.getFlashcardManager();
        studySession = Services.getStudySession();

        initUI();
    }

    private void initUI() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        rvCards = findViewById(R.id.rvCards);
        rvCards.setLayoutManager(new LinearLayoutManager(this));

        cbShuffle = findViewById(R.id.cbShuffle);
        rgFilter = findViewById(R.id.rgFilter);

        deckId = getIntent().getIntExtra("DECK_ID", ValidationConstants.INVALID_ID);
        setupDeckInfo();

        findViewById(R.id.fabAddCard).setOnClickListener(v -> navigateToEditCard(ValidationConstants.INVALID_ID));
        findViewById(R.id.btnStudy).setOnClickListener(v -> startStudySession());

        // Initialize adapter once
        adapter = new Adapter<>(null, R.layout.item_card, this::bindCardItem);
        rvCards.setAdapter(adapter);

        loadCards();
    }

    private void setupDeckInfo() {
        try {
            Deck deck = deckManager.getDeck(deckId);
            if (deck != null) {
                toolbar.setTitle(deck.getName());
            }
        } catch (Exception e) {
            Messages.show(this, e.getMessage());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCards();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void loadCards() {
        try {
            List<Flashcard> cards = flashcardManager.getFlashcardsByDeck(deckId);
            adapter.updateItems(cards);
        } catch (Exception e) {
            Messages.show(this, e.getMessage());
        }
    }

    private void bindCardItem(View view, Flashcard card) {
        ((TextView) view.findViewById(R.id.tvCardFront)).setText(card.getFront());
        ((TextView) view.findViewById(R.id.tvCardBack)).setText(card.getBack());

        view.findViewById(R.id.btnEditCard).setOnClickListener(v -> navigateToEditCard(card.getId()));
        view.findViewById(R.id.btnDeleteCard).setOnClickListener(v -> {
            try {
                flashcardManager.deleteFlashcard(card.getId());
                loadCards();
            } catch (Exception e) {
                Messages.show(this, e.getMessage());
            }
        });
    }

    private void navigateToEditCard(int cardId) {
        Intent intent = new Intent(this, EditCardActivity.class).putExtra("DECK_ID", deckId);
        if (cardId != ValidationConstants.INVALID_ID) intent.putExtra("CARD_ID", cardId);
        startActivity(intent);
    }

    /**
     * Starts the study session. Logic layer handles validation of card existence.
     */
    private void startStudySession() {
        FilterMode filterMode = FilterMode.ALL;
        int checkedId = rgFilter.getCheckedChipId();

        if (checkedId == R.id.rbKnown) {
            filterMode = FilterMode.KNOWN;
        } else if (checkedId == R.id.rbUnknown) {
            filterMode = FilterMode.UNKNOWN;
        }

        try {
            // Validation occurs in logic layer
            studySession.validateSession(deckId, filterMode);
            
            Intent intent = new Intent(this, StudyActivity.class);
            intent.putExtra("DECK_ID", deckId);
            intent.putExtra("SHUFFLE", cbShuffle.isChecked());
            intent.putExtra("FILTER_MODE", filterMode.name());
            startActivity(intent);
        } catch (StudySessionException e) {
            Messages.show(this, e.getMessage());
        }
    }
}
