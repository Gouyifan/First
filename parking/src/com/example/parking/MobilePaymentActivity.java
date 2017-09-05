package com.example.parking;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MobilePaymentActivity extends FragmentActivity {
    private final static int SCANNIN_GREQUEST_CODE = 101; 
	private static final int PAYMENT_TYPE_ALIPAY=202;
	private static final int PAYMENT_TYPE_WECHATPAY=203;
	private Fragment mPaymentFragment;
	private TextView mTwoDimensionsCodeTitleTV;
	private TextView mScanTitleTV;
	private int mCurrentId;
		private String mLicensePlateNumber;
		private int mLocationNumber;
		private String mCarType;
		private String mParkType;
		private String mStartTime;
		private String mLeaveTime;
		private String mExpense;
	private int mPayType;
    private DBAdapter mDBAdapter;
    private long mCurrentRowID;
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
        Intent intent = getIntent();
    	mLicensePlateNumber=intent.getExtras().getString("licenseplate");
 		mLocationNumber = intent.getExtras().getInt("locationnumber");
 		mCarType =  intent.getExtras().getString("cartype");
 		mParkType = intent.getExtras().getString("parktype");
 		mStartTime = intent.getExtras().getString("starttime");
 		mLeaveTime = intent.getExtras().getString("leavetime");
		mExpense=intent.getExtras().getString("expense");
        mPayType = intent.getExtras().getInt("paytype");
    	mDBAdapter = new DBAdapter(this);
        setContentView(R.layout.activity_mobile_payment);
        mTwoDimensionsCodeTitleTV = (TextView) findViewById(R.id.tv_mobile_payment_two_dimensions_code);
        mScanTitleTV = (TextView) findViewById(R.id.tv_mobile_payment_scan);
        mTwoDimensionsCodeTitleTV.setOnClickListener(mTabClickListener);
        mScanTitleTV.setOnClickListener(mTabClickListener); 
        changeSelect(R.id.tv_mobile_payment_two_dimensions_code);
        changeFragment(R.id.tv_mobile_payment_two_dimensions_code);
		getActionBar().setDisplayHomeAsUpEnabled(true);
        IntentFilter filter = new IntentFilter();  
        filter.addAction("ExitApp");  
        filter.addAction("BackMain");  
        registerReceiver(mReceiver, filter); 
	}

	private void changeSelect(int resId) {  
		mTwoDimensionsCodeTitleTV.setSelected(false);
		mTwoDimensionsCodeTitleTV.setBackgroundResource(R.color.gray);
		mScanTitleTV.setSelected(false);  
		mScanTitleTV.setBackgroundResource(R.color.gray);
        switch (resId) {  
        case R.id.tv_mobile_payment_two_dimensions_code:  
        	mTwoDimensionsCodeTitleTV.setSelected(true);  
        	mTwoDimensionsCodeTitleTV.setBackgroundResource(R.color.orange);
            break;  
        case R.id.tv_mobile_payment_scan:  
        	mScanTitleTV.setSelected(true);  
        	mScanTitleTV.setBackgroundResource(R.color.orange);
        	//Toast.makeText(getApplicationContext(), "扫码收款功能开发中", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();  
            intent.setClass(MobilePaymentActivity.this, CaptureActivity.class);  
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  
            startActivityForResult(intent, SCANNIN_GREQUEST_CODE); 
            break;
        }  
    }

	private void changeFragment(int resId) {  
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();//开启一个Fragment事务  
        hideFragments(transaction);//隐藏所有fragment  
        if(resId==R.id.tv_mobile_payment_two_dimensions_code){
        	mPaymentFragment = new MobilePaymentFragment(mPayType);
       	    transaction.replace(R.id.mobile_payment_container, mPaymentFragment);
        }else if(resId==R.id.tv_mobile_payment_scan){
             //TODO
        }
        transaction.commit();//一定要记得提交事务  
    }

	private void hideFragments(FragmentTransaction transaction){  
        if (mPaymentFragment != null) {
        	transaction.hide(mPaymentFragment);
        }
    }
	
    @Override  
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
        super.onActivityResult(requestCode, resultCode, data);  
        switch (requestCode) {  
        case SCANNIN_GREQUEST_CODE:  
            if(resultCode == RESULT_OK){  
                Bundle bundle = data.getExtras();  
                new SQLThread().start();
                //显示扫描到的内容  
                //mTextView.setText(bundle.getString("result"));  
                //显示  
                //mImageView.setImageBitmap((Bitmap) data.getParcelableExtra("bitmap"));  
            }  
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
	
    private BroadcastReceiver mReceiver = new BroadcastReceiver(){  
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction()!=null && intent.getAction().equals("ExitApp")){
				finish();
			}else if(intent.getAction()!=null && intent.getAction().equals("BackMain")){
				finish();
			}
		}            
    }; 
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }
    
    public class SQLThread extends Thread {
        @Override
        public void run () {
        	mDBAdapter.open();
        	Cursor cursor = mDBAdapter.getParkingByLicensePlate(mLicensePlateNumber);
            try {
            	cursor.moveToFirst();
            	if(cursor.getString(cursor.getColumnIndex("paymentpattern")).equals("未付")){
       	             mCurrentRowID = cursor.getLong(cursor.getColumnIndex("_id"));
       	          if(mDBAdapter.updateParking(mCurrentRowID, mLeaveTime, mExpense, "移动支付")){
  	        		Intent intent = new Intent(MobilePaymentActivity.this, MobilePaymentSuccessActivity.class);
  	        		Bundle bundle = new Bundle();
            		bundle.putString("licenseplate", mLicensePlateNumber);
            		bundle.putInt("locationnumber", mLocationNumber);
            		bundle.putString("cartype", mCarType);
            		bundle.putString("parktype", mParkType);
            		bundle.putString("starttime", mStartTime);
            		bundle.putString("leavetime", mLeaveTime);
            		bundle.putString("expense", mExpense);
            		intent.putExtras(bundle);
	        		startActivity(intent);
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
}
