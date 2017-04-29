package com.example.parking;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


public class ParkingInformationFragment extends Fragment {
	private final int EVENT_DISPLAY_TIME = 101;
	private View mView;
	private TextView mLocationNumberTV;
	private TextView mLicenseNumberTV;
	private TextView mCarTypeTV;
	private TextView mParkTypeTV;
	private TextView mStartTimeTV;
	private TextView mLeaveTimeTV;
	private Button mConfirmLeavingBT;
	private Button mCancelLeavingBT;
	private int mLocationNumber;
	private String mCarType;
	private String mParkType;
	private String mStartTime;
	private DBAdapter mDBAdapter;
	private String mLicensePlateNumber;
	
	 @Override
	    public void onAttach(Activity activity) {
	        super.onAttach(activity);
	    }

	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	    }

	    @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	    	mView = inflater.inflate(R.layout.fragment_parking_space_detail, container, false);
	        return mView;
	    }

	    @Override
	    public void onActivityCreated(Bundle savedInstanceState) {
	        super.onActivityCreated(savedInstanceState);
	        mLicensePlateNumber = getActivity().getIntent().getStringExtra("licensePlateNumber");
	        mDBAdapter = new DBAdapter(getActivity());
	        mLocationNumberTV=(TextView)mView.findViewById(R.id.tv_location_number_parking_detail);
			mLicenseNumberTV=(TextView)mView.findViewById(R.id.tv_license_plate_number_parking_detail);
			mCarTypeTV=(TextView)mView.findViewById(R.id.tv_car_type_parking_detail);
			mParkTypeTV=(TextView)mView.findViewById(R.id.tv_parking_type_parking_detail);
	        mStartTimeTV=(TextView) mView.findViewById(R.id.tv_start_time_parking_detail);
	        mLeaveTimeTV=(TextView) mView.findViewById(R.id.tv_leave_time_parking_detail);
	        mConfirmLeavingBT=(Button)mView.findViewById(R.id.bt_confirm_leaving);
	        mConfirmLeavingBT.setOnClickListener(new OnClickListener(){
	        	@Override
	        	public void onClick(View v){
					Intent intent = new Intent(getActivity(),LeavingActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString("licensePlate",mLicensePlateNumber );
					intent.putExtras(bundle);
					startActivity(intent);
	        	}
	        });
	        mCancelLeavingBT=(Button)mView.findViewById(R.id.bt_cancel_leaving);
	        mCancelLeavingBT.setOnClickListener(new OnClickListener(){
	        	@Override
	        	public void onClick(View v){
					Intent intent = new Intent(getActivity(),ParkingSpaceActivity.class);
					startActivity(intent);
	        	}
	        });
	        new TimeThread().start();
	        //new SQLThread().start();
	        setParkingInformation();
	    }

	    @Override
	    public void onStart() {
	        super.onStart();
	    }

	    @Override
	    public void onResume() {
	        super.onResume();
	    }

	    @Override
	    public void onPause() {
	        super.onPause();
	    }

	    @Override
	    public void onStop() {
	        super.onStop();
	    }

	    @Override
	    public void onDestroyView() {
	        super.onDestroyView();
	    }

	    @Override
	    public void onDestroy() {
	        super.onDestroy();
	    }

	    @Override
	    public void onDetach() {
	        super.onDetach();
	    }

		public class TimeThread extends Thread {
	        @Override
	        public void run () {
	            do {
	                try {
	                    Thread.sleep(1000);
	                    Message msg = new Message();
	                    msg.what = EVENT_DISPLAY_TIME;
	                    mHandler.sendMessage(msg);
	                }
	                catch (InterruptedException e) {
	                    e.printStackTrace();
	                }
	            } while(true);
	        }
	    }

		public void setParkingInformation(){
        	mDBAdapter.open();
        	Cursor cursor = mDBAdapter.getParkingByLicensePlate(mLicensePlateNumber);
            try {
            	      cursor.moveToFirst();
      		          mLocationNumber =  cursor.getInt(cursor.getColumnIndex("locationnumber"));
      		          mCarType = cursor.getString(cursor.getColumnIndex("cartype"));
      		          mParkType = cursor.getString(cursor.getColumnIndex("parkingtype"));
      		          mStartTime = cursor.getString(cursor.getColumnIndex("starttime"));
      				  mLicenseNumberTV.setText("车牌号: " + mLicensePlateNumber);
      				  mLocationNumberTV.setText("泊位号: " + mLocationNumber);
      		    	  mCarTypeTV.setText("车辆类型: " + mCarType);
      		          mParkTypeTV.setText("泊车类型: " + mParkType);
      		          mStartTimeTV.setText("入场时间: " + mStartTime);
            }
            catch (Exception e) {
                    e.printStackTrace();
            } finally{
                	if(cursor!=null){
                		cursor.close();
                    }
            }
		}
/*	    public class SQLThread extends Thread {
	        @Override
	        public void run () {
	        	mDBAdapter.open();
	        	Cursor cursor = mDBAdapter.getParkingByLicensePlate(mLicensePlateNumber);
	            try {
	            	      cursor.moveToFirst();
	      		          mLocationNumber =  cursor.getInt(cursor.getColumnIndex("locationnumber"));
	      		          mCarType = cursor.getString(cursor.getColumnIndex("cartype"));
	      		          mParkType = cursor.getString(cursor.getColumnIndex("parkingtype"));
	      		          mStartTime = cursor.getString(cursor.getColumnIndex("starttime"));
	      				  mLicenseNumberTV.setText("车牌号: " + mLicensePlateNumber);
	      				  mLocationNumberTV.setText("泊位号: " + mLocationNumber);
	      		    	  mCarTypeTV.setText("车辆类型: " + mCarType);
	      		          mParkTypeTV.setText("泊车类型: " + mParkType);
	      		          mStartTimeTV.setText("入场时间: " + mStartTime);
	            }
	            catch (Exception e) {
	                    e.printStackTrace();
	            } finally{
	                	if(cursor!=null){
	                		cursor.close();
	                    }
	            }
	        }
	    }*/
	    
		private Handler mHandler = new Handler() {
	        @Override
	        public void handleMessage (Message msg) {
	            super.handleMessage(msg);
	            switch (msg.what) {
	                case EVENT_DISPLAY_TIME:
	                    CharSequence sysTimeStr = DateFormat.format("yyyy-MM-dd HH:mm:ss", System.currentTimeMillis());
	                    mLeaveTimeTV.setText("离场时间：" + sysTimeStr);
	                    break;
	                default:
	                    break;
	            }
	        }
	    };
}