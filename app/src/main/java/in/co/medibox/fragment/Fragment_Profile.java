package in.co.medibox.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import in.co.medibox.R;
import in.co.medibox.gps.GPSTracker;
import in.co.medibox.service.Service_Handler;

public class Fragment_Profile extends Fragment {
    private GPSTracker gps;
    private AlertDialog builder;
    private SharedPreferences mMediPref;
    private EditText mFisrtName,mLastName, mName, mBirthdate, mMobile,mMobileOne, mEmail;
    private LocationManager mLocationManager;
    private LocationListener mLocationListener;
    private Location mLocation;
    private String mLatitude, mLongitude;
    private String mProvider = LocationManager.GPS_PROVIDER;
    private ImageView mLocation_Img;
    private TextView mLocation_txt;
    private Button mSave;
    private EditText mAddress;
    private EditText mCity;
    private EditText mState;
    private static String CITY = null;
    private static String STATE = null;
    private static String COUNTRY = null;
    private static String url = "http://maps.googleapis.com/maps/api/geocode/json?address=";
    private Button mChangePass;
    private EditText mPassTxt;

    public Fragment_Profile() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        // Initializing SHared Preferences
        mMediPref = getActivity().getSharedPreferences("MEDIAPP", Context.MODE_PRIVATE);
        mFisrtName = (EditText) rootView.findViewById(R.id.edtName_fisrtName);
        mLastName = (EditText) rootView.findViewById(R.id.edtName_lastName);
        mName = (EditText) rootView.findViewById(R.id.edtName_Profile);
        mMobile = (EditText) rootView.findViewById(R.id.edtMobile_Profile);
        mMobileOne = (EditText) rootView.findViewById(R.id.edtMobile_one_Profile);
        mEmail = (EditText) rootView.findViewById(R.id.edtEmail_Profile);
        mCity = (EditText) rootView.findViewById(R.id.edtCity_Profile);
        mState = (EditText) rootView.findViewById(R.id.edtState_Profile);
        mAddress = (EditText) rootView.findViewById(R.id.edtAddress_Profile);
        mLocation_Img = (ImageView) rootView.findViewById(R.id.imgLocation_Profile);
        mLocation_txt = (TextView) rootView.findViewById(R.id.txtLocation_Profile);
        mChangePass = (Button) rootView.findViewById(R.id.btnChangePass_Profile);

        mChangePass.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                View promptView = layoutInflater.
                        inflate(R.layout.dialog_changepass, null);

                builder = new AlertDialog.Builder(getActivity()).create();
                //	builder.setTitle(Title);

                builder.setCancelable(false);

                TextView title = (TextView) promptView.findViewById(R.id.txtTitle_dialog_user_placeorder);
                mPassTxt = (EditText) promptView.findViewById(R.id.edtNewPassword_Dialog_Changepass);
                final EditText pass = (EditText) promptView.findViewById(R.id.edtCPass_Dialog_Changepass);

                Button ok = (Button) promptView.findViewById(R.id.btnSave_Dialog_Changepass);
                Button cancel = (Button) promptView.findViewById(R.id.btnCancel_Dialog_Changepass);

                // setting on click listener
                cancel.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        builder.dismiss();
                    }
                });


                ok.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        if (mPassTxt.getText().toString().equalsIgnoreCase("")) {
                            Toast.makeText(getActivity(),
                                    "Enter new password", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (pass.getText().toString().equalsIgnoreCase("")) {
                            Toast.makeText(getActivity(),
                                    "Enter confirm password", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (!pass.getText().toString().equalsIgnoreCase(
                                mPassTxt.getText().toString())) {
                            Toast.makeText(getActivity(),
                                    "Password not matching", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        new ProgressTask_ChangePass().execute();
                        builder.dismiss();
                    }
                });

                builder.setView(promptView);

                builder.show();
            }
        });

        mSave = (Button) rootView.findViewById(R.id.btnSave_Profile);

        mSave.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                if (mName.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(getActivity(),
                            "Enter shop name.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (mMobile.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(getActivity(),
                            "Enter mobile number.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (mMobileOne.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(getActivity(),
                            "Enter mobile number.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (mEmail.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(getActivity(),
                            "Enter Email Id.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (mCity.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(getActivity(),
                            "Enter city.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (mAddress.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(getActivity(),
                            "Enter address.", Toast.LENGTH_SHORT).show();
                    return;
                }

                new ProgressTask_Edit_Profile().execute();

            }
        });

        mLocation_Img.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new ProgressTask_ChangeLoc().execute();
            }
        });

        mLocation_txt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new ProgressTask_ChangeLoc().execute();
            }
        });

        mCity.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub
                if (!hasFocus) {
                    CITY = mCity.getText().toString();
                    new GetContacts().execute();
                }
            }
        });

        mState.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Toast.makeText(getActivity(),
                        "Please enter city first.", Toast.LENGTH_SHORT).show();
            }
        });

        mEmail.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
               /* Toast.makeText(getActivity(),
                        "You cannot change email id.", Toast.LENGTH_SHORT).show();*/
            }
        });

        gps = new GPSTracker(getActivity());
        new ProgressTask_Fetch_Profile().execute();
        return rootView;
    }

    // class to fetch reservation
    private class ProgressTask_Fetch_Profile extends AsyncTask<String, Void, String> {
        String Result = null;
        private ProgressDialog pDialog;
        String first_name, last_name, address, city, state, email, shopNm, mob,mobOne;

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
            param.add(new BasicNameValuePair("user_no", mMediPref.getString("user_no", "").toString()));
            //	param.add(new BasicNameValuePair("order_status","Delivered"));

            String jsonStr = sh.makeServiceCall(getResources().
                            getString(R.string.baseUrl_webservice) + "view_profile.php",
                    Service_Handler.POST, param);

            if (jsonStr != null) {
                try {
                    JSONArray jsonResponse = new JSONArray(jsonStr);
                    for (int i = 0; i < jsonResponse.length(); i++) {
                        JSONObject jsonChildNode = jsonResponse.getJSONObject(i);
                        first_name = jsonChildNode.getString("first_name");
                        last_name = jsonChildNode.getString("last_name");
                        mob = jsonChildNode.getString("mobile_no");
                        mobOne = jsonChildNode.getString("mobile_no_one");
                        email = jsonChildNode.getString("email_id");
                        shopNm = jsonChildNode.getString("shop_name");

                        address = jsonChildNode.getString("shop_address");
                        state = jsonChildNode.getString("shop_state");
                        city = jsonChildNode.getString("shop_city");
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
            mFisrtName.setText(first_name);
            mLastName.setText(last_name);
            mName.setText(shopNm);
            mMobile.setText(mob);
            mMobileOne.setText(mobOne);
            mEmail.setText(email);
            mAddress.setText(address);
            mCity.setText(city);
            mState.setText(state);
        }
    }

    // class to update location
    private class ProgressTask_ChangeLoc extends AsyncTask<String, Void, String> {
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

            // check if GPS enabled
            if (gps.canGetLocation()) {
                mLatitude = Double.toString(gps.getLatitude());
                mLongitude = Double.toString(gps.getLongitude());
                // \n is for new line
                //	Toast.makeText(getActivity(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
            } else {
                // can't get location
                // GPS or Network is not enabled
                // Ask user to enable GPS/network in settings
                gps.showSettingsAlert();
            }
        }

        protected String doInBackground(String... args) {
            // Creating service handler class instance
            Service_Handler sh = new Service_Handler();
            ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();
            // add all parameter here
            param.add(new BasicNameValuePair("user_no", mMediPref.
                    getString("user_no", "").toString()));
            param.add(new BasicNameValuePair("lat", mLatitude));
            param.add(new BasicNameValuePair("lng", mLongitude));
            //	param.add(new BasicNameValuePair("order_status","Delivered"));

            String jsonStr = sh.makeServiceCall(getResources().
                            getString(R.string.baseUrl_webservice) + "change_location.php",
                    Service_Handler.POST, param);

            if (jsonStr != null) {
                try {

                    JSONArray jsonResponse = new JSONArray(jsonStr);
                    for (int i = 0; i < jsonResponse.length(); i++) {
                        JSONObject jsonChildNode = jsonResponse.getJSONObject(i);
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
        protected void onPostExecute(final String success) {
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();

            if (Result != null) {
                if (Result.equalsIgnoreCase("1")) {
                    Toast.makeText(getActivity(),
                            "Location set successfully.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Something went wrong.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    // Class to get all country list and state
    private class GetContacts extends AsyncTask<Void, Void, Void> {
        JSONArray results = null;
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Fetching State.");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            Service_Handler sh = new Service_Handler();
            String url1 = url + CITY;
            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url1, Service_Handler.GET);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    // Getting JSON Array node
                    results = jsonObj.getJSONArray("results");
                    // looping through All Contacts
                    for (int i = 0; i < results.length(); i++) {
                        JSONObject cs = results.getJSONObject(i);
                        JSONArray arr = cs.getJSONArray("address_components");

                        for (int j = 0; j < arr.length(); j++) {
                            JSONObject c = arr.getJSONObject(j);
                            String long_name = c.getString("long_name");
                            String short_name = c.getString("short_name");
                            String types = c.getString("types");

                            if (types.contains("administrative_area_level_1")) {
                                STATE = long_name;
                            }

                            if (types.contains("country")) {
                                COUNTRY = long_name;
                            }

                            if (types.contains("locality")) {
                                if (long_name.toUpperCase().contains(CITY.toUpperCase())) {
                                    CITY = long_name;
                                } else {
                                    CITY = CITY;
                                }
                            }
                        }
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

            mState.setText(STATE);
            mCity.setText(CITY);
        }
    }

    private class ProgressTask_Edit_Profile extends AsyncTask<String, Void, String> {
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
            param.add(new BasicNameValuePair("user_no",
                    mMediPref.getString("user_no", "").toString()));

            param.add(new BasicNameValuePair("status", "1"));
            param.add(new BasicNameValuePair("first_name", mFisrtName.getText().toString()));
            param.add(new BasicNameValuePair("last_name", mLastName.getText().toString()));
            param.add(new BasicNameValuePair("shop_name", mName.getText().toString()));
            param.add(new BasicNameValuePair("shop_address", mAddress.getText().toString()));
            param.add(new BasicNameValuePair("shop_city", mCity.getText().toString()));
            param.add(new BasicNameValuePair("shop_state", mState.getText().toString()));
            param.add(new BasicNameValuePair("mobile_no", mMobile.getText().toString()));
            param.add(new BasicNameValuePair("mobile_no_one", mMobileOne.getText().toString()));
            param.add(new BasicNameValuePair("email_id", mEmail.getText().toString()));

            String jsonStr = sh.makeServiceCall(getResources().
                            getString(R.string.baseUrl_webservice) + "edit_profile.php",
                    Service_Handler.POST, param);

            if (jsonStr != null) {
                try {
                    JSONArray jsonResponse = new JSONArray(jsonStr);
                    for (int i = 0; i < jsonResponse.length(); i++) {
                        JSONObject jsonChildNode = jsonResponse.getJSONObject(i);
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
        protected void onPostExecute(final String success) {
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();

            if (Result != null) {
                if (Result.equalsIgnoreCase("1")) {
                    Toast.makeText(getActivity(),
                            "Your profile updated successfully.", Toast.LENGTH_SHORT).show();
                    new ProgressTask_Fetch_Profile().execute();
                }
            }
        }
    }

    private class ProgressTask_ChangePass extends AsyncTask<String, Void, String> {
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
            param.add(new BasicNameValuePair("user_no", mMediPref.getString("user_no", "").toString()));
            param.add(new BasicNameValuePair("password", mPassTxt.getText().toString()));//approved,cancel

            String jsonStr = sh.makeServiceCall(getResources().
                            getString(R.string.baseUrl_webservice) + "account_setting.php",
                    Service_Handler.POST, param);

            if (jsonStr != null) {
                try {
                    JSONArray jsonResponse = new JSONArray(jsonStr);
                    for (int i = 0; i < jsonResponse.length(); i++) {
                        JSONObject jsonChildNode = jsonResponse.getJSONObject(i);
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
        protected void onPostExecute(final String success) {
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();

            if (Result != null) {
                if (Result.equalsIgnoreCase("1")) {
                    Toast.makeText(getActivity(),
                            "Password changed successfully.", Toast.LENGTH_SHORT).show();
                    new ProgressTask_Fetch_Profile().execute();
                } else {
                    Toast.makeText(getActivity(), "Something went wrong.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
