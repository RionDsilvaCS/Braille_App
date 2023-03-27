package com.example.voicetotext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SecondActivity extends AppCompatActivity {

    TextView history, context;
    String content, date;
    ArrayList<HistoryCard> HistoryCardArrayList;
    ArrayList<String> docId;
    SwipeRefreshLayout historySwipe;
    int flag=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        history = findViewById(R.id.history);
        docId = new ArrayList<String>();
        historySwipe = findViewById(R.id.swipeRefresh);

        RecyclerView journalHistory = findViewById(R.id.journalHistory);

        HistoryCardArrayList = new ArrayList<HistoryCard>();
        HistoryCardArrayList.add(new HistoryCard("Hello good morning", "15 March"));


        historySwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                db.collection("text").get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {

                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                                if (!queryDocumentSnapshots.isEmpty()) {

                                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                                    for (DocumentSnapshot d : list) {
                                        if(!docId.contains(d.getId())){
                                            content = d.getString("text");
                                            date = d.getString("date");
                                            docId.add(d.getId());
                                            HistoryCardArrayList.add(new HistoryCard(content, date));
                                        }
                                    }

                                    HistoryAdapter historyAdapter = new HistoryAdapter (SecondActivity.this, HistoryCardArrayList);
                                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(SecondActivity.this, LinearLayoutManager.VERTICAL, false);
                                    journalHistory.setLayoutManager(linearLayoutManager);
                                    journalHistory.setAdapter(historyAdapter);

                                } else {

                                    Toast.makeText(SecondActivity.this, "No data found in Database", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {

                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(SecondActivity.this, "Fail to get the data.", Toast.LENGTH_SHORT).show();
                            }
                        });
                historySwipe.setRefreshing(false);
            }
        });


        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(SecondActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

//    private void loadData() {
//
//        SharedPreferences sharedPreferences = getSharedPreferences("arrayList", MODE_PRIVATE);
//
//        Gson gson = new Gson();
//
//        String json = sharedPreferences.getString("content", null);
//
//        Type type = new TypeToken<ArrayList<HistoryCard>>() {}.getType();
//
//        HistoryCardArrayList = gson.fromJson(json, type);
//
//        if (HistoryCardArrayList == null) {
//            HistoryCardArrayList = new ArrayList<>();
//        }
//
//        Toast.makeText(this, "Loading Array List to Shared preferences. ", Toast.LENGTH_SHORT).show();
//    }
//
//    private void saveData() {
//
//        SharedPreferences sharedPreferences = getSharedPreferences("arrayList", MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//
//        Gson gson = new Gson();
//
//        String json = gson.toJson(HistoryCardArrayList);
//
//        editor.putString("content", json);
//
//        editor.apply();
//
//        Toast.makeText(this, "Saved Array List to Shared preferences. ", Toast.LENGTH_SHORT).show();
//    }
//
//    private void deleteData(){
//        SharedPreferences pref = getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = pref.edit();
//        editor.clear().commit();
//        Toast.makeText(this, "Deleted Array List", Toast.LENGTH_SHORT).show();
//    }


}