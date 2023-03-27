package com.example.voicetotext;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.viewHolder> {

    private final Context context;
    private final ArrayList<HistoryCard> HistoryCardArrayList;
    private int flag=0;

    public HistoryAdapter(Context context, ArrayList<HistoryCard> HistoryCardArrayList) {
        this.context = context;
        this.HistoryCardArrayList = HistoryCardArrayList;

    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_card, parent, false);
        return new viewHolder(view);
    }

    public static class viewHolder extends RecyclerView.ViewHolder {

        private final TextView date;
        private final TextView content;
        CardView cardView;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date);
            content = itemView.findViewById(R.id.content);
            cardView = itemView.findViewById(R.id.history_card);
            content.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        HistoryCard model = HistoryCardArrayList.get(position);
        holder.date.setText(model.getDate());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(flag==0){
                    holder.content.setText(model.getContent());
                    holder.content.setVisibility(View.VISIBLE);
                    flag=1;
                }else if (flag==1){
                    holder.content.setText(" ");
                    holder.content.setVisibility(View.INVISIBLE);
                    flag=0;
                }

            }
        });

    }

    @Override
    public int getItemCount() {

        return HistoryCardArrayList.size();
    }



}

