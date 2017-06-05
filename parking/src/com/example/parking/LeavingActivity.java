package com.example.parking;


import com.example.parking.TestLeavingActivity.TimeThread;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.view.MenuItem;
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
	private TextView mUserNumberTV;;
	private TextView mLicensePlateNumberTV;
	private TextView mStartTimeTV;
	private TextView mLeaveTimeTV;
	private TextView mFeeScaleTV;
	private TextView mExpenseTV;
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
	private String mExpense;
	private Context mContext;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_leaving);
		mContext=this;
		mDBAdapter = new DBAdapter(this);
		mUserNumberTV=(TextView)findViewById(R.id.tv_user_number_leaving);
		mUserNumberTV.setText("工号:" + this.getString(R.string.user_number_fixed));
		mLicensePlateNumberTV=(TextView)findViewById(R.id.tv_license_number_leaving);
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		mLicensePlateNumber = bundle.getString("licensePlate");
		mLicensePlateNumberTV.setText("牌照:" + mLicensePlateNumber);
		mStartTimeTV=(TextView)findViewById(R.id.tv_start_time_leaving);
		mLeaveTimeTV=(TextView)findViewById(R.id.tv_leave_time_leaving);
		mFeeScaleTV=(TextView)findViewById(R.id.tv_fee_Scale_leaving);
		mFeeScaleTV.setText("收费标准:" + this.getString(R.string.fee_scale_fixed));
		mExpenseTV=(TextView)findViewById(R.id.tv_expense_leaving);
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
			    mConfirmPaymentBT.setEnabled(true);
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
		mConfirmPaymentBT.setEnabled(false);
		mConfirmPaymentBT.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				if(CheckPaymentState()){
    				Message msg = new Message();
                    msg.what = EVENT_PAYMENT_FINISHED;
                    mHandler.sendMessage(msg);
                    Intent intentBack = new Intent();
                    intentBack.setAction("BackMain");
                    sendBroadcast(intentBack);
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
            		bundle.putString("expense", mExpense);
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
            		bundle.putString("expense", mExpense);
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
        getActionBar().setDisplayHomeAsUpEnabled(true);
		new SQLThread().start();
        IntentFilter filter = new IntentFilter();  
        filter.addAction("ExitApp");  
        filter.addAction("BackMain");  
        registerReceiver(mReceiver, filter); 
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
            	//if(cursor.getString(cursor.getColumnIndex("paymentpattern")).equals("未付")){
       	             mCurrentRowID = cursor.getLong(cursor.getColumnIndex("_id"));
      		         String startTime=cursor.getString(cursor.getColumnIndex("starttime"));
      		         mStartTimeTV.setText("入场：" + startTime);
      		         if(cursor.getString(cursor.getColumnIndex("leavetime"))!=null){
          		         String leaveTime=cursor.getString(cursor.getColumnIndex("leavetime"));
          		       mLeaveTimeTV.setText("离场：" + leaveTime);
          		       mLeaveTime = mLeaveTimeTV.getText().toString();
      		         }else{
          		         CharSequence sysTimeStr = DateFormat.format("yyyy-MM-dd HH:mm:ss", System.currentTimeMillis());
          		         mLeaveTimeTV.setText("离场：" + sysTimeStr);
          		         mLeaveTime = sysTimeStr.toString();
      		         }
      		          mLocationNumber =  cursor.getInt(cursor.getColumnIndex("locationnumber"));
      		          mCarType = cursor.getString(cursor.getColumnIndex("cartype"));
      		          mParkType = cursor.getString(cursor.getColumnIndex("parkingtype"));
      		          if(mParkType.equals("免费停车")){
      		        	mExpense=mContext.getString(R.string.free_expense_fixed);
      		    		mExpenseTV.setText("费用总计:" +  mContext.getString(R.string.free_expense_fixed));
      		          }else if(mParkType.equals("普通停车")){
        		        mExpense=mContext.getString(R.string.expense_fixed);
      		        	mExpenseTV.setText("费用总计:" +  mContext.getString(R.string.expense_fixed));
      		          }
      		          mStartTime = cursor.getString(cursor.getColumnIndex("starttime"));
            	//}
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
            	if(mDBAdapter.updateParking(mCurrentRowID, mLeaveTimeTV.getText().toString().replace("离场：",""), mExpense, "现金支付")){
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
            		bundle.putString("expense", mExpense);
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
            	if(mDBAdapter.updateParking(mCurrentRowID, mLeaveTimeTV.getText().toString().replace("离场：",""), mContext.getString(R.string.free_expense_fixed), "逃费")){
    				Message msg = new Message();
                    msg.what = EVENT_ESCAPE_RECORD_SUCCESS;
                    mHandler.sendMessage(msg);
                    Intent intentBack = new Intent();
                    intentBack.setAction("BackMain");
                    sendBroadcast(intentBack);
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
	/**
	 * Add for request to update parking record and finish parking
	public void requestUpdate()throws ParseException, IOException, JSONException{
		  HttpClient httpClient = new DefaultHttpClient();
		  String strurl = "//此处url待定";
		  HttpPost request = new HttpPost(strurl);
		  request.addHeader("Accept","application/json");
		  request.addHeader("Content-Type","application/json");//还可以自定义增加header
		  JSONObject param = new JSONObject();//定义json对象
		  param.put("type", "update");
		  param.put("licenseplatenumber", mLicensePlateNumber);
		  param.put("leavetime", mLeaveTime);
		  param.put("paymentpattern", mPaymentPattern);
		  param.put("expense", mExpense);
		  Log.e("yifan", param.toString());
		  StringEntity se = new StringEntity(param.toString());
		  request.setEntity(se);//发送数据
		  HttpResponse httpResponse = httpClient.execute(request);//获得响应
		  int code = httpResponse.getStatusLine().getStatusCode();
		  if(code==HttpStatus.SC_OK){
			  String strResult = EntityUtils.toString(httpResponse.getEntity());
			  String updateResult = (String) result.get("updateresult");
		  }else{
			  Log.e("yifan", Integer.toString(code));
		  }
		 }
	//Client's json:{ "type":"update", "licenseplatenumber":"津HG025", "leavetime":"2017-05-04 16:50:25", "paymentpattern":"逃费", "expense":"0元" }
    //Server's json:{"updateResult":"ok"}
    //Server's json:{"updateResult":"fail"}
	*/
}
