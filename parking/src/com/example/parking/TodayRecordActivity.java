package com.example.parking;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

public class TodayRecordActivity extends Activity {
	public static final int NO_TODAY_PARKING_RECORD =101;
	private View mView;
	private ListView mListView;
	private int mLocationNumber;
	private DBAdapter mDBAdapter;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_today_record);
        mListView=(ListView)findViewById(R.id.list_record_today);  
        Intent intent = getIntent();
        mLocationNumber=intent.getExtras().getInt("locationNumber");
        mDBAdapter = new DBAdapter(this);
        List<Map<String, Object>> list=getData();  
        mListView.setAdapter(new TodayRecordListAdapter(this, list)); 
		getActionBar().setDisplayHomeAsUpEnabled(true); 
	}
	
	public List<Map<String, Object>> getData(){  
		SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd%"); 
		Date curDate = new Date(System.currentTimeMillis());
		String date = formatter.format(curDate);
        List<Map<String, Object>> list=new ArrayList<Map<String,Object>>();  
        setTodayRecord(date,mLocationNumber,list);
        return list;  
    }

    public void setTodayRecord(String date,int locationNumber,List<Map<String, Object>> list){
    	mDBAdapter.open();
    	Cursor cursor = mDBAdapter.getParkingByStartTime(date);
    	Map<String, Object> titleMap=new HashMap<String, Object>();
    	titleMap.put("licensePlateNumber","牌照号码");
    	titleMap.put("startTime","入场时间");
    	titleMap.put("leaveTime", "离场时间");
    	titleMap.put("paymentState","支付状态");
    	titleMap.put("expense", "支付金额");
        list.add(titleMap); 
    	Log.e("yifan", "count: " + cursor.getCount());
    	Log.e("yifan", "locationNumber: " + locationNumber);
        try {
        	int count = 0;
        	while(cursor.moveToNext()){
        	    	  Log.e("yifan", "dblocation: " + cursor.getInt(cursor.getColumnIndex("locationnumber")));
        	    	  if(cursor.getInt(cursor.getColumnIndex("locationnumber"))==locationNumber ){
        	    		  Log.e("yifan", "+1" );
        	    		  Map<String, Object> map=new HashMap<String, Object>();
        	    		  map.put("licensePlateNumber", cursor.getString(cursor.getColumnIndex("licenseplate")));
        	    		  map.put("startTime", "入场: " + cursor.getString(cursor.getColumnIndex("starttime")));
        	    		  if(cursor.getString(cursor.getColumnIndex("leavetime"))==null){
        	    			  map.put("leaveTime", null);
        	    		  }else{
	        	    		  map.put("leaveTime", "离场: " + cursor.getString(cursor.getColumnIndex("leavetime")));
        	    		  }
        	    		  map.put("paymentState", cursor.getString(cursor.getColumnIndex("paymentpattern")));
        	    		  map.put("expense", cursor.getString(cursor.getColumnIndex("expense")));
      		              list.add(map); 
      		            count++;
        	    	  }
        	      }
        	if(count==0){
        		list.remove(titleMap);
		        Message msg = new Message();
		        msg.what=NO_TODAY_PARKING_RECORD;
		        mHandler.sendMessage(msg);
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
	
	private Handler mHandler = new Handler() {
	    @Override
	    public void handleMessage (Message msg) {
	        super.handleMessage(msg);
	        switch (msg.what) {
	            case NO_TODAY_PARKING_RECORD:
	            	Toast.makeText(getApplicationContext(), "此泊位今日暂无停车记录", Toast.LENGTH_SHORT).show();
	                break;
	            default:
	                break;
	        }
	    }
	};
}

