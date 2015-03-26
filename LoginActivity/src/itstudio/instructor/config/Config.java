/**
 * 
 */
package itstudio.instructor.config;

import android.os.Environment;

/**
 * @author miss
 *
 */
public class Config {
	
	// 所有接口的url
	//public static final String BASE_URL = "http://218.198.127.120:80/assistant/"; 
	public static final String BASE_URL = "http://192.168.1.101:8080/assistant/"; 
	//public static final String BASE_URL = "http://202.196.1.165:80/assistant/"; 
	//public static final String BASE_URL = "http://59.70.149.8:8080/assistant/"; 
	public static final String BASE_URL_API = BASE_URL+"api/"; 
	public static final String IMG_URL = BASE_URL+"img/"; 
	public static final String NOTICE_URL = BASE_URL+"imgnotice/"; 
	public static final String NEWS_URL = BASE_URL+"imgnews/"; 
	public static final String HEAD_URL = BASE_URL+"imghead/"; 
	public static final int PAGE_NUM = 10; 
	
	public static final String  AC_NEWS_lIST = "";                   //news 列表
	public static final String  AC_NEWS_SEND = "news_send"; 		 //发布news
	public static final String  AC_NEWS_LATEST = "news_latest"; 	 //最新的news
	public static final String  AC_NEWS_PERSON = "news_person"; 	 //个人的news
	public static final String  AC_NEWS_FOLLOW = "news_follow"; 	 //关注的人的news
	public static final String  AC_NEWS_PIC = "upload_newspic"; 	 //发布news 图片
	public static final String  AC_USER_HEAD = "upload_head"; 	     //修改头像
	public static final String  AC_NOTICE_LIST = "notice_list"; 	 //通知list
	public static final String  AC_USER_LOGIN = "user_login"; 		 //登陆		    
	public static final String  AC_USER_FANS = "user_fans"; 		 //粉丝		    
	public static final String  AC_USER_FOLLOW = "user_following";     //关注的人		    
	public static final String  AC_USER_INTRO = "user_modify_intro";    //简介		    
	public static final String  AC_USER_EMAIL = "user_modify_email";    //邮箱		    
	public static final String  AC_USER_PASSWORD = "user_modify_password";    //密码		    
	public static final String  AC_COMMENT_LIST = "comment_list";     //评论列表		    
	public static final String  AC_COMMENT_ADD = "comment_add";     //评论添加	    
	public static final String  AC_FOllOW_CANCEL = "follow_cancel";     //取消关注	    
	public static final String  AC_FOLLOW_ADD = "follow_add";     //添加关注	    
	public static final String  AC_USER_LOGOUT = "user_logOut"; 	 //注销
	public static final String  AC_USER_SEEK_TEACHER = "user_seek_teacher";//教师搜索
	public static final String  AC_USER_TEACHER = "user_teacher"; 	 //教师
	public static final String  AC_USER_SEEK_PUB = "user_seek_pub"; 	 //公众号搜索
	public static final String  AC_USER_PUB = "user_publicnum"; 	 //公众号
	public static final String  AC_COLLECT_LIST = "collect_list"; 	 //收藏列表
	public static final String  AC_COLLECT_CANCEL = "collect_cancel";//取消收藏
	public static final String  AC_COLLECT_ADD = "collect_add";   //添加收藏
	public static final String  AC_COMMENT_MY_REPLY = "comment_myreply";//我的回复
	public static final String  AC_COMMENT_REPLY_ME = "comment_replyme";//回复我的
	public static final String  AC_UPDATE = "get_update";//更新app
	public static final String  AC_OPINION = "opinion_feedback";//意见反馈
	public static final String  AC_FINDPSW = "findpsw_forget.action";//找回密码
	public static final String  AC_FINDFRIENDS = "find_friends.action";//找回密码
	public static final String  AC_FINDUSER = "find_user.action";//找回密码
	public static final String  LOGIN_KEY = "iec_wq2014";//找回密码
	
	public static final String apkUrl = BASE_URL+"file/"+"instructor.apk";
	
	public final static String APP_NAME="instructor";
	public final static String IS_USE="isUse";
	public final static int TIME_DELAY_GUIDE=1200;
	public final static int TIME_DELAY_WELCOME=1200;
	
	public static final String  BASE_PATH= Environment.getExternalStorageDirectory().getPath(); 
	public static final String  TEMP_PATH= BASE_PATH+"/Android/data/itstudio.app.wq/temp/";// 临时文件地址 
	public static final String  PACKAGE_PATH= BASE_PATH+"/Android/data/itstudio.app.wq/";// 临时文件地址 

	public static boolean isEditDrafts = false;
	
	public final static int REQUEST_LOCAL_PIC=101; //本地照片
	public final static int REQUEST_TAKE_PHOTO=102;// 拍照
	public final static int REQUEST_PREVIEW_PIC=103;// 预览照片
	
	public final static int RESULT_LOCAL_IMG =104;// 选择本地照片返回
	public final static int RESULT_PREVIEW_PIC=105;// 选择预览照片返回
	public static boolean IS_SEND = false;//是否正在发送news
	
	public final static String SUCCESS = "success";	  //成功
	public final static String FAIL = "fail";		  //失败
	public final static String LOGIN = "login";		  //登陆
	public final static String ERROR = "error"; 	  //错误
	
	
	

}
