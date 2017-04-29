package com.example.parking;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class ParkingHistorySearchActivity extends FragmentActivity {
	public static final int TYPE_UNFINISHED_PAYMENT_STATE = 101;
	public static final int TYPE_FINISHED_PAYMENT_STATE_MOBILE = 102;
	public static final int TYPE_FINISHED_PAYMENT_STATE_CASH = 103;
	public static final int TYPE_UNFINISHED_PAYMENT_STATE_LEAVE = 104;
	private Fragment mUnfinishedPaymentStateFragment;
	private Fragment mFinishedPaymentStateMobileFragment;
	private Fragment mFinishedPaymentStateCashFragment;
	private Fragment mUnfinishedPaymentStateLeaveFragment;
	private TextView mUnfinishedPaymentStateTV;
	private TextView mFinishedPaymentStateMobileTV;
	private TextView mFinishedPaymentStateCashTV;
	private TextView mUnfinishedPaymentStateLeaveTV;
	private Spinner mHistoryDateSP;
	private Button mSearchBT;
	private int mCurrentId;
	private OnClickListener mTabClickListener = new OnClickListener() {
        @Override  
        public void onClick(View v) {  
            if (v.getId() != mCurrentId) {//如果当前选中跟上次选中的一样,不需要处理  
                changeSelect(v.getId());//改变图标跟文字颜色的选中   
                changeFragment(v.getId(),mHistoryDateSP.getSelectedItem().toString());//fragment的切换  
                mCurrentId = v.getId();//设置选中id  
            }  
        }  
    };  
	@Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.activity_parking_history_search);
        mUnfinishedPaymentStateTV = (TextView) findViewById(R.id.tv_payment_state_unfinished_history);
        mFinishedPaymentStateMobileTV = (TextView) findViewById(R.id.tv_payment_state_finished_mobile_history);
        mFinishedPaymentStateCashTV = (TextView) findViewById(R.id.tv_payment_state_finished_cash_history);
        mUnfinishedPaymentStateLeaveTV = (TextView) findViewById(R.id.tv_payment_state_unfinished_leave_history);
        mUnfinishedPaymentStateTV.setOnClickListener(mTabClickListener); 
        mFinishedPaymentStateMobileTV.setOnClickListener(mTabClickListener);
        mFinishedPaymentStateCashTV.setOnClickListener(mTabClickListener); 
        mUnfinishedPaymentStateLeaveTV.setOnClickListener(mTabClickListener);
        mHistoryDateSP = (Spinner) findViewById(R.id.sp_history_Date_history);
        mSearchBT=(Button) findViewById(R.id.bt_search_history);
        mSearchBT.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View v){
				String date = mHistoryDateSP.getSelectedItem().toString();
				changeSelect(R.id.tv_payment_state_unfinished_history);
		        changeFragment(R.id.tv_payment_state_unfinished_history,date);
			}
		});
        changeSelect(R.id.tv_payment_state_unfinished_history);
        changeFragment(R.id.tv_payment_state_unfinished_history,mHistoryDateSP.getSelectedItem().toString());
	}

	private void changeFragment(int resId,String date) {  
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();//开启一个Fragment事务  
        hideFragments(transaction);//隐藏所有fragment  
        if(resId==R.id.tv_payment_state_unfinished_history){
        	mUnfinishedPaymentStateFragment = new HistoryRecordFragment(TYPE_UNFINISHED_PAYMENT_STATE,date);
        	 transaction.replace(R.id.history_payment_container, mUnfinishedPaymentStateFragment);
        }else if(resId==R.id.tv_payment_state_finished_mobile_history){
        	mFinishedPaymentStateMobileFragment = new HistoryRecordFragment(TYPE_FINISHED_PAYMENT_STATE_MOBILE,date);  
        	transaction.replace(R.id.history_payment_container, mFinishedPaymentStateMobileFragment);
        }else if(resId==R.id.tv_payment_state_finished_cash_history){
        	mFinishedPaymentStateCashFragment = new HistoryRecordFragment(TYPE_FINISHED_PAYMENT_STATE_CASH,date);  
        	transaction.replace(R.id.history_payment_container, mFinishedPaymentStateCashFragment);
        }else if(resId==R.id.tv_payment_state_unfinished_leave_history){
        	mUnfinishedPaymentStateLeaveFragment = new HistoryRecordFragment(TYPE_UNFINISHED_PAYMENT_STATE_LEAVE,date);  
        	transaction.replace(R.id.history_payment_container, mUnfinishedPaymentStateLeaveFragment);
        }
        transaction.commit();//一定要记得提交事务  
    }

	private void hideFragments(FragmentTransaction transaction){  
        if (mUnfinishedPaymentStateFragment != null) {
        	transaction.hide(mUnfinishedPaymentStateFragment);
        }else if(mFinishedPaymentStateMobileFragment!=null){
        	transaction.hide(mFinishedPaymentStateMobileFragment);
        }else if(mFinishedPaymentStateCashFragment!=null){
        	transaction.hide(mFinishedPaymentStateCashFragment);
        }else if(mUnfinishedPaymentStateLeaveFragment!=null){
        	transaction.hide(mUnfinishedPaymentStateLeaveFragment);
        }
    }

	private void changeSelect(int resId) {  
		mUnfinishedPaymentStateTV.setSelected(false);
		mUnfinishedPaymentStateTV.setBackgroundResource(R.color.gray);
		mFinishedPaymentStateMobileTV.setSelected(false);  
		mFinishedPaymentStateMobileTV.setBackgroundResource(R.color.gray);
		mFinishedPaymentStateCashTV.setSelected(false);
		mFinishedPaymentStateCashTV.setBackgroundResource(R.color.gray);
		mUnfinishedPaymentStateLeaveTV.setSelected(false);  
		mUnfinishedPaymentStateLeaveTV.setBackgroundResource(R.color.gray);
        switch (resId) {  
        case R.id.tv_payment_state_unfinished_history:  
        	mUnfinishedPaymentStateTV.setSelected(true);  
        	mUnfinishedPaymentStateTV.setBackgroundResource(R.color.orange);
            break;  
        case R.id.tv_payment_state_finished_mobile_history:  
        	mFinishedPaymentStateMobileTV.setSelected(true);  
        	mFinishedPaymentStateMobileTV.setBackgroundResource(R.color.orange);
            break;
        case R.id.tv_payment_state_finished_cash_history:  
        	mFinishedPaymentStateCashTV.setSelected(true);  
        	mFinishedPaymentStateCashTV.setBackgroundResource(R.color.orange);
            break;
        case R.id.tv_payment_state_unfinished_leave_history:  
        	mUnfinishedPaymentStateLeaveTV.setSelected(true);  
        	mUnfinishedPaymentStateLeaveTV.setBackgroundResource(R.color.orange);
            break;
        }  
    }
}
