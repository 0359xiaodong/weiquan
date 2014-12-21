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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.easemob.EMCallBack;
import com.easemob.EMError;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMGroupManager;
import com.easemob.chatuidemo.Constant;
import com.easemob.chatuidemo.DemoApplication;
import com.easemob.chatuidemo.DemoHXSDKHelper;
import com.easemob.chatuidemo.R;
import com.easemob.chatuidemo.db.UserDao;
import com.easemob.chatuidemo.domain.User;
import com.easemob.chatuidemo.utils.CommonUtils;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.EMLog;
import com.easemob.util.HanziToPinyin;
import com.umeng.analytics.MobclickAgent;

/**
 * 登陆页面 
 * 
 */
public class LoginActivity extends BaseActivity {
	public static final int REQUEST_CODE_SETNICK = 1;
	private EditText usernameEditText;
	private EditText passwordEditText;

	private boolean progressShow;
	private boolean autoLogin = false;

	private boolean register = false;
	
	ProgressDialog pd;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

/*		// 如果用户名密码都有，直接进入主页面
		if (DemoHXSDKHelper.getInstance().isLogined()) {
			autoLogin = true;
			startActivity(new Intent(LoginActivity.this, MainActivity.class));

			return;
		}*/
		setContentView(R.layout.activity_login);

		usernameEditText = (EditText) findViewById(R.id.username);
		passwordEditText = (EditText) findViewById(R.id.password);

		// 如果用户名改变，清空密码
		usernameEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				passwordEditText.setText(null);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

	}
	/*登陆的逻辑
	 * 先到自己服务器登陆
	 * 然后到环信登陆
	 * 如果环信登陆失败 说明还没注册
	 * 需要掉注册方法 
	 * 注册完以后在登陆
	 * */

	/**
	 * 环信登陆
	 * 
	 * @param view
	 */
	public void login(View view) {
		if (!CommonUtils.isNetWorkConnected(this)) {
			Toast.makeText(this, R.string.network_isnot_available, Toast.LENGTH_SHORT).show();
			return;
		}
		selfLogin();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == REQUEST_CODE_SETNICK) {
				DemoApplication.currentUserNick = data.getStringExtra("edittext");

				final String username = usernameEditText.getText().toString();
				final String password = passwordEditText.getText().toString();

				if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
					progressShow = true;
					final ProgressDialog pd = new ProgressDialog(LoginActivity.this);
					pd.setCanceledOnTouchOutside(false);
					pd.setOnCancelListener(new OnCancelListener() {

						@Override
						public void onCancel(DialogInterface dialog) {
							progressShow = false;
						}
					});
					pd.setMessage("正在登陆...");
					pd.show();

					final long start = System.currentTimeMillis();
					// 调用sdk登陆方法登陆聊天服务器
					EMChatManager.getInstance().login(username, password, new EMCallBack() {

						@Override
						public void onSuccess() {
							//umeng自定义事件，开发者可以把这个删掉
							loginSuccess2Umeng(start);
							
							if (!progressShow) {
								return;
							}
							// 登陆成功，保存用户名密码
							DemoApplication.getInstance().setUserName(username);
							DemoApplication.getInstance().setPassword(password);
							runOnUiThread(new Runnable() {
								public void run() {
									pd.setMessage("正在获取好友和群聊列表...");
								}
							});
							try {
								// ** 第一次登录或者之前logout后，加载所有本地群和回话
								// ** manually load all local groups and
								// conversations in case we are auto login
								EMGroupManager.getInstance().loadAllGroups();
								EMChatManager.getInstance().loadAllConversations();

								// demo中简单的处理成每次登陆都去获取好友username，开发者自己根据情况而定
								List<String> usernames = EMContactManager.getInstance().getContactUserNames();
								EMLog.d("roster", "contacts size: " + usernames.size());
								Map<String, User> userlist = new HashMap<String, User>();
								for (String username : usernames) {
									User user = new User();
									user.setUsername(username);
									setUserHearder(username, user);
									userlist.put(username, user);
								}
								// 添加user"申请与通知"
								User newFriends = new User();
								newFriends.setUsername(Constant.NEW_FRIENDS_USERNAME);
								newFriends.setNick("申请与通知");
								newFriends.setHeader("");
								userlist.put(Constant.NEW_FRIENDS_USERNAME, newFriends);
								// 添加"群聊"
								User groupUser = new User();
								groupUser.setUsername(Constant.GROUP_USERNAME);
								groupUser.setNick("群聊");
								groupUser.setHeader("");
								userlist.put(Constant.GROUP_USERNAME, groupUser);

								// 存入内存
								DemoApplication.getInstance().setContactList(userlist);
								// 存入db
								UserDao dao = new UserDao(LoginActivity.this);
								List<User> users = new ArrayList<User>(userlist.values());
								dao.saveContactList(users);

								// 获取群聊列表(群聊里只有groupid和groupname等简单信息，不包含members),sdk会把群组存入到内存和db中
								EMGroupManager.getInstance().getGroupsFromServer();
							} catch (Exception e) {
								e.printStackTrace();
							}
							//更新当前用户的nickname 此方法的作用是在ios离线推送时能够显示用户nick
							boolean updatenick = EMChatManager.getInstance().updateCurrentUserNick(DemoApplication.currentUserNick);
							if (!updatenick) {
								EMLog.e("LoginActivity", "update current user nick fail");
							}

							if (!LoginActivity.this.isFinishing())
								pd.dismiss();
							// 进入主页面
							startActivity(new Intent(LoginActivity.this, MainActivity.class));
							finish();
						}

						@Override
						public void onProgress(int progress, String status) {

						}

						@Override
						public void onError(final int code, final String message) {
							loginFailure2Umeng(start,code,message);

							if (!progressShow) {
								return;
							}
							runOnUiThread(new Runnable() {
								public void run() {
									pd.dismiss();
									Toast.makeText(getApplicationContext(), "登录失败: " + message, 0).show();
									startActivity(new Intent(LoginActivity.this, MainActivity.class));
		                            finish();
								}
							});
						}
					});

				}

			}

		}
	}
	/**
	 * 登陆自己的服务器
	 */
	private void selfLogin(){
        final String username = usernameEditText.getText().toString();
        final String password = passwordEditText.getText().toString();

        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
            
          //登陆成功以后 在登陆环信
          //hxLogin()
            pd = new ProgressDialog(LoginActivity.this);
            pd.setCanceledOnTouchOutside(false);
            pd.setMessage("正在登陆...");
            pd.show();
            if(username.equals("nihao")&& password.equals("321")){
                //Toast.makeText(getApplicationContext(), "登录失: ",200).show();
                hxLogin(username, "nihao"+"iec");
            }else{
                Toast.makeText(getApplicationContext(), "xxx: ",200).show();
                pd.dismiss();
            }
        }
	}

	// 环信登陆的方法
	private void hxLogin(){

        DemoApplication.currentUserNick = usernameEditText.getText().toString()+"abc";

        final String username = usernameEditText.getText().toString();
        final String password = passwordEditText.getText().toString();

        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
            progressShow = true;
           
            pd.setCanceledOnTouchOutside(false);
            pd.setOnCancelListener(new OnCancelListener() {

                @Override
                public void onCancel(DialogInterface dialog) {
                    progressShow = false;
                }
            });
            pd.setMessage("正在登陆...");
            pd.show();
            final long start = System.currentTimeMillis();
            // 调用sdk登陆方法登陆聊天服务器
            EMChatManager.getInstance().login(username, password, new EMCallBack() {

                @Override
                public void onSuccess() {
                    //umeng自定义事件，开发者可以把这个删掉
                    loginSuccess2Umeng(start);
                    
                    if (!progressShow) {
                        return;
                    }
                    // 登陆成功，保存用户名密码
                    DemoApplication.getInstance().setUserName(username);
                    DemoApplication.getInstance().setPassword(password);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            pd.setMessage("正在获取好友和群聊列表...");
                        }
                    });
                    try {
                        // ** 第一次登录或者之前logout后，加载所有本地群和回话
                        // ** manually load all local groups and
                        // conversations in case we are auto login
                        EMGroupManager.getInstance().loadAllGroups();
                        EMChatManager.getInstance().loadAllConversations();

                        // demo中简单的处理成每次登陆都去获取好友username，开发者自己根据情况而定
                        List<String> usernames = EMContactManager.getInstance().getContactUserNames();
                        EMLog.d("roster", "contacts size: " + usernames.size());
                        Map<String, User> userlist = new HashMap<String, User>();
                        for (String username : usernames) {
                            User user = new User();
                            user.setUsername(username);
                            setUserHearder(username, user);
                            userlist.put(username, user);
                        }
                        // 添加user"申请与通知"
                        User newFriends = new User();
                        newFriends.setUsername(Constant.NEW_FRIENDS_USERNAME);
                        newFriends.setNick("申请与通知");
                        newFriends.setHeader("");
                        userlist.put(Constant.NEW_FRIENDS_USERNAME, newFriends);
                        // 添加"群聊"
                        User groupUser = new User();
                        groupUser.setUsername(Constant.GROUP_USERNAME);
                        groupUser.setNick("群聊");
                        groupUser.setHeader("");
                        userlist.put(Constant.GROUP_USERNAME, groupUser);

                        // 存入内存
                        DemoApplication.getInstance().setContactList(userlist);
                        // 存入db
                        UserDao dao = new UserDao(LoginActivity.this);
                        List<User> users = new ArrayList<User>(userlist.values());
                        dao.saveContactList(users);

                        // 获取群聊列表(群聊里只有groupid和groupname等简单信息，不包含members),sdk会把群组存入到内存和db中
                        EMGroupManager.getInstance().getGroupsFromServer();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //更新当前用户的nickname 此方法的作用是在ios离线推送时能够显示用户nick
                    boolean updatenick = EMChatManager.getInstance().updateCurrentUserNick(DemoApplication.currentUserNick);
                    if (!updatenick) {
                        EMLog.e("LoginActivity", "update current user nick fail");
                    }

                    if (!LoginActivity.this.isFinishing())
                        pd.dismiss();
                    // 进入主页面
                    //startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                }

                @Override
                public void onProgress(int progress, String status) {

                }

                @Override
                public void onError(final int code, final String message) {
                    loginFailure2Umeng(start,code,message);
                    System.out.println(code+"code");
                    System.out.println(message+"message");
                    if (!progressShow) {
                        return;
                    }
                    //以前没有注册成功  这次曲注册吧
                    if(code==-1005){
                        
                        
                    }
                    runOnUiThread(new Runnable() {
                        public void run() {
                            pd.dismiss();
                            Toast.makeText(getApplicationContext(), "登录失败: " + message, 0).show();
                           // startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        }
                    });
                }
            });

        }

    
	}
	
	// 环信登陆的方法
	private void hxLogin(final String username,final String password){
	    
	        DemoApplication.currentUserNick = usernameEditText.getText().toString()+"abc";
	        // 调用sdk登陆方法登陆聊天服务器
	        EMChatManager.getInstance().login(username, password, new EMCallBack() {
	            
	            @Override
	            public void onSuccess() {
	          
	                // 登陆成功，保存用户名密码
	                DemoApplication.getInstance().setUserName(username);
	                DemoApplication.getInstance().setPassword(password);
	                runOnUiThread(new Runnable() {
	                    public void run() {
	                    }
	                });
	                try {
	                    // ** 第一次登录或者之前logout后，加载所有本地群和回话
	                    // ** manually load all local groups and
	                    // conversations in case we are auto login
	                    EMGroupManager.getInstance().loadAllGroups();
	                    EMChatManager.getInstance().loadAllConversations();
	                    
	                    // demo中简单的处理成每次登陆都去获取好友username，开发者自己根据情况而定
	                    List<String> usernames = EMContactManager.getInstance().getContactUserNames();
	                    EMLog.d("roster", "contacts size: " + usernames.size());
	                    Map<String, User> userlist = new HashMap<String, User>();
	                    for (String username : usernames) {
	                        User user = new User();
	                        user.setUsername(username);
	                        setUserHearder(username, user);
	                        userlist.put(username, user);
	                    }
	                    // 添加user"申请与通知"
	                    User newFriends = new User();
	                    newFriends.setUsername(Constant.NEW_FRIENDS_USERNAME);
	                    newFriends.setNick("申请与通知");
	                    newFriends.setHeader("");
	                    userlist.put(Constant.NEW_FRIENDS_USERNAME, newFriends);
	                    // 添加"群聊"
	                    User groupUser = new User();
	                    groupUser.setUsername(Constant.GROUP_USERNAME);
	                    groupUser.setNick("群聊");
	                    groupUser.setHeader("");
	                    userlist.put(Constant.GROUP_USERNAME, groupUser);
	                    
	                    // 存入内存
	                    DemoApplication.getInstance().setContactList(userlist);
	                    // 存入db
	                    UserDao dao = new UserDao(LoginActivity.this);
	                    List<User> users = new ArrayList<User>(userlist.values());
	                    dao.saveContactList(users);
	                    
	                    // 获取群聊列表(群聊里只有groupid和groupname等简单信息，不包含members),sdk会把群组存入到内存和db中
	                    EMGroupManager.getInstance().getGroupsFromServer();
	                    pd.dismiss();
                        finish();
	                } catch (Exception e) {
	                    e.printStackTrace();
	                    pd.dismiss();
	                    finish();
	                }
	                //更新当前用户的nickname 此方法的作用是在ios离线推送时能够显示用户nick
	                boolean updatenick = EMChatManager.getInstance().updateCurrentUserNick(DemoApplication.currentUserNick);
	                if (!updatenick) {
	                    EMLog.e("LoginActivity", "update current user nick fail");
	                }
	                
	                //if (!LoginActivity.this.isFinishing())
	                    
	                // 进入主页面
	                //startActivity(new Intent(LoginActivity.this, MainActivity.class));
	            }
	            
	            @Override
	            public void onProgress(int progress, String status) {
	                
	            }
	            
	            @Override
	            public void onError(final int code, final String message) {
	               
	                //以前没有注册成功  这次去注册吧
	                if(code==-1005 && register==false){
	                    register(username, password);
	                   
	                }
	                // 注册过一次还是没成功放弃吧
	                if( register ==true){
	                    runOnUiThread(new Runnable() {
	                        public void run() {
	                            // startActivity(new Intent(LoginActivity.this, MainActivity.class));
	                            pd.dismiss();
	                            finish();
	                        }
	                    });
	                }
	                
	            }
	        });
	        
	    }
	    
	
	   /**
     * 注册
     * 
     * @param view
     */
    public void register(final String username,final String pwd) {
        
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
                                DemoApplication.getInstance().setUserName(username);
                                //Toast.makeText(getApplicationContext(), "注册成功", 0).show();
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
                                pd.dismiss();
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

		if (DemoApplication.getInstance().getUserName() != null) {
			usernameEditText.setText(DemoApplication.getInstance().getUserName());
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
	
	private void loginSuccess2Umeng(final long start) {
		runOnUiThread(new Runnable() {
			public void run() {
				long costTime = System.currentTimeMillis() - start;
				Map<String, String> params = new HashMap<String, String>();
				params.put("status", "success");
				MobclickAgent.onEventValue(LoginActivity.this, "login1", params, (int) costTime);
				MobclickAgent.onEventDuration(LoginActivity.this, "login1", (int) costTime);
			}
		});
	}
	private void loginFailure2Umeng(final long start, final int code, final String message) {
		runOnUiThread(new Runnable() {
			public void run() {
				long costTime = System.currentTimeMillis() - start;
				Map<String, String> params = new HashMap<String, String>();
				params.put("status", "failure");
				params.put("error_code", code + "");
				params.put("error_description", message);
				MobclickAgent.onEventValue(LoginActivity.this, "login1", params, (int) costTime);
				MobclickAgent.onEventDuration(LoginActivity.this, "login1", (int) costTime);
				
			}
		});
	}
}
