package ro.softronic.mihai.ro.papagodriver.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.realm.Realm;
import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;
import ro.softronic.mihai.ro.papagodriver.Model.Order;
import ro.softronic.mihai.ro.papagodriver.R;


public class OrderAdapter extends RealmBaseAdapter<Order> implements ListAdapter {
    public  Context mContext;
    private Handler mHandler = new Handler();
    private List<ViewHolder> lstHolders;
    private Realm realm;

    private Runnable updateRemainingTimeRunnable = new Runnable() {
        @Override
        public void run() {
            synchronized (lstHolders) {
                long currentTime = System.currentTimeMillis();
                for (ViewHolder holder : lstHolders) {

                    holder.updateTimeRemaining(currentTime);
                }
            }
        }
    };
    private static class ViewHolder {
        TextView position;
        ImageButton btn_direction;
        ImageButton btn_remove;
        TextView time;
        Order mOrder;

        public void setData(Order item) {
            mOrder = item;
            //??
            updateTimeRemaining(System.currentTimeMillis());
        }

        public void updateTimeRemaining(long currentTime) {
            if (mOrder.isValid() ){
                long timeDiff = mOrder.getExpirationTime() - currentTime;
                int hours = 0, minutes = 0, seconds = 0;
                if (timeDiff > 0) {
                    seconds = (int) (timeDiff / 1000) % 60;
                    minutes = (int) ((timeDiff / (1000 * 60)) % 60);
                    hours = (int) ((timeDiff / (1000 * 60 * 60)) % 24);
                    time.setText(hours + " :" + minutes + " : " + seconds);
                } else {
                    time.setText("Expirat!");
                }
            }
        }


    }

    public OrderAdapter(Context context, int resId, RealmResults<Order> realmResults, boolean automaticUpdate) {
        super(context, realmResults, automaticUpdate);
        this.mContext = context;
        lstHolders = new ArrayList<>();
        startUpdateTimer();

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.order_list_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.position = (TextView) convertView.findViewById(R.id.order_position);
            viewHolder.time = (TextView) convertView.findViewById(R.id.time);
            viewHolder.btn_direction = (ImageButton) convertView.findViewById(R.id.item_direction);
            viewHolder.btn_direction.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    String name = "party";

//                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:44.339120,23.774750"));
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse ("geo:0,0?q=44.339120,23.774750 (" + name + ")"));

                    i.setClassName("com.google.android.apps.maps",
                            "com.google.android.maps.MapsActivity");
                    mContext.startActivity(i);

//                    Bundle bundleCotizacion = new Bundle();
//
//                    bundleCotizacion.putString("hola", "hola");
//
//
//                    Intent intent = new Intent(mContext, DetailActivity.class);
//                    // ("restID", restaurant.getRestID());
//                    mContext.startActivity(intent);


                }

            });
            viewHolder.btn_remove = (ImageButton) convertView.findViewById(R.id.item_remove);
            viewHolder.btn_remove.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                   // *** Remove from Realm
                    realm = Realm.getDefaultInstance();
                    realm.beginTransaction();
                    RealmResults<Order> orders = realm.where(Order.class).findAll();
                    // remove single match
                    int pos = position;
                    //Send broadcast to Harti pentru a sterge markerul
                    String latitude = orders.get(pos).getClient_latitude();
                    String longitude = orders.get(pos).getClient_longitude();
                    Intent intent = new Intent();
                    intent.setAction("delpin_IntentFilter_string");
                    intent.putExtra("longitude", longitude);
                    intent.putExtra("latitude", latitude);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

                    orders.remove(pos);
                    realm.commitTransaction();
                    notifyDataSetChanged();


//                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                }
            });

            convertView.setTag(viewHolder);

            synchronized (lstHolders) {
                lstHolders.add(viewHolder);
            }

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.setData(getItem(position));


        Order item = realmResults.get(position);
        viewHolder.position.setText(item.getClient_latitude() + ',' + item.getClient_longitude());
        return convertView;
    }

    private void startUpdateTimer() {
        Timer tmr = new Timer();
        tmr.schedule(new TimerTask() {
            @Override
            public void run() {
                mHandler.post(updateRemainingTimeRunnable);
            }
        }, 1000, 1000);
    }



    public RealmResults<Order> getRealmResults() {
        return realmResults;
    }
}