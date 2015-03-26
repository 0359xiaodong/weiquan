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

import itstudio.instructor.config.Config;
import itstudio.instructor.config.MyApplication;
import itstudio.instructor.entity.User;
import itstudio.instructor.http.TwitterRestClient;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.Header;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.easemob.EMCallBack;
import com.easemob.EMError;
import com.easemob.applib.controller.HXSDKHelper;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.easemob.chatuidemo.Constant;
import com.easemob.chatuidemo.R;
import com.easemob.chatuidemo.db.DbOpenHelper;
import com.easemob.chatuidemo.db.UserDao;
import com.easemob.chatuidemo.utils.CommonUtils;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.HanziToPinyin;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import copy.util.Tools;

/**
 * 登陆页面 
 * 
 */
public class LoginActivity extends BaseActivity {
	
	public static final int REQUEST_CODE_SETNICK = 1;
	private EditText usernameEditText;
	private EditText passwordEditText;
	private String allPsw="nihaoiec";
	private boolean autoLogin = false;
	private boolean register = false;
	private User user;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		usernameEditText = (EditText) findViewById(R.id.username);
		passwordEditText = (EditText) findViewById(R.id.password);
	
	}
	/*登陆的逻辑
	 * 先到自己服务器登陆
	 * 然后到环信登陆
	 * 如果环信登陆失败 说明还没注册
	 * 需要掉注册方法 
	 * 注册完以后在登陆
	 * */

	/**
	 * 登陆
	 * 
	 * @param view
	 */
	public void login(View view) {
		if (!CommonUtils.isNetWorkConnected(this)) {
			Toast.makeText(this, R.string.network_isnot_available, Toast.LENGTH_SHORT).show();
			return;
		}
		final String username = usernameEditText.getText().toString();
        final String password = passwordEditText.getText().toString();
        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
        	loginServer(username,password);
        }else{
        	Toast.makeText(this, "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
        }
	}
	/**
	 * 登陆自己的服务器
	 */

	
	
	public void loginServer(final String username, final String password) {
		Tools.showDialog(this);
		Tools.log("login sellf server");
		TwitterRestClient.keepCookie();
		RequestParams params = new RequestParams();
		params.put("key", Config.LOGIN_KEY);
		params.put("id", username);
		params.put("password", password);
		TwitterRestClient.post(Config.AC_USER_LOGIN, params, new AsyncHttpResponseHandler() {

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				Toast.makeText(getApplicationContext(), "服务器故障请稍后重试", 200).show();
				Tools.dismissWaitDialog();
			}

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] data) {
				// 加载成功
				Gson gson = new Gson();
				String response = new String(data);
				// 失败返回 "fail" 长度为7
				if (response != null && response.length() > 8) {
					user = gson.fromJson(response, new TypeToken<User>() {}.getType());
					MyApplication.user = user;
					MyApplication.getInstance().setUserJson(response);
	                // 登陆成功，保存用户名密码
	                MyApplication.getInstance().setUserName(username);
	                MyApplication.getInstance().setPassword(password);
					hxLogin(username, password);
				} else {
					Tools.dismissWaitDialog();
					Tools.showToast(R.string.error_account_pasword);
					// ToastUtil.showErrorMsg(LoginActivity.this,
					// R.string.error_account_pasword);
					// loginBtn.setText(R.string.login);
				}
			}
		});

	}

	// 环信登陆的方法
	private void hxLogin(final String username, final String password) {

		Tools.log("login huanxin server");
		MyApplication.currentUserNick = usernameEditText.getText().toString() + "abc";
		// 调用sdk登陆方法登陆聊天服务器
		EMChatManager.getInstance().login(username, allPsw, new EMCallBack() {

			@Override
			public void onSuccess() {
				HXSDKHelper.getInstance().setHXId(username);
				DbOpenHelper.reset();// 有可能更换账号 必须重置！
				DbOpenHelper.getInstance(LoginActivity.this);
				runOnUiThread(new Runnable() {
					public void run() {
						getFriends();
					}
				});
			}

			@Override
			public void onProgress(int progress, String status) {

			}

			@Override
			public void onError(final int code, final String message) {
				// 以前没有注册成功 这次去注册吧
				if (code == -1005 && register == false) {
					Tools.log("this user has not register");
					register(username, allPsw);
				}
				// 注册过一次还是没成功放弃吧
				if (register == true) {
					runOnUiThread(new Runnable() {
						public void run() {
							Tools.dismissWaitDialog();
							finish();
						}
					});
				}

			}
		});

	}
	    
	
	// 获取好友
    private void getFriends() {
    	
		final Map<String, User> userMap = new HashMap<String, User>();
		User newFriends = new User();
		newFriends.setUsername(Constant.NEW_FRIENDS_USERNAME);
		newFriends.setNick("申请与通知");
		newFriends.setHeader("");
		userMap.put(Constant.NEW_FRIENDS_USERNAME, newFriends);
		// 添加"群聊"
		User groupUser = new User();
		groupUser.setUsername(Constant.GROUP_USERNAME);
		groupUser.setNick("群聊");
		groupUser.setHeader("");
		userMap.put(Constant.GROUP_USERNAME, groupUser);
		// 添加聊天机器人
    	Tools.log("get friends");
		EMGroupManager.getInstance().loadAllGroups();
		EMChatManager.getInstance().loadAllConversations();
		RequestParams params = new RequestParams();
		params.put("id", MyApplication.user.getId());
		TwitterRestClient.post(Config.AC_FINDFRIENDS, params, new AsyncHttpResponseHandler() {

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {

				Tools.log("get friends fail");
				// 存入内存
				MyApplication.getInstance().setContactList(userMap);
				// 存入db
				UserDao dao = new UserDao(LoginActivity.this);
				List<User> userList = new ArrayList<User>(userMap.values());
				dao.saveContactList(userList);
				Tools.dismissWaitDialog();
				finish();
			}

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				// 加载成功
				List<User> userList = new ArrayList<User>(userMap.values());
				Gson gson = new GsonBuilder().create();
				if (!"error".equals(new String(arg2)) && !"none".equals(new String(arg2))) {
					List<User> users = gson.fromJson(new String(arg2), new TypeToken<List<User>>() {}.getType());
					userList.addAll(users);
					for (User user : users) {
						user.setUsername(user.getId());
						setUserHearder(user.getId(), user);
						user.setName(user.getName());
						userMap.put(user.getId(), user);
					}

				}
				// 存入内存
				MyApplication.getInstance().setContactList(userMap);
				// 存入db
				UserDao dao = new UserDao(LoginActivity.this);
				dao.saveContactList(userList);
				// 获取群聊列表(群聊里只有groupid和groupname等简单信息，不包含members),sdk会把群组存入到内存和db中
				try {
					EMGroupManager.getInstance().getGroupsFromServer();
				} catch (EaseMobException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					e.printStackTrace();
					Tools.dismissWaitDialog();
					finish();
				}
				Tools.dismissWaitDialog();
				finish();
			}
		});
    }
	   /**
     * 注册
     * 
     * @param view
     */
    public void register(final String username,final String pwd) {
        Tools.log("register hx");
        register = true;//避免死循环
        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(pwd)) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        // 调用sdk注册方法
                        EMChatManager.getInstance().createAccountOnServer(username, pwd);
                        runOnUiThread(new Runnable() {
                            public void run() {
                                if (!LoginActivity.this.isFinishing())
                                // 保存用户名
                                MyApplication.getInstance().setUserName(username);
                                hxLogin(username,pwd);
                            }
                        });
                    } catch (final EaseMobException e) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                int errorCode=e.getErrorCode();
                                if(errorCode==EMError.NONETWORK_ERROR){
                                    Toast.makeText(getApplicationContext(), "网络异常，请检查网络！", Toast.LENGTH_SHORT).show();
                                }else if(errorCode==EMError.USER_ALREADY_EXISTS){
                                   // Toast.makeText(getApplicationContext(), "用户已存在！", Toast.LENGTH_SHORT).show();
                                }else if(errorCode==EMError.UNAUTHORIZED){
                                   // Toast.makeText(getApplicationContext(), "注册失败，无权限！", Toast.LENGTH_SHORT).show();
                                }else{
                                   // Toast.makeText(getApplicationContext(), "注册失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                                Tools.dismissWaitDialog();
                                finish();
                            }
                        });
                    }
                }
            }).start();

        }
    }
	/**
	 * 注册
	 * 
	 * @param view
	 */
	public void register(View view) {
		startActivityForResult(new Intent(this, RegisterActivity.class), 0);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (autoLogin) {
			return;
		}

		if (MyApplication.getInstance().getUserName() != null) {
			usernameEditText.setText(MyApplication.getInstance().getUserName());
		}
	}

	/**
	 * 设置hearder属性，方便通讯中对联系人按header分类显示，以及通过右侧ABCD...字母栏快速定位联系人
	 * 
	 * @param username
	 * @param user
	 */
	protected void setUserHearder(String username, User user) {
		String headerName = null;
		if (!TextUtils.isEmpty(user.getNick())) {
			headerName = user.getNick();
		} else {
			headerName = user.getUsername();
		}
		if (username.equals(Constant.NEW_FRIENDS_USERNAME)) {
			user.setHeader("");
		} else if (Character.isDigit(headerName.charAt(0))) {
			user.setHeader("#");
		} else {
			user.setHeader(HanziToPinyin.getInstance().get(headerName.substring(0, 1)).get(0).target.substring(0, 1).toUpperCase());
			char header = user.getHeader().toLowerCase().charAt(0);
			if (header < 'a' || header > 'z') {
				user.setHeader("#");
			}
		}
	}
	
}
