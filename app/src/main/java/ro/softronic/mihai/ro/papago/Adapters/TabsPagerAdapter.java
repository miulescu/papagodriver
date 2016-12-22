package ro.softronic.mihai.ro.papago.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import ro.softronic.mihai.ro.papagodriver.Fragments.CurseFragment;
import ro.softronic.mihai.ro.papagodriver.Fragments.HartiFragment;

public class TabsPagerAdapter extends FragmentPagerAdapter {
    private String tabTitles[] = new String[] { "Harta", "Curse", "Detalii Cursa" };
    private Bundle mybundle;

    public TabsPagerAdapter(FragmentManager fm, Context context ) {
        super(fm);
    }

    @Override
    public Fragment getItem(int index) {

        switch (index) {
            case 0:
                // Top Rated fragment activity
                return new HartiFragment();
            case 1:
                // Games fragment activity
                return new CurseFragment();
//            case 2:
//                if (mybundle != null)
//                {
//                     DetaliiCursaFragment dtlFragment = DetaliiCursaFragment.newInstance(mybundle);
//                     return dtlFragment;
//                }
//                else{
//                    return new DetaliiCursaFragment();
//                }
        }

        return null;
    }

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }

//    @Override
//    public int getItemPosition(Object object) {
//        return POSITION_NONE;
//    }

    public Bundle getMybundle(){
        return mybundle;
    }

    public void  setMybundle(Bundle bundle){
        this.mybundle = bundle;
//        notifyDataSetChanged();
    }
}