package in.co.medibox.fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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

import java.text.DecimalFormat;
import java.util.ArrayList;

import in.co.medibox.R;
import in.co.medibox.activity.Activity_User_MedicalShopDetails;
import in.co.medibox.gps.GPSTracker;
import in.co.medibox.model.Pojo_User_PlaceOrder;
import in.co.medibox.service.Service_Handler;
import in.co.medibox.utils.ImageView_Rounded;


public class Fragment_User_PlaceOrder extends Fragment {
    private ListView mShop_ListView;
    private SharedPreferences mMediPref;
    private ArrayList<Pojo_User_PlaceOrder> mShopList;
    private LocationManager mLocationManager;
    private LocationListener mLocationListener;
    private Location mLocation;
    private String mLatitude, mLongitude;
    private String mProvider = LocationManager.GPS_PROVIDER;
    private DisplayImageOptions options;
    private TextView mRecNotFound;
    // GPSTracker class
    private GPSTracker gps;

    public Fragment_User_PlaceOrder() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_placeorder, container, false);
        //Initializaing Image Loader
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(getActivity()));
        // Initializing ImageLoading Options
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.loadingicon)
                .showImageForEmptyUri(R.drawable.shopimg)
                .showImageOnFail(R.drawable.shopimg).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        mShop_ListView = (ListView) rootView.findViewById(R.id.list_placeorder_user);
        mRecNotFound = (TextView) rootView.findViewById(R.id.txtNoRecFound);
        mShopList = new ArrayList<Pojo_User_PlaceOrder>();

        // Initializing SHared Preferences
        mMediPref = getActivity().getSharedPreferences("MEDIAPP", Context.MODE_PRIVATE);

        gps = new GPSTracker(getActivity());
        // check if GPS enabled
        if (gps.canGetLocation()) {
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            //Toast.makeText(getActivity().getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }

        try {
            mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            mLocationListener = new LocationListener() {
                @Override
                public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
                    // TODO Auto-generated method stub
                }

                @Override
                public void onProviderEnabled(String arg0) {
                    // TODO Auto-generated method stub
                }

                @Override
                public void onProviderDisabled(String arg0) {
                    // TODO Auto-generated method stub
                }

                @Override
                public void onLocationChanged(Location Loc) {
                    // TODO Auto-generated method stub
                    mLongitude = Double.toString(Loc.getLatitude());
                    mLatitude = Double.toString(Loc.getLongitude());
                }
            };

            if (ActivityCompat.checkSelfPermission(getActivity(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getActivity(),
                            android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
                                android.Manifest.permission.ACCESS_FINE_LOCATION},
                        1);
            } else {
                Log.e("DB", "PERMISSION GRANTED");
            }

            mLocationManager.requestLocationUpdates(mProvider, 0, 0, mLocationListener);
            mLocation = mLocationManager.getLastKnownLocation(mProvider);
            mLocation = mLocationManager.getLastKnownLocation(mProvider);

            mLocationManager.requestLocationUpdates(mProvider, 100, 1, mLocationListener);
            mLocation = mLocationManager.getLastKnownLocation(mProvider);
            if (mLocation != null) {
                mLatitude = Double.toString(mLocation.getLatitude());
                mLongitude = Double.toString(mLocation.getLongitude());
                new ProgressTask_Fetch_ShopList().execute();
                mLocationManager.removeUpdates(mLocationListener);

            } else {
                Toast.makeText(getActivity(),
                        "Problem in getting your location.", Toast.LENGTH_SHORT).show();
                mLocation = mLocationManager.getLastKnownLocation(mProvider);
            }
        } catch (Exception e) {
            // TODO: handle exception
        }

        new ProgressTask_Fetch_ShopList().execute();
        return rootView;
    }

    // class to fetch search shop list
    private class ProgressTask_Fetch_ShopList extends AsyncTask<String, Void, String> {
        String Result = null;
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Please wait..");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... args) {
            // Creating service handler class instance
            Service_Handler sh = new Service_Handler();

            ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();
            // add all parameter here
            param.add(new BasicNameValuePair("user_no", mMediPref.
                    getString("user_no", "").toString()));
            param.add(new BasicNameValuePair("center_lat", mLatitude));
            param.add(new BasicNameValuePair("center_lng", mLongitude));

            String jsonStr = sh.makeServiceCall(getResources().
                            getString(R.string.baseUrl_webservice) + "search_stores.php",
                    Service_Handler.POST, param);

            if (jsonStr != null) {
                try {
                    JSONArray jsonResponse = new JSONArray(jsonStr);
                    mShopList.clear();
                    for (int i = 0; i < jsonResponse.length(); i++) {
                        JSONObject jsonChildNode = jsonResponse.getJSONObject(i);
                        String UserNo = jsonChildNode.getString("user_no");
                        String ShopName = jsonChildNode.getString("shop_name");
                        String Distance = jsonChildNode.getString("distance");
                        String ShopLogo = jsonChildNode.getString("shop_logo");
                        Result = "1";

                        Pojo_User_PlaceOrder pojo = new Pojo_User_PlaceOrder();
                        pojo.setUserNo(UserNo);
                        pojo.setShopName(ShopName);
                        pojo.setDistance(Distance);
                        pojo.setShopLogo(ShopLogo);
                        mShopList.add(pojo);
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
        protected void onPostExecute(final String success) {
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();

            if (Result != null) {
                mShop_ListView.setVisibility(View.VISIBLE);
                ShopListAdapter adapter = new ShopListAdapter(getActivity(), mShopList);
                mShop_ListView.setAdapter(adapter);
                mLocationManager.removeUpdates(mLocationListener);
            } else {
                mShop_ListView.setVisibility(View.GONE);
                mRecNotFound.setVisibility(View.VISIBLE);
                mLocationManager.removeUpdates(mLocationListener);
            }
        }
    }

    public class ShopListAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private ArrayList<Pojo_User_PlaceOrder> mShopArrayList;
        public Context mContext;
        int Toggle = 0;

        public ShopListAdapter(Context context, ArrayList<Pojo_User_PlaceOrder> groupArrayList) {
            mInflater = LayoutInflater.from(context);
            mShopArrayList = groupArrayList;
            mContext = context;
        }

        @Override
        public int getCount() {
            return mShopArrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return mShopArrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view;
            final ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.list_row_user_placeorder, parent, false);
                holder = new ViewHolder();

                holder.shopname = (TextView) convertView.
                        findViewById(R.id.txtShopname_User_PlaceOrder);
                holder.distance = (TextView) convertView.
                        findViewById(R.id.txtDistance_User_PlaceOrder);

                holder.viewdetails = (TextView) convertView.
                        findViewById(R.id.txtViewDetails_User_PlaceOrder);
                holder.shop_logo = (ImageView_Rounded) convertView.
                        findViewById(R.id.imgShopLogo_User_PlaceOrder);

                holder.mMainLayout = (LinearLayout) convertView.
                        findViewById(R.id.linlay_Main_User_placeOrders);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.shopname.setText(mShopArrayList.get(position).getShopName());
            Double d = Double.parseDouble(mShopArrayList.get(position).getDistance()) * 1.60934;
            DecimalFormat twoDForm = new DecimalFormat("#.###");
            holder.distance.setText("Distance: " + Double.valueOf(twoDForm.format(d)) + " km");

            holder.viewdetails.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    Intent in = new Intent(getActivity(), Activity_User_MedicalShopDetails.class);
                    in.putExtra("ShopNo", mShopArrayList.get(position).getUserNo());
                    startActivity(in);
                }
            });


            String path = getResources().getString(R.string.baseUrl_webservice) + "upload_image/" +
                    mShopList.get(position).getShopLogo();

            ImageLoader.getInstance().displayImage(path, holder.shop_logo,
                    options, null);
            return convertView;
        }

        private class ViewHolder {
            public TextView shopname, distance, viewdetails;
            public ImageView_Rounded shop_logo;
            private LinearLayout mMainLayout;
        }
    }
}
