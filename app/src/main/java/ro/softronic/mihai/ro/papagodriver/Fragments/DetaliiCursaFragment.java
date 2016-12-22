package ro.softronic.mihai.ro.papagodriver.Fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ro.softronic.mihai.ro.papagodriver.R;

public class DetaliiCursaFragment extends Fragment {
    ProgressDialog pdialog;
    boolean resp;
    String msg;

    private TextView txBuscarProductoA;


    public DetaliiCursaFragment() {
    }

    public static DetaliiCursaFragment newInstance(Bundle arguments) {
        DetaliiCursaFragment tabFragmentProducto = new DetaliiCursaFragment();
//        Bundle args = new Bundle();
//
//        args.putString("someTitle", "title");
//        tabFragmentProducto.setArguments(args);

        if (arguments != null) {
            tabFragmentProducto.setArguments(arguments);
        }
        return tabFragmentProducto;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments()!=null){
            msg = getArguments().getString("hola", "mmm");
//            msg = getArguments().getString("someTitle", "mmm");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_detalii_cursa, container, false);

        txBuscarProductoA = (TextView) rootView.findViewById(R.id.notification_text);
        txBuscarProductoA.setText(msg);

        return  rootView;
    }
}
