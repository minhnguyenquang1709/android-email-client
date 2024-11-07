package vn.edu.usth.email.Adapter;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import vn.edu.usth.email.Fragment.FolderFragment;
import vn.edu.usth.email.Fragment.InboxFragment;
import vn.edu.usth.email.Fragment.StarredFragment;
import vn.edu.usth.mobile_project.R;
public class MainViewPagerAdapter extends FragmentStateAdapter {
    private static final int NUM_PAGES = 3;
    FloatingActionButton fab;

    public MainViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        fab = fragmentActivity.findViewById(R.id.activity_write);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new FolderFragment();
            case 1:
                return new InboxFragment();
            case 2:
                return new StarredFragment();
            default:
                return new InboxFragment();
        }
    }

    @Override
    public int getItemCount() {
        return NUM_PAGES;
    }
}
