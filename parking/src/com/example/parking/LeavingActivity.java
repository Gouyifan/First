package com.example.parking;


import com.example.parking.TestLeavingActivity.TimeThread;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class LeavingActivity extends Activity {
	private static final int EVENT_ESCAPE_RECORD_SUCCESS = 101;
	private static final int EVENT_RECORD_FAIL = 102;
	private static final int EVENT_CASH_RECORD_SUCCESS = 103;
	private static final int PAYMENT_TYPE_CASH=201;
	private static final int PAYMENT_TYPE_ALIPAY=202;
	private static final int PAYMENT_TYPE_WECHATPAY=203;
	private static final int PAYMENT_TYPE_MOBILE=204;
	private static final int EVENT_PAYMENT_FINISHED=205;
	//private Button mPrintBT;
	private TextView mLicensePlateNumberTV;
	private TextView mStartTimeTV;
	private TextView mLeaveTimeTV;
	private long mCurrentRowID;
	private String mLicensePlateNumber;
	private Button mConfirmPaymentBT;
	private Button mEscapeBT;
	private RadioGroup mPaymentTypeRG;
	private RadioButton  mCashPaymentTypeRB;
	private RadioButton mAlipayPaymentTypeRB;
	private RadioButton mWechatpayPaymentRB;;
	private int mPaymentType;
	private DBAdapter mDBAdapter;
	private int mLocationNumber;
	private String mCarType;
	private String mParkType;
	private String mStartTime;
	private String mLeaveTime;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_leaving);
		mDBAdapter = new DBAdapter(this);
		mLicensePlateNumberTV=(TextView)findViewById(R.id.tv_license_number_leaving);
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		mLicensePlateNumber = bundle.getString("licensePlate");
		mLicensePlateNumberTV.setText(mLicensePlateNumber);
		mStartTimeTV=(TextView)findViewById(R.id.tv_start_time_leaving);
		mLeaveTimeTV=(TextView)findViewById(R.id.tv_leave_time_leaving);
		mPaymentTypeRG=(RadioGroup)findViewById(R.id.rg_payment_type_leaving);
		mCashPaymentTypeRB=(RadioButton)findViewById(R.id.rb_cash_payment_leaving);
		mAlipayPaymentTypeRB=(RadioButton)findViewById(R.id.rb_alipay_payment_leaving);
		mWechatpayPaymentRB=(RadioButton)findViewById(R.id.rb_wechatpay_payment_leaving);
		mPaymentTypeRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() { 
			@Override public void onCheckedChanged(RadioGroup group, int checkedId){
			    if (mCashPaymentTypeRB.getId() == checkedId) {
			    	mPaymentType = PAYMENT_TYPE_CASH; 
		        }else if (mAlipayPaymentTypeRB.getId() == checkedId){
		        	mPaymentType = PAYMENT_TYPE_ALIPAY; 
			    }else if (mWechatpayPaymentRB.getId() == checkedId){
		        	mPaymentType = PAYMENT_TYPE_WECHATPAY; 
			    } 
			  } 
			});
/*		mPrintBT=(Button)findViewById(R.id.bt_print_leaving);
		mPrintBT.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				Intent intent = new Intent(LeavingActivity.this,PrintPreviewActivity.class);
				startActivity(intent);
			}
		});*/
		mConfirmPaymentBT=(Button)findViewById(R.id.bt_confirm_payment_leaving);
		mConfirmPaymentBT.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				if(CheckPaymentState()){
    				Message msg = new Message();
                    msg.what = EVENT_PAYMENT_FINISHED;
                    mHandler.sendMessage(msg);
					Intent intent = new Intent(LeavingActivity.this,MainActivity.class);
					startActivity(intent);
					return;
				}
				if(mPaymentType==PAYMENT_TYPE_CASH){
					showCashPaymentDialog();
				}else if(mPaymentType==PAYMENT_TYPE_ALIPAY){
					Intent intent = new Intent(LeavingActivity.this,MobilePaymentActivity.class);
					Bundle bundle = new Bundle();
					bundle.putInt("paytype", PAYMENT_TYPE_ALIPAY);
            		bundle.putString("licenseplate", mLicensePlateNumber);
            		bundle.putInt("locationnumber", mLocationNumber);
            		bundle.putString("cartype", mCarType);
            		bundle.putString("parktype", mParkType);
            		bundle.putString("starttime", mStartTime);
            		bundle.putString("leavetime", mLeaveTime);
            		intent.putExtras(bundle);
					intent.putExtras(bundle);
					startActivity(intent);
				}else if(mPaymentType==PAYMENT_TYPE_WECHATPAY){
					Intent intent = new Intent(LeavingActivity.this,MobilePaymentActivity.class);
					Bundle bundle = new Bundle();
					bundle.putInt("paytype", PAYMENT_TYPE_WECHATPAY);
            		bundle.putString("licenseplate", mLicensePlateNumber);
            		bundle.putInt("locationnumber", mLocationNumber);
            		bundle.putString("cartype", mCarType);
            		bundle.putString("parktype", mParkType);
            		bundle.putString("starttime", mStartTime);
            		bundle.putString("leavetime", mLeaveTime);
            		intent.putExtras(bundle);
					intent.putExtras(bundle);
					startActivity(intent);
				}
			}
		});
		mEscapeBT=(Button)findViewById(R.id.bt_cancel_payment);
		mEscapeBT.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				showEscapeDialog();
			}
		});
		new SQLThread().start();
	}

	private Handler mHandler = new Handler() {
        @Override
        public void handleMessage (Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case EVENT_ESCAPE_RECORD_SUCCESS:
                	Toast.makeText(getApplicationContext(), "逃费行为已记录", Toast.LENGTH_SHORT).show();
                	break;
                case EVENT_CASH_RECORD_SUCCESS:
                	Toast.makeText(getApplicationContext(), "现金支付成功", Toast.LENGTH_SHORT).show();
                	break;
                case EVENT_RECORD_FAIL:
                	Toast.makeText(getApplicationContext(), "记录失败", Toast.LENGTH_SHORT).show();
                	break;
                case EVENT_PAYMENT_FINISHED:
                	Toast.makeText(getApplicationContext(), "该订单已支付", Toast.LENGTH_SHORT).show();
                	break;
                default:
                    break;
            }
        }
    };

    public class SQLThread extends Thread {
        @Override
        public void run () {
        	mDBAdapter.open();
        	Cursor cursor = mDBAdapter.getParkingByLicensePlate(mLicensePlateNumber);
            try {
            	cursor.moveToFirst();
            	if(cursor.getString(cursor.getColumnIndex("paymentpattern")).equals("未付")){
       	             mCurrentRowID = cursor.getLong(cursor.getColumnIndex("_id"));
      		         String startTime=cursor.getString(cursor.getColumnIndex("starttime"));
      		         mStartTimeTV.setText("入场：" + startTime);
      		         CharSequence sysTimeStr = DateFormat.format("yyyy-MM-dd HH:mm:ss", System.currentTimeMillis());
      		         mLeaveTimeTV.setText("离场：" + sysTimeStr);
      		          mLocationNumber =  cursor.getInt(cursor.getColumnIndex("locationnumber"));
      		          mCarType = cursor.getString(cursor.getColumnIndex("cartype"));
      		          mParkType = cursor.getString(cursor.getColumnIndex("parkingtype"));
      		          mStartTime = cursor.getString(cursor.getColumnIndex("starttime"));
      		          mLeaveTime = sysTimeStr.toString();
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

    private void showCashPaymentDialog(){
        final AlertDialog.Builder cashPaymentDialog = new AlertDialog.Builder(LeavingActivity.this);
        cashPaymentDialog.setIcon(R.drawable.ic_car_leaving);
        cashPaymentDialog.setTitle("现金支付");
        cashPaymentDialog.setMessage("现金支付成功？");
        cashPaymentDialog.setPositiveButton("确定",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            	if(mDBAdapter.updateParking(mCurrentRowID, mLeaveTimeTV.getText().toString().replace("离场：",""), "5元", "现金支付")){
    				Message msg = new Message();
                    msg.what = EVENT_CASH_RECORD_SUCCESS;
                    mHandler.sendMessage(msg);
    				Intent intent = new Intent(LeavingActivity.this,PrintPreviewActivity.class);
            		Bundle bundle = new Bundle();
            		bundle.putInt("paytype", PAYMENT_TYPE_CASH);
            		bundle.putString("licenseplate", mLicensePlateNumber);
            		bundle.putInt("locationnumber", mLocationNumber);
            		bundle.putString("cartype", mCarType);
            		bundle.putString("parktype", mParkType);
            		bundle.putString("starttime", mStartTime);
            		bundle.putString("leavetime", mLeaveTime);
            		intent.putExtras(bundle);
    				startActivity(intent);
            	}else{
    				Message msg = new Message();
                    msg.what = EVENT_RECORD_FAIL;
                    mHandler.sendMessage(msg);
            	}
            }
        });
        cashPaymentDialog.setNegativeButton("关闭",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //...To-do
            }
        });
        cashPaymentDialog.show();
    }

    private void showEscapeDialog(){
        final AlertDialog.Builder escapeDialog = new AlertDialog.Builder(LeavingActivity.this);
        escapeDialog.setIcon(R.drawable.ic_car_leaving);
        escapeDialog.setTitle("逃费");
        escapeDialog.setMessage("该用户已逃费？");
        escapeDialog.setPositiveButton("确定",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            	if(mDBAdapter.updateParking(mCurrentRowID, mLeaveTimeTV.getText().toString().replace("离场：",""), "0元", "逃费")){
    				Message msg = new Message();
                    msg.what = EVENT_ESCAPE_RECORD_SUCCESS;
                    mHandler.sendMessage(msg);
    				Intent intent = new Intent(LeavingActivity.this,MainActivity.class);
    				startActivity(intent);
            	}else{
    				Message msg = new Message();
                    msg.what = EVENT_RECORD_FAIL;
                    mHandler.sendMessage(msg);
            	}
            }
        });
        escapeDialog.setNegativeButton("关闭",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //...To-do
            }
        });
        escapeDialog.show();
    }

    public boolean CheckPaymentState(){
    	boolean paymentState = false;
    	mDBAdapter.open();
    	Cursor cursor = mDBAdapter.getParkingByLicensePlate(mLicensePlateNumber);
        try {
        	      cursor.moveToFirst();
        	      if(cursor.getString(cursor.getColumnIndex("paymentpattern")).equals("未付")){
        	    	  paymentState= false;
        	      }else{
        	    	  paymentState= true;
        	      }
        }
        catch (Exception e) {
                e.printStackTrace();
        } finally{
            	if(cursor!=null){
            		cursor.close();
                }
        }
        return paymentState;
    }
}
