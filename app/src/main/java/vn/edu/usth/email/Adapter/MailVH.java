package vn.edu.usth.email.Adapter;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import vn.edu.usth.email.Model.EmailItem;
import vn.edu.usth.email.R;

public class MailVH extends RecyclerView.ViewHolder {
    private EmailItem data;
    private ImageView profileImg;
    private TextView username;
    private TextView subject;
    private TextView timeSent;
    private TextView content;
    private RelativeLayout overlay;
    private ImageView star;

    public MailVH(@NonNull View itemView) {
        super(itemView);

        // link ViewHolder with child components for easy
        this.profileImg = itemView.findViewById(R.id.profile_image);
        this.username = itemView.findViewById(R.id.user_name);
        this.subject = itemView.findViewById(R.id.subject);
        this.timeSent = itemView.findViewById(R.id.time_sent);
        this.content = itemView.findViewById(R.id.content);
        this.overlay = itemView.findViewById(R.id.profileOverlay);
        this.star = itemView.findViewById(R.id.star_active);
    }

    public ImageView getProfileImg() {
        return profileImg;
    }

    public TextView getSubject() {
        return subject;
    }

    public TextView getTimeSent() {
        return timeSent;
    }

    public TextView getUsername() {
        return username;
    }

    public RelativeLayout getOverlay() {
        return overlay;
    }

    public TextView getContent() {
        return content;
    }

    public ImageView getStar() {
        return star;
    }

    public void setProfileImg(int imgId) {
        this.profileImg.setImageResource(imgId);
    }

    public void setUsername(String username) {
        this.username.setText(username);
    }

    public void setSubject(String subject) {
        this.subject.setText(subject);
    }

    public void setTimeSent(int timeSent) {
        if(timeSent < 60){
            this.timeSent.setText(timeSent + "s ago");
        }else{
            this.timeSent.setText(timeSent/60+"m ago");
        }

    }

    public void setContent(String content) {
        this.content.setText(content);
    }

    public void setStar(boolean visible) {
        if (visible){
            this.star.setVisibility(View.VISIBLE);
        }
        else{
            this.star.setVisibility(View.GONE);
        }
    }

    public void toggleOverlay(){
        Log.i("ViewHolder", "Toggle Visibility: " + this.overlay.getVisibility());
        if(this.overlay.getVisibility() == ViewGroup.GONE){
            this.overlay.setVisibility(ViewGroup.VISIBLE);
        }
        else if(this.overlay.getVisibility() == ViewGroup.VISIBLE){
            this.overlay.setVisibility(ViewGroup.GONE);
        }
    }
}
