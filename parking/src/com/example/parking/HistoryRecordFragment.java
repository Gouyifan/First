package com.example.parking;

import java.util.ArrayList;
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

public class HistoryRecordFragment extends Fragment {
	public static final int TYPE_UNFINISHED_PAYMENT_STATE = 101;
	public static final int TYPE_FINISHED_PAYMENT_STATE_MOBILE = 102;
	public static final int TYPE_FINISHED_PAYMENT_STATE_CASH = 103;
	public static final int TYPE_UNFINISHED_PAYMENT_STATE_LEAVE = 104;
	private View mView;
	private ListView mListView;
	private int mType;
	private String mDate;
	private DBAdapter mDBAdapter;
	public HistoryRecordFragment(int type,String date){
		mType = type;
		mDate = date;
	}
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
	    	mView = inflater.inflate(R.layout.fragment_record_history, container, false);
	        mListView=(ListView)mView.findViewById(R.id.list_record_history);  
	        mDBAdapter = new DBAdapter(getActivity());
	        List<Map<String, Object>> list=getData(mType);  
	        mListView.setAdapter(new HistoryRecordListAdapter(getActivity(), list)); 
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

	    public List<Map<String, Object>> getData(int type){  
	        List<Map<String, Object>> list=new ArrayList<Map<String,Object>>();  
	        setHistoryRecord(mDate,mType,list);
	        return list;  
	    }
	    
	    public void setHistoryRecord(String date,int type,List<Map<String, Object>> list){
	    	mDBAdapter.open();
	    	String paymentState = new String();
	    	if(mType==TYPE_UNFINISHED_PAYMENT_STATE){
	    		paymentState = "未付";
	    	}else if(mType==TYPE_FINISHED_PAYMENT_STATE_MOBILE){
	    		paymentState = "移动支付";
	    	}else if(mType==TYPE_FINISHED_PAYMENT_STATE_CASH){
	    		paymentState = "现金支付";
	    	}else if(mType==TYPE_UNFINISHED_PAYMENT_STATE_LEAVE){
	    		paymentState = "逃费";
	    	}
	    	Log.e("yifan","date : " + date);
	    	Log.e("yifan","paymentState : " + paymentState);
	    	Cursor cursor = mDBAdapter.getParkingByStartTime(date+"%");
	    	Log.e("yifan","count : " + cursor.getCount());
	    	Map<String, Object> titleMap=new HashMap<String, Object>();
	    	titleMap.put("licensePlateNumber","牌照");
	    	titleMap.put("startTime","入场时间");
	    	titleMap.put("leaveTime", "离场时间");
	    	titleMap.put("parkingLocation","泊位");
	    	titleMap.put("expense", "费用");
            list.add(titleMap); 
	        try {
	        	do{
	        		Log.e("yifan","paymentpattern : " + cursor.getString(cursor.getColumnIndex("paymentpattern")));
	        	    	  if((cursor.getString(cursor.getColumnIndex("paymentpattern"))).equals(paymentState) ){
	        	    		  Log.e("yifan", "+1" );
	        	    		  Map<String, Object> map=new HashMap<String, Object>();
	        	    		  map.put("licensePlateNumber", cursor.getString(cursor.getColumnIndex("licenseplate")));
	        	    		  map.put("parkingLocation", cursor.getInt(cursor.getColumnIndex("locationnumber"))+"");
	        	    		  map.put("startTime", "入场: " + cursor.getString(cursor.getColumnIndex("starttime")));
	        	    		  if(cursor.getString(cursor.getColumnIndex("leavetime"))==null){
	        	    			  map.put("leaveTime", null);
	        	    		  }else{
		        	    		  map.put("leaveTime", "离场: " + cursor.getString(cursor.getColumnIndex("leavetime")));
	        	    		  }
	        	    		  map.put("paymentState", cursor.getString(cursor.getColumnIndex("paymentpattern")));
	        	    		  if(cursor.getString(cursor.getColumnIndex("expense"))==null){
		        	    		  map.put("expense", null);
	        	    		  }else{
		        	    		  map.put("expense", cursor.getString(cursor.getColumnIndex("expense")));
	        	    		  }
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
