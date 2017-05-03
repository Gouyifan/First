package com.example.parking;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.example.parking.ParkingInformationActivity.TimeThread;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class WorkAttendanceActivity extends Activity {
    private static final String SAVE_FILE_NAME = "save_spref_attendance";
	private static final int ATTENDANCE_TYPE_START=301;
	private static final int ATTENDANCE_TYPE_END=302;
	private final int EVENT_DISPLAY_TIME_START = 201;
	private final int EVENT_DISPLAY_TIME_END = 202;
	private final int EVENT_ATTENDANCE_START_SUCCESS = 203;
	private final int EVENT_ATTENDANCE_END_SUCCESS = 204;
	private final int EVENT_ENTER_MAIN = 205;
	private final int EVENT_EXIT_LOGIN = 206;
	private int mType;
	private Button mAttendanceBT;
	private TextView mParkNumberTV;
	private TextView mUserNumberTV;
	private TextView mAttendanceWorkStartTimeTV;
	private TextView mAttendanceWorkEndTimeTV;
	private TextView mAttendanceStartLocationTV;
	private TextView mAttendanceEndLocationTV;
	private TextView mAttendanceDate;
	private TextView mLocationState;
	private Context mContext;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_attendance);
		mContext = this;
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		mType = bundle.getInt("attendancetype");
		mParkNumberTV = (TextView)findViewById(R.id.tv_attendance_park_number);
		mParkNumberTV.setText(R.string.park_number_fixed );
		mUserNumberTV = (TextView)findViewById(R.id.tv_attendance_user_number);
		mUserNumberTV.setText(R.string.user_number_fixed );
		mAttendanceDate=(TextView)findViewById(R.id.tv_attendance_date);
		SimpleDateFormat formatter = new SimpleDateFormat ("yyyy年MM月dd日"); 
		Date curDate = new Date(System.currentTimeMillis());
		String dateStr = formatter.format(curDate);
		mAttendanceDate.setText(dateStr);
		mAttendanceWorkStartTimeTV=(TextView)findViewById(R.id.tv_attendance_work_start_time);
		mAttendanceWorkStartTimeTV.setText(R.string.work_start_time_fixed);
		mAttendanceWorkEndTimeTV=(TextView)findViewById(R.id.tv_attendance_work_end_time);
		mAttendanceWorkEndTimeTV.setText(R.string.work_end_time_fixed);
		mAttendanceStartLocationTV=(TextView)findViewById(R.id.tv_attendance_start_location);
		mAttendanceEndLocationTV=(TextView)findViewById(R.id.tv_attendance_end_location);
		Drawable drawable = getResources().getDrawable(R.drawable.ic_add_location_black_18dp);
		drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight()); //设置边界
		if(mType==ATTENDANCE_TYPE_START){
			mAttendanceStartLocationTV.setText(R.string.work_start_location_fixed);
			mAttendanceStartLocationTV.setCompoundDrawables(drawable, null, null, null);//画在左边
		}else if(mType==ATTENDANCE_TYPE_END){
			mAttendanceWorkStartTimeTV.setText(readData("attendancestarttime"));
			mAttendanceStartLocationTV.setText(R.string.work_start_location_fixed);
			mAttendanceStartLocationTV.setCompoundDrawables(drawable, null, null, null);//画在左边
			mAttendanceEndLocationTV.setText(R.string.work_end_location_fixed);
			mAttendanceEndLocationTV.setCompoundDrawables(drawable, null, null, null);//画在左边		
		}
		mLocationState=(TextView)findViewById(R.id.tv_location_state);
		mLocationState.setText("当前位置:" + this.getString(R.string.location_state));
		new TimeThread().start();
		mAttendanceBT=(Button)findViewById(R.id.bt_work_attendance);
		mAttendanceBT.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View  v){
				if(mType==ATTENDANCE_TYPE_START){
					mAttendanceBT.setEnabled(false);
	        		Message msg1 = new Message();
	                msg1.what = EVENT_ATTENDANCE_START_SUCCESS;
	                mHandler.sendMessage(msg1);
	        		Message msg2 = new Message();
	                msg2.what = EVENT_ENTER_MAIN;
	                mHandler.sendMessageDelayed(msg2, 1000);
				}else if(mType==ATTENDANCE_TYPE_END){
					mAttendanceBT.setEnabled(false);
	        		Message msg1 = new Message();
	                msg1.what = EVENT_ATTENDANCE_END_SUCCESS;
	                mHandler.sendMessage(msg1);
	        		Message msg2 = new Message();
	                msg2.what = EVENT_EXIT_LOGIN;
	                mHandler.sendMessageDelayed(msg2, 5000);
				}
			}
		});
		getActionBar().setDisplayHomeAsUpEnabled(true); 
	}

	public class TimeThread extends Thread {
        @Override
        public void run () {
            do {
                try {
                    Thread.sleep(1000);
                    Message msg = new Message();
                    if(mType==ATTENDANCE_TYPE_START){
                    	msg.what = EVENT_DISPLAY_TIME_START;
                    }else if(mType==ATTENDANCE_TYPE_END){
                    	msg.what = EVENT_DISPLAY_TIME_END;
                    }
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
                case EVENT_DISPLAY_TIME_START:
                    CharSequence sysTimeStrStart = DateFormat.format("HH:mm:ss", System.currentTimeMillis());
                    mAttendanceBT.setText("上班打卡\n" + sysTimeStrStart);
                    break;
                case EVENT_DISPLAY_TIME_END:
                    CharSequence sysTimeStrEnd = DateFormat.format("HH:mm:ss", System.currentTimeMillis());
                    mAttendanceBT.setText("下班打卡\n" + sysTimeStrEnd);
                    break;
                case EVENT_ATTENDANCE_START_SUCCESS:
                	String str = "打卡时间:" + mAttendanceBT.getText().toString().replaceAll("上班打卡\n", "") + "(" 
                                       +	mContext.getString(R.string.work_start_time_fixed) + ")";
                	 writeData(str);
    				 mAttendanceWorkStartTimeTV.setText(str);
                     Toast.makeText(getApplicationContext(), "打卡成功，即将进入主页", Toast.LENGTH_SHORT).show();
                	 break;
                case EVENT_ATTENDANCE_END_SUCCESS:
                	mAttendanceWorkEndTimeTV.setText("打卡时间:" + mAttendanceBT.getText().toString().replaceAll("下班打卡\n", "")
    							+ "(" + mContext.getString(R.string.work_end_time_fixed) + ")");
                     Toast.makeText(getApplicationContext(), "打卡成功，即将退出登录", Toast.LENGTH_SHORT).show();
                	 break;
                case EVENT_ENTER_MAIN:
                	Intent intentMain = new Intent(WorkAttendanceActivity.this,MainActivity.class);
                	startActivity(intentMain);
                	break;
                case EVENT_EXIT_LOGIN:
                	Intent intentLogin = new Intent(WorkAttendanceActivity.this,LoginActivity.class);
                	startActivity(intentLogin);
                	break;
                default:
                    break;
            }
        }
    };

    private String readData(String data) {
        SharedPreferences pref = getSharedPreferences(SAVE_FILE_NAME, MODE_MULTI_PROCESS);
        String str = pref.getString(data, "");
        return str;
    }

    private boolean writeData(String attendancestarttime) {
        SharedPreferences.Editor share_edit = getSharedPreferences(SAVE_FILE_NAME,
                MODE_MULTI_PROCESS).edit();
        share_edit.putString("attendancestarttime", attendancestarttime);
        share_edit.commit();
        return true;
    }
    
	public boolean onOptionsItemSelected(MenuItem item) {  
	    switch (item.getItemId()) {  
	         case android.R.id.home:  
	     		if(mType==ATTENDANCE_TYPE_START){
                	Intent intent = new Intent(WorkAttendanceActivity.this,LoginActivity.class);
                	startActivity(intent);
	     		}else if(mType==ATTENDANCE_TYPE_END){
                	Intent intent = new Intent(WorkAttendanceActivity.this,MainActivity.class);
                	startActivity(intent);
	    		}
	             finish();  
	             break;    
	        default:  
	             break;  
	    }  
	    return super.onOptionsItemSelected(item);  
	  }  
}
