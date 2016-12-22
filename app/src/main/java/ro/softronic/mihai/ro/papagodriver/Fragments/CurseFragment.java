package ro.softronic.mihai.ro.papagodriver.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import io.realm.Realm;
import io.realm.RealmResults;
import ro.softronic.mihai.ro.papagodriver.Adapters.OrderAdapter;
import ro.softronic.mihai.ro.papagodriver.Model.Order;
import ro.softronic.mihai.ro.papagodriver.R;

public class CurseFragment extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";
    private int mPage;

    private Realm myRealm;
    private OrderAdapter adapter;
    private ListView mListView;
    private final Object realmLock = new Object();



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myRealm = Realm.getDefaultInstance();


//        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this.getActivity())
//                .name(Realm.DEFAULT_REALM_NAME)
//                .schemaVersion(0)
//                .deleteRealmIfMigrationNeeded()
//                .build();

//          myRealm = Realm.getDefaultInstance();
//        try {
//            myRealm = Realm.getInstance(realmConfiguration);
//        } catch (RealmMigrationNeededException e) {
//            try {
//                Realm.deleteRealm(realmConfiguration);
//                //Realm file has been deleted.
//                myRealm = getInstance(realmConfiguration);
//            } catch (Exception ex) {
//                throw ex;
//                //No Realm file to remove.
//            }
//        }
    }





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_curse, container, false);
        mListView = (ListView)rootView.findViewById(android.R.id.list);


        RealmResults<Order> orders = myRealm.where(Order.class).findAll();
        adapter = new OrderAdapter(this.getActivity(), android.R.id.list, orders, true);
        mListView.setAdapter(adapter);



        Button btn_stertot= (Button)rootView.findViewById(R.id.btn_sterg);

                    btn_stertot.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
//                            myRealm = getInstance(getActivity());
                            myRealm.beginTransaction();
                            RealmResults<Order> results = myRealm.where(Order.class).findAll();
                            results.clear();
                            myRealm.commitTransaction();

                            Intent intent = new Intent();
                            intent.setAction("delpin_IntentFilter_string");
                            intent.putExtra("clear_all", "OK");
                            LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);

                        }
                    });







        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        // In case Google Play services has since become available.


//        myRealm = getInstance(getActivity());
//        myRealm.beginTransaction();
//        RealmResults<Order> results = myRealm.where(Order.class).findAll();
//        results.clear();
//        // Create an object
////        Order order1 = myRealm.createObject(Order.class);
////
////        // Set its fields
////        order1.setId(4);
////        order1.setRestaurant("Temple Bar");
////        order1.setStatus("cooking");
//
//        myRealm.commitTransaction();
//
//        if(mAdapter == null) {
//            List<School> schools = null;
//            try {
//                schools = loadSchools();
//                RealmPath =  realm.getPath();
//                //Log.d( tag, String.valueOf(RealmPath));
//                Log.d( tag, String.valueOf(schools));
//
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//        }
        myRealm = Realm.getDefaultInstance();
        RealmResults<Order> orders = myRealm.where(Order.class).findAll();
        adapter = new OrderAdapter(this.getActivity(), android.R.id.list, orders, true);
        mListView.setAdapter(adapter);

    }




//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        myRealm.close();
//    }






}