package vn.edu.usth.email.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

// import vn.edu.usth.email.Activity.ReadMailActivity;
import vn.edu.usth.email.Model.EmailItem;
import vn.edu.usth.email.R;

public class MailAdapter extends RecyclerView.Adapter<MailVH> {
    private Context context;
    private ArrayList<EmailItem> emails;

    public MailAdapter(Context context, ArrayList<EmailItem> emails){
        this.context = context; // for
        this.emails = emails;
    }

    @NonNull
    @Override
    public MailVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(this.context);
        View email = inflater.inflate(R.layout.item_inbox, parent, false);
        MailVH mailVh = new MailVH(email);
        return mailVh;
    }

    @Override
    public void onBindViewHolder(@NonNull MailVH holder, int position) {
        // bind email item data to the ViewHolder
        EmailItem emailItem = this.emails.get(position);
        holder.setProfileImg(R.drawable.profile_picture1);
        holder.setUsername(emailItem.getUsername());
        holder.setSubject(emailItem.getSubject());
        holder.setTimeSent(emailItem.getTimeSent());
        holder.setContent(emailItem.getContent());
        if (emailItem.isStarred()){
            holder.setStar(true);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("item", "open mail");
                // Intent intent = new Intent(context, ReadMailActivity.class);
                // context.startActivity(intent);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                holder.toggleOverlay();
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.emails.size();
    }
}
