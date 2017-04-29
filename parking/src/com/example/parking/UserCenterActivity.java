package com.example.parking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.parking.R.drawable;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class UserCenterActivity extends Activity {
	private ListView mListView;
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_center);
		mListView=(ListView)findViewById(R.id.list_function_user_center);  
        List<Map<String, Object>> list=getData();  
        mListView.setAdapter(new UserCenterListAdapter(this, list));
        mListView.setOnItemClickListener(new OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                    long arg3) {
                // TODO Auto-generated method stub
            	Map<String,Object> map=(Map<String,Object>)mListView.getItemAtPosition(arg2);
                String userCenterFunction=(String)map.get("userCenterFunction");
                if(userCenterFunction.equals("消息中心")){
                	Intent intent = new Intent(UserCenterActivity.this,MessageCenterActivity.class);
	        	    startActivity(intent);
                }else if(userCenterFunction.equals("重置密码")){
                	Intent intent = new Intent(UserCenterActivity.this,ResetPasswdActivity.class);
	        	    startActivity(intent);
                }else if(userCenterFunction.equals("退出账号")){
                	showExitDialog();
                }
            }
        });
	}

    public List<Map<String, Object>> getData(){  
        List<Map<String, Object>> list=new ArrayList<Map<String,Object>>();  
        for (int i = 1; i <= 3; i++) {  
            Map<String, Object> map=new HashMap<String, Object>();  
            if(i==1){
                map.put("userCenterFunction",  "消息中心");
                map.put("userCenterFunctionSpreadImage",  drawable.ic_chevron_right_black_24dp);
                map.put("userCenterFunctionImage",  drawable.ic_message_black_18dp);
            }else if(i==2){
                map.put("userCenterFunction",  "重置密码");
                map.put("userCenterFunctionSpreadImage",  drawable.ic_chevron_right_black_24dp);
                map.put("userCenterFunctionImage",  drawable.ic_lock_black_18dp);
            }else if(i==3){
            	map.put("userCenterFunction",  "退出账号");
            	map.put("userCenterFunctionSpreadImage",  drawable.ic_chevron_right_black_24dp);
                map.put("userCenterFunctionImage",  drawable.ic_power_settings_new_black_18dp);
            }
            list.add(map);  
        }  
        return list;  
      }

    private void showExitDialog(){
        final AlertDialog.Builder exitDialog = new AlertDialog.Builder(UserCenterActivity.this);
        exitDialog.setIcon(R.drawable.ic_exit_to_app_white_24dp);
        exitDialog.setTitle("退出账号");
        exitDialog.setMessage("确定退出当前账号？");
        exitDialog.setPositiveButton("确定",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(UserCenterActivity.this,LoginActivity.class);
				startActivity(intent);
				finish();
            }
        });
        exitDialog.setNegativeButton("关闭",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //...To-do
            }
        });
        exitDialog.show();
    }
}
