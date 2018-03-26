package in.co.medibox.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import in.co.medibox.R;
import in.co.medibox.service.Service_Handler;

/**
 * Created by kailash on 3/24/2018.
 */

public class Activity_Admin_Panel extends Activity {
    private EditText adminFirstName, adminLastName, adminMobile, adminEmailId;
    private Button adminRegistrar, adminLogin;
    private SharedPreferences mMediPref;
    private static int MOB_FLAG = 0, EMAIL_FLAG = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // Initializing SHared Preferences
        mMediPref = this.getSharedPreferences("MEDIAPP", MODE_PRIVATE);

        initUi();
        listeners();
    }

    private void initUi() {
        adminRegistrar = (Button) findViewById(R.id.admin_reg_btn);
        adminLogin = (Button) findViewById(R.id.admin_login_btn);
        adminFirstName = (EditText) findViewById(R.id.admin_first_name);
        adminLastName = (EditText) findViewById(R.id.admin_last_name);
        adminMobile = (EditText) findViewById(R.id.admin_mobile_no);
        adminEmailId = (EditText) findViewById(R.id.admin_email);
    }

    private void listeners() {

        adminMobile.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub
                if (!hasFocus) {
                    new ProgressTask_CheckMobile().execute();
                }
            }
        });

        adminEmailId.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub
                if (!hasFocus) {
                    new ProgressTask_CheckEmail().execute();
                }
            }
        });

        adminRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adminFirstName.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(), "Enter first name.", Toast.LENGTH_SHORT).show();
                    adminFirstName.requestFocus();
                    return;
                }

                if (adminLastName.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(), "Enter last name.", Toast.LENGTH_SHORT).show();
                    adminLastName.requestFocus();
                    return;
                }


                if (adminMobile.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(), "Enter mobile number.", Toast.LENGTH_SHORT).show();
                    adminMobile.requestFocus();
                    return;
                }
                if (adminEmailId.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(), "Enter email id.", Toast.LENGTH_SHORT).show();
                    adminEmailId.requestFocus();
                    return;
                }


                if (adminMobile.getText().toString().length() != 10) {
                    Toast.makeText(getApplicationContext(), "Invalid mobile number.", Toast.LENGTH_SHORT).show();
                    adminMobile.requestFocus();
                    return;
                }

                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(adminEmailId.getText().toString()).matches()) {
                    Toast.makeText(getApplicationContext(), "Invalid email id.", Toast.LENGTH_SHORT).show();
                    adminEmailId.requestFocus();
                    return;
                }

                if (MOB_FLAG == 1) {
                    Toast.makeText(getApplicationContext(), "Mobile number already registered.", Toast.LENGTH_SHORT).show();
                    adminMobile.requestFocus();
                    return;
                }

                if (EMAIL_FLAG == 1) {
                    Toast.makeText(getApplicationContext(), "Email Id already registered.", Toast.LENGTH_SHORT).show();
                    adminEmailId.requestFocus();
                    return;
                }

                if (!checkInternet(Activity_Admin_Panel.this)) {
                    Toast.makeText(Activity_Admin_Panel.this, "No Internet Connection.", Toast.LENGTH_LONG).show();

                } else {
                    new ProgressTask_Admin().execute();
                }
            }
        });

        adminLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adminFirstName.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(), "Enter first name.", Toast.LENGTH_SHORT).show();
                    adminFirstName.requestFocus();
                    return;
                }

                if (adminLastName.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(), "Enter last name.", Toast.LENGTH_SHORT).show();
                    adminLastName.requestFocus();
                    return;
                }


                if (adminMobile.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(), "Enter mobile number.", Toast.LENGTH_SHORT).show();
                    adminMobile.requestFocus();
                    return;
                }
                if (adminEmailId.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(), "Enter email id.", Toast.LENGTH_SHORT).show();
                    adminEmailId.requestFocus();
                    return;
                }


                if (adminMobile.getText().toString().length() != 10) {
                    Toast.makeText(getApplicationContext(), "Invalid mobile number.", Toast.LENGTH_SHORT).show();
                    adminMobile.requestFocus();
                    return;
                }

                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(adminEmailId.getText().toString()).matches()) {
                    Toast.makeText(getApplicationContext(), "Invalid email id.", Toast.LENGTH_SHORT).show();
                    adminEmailId.requestFocus();
                    return;
                }

                if (MOB_FLAG == 1) {
                    Toast.makeText(getApplicationContext(), "Mobile number already registered.", Toast.LENGTH_SHORT).show();
                    adminMobile.requestFocus();
                    return;
                }

                if (EMAIL_FLAG == 1) {
                    Toast.makeText(getApplicationContext(), "Email Id already registered.", Toast.LENGTH_SHORT).show();
                    adminEmailId.requestFocus();
                    return;
                }

                if (!checkInternet(Activity_Admin_Panel.this)) {
                    Toast.makeText(Activity_Admin_Panel.this, "No Internet Connection.", Toast.LENGTH_LONG).show();

                } else {
                    new ProgressTask_Admin_Login().execute();
                }
            }
        });
    }

    // Class to Admin panel
    private class ProgressTask_Admin_Login extends AsyncTask<Void, Void, Void> {
        String Result = null;
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(Activity_Admin_Panel.this);
            pDialog.setMessage("Registering you..");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            Service_Handler sh = new Service_Handler();
            ArrayList<NameValuePair> registerParam = new ArrayList<NameValuePair>();
            // add all parameter here
            registerParam.add(new BasicNameValuePair("admin_firstname", adminFirstName.getText().toString()));
            registerParam.add(new BasicNameValuePair("admin_lastname", adminLastName.getText().toString()));
            registerParam.add(new BasicNameValuePair("admin_mobileno", adminMobile.getText().toString()));
            registerParam.add(new BasicNameValuePair("admin_emailid", adminEmailId.getText().toString()));

            String jsonStr = sh.makeServiceCall(getResources().
                            getString(R.string.baseUrl_webservice) + "admin_login.php",
                    Service_Handler.POST, registerParam);

            if (jsonStr != null) {
                try {
                    JSONArray jsonResponse = new JSONArray(jsonStr);
                    for (int i = 0; i < jsonResponse.length(); i++) {
                        JSONObject jsonChildNode = jsonResponse.getJSONObject(0);
                        String fullName = jsonChildNode.getString("fullName");
                        String admin_mobilno = jsonChildNode.getString("admin_mobileno");
                        String admin_emailid = jsonChildNode.getString("admin_emailid");
                        String status = jsonChildNode.getString("status");
                        Result = jsonChildNode.getString("result");

                        SharedPreferences.Editor editor = mMediPref.edit();
                        editor.putString("fullName", fullName);
                        editor.putString("admin_mobileno", admin_mobilno);
                        editor.putString("admin_emailid", admin_emailid);
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
                    Intent ins = new Intent(getApplicationContext(), Activity_Admin_Approved_User.class);
                    startActivity(ins);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Something went wrong.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    // Class to Admin panel
    private class ProgressTask_Admin extends AsyncTask<Void, Void, Void> {
        String Result = null;
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(Activity_Admin_Panel.this);
            pDialog.setMessage("Registering you..");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            Service_Handler sh = new Service_Handler();
            ArrayList<NameValuePair> registerParam = new ArrayList<NameValuePair>();
            // add all parameter here
            registerParam.add(new BasicNameValuePair("admin_firstname", adminFirstName.getText().toString()));
            registerParam.add(new BasicNameValuePair("admin_lastname", adminLastName.getText().toString()));
            registerParam.add(new BasicNameValuePair("admin_mobileno", adminMobile.getText().toString()));
            registerParam.add(new BasicNameValuePair("admin_emailid", adminEmailId.getText().toString()));

            String jsonStr = sh.makeServiceCall(getResources().
                            getString(R.string.baseUrl_webservice) + "admin_register.php",
                    Service_Handler.POST, registerParam);

            if (jsonStr != null) {
                try {
                    JSONArray jsonResponse = new JSONArray(jsonStr);
                    for (int i = 0; i < jsonResponse.length(); i++) {
                        JSONObject jsonChildNode = jsonResponse.getJSONObject(0);
                        String fullName = jsonChildNode.getString("fullName");
                        String admin_mobilno = jsonChildNode.getString("admin_mobileno");
                        String admin_emailid = jsonChildNode.getString("admin_emailid");
                        String status = jsonChildNode.getString("status");
                        Result = jsonChildNode.getString("result");

                        SharedPreferences.Editor editor = mMediPref.edit();
                        editor.putString("fullName", fullName);
                        editor.putString("admin_mobileno", admin_mobilno);
                        editor.putString("admin_emailid", admin_emailid);
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
                    Intent ins = new Intent(getApplicationContext(), Activity_Admin_Approved_User.class);
                    startActivity(ins);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Something went wrong.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    // Function to check internet connection
    boolean checkInternet(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        //should check null because in air plan mode it will be null
        return (netInfo != null && netInfo.isConnected());
    }


    // Class to check Mobile Number
    private class ProgressTask_CheckMobile extends AsyncTask<Void, Void, Void> {
        String Result = null;
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(Activity_Admin_Panel.this);
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
            checkMobParam.add(new BasicNameValuePair("mobile_no", adminMobile.getText().toString()));
            String m = adminMobile.getText().toString();
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
            pDialog = new ProgressDialog(Activity_Admin_Panel.this);
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
            checkMobParam.add(new BasicNameValuePair("email_id", adminEmailId.getText().toString()));
            String str = adminEmailId.getText().toString();
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
