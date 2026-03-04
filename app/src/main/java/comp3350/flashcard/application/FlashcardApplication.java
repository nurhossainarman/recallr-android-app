package comp3350.flashcard.application;

import android.app.Application;

public class FlashcardApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Services.setContext(this);
    }
}
