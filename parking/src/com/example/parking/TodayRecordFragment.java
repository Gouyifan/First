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
	    	titleMap.put("expense", "支付金额");
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
	    
		/**
		 * Add for request to search detail information for licensenumber
		public void requestSearchTodayRecord()throws ParseException, IOException, JSONException{
			  HttpClient httpClient = new DefaultHttpClient();
			  String strurl = "//此处url待定";
			  HttpPost request = new HttpPost(strurl);
			  request.addHeader("Accept","application/json");
			  request.addHeader("Content-Type","application/json");//还可以自定义增加header
			  JSONObject param = new JSONObject();//定义json对象
			  param.put("type", "todayrecordsearch");
			  param.put("locationnumber", mLocationNumber);
			  SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd%"); 
			  Date curDate = new Date(System.currentTimeMillis());
			  String date = formatter.format(curDate);
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
				             String paymentPattern = (String) jsonData3.get("paymentpattern");
				             String expense = (String) jsonData3.get("expense");
                         }                              
                }  
			      JSONObject jsonData = new JSONObject(data);
			  }else{
				  Log.e("yifan", Integer.toString(code));
			  }
			 }
		//Client's json:{ "type":"todayrecordsearch", "locationnumber":1, "date", "2017-05-04"}
		//Server's json:{"list":{"list":{"licensenumber":"津HG9025", "starttime":"2017-05-04 15:49:20", "leavetime":“2017-05-04 16:50:25”, "paymentpattern":"现金支付"， "expense":"5元"},
		 //                                        "list":{"licensenumber":"津HG9025", "starttime":"2017-05-04 15:49:20", "leavetime":“2017-05-04 16:50:25”, "paymentpattern":"现金支付"， "expense":"5元"}}}
		*/
}
