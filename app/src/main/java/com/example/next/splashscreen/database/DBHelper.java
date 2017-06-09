package com.example.next.splashscreen.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.ContactsContract;
import android.util.Log;

import com.example.next.splashscreen.model.DataModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by next on 21/2/17.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 9;
    public static final String DATABASE_NAME = "MyDBName.db";
    public static final String TABLE_NAME = "xtable";
    public static final String COLUMN_SNO = "SNO";
    public static final String COLUMN_Title = "Title";
    public static final String COLUMN_Discrip = "Discription";
    public static final String COLUMN_Userid = "UserId";
    public static final String COLUMN_Time = "Time";
    public static final String COLUMN_SYNCHED = "Sync";


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_ENTRIES_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_SNO + " INTEGER PRIMARY KEY , " + COLUMN_Title + " TEXT, "+COLUMN_Discrip+" TEXT, "+ COLUMN_Userid +" TEXT , "
                + COLUMN_Time  + " TEXT, " + COLUMN_SYNCHED + " INTEGER" +")";
        db.execSQL(CREATE_ENTRIES_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " +TABLE_NAME );

        // Create tables again
        onCreate(db);
    }

   //convert to json object
    //put sqlite
    public  void insertData(String data, int sync) {
        SQLiteDatabase db = this.getWritableDatabase();
        JSONObject json = null;
        Log.i("method", "insertData: "+data);
        try {

            json = new JSONObject(data);
            Log.i("Mainactivity", "insertData: "+json.toString());
            JSONArray jsonArray= json.getJSONArray("list");


            for (int i = 0; i <jsonArray.length() ; i++) {
                JSONObject childObject = jsonArray.getJSONObject(i);
                ContentValues contentValues = new ContentValues();
                contentValues.put(COLUMN_SNO, childObject.getInt("sno"));
                contentValues.put(COLUMN_Title,childObject.getString("title"));
                contentValues.put(COLUMN_Discrip,childObject.getString("data"));
                contentValues.put(COLUMN_Userid,childObject.getString("userid"));
                contentValues.put(COLUMN_Time,childObject.getString("time"));
                contentValues.put(COLUMN_SYNCHED, sync);
                String unreadquery="SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_SNO + "=" + contentValues.getAsInteger(COLUMN_SNO);
                Cursor cursor=db.rawQuery(unreadquery, null);
                cursor.moveToFirst();
                if(cursor.getCount()>0)
                {
                    db.update(TABLE_NAME, contentValues, COLUMN_SNO + "=?", new String[] {String.valueOf(contentValues.getAsInteger(COLUMN_SNO))});

                } else {
                    Log.i("database", "insertItems: "+contentValues);
                    db.insert(TABLE_NAME, null, contentValues);
                }

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }







    }


    public List<DataModel> getAllDetails() {
        List<DataModel> listofdata= new ArrayList<DataModel>();
//write a query to select all datas
        String query="select * from " +TABLE_NAME;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {


                DataModel model= new DataModel();
                model.setTitle(cursor.getString(1));
                model.setData(cursor.getString(2));
                model.setUserid(cursor.getString(3));
                model.setSno(cursor.getInt(0));
                model.setTime(cursor.getString(4));

                //Adding contacts to list

                listofdata.add(model);

            }
            while (cursor.moveToNext());
        }
        Log.i("dbhelper", "getAllDetails: "+listofdata.size());
        return listofdata;

    }

  /*  public int  updateListItem(DataModel dataModelcls){

     SQLiteDatabase db =this.getWritableDatabase();
        ContentValues cv= new ContentValues();
        cv.put("Title","title");
        cv.put("Discription","discp");

        return db.update(TABLE_NAME, cv, CONTACTS_COLUMN_SNO + " = ?",
                new String[] { String.valueOf(dataModelcls.getSno()) });

    }
*/
    public void deleteItem(){
        SQLiteDatabase db=this.getWritableDatabase();
        db.delete(TABLE_NAME,null,null);
        db.close();
    }

    public boolean hasNonSyncdata(){
        int a=0;
        SQLiteDatabase db=this.getReadableDatabase();
        String unreadquery="SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_SYNCHED + "=1";
        Cursor cursor=db.rawQuery(unreadquery, null);
        cursor.moveToFirst();
        if(cursor.getCount()>0)
        {
            a=cursor.getCount();
        }
        cursor.close();
        db.close();
        return a > 0;
    }

    public List<DataModel> getOfflineTasks() {
        List<DataModel> dataModels = new ArrayList<DataModel>();
        SQLiteDatabase db=this.getReadableDatabase();
        String unreadquery="SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_SYNCHED + "=1";
        Cursor cursor=db.rawQuery(unreadquery, null);
        cursor.moveToFirst();
        DataModel dataModel = new DataModel();
        if(cursor.moveToFirst()) {
            do {
                dataModel.setSno(cursor.getInt(cursor.getColumnIndex(COLUMN_SNO)));
                dataModel.setData(cursor.getString(cursor.getColumnIndex(COLUMN_Discrip)));
                dataModel.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_Title)));
                dataModel.setTime(cursor.getString(cursor.getColumnIndex(COLUMN_Time)));
                dataModel.setUserid(cursor.getString(cursor.getColumnIndex(COLUMN_Userid)));
                dataModels.add(dataModel);
            } while (cursor.moveToNext());

        }

        cursor.close();
        db.close();
        return dataModels;
    }

    public void updateTask(int taskId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_SYNCHED, 0);
        contentValues.put(COLUMN_SNO, taskId);
        db.update(TABLE_NAME, contentValues, COLUMN_SNO + "=?", new String[] {String.valueOf(taskId)});

        db.close();
    }





}
