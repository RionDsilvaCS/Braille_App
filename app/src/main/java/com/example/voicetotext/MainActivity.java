package com.example.voicetotext;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
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
    String language, langSpeech, finalTxt=null;
    int langCodeTrans=50;
    Button print;
    TextView history, translate;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // DocumentReference dbTextRef = db.collection("text").document();

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
                        langCodeTrans=51;
                        break;
                    case "Kannada":
                        langSpeech = langCode[2];
                        langCodeTrans=31;
                        break;
                    case "Hindi":
                        langSpeech = langCode[3];
                        langCodeTrans=22;
                        break;
                    case "English":
                        langSpeech = langCode[4];
                        langCodeTrans=11;
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
                String txt = tv_Speech_to_text.getText().toString();

                if(langCodeTrans!=11){

                FirebaseTranslatorOptions options = new FirebaseTranslatorOptions.Builder()
                        .setSourceLanguage(FirebaseTranslateLanguage.HI)
                        .setTargetLanguage(FirebaseTranslateLanguage.EN)
                        .build();
                final FirebaseTranslator everyToEnglishTranslator =
                        FirebaseNaturalLanguage.getInstance().getTranslator(options);


                    everyToEnglishTranslator.translate(txt).addOnSuccessListener(new OnSuccessListener<String>() {
                        @Override
                        public void onSuccess(String s) {
                            finalTxt = s;
                            Toast.makeText(MainActivity.this, "translate success" + s, Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, "Fail to translate", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                tv_Speech_to_text.setText(finalTxt);
            }
        });

        print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txt = tv_Speech_to_text.getText().toString();


                FirebaseTranslatorOptions options = new FirebaseTranslatorOptions.Builder()
                        .setSourceLanguage(Integer.parseInt(FirebaseTranslateLanguage.languageCodeForLanguage(langCodeTrans)))
                        .setTargetLanguage(FirebaseTranslateLanguage.EN)
                        .build();
                final FirebaseTranslator everyToEnglishTranslator =
                        FirebaseNaturalLanguage.getInstance().getTranslator(options);

                if(langCodeTrans!=11){
                    everyToEnglishTranslator.translate(txt).addOnSuccessListener(new OnSuccessListener<String>() {
                        @Override
                        public void onSuccess(String s) {
                            finalTxt =s;
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, "Fail to translate", Toast.LENGTH_SHORT).show();
                        }
                    });
                }


                String date = "19 March";
                Map<String, Object> data = new HashMap<>();
                data.put("text", finalTxt);
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
}


