package com.example.next.splashscreen.controller;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.next.splashscreen.interfac.OnUploadSyncI;
import com.example.next.splashscreen.model.DataModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by next on 22/2/17.
 */
public class UpdateController {
    Context mContext;
    private String mData = "";
    private String mUrl = "";
    private static String TAG = "UpdateController";

    public UpdateController(Context mContext)
    {
        this.mContext = mContext;
    }
    public void updateData(String object, String postUrl, OnUploadSyncI onUploadSyncI, int key)
    {
        Log.i(TAG, "updateData: ");
        new PostRequesttask(key, onUploadSyncI).execute(object, postUrl);
    }




    private class PostRequesttask extends AsyncTask<String, Void, String> {

        ProgressDialog dialog = null;
        HttpURLConnection urlConnection = null;

        private OnUploadSyncI mOnUploadSyncI;
        private int mKey;

        public PostRequesttask(int key, OnUploadSyncI onUploadSyncI) {
            this.mKey = key;
            this.mOnUploadSyncI = onUploadSyncI;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(mOnUploadSyncI == null) {
                dialog = new ProgressDialog(mContext);
                dialog.setTitle("please wait......");
                dialog.setMessage("updating the task");
                dialog.show();
                Log.i(TAG, "onPreExecute: ");
            }

        }

        @Override
        protected String doInBackground(String... params) {
            String JsonResponse = null;
            String JsonDATA = params[0];
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(params[1]);
                urlConnection = (HttpURLConnection) url.openConnection();
                //configure the connection
                urlConnection.setDoOutput(true);
                //when we dont know body length we hv write below line for best performance
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");
                Writer writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8"));
                writer.write(JsonDATA);
                Log.i(TAG, "doInBackground: " + JsonDATA);
                writer.close();

                //input Stream
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                Log.i(TAG, "doInBackground: " + inputStream.toString());
                if (inputStream == null) {
                    dialog.dismiss();
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String inputLine;
                while ((inputLine = reader.readLine()) != null) {
                    Log.i(TAG, "doInBackground: " + inputLine);
                    buffer.append(inputLine + "\n");
                    if (buffer.length() == 0) {
                        dialog.dismiss();
                        return null;
                    }
                    JsonResponse = buffer.toString();
                    return JsonResponse;
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }


        @Override
        protected void onPostExecute(String data) {
            super.onPostExecute(data);

            if(mOnUploadSyncI == null) {
                if (dialog != null) {
                    dialog.dismiss();
                }

                Log.i(TAG, "onPostExecute: " + data);
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(data);
                    String message = jsonObject.getString("message");
                    Toast.makeText(mContext, "update"+message, Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "onPostExecute: "+message);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


            if(mOnUploadSyncI != null) {
                mOnUploadSyncI.onuploaded(mKey);
            }






        }


        //get data to post from
        public void getpostData(String postdatafromadapter) {
            Log.i("update", "getpostData: " + postdatafromadapter);

        }
    }
}