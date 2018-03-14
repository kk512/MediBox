package in.co.medibox.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;

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
import in.co.medibox.notification.Config;
import in.co.medibox.service.Service_Handler;
import in.co.medibox.utils.Utils;

import static com.google.android.gms.wearable.DataMap.TAG;

public class Activity_Register_User extends Activity implements OnClickListener {
    private static int MOB_FLAG = 0, EMAIL_FLAG = 0, SHOPREGNO_FLAG = 0;
    private EditText mFirstName, mLastName, mDob, mMob, mEmail, mPassword, mConfPass;
    private Calendar myCalendar;
    private Button mRegister;
    private RadioButton mMale, mFemale;
    private SharedPreferences mMediPref;
    public static String regId;
    String PROJECT_NUMBER = "1043504119019";

    private LocationManager mLocationManager;
    private LocationListener mLocationListener;
    private Location mLocation;

    private String mLatitude, mLongitude;
    private String mProvider = LocationManager.GPS_PROVIDER;
    // GPSTracker class
    private GPSTracker gps;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);
        getActionBar().hide();

        // Initializing variable
        myCalendar = Calendar.getInstance();
        gps = new GPSTracker(getApplicationContext());

       // FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);

        /*GCMRegistrar.checkDevice(this);
        GCMRegistrar.checkManifest(this);
        GCMRegistrar.register(Activity_Register_User.this,
                GCMIntentService.SENDER_ID);
        // binding variable to controls
*/
        mFirstName = (EditText) findViewById(R.id.edtFirstName_Reg_User);
        mLastName = (EditText) findViewById(R.id.edtLastName_Reg_User);
        mDob = (EditText) findViewById(R.id.edtDOB_Reg_User);
        mMob = (EditText) findViewById(R.id.edtMobileNum_Reg_User);
        mEmail = (EditText) findViewById(R.id.edtEmail_Reg_User);
        mPassword = (EditText) findViewById(R.id.edtPassword_Reg_User);
        mRegister = (Button) findViewById(R.id.btnRegister_Reg_User);
        mMale = (RadioButton) findViewById(R.id.rbtnMale_Reg_User);
        mFemale = (RadioButton) findViewById(R.id.rbtnFemale_Reg_User);
        mConfPass = (EditText) findViewById(R.id.edtConfPassword_Reg_User);

        //adding on click listiner
        mDob.setOnClickListener(this);
        mRegister.setOnClickListener(this);
        mMediPref = this.getSharedPreferences("MEDIAPP", MODE_PRIVATE);

        mMob.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub
                if (!hasFocus) {
                    new ProgressTask_CheckMobile().execute();
                }
            }
        });

        mEmail.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub
                if (!hasFocus) {
                    new ProgressTask_CheckEmail().execute();
                }
            }
        });


        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);
                    displayFirebaseRegId();

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received
                    String message = intent.getStringExtra("message");
                    Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();
                }
            }
        };

        displayFirebaseRegId();
    }

    // Fetches reg id from shared preferences
    // and displays on the screen
    private void displayFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        regId = pref.getString("regId", null);
        Log.e(TAG, "REG ID: " + regId);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        int id = v.getId();
        switch (id) {
            case R.id.edtDOB_Reg_User:
                // Date dialog creation code
                DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        // TODO Auto-generated method stub
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        updateLabel();
                    }
                };
                // Calling Date Dialog
                new DatePickerDialog(Activity_Register_User.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();

                break;

            case R.id.btnRegister_Reg_User:
                if (mFirstName.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(), "Enter first name.", Toast.LENGTH_SHORT).show();
                    mFirstName.requestFocus();
                    return;
                }

                if (mLastName.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(), "Enter last name.", Toast.LENGTH_SHORT).show();
                    mLastName.requestFocus();
                    return;
                }

                if (mDob.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(), "Enter birth date.", Toast.LENGTH_SHORT).show();
                    mDob.requestFocus();
                    return;
                }
                if (mMob.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(), "Enter mobile number.", Toast.LENGTH_SHORT).show();
                    mMob.requestFocus();
                    return;
                }
                if (mEmail.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(), "Enter email id.", Toast.LENGTH_SHORT).show();
                    mEmail.requestFocus();
                    return;
                }
                if (mPassword.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(), "Enter password.", Toast.LENGTH_SHORT).show();
                    mPassword.requestFocus();
                    return;
                }
                if (mConfPass.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(), "Enter confirm password.", Toast.LENGTH_SHORT).show();
                    mConfPass.requestFocus();
                    return;
                }

                if (mMob.getText().toString().length() != 10) {
                    Toast.makeText(getApplicationContext(), "Invalid mobile number.", Toast.LENGTH_SHORT).show();
                    mMob.requestFocus();
                    return;
                }

                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(mEmail.getText().toString()).matches()) {
                    Toast.makeText(getApplicationContext(), "Invalid email id.", Toast.LENGTH_SHORT).show();
                    mEmail.requestFocus();
                    return;
                }

                if (mPassword.getText().toString().length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password must be atleast 6 letters.", Toast.LENGTH_SHORT).show();
                    mPassword.requestFocus();
                    return;
                }

                if (!mPassword.getText().toString().equalsIgnoreCase(mConfPass
                        .getText().toString())) {
                    Toast.makeText(getApplicationContext(), "Confirm password not matched.", Toast.LENGTH_SHORT).show();
                    mConfPass.requestFocus();
                    return;
                }

                if (MOB_FLAG == 1) {
                    Toast.makeText(getApplicationContext(), "Mobile number already registered.", Toast.LENGTH_SHORT).show();
                    mMob.requestFocus();
                    return;
                }

                if (EMAIL_FLAG == 1) {
                    Toast.makeText(getApplicationContext(), "Email Id already registered.", Toast.LENGTH_SHORT).show();
                    mEmail.requestFocus();
                    return;
                }

                if (!Utils.checkInternet(this)) {
                    Toast.makeText(this, "No Internet Connection.", Toast.LENGTH_LONG).show();
                } else {
                    new ProgressTask_RegisterUser().execute();
                }
                break;

            default:
                break;
        }

    }

    // Method to upadate EditText
    private void updateLabel() {
        String myFormat = "dd MMM, yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        mDob.setText(sdf.format(myCalendar.getTime()));
    }

    // Class to Register User
    private class ProgressTask_RegisterUser extends AsyncTask<Void, Void, Void> {
        String Result = null;
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(Activity_Register_User.this);
            pDialog.setMessage("Registering you..");
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

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            Service_Handler sh = new Service_Handler();
            ArrayList<NameValuePair> registerParam = new ArrayList<NameValuePair>();
            // add all parameter here
            registerParam.add(new BasicNameValuePair("first_name", mFirstName.getText().toString()));
            registerParam.add(new BasicNameValuePair("last_name", mLastName.getText().toString()));

            if (mMale.isChecked()) {
                registerParam.add(new BasicNameValuePair("gender", "M"));
            }

            if (mFemale.isChecked()) {
                registerParam.add(new BasicNameValuePair("gender", "F"));
            }

            registerParam.add(new BasicNameValuePair("birth_date", mDob.getText().toString()));
            registerParam.add(new BasicNameValuePair("mobile_no", mMob.getText().toString()));
            registerParam.add(new BasicNameValuePair("email_id", mEmail.getText().toString()));
            registerParam.add(new BasicNameValuePair("password", mPassword.getText().toString()));
            registerParam.add(new BasicNameValuePair("status", "2"));
            registerParam.add(new BasicNameValuePair("gcm_key", regId));

            registerParam.add(new BasicNameValuePair("lat", mLatitude));
            registerParam.add(new BasicNameValuePair("lng", mLongitude));

            String jsonStr = sh.makeServiceCall(getResources().
                            getString(R.string.baseUrl_webservice) + "user_register.php",
                    Service_Handler.POST, registerParam);

            if (jsonStr != null) {
                try {
                    JSONArray jsonResponse = new JSONArray(jsonStr);
                    for (int i = 0; i < jsonResponse.length(); i++) {
                        JSONObject jsonChildNode = jsonResponse.getJSONObject(0);
                        String fullName = jsonChildNode.getString("fullName");
                        String user_no = jsonChildNode.getString("user_no");
                        String status = jsonChildNode.getString("status");
                        Result = jsonChildNode.getString("result");

                        SharedPreferences.Editor editor = mMediPref.edit();
                        editor.putString("fullName", fullName);
                        editor.putString("user_no", user_no);
                        editor.putString("status", status);
                        editor.commit();
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
                    Intent ins = new Intent(getApplicationContext(), Activity_Drawer_User.class);
                    startActivity(ins);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Something went wrong.", Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

    // Class to check Mobile Number
    private class ProgressTask_CheckMobile extends AsyncTask<Void, Void, Void> {
        String Result = null;
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(Activity_Register_User.this);
            pDialog.setMessage("Checking mobile number..");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            Service_Handler sh = new Service_Handler();
            ArrayList<NameValuePair> checkMobParam = new ArrayList<NameValuePair>();
            // add all parameter here
            checkMobParam.add(new BasicNameValuePair("mobile_no", mMob.getText().toString()));
            String m = mMob.getText().toString();
            String URL = getResources().getString(R.string.baseUrl_webservice) + "unique_mobile_no.php";
            String jsonStr = sh.makeServiceCall(getResources().
                            getString(R.string.baseUrl_webservice) + "unique_mobno.php",
                    Service_Handler.POST, checkMobParam);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    Result = jsonObj.getString("result");
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
                    Toast.makeText(getApplicationContext(), "Mobile number already registered.", Toast.LENGTH_SHORT).show();
                    MOB_FLAG = 1;
                } else {
                    MOB_FLAG = 0;
                }
            }
        }

    }

    // Class to check Email
    private class ProgressTask_CheckEmail extends AsyncTask<Void, Void, Void> {
        String Result = null;
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(Activity_Register_User.this);
            pDialog.setMessage("Checking email id..");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            Service_Handler sh = new Service_Handler();
            ArrayList<NameValuePair> checkMobParam = new ArrayList<NameValuePair>();
            // add all parameter here
            checkMobParam.add(new BasicNameValuePair("email_id", mEmail.getText().toString()));
            String str = mEmail.getText().toString();
            String jsonStr = sh.makeServiceCall(getResources().
                            getString(R.string.baseUrl_webservice) + "unique_email.php",
                    Service_Handler.POST, checkMobParam);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    Result = jsonObj.getString("result");
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
                    Toast.makeText(getApplicationContext(), "Email Id already registered.", Toast.LENGTH_SHORT).show();
                    EMAIL_FLAG = 1;
                } else {
                    EMAIL_FLAG = 0;
                }
            }
        }

    }
}


