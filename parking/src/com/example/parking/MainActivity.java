package com.example.parking;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
	private static final int ARRIVING_TYPE=101;
	private static final int LEAVING_TYPE=102;
	private static final int ATTENDANCE_TYPE_START=301;
	private static final int ATTENDANCE_TYPE_END=302;
	private TextView mParkNumberTV;
	private TextView mUserNumberTV;
	private TextView mUserTimeTV;
	private Button mArrivingButton;
	private Button mLeavingButton;
	private Button mMangementButton;
	private Button mQueryButton;
	private Button mWorkAttendanceButton;
	private Button mUserCenterButton;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
/*		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		mUserTV = (TextView)findViewById(R.id.textView1);
		mUserTV.setText("工号： "+bundle.getString("user") );*/
		mParkNumberTV = (TextView)findViewById(R.id.tv_park_number);
		mParkNumberTV.setText("车场编号:" + this.getString(R.string.park_number_fixed));
		mUserNumberTV = (TextView)findViewById(R.id.tv_user_number);
		mUserNumberTV.setText("工号:" + this.getString(R.string.user_number_fixed));
		mUserTimeTV = (TextView)findViewById(R.id.tv_user_time);
		mUserTimeTV.setText("时间:" + this.getString(R.string.work_time_fixed));
		mArrivingButton = (Button) findViewById(R.id.bt_arriving);
		mArrivingButton.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v){
				Intent intent = new Intent(MainActivity.this,InputLicenseActivity.class);
				Bundle bundle = new Bundle();
				bundle.putInt("type", ARRIVING_TYPE);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
		mLeavingButton = (Button) findViewById(R.id.bt_leaving);
		mLeavingButton.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v){
				Intent intent = new Intent(MainActivity.this,InputLicenseActivity.class);
				Bundle bundle = new Bundle();
				bundle.putInt("type", LEAVING_TYPE);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
		mMangementButton = (Button) findViewById(R.id.bt_parking_space);
		mMangementButton.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v){
				Intent intent = new Intent(MainActivity.this,ParkingSpaceActivity.class);
				startActivity(intent);
			}
		});
		mQueryButton = (Button) findViewById(R.id.bt_history_search);
		mQueryButton.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v){
				Intent intent = new Intent(MainActivity.this,ParkingHistorySearchActivity.class);
				startActivity(intent);
			}
		});
		mWorkAttendanceButton = (Button) findViewById(R.id.bt_work_attendance);
		mWorkAttendanceButton.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v){
				Intent intent = new Intent(MainActivity.this,WorkAttendanceActivity.class);
				Bundle bundle = new Bundle();
				bundle.putInt("attendancetype", ATTENDANCE_TYPE_END);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
		mUserCenterButton = (Button) findViewById(R.id.bt_user_account);
		mUserCenterButton.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v){
				Intent intent = new Intent(MainActivity.this,UserCenterActivity.class);
				startActivity(intent);
			}
		});
	}
	 
}
