package com.example.parking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.R.color;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ParkingSpaceActivity extends Activity {
	private ProgressBar mProgressBar;
	private ListView mListView;
	private TextView mlicenseplatenumber;
	private String mLicenseNumber;
	private DBAdapter mDBAdapter;
	private TextView mTotalParkingNumberTV;
	private TextView mIdleParkingNumberTV;
	private  static final int MAX_LOCATION_SIZE = 10;
	private int mIdleLocationNumber = 10;
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_parking_space_management);
		mDBAdapter = new DBAdapter(this);
		mProgressBar=(ProgressBar) findViewById(R.id.progressBar_horizontal_read_data);
		mProgressBar.setMax(MAX_LOCATION_SIZE);//设置进度条最大值
		mTotalParkingNumberTV=(TextView)findViewById(R.id.tv_total_parking_number);
		mTotalParkingNumberTV.setText("车位总数：" + MAX_LOCATION_SIZE);
		mIdleParkingNumberTV=(TextView)findViewById(R.id.tv_idle_parking_number);
		mListView=(ListView)findViewById(R.id.list_parking_detail);  
        List<Map<String, Object>> list=getData();  
        mListView.setAdapter(new ParkingPlaceListAdapter(this, list)); 
        mlicenseplatenumber=(TextView)findViewById(R.id.tv_licenseplatenumber);
        mListView.setOnItemClickListener(new OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                    long arg3) {
                // TODO Auto-generated method stub
            	Map<String,Object> map=(Map<String,Object>)mListView.getItemAtPosition(arg2);
                String licensePlateNumber=(String)map.get("licensePlateNumber");
                int locationNumber=Integer.valueOf(map.get("parkingNumber").toString());
                if(licensePlateNumber==null){
                	Intent intent = new Intent(ParkingSpaceActivity.this,TodayRecordActivity.class);
	        	    Bundle bundle = new Bundle();
	        	    bundle.putString("licensePlateNumber", licensePlateNumber);
	        	    bundle.putInt("locationNumber", locationNumber);
	        	    intent.putExtras(bundle);
	        	    startActivity(intent);
                }else{
                	Intent intent = new Intent(ParkingSpaceActivity.this,ParkingSpaceDetailActivity.class);
	        	    Bundle bundle = new Bundle();
	        	    bundle.putString("licensePlateNumber", licensePlateNumber);
	        	    bundle.putInt("locationNumber", locationNumber);
	        	    intent.putExtras(bundle);
	        	    startActivity(intent);
                }
            }
        });
	}
    public List<Map<String, Object>> getData(){  
        List<Map<String, Object>> list=new ArrayList<Map<String,Object>>();  
        for (int i = 1; i <= MAX_LOCATION_SIZE; i++) {  
            Map<String, Object> map=new HashMap<String, Object>();  
            map.put("parkingNumber",  i+"");
            String licenseNumber = getLicenseNumber(i);
            map.put("licensePlateNumber", licenseNumber);
            if(licenseNumber!=null){
            	mIdleLocationNumber--;
                map.put("backgroundColor", R.color.orange);
                map.put("textColor", R.color.white);
            }else{
                map.put("backgroundColor", R.color.white);
                map.put("textColor", R.color.orange);
            }
            list.add(map);  
        }
        mProgressBar.setProgress(mIdleLocationNumber);
        mIdleParkingNumberTV.setText("空闲车位：" + mIdleLocationNumber);
        return list;  
    }
    
    public String getLicenseNumber(int locationNumber){
    	mDBAdapter.open();
    	mLicenseNumber=null;
    	Cursor cursor = mDBAdapter.getParkingByLocationNumber(locationNumber);
        try {
        	      cursor.moveToFirst();
        	      if(cursor.getString(cursor.getColumnIndex("paymentpattern")).equals("未付")){
      		          mLicenseNumber =  cursor.getString(cursor.getColumnIndex("licenseplate"));
        	      }
        }
        catch (Exception e) {
                e.printStackTrace();
        } finally{
            	if(cursor!=null){
            		cursor.close();
                }
        }
    	return mLicenseNumber;
    }
/*	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		//获取屏幕大小，以合理设定 按钮 大小及位置
		  DisplayMetrics dm = new DisplayMetrics();
		  getWindowManager().getDefaultDisplay().getMetrics(dm);
		  int width = dm.widthPixels;
		  int height = dm.heightPixels;
		  RelativeLayout layout = new RelativeLayout(this); 
		//这里创建16个按钮，每行放置4个按钮
		  Button Btn[] = new Button[16];
		  int j = -1;
		  for  (int i=1; i<=16; i++) {     
		        Btn[i-1]=new Button(this);
		        Btn[i-1].setId(1000+i);
		        Btn[i-1].setText("车位"+i);  
		        RelativeLayout.LayoutParams btParams = new RelativeLayout.LayoutParams ((width-50)/4,(height-120)/4);  //设置按钮的宽度和高度
		        if ((i-1)%4 == 0) {
		            j++;
		        }
		        btParams.leftMargin = 10+ ((width-50)/4+10)*((i-1)%4);   //横坐标定位       
		        btParams.topMargin = ( (height-120)/4)*j;   //纵坐标定位      
		        layout.addView(Btn[i-1],btParams);   //将按钮放入layout组件
		  }
		      this.setContentView(layout);
		    //批量设置监听
		      for (int k = 0; k <= Btn.length-1; k++) {
		       Btn[k].setTag(k);                //为按钮设置一个标记，来确认是按下了哪一个按钮
		       Btn[k].setOnClickListener(new Button.OnClickListener() {
		        @Override
		            public void onClick(View v) {
		        	    Intent intent = new Intent(ParkingSpaceActivity.this,LeavingActivity.class);
		        	    Bundle bundle = new Bundle();
		        	    bundle.putInt("locationNumber", 5);
		        	    intent.putExtras(bundle);
		        	    startActivity(intent);
		           }
		         });
		       }
	}*/
}