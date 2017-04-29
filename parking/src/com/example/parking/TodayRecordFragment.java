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
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class TodayRecordFragment extends Fragment {
	private View mView;
	private ListView mListView;
	private int mLocationNumber;
	private DBAdapter mDBAdapter;
	
	 @Override
	    public void onAttach(Activity activity) {
	        super.onAttach(activity);
	    }

	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	    }

	    @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	    	mView = inflater.inflate(R.layout.fragment_record_today, container, false);
	        mListView=(ListView)mView.findViewById(R.id.list_record_today);  
	        Intent intent = getActivity().getIntent();
	        mLocationNumber=intent.getExtras().getInt("locationNumber");
	        mDBAdapter = new DBAdapter(getActivity());
	        List<Map<String, Object>> list=getData();  
	        mListView.setAdapter(new TodayRecordListAdapter(getActivity(), list)); 
	        return mView;
	    }

	    @Override
	    public void onActivityCreated(Bundle savedInstanceState) {
	        super.onActivityCreated(savedInstanceState);
	    }

	    @Override
	    public void onStart() {
	        super.onStart();
	    }

	    @Override
	    public void onResume() {
	        super.onResume();
	    }

	    @Override
	    public void onPause() {
	        super.onPause();
	    }

	    @Override
	    public void onStop() {
	        super.onStop();
	    }

	    @Override
	    public void onDestroyView() {
	        super.onDestroyView();
	    }

	    @Override
	    public void onDestroy() {
	        super.onDestroy();
	    }

	    @Override
	    public void onDetach() {
	        super.onDetach();
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
	    	titleMap.put("expense", "费用");
            list.add(titleMap); 
	    	Log.e("yifan", "count: " + cursor.getCount());
	    	Log.e("yifan", "locationNumber: " + locationNumber);
	        try {
	        	do{
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
	        	    	  }
	        	      }while(cursor.moveToNext());
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
