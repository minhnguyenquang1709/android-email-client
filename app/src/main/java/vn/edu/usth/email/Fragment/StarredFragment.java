package vn.edu.usth.email.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import vn.edu.usth.email.Adapter.MailAdapter;
import vn.edu.usth.email.Model.EmailItem;
import vn.edu.usth.mobile_project.R;
/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StarredFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StarredFragment extends Fragment {
    private MailAdapter adapter;
    private RecyclerView mailListView;
    private ArrayList<EmailItem> mailList;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public StarredFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StarredFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StarredFragment newInstance(String param1, String param2) {
        StarredFragment fragment = new StarredFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_inbox, container, false);

        mailList = new ArrayList<>();
        createMails(6);

        // enable star icon
        mailList.forEach(emailItem -> {emailItem.setStarred(true);});

        if(view != null){
            mailListView = view.findViewById(R.id.recycler_mail);
            adapter = new MailAdapter(getContext(), mailList);
            mailListView.setAdapter(adapter);
            mailListView.setLayoutManager(new LinearLayoutManager(getContext()));
        }

        return view;
    }

    public void createMails(int num){
        mailList.add(new EmailItem("Sammy Hackett", "sam01@gmail.com", "International Officer", 120, getString(R.string.content), R.drawable.profile_picture1));
        mailList.add(new EmailItem("Joshua Durgan", "josh71x@gmail.com", "Islands", 2400, getString(R.string.content), R.drawable.profile_picture2));
        mailList.add(new EmailItem("Karen Lind", "kl1994@gmail.com", "Electronics International", 2580, getString(R.string.content), R.drawable.profile_picture3));
        mailList.add(new EmailItem("Elizabeth Leannon DVM", "elizaL@gmail.com", "Markets Australia", 3600, getString(R.string.content), R.drawable.profile_picture4));
        mailList.add(new EmailItem("Kristopher Cremin", "kris92@gmail.com", "Buckinghamshire", 3751, getString(R.string.content), R.drawable.profile_picture5));
        mailList.add(new EmailItem("John Pfannerstill", "johny@gmail.com", "Proactive", 7271, getString(R.string.content), R.drawable.profile_picture6));

        mailList.add(new EmailItem("Sammy Hackett", "sam01@gmail.com", "International Officer", 120, getString(R.string.content), R.drawable.profile_picture1));
        mailList.add(new EmailItem("Joshua Durgan", "josh71x@gmail.com", "Islands", 2400, getString(R.string.content), R.drawable.profile_picture2));
        mailList.add(new EmailItem("Karen Lind", "kl1994@gmail.com", "Electronics International", 2580, getString(R.string.content), R.drawable.profile_picture3));
        mailList.add(new EmailItem("Elizabeth Leannon DVM", "elizaL@gmail.com", "Markets Australia", 3600, getString(R.string.content), R.drawable.profile_picture4));
        mailList.add(new EmailItem("Kristopher Cremin", "kris92@gmail.com", "Buckinghamshire", 3751, getString(R.string.content), R.drawable.profile_picture5));
        mailList.add(new EmailItem("John Pfannerstill", "johny@gmail.com", "Proactive", 7271, getString(R.string.content), R.drawable.profile_picture6));

        mailList.add(new EmailItem("Sammy Hackett", "sam01@gmail.com", "International Officer", 120, getString(R.string.content), R.drawable.profile_picture1));
        mailList.add(new EmailItem("Joshua Durgan", "josh71x@gmail.com", "Islands", 2400, getString(R.string.content), R.drawable.profile_picture2));
        mailList.add(new EmailItem("Karen Lind", "kl1994@gmail.com", "Electronics International", 2580, getString(R.string.content), R.drawable.profile_picture3));
        mailList.add(new EmailItem("Elizabeth Leannon DVM", "elizaL@gmail.com", "Markets Australia", 3600, getString(R.string.content), R.drawable.profile_picture4));
        mailList.add(new EmailItem("Kristopher Cremin", "kris92@gmail.com", "Buckinghamshire", 3751, getString(R.string.content), R.drawable.profile_picture5));
        mailList.add(new EmailItem("John Pfannerstill", "johny@gmail.com", "Proactive", 7271, getString(R.string.content), R.drawable.profile_picture6));
    }
}