package in.co.medibox.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
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
import in.co.medibox.utils.Utils;

public class Activity_Forgot_Password extends Activity {
    private EditText mEmailId;
    private Button mRecover;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);
        getActionBar().hide();

        mEmailId = (EditText) findViewById(R.id.edtEmail_ForgotPass);
        mRecover = (Button) findViewById(R.id.btnRecoverPass_ForgotPass);
        mRecover.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEmailId.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(), R.string.please_enter_email, Toast.LENGTH_SHORT).show();
                    mEmailId.requestFocus();
                    return;
                }

                if (!Utils.checkInternet(getApplicationContext())) {
                    Toast.makeText(getApplicationContext(), R.string.please_connect_internet, Toast.LENGTH_LONG).show();
                } else {
                    new ProgressTask_ForgotPass().execute();
                }
            }
        });

    }

    // Class to Forgot Pass
    private class ProgressTask_ForgotPass extends AsyncTask<Void, Void, Void> {
        String Result = null;
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(Activity_Forgot_Password.this);
            pDialog.setMessage("Authenticating you..");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            Service_Handler sh = new Service_Handler();
            ArrayList<NameValuePair> registerParam = new ArrayList<NameValuePair>();
            // add all parameter here
            registerParam.add(new BasicNameValuePair("email_id", mEmailId.getText().toString()));
            String jsonStr = sh.makeServiceCall(getResources().
                            getString(R.string.baseUrl_webservice) + "forgot_password.php",
                    Service_Handler.POST, registerParam);

            if (jsonStr != null) {
                try {
                    JSONArray jsonResponse = new JSONArray(jsonStr);
                    for (int i = 0; i < jsonResponse.length(); i++) {
                        JSONObject jsonChildNode = jsonResponse.getJSONObject(0);
                        Result = jsonChildNode.getString("status");
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

            if (Result != null) {
                if (Result.equalsIgnoreCase("1")) {
                    Toast.makeText(getApplicationContext(), "Password sent to your registered email id..", Toast.LENGTH_SHORT).show();
                    mEmailId.setText("");
                } else {
                    Toast.makeText(getApplicationContext(), "Sorry! You are not registered.", Toast.LENGTH_SHORT).show();
                }
            }
        }

    }
}


