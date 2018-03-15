package in.co.medibox.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;

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
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import in.co.medibox.R;
import in.co.medibox.gps.GPSTracker;
import in.co.medibox.notification.Config;
import in.co.medibox.service.Service_Handler;

import static com.google.android.gms.wearable.DataMap.TAG;

public class Activity_Register_MedicalShop extends Activity implements OnClickListener {
    private static String CITY = null, STATE = null, COUNTRY = null;
    private static String url = "http://maps.googleapis.com/maps/api/geocode/json?address=";
    private int FlagControl = 0, FlagImage = 0;
    private Bitmap mPhoto_captured1 = null, mPhoto_captured2 = null, mPhoto_captured3 = null;
    private static final int CAMERA_CODE = 101, GALLERY_CODE = 102;
    static final String EXTRA_MESSAGE = "message";
    public static String regId;
    private String PROJECT_NUMBER = "1043504119019";
    private TextView lblMessage;
    private EditText edtMsg;
    private Button btnMsg;

    private LocationManager mLocationManager;
    private LocationListener mLocationListener;
    private Location mLocation;
    private String mLatitude, mLongitude;
    private String mProvider = LocationManager.GPS_PROVIDER;
    private static int MOB_FLAG = 0, EMAIL_FLAG = 0, SHOPREGNO_FLAG = 0;

    private EditText mDob;
    private Calendar myCalendar;
    private Button mNext, mBack, mRegister;
    private LinearLayout mFirstForm, mSecondForm;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private AlertDialog abuilder;
    private RadioButton mMale, mFemale, mDeliveryYes, mDeliveryNo;
    private EditText mFirstName,
            mLastName,
            mMobNum,
            mMobNum1,
            mAddress,
            mEmailId,
            mPassword,
            mConfPassword,
            mShopName,
            mShopAddress,
            mShopRegistrationNo,
            mShopLandMark,
            mCity,
            mState,
            mZipCode,
            mPrize;

    private ImageView mShopDoc1, mShopDoc2, mShopDoc3;
    private SharedPreferences mMediPref;
    private GPSTracker gps;
    private int shopRangeValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_medicalshop);
        getActionBar().hide();

        // Initializing variable
        myCalendar = Calendar.getInstance();
        gps = new GPSTracker(getApplicationContext());

        mNext = (Button) findViewById(R.id.btnNext_Reg_MediShop1);
        mBack = (Button) findViewById(R.id.btnBack_Reg_MediShop2);

        mFirstForm = (LinearLayout) findViewById(R.id.layoutFirstForm);
        mSecondForm = (LinearLayout) findViewById(R.id.layoutSecondForm);

        mFirstName = (EditText) findViewById(R.id.edtFirstName_Reg_MediShop1);
        mLastName = (EditText) findViewById(R.id.edtLastName_Reg_MediShop1);
        mDob = (EditText) findViewById(R.id.edtDOB_Reg_MediShop1);
        mMobNum = (EditText) findViewById(R.id.edtMobileNum_Reg_MediShop1);
        mMobNum1 = (EditText) findViewById(R.id.edtMobileNum_Reg_MediShop2);
        mAddress = (EditText) findViewById(R.id.edtAddress_Reg_MediShop1);
        mEmailId = (EditText) findViewById(R.id.edtEmail_Reg_MediShop1);
        mPassword = (EditText) findViewById(R.id.edtPassword_Reg_MediShop1);
        mConfPassword = (EditText) findViewById(R.id.edtConfPassword_Reg_MediShop1);
        mMale = (RadioButton) findViewById(R.id.rbtnMale_Reg_MediShop1);
        mFemale = (RadioButton) findViewById(R.id.rbtnFemale_Reg_MediShop1);

        mRegister = (Button) findViewById(R.id.btnRegister_Reg_MediShop2);
        mShopName = (EditText) findViewById(R.id.edtShopName_Reg_MediShop2);
        mShopRegistrationNo = (EditText) findViewById(R.id.edtShopRegNo_Reg_MediShop2);
        mShopAddress = (EditText) findViewById(R.id.edtShopAdd_Reg_MediShop2);
        mShopLandMark = (EditText) findViewById(R.id.edtShopLandmark_Reg_MediShop2);
        mCity = (EditText) findViewById(R.id.edtShopAddCity_Reg_MediShop2);
        mState = (EditText) findViewById(R.id.edtShopAddState_Reg_MediShop2);
        mZipCode = (EditText) findViewById(R.id.edtShopAddZipcode_Reg_MediShop2);
        mPrize = (EditText) findViewById(R.id.edtShopPrize_Reg_MediShop2);
        mDeliveryYes = (RadioButton) findViewById(R.id.delivery_yes_btn);
        mDeliveryNo = (RadioButton) findViewById(R.id.delivery_no_btn);

        mShopDoc1 = (ImageView) findViewById(R.id.imgShopDoc1_Reg_MediShop2);
        mShopDoc2 = (ImageView) findViewById(R.id.imgShopDoc2_Reg_MediShop2);
        mShopDoc3 = (ImageView) findViewById(R.id.imgShopDoc3_Reg_MediShop2);

        mDob.setOnClickListener(this);
        mNext.setOnClickListener(this);
        mBack.setOnClickListener(this);
        mRegister.setOnClickListener(this);

        // Initializing SHared Preferences
        mMediPref = this.getSharedPreferences("MEDIAPP", MODE_PRIVATE);
        mMobNum.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub
                if (!hasFocus) {
                    new ProgressTask_CheckMobile().execute();
                }
            }
        });

        mEmailId.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub
                if (!hasFocus) {
                    new ProgressTask_CheckEmail().execute();
                }
            }
        });

        mShopRegistrationNo.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub
                if (!hasFocus) {
                    new ProgressTask_CheckShopRegNo().execute();
                }
            }
        });

        mShopDoc1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                LayoutInflater layoutInflater = LayoutInflater.from(Activity_Register_MedicalShop.this);
                View promptView = layoutInflater.inflate(R.layout.dialogbox_image_select, null);
                final AlertDialog builder = new AlertDialog.Builder(Activity_Register_MedicalShop.this).create();

                ImageView Camera, Gallery;
                FlagControl = 1;
                Camera = (ImageView) promptView.findViewById(R.id.imgDialog_camera);
                Gallery = (ImageView) promptView.findViewById(R.id.imgDialog_gallery);

                Camera.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent, CAMERA_CODE);
                        builder.dismiss();
                    }
                });

                Gallery.setOnClickListener(new OnClickListener() {
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

        mShopDoc2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                LayoutInflater layoutInflater = LayoutInflater.from(Activity_Register_MedicalShop.this);
                View promptView = layoutInflater.inflate(R.layout.dialogbox_image_select, null);
                final AlertDialog builder = new AlertDialog.Builder(Activity_Register_MedicalShop.this).create();

                ImageView Camera, Gallery;
                FlagControl = 2;
                Camera = (ImageView) promptView.findViewById(R.id.imgDialog_camera);
                Gallery = (ImageView) promptView.findViewById(R.id.imgDialog_gallery);
                Camera.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent, CAMERA_CODE);
                        builder.dismiss();

                    }
                });

                Gallery.setOnClickListener(new OnClickListener() {
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


        mShopDoc3.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                LayoutInflater layoutInflater = LayoutInflater.from(Activity_Register_MedicalShop.this);
                View promptView = layoutInflater.inflate(R.layout.dialogbox_image_select, null);
                final AlertDialog builder = new AlertDialog.Builder(Activity_Register_MedicalShop.this).create();

                ImageView Camera, Gallery;
                FlagControl = 3;
                Camera = (ImageView) promptView.findViewById(R.id.imgDialog_camera);
                Gallery = (ImageView) promptView.findViewById(R.id.imgDialog_gallery);

                Camera.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent, CAMERA_CODE);
                        builder.dismiss();

                    }
                });

                Gallery.setOnClickListener(new OnClickListener() {
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
                Toast.makeText(getApplicationContext(),
                        "Please enter city first.", Toast.LENGTH_SHORT).show();
            }
        });

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);
                    displayFirebaseRegId();
                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    String message = intent.getStringExtra("message");
                    Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();
                }
            }
        };

        displayFirebaseRegId();
    }

    private void displayFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        regId = pref.getString("regId", null);
        Log.e(TAG, "REG ID: " + regId);
    }

    public void onInfoClicked(View view) {
        switch (view.getId()) {
            case R.id.info:
                compulsoryFieldMessage();
                break;
            case R.id.info_one:
                compulsoryFieldMessage();
                break;
            case R.id.info_two:
                compulsoryFieldMessage();
                break;
            case R.id.info_three:
                notCompulsoryFieldMessage();
                break;
            case R.id.info_four:
                notCompulsoryFieldMessage();
                break;
            case R.id.info_five:
                notCompulsoryFieldMessage();
                break;
            case R.id.info_six:
                compulsoryFieldMessage();
                break;
            case R.id.info_seven:
                compulsoryFieldMessage();
                break;
            case R.id.info_eight:
                compulsoryFieldMessage();
                break;
        }
    }

    private void compulsoryFieldMessage() {
        Toast toast = Toast.makeText(Activity_Register_MedicalShop.this, "This field is compulsory", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    private void notCompulsoryFieldMessage() {
        Toast toast = Toast.makeText(Activity_Register_MedicalShop.this, "This field is not compulsory field", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        switch (view.getId()) {
            case R.id.delivery_yes_btn:
                if (checked)
                    medicalShopRange();
                break;
            case R.id.delivery_no_btn:
                if (checked)
                    break;
        }
    }

    private void medicalShopRange() {
        LayoutInflater layoutInflater = LayoutInflater.from(Activity_Register_MedicalShop.this);
        View promptView = layoutInflater.inflate(R.layout.dialog_range_order, null);

        abuilder = new AlertDialog.Builder(Activity_Register_MedicalShop.this).create();
        abuilder.setCancelable(false);

        final EditText mRangeValue = (EditText) promptView.findViewById(R.id.edt_Dialog_range_value);
        Button ok = (Button) promptView.findViewById(R.id.ok_btn);
        Button cancel = (Button) promptView.findViewById(R.id.cancel_btn);

        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                abuilder.dismiss();
            }
        });

        ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRangeValue.getText().toString().equalsIgnoreCase("")) {
                    shopRangeValue = Integer.parseInt(mRangeValue.getText().toString().trim());
                    Toast.makeText(Activity_Register_MedicalShop.this, "Please provide range of delivery", Toast.LENGTH_SHORT).show();
                    return;
                }
                abuilder.dismiss();
            }
        });

        abuilder.setView(promptView);
        abuilder.show();
    }


    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        int id = v.getId();
        switch (id) {
            case R.id.edtDOB_Reg_MediShop1:
                DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // TODO Auto-generated method stub
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        updateLabel();
                    }
                };
                // Calling Date Dialog
                new DatePickerDialog(Activity_Register_MedicalShop.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                break;

            case R.id.btnNext_Reg_MediShop1:
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

                if (mMobNum.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(), "Enter mobile number.", Toast.LENGTH_SHORT).show();
                    mMobNum.requestFocus();
                    return;
                }
                if (mEmailId.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(), "Enter email id.", Toast.LENGTH_SHORT).show();
                    mEmailId.requestFocus();
                    return;
                }
                if (mPassword.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(), "Enter password.", Toast.LENGTH_SHORT).show();
                    mPassword.requestFocus();
                    return;
                }
                if (mConfPassword.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(), "Enter confirm password.", Toast.LENGTH_SHORT).show();
                    mConfPassword.requestFocus();
                    return;
                }

                if (mMobNum.getText().toString().length() != 10) {
                    Toast.makeText(getApplicationContext(), "Invalid mobile number.", Toast.LENGTH_SHORT).show();
                    mMobNum.requestFocus();
                    return;
                }

                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(mEmailId.getText().toString()).matches()) {
                    Toast.makeText(getApplicationContext(), "Invalid email id.", Toast.LENGTH_SHORT).show();
                    mEmailId.requestFocus();
                    return;
                }

                if (mPassword.getText().toString().length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password must be atleast 6 letters.", Toast.LENGTH_SHORT).show();
                    mPassword.requestFocus();
                    return;
                }

                if (!mPassword.getText().toString().equalsIgnoreCase(mConfPassword
                        .getText().toString())) {
                    Toast.makeText(getApplicationContext(), "Confirm password not matched.", Toast.LENGTH_SHORT).show();
                    mConfPassword.requestFocus();
                    return;
                }

                if (MOB_FLAG == 1) {
                    Toast.makeText(getApplicationContext(), "Mobile number already registered.", Toast.LENGTH_SHORT).show();
                    mMobNum.requestFocus();
                    return;
                }

                if (EMAIL_FLAG == 1) {
                    Toast.makeText(getApplicationContext(), "Email Id already registered.", Toast.LENGTH_SHORT).show();
                    mEmailId.requestFocus();
                    return;
                }

                Animation slide_show = AnimationUtils.loadAnimation(
                        getApplicationContext(), R.anim.lefttoright);
                slide_show.setFillAfter(false);
                mSecondForm.startAnimation(slide_show);
                mSecondForm.setVisibility(View.VISIBLE);

                Animation slide_hides = AnimationUtils.loadAnimation(
                        getApplicationContext(), R.anim.righttoleft);
                slide_hides.setFillAfter(false);
                mFirstForm.startAnimation(slide_hides);
                mFirstForm.setVisibility(View.GONE);
                break;

            case R.id.btnBack_Reg_MediShop2:
                mPassword.setText("");
                mConfPassword.setText("");
                Animation slide_hide = AnimationUtils.loadAnimation(
                        getApplicationContext(), R.anim.righttoleft);
                slide_hide.setFillAfter(false);
                mSecondForm.startAnimation(slide_hide);

                mSecondForm.setVisibility(View.GONE);

                Animation slide_shows = AnimationUtils.loadAnimation(
                        getApplicationContext(), R.anim.lefttoright);
                slide_shows.setFillAfter(false);
                mFirstForm.startAnimation(slide_shows);
                mFirstForm.setVisibility(View.VISIBLE);
                break;

            case R.id.btnRegister_Reg_MediShop2:
                if (mShopName.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(), "Enter shop name.", Toast.LENGTH_SHORT).show();
                    mShopName.requestFocus();
                    return;
                }

                if (mShopAddress.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(), "Enter shop address.", Toast.LENGTH_SHORT).show();
                    mShopAddress.requestFocus();
                    return;
                }

                if (mShopRegistrationNo.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(), "Enter shop registration number.", Toast.LENGTH_SHORT).show();
                    mShopRegistrationNo.requestFocus();
                    return;
                }

                if (mShopLandMark.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(), "Enter shop landmark details.", Toast.LENGTH_SHORT).show();
                    mShopLandMark.requestFocus();
                    return;
                }

                if (mZipCode.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(), "Enter Zipcode.", Toast.LENGTH_SHORT).show();
                    mZipCode.requestFocus();
                    return;
                }


                if (mZipCode.getText().toString().length() < 6) {
                    Toast.makeText(getApplicationContext(), "Zip Code must be at least 6 digits.", Toast.LENGTH_SHORT).show();
                    mZipCode.requestFocus();
                    return;
                }

                if (SHOPREGNO_FLAG == 1) {
                    Toast.makeText(getApplicationContext(), "Shop regitration number already registered.", Toast.LENGTH_SHORT).show();
                    mShopRegistrationNo.requestFocus();
                    return;
                }


                if (mPhoto_captured1 == null) {
                    Toast.makeText(getApplicationContext(), "Please upload shop document.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!checkInternet(this)) {
                    Toast.makeText(this, "No Internet Connection.", Toast.LENGTH_LONG).show();

                } else {
                    new ProgressTask_RegisterMediShop().execute();
                }
                break;
        }
    }

    private void updateLabel() {
        String myFormat = "dd MMM, yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        mDob.setText(sdf.format(myCalendar.getTime()));
    }

    // Class to Register Medical Shop
    private class ProgressTask_RegisterMediShop extends AsyncTask<Void, Void, Void> {
        String Result = null;
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(Activity_Register_MedicalShop.this);
            pDialog.setMessage("Registering you..");
            pDialog.setCancelable(false);
            pDialog.show();
            // check if GPS enabled
            if (gps.canGetLocation()) {
                mLatitude = Double.toString(gps.getLatitude());
                mLongitude = Double.toString(gps.getLongitude());
                //	Toast.makeText(getActivity(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
            } else {
                gps.showSettingsAlert();
            }
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            Service_Handler sh = new Service_Handler();
            ArrayList<NameValuePair> registerParam = new ArrayList<NameValuePair>();
            Date d = new Date();

            byte[] data1 = null, data2 = null, data3 = null;
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss");
            String currentDateandTime = sdf.format(new Date());
            String picName_send1, picName_send2, picName_send3;
            ByteArrayOutputStream bos1, bos2, bos3;
            String strImg1, strImg2, strImg3;

            if (mPhoto_captured1 != null) {
                bos1 = new ByteArrayOutputStream();
                picName_send1 = currentDateandTime + "_" + "1" + ".jpg";
                mPhoto_captured1.compress(CompressFormat.JPEG, 100, bos1);
                data1 = bos1.toByteArray();
                strImg1 = Base64.encodeToString(data1, Base64.DEFAULT);
                registerParam.add(new BasicNameValuePair("shop_doc1", picName_send1));
                registerParam.add(new BasicNameValuePair("image1", strImg1));
            } else {
                data1 = null;
            }

            if (mPhoto_captured2 != null) {
                bos2 = new ByteArrayOutputStream();
                picName_send2 = currentDateandTime + "_" + "2" + ".jpg";
                mPhoto_captured2.compress(CompressFormat.JPEG, 100, bos2);
                data2 = bos2.toByteArray();
                strImg2 = Base64.encodeToString(data2, Base64.DEFAULT);
                registerParam.add(new BasicNameValuePair("shop_doc2", picName_send2));
                registerParam.add(new BasicNameValuePair("image2", strImg2));
            } else {
                data2 = null;
            }

            if (mPhoto_captured3 != null) {
                bos3 = new ByteArrayOutputStream();
                picName_send3 = currentDateandTime + "_" + "3" + ".jpg";
                mPhoto_captured3.compress(CompressFormat.JPEG, 100, bos3);
                data3 = bos3.toByteArray();
                strImg3 = Base64.encodeToString(data3, Base64.DEFAULT);
                registerParam.add(new BasicNameValuePair("shop_doc3", picName_send3));
                registerParam.add(new BasicNameValuePair("image3", strImg3));
            } else {
                data3 = null;
            }

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
            registerParam.add(new BasicNameValuePair("mobile_no", mMobNum.getText().toString()));
            registerParam.add(new BasicNameValuePair("mobile_no_one", mMobNum1.getText().toString()));
            registerParam.add(new BasicNameValuePair("owner_address", mAddress.getText().toString()));
            registerParam.add(new BasicNameValuePair("email_id", mEmailId.getText().toString()));
            registerParam.add(new BasicNameValuePair("password", mPassword.getText().toString()));
            registerParam.add(new BasicNameValuePair("shop_name", mShopName.getText().toString()));
            registerParam.add(new BasicNameValuePair("shop_address", mShopAddress.getText().toString()));
            registerParam.add(new BasicNameValuePair("shop_regNo", mShopRegistrationNo.getText().toString()));
            registerParam.add(new BasicNameValuePair("shop_landmark", mShopLandMark.getText().toString()));
            registerParam.add(new BasicNameValuePair("status", "1"));
            registerParam.add(new BasicNameValuePair("gcm_key", regId));
            registerParam.add(new BasicNameValuePair("shop_state", mState.getText().toString()));
            registerParam.add(new BasicNameValuePair("shop_city", mCity.getText().toString()));
            registerParam.add(new BasicNameValuePair("shop_zipcode", mZipCode.getText().toString()));
            registerParam.add(new BasicNameValuePair("shop_prize", mPrize.getText().toString()));
            registerParam.add(new BasicNameValuePair("shop_range", String.valueOf(shopRangeValue)));

            registerParam.add(new BasicNameValuePair("lat", mLatitude));
            registerParam.add(new BasicNameValuePair("lng", mLongitude));

            String jsonStr = sh.makeServiceCall(getResources().
                            getString(R.string.baseUrl_webservice) + "shop_register.php",
                    Service_Handler.POST, registerParam);

            if (jsonStr != null) {
                try {
                    JSONArray jsonResponse = new JSONArray(jsonStr);
                    for (int i = 0; i < jsonResponse.length(); i++) {
                        JSONObject jsonChildNode = jsonResponse.getJSONObject(0);
                        String fullName = jsonChildNode.getString("fullName");
                        String shopName = jsonChildNode.getString("shopName");
                        String user_no = jsonChildNode.getString("user_no");
                        String status = jsonChildNode.getString("status");
                        Result = jsonChildNode.getString("result");

                        SharedPreferences.Editor editor = mMediPref.edit();
                        editor.putString("fullName", fullName);
                        editor.putString("name", shopName);
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
                    Intent ins = new Intent(getApplicationContext(), Activity_Drawer_MedicalStore.class);
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
            pDialog = new ProgressDialog(Activity_Register_MedicalShop.this);
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
            checkMobParam.add(new BasicNameValuePair("mobile_no", mMobNum.getText().toString()));
            String m = mMobNum.getText().toString();
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
            pDialog = new ProgressDialog(Activity_Register_MedicalShop.this);
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
            checkMobParam.add(new BasicNameValuePair("email_id", mEmailId.getText().toString()));
            String str = mEmailId.getText().toString();
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

    // Class to check Shop reg no.
    private class ProgressTask_CheckShopRegNo extends AsyncTask<Void, Void, Void> {
        String Result = null;
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(Activity_Register_MedicalShop.this);
            pDialog.setMessage("Checking shop registration..");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            Service_Handler sh = new Service_Handler();
            ArrayList<NameValuePair> checkMobParam = new ArrayList<NameValuePair>();
            // add all parameter here
            checkMobParam.add(new BasicNameValuePair("shop_regNo", mShopRegistrationNo.getText().toString()));
            String jsonStr = sh.makeServiceCall(getResources().
                            getString(R.string.baseUrl_webservice) + "unique_regno.php",
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
                    Toast.makeText(getApplicationContext(), "Shop registration number already registered.", Toast.LENGTH_SHORT).show();
                    SHOPREGNO_FLAG = 1;
                } else {
                    SHOPREGNO_FLAG = 0;
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_CODE && resultCode == RESULT_OK) {
            if (FlagControl == 1) {
                mPhoto_captured1 = (Bitmap) data.getExtras().get("data");
                mShopDoc1.setImageBitmap(mPhoto_captured1);
                FlagControl = 0;
                mShopDoc2.setVisibility(View.VISIBLE);
            }

            if (FlagControl == 2) {
                mPhoto_captured2 = (Bitmap) data.getExtras().get("data");
                mShopDoc2.setImageBitmap(mPhoto_captured2);
                FlagControl = 0;
                mShopDoc3.setVisibility(View.VISIBLE);
            }

            if (FlagControl == 3) {
                mPhoto_captured3 = (Bitmap) data.getExtras().get("data");
                mShopDoc3.setImageBitmap(mPhoto_captured3);
                FlagControl = 0;
            }
            FlagImage = 1;
        }

        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            try {
                if (FlagControl == 1) {
                    mPhoto_captured1 = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), uri);
                    mShopDoc1.setImageBitmap(mPhoto_captured1);
                    FlagControl = 0;
                    mShopDoc2.setVisibility(View.VISIBLE);
                }

                if (FlagControl == 2) {
                    mPhoto_captured2 = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), uri);
                    mShopDoc2.setImageBitmap(mPhoto_captured2);
                    FlagControl = 0;
                    mShopDoc3.setVisibility(View.VISIBLE);
                }

                if (FlagControl == 3) {
                    mPhoto_captured3 = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), uri);
                    mShopDoc3.setImageBitmap(mPhoto_captured3);
                    FlagControl = 0;
                }
                FlagImage = 1;
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
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


    // Class to get all country list and state
    private class GetContacts extends AsyncTask<Void, Void, Void> {
        JSONArray results = null;
        // Hashmap for ListView
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(Activity_Register_MedicalShop.this);
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
                                /*  JSONObject type = c.getJSONObject("types");*/
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
            //		mCountry.setText(COUNTRY);
            mState.setText(STATE);
            mCity.setText(CITY);
        }
    }
}


