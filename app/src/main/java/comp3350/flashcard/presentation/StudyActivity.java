package comp3350.flashcard.presentation;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import comp3350.flashcard.R;
import comp3350.flashcard.application.Services;
import comp3350.flashcard.logic.IStudySession;

/**
 * StudyActivity - UI for studying flashcards in a session.
 */
public class StudyActivity extends AppCompatActivity {

    private TextView tvProgress;
    private TextView tvContent;
    private IStudySession studySession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study);

        studySession = Services.getStudySession();
        initUI();
        startSession();
    }

    private void initUI() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        tvProgress = findViewById(R.id.tvProgress);
        tvContent = findViewById(R.id.tvContent);

        findViewById(R.id.btnFlip).setOnClickListener(v -> {
            studySession.flip();
            updateUI();
        });
        
        findViewById(R.id.btnNext).setOnClickListener(v -> {
            studySession.nextCard();
            if (studySession.isFinished()) {
                finish();
            } else {
                updateUI();
            }
        });
        
        findViewById(R.id.btnPrevious).setOnClickListener(v -> {
            studySession.previousCard();
            updateUI();
        });
    }

    private void startSession() {
        int deckId = getIntent().getIntExtra("DECK_ID", -1);
        boolean shuffle = getIntent().getBooleanExtra("SHUFFLE", false);

        studySession.startSession(deckId, shuffle);
        
        if (studySession.hasCards()) {
            updateUI();
        } else {
            finish();
        }
    }

    private void updateUI() {
        tvContent.setText(studySession.getCurrentText());
        tvProgress.setText(studySession.getProgressText());
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
