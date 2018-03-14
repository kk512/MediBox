package in.co.medibox.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import in.co.medibox.R;
import in.co.medibox.adapter.NavDrawerListAdapter;
import in.co.medibox.fragment.Fragment_CancelledOrders;
import in.co.medibox.fragment.Fragment_ConfirmedOrders;
import in.co.medibox.fragment.Fragment_DeliveredOrders;
import in.co.medibox.fragment.Fragment_NewOrders;
import in.co.medibox.fragment.Fragment_ProcessingOrders;
import in.co.medibox.fragment.Fragment_Profile;
import in.co.medibox.fragment.Fragment_SentOrders;
import in.co.medibox.model.NavDrawerItem;
import in.co.medibox.service.Receiver_NetworkChange;
import in.co.medibox.service.Service_Handler;
import in.co.medibox.utils.ImageView_Rounded;

public class Activity_Drawer_MedicalStore extends Activity {
    private Bitmap mPhoto_captured = null;
    private String mUserSequenceNo;
    public DrawerLayout mDrawerLayout;
    private LinearLayout mLinearLayout;
    private ListView mDrawerList;
    public ActionBarDrawerToggle mDrawerToggle;
    public static int mDrawerPosition;
    // nav drawer title
    public CharSequence mDrawerTitle;
    private static final int CAMERA_CODE = 101, GALLERY_CODE = 102;
    // used to store app title
    public CharSequence mTitle;
    // slide menu items
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;
    private ArrayList<NavDrawerItem> navDrawerItems;
    private NavDrawerListAdapter adapter;
    private TextView mName;
    private ImageView_Rounded mProfilePic;
    public static int BusinessTitleSet_FLAG = 0;
    private DisplayImageOptions options;
    private SharedPreferences mMediPref;
    private String mProfilePhoto_Path;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer_medicalstore);

        ActionBar bar_profile = getActionBar();
        if (bar_profile != null) {
            bar_profile.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ff3232")));
        }

        //Initializaing Image Loader
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(Activity_Drawer_MedicalStore.this));

        // Initializing ImageLoading Options
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.loadingicon)
                .showImageForEmptyUri(R.drawable.shopimg)
                .showImageOnFail(R.drawable.shopimg).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        mProfilePic = (ImageView_Rounded) findViewById(R.id.imgProfilePic_Home_User);

        mProfilePic.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater = LayoutInflater.from(Activity_Drawer_MedicalStore.this);
                View promptView = layoutInflater.inflate(R.layout.dialogbox_image_select, null);

                final AlertDialog builder = new AlertDialog.Builder(Activity_Drawer_MedicalStore.this).create();

                ImageView camera, gallery;
                camera = (ImageView) promptView.findViewById(R.id.imgDialog_camera);
                gallery = (ImageView) promptView.findViewById(R.id.imgDialog_gallery);

                camera.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent, CAMERA_CODE);
                        builder.dismiss();
                    }
                });

                gallery.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent,
                                getResources().getString(R.string.pickgallery)), GALLERY_CODE);

                        builder.dismiss();
                    }
                });

                builder.setView(promptView);
                builder.show();
            }
        });

        // Initializing SHared Preferences
        mMediPref = this.getSharedPreferences("MEDIAPP", MODE_PRIVATE);
        mProfilePhoto_Path = getResources().getString(R.string.baseUrl_webservice) + "upload_image/" +
                mMediPref.getString("shopImage", "0");
        ImageLoader.getInstance().displayImage(mProfilePhoto_Path, mProfilePic, options, null);

        // Declaring intent filter for internet connection
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        Receiver_NetworkChange nm = new Receiver_NetworkChange();
        registerReceiver(nm, filter); // Registering receiver for internet connection

        mTitle = mDrawerTitle = getTitle();

        // load slide menu items
        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

        // nav drawer icons from resources
        navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);
        mLinearLayout = (LinearLayout) findViewById(R.id.linearlayoutDrawer);

        mName = (TextView) findViewById(R.id.txtName_Home);
        mName.setText(mMediPref.getString("name", ""));

        navDrawerItems = new ArrayList<NavDrawerItem>();

        // adding nav drawer items to array
        // Home
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
        // Find People
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
        // Photos
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
        // Communities, Will add a counter here
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1)));
        // Pages
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1)));
        // What's hot, We  will add a counter here
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons.getResourceId(5, -1)));

        navDrawerItems.add(new NavDrawerItem(navMenuTitles[6], navMenuIcons.getResourceId(6, -1)));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[7], navMenuIcons.getResourceId(7, -1)));

        // Recycle the typed array
        navMenuIcons.recycle();

        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

        // setting the nav drawer list adapter
        adapter = new NavDrawerListAdapter(getApplicationContext(),
                navDrawerItems);

        mDrawerList.setAdapter(adapter);

        // enabling action bar app icon and behaving it as toggle button
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_menu_white_24dp, //nav menu toggle icon
                R.string.app_name, // nav drawer open - description for accessibility
                R.string.app_name // nav drawer close - description for accessibility
        ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu();
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            displayView(0);
        }
    }

    private class SlideMenuClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            displayView(position);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action bar actions click
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Diplaying fragment view for selected nav drawer list item
     */
    public void displayView(int position) {
        // update the main content by replacing fragments
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new Fragment_NewOrders();
                break;

            case 1:
                fragment = new Fragment_ConfirmedOrders();
                break;

            case 2:
                fragment = new Fragment_ProcessingOrders();
                break;

            case 3:
                fragment = new Fragment_SentOrders();
                break;

            case 4:
                fragment = new Fragment_DeliveredOrders();
                break;

            case 5:
                fragment = new Fragment_CancelledOrders();
                break;

            case 6:
                fragment = new Fragment_Profile();
                break;

            case 7:
                SharedPreferences.Editor editor = mMediPref.edit();
                editor.putString("fullName", "");
                editor.putString("name", "");
                editor.putString("user_no", "");
                editor.putString("status", "");
                editor.putString("shopImage", "");
                editor.putString("profilePicture", "");
                editor.commit();

                Intent in = new Intent(getApplicationContext(), Activity_Login.class);
                startActivity(in);
                finish();
                break;

            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment).commit();

            // update selected item and title, then close the drawer
            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            setTitle(navMenuTitles[position]);
            mDrawerLayout.closeDrawer(mLinearLayout);
        } else {
            // error in creating fragment
            Log.e("MainActivity", "Error in creating fragment");
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_CODE && resultCode == RESULT_OK) {
            mPhoto_captured = (Bitmap) data.getExtras().get("data");
            int h = mPhoto_captured.getHeight();
            int w = mPhoto_captured.getWidth();
            if (h > 150 && w > 100) {
                new ProgressTask_ChangeProfilePic().execute();
            } else {
                Toast.makeText(getApplicationContext(), "Please select image with 400 x 400.", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            try {
                mPhoto_captured = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                int h = mPhoto_captured.getHeight();
                int w = mPhoto_captured.getWidth();

                if (h > 150 && w > 100) {
                    //	new ImageUploadTask_ChangeProPic().execute();
                    new ProgressTask_ChangeProfilePic().execute();
                } else {
                    Toast.makeText(getApplicationContext(), "Please select image with 400 x 400.", Toast.LENGTH_SHORT).show();
                }
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private class ProgressTask_ChangeProfilePic extends AsyncTask<Void, Void, Void> {
        String Result = null, Status = null;
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(Activity_Drawer_MedicalStore.this);
            pDialog.setMessage("Changing profile..");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            Date d = new Date();
            String strImage = null;
            byte[] data = null;

            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            if (mPhoto_captured != null) {
                mPhoto_captured.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                data = bos.toByteArray();
                strImage = Base64.encodeToString(data, Base64.DEFAULT);
            }

            String picName_send = null;
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss");
            String currentDateandTime = sdf.format(new Date());

            picName_send = currentDateandTime + ".jpg".trim();

            Service_Handler sh = new Service_Handler();

            ArrayList<NameValuePair> registerParam = new ArrayList<NameValuePair>();
            // add all parameter here
            registerParam.add(new BasicNameValuePair("user_no", mMediPref.getString("user_no", "")));
            registerParam.add(new BasicNameValuePair("image", strImage));
            registerParam.add(new BasicNameValuePair("shop_image", picName_send));

            String jsonStr = sh.makeServiceCall(getResources().
                            getString(R.string.baseUrl_webservice) + "upload_shop_image.php",
                    Service_Handler.POST, registerParam);

            if (jsonStr != null) {
                try {
                    JSONArray jsonResponse = new JSONArray(jsonStr);
                    for (int i = 0; i < jsonResponse.length(); i++) {
                        JSONObject jsonChildNode = jsonResponse.getJSONObject(0);
                        Result = jsonChildNode.getString("result");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */

            if (Result != null) {
                if (Result.equalsIgnoreCase("1")) {
                    mProfilePic.setImageBitmap(mPhoto_captured);
                    mPhoto_captured = null;
                    Toast.makeText(getApplicationContext(), "Profile changed successfully.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Something went wrong.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to exit ?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //   super.onBackPressed();
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
