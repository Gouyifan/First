package com.example.parking;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class LocationFragment extends Fragment {
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
        return inflater.inflate(R.layout.fragment_location, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Button locationButtonTJ = (Button) getActivity().findViewById(R.id.locationButtonTJ);
        Button locationButtonBJ = (Button) getActivity().findViewById(R.id.locationButtonBJ);
        Button locationButtonSH = (Button) getActivity().findViewById(R.id.locationButtonSH);
        Button locationButtonHB = (Button) getActivity().findViewById(R.id.locationButtonHB);
        Button locationButtonSD = (Button) getActivity().findViewById(R.id.locationButtonSD);
        Button locationButtonSX = (Button) getActivity().findViewById(R.id.locationButtonSX);
        Button backspaceButton = (Button) getActivity().findViewById(R.id.bt_back_space_location);
        locationButtonTJ.setOnClickListener(mOnClickListener);  
        locationButtonBJ.setOnClickListener(mOnClickListener); 
        locationButtonSH.setOnClickListener(mOnClickListener); 
        locationButtonHB.setOnClickListener(mOnClickListener); 
        locationButtonSD.setOnClickListener(mOnClickListener); 
        locationButtonSX.setOnClickListener(mOnClickListener); 
        backspaceButton.setOnClickListener(mOnClickListener);
    }

    OnClickListener mOnClickListener = new OnClickListener() {  
        @Override  
        public void onClick(View v) {
        	int resId = v.getId();
        	EditText editText = (EditText) getActivity().findViewById(R.id.et_license_plate);
            switch (resId) {  
                case R.id.locationButtonTJ:  
            	    editText.append("津");
                    break;  
                case R.id.locationButtonBJ:  
                	editText.append("京");
                    break;
                case R.id.locationButtonSH:  
                	editText.append("沪");
                    break;
                case R.id.locationButtonHB:  
                	editText.append("冀");
                    break;  
                case R.id.locationButtonSD:  
                	editText.append("鲁");
                    break;  
                case R.id.locationButtonSX:  
                	editText.append("晋");
                    break;
                case R.id.bt_back_space_location:
                	String str = editText.getText().toString();
                    if (str!= null && !str.equals("")){   
                        String string = new String();   
                        if (str.length() > 1){     
                             string =   str.substring(0, str.length() - 1);    
                        }else {    
                             string =  null;   
                        }
                        editText.setText(string);
                    }
                    break;
               }
        }  
    };

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
}
