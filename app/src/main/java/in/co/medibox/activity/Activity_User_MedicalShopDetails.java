package in.co.medibox.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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
import java.util.Calendar;
import java.util.Date;

import in.co.medibox.R;
import in.co.medibox.service.Service_Handler;
import in.co.medibox.utils.ImageView_Rounded;

public class Activity_User_MedicalShopDetails extends Activity {
    private int FlagControl = 0;
    private int FlagImage = 0;
    private Bitmap mPhoto_captured1 = null,
            mPhoto_captured2 = null,
            mPhoto_captured3 = null;

    private ImageView pres1, pres2, pres3;
    private SharedPreferences mMediPref;
    private static String mShopNo;
    private CheckBox mSameComp, mCOD;
    private static final int CAMERA_CODE = 101, GALLERY_CODE = 102;
    private ImageView_Rounded mShopLogo;
    private TextView mShopName, mAddress, mMobile, mEmail, mBack, mPlaceOrder;

    private String mShopName_GET,
            mAddress_GET,
            mMobile_GET,
            mEmail_GET,
            mUser_No_Get,
            mShop_Logo_Get,
            mUserNo_Send;

    private Spinner mTimeSlot;
    private EditText mAddress_Send;
    private EditText mComment_Send;
    private AlertDialog builder;
    private DisplayImageOptions options;
    private ImageView mCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_medicalshopdeatails);

        ActionBar bar_profile = getActionBar();
        if (bar_profile != null) {
            bar_profile.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ff3232")));
        }

        // Initializing SHared Preferences
        mMediPref = this.getSharedPreferences("MEDIAPP", MODE_PRIVATE);

        Bundle d = getIntent().getExtras();
        if (d != null) {
            mShopNo = d.getString("ShopNo");
        }
        mUserNo_Send = mMediPref.getString("user_no", "");

        //	new ProgressTask_LoginMail().execute();

        mShopLogo = (ImageView_Rounded) findViewById(R.id.imgShopLogo_User_MedicalShopDetails);
        mShopName = (TextView) findViewById(R.id.txtShopName_User_MedicalShopDetails);
        mAddress = (TextView) findViewById(R.id.txtAddress_User_MedicalShopDetails);
        mMobile = (TextView) findViewById(R.id.txtMobile_User_MedicalShopDetails);
        mEmail = (TextView) findViewById(R.id.txtEmail_User_MedicalShopDetails);
        mBack = (TextView) findViewById(R.id.txtBack_User_MedicalShopDetails);
        mPlaceOrder = (TextView) findViewById(R.id.txtPlaceOrder_User_MedicalShopDetails);
        mCall = (ImageView) findViewById(R.id.imgCall_User_MedicalShopDetails);


        //Initializaing Image Loader
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(Activity_User_MedicalShopDetails.this));

        // Initializing ImageLoading Options
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.loadingicon)
                .showImageForEmptyUri(R.drawable.shopimg)
                .showImageOnFail(R.drawable.shopimg).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();


        mCall.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + mMobile.getText().toString()));
                startActivity(intent);
            }
        });

        mMobile.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + mMobile.getText().toString()));
                startActivity(intent);
            }
        });

        mBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mPlaceOrder.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                LayoutInflater layoutInflater = LayoutInflater.from(Activity_User_MedicalShopDetails.this);
                View promptView = layoutInflater.inflate(R.layout.dialog_place_order, null);

                builder = new AlertDialog.Builder(Activity_User_MedicalShopDetails.this).create();
                //builder.setTitle(Title);
                builder.setCancelable(false);

                TextView title = (TextView) promptView.findViewById(R.id.txtTitle_dialog_user_placeorder);
                mAddress_Send = (EditText) promptView.findViewById(R.id.edtAddress_dialog_user_placeorder);
                mComment_Send = (EditText) promptView.findViewById(R.id.edtComment_dialog_user_placeorder);
                Button place = (Button) promptView.findViewById(R.id.btnPlaceOrder_dialog_user_placeorder);
                Button cancel = (Button) promptView.findViewById(R.id.btnCancel_dialog_user_placeorder);

                pres1 = (ImageView) promptView.
                        findViewById(R.id.imgPre1_dialog_user_placeorder);
                pres2 = (ImageView) promptView.
                        findViewById(R.id.imgPre2_dialog_user_placeorder);
                pres3 = (ImageView) promptView.
                        findViewById(R.id.imgPre3_dialog_user_placeorder);
                mSameComp = (CheckBox) promptView.findViewById(R.id.chkSameComp_dialog_user_placeorder);
                mCOD = (CheckBox) promptView.findViewById(R.id.chkCOD_dialog_user_placeorder);
                mTimeSlot = (Spinner) promptView.findViewById(R.id.spinTime_dialog_user_placeorder);


                Calendar c = Calendar.getInstance();
                int hrs = c.get(Calendar.HOUR_OF_DAY);

                ArrayAdapter<String> arr = null;
                //arr.clear();
                if (hrs <= 23 && hrs >= 20) {
                    arr = new ArrayAdapter<String>(getApplicationContext(),
                            R.layout.list_spinner_timeslot, getResources().
                            getStringArray(R.array.timeslot_arry_all));
                }

                if (hrs <= 10 && hrs >= 0) {
                    arr = new ArrayAdapter<String>(getApplicationContext(),
                            R.layout.list_spinner_timeslot, getResources().
                            getStringArray(R.array.timeslot_arry_all));
                }

                if (hrs <= 12 && hrs >= 11) {
                    arr = new ArrayAdapter<String>(getApplicationContext(),
                            R.layout.list_spinner_timeslot, getResources().
                            getStringArray(R.array.timeslot_arry_11_01));
                }

                if (hrs <= 17 && hrs >= 13) {
                    arr = new ArrayAdapter<String>(getApplicationContext(),
                            R.layout.list_spinner_timeslot, getResources().
                            getStringArray(R.array.timeslot_arry_04_06));
                }

                if (hrs <= 19 && hrs >= 18) {
                    arr = new ArrayAdapter<String>(getApplicationContext(),
                            R.layout.list_spinner_timeslot, getResources().
                            getStringArray(R.array.timeslot_arry_06_08));
                }


                mTimeSlot.setAdapter(arr);
                mAddress_Send.setHintTextColor(Color.WHITE);
                mComment_Send.setHintTextColor(Color.WHITE);

                // setting on click listener
                cancel.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        builder.dismiss();
                    }
                });


                pres1.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        LayoutInflater layoutInflater = LayoutInflater.from(Activity_User_MedicalShopDetails.this);
                        View promptView = layoutInflater.inflate(R.layout.dialogbox_image_select, null);
                        final AlertDialog builder = new AlertDialog.Builder(Activity_User_MedicalShopDetails.this).create();

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

                pres2.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        LayoutInflater layoutInflater = LayoutInflater.from(Activity_User_MedicalShopDetails.this);
                        View promptView = layoutInflater.inflate(R.layout.dialogbox_image_select, null);
                        final AlertDialog builder = new AlertDialog.Builder(Activity_User_MedicalShopDetails.this).create();

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


                pres3.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        LayoutInflater layoutInflater = LayoutInflater.from(Activity_User_MedicalShopDetails.this);
                        View promptView = layoutInflater.inflate(R.layout.dialogbox_image_select, null);
                        final AlertDialog builder = new AlertDialog.Builder(Activity_User_MedicalShopDetails.this).create();

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

                place.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mAddress_Send.getText().toString().equalsIgnoreCase("")) {
                            Toast.makeText(getApplicationContext(), "Enter address.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (mComment_Send.getText().toString().equalsIgnoreCase("")
                                && mPhoto_captured1 == null) {
                            Toast.makeText(getApplicationContext(), "Enter comments / add prescription.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (mTimeSlot.getSelectedItemPosition() == 0) {
                            Toast.makeText(getApplicationContext(), "Select time slot.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (!mCOD.isChecked()) {
                            Toast.makeText(getApplicationContext(), "Select Cash on delivery.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        new ProgressTask_PlaceOrder().execute();
                    }
                });

                builder.setView(promptView);
                builder.show();
            }
        });


        new ProgressTask_GetShopDetails().execute();

    }


    // Class to login Mail
    private class ProgressTask_GetShopDetails extends AsyncTask<Void, Void, Void> {
        String Result = null, Status = null;
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(Activity_User_MedicalShopDetails.this);
            pDialog.setMessage("Please wait..");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            Service_Handler sh = new Service_Handler();

            ArrayList<NameValuePair> registerParam = new ArrayList<NameValuePair>();
            // add all parameter here
            registerParam.add(new BasicNameValuePair("user_no", mShopNo));

            String jsonStr = sh.makeServiceCall(getResources().
                            getString(R.string.baseUrl_webservice) + "view_medical_store_profile.php",
                    Service_Handler.POST, registerParam);

            if (jsonStr != null) {
                try {
                    JSONArray jsonResponse = new JSONArray(jsonStr);
                    for (int i = 0; i < jsonResponse.length(); i++) {
                        JSONObject jsonChildNode = jsonResponse.getJSONObject(0);
                        mUser_No_Get = jsonChildNode.getString("user_no");
                        mEmail_GET = jsonChildNode.getString("email_id");
                        mMobile_GET = jsonChildNode.getString("mobile_no");
                        mAddress_GET = jsonChildNode.getString("shop_address");
                        mShopName_GET = jsonChildNode.getString("shop_name");
                        mShop_Logo_Get = jsonChildNode.getString("shop_logo");
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

            mShopName.setText(mShopName_GET);
            mAddress.setText(mAddress_GET);
            mMobile.setText(mMobile_GET);
            mEmail.setText(mEmail_GET);

            String path = getResources().getString(R.string.baseUrl_webservice) + "upload_image/" +
                    mShop_Logo_Get;

            ImageLoader.getInstance().displayImage(path, mShopLogo,
                    options, null);
        }
    }

    // Class to place order
    private class ProgressTask_PlaceOrder extends AsyncTask<Void, Void, Void> {
        String Result = null, Status = null;
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(Activity_User_MedicalShopDetails.this);
            pDialog.setMessage("Please wait..");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
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
                registerParam.add(new BasicNameValuePair("prescription1", picName_send1));
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
                registerParam.add(new BasicNameValuePair("prescription2", picName_send2));
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
                registerParam.add(new BasicNameValuePair("prescription3", picName_send3));
                registerParam.add(new BasicNameValuePair("image3", strImg3));
            } else {
                data3 = null;
            }

            Service_Handler sh = new Service_Handler();
            // add all parameter here
            registerParam.add(new BasicNameValuePair("user_no_fk", mUserNo_Send));
            registerParam.add(new BasicNameValuePair("shop_no_fk", mUser_No_Get));
            registerParam.add(new BasicNameValuePair("order_status", "Placed"));
            registerParam.add(new BasicNameValuePair("comments", mComment_Send.getText().toString()));
            registerParam.add(new BasicNameValuePair("cust_address", mAddress_Send.getText().toString()));

            if (mSameComp.isChecked()) {
                registerParam.add(new BasicNameValuePair("same_composition", "Yes"));
            } else {
                registerParam.add(new BasicNameValuePair("same_composition", "No"));
            }

            registerParam.add(new BasicNameValuePair("payment_mode", "Cash on delivery"));
            registerParam.add(new BasicNameValuePair("time_slot",
                    mTimeSlot.getSelectedItem().toString()));

            String jsonStr = sh.makeServiceCall(getResources().
                            getString(R.string.baseUrl_webservice) + "place_order.php",
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
                    mPhoto_captured1 = null;
                    mPhoto_captured2 = null;
                    mPhoto_captured3 = null;
                    builder.dismiss();
                    finish();
                    Toast.makeText(getApplicationContext(), "Order placed successfully.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Something went wrong.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_CODE && resultCode == RESULT_OK) {
            if (FlagControl == 1) {
                mPhoto_captured1 = (Bitmap) data.getExtras().get("data");
                pres1.setImageBitmap(mPhoto_captured1);
                FlagControl = 0;
                pres2.setVisibility(View.VISIBLE);
            }

            if (FlagControl == 2) {
                mPhoto_captured2 = (Bitmap) data.getExtras().get("data");
                pres2.setImageBitmap(mPhoto_captured2);
                FlagControl = 0;
                pres3.setVisibility(View.VISIBLE);
            }
            if (FlagControl == 3) {
                mPhoto_captured3 = (Bitmap) data.getExtras().get("data");
                pres3.setImageBitmap(mPhoto_captured3);
                FlagControl = 0;
            }

            FlagImage = 1;
        }

        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            try {
                if (FlagControl == 1) {
                    mPhoto_captured1 = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), uri);
                    pres1.setImageBitmap(mPhoto_captured1);
                    FlagControl = 0;
                    pres2.setVisibility(View.VISIBLE);
                }

                if (FlagControl == 2) {
                    mPhoto_captured2 = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), uri);
                    pres2.setImageBitmap(mPhoto_captured2);
                    FlagControl = 0;
                    pres3.setVisibility(View.VISIBLE);
                }
                if (FlagControl == 3) {
                    mPhoto_captured3 = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), uri);
                    pres3.setImageBitmap(mPhoto_captured3);
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
}


