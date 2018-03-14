package in.co.medibox.fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import in.co.medibox.R;
import in.co.medibox.gps.GPSTracker;
import in.co.medibox.service.Service_Handler;

public class Fragment_User_Profile extends Fragment {
    private AlertDialog builder;
    private Calendar myCalendar;
    private GPSTracker gps;
    private SharedPreferences mMediPref;
    /*private TextView mName,mGender,mBirthdate,mMobile,mEmail,mShopNm;*/
    private EditText mName, mLname, mBirthdate, mMobile, mEmail, mShopNm;
    private LocationManager mLocationManager;
    private LocationListener mLocationListener;
    private Location mLocation;
    private String mLatitude, mLongitude;
    private String mProvider = LocationManager.GPS_PROVIDER;
    private ImageView mLocation_Img;
    private TextView mLocation_txt;
    private Button mSave;
    private RadioGroup mGender;
    private RadioButton mMale, mFemale;
    private Button mChangePass;
    private EditText mPassTxt;

    public Fragment_User_Profile() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_profile, container, false);
        // Initializing SHared Preferences
        mMediPref = getActivity().getSharedPreferences("MEDIAPP", Context.MODE_PRIVATE);
        myCalendar = Calendar.getInstance();

		/*	mName=(TextView) rootView.findViewById(R.id.txtName_Profile);
        mGender=(TextView) rootView.findViewById(R.id.txtGender_Profile);
		mBirthdate=(TextView) rootView.findViewById(R.id.txtDob_Profile);
		mMobile=(TextView) rootView.findViewById(R.id.txtMobile_Profile);
		mEmail=(TextView) rootView.findViewById(R.id.txtEmail_Profile);
		mShopNm=(TextView) rootView.findViewById(R.id.txtShopName_Profile);*/

        mName = (EditText) rootView.findViewById(R.id.edtName_Profile);
        mLname = (EditText) rootView.findViewById(R.id.edtLName_Profile);
        mBirthdate = (EditText) rootView.findViewById(R.id.edtDob_Profile);
        mMobile = (EditText) rootView.findViewById(R.id.edtMobile_Profile);
        mEmail = (EditText) rootView.findViewById(R.id.edtEmail_Profile);
        //	mShopNm=(EditText) rootView.findViewById(R.id.edtShopName_Profile);
        mGender = (RadioGroup) rootView.findViewById(R.id.rbgGender_Profile);
        mMale = (RadioButton) rootView.findViewById(R.id.rbtnMale);
        mFemale = (RadioButton) rootView.findViewById(R.id.rbtnFemale);
        mLocation_Img = (ImageView) rootView.findViewById(R.id.imgLocation_Profile);
        mLocation_txt = (TextView) rootView.findViewById(R.id.txtLocation_Profile);
        mSave = (Button) rootView.findViewById(R.id.btnSave_Profile);
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

        mBirthdate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                // Date dialog creation code
                DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        // TODO Auto-generated method stub
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        updateLabel(mBirthdate);
                    }
                };

                // Calling Date Dialog
                new DatePickerDialog(getActivity(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        mSave.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (mName.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(getActivity(),
                            "Enter first name.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (mLname.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(getActivity(),
                            "Enter last name.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (mMobile.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(getActivity(),
                            "Enter mobile number.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (mBirthdate.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(getActivity(),
                            "Enter birthdate.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!mMale.isChecked() && !mFemale.isChecked()) {
                    Toast.makeText(getActivity(),
                            "Select gender.", Toast.LENGTH_SHORT).show();
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


        mEmail.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Toast.makeText(getActivity(),
                        "You cannot change email id.", Toast.LENGTH_SHORT).show();
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
        String first_name, last_name, gender, dob, mob, email;

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
                        gender = jsonChildNode.getString("gender");
                        dob = jsonChildNode.getString("birth_date");
                        mob = jsonChildNode.getString("mobile_no");
                        email = jsonChildNode.getString("email_id");
                        //	mShopNm.setText(jsonChildNode.getString("shop_name"));
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

            mName.setText(first_name);
            mLname.setText(last_name);

            if (gender.equalsIgnoreCase("M")) {
                mMale.setChecked(true);
                mFemale.setChecked(false);
            } else {
                mMale.setChecked(false);
                mFemale.setChecked(true);
            }

            mBirthdate.setText(dob);
            mMobile.setText(mob);
            mEmail.setText(email);
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


            mLocationManager = (LocationManager) getActivity()
                    .getSystemService(Context.LOCATION_SERVICE);

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
                    ActivityCompat.checkSelfPermission(getContext(),
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
                mLocationManager.removeUpdates(mLocationListener);

            } else {
                Toast.makeText(getActivity(),
                        "Problem in getting your location.", Toast.LENGTH_SHORT).show();
                mLocation = mLocationManager.getLastKnownLocation(mProvider);
            }

            // check if GPS enabled
            if (gps.canGetLocation()) {
                mLatitude = Double.toString(gps.getLatitude());
                mLongitude = Double.toString(gps.getLongitude());
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
            param.add(new BasicNameValuePair("status", "2"));
            param.add(new BasicNameValuePair("first_name", mName.getText().toString()));
            param.add(new BasicNameValuePair("last_name", mLname.getText().toString()));

            if (mMale.isChecked()) {
                param.add(new BasicNameValuePair("gender", "M"));
            } else {
                param.add(new BasicNameValuePair("gender", "F"));
            }

            param.add(new BasicNameValuePair("birth_date", mBirthdate.getText().toString()));
            param.add(new BasicNameValuePair("mobile_no", mMobile.getText().toString()));

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

    // Method to upadate EditText
    private void updateLabel(EditText control) {
        String myFormat = "dd MMM, yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        control.setText(sdf.format(myCalendar.getTime()));
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
