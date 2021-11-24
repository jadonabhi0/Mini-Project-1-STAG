package com.example.stag.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.stag.Fragments.Gossips;
import com.example.stag.Fragments.Meet;

public class FragmentAdapters extends FragmentPagerAdapter {


    public FragmentAdapters(@NonNull FragmentManager fm) {
        super(fm);
    }

    public FragmentAdapters(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch (position){
            case 0: return new Gossips();
            case 1: return new Meet();


            default: return new Gossips();

        }
    }

    @Override
    public int getCount() {
        return 2;
    }


    @Override
    public CharSequence getPageTitle(int position) {

        String title = null;


        if (position==0){
            title = "GOSSIPS";
        }

        else if (position == 1){
            title = "MEET";
        }




        return title;
    }



}
