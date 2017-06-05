package com.example.parking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

public class HistoryRecordFragment extends Fragment {
	public static final int TYPE_UNFINISHED_PAYMENT_STATE = 101;
	public static final int TYPE_FINISHED_PAYMENT_STATE_MOBILE = 102;
	public static final int TYPE_FINISHED_PAYMENT_STATE_CASH = 103;
	public static final int TYPE_UNFINISHED_PAYMENT_STATE_LEAVE = 104;
	public static final int NO_TODAY_PARKING_RECORD =201;
	private View mView;
	private ListView mListView;
	private TextView mEmptyNotifyTV;
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
	        mEmptyNotifyTV=(TextView)mView.findViewById(R.id.tv_notify_history_record_list_empty);  
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
	    	titleMap.put("expense", "支付");
            list.add(titleMap); 
            int count = 0;
	        try {
	        	do{
	        		      Log.e("yifan","paymentpattern : " + cursor.getString(cursor.getColumnIndex("paymentpattern")));
	        	    	  if((cursor.getString(cursor.getColumnIndex("paymentpattern"))).equals(paymentState) ){
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
	      		              count++;
	        	    	  }
	        	      }while(cursor.moveToNext());
	        }catch (Exception e) {
	                e.printStackTrace();
	        } finally{
       	            if(count==0){
    	    	        list.remove(titleMap);
    		            Message msg = new Message();
    		            msg.what=NO_TODAY_PARKING_RECORD;
    		            mHandler.sendMessage(msg);
    	    	        mEmptyNotifyTV.setVisibility(View.VISIBLE);
    	            }
	            	if(cursor!=null){
	            		cursor.close();
	                }
	        }
	    }
	    
		private Handler mHandler = new Handler() {
		    @Override
		    public void handleMessage (Message msg) {
		        super.handleMessage(msg);
		        switch (msg.what) {
		            case NO_TODAY_PARKING_RECORD:
		            	//Toast.makeText(getApplicationContext(), "此泊位今日暂无停车记录", Toast.LENGTH_SHORT).show();
		            	mEmptyNotifyTV.setVisibility(View.VISIBLE);
		                break;
		            default:
		                break;
		        }
		    }
		};
		
		/**
		 * Add for request to search detail information for licensenumber
		public void requestSearchHistoryRecord()throws ParseException, IOException, JSONException{
			  HttpClient httpClient = new DefaultHttpClient();
			  String strurl = "//此处url待定";
			  HttpPost request = new HttpPost(strurl);
			  request.addHeader("Accept","application/json");
			  request.addHeader("Content-Type","application/json");//还可以自定义增加header
			  JSONObject param = new JSONObject();//定义json对象
			  param.put("type", "historyrecordsearch");
			  param.put("paymentpattern", mType);
			  param.put("date", date);
			  Log.e("yifan", param.toString());
			  StringEntity se = new StringEntity(param.toString());
			  request.setEntity(se);//发送数据
			  HttpResponse httpResponse = httpClient.execute(request);//获得响应
			  int code = httpResponse.getStatusLine().getStatusCode();
			  if(code==HttpStatus.SC_OK){
				  String strResult = EntityUtils.toString(httpResponse.getEntity());
				  JSONObject jsonData1 = new JSONObject(strResult);
			      if(jsonData1.get("list")!=null){  
                     JSONArray array = jsonData1.getJSONArray("list");  
                     for (int i = 0; i < array.length(); i++) {                                
                      JSONObject jsonData2 = (JSONObject) array.get(i);
                      if(jsonDataDetail.get("list")!=null){   
                          JSONArray array2 = jsonData2.getJSONArray("list");  
                         for (int i = 0; i < array2.length(); i++) {
                             JSONObject jsonData3 = (JSONObject) array.get(i);
                             String licenseNumer = (String) jsonData3.get("licensenumber");
				             String startTime = (String) jsonData3.get("starttime");
				             String leaveTime = (String) jsonData3.get("leavetime");
				             int locationNumber = (String) jsonData3.get("locationnumber");
				             String expense = (String) jsonData3.get("expense");
                         }                              
                }  
			      JSONObject jsonData = new JSONObject(data);
			  }else{
				  Log.e("yifan", Integer.toString(code));
			  }
			 }
		//Client's json:{ "type":"historyrecordsearch", "paymentpattern":"现金支付", "date", "2017-05-04"}
		//Server's json:{"list":{"list":{"licensenumber":"津HG9025", "starttime":"2017-05-04 15:49:20", "leavetime":“2017-05-04 16:50:25”, "locationnumber":1， "expense":"5元"},
		 //                                        "list":{"licensenumber":"津HG9025", "starttime":"2017-05-04 15:49:20", "leavetime":“2017-05-04 16:50:25”, "locationnumber":2， "expense":"5元"}}}
		*/
}
