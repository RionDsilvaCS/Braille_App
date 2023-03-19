package com.example.voicetotext;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.view.translation.Translator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.collection.ImmutableSortedMap;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;



public class MainActivity extends AppCompatActivity {
    private ImageView iv_mic;
    private TextView tv_Speech_to_text;
    private static final int REQUEST_CODE_SPEECH_INPUT = 1;
    String[] langCode = new String[]{"ta-IN","te-IN","kn-IN","hi-IN","en-IN"};
    Spinner lanDrop;
    String language, langSpeech;
    Button print;
    TextView history, translate;
    FirebaseTranslatorOptions tamilOptions, teluguOptions, kannadaOptions, hindiOptions;
    FirebaseTranslator allTranslator, tamilTranslator, teluguTranslator, kannadaTranslator, hindiTranslator;
    FirebaseModelDownloadConditions conditions;
    int flag=0;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Handler handler = new Handler();

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
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
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
                            }, 3000);
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

                String date = "19 March";
                Map<String, Object> data = new HashMap<>();
                data.put("text", txt);
                data.put("date", date);
                Toast toast = Toast.makeText(getApplicationContext(), "Successful", Toast.LENGTH_SHORT);
                db.collection("text")
                        .add(data)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                                toast.show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error adding document", e);
                            }
                        });

            }
        });

        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
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
        // TODO Auto-generated method stub
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


