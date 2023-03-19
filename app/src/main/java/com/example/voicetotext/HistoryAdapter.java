package com.example.voicetotext;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.viewHolder> {

    private final Context context;
    private final ArrayList<HistoryCard> HistoryCardArrayList;

    // Constructor
    public HistoryAdapter(Context context, ArrayList<HistoryCard> HistoryCardArrayList) {
        this.context = context;
        this.HistoryCardArrayList = HistoryCardArrayList;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // to inflate the layout for each item of recycler view.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_card, parent, false);
        return new viewHolder(view);
    }

    public static class viewHolder extends RecyclerView.ViewHolder {

        private final TextView date;
        private final TextView content;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date);
            content = itemView.findViewById(R.id.content);

        }
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        HistoryCard model = HistoryCardArrayList.get(position);
        holder.content.setText(model.getContent());
        holder.date.setText(model.getDate());
    }


//    public void onBindViewHolder(@NonNull HistoryAdapter.Viewholder holder, int position) {
//        // to set data to textview and imageview of each card layout
//        HistoryCard model = HistoryCardArrayList.get(position);
//        holder.content.setText(model.getContent());
//        holder.date.setText(model.getDate());
//
//    }

    @Override
    public int getItemCount() {
        // this method is used for showing number of card items in recycler view
        return HistoryCardArrayList.size();
    }



}

