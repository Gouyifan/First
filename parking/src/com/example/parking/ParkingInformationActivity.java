package com.example.parking;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ParkingInformationActivity extends Activity {
	private final static int EVENT_DISPLAY_TIME = 101;
	private final static int EVENT_INSERT_SUCCESS = 102;
	private final static int EVENT_DUPLICATED_LOCATION_NUMBER = 103;
	private Spinner mCarType;
	private Spinner mParkingType;
	private Spinner mLocationNumber;
	private TextView mLicensePlateNumberTV;
	private TextView mStartTime;
	private Button mOkButton;
	private Button mCancelButton;
	private DBAdapter mDBAdapter;
	private boolean mPermissionState=true;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mDBAdapter = new DBAdapter(this);
		setContentView(R.layout.activity_parking_information);
		mCarType = (Spinner) findViewById(R.id.sp_car_type);
		mParkingType = (Spinner) findViewById(R.id.sp_parking_type);
		mLocationNumber = (Spinner) findViewById(R.id.sp_parking_location);
		mLicensePlateNumberTV = (TextView) findViewById(R.id.tv_license_plate_number);
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		mLicensePlateNumberTV.setText(bundle.getString("licensePlate"));
		mStartTime=(TextView) findViewById(R.id.tv_start_time_arriving);
		new TimeThread().start();
		mOkButton=(Button) findViewById(R.id.bt_confirm_arriving);
		mOkButton.setOnClickListener(new InsertOnclickListener(mLicensePlateNumberTV.getText().toString(),
				mCarType.getSelectedItem().toString(), mParkingType.getSelectedItem().toString(), Integer.parseInt(mLocationNumber.getSelectedItem().toString()),
				DateFormat.format("yyyy-MM-dd HH:mm:ss", System.currentTimeMillis()).toString(), null, null, "未付"));
		mCancelButton=(Button) findViewById(R.id.bt_cancel_arriving);
		mCancelButton.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View v){
				finish();
				Intent intent = new Intent(ParkingInformationActivity.this,MainActivity.class);
				startActivity(intent);
			}
		});
	}

	private class InsertOnclickListener implements Button.OnClickListener{
		 private String licensePlate;
		 private String carType;
		 private String parkingType;
		 private int locationNumber;
		 private String startTime;
		 private String leaveTime;
		 private String expense;
		 private String paymentPattern;
        public InsertOnclickListener(String licensePlate, String carType, String parkingType, int locationNumber, 
        		String startTime, String leaveTime, String expense, String paymentPattern){
            this.licensePlate = licensePlate;
            this.carType = carType;
            this.parkingType = parkingType;
            this.locationNumber = locationNumber;
            this.startTime = startTime;
            this.leaveTime = leaveTime;
            this.expense = expense;
            this.paymentPattern = paymentPattern;
        }
        @Override
        public void onClick(View v){
        	 mDBAdapter.open();
        	 Cursor cursor = mDBAdapter.getParkingByLocationNumber( Integer.parseInt(mLocationNumber.getSelectedItem().toString()));
        	 try {
             	cursor.moveToFirst();
             	if(cursor.getString(cursor.getColumnIndex("paymentpattern")).equals("未付")){
            		Message msg = new Message();
                    msg.what = EVENT_DUPLICATED_LOCATION_NUMBER;
                    mHandler.sendMessage(msg);
                    mPermissionState=false;
             	}
             }
             catch (Exception e) {
                     e.printStackTrace();
             } finally{
                 	if(cursor!=null){
                 		cursor.close();
                     }
             }
        	 if(mPermissionState){
             	long  result = mDBAdapter.insertParking(licensePlate,mCarType.getSelectedItem().toString(),mParkingType.getSelectedItem().toString(),
             			 Integer.parseInt(mLocationNumber.getSelectedItem().toString()),startTime,leaveTime,expense,paymentPattern);
            	if (result != -1){//插入成功
            		Message msg = new Message();
                    msg.what = EVENT_INSERT_SUCCESS;
                    mHandler.sendMessage(msg);
                	Intent intent = new Intent(ParkingInformationActivity.this,MainActivity.class);
    				startActivity(intent);
    				finish();
                }
            	mDBAdapter.close(); 
        	 }
        }
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

	private Handler mHandler = new Handler() {
        @Override
        public void handleMessage (Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case EVENT_DISPLAY_TIME:
                    CharSequence sysTimeStr = DateFormat.format("yyyy-MM-dd HH:mm:ss", System.currentTimeMillis());
                    mStartTime.setText("入场时间：" + sysTimeStr);
                    break;
                case EVENT_INSERT_SUCCESS:
                	Toast.makeText(getApplicationContext(), "入场成功", Toast.LENGTH_SHORT).show();
                	break;
                case EVENT_DUPLICATED_LOCATION_NUMBER:
                	Toast.makeText(getApplicationContext(), "该泊位已被占用", Toast.LENGTH_SHORT).show();
                	break;
                default:
                    break;
            }
        }
    };
}
