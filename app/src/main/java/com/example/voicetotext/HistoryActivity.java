package com.example.voicetotext;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    ImageView history;
    TextView context;
    String content, date;
    ArrayList<HistoryCard> HistoryCardArrayList;
    ArrayList<String> docId;
    SwipeRefreshLayout historySwipe;
    int flag=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        history = findViewById(R.id.history);
        docId = new ArrayList<String>();
        historySwipe = findViewById(R.id.swipeRefresh);

        RecyclerView journalHistory = findViewById(R.id.journalHistory);

        HistoryCardArrayList = new ArrayList<HistoryCard>();
//        HistoryCardArrayList.add(new HistoryCard("Hello good morning", "15 March"));


//        Toast.makeText(getApplicationContext(), docRef.get("count"), Toast.LENGTH_LONG).show();



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

                                    HistoryAdapter historyAdapter = new HistoryAdapter (HistoryActivity.this, HistoryCardArrayList);
                                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(HistoryActivity.this, LinearLayoutManager.VERTICAL, false);
                                    journalHistory.setLayoutManager(linearLayoutManager);
                                    journalHistory.setAdapter(historyAdapter);

                                } else {

                                    Toast.makeText(HistoryActivity.this, "No data found in Database", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {

                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(HistoryActivity.this, "Fail to get the data.", Toast.LENGTH_SHORT).show();
                            }
                        });
                historySwipe.setRefreshing(false);
            }
        });

        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HistoryActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });


    }
}