package com.example.parking;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ParkingSpaceDetailActivity extends FragmentActivity {
	private Fragment mParkingInformationFragment;
	private Fragment mTodayRecordFragment;
	private TextView mParkingInformationTV;
	private TextView mRecordOfTodayTV;
	private int mCurrentId;
	private String mLicensePlateNumber;
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
        setContentView(R.layout.activity_parking_space_detail);
        mParkingInformationTV = (TextView) findViewById(R.id.parkingInformation);
        mRecordOfTodayTV = (TextView) findViewById(R.id.recordOfToday);
        mParkingInformationTV.setOnClickListener(mTabClickListener); 
        mRecordOfTodayTV.setOnClickListener(mTabClickListener);
        changeSelect(R.id.parkingInformation);
        changeFragment(R.id.parkingInformation);
		getActionBar().setDisplayHomeAsUpEnabled(true); 
	}

	private void changeFragment(int resId) {  
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();//开启一个Fragment事务  
        hideFragments(transaction);//隐藏所有fragment  
        if(resId==R.id.parkingInformation){
        	mParkingInformationFragment = new ParkingInformationFragment();
        	 transaction.replace(R.id.parking_container, mParkingInformationFragment);
/*            if(mParkingInformationFragment==null){//如果为空先添加进来.不为空直接显示  
            	mParkingInformationFragment = new ParkingInformationFragment();  
                transaction.add(R.id.parking_container,mParkingInformationFragment);  
            }else {  
            	transaction.replace(R.id.parking_container, mParkingInformationFragment);
            }*/
        }else if(resId==R.id.recordOfToday){
        	mTodayRecordFragment = new TodayRecordFragment();  
        	transaction.replace(R.id.parking_container, mTodayRecordFragment);
            /*if(mTodayRecordFragment==null){//如果为空先添加进来.不为空直接显示  
            	mTodayRecordFragment = new TodayRecordFragment();  
                transaction.add(R.id.parking_container,mTodayRecordFragment);  
            }else {  
                transaction.show(mTodayRecordFragment);  
            }*/
        }
        transaction.commit();//一定要记得提交事务  
    }

	private void hideFragments(FragmentTransaction transaction){  
        if (mParkingInformationFragment != null) {
        	transaction.hide(mParkingInformationFragment);
        }else if(mTodayRecordFragment!=null){
        	transaction.hide(mTodayRecordFragment);
        }
    }

	private void changeSelect(int resId) {  
		mParkingInformationTV.setSelected(false);
		mParkingInformationTV.setBackgroundResource(R.color.gray);
		mRecordOfTodayTV.setSelected(false);  
		mRecordOfTodayTV.setBackgroundResource(R.color.gray);
        switch (resId) {  
        case R.id.parkingInformation:  
        	mParkingInformationTV.setSelected(true);
        	mParkingInformationTV.setBackgroundResource(R.color.orange);
            break;  
        case R.id.recordOfToday:  
        	mRecordOfTodayTV.setSelected(true);
        	mRecordOfTodayTV.setBackgroundResource(R.color.orange);
            break;
        }  
    }
	
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
