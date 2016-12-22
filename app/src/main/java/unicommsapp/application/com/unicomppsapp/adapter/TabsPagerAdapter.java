package unicommsapp.application.com.unicomppsapp.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import unicommsapp.application.com.unicomppsapp.fragments.AnnouncementFragment;
import unicommsapp.application.com.unicomppsapp.fragments.ChatFragment;
import unicommsapp.application.com.unicomppsapp.fragments.ContestFragment;
import unicommsapp.application.com.unicomppsapp.fragments.ProfileFragment;
import unicommsapp.application.com.unicomppsapp.fragments.SettingsFragment;

/**
 * Created by furqan on 2/10/2016.
 */
public class TabsPagerAdapter extends FragmentStatePagerAdapter {

    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                //Fragement for Android Tab
                return new AnnouncementFragment();
            case 1:
                //Fragment for Ios Tab
                return new ChatFragment();
            case 2:
                //Fragment for Windows Tab
                return new ContestFragment();
            case 3:
                //Fragement for Android Tab
                return new SettingsFragment();
            case 4:
                //Fragment for Ios Tab
                return new ProfileFragment();
        }
        return null;

    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return 5; //No of Tabs
    }



}
