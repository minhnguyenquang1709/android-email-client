package vn.edu.usth.email.Adapter;// EmailAdapter.java

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import vn.edu.usth.email.Activity.EmailDetailActivity;
import vn.edu.usth.email.Model.Email;
import vn.edu.usth.email.R;

public class EmailAdapter extends RecyclerView.Adapter<EmailAdapter.EmailViewHolder> {
    private Context context;
    private List<Email> originalEmailList;
    private List<Email> filteredEmailList;
    private String accessToken;

    public EmailAdapter(Context context, List<Email> emailList, String accessToken) {
        this.context = context;
        this.originalEmailList = new ArrayList<>(emailList);
        this.filteredEmailList = new ArrayList<>(emailList);
        this.accessToken = accessToken;
    }

    @NonNull
    @Override
    public EmailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_email, parent, false);
        return new EmailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmailViewHolder holder, int position) {
        Email email = filteredEmailList.get(position);
        holder.icon.setText(email.getIcon());
//        holder.senderName.setText(email.getSenderName());
        holder.snippet.setText(email.getSnippet());
        holder.time.setText(email.getTime());
        holder.subject.setText(email.getSubject());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, EmailDetailActivity.class);
            intent.putExtra("senderName", email.getSenderName());
            intent.putExtra("snippet", email.getSnippet());
            intent.putExtra("time", email.getTime());
            intent.putExtra("subject", email.getSubject());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return filteredEmailList.size();
    }

    // method to update the original email list and reset the filtered list
    public void updateOriginalEmails(List<Email> newEmails) {
        Log.i("EmailAdapter", "updating original email list...");
        for(Email email: newEmails){
            Log.i("EmailAdapter", newEmails.indexOf(email) + ": " +email.getTime());
        }
        originalEmailList.clear();
        originalEmailList.addAll(newEmails);
        filteredEmailList.clear();
        filteredEmailList.addAll(newEmails); // Reset filtered list to original
        notifyDataSetChanged();
    }

    public static class EmailViewHolder extends RecyclerView.ViewHolder {
        TextView icon, senderName, snippet, time, subject;
        ImageView starIcon;

        public EmailViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.icon);
//            senderName = itemView.findViewById(R.id.sender_name);
            snippet = itemView.findViewById(R.id.snippet);
            time = itemView.findViewById(R.id.time);
            starIcon = itemView.findViewById(R.id.starIcon);
            subject = itemView.findViewById(R.id.subject);
        }
    }

    // Method to filter the list
    public void filter(String query) {
        filteredEmailList.clear();
        if (query.isEmpty()) {
            filteredEmailList.addAll(originalEmailList); // Show all if query is empty
        } else {
            for (Email email : originalEmailList) {
                if (email.getSenderName().toLowerCase().contains(query.toLowerCase()) ||
                        email.getSnippet().toLowerCase().contains(query.toLowerCase())) {
                    filteredEmailList.add(email);
                }
            }
        }
        notifyDataSetChanged();
    }
}
