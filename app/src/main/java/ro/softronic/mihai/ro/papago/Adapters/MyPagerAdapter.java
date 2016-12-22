package ro.softronic.mihai.ro.papago.Adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import ro.softronic.mihai.ro.papagodriver.Fragments.CurseFragment;
import ro.softronic.mihai.ro.papagodriver.Fragments.DetaliiCursaFragment;
import ro.softronic.mihai.ro.papagodriver.Fragments.HartiFragment;

public class MyPagerAdapter extends SmartFragmentStatePagerAdapter {
    private static int NUM_ITEMS = 3;
    private Bundle mybundle;

    public MyPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    // Returns total number of pages
    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    // Returns the fragment to display for that page
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: // Fragment # 0 - This will show FirstFragment
                return new HartiFragment();
            case 1: // Fragment # 0 - This will show FirstFragment different title
                return new CurseFragment();
            case 2: // Fragment # 1 - This will show SecondFragment
                if (mybundle != null)
                {
                    DetaliiCursaFragment dtlFragment = DetaliiCursaFragment.newInstance(mybundle);
                    return dtlFragment;
                }
                else{
                    return new DetaliiCursaFragment();
                }
//                return DetaliiCursaFragment.newInstance(2, "Page # 3");
            default:
                return null;
        }
    }

    // Returns the page title for the top indicator
    @Override
    public CharSequence getPageTitle(int position) {
        return "Page " + position;
    }
    public Bundle getMybundle(){
        return mybundle;
    }

    public void  setMybundle(Bundle bundle){
        this.mybundle = bundle;
//        notifyDataSetChanged();
    }

    @Override
    public int getItemPosition(Object object) {
//        Fragment fragment = (Fragment) object;
//        int position = super.getItemPosition(object);
//
//        if (position == 0 | position ==1  ) {
//            return position;
//        } else {
//            return POSITION_NONE;
//        }
        return POSITION_NONE;
    }
}

