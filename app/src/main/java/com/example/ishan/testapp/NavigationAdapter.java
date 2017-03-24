package com.example.ishan.testapp;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Ishan on 7/1/2016.
 */
public class NavigationAdapter extends FragmentPagerAdapter {
    FragmentManager fm;
    Context mContext;

    public NavigationAdapter(FragmentManager fm, Context context){
        super(fm);
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public Fragment getItem(int position) {
        if(position == 1){
            return new DataFragment();
        } else {
            return new DataEntryFragment();
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if(position == 1){
            return mContext.getString(R.string.enter_data);
        } else {
            return mContext.getString(R.string.view_data);
        }
    }
}
