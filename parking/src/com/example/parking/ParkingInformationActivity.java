package com.example.parking;

import java.io.ByteArrayOutputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ParkingInformationActivity extends Activity {
	private final static int EVENT_DISPLAY_TIME = 101;
	private final static int EVENT_INSERT_SUCCESS = 102;
	private final static int EVENT_DUPLICATED_LOCATION_NUMBER = 103;
	private final static int TAKE_PHOTO =201;
	private TextView mParkNameTV;
	private TextView mParkNumberTV;
	private Spinner mCarType;
	private Spinner mParkingType;
	private Spinner mLocationNumber;
	private TextView mLicensePlateNumberTV;
	private TextView mStartTime;
	private Button mOkButton;
	private Button mCancelButton;
	private TextView mPhotoTitleTV;;
	private ImageView mPhotoIV;
	private DBAdapter mDBAdapter;
	private boolean mPermissionState=true;
    private Bitmap mPhoto = null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mDBAdapter = new DBAdapter(this);
		setContentView(R.layout.activity_parking_information);
		mParkNameTV = (TextView) findViewById(R.id.tv_parking_name);
		mParkNameTV.setText(R.string.park_name_fixed);
		mParkNumberTV = (TextView) findViewById(R.id.tv_parking_number);
		mParkNumberTV.setText("车场编号:" + this.getString(R.string.park_number_fixed));
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
		mCancelButton=(Button) findViewById(R.id.bt_camera_arriving);
		mCancelButton.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View v){
/*				finish();
				Intent intent = new Intent(ParkingInformationActivity.this,MainActivity.class);
				startActivity(intent);*/
				openTakePhoto();
			}
		});
		mPhotoTitleTV = (TextView)findViewById(R.id.tv_photo_title_arriving);
		mPhotoIV = (ImageView)findViewById(R.id.iv_photo_arriving);
		mPhotoIV.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View v){
				LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
				View imgEntryView = inflater.inflate(R.layout.dialog_photo_entry, null); // 加载自定义的布局文件
				final AlertDialog dialog = new AlertDialog.Builder(ParkingInformationActivity.this).create();
				ImageView img = (ImageView)imgEntryView.findViewById(R.id.iv_large_image);
				img.setImageBitmap(mPhoto);
				dialog.setView(imgEntryView); // 自定义dialog
				dialog.show();
				imgEntryView.setOnClickListener(new OnClickListener() {
				    public void onClick(View paramView) {
				        dialog.cancel();
				    }
			    });
			}
		});
		getActionBar().setDisplayHomeAsUpEnabled(true); 
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
             	mPermissionState=true;
             	if(cursor.getString(cursor.getColumnIndex("paymentpattern")).equals("未付")){
            		Message msg = new Message();
                    msg.what = EVENT_DUPLICATED_LOCATION_NUMBER;
                    mHandler.sendMessage(msg);
                    mPermissionState=false;
                    Log.e("yifan","p1 = " + mPermissionState);
             	}
             }
             catch (Exception e) {
                     e.printStackTrace();
             } finally{
                 	if(cursor!=null){
                 		cursor.close();
                     }
             }
        	 Log.e("yifan","p3 = " + mPermissionState);
        	 if(mPermissionState){
             	long  result = mDBAdapter.insertParking(licensePlate,mCarType.getSelectedItem().toString(),mParkingType.getSelectedItem().toString(),
             			 Integer.parseInt(mLocationNumber.getSelectedItem().toString()),startTime,leaveTime,expense,paymentPattern,converImageToByte(mPhoto));
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
	
	private void openTakePhoto(){
		 /**
		 * 在启动拍照之前最好先判断一下sdcard是否可用
		 */
		     String state = Environment.getExternalStorageState(); //拿到sdcard是否可用的状态码
		     if (state.equals(Environment.MEDIA_MOUNTED)){   //如果可用
		          Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
		          startActivityForResult(intent,TAKE_PHOTO);
		     }else {
		          Toast.makeText(ParkingInformationActivity.this,"sdcard不可用",Toast.LENGTH_SHORT).show();
		     }
		}
	
	@Override
	 protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	       super.onActivityResult(requestCode, resultCode, data);
	       if (data!= null) {
	           switch (requestCode) {
	               case TAKE_PHOTO: //拍摄图片并选择
	               if (data.getData() != null|| data.getExtras() != null){ //防止没有返回结果
	                   Uri uri =data.getData();
	                   if (uri != null) {
	                	   mPhoto =BitmapFactory.decodeFile(uri.getPath()); //拿到图片
	                   }
	                   if (mPhoto == null) {
	                       Bundle bundle =data.getExtras();
	                       if (bundle != null){
	                    	   mPhoto =(Bitmap) bundle.get("data");
	                       } else {
	                       Toast.makeText(getApplicationContext(), "找不到图片",Toast.LENGTH_SHORT).show();
	                        }
	                    }
	               }
	               if(mPhoto!=null){
		               mPhotoTitleTV.setVisibility(View.VISIBLE);
		               mPhotoIV.setVisibility(View.VISIBLE);
		               mPhotoIV.setImageBitmap(mPhoto);
	               }
	               break;
	          }
	      }
	   }
	


public byte[] converImageToByte(Bitmap bitmap) {
	    if(bitmap!=null){
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            return baos.toByteArray();
	    }else{
	    	return null;
	    }
    }

}

