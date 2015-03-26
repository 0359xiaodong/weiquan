package itstudio.instructor.fragment;

import itstudio.instructor.config.MyApplication;
import itstudio.instructor.util.FileUtils;

import com.easemob.chatuidemo.R;
import com.easemob.chatuidemo.activity.LoginActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class SettingFragment extends Fragment {

    private View rootView ;
    private ImageView headImg;
    private TextView nameTv;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_me, container,false);
            initView(rootView, inflater);
        }
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
        }
        return rootView;
    }
    private void initView(View rootView, LayoutInflater inflater) {
        // TODO Auto-generated method stub
        rootView.findViewById(R.id.rl_login).setOnClickListener(clickListener);
        headImg = (ImageView) rootView.findViewById(R.id.headImg);
        nameTv= (TextView) rootView.findViewById(R.id.userName);
        
        if(MyApplication.user!=null){
            nameTv.setText(MyApplication.user.getName());
            FileUtils.setImageHead(MyApplication.user.getHeadUrl(), headImg);
        }
        
    }
    OnClickListener clickListener = new OnClickListener() {
        
        @Override
        public void onClick(View view) {
            // TODO Auto-generated method stub
            
            switch (view.getId()) {
            case R.id.rl_login:
               // if(MyApplication.user==null)
                startActivity(new Intent(getActivity(), LoginActivity.class));
                break;
            case R.id.rl_setting:
                
                break;

            default:
                break;
            }
        }
    };
    public void onResume() {
        super.onResume();
        if(MyApplication.user!=null){
            nameTv.setText(MyApplication.user.getName());
            FileUtils.setImageHead(MyApplication.user.getHeadUrl(), headImg);
        }  
    };

}
