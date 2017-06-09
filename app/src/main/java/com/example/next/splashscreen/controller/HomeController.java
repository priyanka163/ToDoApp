package com.example.next.splashscreen.controller;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.next.splashscreen.Fragment.HomeFragment;
import com.example.next.splashscreen.database.DBHelper;
import com.example.next.splashscreen.interfac.ContentInterface;
import com.example.next.splashscreen.model.DataModel;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by next on 15/2/17.
 */
public class HomeController
{
String filename="MyFile.json";
    Context context ;
    DBHelper database;

    public HomeController(Context context) {
        this.context = context;
        //passing data to this class
         database = new DBHelper(context);
    }



      //rest api call
    public void restcall(final ContentInterface contentInterface)

     {
         new ServerTask(this,contentInterface).execute();
       /* final ArrayList<DataModel> arrayList=new ArrayList<DataModel>();
        AsyncHttpClient client= new AsyncHttpClient();
        client.get("http://freejsonapis-showin.rhcloud.com/getNote?userid=ram@gmail.com", new AsyncHttpResponseHandler()
        {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody)
            {


                JSONObject object= null;
                try {
                    object = new JSONObject(new String(responseBody));
           //data passed to database through  method in below line
                   database.insertData(object.toString());
                   writeToFile(object.toString());
                    JSONArray array =object.getJSONArray("list");
                    for (int i = 0; i < array.length(); i++)
                    {
                        DataModel model= new DataModel();
                        JSONObject childobj=array.getJSONObject(i);
                       model.setSno( childobj.getInt("sno"));
                        Log.i("controller", "onSuccess: "+childobj.getInt("sno"));
                        model.setData(childobj.getString("data"));
                        Log.i("controller", "onSuccess: "+childobj.getString("data"));
                        model.setUserid(childobj.getString("userid"));
                        Log.i("controller", "onSuccess: "+childobj.getString("userid"));
                        model.setTitle(childobj.getString("title"));
                        Log.i("controller", "onSuccess: "+childobj.getString("title"));
                        model.setTime(childobj.getString("time"));
                        Log.i("controller", "onSuccess: "+childobj.getString("time"));
                         arrayList.add(model);
                    }
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }

                contentInterface.populateData(arrayList);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error)
            {
                Log.i("controller", "onFailure: "+statusCode);
            }
        });*/
    }

    private void writeToFile(String data) {
        OutputStream outputStream = null;
        try {
            //store data in localfile system(internal storage)
            outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(data.getBytes());
            outputStream.close();
        } catch (FileNotFoundException exception) {
            Log.d("FileNotFound", "File not found Error");
        } catch (IOException ioException) {
            Log.d("IOError", "IO Error");
        }
    }


  private   class ServerTask extends AsyncTask<ArrayList<DataModel> ,Void ,ArrayList<DataModel>>  {
        String response;
      ArrayList<DataModel> arrayList;
      HomeController homeController;
      ContentInterface contentInterface;

      public ServerTask(HomeController homeController,ContentInterface interfacee) {
         this.homeController=homeController;
        this.contentInterface=interfacee;
      }

      @Override
        protected ArrayList<DataModel> doInBackground(ArrayList<DataModel>... params) {
            HttpURLConnection connection=null;
            try {
                URL url= new URL("http://freejsonapis-showin.rhcloud.com/getNote?userid=sgpriyanka18@gmail.com");

                connection= (HttpURLConnection) url.openConnection();
                int responseCode= connection.getResponseCode();


                if (responseCode== HttpURLConnection.HTTP_OK)
                {
                    response =readStream(connection.getInputStream());
                    Log.i("server", "doInBackground: "+response);


                     arrayList=new ArrayList<DataModel>();
                    Log.i("onpostresult", "onPostExecute: "+response);


                    JSONObject object=null;

                    try {
                        object = new JSONObject(response);
                        //data passed to database through  method in below line+
                        database.deleteItem();
                        database.insertData(response,1);

                        writeToFile(object.toString());
                        JSONArray array =object.getJSONArray("list");
                        for (int i = 0; i < array.length(); i++)
                        {
                            DataModel model= new DataModel();
                            JSONObject childobj=array.getJSONObject(i);
                            model.setSno( childobj.getInt("sno"));
                            Log.i("homecontroller", "onSuccesrestapicallget: "+childobj.getInt("sno"));
                            model.setData(childobj.getString("data"));
                            Log.i("homecontroller", "onSuccesrestapicallget: "+childobj.getString("data"));
                            model.setUserid(childobj.getString("userid"));
                            Log.i("homecontroller", "onSuccesrestapicallget: "+childobj.getString("userid"));
                            model.setTitle(childobj.getString("title"));
                            Log.i("homecontroller", "onSuccesrestapicallget: "+childobj.getString("title"));
                            model.setTime(childobj.getString("time"));
                            Log.i("homecontroller", "onSuccesrestapicallget: "+childobj.getString("time"));
                            arrayList.add(model);

                           // HomeFragment.callAdapter(arrayList,context);


                        }
                    } catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }


            } catch (MalformedURLException e) {

                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }



            return arrayList;
        }

        @Override
        protected void onPostExecute(ArrayList<DataModel> response) {
            super.onPostExecute(response);

            contentInterface.populateData(response);

            /*  ArrayList<DataModel> arrayList=new ArrayList<DataModel>();
            Log.i("onpostresult", "onPostExecute: "+response);


            JSONObject object=null;

            try {
                object = new JSONObject(response);
                //data passed to database through  method in below line
                database.insertData(response);
                writeToFile(object.toString());
                JSONArray array =object.getJSONArray("list");
                for (int i = 0; i < array.length(); i++)
                {
                    DataModel model= new DataModel();
                    JSONObject childobj=array.getJSONObject(i);
                    model.setSno( childobj.getInt("sno"));
                    Log.i("controller", "onSuccess: "+childobj.getInt("sno"));
                    model.setData(childobj.getString("data"));
                    Log.i("controller", "onSuccess: "+childobj.getString("data"));
                    model.setUserid(childobj.getString("userid"));
                    Log.i("controller", "onSuccess: "+childobj.getString("userid"));
                    model.setTitle(childobj.getString("title"));
                    Log.i("controller", "onSuccess: "+childobj.getString("title"));
                    model.setTime(childobj.getString("time"));
                    Log.i("controller", "onSuccess: "+childobj.getString("time"));
                    arrayList.add(model);
                    HomeFragment.callAdapter(arrayList,context);
                }
            } catch (JSONException e)
            {
                e.printStackTrace();
            }*/

            Log.e("Response", "" + response);
        }



        private String readStream(InputStream in) {
            BufferedReader reader = null;
            StringBuffer response = new StringBuffer();
            try {
                reader = new BufferedReader(new InputStreamReader(in));
                String line = "";
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return response.toString();
        }


  }


}



