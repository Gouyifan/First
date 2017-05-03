package com.example.parking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.parking.R.drawable;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class MessageCenterActivity extends Activity {
	private ListView mListView;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message_center);
		mListView=(ListView)findViewById(R.id.list_message_center);  
        List<Map<String, Object>> list=getData();  
        mListView.setAdapter(new MessageCenterListAdapter(this, list)); 
        mListView.setOnItemClickListener(new OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                    long arg3) {
            	//TODO
            }
        });
		getActionBar().setDisplayHomeAsUpEnabled(true); 
	}

    public List<Map<String, Object>> getData(){  
        List<Map<String, Object>> list=new ArrayList<Map<String,Object>>();  
        for (int i = 1; i <= 2; i++) {  
            Map<String, Object> map=new HashMap<String, Object>();  
            if(i==1){
                map.put("messageCenterImage",  drawable.ic_add_alert_black_18dp);
                map.put("messageCenterTitle", "考勤通知");
                map.put("messageCenterDetail", "您4月25日出现一次考勤异常");
                map.put("messageCenterTime", "2017.04.25" + " " + "15:15:40");
                map.put("messageCenterDetailHide", "您4月25日上班打卡时间08:40:36(上班时间9:00)，" +
                		"下班打卡时间15:30:23(下班时间17:30)，存在异常，请联系考勤员确认。");
            }else if(i==2){
                map.put("messageCenterImage",  drawable.ic_error_outline_black_18dp);
                map.put("messageCenterTitle", "停车通知");
                map.put("messageCenterDetail", "4月25日出现一次停车逃费现象");
                map.put("messageCenterTime", "2017.04.25" + " " + "16:25:36");
                map.put("messageCenterDetailHide", "4月25日出现一次停车逃费现象，入场时间11:15:36，" +
                		"牌照号津A00001，泊位号6，请联系稽查员确认。");
            }
            list.add(map);  
        }  
        return list;  
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
	
}
