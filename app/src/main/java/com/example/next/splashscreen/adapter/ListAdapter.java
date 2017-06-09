package com.example.next.splashscreen.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.next.splashscreen.Fragment.HomeFragment;
import com.example.next.splashscreen.R;
import com.example.next.splashscreen.controller.UpdateController;
import com.example.next.splashscreen.database.DBHelper;
import com.example.next.splashscreen.interfac.AdapterInterface;
import com.example.next.splashscreen.interfac.ContentInterface;
import com.example.next.splashscreen.model.DataModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by next on 16/2/17.
 */
public class ListAdapter extends BaseAdapter
{
    private EditText mEditDiscrip, mEditTitle;
   private Button mOK;
   private Context mcontext;
   private ArrayList<DataModel> mDatamodelArylst;
   private LayoutInflater inflate;
    private AdapterInterface adpterIntfc;


    public ListAdapter(Context context, ArrayList<DataModel> rowItems) {
        this.mcontext = context;
        this.mDatamodelArylst = rowItems;
        inflate = LayoutInflater.from(mcontext);

    }

    public ListAdapter(Context mContext, ArrayList<DataModel> mData, AdapterInterface adapterInterface) {
        this.mcontext = mContext;
        this.mDatamodelArylst = mData;
        inflate = LayoutInflater.from(mcontext);
        this.adpterIntfc = adapterInterface;
    }

    public void setData(ArrayList<DataModel> list) {
        this.mDatamodelArylst = list;
        notifyDataSetChanged();
    }


    /*private view holder class*/
    private class ViewHolder {
        ImageView imageView;
        TextView txtTitle;
        TextView txtDesc;
        LinearLayout mLinearlayout;

        public ViewHolder(View holder) {
            txtDesc = (TextView) holder.findViewById(R.id.desp);
            txtTitle = (TextView) holder.findViewById(R.id.title);
            imageView = (ImageView) holder.findViewById(R.id.img);
            mLinearlayout = (LinearLayout) holder.findViewById(R.id.linearlayout);
        }
    }

    @Override
    public int getCount() {
        return mDatamodelArylst.size();

    }

    @Override
    public Object getItem(int position) {
        return mDatamodelArylst.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;


        if (convertView == null) {

            convertView = inflate.inflate(R.layout.listviewitems, parent, false);
            holder = new ViewHolder(convertView);

            convertView.setTag(holder);


        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.mLinearlayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {


                AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
                alert.setTitle("Do you want to delete this Task? ");
                alert.setCancelable(false);
                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(mcontext, " deleted Sucessfully", Toast.LENGTH_SHORT).show();
                    }
                });
                alert.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(mcontext, "no changes", Toast.LENGTH_SHORT).show();
                    }
                });
                alert.show();

                return true;
            }
        });


        holder.imageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(mcontext);
                LinearLayout.LayoutParams linearLayout = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 500);
                LayoutInflater inflater = (LayoutInflater) mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.content_popup, null);


                dialog.setContentView(view, linearLayout);
                dialog.setTitle("            TASK   ");
                mEditDiscrip = (EditText) dialog.findViewById(R.id.edittext);
                mEditTitle = (EditText) dialog.findViewById(R.id.edittitle);

                //taking data into the popupbox
                mEditDiscrip.setText(mDatamodelArylst.get(position).getData());
                mEditTitle.setText(mDatamodelArylst.get(position).getTitle());

                mOK = (Button) dialog.findViewById(R.id.okbutton);
                dialog.show();

                mOK.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       // adpterIntfc.onItemclicked(position);
                        if(adpterIntfc != null) {
                            adpterIntfc.onItemclicked(position);
                        }
                        final String text = mEditDiscrip.getText().toString();
                        final String titledata = mEditTitle.getText().toString();
                        Toast.makeText(mcontext, text, Toast.LENGTH_SHORT).show();

                       /* HomeFragment.modelList(new ContentInterface() {
                            @Override
                            public void populateData(ArrayList<DataModel> dataModels) {*/

                                mDatamodelArylst.get(position).setData(text);
                                mDatamodelArylst.get(position).setTitle(titledata);
                                notifyDataSetChanged();

                                if(isOnlile(mcontext)) {
                                    inserDataSQl(mDatamodelArylst.get(position), 0);
                                    JSONObject jsonObject = getJsonObject(mDatamodelArylst.get(position));
                                    UpdateController updateController = new UpdateController(mcontext);
                                    String posturl = mcontext.getResources().getString(R.string.urlPost);
                                    updateController.updateData(jsonObject.toString(), posturl, null, 0);
                                } else {
                                    inserDataSQl(mDatamodelArylst.get(position), 1);
                                }
                          //  }
                        //});

                        dialog.dismiss();
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
                });
            }
        });

        DataModel dataModel = (DataModel) getItem(position);
       /* if (dataModel.getTitle().length() > 15) {
            String string = dataModel.getTitle().substring(0, 20) + "...";
            holder.txtTitle.setText(string);
        } else {
            holder.txtTitle.setText(dataModel.getTitle());
        }*/
        if (dataModel.getData() != null) {
            if (dataModel.getData().length() > 30) {
                String subString = dataModel.getData().substring(0, 30) + "...";
                holder.txtDesc.setText(subString);
            } else {
                holder.txtDesc.setText(dataModel.getData());
            }
        }


        //holder.txtDesc.setText(dataModel.getData());
        holder.txtTitle.setText(dataModel.getTitle());


        return convertView;
    }

    private void inserDataSQl(DataModel dataModel, int sync) {
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

/*        Intent serviceIntent = new Intent(mcontext, UpdateService.class);
        PendingIntent pendingIntent = PendingIntent.getService(mcontext, 1111, serviceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) mcontext.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),10000, pendingIntent);
        UpdateController updateController = new UpdateController(mcontext);
        updateController.updateData(dataModel.getTitle()+" "+dataModel.getData()+" "+dataModel.getUserid(), "url");*/


        array.put(childObject);
        try {
            obj.put("list", array);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //updated data after clicking ok in popup it will store in db
        DBHelper db = new DBHelper(mcontext);
        db.insertData(obj.toString(), sync);
    }

    //Internet Connection method
    public static boolean isOnlile(Context context) {
        ConnectivityManager connection = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connection.getActiveNetworkInfo();

        return (info != null && info.isConnected());


    }
}

