package in.co.medibox.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import in.co.medibox.R;
import in.co.medibox.service.Service_Handler;

public class Activity_Admin_Approved_User extends Activity {
    private TextView userFirstName;
    private TextView userLastName;
    private TextView userMobile;
    private TextView userEmaiId;
    private Button approvedBtn, cancelBtn;
    private SharedPreferences mMediPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_approved_user);

        mMediPref = this.getSharedPreferences("MEDIAPP", Context.MODE_PRIVATE);

        unitUi();
        setListeners();
    }

    @Override
    protected void onStart() {
        super.onStart();
        new ProgressTask_Fetch_Profile().execute();
    }

    private void unitUi() {
        userFirstName = (TextView) findViewById(R.id.user_firstname);
        userLastName = (TextView) findViewById(R.id.user_lastname);
        userMobile = (TextView) findViewById(R.id.user_mobileno);
        userEmaiId = (TextView) findViewById(R.id.user_emailid);
        approvedBtn = (Button) findViewById(R.id.approved_btn);
        cancelBtn = (Button) findViewById(R.id.cancel_btn);
    }

    private void setListeners() {
        approvedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ins = new Intent(getApplicationContext(), Activity_Drawer_MedicalStore.class);
                startActivity(ins);
                Toast.makeText(getApplicationContext(), "Your permission is approved !", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ins = new Intent(getApplicationContext(), Activity_Login.class);
                startActivity(ins);
                finish();
            }
        });
    }

    // class to fetch reservation
    private class ProgressTask_Fetch_Profile extends AsyncTask<String, Void, String> {
        String Result = null;
        private ProgressDialog pDialog;
        private String firstName, lastName, mobileNo, emailID;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(Activity_Admin_Approved_User.this);
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

                        firstName = jsonChildNode.getString("first_name");
                        lastName = jsonChildNode.getString("last_name");
                        mobileNo = jsonChildNode.getString("mobile_no");
                        emailID = jsonChildNode.getString("email_id");
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

            userFirstName.setText(firstName);
            userLastName.setText(lastName);
            userMobile.setText(mobileNo);
            userEmaiId.setText(emailID);
        }
    }
}
