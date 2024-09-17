package vn.edu.usth.email.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import vn.edu.usth.email.Fragment.FolderFragment;
import vn.edu.usth.email.Fragment.InboxFragment;
import vn.edu.usth.email.Fragment.StarredFragment;

public class MainViewPagerAdapter extends FragmentStateAdapter {
    private static final int NUM_PAGES = 3;

    public MainViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0: return new FolderFragment();
            case 1: return new InboxFragment();
            case 2: return new StarredFragment();
            default: return new InboxFragment();
        }
    }

    @Override
    public int getItemCount() {
        return NUM_PAGES;
    }
}
