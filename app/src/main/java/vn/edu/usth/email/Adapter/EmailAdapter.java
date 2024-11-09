package vn.edu.usth.email.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import vn.edu.usth.email.Model.Email;
import vn.edu.usth.email.R;
import java.util.ArrayList;
import java.util.List;

public class EmailAdapter extends RecyclerView.Adapter<EmailAdapter.EmailViewHolder> {

    // Interface for item click listener to handle item clicks
    public interface OnItemClickListener {
        void onItemClick(String senderName, String snippet, String time, String messageId, String subject);
    }

    private OnItemClickListener listener;
    private Context context;
    private List<Email> originalEmailList;
    private List<Email> filteredEmailList;
    private String accessToken;

    // Constructor to initialize context, email list, and access token
    public EmailAdapter(Context context, List<Email> emailList, String accessToken) {
        this.context = context;
        this.originalEmailList = new ArrayList<>(emailList);
        this.filteredEmailList = new ArrayList<>(emailList);
        this.accessToken = accessToken;
    }

    // Method to set the item click listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public EmailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_email, parent, false);
        return new EmailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmailViewHolder holder, int position) {
        // Get the email data at the current position
        Email email = filteredEmailList.get(position);

        // Set data to the view holder views
        holder.icon.setText(email.getIcon());
        holder.senderName.setText(email.getSenderName());
        holder.snippet.setText(email.getSnippet());
        holder.time.setText(email.getTime());

        // Set click listener for the item
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                // Pass the subject along with other details to the listener
                listener.onItemClick(
                        email.getSenderName(),
                        email.getSnippet(),
                        email.getTime(),
                        email.getMessageId(),
                        email.getSubject() // Pass the subject here
                );
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredEmailList.size();
    }

    // ViewHolder class to represent each item
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
