package in.co.medibox.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
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

import java.util.ArrayList;

import in.co.medibox.R;
import in.co.medibox.activity.FullScreenViewActivity;
import in.co.medibox.model.Pojo_NewOrders;
import in.co.medibox.service.Service_Handler;

public class Fragment_NewOrders extends Fragment {
    private AlertDialog builder;
    private ExpandableListView mNewOrdersExpList;
    private SharedPreferences mMediPref;
    private ArrayList<Pojo_NewOrders> mNewOrederList;
    private static String mOrderNo_Send, mOrderStatus_Send;
    private DisplayImageOptions options;
    private TextView mRecNotFound;
    private RadioGroup rbgAvail;
    private RadioButton rbtnAll, rbtnFew;
    private EditText mMediAvailComment, mDeliveryTime, mAmt, mComment_send, mCancelComment;

    public Fragment_NewOrders() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_neworders, container, false);
        mNewOrdersExpList = (ExpandableListView) rootView.findViewById(R.id.expListView_NewOrders);
        mRecNotFound = (TextView) rootView.findViewById(R.id.txtNoRecFound);

        // Initializing SHared Preferences
        mMediPref = getActivity().getSharedPreferences("MEDIAPP", Context.MODE_PRIVATE);
        mNewOrederList = new ArrayList<Pojo_NewOrders>();

        //Initializaing Image Loader
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(getActivity()));

        // Initializing ImageLoading Options
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.loadingicon)
                .showImageForEmptyUri(R.drawable.notfound)
                .showImageOnFail(R.drawable.failed).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        mNewOrdersExpList.setOnGroupExpandListener(new OnGroupExpandListener() {
            int previousGroup = -1;

            @Override
            public void onGroupExpand(int groupPosition) {
                if (groupPosition != previousGroup)
                    mNewOrdersExpList.collapseGroup(previousGroup);
                previousGroup = groupPosition;
            }
        });

        new ProgressTask_Fetch_NewOrders().execute();
        return rootView;
    }

    // class to fetch reservation
    private class ProgressTask_Fetch_NewOrders extends AsyncTask<String, Void, String> {
        String Result = null;
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Fetching orders..");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... args) {
            // Creating service handler class instance
            Service_Handler sh = new Service_Handler();

            ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();
            // add all parameter here
            param.add(new BasicNameValuePair("shop_no_fk", mMediPref.getString("user_no", "").toString()));
            param.add(new BasicNameValuePair("order_status", "Placed"));

            String jsonStr = sh.makeServiceCall(getResources().
                            getString(R.string.baseUrl_webservice) + "view_orders.php",
                    Service_Handler.POST, param);

            if (jsonStr != null) {
                try {
                    JSONArray jsonResponse = new JSONArray(jsonStr);
                    //clear the list view items data
                    mNewOrederList.clear();
                    for (int i = 0; i < jsonResponse.length(); i++) {
                        JSONObject jsonChildNode = jsonResponse.getJSONObject(i);

                        String order_no = jsonChildNode.getString("order_no");
                        String user_no_fk = jsonChildNode.getString("user_no_fk");
                        String cust_address = jsonChildNode.getString("cust_address");
                        String prescription_img1 = jsonChildNode.getString("prescription_img1");
                        String prescription_img2 = jsonChildNode.getString("prescription_img2");
                        String prescription_img3 = jsonChildNode.getString("prescription_img3");
                        String comments = jsonChildNode.getString("comments");
                        String order_date = jsonChildNode.getString("order_date");
                        String order_status = jsonChildNode.getString("order_status");
                        String first_name = jsonChildNode.getString("first_name");
                        String last_name = jsonChildNode.getString("last_name");
                        String email_id = jsonChildNode.getString("email_id");
                        String status = jsonChildNode.getString("status");
                        String composition = jsonChildNode.getString("same_composition");
                        String time_slot = jsonChildNode.getString("time_slot");
                        String payment_mode = jsonChildNode.getString("payment_mode");

                        Result = "1";

                        Pojo_NewOrders pojo = new Pojo_NewOrders();
                        pojo.setOrder_no(order_no);
                        pojo.setUser_no_fk(user_no_fk);
                        pojo.setCust_address(cust_address);
                        pojo.setPrescription_img1(prescription_img1);
                        pojo.setPrescription_img2(prescription_img2);
                        pojo.setPrescription_img3(prescription_img3);
                        pojo.setComments(comments);
                        pojo.setOrder_date(order_date);
                        pojo.setOrder_status(order_status);
                        pojo.setFirst_name(first_name);
                        pojo.setLast_name(last_name);
                        pojo.setEmail_id(email_id);
                        pojo.setStatus(status);
                        pojo.setComposition(composition);

                        pojo.setTime_slot(time_slot);
                        pojo.setPayment_mode(payment_mode);

                        mNewOrederList.add(pojo);
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
            /**
             * Updating parsed JSON data into ListView
             * */
            if (Result != null) {
                mNewOrdersExpList.setVisibility(View.VISIBLE);
                NewOrderAdapter adapter = new NewOrderAdapter(getActivity(), mNewOrederList);
                mNewOrdersExpList.setAdapter(adapter);
                new ProgressTask_Fetch_TotalOrders().execute();
            } else {
                mNewOrdersExpList.setVisibility(View.GONE);
                mRecNotFound.setVisibility(View.VISIBLE);
                new ProgressTask_Fetch_TotalOrders().execute();
            }
        }
    }

    public class NewOrderAdapter extends BaseExpandableListAdapter {
        private ArrayList<Pojo_NewOrders> mList;
        public ArrayList<String> groupItem, tempChild;
        public ArrayList<String> Childtem = new ArrayList<String>();
        public LayoutInflater minflater;
        public ProgressTask_Fetch_NewOrders activity;
        public Context mContext;

        public NewOrderAdapter(Context reservationFragment, ArrayList list) {
            minflater = LayoutInflater.from(reservationFragment);
            mContext = reservationFragment;
            mList = list;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return null;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return 0;
        }

        @Override
        public View getChildView(final int groupPosition, final int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {
            TextView mAddress, mComment, mEmail, mComposition, mTimeSlot, mPaymode;
            Button mConfirmOrder, mCancel;
            ImageView mPrescription1,
                    mPrescription2,
                    mPrescription3,
                    mZoom1,
                    mZoom2,
                    mZoom3;

            if (convertView == null) {
                convertView = minflater.inflate(R.layout.list_row_description_neworders, null);
            }

            mAddress = (TextView) convertView.findViewById(R.id.txtAddress_NewOrders);
            mComment = (TextView) convertView.findViewById(R.id.txtComments_NewOrders);
            mEmail = (TextView) convertView.findViewById(R.id.txtEmail_NewOrders);
            mComposition = (TextView) convertView.findViewById(R.id.txtCompostion_NewOrders);
            mConfirmOrder = (Button) convertView.findViewById(R.id.btnConfirm_NewOrders);
            mCancel = (Button) convertView.findViewById(R.id.btnCancel_NewOrders);

            mTimeSlot = (TextView) convertView.findViewById(R.id.txtTimeSlot_NewOrders);
            mPaymode = (TextView) convertView.findViewById(R.id.txtPaymode_NewOrders);

            mPrescription1 = (ImageView) convertView.
                    findViewById(R.id.imgPrescription1_NewOrders);

            mPrescription2 = (ImageView) convertView.
                    findViewById(R.id.imgPrescription2_NewOrders);

            mPrescription3 = (ImageView) convertView.
                    findViewById(R.id.imgPrescription3_NewOrders);

            mPrescription1.setVisibility(View.GONE);
            mPrescription2.setVisibility(View.GONE);
            mPrescription3.setVisibility(View.GONE);


            mZoom1 = (ImageView) convertView.
                    findViewById(R.id.imgZoom1);
            mZoom2 = (ImageView) convertView.
                    findViewById(R.id.imgZoom2);
            mZoom3 = (ImageView) convertView.
                    findViewById(R.id.imgZoom3);

            mZoom1.setVisibility(View.GONE);
            mZoom2.setVisibility(View.GONE);
            mZoom3.setVisibility(View.GONE);


            mAddress.setText(mList.get(groupPosition).getCust_address().toUpperCase());
            mComment.setText(mList.get(groupPosition).getComments().toUpperCase());
            mEmail.setText(mList.get(groupPosition).getEmail_id());

            mTimeSlot.setText(mList.get(groupPosition).getTime_slot());
            mPaymode.setText(mList.get(groupPosition).getPayment_mode());

            mComposition.setText(mList.get(groupPosition).getComposition().toString());

            final ArrayList<String> paths = new ArrayList<String>();

            if (!mList.get(groupPosition).getPrescription_img1().equalsIgnoreCase("")) {
                mPrescription1.setVisibility(View.VISIBLE);
                mZoom1.setVisibility(View.VISIBLE);

                String path = getResources().getString(R.string.baseUrl_webservice) + "upload_prescriptions/" +
                        mList.get(groupPosition).getPrescription_img1();
                paths.add(path);

                ImageLoader.getInstance().displayImage(path, mPrescription1,
                        options, null);
            }

            if (!mList.get(groupPosition).getPrescription_img2().equalsIgnoreCase("")) {
                mZoom2.setVisibility(View.VISIBLE);
                mPrescription2.setVisibility(View.VISIBLE);

                String path = getResources().getString(R.string.baseUrl_webservice) + "upload_prescriptions/" +
                        mList.get(groupPosition).getPrescription_img2();
                paths.add(path);

                ImageLoader.getInstance().displayImage(path, mPrescription2,
                        options, null);

            }

            if (!mList.get(groupPosition).getPrescription_img3().equalsIgnoreCase("")) {
                mZoom3.setVisibility(View.VISIBLE);
                mPrescription3.setVisibility(View.VISIBLE);
                String path = getResources().getString(R.string.baseUrl_webservice) + "upload_prescriptions/" +
                        mList.get(groupPosition).getPrescription_img3();
                paths.add(path);

                ImageLoader.getInstance().displayImage(path, mPrescription3,
                        options, null);
            }

            mConfirmOrder.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    mOrderNo_Send = mList.get(groupPosition).getOrder_no();
                    mOrderStatus_Send = "Confirmed";

                    LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                    View promptView = layoutInflater.inflate(R.layout.dialog_confirm_order, null);

                    builder = new AlertDialog.Builder(getActivity()).create();
                    builder.setCancelable(false);

                    TextView title = (TextView) promptView.findViewById(R.id.txtTitle_dialog_user_placeorder);
                    rbgAvail = (RadioGroup) promptView.findViewById(R.id.rbgMediAvail_Dialog_ConfirmOrder);
                    rbtnAll = (RadioButton) promptView.findViewById(R.id.rbtnAll_Dialog_ConfirmOrder);
                    rbtnFew = (RadioButton) promptView.findViewById(R.id.rbtnFew_Dialog_ConfirmOrder);

                    mMediAvailComment = (EditText) promptView.
                            findViewById(R.id.edtFewComment_Dialog_ConfirmOrder);

                    mDeliveryTime = (EditText) promptView.
                            findViewById(R.id.edtTime_Dialog_ConfirmOrder);

                    mAmt = (EditText) promptView.
                            findViewById(R.id.edtAmt_Dialog_ConfirmOrder);

                    mComment_send = (EditText) promptView.
                            findViewById(R.id.edtComment_Dialog_ConfirmOrder);

                    Button confirm = (Button) promptView.findViewById(R.id.btnConfirmOrder_Dialog_ConfirmOrder);
                    Button cancel = (Button) promptView.findViewById(R.id.btnCancel_Dialog_ConfirmOrder);

                    rbgAvail.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup group, int checkedId) {
                            // TODO Auto-generated method stub
                            if (checkedId == R.id.rbtnFew_Dialog_ConfirmOrder) {
                                mMediAvailComment.setVisibility(View.VISIBLE);
                            }

                            if (checkedId == R.id.rbtnAll_Dialog_ConfirmOrder) {
                                mMediAvailComment.setVisibility(View.GONE);
                            }
                        }
                    });

                    // setting on click listener
                    cancel.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            builder.dismiss();
                        }
                    });


                    confirm.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub

                            if (rbtnFew.isChecked()) {
                                if (mMediAvailComment.getText().toString().equalsIgnoreCase("")) {
                                    Toast.makeText(getActivity(), "Please enter medicine details.", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }

                            if (mDeliveryTime.getText().toString().equalsIgnoreCase("")) {
                                Toast.makeText(getActivity(), "Enter delivery time.", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            if (mAmt.getText().toString().equalsIgnoreCase("")) {
                                Toast.makeText(getActivity(), "Enter amount.", Toast.LENGTH_SHORT).show();
                                return;
                            }


                            new ProgressTask_ConfirmOrder().execute();
                        }
                    });

                    builder.setView(promptView);

                    builder.show();

                }
            });

            mCancel.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                    View promptView = layoutInflater.
                            inflate(R.layout.dialog_cancel_order, null);

                    builder = new AlertDialog.Builder(getActivity()).create();
                    //	builder.setTitle(Title);

                    builder.setCancelable(false);

                    TextView title = (TextView) promptView.findViewById(R.id.txtTitle_dialog_user_placeorder);

                    mCancelComment = (EditText) promptView.
                            findViewById(R.id.edtCancelReason_Dialog_CancelOrder);


                    Button ok = (Button) promptView.findViewById(R.id.btnCancelOrder_Dialog_CancelOrder);
                    Button cancel = (Button) promptView.findViewById(R.id.btnCancel_Dialog_CancelOrder);

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
                            if (mCancelComment.getText().toString().equalsIgnoreCase("")) {
                                Toast.makeText(getActivity(), "Enter cancelation reason.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            mOrderNo_Send = mList.get(groupPosition).getOrder_no();
                            mOrderStatus_Send = "Cancel";
                            new ProgressTask_CancelOrder().execute();
                            builder.hide();
                        }
                    });

                    builder.setView(promptView);
                    builder.show();
                }
            });


            mPrescription1.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    Intent in = new Intent(getActivity(), FullScreenViewActivity.class);
                    in.putExtra("Pic_path", paths);
                    in.putExtra("Position", groupPosition);
                    getActivity().startActivity(in);
                }
            });

            mPrescription2.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    Intent in = new Intent(getActivity(), FullScreenViewActivity.class);
                    in.putExtra("Pic_path", paths);
                    in.putExtra("Position", groupPosition);
                    getActivity().startActivity(in);
                }
            });
            mPrescription3.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    Intent in = new Intent(getActivity(), FullScreenViewActivity.class);
                    in.putExtra("Pic_path", paths);
                    in.putExtra("Position", groupPosition);
                    getActivity().startActivity(in);
                }
            });

            return convertView;
        }


        @Override
        public int getChildrenCount(int groupPosition) {
            return 1;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return null;
        }

        @Override
        public int getGroupCount() {
            return mList.size();
        }

        @Override
        public void onGroupCollapsed(int groupPosition) {
            super.onGroupCollapsed(groupPosition);
        }

        @Override
        public void onGroupExpanded(int groupPosition) {
            super.onGroupExpanded(groupPosition);


        }

        @Override
        public long getGroupId(int groupPosition) {
            return 0;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {
            TextView mName, mDate;
            if (convertView == null) {
                convertView = minflater.inflate(R.layout.list_row_neworders, null);
            }

            mName = (TextView) convertView.findViewById(R.id.txtCustomerName_NewOrders);
            mDate = (TextView) convertView.findViewById(R.id.txtDate_NewOrders);


            mName.setText(mList.get(groupPosition).getFirst_name() +
                    " " + mList.get(groupPosition).getLast_name());
            mDate.setText(mList.get(groupPosition).getOrder_date());


            return convertView;
        }

        @Override
        public boolean hasStableIds() {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            // TODO Auto-generated method stub
            return false;
        }
    }


    private class ProgressTask_ConfirmOrder extends AsyncTask<String, Void, String> {
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
            param.add(new BasicNameValuePair("order_no", mOrderNo_Send));
            param.add(new BasicNameValuePair("order_status", mOrderStatus_Send));

            param.add(new BasicNameValuePair("amount", mAmt.getText().toString()));
            param.add(new BasicNameValuePair("comment", mComment_send.getText().toString()));

            if (rbtnAll.isChecked()) {
                param.add(new BasicNameValuePair("availablity", "all_medicine"));
                param.add(new BasicNameValuePair("medicines_available", "All medicines are available"));
            }

            if (rbtnFew.isChecked()) {
                param.add(new BasicNameValuePair("availablity", "few_medicine"));
                param.add(new BasicNameValuePair("medicines_available", mMediAvailComment.getText().toString()));
            }

            param.add(new BasicNameValuePair("delivery_time", mDeliveryTime.getText().toString()));

            String jsonStr = sh.makeServiceCall(getResources().
                            getString(R.string.baseUrl_webservice) + "change_order_status.php",
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
                    if (mOrderStatus_Send.equalsIgnoreCase("Confirmed")) {
                        builder.hide();
                        Toast.makeText(getActivity(), "You have confirmed the order.", Toast.LENGTH_SHORT).show();
                        new ProgressTask_Fetch_NewOrders().execute();
                    }

                    if (mOrderStatus_Send.equalsIgnoreCase("Cancel")) {
                        Toast.makeText(getActivity(), "You have cancelled the order.", Toast.LENGTH_SHORT).show();
                        new ProgressTask_Fetch_NewOrders().execute();
                    }

                } else {
                    Toast.makeText(getActivity(), "Something went wrong.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    private class ProgressTask_CancelOrder extends AsyncTask<String, Void, String> {
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
            param.add(new BasicNameValuePair("order_no", mOrderNo_Send));
            param.add(new BasicNameValuePair("order_status", mOrderStatus_Send));
            param.add(new BasicNameValuePair("cancel_comment", mCancelComment.getText().toString()));

            String jsonStr = sh.makeServiceCall(getResources().
                            getString(R.string.baseUrl_webservice) + "change_order_status.php",
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
            /**
             * Updating parsed JSON data into ListView
             * */

            if (Result != null) {
                if (Result.equalsIgnoreCase("1")) {
                    if (mOrderStatus_Send.equalsIgnoreCase("Confirmed")) {
                        Toast.makeText(getActivity(), "You have confirmed the order.", Toast.LENGTH_SHORT).show();
                        new ProgressTask_Fetch_NewOrders().execute();
                    }

                    if (mOrderStatus_Send.equalsIgnoreCase("Cancel")) {
                        Toast.makeText(getActivity(), "You have cancelled the order.", Toast.LENGTH_SHORT).show();
                        new ProgressTask_Fetch_NewOrders().execute();
                    }

                } else {

                    Toast.makeText(getActivity(), "Something went wrong.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    // class to fetch reservation
    private class ProgressTask_Fetch_TotalOrders extends AsyncTask<String, Void, String> {
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
            //		param.add(new BasicNameValuePair("order_status","Placed"));

            String jsonStr = sh.makeServiceCall(getResources().
                            getString(R.string.baseUrl_webservice) + "total_orders.php",
                    Service_Handler.POST, param);

            if (jsonStr != null) {
                try {
                    JSONArray jsonResponse = new JSONArray(jsonStr);
                    for (int i = 0; i < jsonResponse.length(); i++) {
                        JSONObject jsonChildNode = jsonResponse.getJSONObject(i);
                        Result = jsonChildNode.getString("total_orders");
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
            /**
             * Updating parsed JSON data into ListView
             * */

            if (Result != null) {
                if (Result.equalsIgnoreCase("yes")) {
                    AlertDialog alert = null;
                    AlertDialog.Builder builders = new AlertDialog.Builder(getActivity());
                    builders.setMessage("You have more than 15 orders to deliver.")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //   super.onBackPressed();

                                    //alert.dismiss();
                                }
                            });

                    alert = builders.create();
                    alert.show();
                }
            }
        }
    }
}
