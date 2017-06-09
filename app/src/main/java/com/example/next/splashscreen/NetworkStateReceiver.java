package com.example.next.splashscreen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.next.splashscreen.adapter.ListAdapter;
import com.example.next.splashscreen.database.DBHelper;
import com.example.next.splashscreen.services.UpdateService;

/**
 * Created by next on 27/2/17.
 */
public class NetworkStateReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("app","Network connectivity change");

        DBHelper databaase= new DBHelper(context);
        if(databaase.hasNonSyncdata() && ListAdapter.isOnlile(context)) {
            System.out.println("Starting service...");
            context.startService(new Intent(context, UpdateService.class));
        }
    }
}
