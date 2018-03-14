package in.co.medibox.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import in.co.medibox.R;
import in.co.medibox.service.Service_Handler;
import in.co.medibox.notification.Config;
import in.co.medibox.utils.Utils;

import static com.google.android.gms.wearable.DataMap.TAG;

public class Activity_Login extends Activity implements OnClickListener {
    private TextView mRegister, mForgotPassword;
    private Button mLogin;
    public static String REGID;
    private EditText mPassword;
    private AutoCompleteTextView mUsername;
    private SharedPreferences mMediPref;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getActionBar().hide();

        // Initializing Share Preferences
        mMediPref = this.getSharedPreferences("MEDIAPP", MODE_PRIVATE);
        if (!mMediPref.getString("fullName", "").equalsIgnoreCase("")) {
            String st = mMediPref.getString("status", "");
            if (st.equalsIgnoreCase("1")) {
                Intent ins = new Intent(getApplicationContext(), Activity_Drawer_MedicalStore.class);
                startActivity(ins);
                finish();
            }
            if (st.equalsIgnoreCase("2")) {
                Intent ins = new Intent(getApplicationContext(), Activity_Drawer_User.class);
                startActivity(ins);
                finish();
            }
        }

        // binding variable to controls
        mRegister = (TextView) findViewById(R.id.txtRegister_Login);
        mForgotPassword = (TextView) findViewById(R.id.txtForgotPassword_Login);
        mUsername = (AutoCompleteTextView) findViewById(R.id.edtEmail_Login);
        mPassword = (EditText) findViewById(R.id.edtPassword_Login);
        mLogin = (Button) findViewById(R.id.btnLoginMail_Login);

        //adding on click listiner
        mRegister.setOnClickListener(this);
        mForgotPassword.setOnClickListener(this);
        mLogin.setOnClickListener(this);

        // Check email pattern
        Account[] accounts = AccountManager.get(this).getAccounts();
        Set<String> emailSet = new HashSet<String>();
        for (Account account : accounts) {
            if (Utils.EMAIL_PATTERN.matcher(account.name).matches()) {
                emailSet.add(account.name);
            }
        }

        mUsername.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, new ArrayList<String>(emailSet)));


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

    private void displayFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        REGID = pref.getString("regId", null);
        Log.e(TAG, "Firebase reg id: " + REGID);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btnLoginMail_Login:
                if (mUsername.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(), R.string.please_enter_email, Toast.LENGTH_SHORT).show();
                    mUsername.requestFocus();
                    return;
                }
                if (mPassword.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(), R.string.please_enter_valid_password, Toast.LENGTH_SHORT).show();
                    mPassword.requestFocus();
                    return;
                }

                if (!Utils.checkInternet(this)) {
                    Toast.makeText(this, R.string.please_connect_internet, Toast.LENGTH_LONG).show();
                } else {
                    new ProgressTask_LoginMail().execute();
                }
                break;

            case R.id.txtRegister_Login:
                LayoutInflater layoutInflater = LayoutInflater.from(Activity_Login.this);
                View promptView = layoutInflater.inflate(R.layout.dialog_medishop_user, null);
                final AlertDialog builder = new AlertDialog.Builder(Activity_Login.this).create();

                ImageView MedicalShop, User;

                MedicalShop = (ImageView) promptView.findViewById(R.id.imgMediShop_Login);
                User = (ImageView) promptView.findViewById(R.id.imgUser_Login);

                MedicalShop.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent in = new Intent(getApplicationContext(), Activity_Register_MedicalShop.class);
                        startActivity(in);
                        builder.dismiss();

                    }
                });

                User.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        Intent in = new Intent(getApplicationContext(), Activity_Register_User.class);
                        startActivity(in);
                        builder.dismiss();
                    }
                });

                builder.setView(promptView);
                builder.show();
                break;

            case R.id.txtForgotPassword_Login:
                Intent in = new Intent(getApplicationContext(), Activity_Forgot_Password.class);
                startActivity(in);
                finish();
                break;
        }

    }

    // Class to login Mail
    private class ProgressTask_LoginMail extends AsyncTask<Void, Void, Void> {
        String Result = null, Status = null, totalorders;
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(Activity_Login.this);
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
            registerParam.add(new BasicNameValuePair("email_id", mUsername.getText().toString()));
            registerParam.add(new BasicNameValuePair("password", mPassword.getText().toString()));
            registerParam.add(new BasicNameValuePair("gcm_key", REGID));

            String jsonStr = sh.makeServiceCall(getResources().
                            getString(R.string.baseUrl_webservice) + "login.php",
                    Service_Handler.POST, registerParam);

            if (jsonStr != null) {
                try {
                    JSONArray jsonResponse = new JSONArray(jsonStr);
                    for (int i = 0; i < jsonResponse.length(); i++) {
                        JSONObject jsonChildNode = jsonResponse.getJSONObject(0);
                        String fullName = jsonChildNode.getString("fullName");
                        String shopName = jsonChildNode.getString("shopName");
                        String user_no = jsonChildNode.getString("user_no");
                        String shopImage = jsonChildNode.getString("shop_image");
                        String profilePicture = jsonChildNode.getString("profile_picture");

                        Status = jsonChildNode.getString("status");
                        Result = jsonChildNode.getString("result");

                        SharedPreferences.Editor editor = mMediPref.edit();
                        editor.putString("fullName", fullName);
                        editor.putString("name", shopName);
                        editor.putString("user_no", user_no);
                        editor.putString("status", Status);
                        editor.putString("shopImage", shopImage);
                        editor.putString("profilePicture", profilePicture);
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

            if (Result != null) {
                if (Result.equalsIgnoreCase("1")) {
                    if (Status.equalsIgnoreCase("1")) {
                        Intent ins = new Intent(getApplicationContext(), Activity_Drawer_MedicalStore.class);
                        startActivity(ins);
                        finish();
                    }
                    if (Status.equalsIgnoreCase("2")) {
                        Intent ins = new Intent(getApplicationContext(), Activity_Drawer_User.class);
                        startActivity(ins);
                        finish();
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "Invalid email id / password.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}


