/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.easemob.chatuidemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;

import com.easemob.chatuidemo.R;


/**
 * 联系人列表页
 * 
 */
public class HomeFragment extends Fragment implements OnClickListener{


    private View homeTv;
    private View rootView;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	    
	    rootView = inflater.inflate(R.layout.fragment_home, container, false);
	    findViews();
	    setOnClick();
		return rootView;
	}

    private void findViews(){
        homeTv= rootView.findViewById(R.id.homeTv);
    }
	private void setOnClick() {
        // TODO Auto-generated method stub
	    homeTv.setOnClickListener(this);
    }


	@Override
	public void onSaveInstanceState(Bundle outState) {
	    if(((MainActivity)getActivity()).isConflict)
	        outState.putBoolean("isConflict", true);
	    super.onSaveInstanceState(outState);
	    
	}

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        startActivity(new Intent(getActivity(), LoginActivity.class));
    }
}
