package vn.edu.usth.email.Adapter;// EmailAdapter.java
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import vn.edu.usth.email.Activity.EmailDetailActivity;
import vn.edu.usth.email.R;
import java.util.List;


import vn.edu.usth.email.Model.Email;

public class EmailAdapter extends RecyclerView.Adapter<EmailAdapter.EmailViewHolder> {

    private Context context;
    private List<Email> emailList;

    public EmailAdapter(Context context, List<Email> emailList) {
        this.context = context;
        this.emailList = emailList;
    }

    @NonNull
    @Override
    public EmailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_email, parent, false);
        return new EmailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmailViewHolder holder, int position) {
        Email email = emailList.get(position);
        holder.icon.setText(email.getIcon());
        holder.senderName.setText(email.getSenderName()); // Changed from title to senderName
        holder.snippet.setText(email.getSnippet());
        holder.time.setText(email.getTime());
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, EmailDetailActivity.class);
            intent.putExtra("senderName", email.getSenderName());
            intent.putExtra("snippet", email.getSnippet());
            intent.putExtra("time", email.getTime());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return emailList.size();
    }

    public static class EmailViewHolder extends RecyclerView.ViewHolder {
        TextView icon, senderName, snippet, time;
        ImageView starIcon;

        public EmailViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.icon);
            senderName = itemView.findViewById(R.id.title);
            snippet = itemView.findViewById(R.id.snippet);
            time = itemView.findViewById(R.id.time);
            starIcon = itemView.findViewById(R.id.star_icon);
        }
    }
}
