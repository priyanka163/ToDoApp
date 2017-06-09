package com.example.next.splashscreen.Fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.next.splashscreen.R;
import com.example.next.splashscreen.adapter.ListAdapter;
import com.example.next.splashscreen.controller.HomeController;
import com.example.next.splashscreen.controller.UpdateController;
import com.example.next.splashscreen.database.DBHelper;
import com.example.next.splashscreen.interfac.AdapterInterface;
import com.example.next.splashscreen.interfac.ContentInterface;
import com.example.next.splashscreen.model.DataModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by next on 14/2/17.
 */
@SuppressLint("ValidFragment")
public class HomeFragment extends Fragment implements AdapterView.OnItemClickListener{
    static ListView mListView;
   ArrayList<DataModel> mData;
    TextView mTitle;
   private Context mContext;
    boolean aBoolean=false;
     private EditText  mEditText;
    private ListAdapter adapter;
    Button mOK;
    String text;

    @SuppressLint("ValidFragment")
    public HomeFragment(Context mContext) {
        this.mContext = mContext;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view=inflater.inflate(R.layout.home,container,false);
        setHasOptionsMenu (true);

        checkInternetConn();
        // Get ListView object from xml
        mListView= (ListView) view.findViewById(R.id.list);
        HomeController controller= new HomeController(getActivity());
        if (aBoolean)
        {
            controller.restcall(new ContentInterface() {
                @Override
                public void populateData(ArrayList<DataModel> dataModels) {
                    mData=dataModels;
                    for (int i = 0; i <mData.size() ; i++) {
                        Log.i("hiiii",mData.get(i).getTitle());


                        Log.i("lisyview", "homefragment: "+mData.size());}
                   callAdapter(mData,getActivity());
                }
            });
        }
        else{
            //offline purpose
            //calling dbhelper class to fetch stored data
            DBHelper database= new DBHelper(getActivity());
            List<DataModel> dataModels= database.getAllDetails();
           ArrayList<DataModel> dataModelArrayList=new ArrayList<DataModel>();
            dataModelArrayList = (ArrayList<DataModel>) dataModels;
            callAdapter(dataModelArrayList, getActivity());
            Log.i("fetchdatabase", "onCreateView: "+dataModelArrayList);
            Log.i("fetchdatabase", "onCreateView: "+dataModels.size());

        }





        return  view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast toast = Toast.makeText(getActivity(),
                "Item " + (position + 1) + ": " + mData.get(position),
                Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
    }


   public void modelList(final ContentInterface contentInterface) {
       contentInterface.populateData(mData);

   }
    public void callAdapter(ArrayList<DataModel> dataModels, Context context){
        adapter = new ListAdapter(context,dataModels, new AdapterInterface() {
            @Override
            public void onItemclicked(int position) {
                if(isOnlile(mContext)) {
                    inserDataSQl(mData.get(position), 0);
                    JSONObject jsonObject = getJsonObject(mData.get(position));

                    UpdateController updateController = new UpdateController(mContext);
                    String posturl = mContext.getResources().getString(R.string.urlPost);
                    updateController.updateData(jsonObject.toString(), posturl, null, 0);

                } else {
                    inserDataSQl(mData.get(position), 1);
                }

            }
        });

    }

    public String readSavedData ( ) {
        StringBuffer datax = new StringBuffer("");
        try {
            FileInputStream fIn =mContext.openFileInput ( "MyFile.json") ;
            InputStreamReader isr = new InputStreamReader ( fIn ) ;
            BufferedReader buffreader = new BufferedReader ( isr ) ;


            String readString = buffreader.readLine ( ) ;
            while ( readString != null ) {
                datax.append(readString);
                readString = buffreader.readLine ( ) ;
            }

            isr.close ( ) ;
        } catch ( IOException ioe ) {
            ioe.printStackTrace ( ) ;
        }
        Log.i("homefrag", "readSavedData: "+datax.toString());
        return datax.toString() ;
    }
    private void checkInternetConn() {
        ConnectivityManager connection = (ConnectivityManager) getActivity().getSystemService(mContext.CONNECTIVITY_SERVICE);
        NetworkInfo info = connection.getActiveNetworkInfo();

        if (info != null && info.isConnected()) {
            aBoolean=true;


        } else {
            aBoolean=false;
            Toast.makeText(getActivity(), "Check the internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem item = menu.findItem(R.id.action_settings);
        item.setVisible(true);


        final Dialog dialog = new Dialog(getActivity());
        LinearLayout.LayoutParams  linearLayout = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,325);
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.popup_views,null);
        dialog.setContentView(view,linearLayout);
        dialog.setTitle("Add  New Title ");
        mEditText= (EditText) dialog.findViewById(R.id.edittext);
        mOK= (Button) dialog.findViewById(R.id.okbutton);

       // dialog.show();

        mOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text = mEditText.getText().toString();
                Toast.makeText(getActivity(), "New Task"+text, Toast.LENGTH_SHORT).show();
                // this line adds the data of your EditText and puts in your array
             //   mData.add(text);
                // next thing you have to do is check if your adapter has changed



                DataModel model= new DataModel();
                model.setSno(mData.size()+1);
                model.setTitle("text");
                model.setTime("1233212222");
                model.setData("");
                model.setUserid("sgpriyanka18@gmail.com");
                mData.add(model);

                //mDatamodelArylst.get(position).setTitle(titledata);
                //adapter.notifyDataSetChanged();
                adapter.setData(mData);






                dialog.dismiss();

                // menu.getItem(1).setIcon(getResources().getDrawable(R.drawable.plusicon));
                    /*Toast.makeText(this, "You have chosen the " + getResources().getString(R.string.action_settings) + " to add title",
                            Toast.LENGTH_SHORT).show();
                    */
                //  default:
            }


        });

    }

    private JSONObject getJsonObject(DataModel dataModel) {
        JSONObject obj = new JSONObject();

        try {
            obj.put("note", dataModel.getData());
            obj.put("time", "1233212222");
            obj.put("userid", "sgpriyanka18@gmail.com");
            obj.put("title", dataModel.getTitle());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }


    public  void setTitle(String title){
        Log.i("addtitlintolist", "getTitle: "+title);
        DataModel model= new DataModel();
        model.setTitle(title);
        //to avoid to take different position so the below line
        model.setData("");
        if(mData == null) {
            mData = new ArrayList<>();
        }
        mData.add(model);
        adapter.setData(mData);

    }

    public void inserDataSQl(DataModel dataModel, int sync) {
        JSONObject obj = new JSONObject();
        JSONArray array = new JSONArray();
        JSONObject childObject = new JSONObject();
        try {
            childObject.put("title", dataModel.getTitle());
            childObject.put("data", dataModel.getData());
            childObject.put("userid", dataModel.getUserid());
            childObject.put("time", dataModel.getTime());
            childObject.put("sno", dataModel.getSno());
            childObject.put("sync", sync);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        array.put(childObject);
        try {
            obj.put("list", array);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //updated data after clicking ok in popup it will store in db
        DBHelper db = new DBHelper(mContext);
        db.insertData(obj.toString(), sync);
    }

    //Internet Connection method
    public static boolean isOnlile(Context context) {
        ConnectivityManager connection = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connection.getActiveNetworkInfo();

        return (info != null && info.isConnected());


    }

}

