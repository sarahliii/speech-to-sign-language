package com.example.speech_to_sign_language;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.example.speech_to_sign_language.services.ProcessConversationService;
import com.microsoft.cognitiveservices.speech.CancellationDetails;
import com.microsoft.cognitiveservices.speech.ResultReason;
import com.microsoft.cognitiveservices.speech.SpeechConfig;
import com.microsoft.cognitiveservices.speech.SpeechRecognitionResult;
import com.microsoft.cognitiveservices.speech.SpeechRecognizer;
import com.microsoft.cognitiveservices.speech.audio.AudioConfig;

import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.RECORD_AUDIO;
import static com.example.speech_to_sign_language.APIKeys.serviceRegion;
import static com.example.speech_to_sign_language.APIKeys.speechSubscriptionKey;

public class MainActivity extends AppCompatActivity {


    private EditText editText;
    private ImageButton button;
    private SpeechRecognizer reco;
    private boolean isActive = false;
    private String inputText;
    public HashMap<String, Integer> map = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText inputText = findViewById(R.id.inputText);
        final GridView signImagesGrid = findViewById(R.id.signImagesGrid);

//        String[] plants = new String[]{
//                "Catalina ironwood",
//                "Cabinet cherry",
//                "Pale corydalis",
//                "Pink corydalis",
//                "Land cress",
//                "Coast polypody",
//                "Water fern"
//        };
//
//        // Populate a List from Array elements
//        final List<String> plantsList = new ArrayList<String>(Arrays.asList(plants));
//
//        // Create a new ArrayAdapter
//        final ArrayAdapter<String> gridViewArrayAdapter = new ArrayAdapter<String>
//                (this, android.R.layout.simple_list_item_1, plantsList);
//
//        // Data bind GridView with ArrayAdapter (String Array elements)
//        signImagesGrid.setAdapter(gridViewArrayAdapter);
        editText = findViewById(R.id.inputText);
        button = findViewById(R.id.imageButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickRecordSpeechButton(v);
            }
        });
        // Note: we need to request the permissions
        int requestCode = 5; // unique code for the permission request
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{RECORD_AUDIO, INTERNET}, requestCode);
    }



    public void onClickRecordButton(View view) {
        hideKeyboard(this);
        final TextView inputTextView = findViewById(R.id.inputText);
        final String inputText = inputTextView.getText().toString();
//
//        final GridView signImagesGrid = findViewById(R.id.signImagesGrid);
//
//        String[] inputString = inputText.replaceAll("[\\t\\n\\r]+", " ").split(" ");
//
//        // Populate a List from Array elements
//        final List<String> plantsList = new ArrayList<String>(Arrays.asList(inputString));
//
//        // Create a new ArrayAdapter
//        final ArrayAdapter<String> gridViewArrayAdapter = new ArrayAdapter<String>
//                (this, android.R.layout.simple_list_item_1, plantsList);
//
//        signImagesGrid.setAdapter(gridViewArrayAdapter);
//
        ProcessConversationService processConversationService = new ProcessConversationService(getResources());
        List<String> listPath = processConversationService.processConversation(inputText);
        String pp = "";
        for (String p : listPath) {
            pp += p;
        }

        LinearLayout layout = findViewById(R.id.linearLayout);
        layout.removeAllViews();
        int count = 0;
        LinearLayout horizontalLayout = new LinearLayout(this);
        horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);
        for(String path : listPath)
        {
            String name = path.substring(0, path.length() - 4);
            if (path.equals("images/_.png")) name = "";
            LinearLayout vertLayout = new LinearLayout(this);
            vertLayout.setOrientation(LinearLayout.VERTICAL);
            TextView textView = new TextView(this);
            textView.setText(name);
            textView.setGravity(Gravity.CENTER);
            ImageView image = new ImageView(this);
            image.setLayoutParams(new android.view.ViewGroup.LayoutParams(200,200));
            image.setMaxHeight(200);
            image.setMaxWidth(200);
            String imagePath = "images/" + path;
            Bitmap bitmap = getBitmapFromAssets(imagePath);
            //image.setImageResource(R.drawable.ic_launcher_background);
            image.setImageBitmap(bitmap);

            // Adds the view to the layout
            vertLayout.addView(image);
            vertLayout.addView(textView);
            horizontalLayout.addView(vertLayout);
            if (count > 3) {
                layout.addView(horizontalLayout);
                horizontalLayout = new LinearLayout(this);
                horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);
                count = 0;
            } else count++;
        }
        if (count != 0) {
            layout.addView(horizontalLayout);
        }

    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public Bitmap getBitmapFromAssets(String fileName) {
        Bitmap bitmap;
        try {
            AssetManager assetManager = getAssets();
            InputStream istr = assetManager.open(fileName);
            bitmap = BitmapFactory.decodeStream(istr);
        } catch (java.io.IOException e) {
            bitmap = null;
        }
        return bitmap;
    }

    public void onClickRecordSpeechButton(View v) {
        isActive = !isActive;
        if (isActive) {
            button.setImageResource(R.drawable.stop_recording);
            EditText txt = findViewById(R.id.inputText); // 'hello' is the ID of your text view
            try {
                SpeechConfig config = SpeechConfig.fromSubscription(speechSubscriptionKey, serviceRegion);
                assert(config != null);
                final AudioConfig audioInput = AudioConfig.fromDefaultMicrophoneInput();
                reco = new SpeechRecognizer(config, audioInput);
                assert(reco != null);
                Future<SpeechRecognitionResult> task = reco.recognizeOnceAsync();
                assert(task != null);
                setOnTaskCompletedListener(task, result -> {
                    String s = result.getText();
                    reco.close();
                    if (result.getReason() != ResultReason.RecognizedSpeech) {
                        String errorDetails = (result.getReason() == ResultReason.Canceled) ? CancellationDetails.fromResult(result).getErrorDetails() : "";
                        System.out.println(errorDetails);
                        s = "";
                    } else {
                        setRecognizedText(s);
                    }
                    enableButtons();
                });

            } catch (Exception ex) {
                Log.e("SpeechSDKDemo", "unexpected " + ex.getMessage());
                assert(false);
            }
            editText.setText(txt.getText());
        } else {
            if (reco != null) {
                reco.stopContinuousRecognitionAsync();
                button.setImageResource(R.drawable.record);
            }
        }
    }

    private void setRecognizedText(final String s) {
        AppendTextLine(s, true);
    }

    private void AppendTextLine(final String s, final Boolean erase) {
        MainActivity.this.runOnUiThread(() -> {
            if (erase) {
                editText.setActivated(false);
                editText.setText(s);
            } else {
                String txt = editText.getText().toString();
                editText.setText(txt + System.lineSeparator() + s);
            }
        });
    }

    private void enableButtons() {
        MainActivity.this.runOnUiThread(() -> {
            button.setEnabled(true);
        });
    }

    private <T> void setOnTaskCompletedListener(Future<T> task, MainActivity.OnTaskCompletedListener<T> listener) {
        s_executorService.submit(() -> {
            T result = task.get();
            listener.onCompleted(result);
            return null;
        });
    }

    private interface OnTaskCompletedListener<T> {
        void onCompleted(T taskResult);
    }

    private static ExecutorService s_executorService;
    static {
        s_executorService = Executors.newCachedThreadPool();
    }
}
