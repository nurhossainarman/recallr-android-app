package comp3350.flashcard.presentation;

import android.content.Context;
import android.widget.Toast;

/**
 * Utility class for displaying common user messages (Toasts).
 * Centralizes toast logic to adhere to the DRY principle.
 */
public class Messages {

    /**
     * Displays a short toast message.
     *
     * @param context the activity or application context
     * @param message the text message to display
     */
    public static void show(Context context, String message) {
        if (context != null && message != null && !message.trim().isEmpty()) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }
}
