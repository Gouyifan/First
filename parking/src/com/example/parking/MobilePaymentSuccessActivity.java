package com.example.parking;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MobilePaymentSuccessActivity extends Activity {
	private static final int EVENT_PAYMENT_SUCCESS= 101;
	private static final int PAYMENT_TYPE_CASH=201;
	private static final int PAYMENT_TYPE_ALIPAY=202;
	private static final int PAYMENT_TYPE_WECHATPAY=203;
	private static final int PAYMENT_TYPE_MOBILE=204;
	private Button mPrintPreviewBT;
	private Button mPaymentSuccessBT;
    private String mLicensePlateNumber;
	private int mLocationNumber;
	private String mCarType;
	private String mParkType;
	private String mStartTime;
	private String mLeaveTime;
	@Override  
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.activity_payment_success);
    	Intent intent = getIntent();
    	mLicensePlateNumber=intent.getExtras().getString("licenseplate");
 		mLocationNumber = intent.getExtras().getInt("locationnumber");
 		mCarType =  intent.getExtras().getString("cartype");
 		mParkType = intent.getExtras().getString("parktype");
 		mStartTime = intent.getExtras().getString("starttime");
 		mLeaveTime = intent.getExtras().getString("leavetime");
        mPrintPreviewBT=(Button)findViewById(R.id.bt_print_preview_mobile_payment_success);
        mPaymentSuccessBT=(Button)findViewById(R.id.bt_finish_payment_success);
        mPrintPreviewBT.setOnClickListener(new OnClickListener(){
        	@Override
        	public void onClick(View v){
        		Intent intent = new Intent(MobilePaymentSuccessActivity.this,PrintPreviewActivity.class);
        		Bundle bundle = new Bundle();
        		bundle.putInt("paytype", PAYMENT_TYPE_MOBILE);
        		bundle.putString("licenseplate", mLicensePlateNumber);
        		bundle.putInt("locationnumber", mLocationNumber);
        		bundle.putString("cartype", mCarType);
        		bundle.putString("parktype", mParkType);
        		bundle.putString("starttime", mStartTime);
        		bundle.putString("leavetime", mLeaveTime);
        		intent.putExtras(bundle);
        		startActivity(intent);
        		finish();
        	}
        });
        mPaymentSuccessBT.setOnClickListener(new OnClickListener(){
        	@Override
        	public void onClick(View v){
				Message msg = new Message();
                msg.what = EVENT_PAYMENT_SUCCESS;
                mHandler.sendMessage(msg);
        		Intent intent = new Intent(MobilePaymentSuccessActivity.this,MainActivity.class);
        		startActivity(intent);
        		finish();
        	}
        });
	}

	private Handler mHandler = new Handler() {
        @Override
        public void handleMessage (Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case EVENT_PAYMENT_SUCCESS:
                	Toast.makeText(getApplicationContext(), "收款成功", Toast.LENGTH_SHORT).show();
                default:
                    break;
            }
        }
    };
}
