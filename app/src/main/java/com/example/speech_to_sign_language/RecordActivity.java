package com.example.speech_to_sign_language;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.microsoft.cognitiveservices.speech.CancellationDetails;
import com.microsoft.cognitiveservices.speech.ResultReason;
import com.microsoft.cognitiveservices.speech.SpeechConfig;
import com.microsoft.cognitiveservices.speech.SpeechRecognitionResult;
import com.microsoft.cognitiveservices.speech.SpeechRecognizer;
import com.microsoft.cognitiveservices.speech.audio.AudioConfig;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.RECORD_AUDIO;
import static com.example.speech_to_sign_language.APIKeys.serviceRegion;
import static com.example.speech_to_sign_language.APIKeys.speechSubscriptionKey;

public class RecordActivity extends AppCompatActivity {

    private TextView textView;
    private Button button;
    private SpeechRecognizer reco;
    private boolean isActive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textView);
        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSpeechButtonClicked(v);
            }
        });
        // Note: we need to request the permissions
        int requestCode = 5; // unique code for the permission request
        ActivityCompat.requestPermissions(RecordActivity.this, new String[]{RECORD_AUDIO, INTERNET}, requestCode);
    }

    public void onSpeechButtonClicked(View v) {
        isActive = !isActive;
        if (isActive) {
            button.setText("Stop Recording");
            TextView txt = (TextView) this.findViewById(R.id.textView); // 'hello' is the ID of your text view
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
            txt.getText();
        } else {
            if (reco != null) {
                reco.stopContinuousRecognitionAsync();
                button.setText("Record");
            }
        }
    }


    private void displayException(Exception ex) {
        textView.setText(ex.getMessage() + System.lineSeparator() + TextUtils.join(System.lineSeparator(), ex.getStackTrace()));
    }

    private void clearTextBox() {
        AppendTextLine("", true);
    }

    private void setRecognizedText(final String s) {
        AppendTextLine(s, true);
    }

    private void AppendTextLine(final String s, final Boolean erase) {
        RecordActivity.this.runOnUiThread(() -> {
            if (erase) {
                textView.setText(s);
            } else {
                String txt = textView.getText().toString();
                textView.setText(txt + System.lineSeparator() + s);
            }
        });
    }

    private void disableButtons() {
        RecordActivity.this.runOnUiThread(() -> {
            button.setEnabled(false);
        });
    }

    private void enableButtons() {
        RecordActivity.this.runOnUiThread(() -> {
            button.setEnabled(true);
        });
    }

    private <T> void setOnTaskCompletedListener(Future<T> task, OnTaskCompletedListener<T> listener) {
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
