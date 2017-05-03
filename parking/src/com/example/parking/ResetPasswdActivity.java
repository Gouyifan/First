package com.example.parking;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ResetPasswdActivity extends Activity {
	private static final String[] DUMMY_CREDENTIALS = new String[] {
	"gouyf@ehualu.com","123456"};
	private static final int EVENT_INPUT_UNFINISHED = 101;
	private static final int EVENT_OLD_PASSWD_ERROR = 102;
	private static final int EVENT_INPUT_RENEW_ERROR =103;
	private static final int EVENT_MODIFY_SUCCESS = 104;
	private Button mConfirmBT;
	private Button mCancelBT;
	private EditText mOldPasswdET;
	private EditText mNewPasswdET;
	private EditText mRenewPasswdET;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reset_passwd);
		mOldPasswdET=(EditText)findViewById(R.id.et_old_passwd);
		mNewPasswdET=(EditText)findViewById(R.id.et_new_passwd);
		mRenewPasswdET=(EditText)findViewById(R.id.et_renew_passwd);
		mConfirmBT=(Button)findViewById(R.id.bt_confirm_reset_passwd);
		mConfirmBT.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				if((mOldPasswdET.getText().toString()).equals("") || (mNewPasswdET.getText().toString()).equals("") 
						|| (mRenewPasswdET.getText().toString()).equals("")){
					Message msg = new Message();
	                msg.what = EVENT_INPUT_UNFINISHED;
	                mHandler.sendMessage(msg);
				}else if(!(DUMMY_CREDENTIALS[1].toString()).equals(mOldPasswdET.getText().toString())){
					Message msg = new Message();
	                msg.what = EVENT_OLD_PASSWD_ERROR;
	                mHandler.sendMessage(msg);
				}else if(!(mNewPasswdET.getText().toString()).equals(mRenewPasswdET.getText().toString())){
					Message msg = new Message();
	                msg.what = EVENT_INPUT_RENEW_ERROR;
	                mHandler.sendMessage(msg);
				}else{
					Message msg = new Message();
	                msg.what = EVENT_MODIFY_SUCCESS;
	                mHandler.sendMessage(msg);
					Intent intent = new Intent(ResetPasswdActivity.this,UserCenterActivity.class);
					startActivity(intent);
					finish();
				}
			}
		});
		mCancelBT=(Button)findViewById(R.id.bt_cancel_reset_passwd);
		mCancelBT.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				Intent intent = new Intent(ResetPasswdActivity.this,UserCenterActivity.class);
				startActivity(intent);
				finish();
		    }
		});
		getActionBar().setDisplayHomeAsUpEnabled(true); 
	}
	
	private Handler mHandler = new Handler() {
        @Override
        public void handleMessage (Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case EVENT_INPUT_UNFINISHED:
            	    Toast.makeText(getApplicationContext(), "请完成输入", Toast.LENGTH_SHORT).show();
            	    break;
                case EVENT_OLD_PASSWD_ERROR:
                	Toast.makeText(getApplicationContext(), "原密码错误", Toast.LENGTH_SHORT).show();
                	break;
                case EVENT_INPUT_RENEW_ERROR:
                	Toast.makeText(getApplicationContext(), "两次输入不一致", Toast.LENGTH_SHORT).show();
                	break;
                case EVENT_MODIFY_SUCCESS:
                	Toast.makeText(getApplicationContext(), "修改成功", Toast.LENGTH_SHORT).show();
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
}
