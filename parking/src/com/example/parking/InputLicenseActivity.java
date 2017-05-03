package com.example.parking;

import com.example.parking.R.color;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.InputType;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class InputLicenseActivity extends FragmentActivity {
	private static final int LICENSE_PLATE_NUMBER_SIZE=7;
	private static final int ARRIVING_TYPE=101;
	private static final int LEAVING_TYPE=102;
	private static final int EVENT_UNFINISHED_LICENSE_PLATE=201;
	private static final int EVENT_ESCAPE_LICENSE_PLATE=202;
	private static final int EVENT_ENTER_PARK_INFORMATION=203;
	private static final int EVENT_NOT_EXISTS_LICENSE_PLATE=204;
	private static final int EVENT_INVALID_LICENSE_PLATE=205;
	private static final int EVENT_SCAN_STATE_NOTIFY=301;
	private Fragment mNumberFragment;
	private Fragment mLetterFragment;
	private Fragment mLocationFragment;
	private EditText mLicensePlateET;
	private TextView mNumberTV;
	private TextView mLetterTV;
	private TextView mLocationTV;
	private Button mScanBT;
	private Button mNextBT;
	private int mCurrentId;
	private int mType;
	private DBAdapter mDBAdapter;
	private OnClickListener mTabClickListener = new OnClickListener() {
        @Override  
        public void onClick(View v) {  
            if (v.getId() != mCurrentId) {//如果当前选中跟上次选中的一样,不需要处理  
                changeSelect(v.getId());//改变图标跟文字颜色的选中   
                changeFragment(v.getId());//fragment的切换  
                mCurrentId = v.getId();//设置选中id  
            }  
        }  
    };  
	@Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
    	mDBAdapter = new DBAdapter(this);
        Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		if(bundle.getInt("type")==ARRIVING_TYPE){
			mType=ARRIVING_TYPE;
		}else if(bundle.getInt("type")==LEAVING_TYPE){
			mType=LEAVING_TYPE;
		}
        setContentView(R.layout.activity_input_license);
        mLicensePlateET = (EditText) findViewById(R.id.et_license_plate);
        mLetterTV = (TextView) findViewById(R.id.tv_letter);
        mNumberTV = (TextView) findViewById(R.id.tv_number);
        mLocationTV = (TextView) findViewById(R.id.tv_location);
    	mScanBT = (Button) findViewById(R.id.bt_scan);
    	mNextBT = (Button) findViewById(R.id.bt_next);
        mLetterTV.setOnClickListener(mTabClickListener); 
    	mNumberTV.setOnClickListener(mTabClickListener);
    	mLocationTV.setOnClickListener(mTabClickListener);
    	changeSelect(R.id.tv_location);
    	changeFragment(R.id.tv_location);
    	mLicensePlateET.setOnTouchListener(new OnTouchListener() {	 
    	      @Override
    	      public boolean onTouch(View v, MotionEvent event) {
    	        // et.getCompoundDrawables()得到一个长度为4的数组，分别表示左右上下四张图片
    	        Drawable drawable = mLicensePlateET.getCompoundDrawables()[2];
    	        //如果右边没有图片，不再处理
    	        if (drawable == null)
    	            return false;
    	        //如果不是按下事件，不再处理
    	        if (event.getAction() != MotionEvent.ACTION_UP)
    	            return false;
    	        if (event.getX() > mLicensePlateET.getWidth()
                   -mLicensePlateET.getPaddingRight()
    	           - drawable.getIntrinsicWidth()){
    	        	mLicensePlateET.setText("");
    	        }
    	          return false;
    	      }
    	    });
    	mScanBT.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v){
				Message msg = new Message();
				msg.what= EVENT_SCAN_STATE_NOTIFY;
				mHandler.sendMessage(msg);
			}
		});
    	mLicensePlateET.setInputType(InputType.TYPE_NULL);  
    	mNextBT.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v){
				if(mLicensePlateET.getText().length() !=LICENSE_PLATE_NUMBER_SIZE){
            		Message msg = new Message();
            		msg.what=EVENT_INVALID_LICENSE_PLATE;
            		mHandler.sendMessage(msg);
            		return;
				}
				new SQLThread().start();
			}
		});
    	getActionBar().setDisplayHomeAsUpEnabled(true); 
	}

	private void changeFragment(int resId) {  
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();//开启一个Fragment事务  
        hideFragments(transaction);//隐藏所有fragment  
        if(resId==R.id.tv_letter){
            if(mLetterFragment==null){//如果为空先添加进来.不为空直接显示  
            	mLetterFragment = new LetterFragment();  
                transaction.add(R.id.main_container,mLetterFragment);  
            }else {  
                transaction.show(mLetterFragment);  
            }
        }else if(resId==R.id.tv_number){
            if(mNumberFragment==null){//如果为空先添加进来.不为空直接显示  
            	mNumberFragment = new NumberFragment();  
                transaction.add(R.id.main_container,mNumberFragment);  
            }else {  
                transaction.show(mNumberFragment);  
            }
        }else if(resId==R.id.tv_location){
            if(mLocationFragment==null){//如果为空先添加进来.不为空直接显示  
            	mLocationFragment = new LocationFragment();  
                transaction.add(R.id.main_container,mLocationFragment);  
            }else {  
                transaction.show(mLocationFragment);  
            }
        }
        transaction.commit();//一定要记得提交事务  
    }

	private void hideFragments(FragmentTransaction transaction){  
        if (mLetterFragment != null)  
            transaction.hide(mLetterFragment);
        if (mNumberFragment != null)
            transaction.hide(mNumberFragment);
        if (mLocationFragment != null)
            transaction.hide(mLocationFragment);
    }

	private void changeSelect(int resId) {  
		mLetterTV.setSelected(false);
		mLetterTV.setBackgroundResource(R.color.gray);
		mNumberTV.setSelected(false);
		mNumberTV.setBackgroundResource(R.color.gray);
		mLocationTV.setSelected(false);
		mLocationTV.setBackgroundResource(R.color.gray);
        switch (resId) {  
        case R.id.tv_letter:  
        	mLetterTV.setSelected(true);  
        	mLetterTV.setBackgroundResource(R.color.orange);
            break;  
        case R.id.tv_number:  
        	mNumberTV.setSelected(true);  
        	mNumberTV.setBackgroundResource(R.color.orange);
            break;
        case R.id.tv_location:  
        	mLocationTV.setSelected(true);  
        	mLocationTV.setBackgroundResource(R.color.orange);
            break;  
        }  
    }

    public class SQLThread extends Thread {
        @Override
        public void run () {
        	mDBAdapter.open();
        	Cursor cursor = mDBAdapter.getParkingByLicensePlate(mLicensePlateET.getText().toString());
        	if(cursor.getCount() == 0){
        		if(mType==ARRIVING_TYPE){
            		Message msg = new Message();
            		msg.what=	EVENT_ENTER_PARK_INFORMATION;
            		mHandler.sendMessage(msg);
        		}else if(mType==LEAVING_TYPE){
            		Message msg = new Message();
            		msg.what=	EVENT_NOT_EXISTS_LICENSE_PLATE;
            		mHandler.sendMessage(msg);
        		}
        	}
            try {
            	cursor.moveToFirst();
            	if(mType==ARRIVING_TYPE){
                	if(cursor.getString(cursor.getColumnIndex("paymentpattern")).equals("未付")){
                		Message msg = new Message();
                		msg.what=EVENT_UNFINISHED_LICENSE_PLATE;
                		mHandler.sendMessage(msg);
                	}else if(cursor.getString(cursor.getColumnIndex("paymentpattern")).equals("逃费")){
                		Message msg = new Message();
                		msg.what=EVENT_ESCAPE_LICENSE_PLATE;
                		mHandler.sendMessage(msg);
                	}else{
                		Message msg = new Message();
                		msg.what=	EVENT_ENTER_PARK_INFORMATION;
                		mHandler.sendMessage(msg);
                	}	
            	}else if(mType==LEAVING_TYPE){
                	if(cursor.getString(cursor.getColumnIndex("paymentpattern")).equals("未付")){
    					Intent intent = new Intent(InputLicenseActivity.this,LeavingActivity.class);
    					Bundle bundle = new Bundle();
    					bundle.putString("licensePlate",mLicensePlateET.getText().toString() );
    					intent.putExtras(bundle);
    					startActivity(intent);
                	}else if(cursor.getString(cursor.getColumnIndex("paymentpattern")).equals("现金支付") ||
                			cursor.getString(cursor.getColumnIndex("paymentpattern")).equals("移动支付") ||
                			cursor.getString(cursor.getColumnIndex("paymentpattern")).equals("逃费")) {
                		Message msg = new Message();
                		msg.what=	EVENT_NOT_EXISTS_LICENSE_PLATE;
                		mHandler.sendMessage(msg);
                	}
            	}
            }
            catch (Exception e) {
                    e.printStackTrace();
            } finally{
                	if(cursor!=null){
                		cursor.close();
                    }
            }
        }
    }

	private Handler mHandler = new Handler() {
        @Override
        public void handleMessage (Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case EVENT_UNFINISHED_LICENSE_PLATE:
                	Toast.makeText(getApplicationContext(), "该车辆已在场内", Toast.LENGTH_SHORT).show();
                	break;
                case EVENT_ESCAPE_LICENSE_PLATE:
                	Toast.makeText(getApplicationContext(), "逃费用户禁止入场", Toast.LENGTH_SHORT).show();
                	break;
                case EVENT_ENTER_PARK_INFORMATION:
					Intent intent = new Intent(InputLicenseActivity.this,ParkingInformationActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString("licensePlate",mLicensePlateET.getText().toString() );
					intent.putExtras(bundle);
					startActivity(intent);
					break;
                case EVENT_NOT_EXISTS_LICENSE_PLATE:
                	Toast.makeText(getApplicationContext(), "场内无此用户", Toast.LENGTH_SHORT).show();
                	break;
                case EVENT_SCAN_STATE_NOTIFY:
                    Toast.makeText(getApplicationContext(), "扫码功能开发中", Toast.LENGTH_SHORT).show();
            	    break;
                case EVENT_INVALID_LICENSE_PLATE:
                    Toast.makeText(getApplicationContext(), "请输入正确牌照", Toast.LENGTH_SHORT).show();
            	    break;
                default:
                    break;
            }
        }
    };
    
	public boolean onOptionsItemSelected(MenuItem item) {  
	    switch (item.getItemId()) {  
	         case android.R.id.home:  
	             finish();  
	             break;    
	        default:  
	             break;  
	    }  
	    return super.onOptionsItemSelected(item);  
	  }  
}
