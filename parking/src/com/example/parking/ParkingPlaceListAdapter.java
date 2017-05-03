package com.example.parking;

import java.util.List;
import java.util.Map;

import android.R.color;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ParkingPlaceListAdapter extends BaseAdapter {  
	  
    private List<Map<String, Object>> data;  
    private LayoutInflater layoutInflater;  
    private Context context;  
    public ParkingPlaceListAdapter(Context context,List<Map<String, Object>> data){  
        this.context=context;  
        this.data=data;  
        this.layoutInflater=LayoutInflater.from(context);  
    }  
    /** 
     * 组件集合，对应list.xml中的控件 
     * @author Administrator 
     */  
    public final class Zujian{  
        public TextView parkingNumberTV; 
        public TextView licensePlateNumberTV;  
    }  
    @Override  
    public int getCount() {  
        return data.size();  
    }  
    /** 
     * 获得某一位置的数据 
     */  
    @Override  
    public Object getItem(int position) {  
        return data.get(position);  
    }  
    /** 
     * 获得唯一标识 
     */  
    @Override  
    public long getItemId(int position) {  
        return position;  
    }  
  
    @Override  
    public View getView(int position, View convertView, ViewGroup parent) {  
        Zujian zujian=null;  
        if(convertView==null){  
            zujian=new Zujian();  
            //获得组件，实例化组件  
            convertView=layoutInflater.inflate(R.layout.list_parking_detail, null);  
            zujian.parkingNumberTV=(TextView)convertView.findViewById(R.id.tv_parkingnumber);  
            zujian.licensePlateNumberTV=(TextView)convertView.findViewById(R.id.tv_licenseplatenumber);  
            convertView.setTag(zujian);  
        }else{  
            zujian=(Zujian)convertView.getTag();  
        }  
        //绑定数据  
        zujian.parkingNumberTV.setText((String)data.get(position).get("parkingNumber"));  
        zujian.parkingNumberTV.setBackgroundColor(context.getResources().getColor(R.color.white));
        zujian.licensePlateNumberTV.setText((String)data.get(position).get("licensePlateNumber"));
        return convertView;  
    }  
  
} 