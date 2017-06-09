package com.example.next.splashscreen.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.next.splashscreen.Fragment.HomeFragment;
import com.example.next.splashscreen.R;
import com.example.next.splashscreen.interfac.ContentInterface;
import com.example.next.splashscreen.model.DataModel;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by next on 14/2/17.
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ContentInterface {
    private static final String TAG = "MainACtivity";
    DrawerLayout drawer;
    MainActivity mMainActivity;
    LoginActivity mLoginActivity;
    Context c;
    TextView mName, mEmail;
    ImageView mImageView;
    HomeFragment mHomeFragment;
    GoogleApiClient mGoogleApiClient;
    private Menu menu;
    Toolbar mToolbar;
    EditText mEditText;
    String text;
    Button mOK;
    MenuItem item;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nextfragment);

        mHomeFragment = new HomeFragment(MainActivity.this);


        initView();
        Bundle bundle = getIntent().getExtras();
        String message = bundle.getString("personname");
        String email = bundle.getString("personemail");
        String mUrl = bundle.getString("url");


        Log.i(TAG, "onCreate: " + message);
        mName.setText(message);
        mEmail.setText(email);

        if (!mUrl.equalsIgnoreCase("null")) {
            Log.i(TAG, "onCreate: " + mUrl);
            new ImageDownloader().execute(mUrl);
        }
        JSONObject obj = new JSONObject();
        try {
            obj.put("Name", "crunchify.com");
            obj.put("Author", "App Shah");
            JSONArray company = new JSONArray();
        /*    company.add("Compnay: eBay");
            company.add("Compnay: Paypal");
            company.add("Compnay: Google");*/
            obj.put("Company List", company);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        // try-with-resources statement based on post comment below :)
        try (FileWriter file = new FileWriter("file1.txt")) {
            //file.write(obj.toJSONString());
            System.out.println("Successfully Copied JSON Object to File...");
            System.out.println("\nJSON Object: " + obj);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();


    }

    private void initView() {

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        // toolbar.setLogo(R.drawable.plus_add_green);
        /*ActionBar ab = getSupportActionBar();
        ab.setHomeButtonEnabled(true);
        ab.setDisplayUseLogoEnabled(true);
        ab.setLogo(R.drawable.plusicon);
*/

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle

                (this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View navview = navigationView.inflateHeaderView(R.layout.nav_header_main);
        initNavgView(navview);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void initNavgView(View navview) {
        mName = (TextView) navview.findViewById(R.id.name);
        mEmail = (TextView) navview.findViewById(R.id.email);
        mImageView = (ImageView) navview.findViewById(R.id.imageView);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
       /* MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);*/
        getMenuInflater().inflate(R.menu.main, menu);
        /* menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.plusicon));
        MenuItem menuItem = menu.findItem(R.id.action_settings);
        menuItem.setIcon(getResources().getDrawable(R.drawable.plusicon));*/
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)

    {

        this.item = item;



       /* switch (item.getItemId())
        {
            case R.id.action_settings:
                Toast.makeText(getApplicationContext(), "Item 1 Selected", Toast.LENGTH_LONG).show();
                return true;*/
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        item = menu.findItem(R.id.action_settings);

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            final Dialog dialog = new Dialog(MainActivity.this);
            LinearLayout.LayoutParams linearLayout = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 350);
            LayoutInflater inflater = (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.popup_views, null);
            dialog.setContentView(view, linearLayout);
            dialog.setTitle("Add New Title ");
            mEditText = (EditText) dialog.findViewById(R.id.edittext);

            mOK = (Button) dialog.findViewById(R.id.okbutton);

            dialog.show();

            mOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    text = mEditText.getText().toString();
                    Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
                    mHomeFragment.setTitle(text);
                    dialog.dismiss();

                    // menu.getItem(1).setIcon(getResources().getDrawable(R.drawable.plusicon));
                    /*Toast.makeText(this, "You have chosen the " + getResources().getString(R.string.action_settings) + " to add title",
                            Toast.LENGTH_SHORT).show();
                    */
                    //  default:
                }

            });
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_home) {

            //item.setVisible(true);
            // Handle the camera action
            getFragmentManager().beginTransaction().replace(R.id.frameMain, mHomeFragment).commit();
            mToolbar.setTitle("Home");


        } else if (id == R.id.nav_signout) {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient);
            finish();
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void populateData(ArrayList<DataModel> dataModels) {
        DataModel model = new DataModel();
        model.setTitle(text);

    }

    public class ImageDownloader extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Bitmap doInBackground(String... params) {

            Bitmap bitmap = null;
            try {
                InputStream stream = new java.net.URL(params[0]).openStream();
                bitmap = BitmapFactory.decodeStream(stream);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            mImageView.setImageBitmap(bitmap);
        }
    }
    /*public void showPopup(){
        View menuItemView = findViewById(R.id.action_settings);
        PopupMenu popup = new PopupMenu(MainActivity.this, menuItemView);
        MenuInflater inflate = popup.getMenuInflater();
        inflate.inflate(R.menu.main, popup.getMenu());
        popup.show();

    }
*/

  /*  public void showPopup(View v) {
        LayoutInflater inflater = (LayoutInflater) MainActivity.this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        PopupWindow pw = new PopupWindow(inflater.inflate(
                R.layout.popup_views, null, false), 300, 400, true);
        pw.showAtLocation(findViewById(R.id.container), Gravity.CENTER, 0,
                0);
    }*/

}
