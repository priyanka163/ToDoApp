package com.example.next.splashscreen.services;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.example.next.splashscreen.R;
import com.example.next.splashscreen.controller.UpdateController;
import com.example.next.splashscreen.database.DBHelper;
import com.example.next.splashscreen.interfac.OnUploadSyncI;
import com.example.next.splashscreen.model.DataModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by next on 27/2/17.
 */
public class UpdateService extends Service implements OnUploadSyncI{


    private String posturl = "";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {


        return null;

    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        System.out.println("inside onStartCommand...");
        posturl = getResources().getString(R.string.urlPost);

        DBHelper dbHelper = new DBHelper(this);
        List<DataModel> dataModels = dbHelper.getOfflineTasks();
        for(int i=0; i < dataModels.size(); i++) {
            // Updating the data to Server
            UpdateController updateController = new UpdateController(getApplicationContext());
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("title", dataModels.get(i).getTitle());
                jsonObject.put("note",dataModels.get(i).getData());
                jsonObject.put("userid",dataModels.get(i).getUserid());
                jsonObject.put("time",dataModels.get(i).getTime());
                //Remaining
            } catch (JSONException e) {
                e.printStackTrace();
            }

            updateController.updateData(jsonObject.toString(), posturl, this, dataModels.get(0).getSno());
        }



        return START_NOT_STICKY;
    }

    @Override
    public void onuploaded(int taskId) {
        System.out.println("inside onuploaded...");
        // After a success callback , update the primary key with 0
        DBHelper dbHelper = new DBHelper(getApplicationContext());
        dbHelper.updateTask(taskId);
    }

}
