package com.digipass.android.helpers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import com.digipass.android.MainActivity;
import com.digipass.android.R;

public class NetworkReceiver extends BroadcastReceiver {

    private boolean lostConnection = false;
    private boolean reshow = false;
    private View view;

    public NetworkReceiver(Context c) {
        view = ((MainActivity)c).findViewById(R.id.root_view);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager conn = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conn.getActiveNetworkInfo();

        if (networkInfo != null) {
            if (lostConnection) {
                if (view != null ){
                    Snackbar.make(view, R.string.io_connected, Snackbar.LENGTH_SHORT).show();
                }
                lostConnection = false;
            }
        } else {
            notConnected();
        }

    }

    private void notConnected() {
        if (view == null) {
            return;
        }
        reshow = false;
        final Snackbar snackbar = Snackbar.make(view, R.string.lost_connection, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(R.string.what_now, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Digipass", "What now???");
                reshow = true;
            }
        });
        snackbar.setCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                super.onDismissed(snackbar, event);
                if (reshow) {
                    notConnected();
                }
            }
        });
        snackbar.show();
        lostConnection = true;
    }
}