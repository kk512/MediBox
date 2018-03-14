package in.co.medibox.fragment;

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
import in.co.medibox.model.Pojo_ConfirmedOrders;
import in.co.medibox.service.Service_Handler;

public class Fragment_ConfirmedOrders extends Fragment {
    private ExpandableListView mConfirmedOrdersExpList;
    private SharedPreferences mMediPref;
    private ArrayList<Pojo_ConfirmedOrders> mConfirmedOrederList;
    private static String mOrderNo_Send;
    private DisplayImageOptions options;
    private TextView mRecNotFound;

    public Fragment_ConfirmedOrders() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_confirmedorders, container, false);
        mConfirmedOrdersExpList = (ExpandableListView) rootView.findViewById(R.id.expListView_ConfirmedOrders);
        mRecNotFound = (TextView) rootView.findViewById(R.id.txtNoRecFound);
        // Initializing SHared Preferences
        mMediPref = getActivity().getSharedPreferences("MEDIAPP", Context.MODE_PRIVATE);

        mConfirmedOrederList = new ArrayList<Pojo_ConfirmedOrders>();

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

        mConfirmedOrdersExpList.setOnGroupExpandListener(new OnGroupExpandListener() {
            int previousGroup = -1;

            @Override
            public void onGroupExpand(int groupPosition) {
                if (groupPosition != previousGroup)
                    mConfirmedOrdersExpList.collapseGroup(previousGroup);
                previousGroup = groupPosition;
            }
        });

        new ProgressTask_Fetch_ConfirmedOrders().execute();
        return rootView;
    }

    // class to fetch reservation
    private class ProgressTask_Fetch_ConfirmedOrders extends AsyncTask<String, Void, String> {
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
            param.add(new BasicNameValuePair("order_status", "Confirmed"));

            String jsonStr = sh.makeServiceCall(getResources().
                            getString(R.string.baseUrl_webservice) + "view_orders.php",
                    Service_Handler.POST, param);

            if (jsonStr != null) {
                try {
                    JSONArray jsonResponse = new JSONArray(jsonStr);
                    mConfirmedOrederList.clear();
                    for (int i = 0; i < jsonResponse.length(); i++) {
                        JSONObject jsonChildNode = jsonResponse.getJSONObject(i);

                        String order_no = jsonChildNode.getString("order_no");
                        String user_no_fk = jsonChildNode.getString("user_no_fk");
                        String cust_address = jsonChildNode.getString("cust_address");
                        String comments = jsonChildNode.getString("comments");
                        String order_date = jsonChildNode.getString("order_date");
                        String order_status = jsonChildNode.getString("order_status");
                        String first_name = jsonChildNode.getString("first_name");
                        String last_name = jsonChildNode.getString("last_name");
                        String email_id = jsonChildNode.getString("email_id");
                        String status = jsonChildNode.getString("status");

                        String prescription_img1 = jsonChildNode.getString("prescription_img1");
                        String prescription_img2 = jsonChildNode.getString("prescription_img2");
                        String prescription_img3 = jsonChildNode.getString("prescription_img3");
                        String composition = jsonChildNode.getString("same_composition");

                        String time_slot = jsonChildNode.getString("time_slot");
                        String payment_mode = jsonChildNode.getString("payment_mode");

                        String user_response = jsonChildNode.getString("user_response");
                        String amount = jsonChildNode.getString("amount");
                        Result = "1";

                        Pojo_ConfirmedOrders pojo = new Pojo_ConfirmedOrders();

                        pojo.setOrder_no(order_no);
                        pojo.setUser_no_fk(user_no_fk);
                        pojo.setCust_address(cust_address);
                        pojo.setComments(comments);
                        pojo.setOrder_date(order_date);
                        pojo.setOrder_status(order_status);
                        pojo.setFirst_name(first_name);
                        pojo.setLast_name(last_name);
                        pojo.setEmail_id(email_id);
                        pojo.setStatus(status);
                        pojo.setPrescription_img1(prescription_img1);
                        pojo.setPrescription_img2(prescription_img2);
                        pojo.setPrescription_img3(prescription_img3);
                        pojo.setComposition(composition);
                        pojo.setTime_slot(time_slot);
                        pojo.setPayment_mode(payment_mode);
                        pojo.setAmount(amount);
                        pojo.setUser_response(user_response);

                        mConfirmedOrederList.add(pojo);

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
                mConfirmedOrdersExpList.setVisibility(View.VISIBLE);
                ConfirmedOrderAdapter adapter = new ConfirmedOrderAdapter(getActivity(), mConfirmedOrederList);
                mConfirmedOrdersExpList.setAdapter(adapter);
            } else {
                mConfirmedOrdersExpList.setVisibility(View.GONE);
                mRecNotFound.setVisibility(View.VISIBLE);
            }
        }
    }

    public class ConfirmedOrderAdapter extends BaseExpandableListAdapter {
        private ArrayList<Pojo_ConfirmedOrders> mList;
        public ArrayList<String> groupItem, tempChild;
        public ArrayList<String> Childtem = new ArrayList<String>();
        public LayoutInflater minflater;
        public ProgressTask_Fetch_ConfirmedOrders activity;
        public Context mContext;

        public ConfirmedOrderAdapter(Context reservationFragment, ArrayList list) {
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
            TextView mAddress, mComment, mEmail, mComposition, mTimeSlot, mPaymode, mAmount;
            Button mProcessingOrder;
            ImageView mPrescription1,
                    mPrescription2,
                    mPrescription3,
                    mZoom1,
                    mZoom2,
                    mZoom3;

            if (convertView == null) {
                convertView = minflater.inflate(R.layout.list_row_description_confirmedorders, null);
            }

            mAddress = (TextView) convertView.findViewById(R.id.txtAddress_ConfirmedOrders);
            mComment = (TextView) convertView.findViewById(R.id.txtComments_ConfirmedOrders);
            mEmail = (TextView) convertView.findViewById(R.id.txtEmail_ConfirmedOrders);
            mProcessingOrder = (Button) convertView.findViewById(R.id.btnProcessing_ConfrimedOrders);
            mComposition = (TextView) convertView.findViewById(R.id.txtCompostion_NewOrders);

            mTimeSlot = (TextView) convertView.findViewById(R.id.txtTimeSlot_NewOrders);
            mPaymode = (TextView) convertView.findViewById(R.id.txtPaymode_NewOrders);
            mAmount = (TextView) convertView.findViewById(R.id.txtAmt_NewOrders);

            mTimeSlot.setText(mList.get(groupPosition).getTime_slot());
            mPaymode.setText(mList.get(groupPosition).getPayment_mode());
            mAmount.setText(mList.get(groupPosition).getAmount());

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


            if (mList.get(groupPosition).getUser_response().equalsIgnoreCase("")) {
                mProcessingOrder.setVisibility(View.GONE);
            } else {
                mProcessingOrder.setVisibility(View.VISIBLE);
            }

            mAddress.setText(mList.get(groupPosition).getCust_address().toUpperCase());
            mComment.setText(mList.get(groupPosition).getComments().toUpperCase());
            mEmail.setText(mList.get(groupPosition).getEmail_id());
            mComposition.setText(mList.get(groupPosition).getComposition().toString());

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
            mProcessingOrder.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    mOrderNo_Send = mList.get(groupPosition).getOrder_no();
                    new ProgressTask_ProcessingOrder().execute();
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
                convertView = minflater.inflate(R.layout.list_row_confirmedorders, null);
            }

            mName = (TextView) convertView.findViewById(R.id.txtCustomerName_ConfirmedOrders);
            mDate = (TextView) convertView.findViewById(R.id.txtDate_ConfirmedOrders);

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

    private class ProgressTask_ProcessingOrder extends AsyncTask<String, Void, String> {
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
            param.add(new BasicNameValuePair("order_status", "Processing"));

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

                    Toast.makeText(getActivity(), "Order sent for processing.", Toast.LENGTH_SHORT).show();
                    new ProgressTask_Fetch_ConfirmedOrders().execute();
                } else {

                    Toast.makeText(getActivity(), "Something went wrong.", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }
}
