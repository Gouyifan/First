package com.example.parking;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.example.parking.ParkingInformationActivity.TimeThread;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.util.Log;
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
	
    private LocationManager locationManager;  
    private String locationProvider; 
    private String mLocation;
    
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
  //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_attendance);
		mContext = this;
		//初始化定位
		mLocationClient = new AMapLocationClient(getApplicationContext());
		//设置定位回调监听
		mLocationClient.setLocationListener(mLocationListener);
		setUpMap();
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		mType = bundle.getInt("attendancetype");
		mParkNumberTV = (TextView)findViewById(R.id.tv_attendance_park_number);
		mParkNumberTV.setText("车场编号:" + this.getString(R.string.park_number_fixed));
		mUserNumberTV = (TextView)findViewById(R.id.tv_attendance_user_number);
		mUserNumberTV.setText("工号:" + this.getString(R.string.user_number_fixed) );
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
    /*Drawable drawable = getResources().getDrawable(R.drawable.ic_add_location_black_18dp);
		drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight()); //设置边界
        if(mType==ATTENDANCE_TYPE_START){
			mAttendanceStartLocationTV.setText(R.string.work_start_location_fixed);
			mAttendanceStartLocationTV.setCompoundDrawables(drawable, null, null, null);//画在左边
		}else if(mType==ATTENDANCE_TYPE_END){
			mAttendanceWorkStartTimeTV.setText(readData("attendancestarttime"));
			mAttendanceStartLocationTV.setText(readData("attendancestartlocation"));
			mAttendanceStartLocationTV.setCompoundDrawables(drawable, null, null, null);//画在左边
		    mAttendanceEndLocationTV.setText(R.string.work_end_location_fixed);
			mAttendanceEndLocationTV.setCompoundDrawables(drawable, null, null, null);//画在左边		
		}*/
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
    /*//获取地理位置管理器  
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);  
        //获取所有可用的位置提供器  
        List<String> providers = locationManager.getProviders(true);  
        if(providers.contains(LocationManager.GPS_PROVIDER)){  
            //如果是GPS  
            locationProvider = LocationManager.GPS_PROVIDER;  
        }else if(providers.contains(LocationManager.NETWORK_PROVIDER)){  
            //如果是Network  
            locationProvider = LocationManager.NETWORK_PROVIDER;  
        }else{  
            Toast.makeText(this, "没有可用的位置提供器", Toast.LENGTH_SHORT).show();  
            return ;  
        }  
        //获取Location  
        Location location = locationManager.getLastKnownLocation(locationProvider);  
        if(location!=null){  
            //不为空,显示地理位置经纬度  
            //showLocation(location);  
        }else{
    		if(mType==ATTENDANCE_TYPE_START){
            	mAttendanceStartLocationTV.setText(R.string.work_start_location_fixed);
    		}else if(mType==ATTENDANCE_TYPE_END){
    			mAttendanceEndLocationTV.setText(R.string.work_end_location_fixed);
    		}
        }
        //监视地理位置变化  
        locationManager.requestLocationUpdates(locationProvider, 3000, 1, locationListener);*/  
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
                	String startTime = "打卡时间:" + mAttendanceBT.getText().toString().replaceAll("上班打卡\n", "") + "(" 
                                       +	mContext.getString(R.string.work_start_time_fixed) + ")";
                	 String startLocation = mAttendanceStartLocationTV.getText().toString();
                	 writeData(startTime,startLocation);
    				 mAttendanceWorkStartTimeTV.setText(startTime);
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
                	finish();
                	break;
                case EVENT_EXIT_LOGIN:
                    Intent intentFinsh = new Intent();  
                    intentFinsh.setAction("ExitApp");  
                    sendBroadcast(intentFinsh); 
                	Intent intentLogin = new Intent(WorkAttendanceActivity.this,LoginActivity.class);
                	startActivity(intentLogin);
                	finish();
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

    private boolean writeData(String attendancestarttime,String attendancestartlocation) {
        SharedPreferences.Editor share_edit = getSharedPreferences(SAVE_FILE_NAME,
                MODE_MULTI_PROCESS).edit();
        share_edit.putString("attendancestarttime", attendancestarttime);
        share_edit.putString("attendancestartlocation", attendancestartlocation);
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
	
    @Override  
    protected void onDestroy() {  
        super.onDestroy();  
        if(locationManager!=null){  
            //移除监听器  
            locationManager.removeUpdates(locationListener);  
        }  
    }
    
    /** 
     * LocationListern监听器 
     * 参数：地理位置提供器、监听位置变化的时间间隔、位置变化的距离间隔、LocationListener监听器 
     */  
    LocationListener locationListener =  new LocationListener() {       
        @Override  
        public void onStatusChanged(String provider, int status, Bundle arg2) {  
             
        }           
        @Override  
        public void onProviderEnabled(String provider) {  
              
        }         
        @Override  
        public void onProviderDisabled(String provider) {  
              
        }          
        @Override  
        public void onLocationChanged(Location location) {  
            //如果位置发生变化,重新显示  
            //showLocation(location);              
        }  
    };
    
    /** 
     * 显示地理位置经度和纬度信息 
     * @param location 
     */  
    private void showLocation(Location location){  
        mLocation = "纬度:" + location.getLatitude() + "经度:" + location.getLongitude();  
		if(mType==ATTENDANCE_TYPE_START){
			mAttendanceStartLocationTV.setText(mLocation);  
		}else if(mType==ATTENDANCE_TYPE_END){
			mAttendanceEndLocationTV.setText(mLocation);  
		}
    }
    
    /**
     * 配置定位参数
     */
    private void setUpMap() {
     //初始化定位参数
     mLocationOption = new AMapLocationClientOption();
     //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
     mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
     //设置是否返回地址信息（默认返回地址信息）
     mLocationOption.setNeedAddress(true);
     //设置是否只定位一次,默认为false
     mLocationOption.setOnceLocation(false);
     //设置是否强制刷新WIFI，默认为强制刷新
     mLocationOption.setWifiActiveScan(true);
     //设置是否允许模拟位置,默认为false，不允许模拟位置
     mLocationOption.setMockEnable(false);
     //设置定位间隔,单位毫秒,默认为2000ms
     mLocationOption.setInterval(2000);
     //给定位客户端对象设置定位参数
     mLocationClient.setLocationOption(mLocationOption);
     //启动定位
     mLocationClient.startLocation();
    }
    
    /**
     * 声明定位回调监听器
     */
    public AMapLocationListener mLocationListener = new AMapLocationListener() {
     @Override
     public void onLocationChanged(AMapLocation amapLocation) {
        if (amapLocation != null) {
        	android.util.Log.d("yifan","error code is "  + amapLocation.getErrorCode());
           if (amapLocation.getErrorCode() == 0) {
            //定位成功回调信息，设置相关消息
                amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                amapLocation.getLatitude();//获取纬度
                amapLocation.getLongitude();//获取经度
                amapLocation.getAccuracy();//获取精度信息
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(amapLocation.getTime());
                df.format(date);//定位时间
                amapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                amapLocation.getCountry();//国家信息
                amapLocation.getProvince();//省信息
                amapLocation.getCity();//城市信息
                amapLocation.getDistrict();//城区信息
                amapLocation.getStreet();//街道信息
                amapLocation.getStreetNum();//街道门牌号信息
                amapLocation.getCityCode();//城市编码
                amapLocation.getAdCode();//地区编码
                amapLocation.getAoiName();//获取当前定位点的AOI信息
                Drawable drawable = getResources().getDrawable(R.drawable.ic_add_location_black_18dp);
        		drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight()); //设置边界
                if(mType==ATTENDANCE_TYPE_START){
                	String location = amapLocation.getProvince() + "" + amapLocation.getDistrict() + "" + amapLocation.getStreet() + "" + amapLocation.getStreetNum();
                	android.util.Log.d("yifan","location is "  + location);
    			    mAttendanceStartLocationTV.setText(location);
    			    mAttendanceStartLocationTV.setCompoundDrawables(drawable, null, null, null);//画在左边
    		    }else if(mType==ATTENDANCE_TYPE_END){
    			    mAttendanceWorkStartTimeTV.setText(readData("attendancestarttime"));
    			    mAttendanceStartLocationTV.setText(readData("attendancestartlocation"));
    			    mAttendanceStartLocationTV.setCompoundDrawables(drawable, null, null, null);//画在左边
    			    String location = amapLocation.getProvince() + "" + amapLocation.getDistrict() + "" + amapLocation.getStreet() + "" + amapLocation.getStreetNum();
    		        mAttendanceEndLocationTV.setText(location);
    			    mAttendanceEndLocationTV.setCompoundDrawables(drawable, null, null, null);//画在左边		
    		    }
           } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:" + amapLocation.getErrorCode() + ", errInfo:" + amapLocation.getErrorInfo());
           }
        }
     }
    };
	/**
	 * Add for request location's name and location's state
	public void requestLocationState()throws ParseException, IOException, JSONException{
		  HttpClient httpClient = new DefaultHttpClient();
		  String strurl = "//此处url待定";
		  HttpPost request = new HttpPost(strurl);
		  request.addHeader("Accept","application/json");
		  request.addHeader("Content-Type","application/json");//还可以自定义增加header
		  JSONObject param = new JSONObject();//定义json对象
		  param.put("type", "location");
		  param.put("location", mLocation);
		  Log.e("yifan", param.toString());
		  StringEntity se = new StringEntity(param.toString());
		  request.setEntity(se);//发送数据
		  HttpResponse httpResponse = httpClient.execute(request);//获得相应
		  int code = httpResponse.getStatusLine().getStatusCode();
		  if(code==HttpStatus.SC_OK){
			  String strtResult = EntityUtils.toString(httpResponse.getEntity());
			  JSONObject result = new JSONObject(strtResult);
			  String loginResult = (String) result.get("locationname");
			   String parkName = (String) result.get("locationstate");
		  }else{
			  Log.e("yifan", Integer.toString(code));
		  }
	}
     //Client's json:{ "type":"location", "location":"纬度:122经度:37"}
	//Server's json:{ "locationname":"天津市津南区易华录停车场", "locationstate":"考勤范围内"}
	//Server's json:{ "locationname":"天津市津南区碧桂园停车场", "locationstate":"考勤范围外"}
    */
    
    
	/**
	 * Add for clock
	public void clock()throws ParseException, IOException, JSONException{
		  HttpClient httpClient = new DefaultHttpClient();
		  String strurl = "//此处url待定";
		  HttpPost request = new HttpPost(strurl);
		  request.addHeader("Accept","application/json");
		  request.addHeader("Content-Type","application/json");//还可以自定义增加header
		  JSONObject param = new JSONObject();//定义json对象
		  param.put("type", "clock");
		  param.put("time", mAttendanceBT.getText().toString().replaceAll("上班打卡\n", ""));
		  param.put("locationname", mAttendanceStartLocationTV.getText).toString());
		  Log.e("yifan", param.toString());
		  StringEntity se = new StringEntity(param.toString());
		  request.setEntity(se);//发送数据
		  HttpResponse httpResponse = httpClient.execute(request);//获得相应
		  int code = httpResponse.getStatusLine().getStatusCode();
		  if(code==HttpStatus.SC_OK){
			  String strtResult = EntityUtils.toString(httpResponse.getEntity());
			  JSONObject result = new JSONObject(strtResult);
			  String clockResult = (String) result.get("clockresult");
		  }else{
			  Log.e("yifan", Integer.toString(code));
		  }
	}
     //Client's json:{ "type":"clock", “time”:"08:57:56", "locationname":"天津市津南区易华录停车场"}
	//Server's json:{ "clockresult":"ok"}
	//Server's json:{ "clockresult":"fail"}
    */
}
