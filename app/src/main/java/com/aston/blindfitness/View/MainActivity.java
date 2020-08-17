package com.aston.blindfitness.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.text.Html;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.aston.blindfitness.R;
import com.aston.blindfitness.SharedPrefs;

import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    SharedPrefs userPreferences;

    private static final int AUDIO_REQUEST_CODE = 100;
    private static final int LOCATION_REQUEST_CODE = 200;

    Button ocr, myFitness, btnSettings, btnHelp, btnNews, btnMap;
    private TextToSpeech textToSpeech;
    private SpeechRecognizer speechRecognizer;

    String[] audioPermission;
    String[] locationPermissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        userPreferences = new SharedPrefs(MainActivity.this);
        if (userPreferences.loadDarkTheme()) {
            userPreferences.setLightTheme(false);
            userPreferences.setYellowTheme(false);
            setTheme(R.style.DarkTheme);
        } else if (userPreferences.loadYellowTheme()) {
            userPreferences.setLightTheme(false);
            userPreferences.setDarkTheme(false);
            setTheme(R.style.YellowTheme);
        } else if (userPreferences.loadLightTheme()) {
            userPreferences.setDarkTheme(false);
            userPreferences.setYellowTheme(false);
            setTheme(R.style.AppTheme);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ocr = findViewById(R.id.btn_ocr);
        myFitness = findViewById(R.id.btn_my_fitness);
        btnSettings = findViewById(R.id.settings);
        btnHelp = findViewById(R.id.help);
        btnNews = findViewById(R.id.news);
        btnMap = findViewById(R.id.map);


        audioPermission = new String[]{Manifest.permission.RECORD_AUDIO};
        locationPermissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};

        ocr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, OCRActivity.class);
                startActivity(intent);
            }
        });

        myFitness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(MainActivity.this, FitnessActivity.class);
                //startActivity(intent);
                SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences("myFitness", MODE_PRIVATE);
                boolean firstTime = sharedPreferences.getBoolean("firstTime", true);

                if (firstTime) {
                    Intent swag = new Intent(MainActivity.this, MyFitness.class);
                    startActivity(swag);
                } else {
                    Intent sweg = new Intent(MainActivity.this, FitActivity.class);
                    startActivity(sweg);
                }
            }
        });

        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivityForResult(intent, 10001);
            }
        });

        btnHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, HelpActivity.class);
                startActivity(intent);
            }
        });

        btnNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NewsActivity.class);
                startActivity(intent);
            }
        });

        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MapActivity.class);
                startActivity(intent);
            }
        });

        initialiseTextToSpeech();
        initialiseSpeechRecognizer();

        if (userPreferences.loadDarkTheme()) {
            getSupportActionBar().setTitle(Html.fromHtml("<font color='#000000'> Blind Fitness</font>"));
        } else {
            getSupportActionBar().setTitle(Html.fromHtml("BlindFitness"));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    /**
     * @return result of users location permission check
     */
    private boolean checkLocationPermissions() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    /**
     * Request users location using permissions
     */
    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, locationPermissions, LOCATION_REQUEST_CODE);
    }

    /**
     * Use SpeechRecogniser to listen to users queries
     */
    private void performSpeechRecognition() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        speechRecognizer.startListening(intent);
    }

    /**
     * Request user audio using permissions
     */
    private void requestAudioPermissions() {
        ActivityCompat.requestPermissions(this, audioPermission, AUDIO_REQUEST_CODE);
    }

    /**
     * @return result of users audio permission check
     */
    private boolean checkAudioPermission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    /**
     * Set-up speech recognizer
     */
    private void initialiseSpeechRecognizer() {
        if (SpeechRecognizer.isRecognitionAvailable(this)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
            RecognitionListener recognitionListener = new RecognitionListener() {
                @Override
                public void onReadyForSpeech(Bundle bundle) {

                }

                @Override
                public void onBeginningOfSpeech() {

                }

                @Override
                public void onRmsChanged(float v) {

                }

                @Override
                public void onBufferReceived(byte[] bytes) {

                }

                @Override
                public void onEndOfSpeech() {

                }

                @Override
                public void onError(int i) {

                    String message;
                    switch (i) {
                        case SpeechRecognizer.ERROR_AUDIO:
                            message = "Audio recording error";
                            break;
                        case SpeechRecognizer.ERROR_CLIENT:
                            message = "Client side error";
                            break;
                        case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                            message = "Insufficient permissions";
                            break;
                        case SpeechRecognizer.ERROR_NETWORK:
                            message = "Network error";
                            break;
                        case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                            message = "Network timeout";
                            break;
                        case SpeechRecognizer.ERROR_NO_MATCH:
                            message = "No match";
                            break;
                        case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                            message = "RecognitionService busy";
                            break;
                        case SpeechRecognizer.ERROR_SERVER:
                            message = "error from server";
                            break;
                        case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                            message = "No speech input";
                            break;
                        default:
                            message = "Didn't understand, please try again.";
                            break;
                    }
                    speak(message);
                }

                @Override
                public void onResults(Bundle bundle) {
                    List<String> results = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                    processResult(results.get(0));

                }

                @Override
                public void onPartialResults(Bundle bundle) {

                }

                @Override
                public void onEvent(int i, Bundle bundle) {

                }
            };
            speechRecognizer.setRecognitionListener(recognitionListener);
        }
    }

    /**
     * Process users queries and return an appropriate answer or feature
     * @param s
     */
    private void processResult(String s) {
        s = s.toLowerCase();

        if (s.indexOf(getString(R.string.hello)) != -1) {
            speak(getString(R.string.hello));
            if (s.indexOf(getString(R.string.how_are_you)) != -1) {
                speak(getString(R.string.good_thank_you));
            }
        } else if (s.indexOf(getString(R.string.what)) != -1) {
            if (s.indexOf(getString(R.string.your_name)) != -1) {
                speak(getString(R.string.my_name_is));
            }
            if (s.indexOf(getString(R.string.time)) != -1) {
                Date date = new Date();
                String time = DateUtils.formatDateTime(this, date.getTime(), DateUtils.FORMAT_SHOW_TIME);
                speak(getString(R.string.the_time_is) + time);
            }
            if (s.indexOf(getString(R.string.date)) != -1) {
                Date date = new Date();
                String time = DateUtils.formatDateTime(this, date.getTime(), DateUtils.FORMAT_SHOW_DATE);
                speak(getString(R.string.todays_date_is) + time);
            }
        } else if (s.indexOf("open") != -1) {
            // Sub options of "open"
            if (s.indexOf("browser") != -1) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://google.com"));
                startActivity(intent);
            }
            if (s.indexOf("ocr") != -1 || s.indexOf("optical character recognition") != -1) {
                Intent intent = new Intent(MainActivity.this, OCRActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
            if (s.indexOf("object recognition") != -1 || s.indexOf("object detection") != -1 || s.indexOf("recognition") != -1) {
                Intent intent = new Intent(MainActivity.this, DetectorActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }
    }

    /**
     * Initialise text-to-speech for user
     */
    private void initialiseTextToSpeech() {
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (textToSpeech.getEngines().size() == 0) {
                    Toast.makeText(MainActivity.this, "No Text-To-Speech found on your device", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    textToSpeech.setLanguage(Locale.UK);
                    //speak("Welcome back.");
                }
            }
        });
    }

    /**
     * Provide message to user in form of a voice
     * @param s
     */
    private void speak(String s) {
        if (Build.VERSION.SDK_INT >= 21) {
            textToSpeech.speak(s, TextToSpeech.QUEUE_FLUSH, null, null);
        } else {
            textToSpeech.speak(s, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        textToSpeech.shutdown();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10001) {
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }
        if (resultCode == RESULT_OK) {
            if (requestCode == LOCATION_REQUEST_CODE) {
                Intent intent = new Intent(MainActivity.this, MapActivity.class);
                startActivity(intent);
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case AUDIO_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean audioAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (audioAccepted) {
                        performSpeechRecognition();
                    } else {
                        Toast.makeText(this, "BlindFitness needs Audio permission to perform this action", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case LOCATION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (locationAccepted) {
                        // Location permission has been granted by user
                        Intent intent = new Intent(MainActivity.this, MapActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(this, "BlindFitness needs Location permission to get your location", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_mic:
                if (!checkAudioPermission()) {
                    requestAudioPermissions();
                } else {
                    performSpeechRecognition();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
