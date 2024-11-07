package vn.edu.usth.email.Fragment;

import android.media.Image;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import vn.edu.usth.mobile_project.R;
/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainToolbarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainToolbarFragment extends Fragment {
    ImageButton btnSearch;
    ImageButton btnSettings;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MainToolbarFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainToolbarFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainToolbarFragment newInstance(String param1, String param2) {
        MainToolbarFragment fragment = new MainToolbarFragment();
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
        View view = inflater.inflate(R.layout.fragment_main_toolbar, container, false);

        btnSearch = view.findViewById(R.id.btn_search);
        btnSettings = view.findViewById(R.id.btn_settings);

        // setup click events
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("MainToolbarFragment", "Start search");

                // start SearchActivity
            }
        });
        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("MainToolbarFragment", "Start settings");

                // start SettingsActivity
            }
        });

        return view;
    }
}