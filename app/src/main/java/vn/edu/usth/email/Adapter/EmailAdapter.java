package vn.edu.usth.email.Adapter;

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
import vn.edu.usth.email.Model.Email;
import vn.edu.usth.email.R;
import java.util.ArrayList;
import java.util.List;

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
        holder.senderName.setText(email.getSenderName());
        holder.snippet.setText(email.getSnippet());
        holder.time.setText(email.getTime());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, EmailDetailActivity.class);
            intent.putExtra("senderName", email.getSenderName());
            intent.putExtra("snippet", email.getSnippet());
            intent.putExtra("time", email.getTime());
            intent.putExtra("messageId", email.getMessageId());
            intent.putExtra("accessToken", accessToken);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return filteredEmailList.size();
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
            starIcon = itemView.findViewById(R.id.starIcon);
        }
    }
}
