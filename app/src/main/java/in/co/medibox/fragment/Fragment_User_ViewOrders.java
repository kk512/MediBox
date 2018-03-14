package in.co.medibox.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
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
import in.co.medibox.model.Pojo_User_ViewOrders;
import in.co.medibox.service.Service_Handler;


public class Fragment_User_ViewOrders extends Fragment {
    private AlertDialog builder;
    private DeliveredOrderAdapter adapter;
    private AlertDialog abuilder;
    private static String mOrderNo_Send, mOrderStatus_Send;
    private ExpandableListView mViewOrdersExpList;
    private SharedPreferences mMediPref;
    private ArrayList<Pojo_User_ViewOrders> mViewOrdersList;

    private DisplayImageOptions options;
    private TextView mRecNotFound;
    private static String mUserConf;
    private EditText mCancelComment;

    public Fragment_User_ViewOrders() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_user_viewrders, container, false);
        mViewOrdersExpList = (ExpandableListView) rootView.findViewById(R.id.expListView_User_ViewOrders);

        mRecNotFound = (TextView) rootView.findViewById(R.id.txtNoRecFound);

        // Initializing SHared Preferences
        mMediPref = getActivity().getSharedPreferences("MEDIAPP", Context.MODE_PRIVATE);
        mViewOrdersList = new ArrayList<Pojo_User_ViewOrders>();

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

        mViewOrdersExpList.setOnGroupExpandListener(new OnGroupExpandListener() {
            int previousGroup = -1;

            @Override
            public void onGroupExpand(int groupPosition) {
                if (groupPosition != previousGroup)
                    mViewOrdersExpList.collapseGroup(previousGroup);
                previousGroup = groupPosition;
            }
        });

        new ProgressTask_Fetch_ViewOrdersOrders().execute();
        return rootView;
    }

    // class to fetch reservation
    private class ProgressTask_Fetch_ViewOrdersOrders extends AsyncTask<String, Void, String> {
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
            param.add(new BasicNameValuePair("user_no_fk", mMediPref.getString("user_no", "").toString()));
            //	param.add(new BasicNameValuePair("order_status","Delivered"));

            String jsonStr = sh.makeServiceCall(getResources().
                            getString(R.string.baseUrl_webservice) + "view_user_orders.php",
                    Service_Handler.POST, param);

            if (jsonStr != null) {
                try {
                    JSONArray jsonResponse = new JSONArray(jsonStr);
                    mViewOrdersList.clear();
                    for (int i = 0; i < jsonResponse.length(); i++) {
                        JSONObject jsonChildNode = jsonResponse.getJSONObject(i);
                        String order_no = jsonChildNode.getString("order_no");
                        String user_no_fk = jsonChildNode.getString("user_no_fk");
                        String shop_address = jsonChildNode.getString("shop_address");
                        String prescription_img1 = jsonChildNode.getString("prescription_img1");
                        String prescription_img2 = jsonChildNode.getString("prescription_img2");
                        String prescription_img3 = jsonChildNode.getString("prescription_img3");

                        String comments = jsonChildNode.getString("comments");
                        String order_date = jsonChildNode.getString("order_date");
                        String order_status = jsonChildNode.getString("order_status");
                        String shop_name = jsonChildNode.getString("shop_name");

                        String email_id = jsonChildNode.getString("email_id");
                        String status = jsonChildNode.getString("status");

                        String composition = jsonChildNode.getString("same_composition");
                        String user_response = jsonChildNode.getString("user_response");

                        String receivedorder = jsonChildNode.getString("received_order");

                        String time_slot = jsonChildNode.getString("time_slot");
                        String payment_mode = jsonChildNode.getString("payment_mode");

                        String medicines_available = jsonChildNode.getString("medicines_available");
                        String amount = jsonChildNode.getString("amount");

                        Result = "1";

                        Pojo_User_ViewOrders pojo = new Pojo_User_ViewOrders();
                        pojo.setOrder_no(order_no);
                        pojo.setUser_no_fk(user_no_fk);
                        pojo.setShop_address(shop_address);
                        pojo.setPrescription_img1(prescription_img1);
                        pojo.setPrescription_img2(prescription_img2);
                        pojo.setPrescription_img3(prescription_img3);
                        pojo.setComments(comments);
                        pojo.setOrder_date(order_date);
                        pojo.setOrder_status(order_status);
                        pojo.setShop_name(shop_name);

                        pojo.setEmail_id(email_id);
                        pojo.setStatus(status);
                        pojo.setComposition(composition);
                        pojo.setUser_response(user_response);
                        pojo.setReceivedorder(receivedorder);

                        pojo.setTime_slot(time_slot);
                        pojo.setPayment_mode(payment_mode);
                        pojo.setMedicines_available(medicines_available);
                        pojo.setAmount(amount);

                        mViewOrdersList.add(pojo);
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
                mViewOrdersExpList.setVisibility(View.VISIBLE);
                adapter = new DeliveredOrderAdapter(getActivity(), mViewOrdersList);
                mViewOrdersExpList.setAdapter(adapter);
            } else {
                mViewOrdersExpList.setVisibility(View.GONE);
                mRecNotFound.setVisibility(View.VISIBLE);
            }
        }
    }

    public class DeliveredOrderAdapter extends BaseExpandableListAdapter {
        private ArrayList<Pojo_User_ViewOrders> mList;
        public ArrayList<String> groupItem, tempChild;
        public ArrayList<String> Childtem = new ArrayList<String>();
        public LayoutInflater minflater;
        public ProgressTask_Fetch_ViewOrdersOrders activity;
        public Context mContext;

        public DeliveredOrderAdapter(Context reservationFragment, ArrayList list) {
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
            TextView mAddress, mComment, mEmail, mStatus, mComposition, mTimeSlot, mPaymode, mAmount, mMediAvail;
            ImageView mPrescription1,
                    mPrescription2,
                    mPrescription3,
                    mZoom1,
                    mZoom2,
                    mZoom3;
            Button mReorders, mOtherShop;

            if (convertView == null) {
                convertView = minflater.inflate(R.layout.list_row_description_user_vieworders, null);
            }

            mAddress = (TextView) convertView.findViewById(R.id.txtAddress_User_ViewOrders);
            mComment = (TextView) convertView.findViewById(R.id.txtComments_User_ViewOrders);
            mEmail = (TextView) convertView.findViewById(R.id.txtEmail_User_ViewOrders);
            mStatus = (TextView) convertView.findViewById(R.id.txtStatus_User_ViewOrders);
            mReorders = (Button) convertView.findViewById(R.id.btnReOrder_User_ViewOrders);
            mOtherShop = (Button) convertView.findViewById(R.id.btnOtherVendor_User_ViewOrders);
            mComposition = (TextView) convertView.findViewById(R.id.txtCompostion_NewOrders);
            mTimeSlot = (TextView) convertView.findViewById(R.id.txtTimeSlot_NewOrders);
            mPaymode = (TextView) convertView.findViewById(R.id.txtPaymode_NewOrders);
            mAmount = (TextView) convertView.findViewById(R.id.txtAmt_NewOrders);
            mMediAvail = (TextView) convertView.findViewById(R.id.txtMediAvail_NewOrders);


            mAddress.setText(mList.get(groupPosition).getCust_address().toUpperCase());
            mComment.setText(mList.get(groupPosition).getComments().toUpperCase());
            mEmail.setText(mList.get(groupPosition).getEmail_id());
            mStatus.setText(mList.get(groupPosition).getOrder_status());
            mComposition.setText(mList.get(groupPosition).getComposition().toString());
            mTimeSlot.setText(mList.get(groupPosition).getTime_slot().toString());
            mPaymode.setText(mList.get(groupPosition).getPayment_mode().toString());

            if (!mList.get(groupPosition).getAmount().toString().equalsIgnoreCase("")) {
                mAmount.setText(mList.get(groupPosition).getAmount().toString());
            } else {
                mAmount.setText("Not available");
            }

            if (!mList.get(groupPosition).getMedicines_available().toString().equalsIgnoreCase("")) {
                mMediAvail.setText(mList.get(groupPosition).getMedicines_available().toString());
            } else {
                mMediAvail.setText("Not available");
            }


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

            final ArrayList<String> paths = new ArrayList<String>();

            if (!mList.get(groupPosition).getPrescription_img1().equalsIgnoreCase("")) {
                mZoom1.setVisibility(View.VISIBLE);
                mPrescription1.setVisibility(View.VISIBLE);
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

            mReorders.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOrderNo_Send = mList.get(groupPosition).getOrder_no();
                    new ProgressTask_Reorder().execute();
                }
            });

            mOtherShop.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment fragment = new Fragment_User_PlaceOrder();
                    android.app.FragmentManager fragmentManager = getActivity().getFragmentManager();
                    android.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.show(fragment);
                    fragmentTransaction.replace(R.id.frame_container, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
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
            if (mList.get(groupPosition).getOrder_status().equalsIgnoreCase("Confirmed")) {
                if (mList.get(groupPosition).getUser_response().equalsIgnoreCase("")) {
                    mOrderNo_Send = mList.get(groupPosition).getOrder_no();
                    new ProgressTask_Fetch_OrderDetails().execute();
                    // notifyDataSetChanged();
                }
            }
            if (mList.get(groupPosition).getOrder_status().equalsIgnoreCase("Delivered")) {
                if (mList.get(groupPosition).getReceivedorder().equalsIgnoreCase("")) {
                    mOrderNo_Send = mList.get(groupPosition).getOrder_no();
                    showDialogYesNo();
                }
            }
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
                convertView = minflater.inflate(R.layout.list_row_deliveredorders, null);
            }
            mName = (TextView) convertView.findViewById(R.id.txtCustomerName_DeliveredOrders);
            mDate = (TextView) convertView.findViewById(R.id.txtDate_DeliveredOrders);

            mName.setText(mList.get(groupPosition).getShop_name());
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

        // Class for reorder
        private class ProgressTask_Reorder extends AsyncTask<String, Void, String> {
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
                //		param.add(new BasicNameValuePair("order_status","Sent"));

                String jsonStr = sh.makeServiceCall(getResources().
                                getString(R.string.baseUrl_webservice) + "reorder.php",
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
                        Toast.makeText(getActivity(), "Order placed successfully..", Toast.LENGTH_SHORT).show();
                        new ProgressTask_Fetch_ViewOrdersOrders().execute();
                    } else {
                        Toast.makeText(getActivity(), "Something went wrong.", Toast.LENGTH_SHORT).show();
                    }
                }

            }

        }
    }


    // class to fetch reservation
    private class ProgressTask_Fetch_OrderDetails extends AsyncTask<String, Void, String> {
        String Result = null;
        private ProgressDialog pDialog;
        String order_no, medicines_available, delivery_time, amount, comments;

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
            param.add(new BasicNameValuePair("order_no", mOrderNo_Send));
            //	param.add(new BasicNameValuePair("order_status","Delivered"));

            String jsonStr = sh.makeServiceCall(getResources().
                            getString(R.string.baseUrl_webservice) + "shop_response.php",
                    Service_Handler.POST, param);

            if (jsonStr != null) {
                try {
                    JSONArray jsonResponse = new JSONArray(jsonStr);
                    mViewOrdersList.clear();
                    for (int i = 0; i < jsonResponse.length(); i++) {
                        JSONObject jsonChildNode = jsonResponse.getJSONObject(i);
                        order_no = jsonChildNode.getString("order_no");
                        medicines_available = jsonChildNode.getString("medicines_available");
                        delivery_time = jsonChildNode.getString("delivery_time");
                        amount = jsonChildNode.getString("amount");
                        comments = jsonChildNode.getString("comment");
                        Result = "1";
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
            if (pDialog.isShowing())
                pDialog.dismiss();

            if (Result != null) {
                LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                View promptView = layoutInflater.
                        inflate(R.layout.dialog_shopresponse_user, null);

                builder = new AlertDialog.Builder(getActivity()).create();
                //	builder.setTitle(Title);
                builder.setCancelable(false);

                TextView title = (TextView) promptView.findViewById(R.id.txtTitle_dialog_user_placeorder);
                TextView del_time = (TextView) promptView.findViewById(R.id.txtDeliveryTime_Dialog_ShopRespose);
                TextView amt = (TextView) promptView.findViewById(R.id.txtAmount_Dialog_ShopRespose);
                TextView avail_medi = (TextView) promptView.findViewById(R.id.txtAvailMedi_Dialog_ShopRespose);
                TextView comment = (TextView) promptView.findViewById(R.id.txtComment_Dialog_ShopRespose);
                Button accept = (Button) promptView.findViewById(R.id.btnAccept_Dialog_ShopRespose);
                Button reject = (Button) promptView.findViewById(R.id.btnReject_Dialog_ShopRespose);


                del_time.setText(delivery_time);
                amt.setText(amount);
                avail_medi.setText(medicines_available);
                comment.setText(comments);

                // setting on click listener
                reject.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        builder.dismiss();
                        adapter.notifyDataSetChanged();
                        mUserConf = "cancel";
                        mOrderNo_Send = order_no;

                        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                        View promptView = layoutInflater.
                                inflate(R.layout.dialog_cancel_order, null);

                        abuilder = new AlertDialog.Builder(getActivity()).create();
                        //	builder.setTitle(Title);
                        abuilder.setCancelable(false);

                        TextView title = (TextView) promptView.findViewById(R.id.txtTitle_dialog_user_placeorder);
                        mCancelComment = (EditText) promptView.
                                findViewById(R.id.edtCancelReason_Dialog_CancelOrder);
                        Button ok = (Button) promptView.findViewById(R.id.btnCancelOrder_Dialog_CancelOrder);
                        Button cancel = (Button) promptView.findViewById(R.id.btnCancel_Dialog_CancelOrder);

                        // setting on click listener
                        cancel.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                abuilder.dismiss();
                                new ProgressTask_Fetch_ViewOrdersOrders().execute();
                            }
                        });

                        ok.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (mCancelComment.getText().toString().equalsIgnoreCase("")) {
                                    Toast.makeText(getActivity(), "Enter cancelation reason.", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                new ProgressTask_AcceptCancelOrder().execute();
                                abuilder.dismiss();
                            }
                        });

                        abuilder.setView(promptView);
                        abuilder.show();
                    }
                });


                accept.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOrderNo_Send = order_no;
                        mUserConf = "approved";
                        new ProgressTask_AcceptCancelOrder().execute();
                        builder.dismiss();
                    }
                });

                builder.setView(promptView);
                builder.show();
            } else {
                Toast.makeText(getActivity(),
                        "Something went wront.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class ProgressTask_AcceptCancelOrder extends AsyncTask<String, Void, String> {
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
            param.add(new BasicNameValuePair("user_confirmation", mUserConf));//approved,cancel

            if (mUserConf.equalsIgnoreCase("cancel")) {
                param.add(new BasicNameValuePair("cancel_comment", mCancelComment.getText().toString()));
            }

            String jsonStr = sh.makeServiceCall(getResources().
                            getString(R.string.baseUrl_webservice) + "user_confirmation.php",
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
                    if (mUserConf.equalsIgnoreCase("approved")) {
                        Toast.makeText(getActivity(),
                                "You have accepted the order.", Toast.LENGTH_SHORT).show();
                        new ProgressTask_Fetch_ViewOrdersOrders().execute();
                    }

                    if (mUserConf.equalsIgnoreCase("cancel")) {
                        Toast.makeText(getActivity(),
                                "You have rejected the order.", Toast.LENGTH_SHORT).show();
                        new ProgressTask_Fetch_ViewOrdersOrders().execute();
                    }

                } else {
                    Toast.makeText(getActivity(), "Something went wrong.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void showDialogYesNo() {
        // TODO Auto-generated method stub
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View promptView = layoutInflater.
                inflate(R.layout.dialog_received_order, null);

        builder = new AlertDialog.Builder(getActivity()).create();
        //	builder.setTitle(Title);
        builder.setCancelable(false);

        TextView title = (TextView) promptView.findViewById(R.id.txtTitle_dialog_user_placeorder);
        Button ok = (Button) promptView.findViewById(R.id.btnYes_Dialog_ReceivedOrder);
        Button cancel = (Button) promptView.findViewById(R.id.btnNo_Dialog_ReceivedOrder);

        // setting on click listener
        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mOrderStatus_Send = "no";
                new ProgressTask_OrderReceived().execute();
                builder.dismiss();
            }
        });


        ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mOrderStatus_Send = "yes";
                new ProgressTask_OrderReceived().execute();
                builder.dismiss();
            }
        });

        builder.setView(promptView);
        builder.show();
    }

    private class ProgressTask_OrderReceived extends AsyncTask<String, Void, String> {
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
            param.add(new BasicNameValuePair("received_order", mOrderStatus_Send));
            //	param.add(new BasicNameValuePair("cancel_comment",mCancelComment.getText().toString()));

            String jsonStr = sh.makeServiceCall(getResources().
                            getString(R.string.baseUrl_webservice) + "received_order.php",
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
                    if (mOrderStatus_Send.equalsIgnoreCase("yes")) {
                        Toast.makeText(getActivity(), "Thanks for your feedback.", Toast.LENGTH_SHORT).show();
                        new ProgressTask_Fetch_ViewOrdersOrders().execute();
                    }

                    if (mOrderStatus_Send.equalsIgnoreCase("no")) {
                        Toast.makeText(getActivity(), "Thanks for your feedback.", Toast.LENGTH_SHORT).show();
                        new ProgressTask_Fetch_ViewOrdersOrders().execute();
                    }
                } else {
                    Toast.makeText(getActivity(), "Something went wrong.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
