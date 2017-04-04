package com.iemqra.bme.lostnfound.ui.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class PagerFragment extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PagerFragment(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                SearchFragment tab1 = new SearchFragment();
                return tab1;
            case 1:
                MyItemsFragment tab2 = new MyItemsFragment();
                return tab2;
            case 2:
                NewItemFragment tab3 = new NewItemFragment();
                return tab3;
            case 3:
                FollowingsFragment tab4 = new FollowingsFragment();
                return tab4;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}