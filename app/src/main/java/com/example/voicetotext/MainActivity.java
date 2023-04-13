package com.example.voicetotext;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import java.text.SimpleDateFormat;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;



public class MainActivity extends AppCompatActivity {
    ImageView iv_mic;
    TextView tv_Speech_to_text;
    private static final int REQUEST_CODE_SPEECH_INPUT = 1;

    String date;
    String[] langCode = new String[]{"ta-IN","te-IN","kn-IN","hi-IN","en-IN"};
    Spinner lanDrop;
    String language, langSpeech;
    Button print;
    TextView history, translate;
    FirebaseTranslatorOptions tamilOptions, teluguOptions, kannadaOptions, hindiOptions;
    FirebaseTranslator tamilTranslator, teluguTranslator, kannadaTranslator, hindiTranslator;
    FirebaseModelDownloadConditions conditions;

    int count;

    Date currentTime = Calendar.getInstance().getTime();

    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        date = String.valueOf(currentTime);
        date  = date.substring(0, Math.min(date.length(), 10));


        FirebaseFirestore db = FirebaseFirestore.getInstance();

        tamilOptions = new FirebaseTranslatorOptions.Builder()
                .setSourceLanguage(FirebaseTranslateLanguage.TA)
                .setTargetLanguage(FirebaseTranslateLanguage.EN)
                .build();

        teluguOptions = new FirebaseTranslatorOptions.Builder()
                .setSourceLanguage(FirebaseTranslateLanguage.TE)
                .setTargetLanguage(FirebaseTranslateLanguage.EN)
                .build();

        kannadaOptions = new FirebaseTranslatorOptions.Builder()
                .setSourceLanguage(FirebaseTranslateLanguage.KN)
                .setTargetLanguage(FirebaseTranslateLanguage.EN)
                .build();

        hindiOptions = new FirebaseTranslatorOptions.Builder()
                .setSourceLanguage(FirebaseTranslateLanguage.HI)
                .setTargetLanguage(FirebaseTranslateLanguage.EN)
                .build();


        tamilTranslator = FirebaseNaturalLanguage.getInstance().getTranslator(tamilOptions);
        teluguTranslator = FirebaseNaturalLanguage.getInstance().getTranslator(teluguOptions);
        kannadaTranslator = FirebaseNaturalLanguage.getInstance().getTranslator(kannadaOptions);
        hindiTranslator = FirebaseNaturalLanguage.getInstance().getTranslator(hindiOptions);


        conditions = new FirebaseModelDownloadConditions.Builder()
                .requireWifi()
                .build();

        iv_mic = findViewById(R.id.iv_mic);
        tv_Speech_to_text = findViewById(R.id.tv_speech_to_text);
        lanDrop = findViewById(R.id.spinner);
        print = findViewById(R.id.print);
        history = findViewById(R.id.history);
        translate = findViewById(R.id.translate);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.languages, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lanDrop.setAdapter(adapter);

        DocumentReference docRef = db.collection("count").document("count");

        WriteBatch batch = db.batch();

        iv_mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                language = lanDrop.getSelectedItem().toString();
                Toast.makeText(getApplicationContext(),language , Toast.LENGTH_LONG).show();

                switch(language) {
                    case "Telugu":
                        langSpeech = langCode[1];

                        break;
                    case "Kannada":
                        langSpeech = langCode[2];

                        break;
                    case "Hindi":
                        langSpeech = langCode[3];

                        break;
                    case "English":
                        langSpeech = langCode[4];

                        break;
                    default:
                        langSpeech = langCode[0];

                }

                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, langSpeech);
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text");

                try {
                    startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
                }
                catch (Exception e) {
                    Toast.makeText(MainActivity.this, " " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });

            translate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    language = lanDrop.getSelectedItem().toString();
                    String txt = tv_Speech_to_text.getText().toString();
                    Toast.makeText(MainActivity.this,"translating",Toast.LENGTH_SHORT).show();

                        if(language.equals("Telugu")){
                            teluguTranslator.downloadModelIfNeeded(conditions)
                                    .addOnSuccessListener(
                                            new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void v) {
                                                    Toast.makeText(MainActivity.this,"still translating",Toast.LENGTH_SHORT).show();
                                                    translateLang(txt, language);
                                                }
                                            })
                                    .addOnFailureListener(
                                            new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {

                                                }
                                            });
                        }


                        if(language.equals("Kannada"))    {
                            kannadaTranslator.downloadModelIfNeeded(conditions)
                                    .addOnSuccessListener(
                                            new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void v) {
                                                    Toast.makeText(MainActivity.this,"still translating",Toast.LENGTH_SHORT).show();
                                                    translateLang(txt, language);
                                                }
                                            })
                                    .addOnFailureListener(
                                            new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {

                                                }
                                            });
                        }
                        if(language.equals("Hindi")){

                            hindiTranslator.downloadModelIfNeeded(conditions)
                                            .addOnSuccessListener(
                                                    new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void v) {
                                                            Toast.makeText(MainActivity.this,"still translating",Toast.LENGTH_SHORT).show();
                                                            translateLang(txt, language);
                                                        }
                                                    })
                                            .addOnFailureListener(
                                                    new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {

                                                        }
                                                    });

                        }

                        if(language.equals("Tamil")){
                            tamilTranslator.downloadModelIfNeeded(conditions)
                                    .addOnSuccessListener(
                                            new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void v) {
                                                    Toast.makeText(MainActivity.this,"still translating",Toast.LENGTH_SHORT).show();
                                                    translateLang(txt, language);
                                                }
                                            })
                                    .addOnFailureListener(
                                            new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {

                                                }
                                            });
                        }
                    if(language.equals("English")){
                            Toast.makeText(MainActivity.this,"Already in English",Toast.LENGTH_SHORT).show();
                        }

                    Toast.makeText(MainActivity.this,"wait few seconds",Toast.LENGTH_SHORT).show();

                }
            });




        print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txt = tv_Speech_to_text.getText().toString();

//                String date = "19 March";
                Map<String, Object> data = new HashMap<>();
                data.put("text", txt);
                data.put("date", date);

                Toast toast = Toast.makeText(getApplicationContext(), "Successfully updated", Toast.LENGTH_SHORT);

                db.collection("count")
                        .document("count")
                        .get().
                        addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                Toast.makeText(getApplicationContext(), "inside", Toast.LENGTH_SHORT).show();
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
//                                    Toast.makeText(getApplicationContext(), String.valueOf(document.getString("count")), Toast.LENGTH_SHORT).show();
                                    count = Integer.parseInt(document.getString("count"));
                                    count += 1;
                                    db.collection("text").document(String.valueOf(count)).set(data);
                                    batch.update(docRef, "count", String.valueOf(count));
                                    batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(MainActivity.this, "Journal count updated", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                    toast.show();
                            } else {
                                Log.d(TAG, "Cached get failed: ", task.getException());
                            }
                    }
                });

            }
        });

        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                tv_Speech_to_text.setText(Objects.requireNonNull(result).get(0));
            }
        }
    }

    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
        Toast.makeText(getApplicationContext(),langCode[position] , Toast.LENGTH_LONG).show();
    }

    public void onNothingSelected(AdapterView<?> arg0) {

    }


    public void translateLang(String txt, String lang){

        switch(lang) {
            case "Telugu":
                teluguTranslator.translate(txt)
                        .addOnSuccessListener(
                                new OnSuccessListener<String>() {
                                    @Override
                                    public void onSuccess(@NonNull String translatedText) {
                                        tv_Speech_to_text.setText(translatedText);
                                        Toast.makeText(MainActivity.this,translatedText,Toast.LENGTH_SHORT).show();
                                    }
                                })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(MainActivity.this,"failed to convert",Toast.LENGTH_SHORT).show();
                                    }
                                });
                break;
            case "Kannada":
                kannadaTranslator.translate(txt)
                        .addOnSuccessListener(
                                new OnSuccessListener<String>() {
                                    @Override
                                    public void onSuccess(@NonNull String translatedText) {
                                        tv_Speech_to_text.setText(translatedText);
                                        Toast.makeText(MainActivity.this,translatedText,Toast.LENGTH_SHORT).show();
                                    }
                                })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(MainActivity.this,"failed to convert",Toast.LENGTH_SHORT).show();
                                    }
                                });
                break;
            case "Hindi":
                hindiTranslator.translate(txt)
                        .addOnSuccessListener(
                                new OnSuccessListener<String>() {
                                    @Override
                                    public void onSuccess(@NonNull String translatedText) {
                                        tv_Speech_to_text.setText(translatedText);
                                        Toast.makeText(MainActivity.this,translatedText,Toast.LENGTH_SHORT).show();
                                    }
                                })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(MainActivity.this,"failed to convert",Toast.LENGTH_SHORT).show();
                                    }
                                });
                break;
            case "English":
                break;
            default:
                tamilTranslator.translate(txt)
                        .addOnSuccessListener(
                                new OnSuccessListener<String>() {
                                    @Override
                                    public void onSuccess(@NonNull String translatedText) {
                                        tv_Speech_to_text.setText(translatedText);
                                        Toast.makeText(MainActivity.this,translatedText,Toast.LENGTH_SHORT).show();
                                    }
                                })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(MainActivity.this,"failed to convert",Toast.LENGTH_SHORT).show();
                                    }
                                });

        }

    }
}


