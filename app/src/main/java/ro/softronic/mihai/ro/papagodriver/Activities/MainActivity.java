package ro.softronic.mihai.ro.papagodriver.Activities;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import io.realm.Realm;
import ro.softronic.mihai.ro.papago.Adapters.TabsPagerAdapter;
import ro.softronic.mihai.ro.papagodriver.R;


public class MainActivity extends AppCompatActivity {
    private Realm realm;
    public ViewPager viewPager;
    public TabLayout tabLayout;
//    private PagerAdapter mPagerAdapter;
    PagerAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        List<Fragment> fragments = new Vector<Fragment>();
//        // Add Fragments in a list
//
//
//        fragments.add(Fragment.instantiate(this,HartiFragment.class.getName()));
//        fragments.add(Fragment.instantiate(this,CurseFragment.class.getName()));
//        fragments.add(Fragment.instantiate(this,DetaliiCursaFragment.class.getName()));
        // Creation of theadapter
//        this.mPagerAdapter = new MyPagerAdapter(super.getSupportFragmentManager(), fragments);

        adapter = new TabsPagerAdapter(getSupportFragmentManager(), MainActivity.this);

//        adapter = new MyPagerAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);



        // Get the ViewPager and set it's PagerAdapter so that it can display items




//        viewPager.setAdapter(mPagerAdapter);


        // Give the TabLayout the ViewPager
        tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);

        tabLayout.setupWithViewPager(viewPager);

        realm = Realm.getDefaultInstance();
//
//        realm.beginTransaction();
//        RealmResults<Order> results = realm.where(Order.class).findAll();
//        results.clear();
//        realm.commitTransaction();
    }

    public Realm getRealm(){
        return realm;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }


}
