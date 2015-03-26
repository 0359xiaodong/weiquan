package itstudio.instructor.ui;

import itstudio.instructor.adapter.CommentAdapter;
import itstudio.instructor.config.Config;
import itstudio.instructor.config.MyApplication;
import itstudio.instructor.entity.Comment;
import itstudio.instructor.entity.News;
import itstudio.instructor.http.TwitterRestClient;
import itstudio.instructor.util.PixelUtil;
import itstudio.instructor.util.TimeUtil;
import itstudio.instructor.xlistview.XListView;
import itstudio.instructor.xlistview.XListView.IXListViewListener;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.Header;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.easemob.chatuidemo.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

/**
 * @Description
 * @author MR.Wang
 * @date 2014-7-14 下午9:42:06
 * @version V1.0
 */
public class NewsDetailActivity extends Activity implements IXListViewListener {

	//view
    private LinearLayout loadingLayout;
    private LinearLayout loadNoneLayout;
	private LinearLayout imgeLayout;
	private View back_layout;
	private XListView xListview;
	private View bt_setting;
	private View tv_comment;
	private Button bt_commment;
	private EditText et_comment;
	private PopupWindow popupWindow;
	private View pop_view;
	private View view_share;
	private View view_collect;
	private String url;
	// config
	DisplayImageOptions options;
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	//data
	private CommentAdapter adapter;
	private News news ;
	private int page=0;
	public static String replyId ;
	public static String replyName ;
	
	List<Comment> commentList = new ArrayList<Comment>();
	
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_news_detail);
		initImageOptions();
		news = (News) getIntent().getExtras().get("news");
		back_layout = findViewById(R.id.content_back_layout);
		loadingLayout = (LinearLayout) findViewById(R.id.view_loading);
		loadNoneLayout = (LinearLayout) findViewById(R.id.view_load_nodata);
		bt_setting = findViewById(R.id.bt_setting);
		tv_comment = findViewById(R.id.tv_comment);
		bt_commment = (Button) findViewById(R.id.bt_commment);
		et_comment = (EditText) findViewById(R.id.et_comment);
		pop_view = getLayoutInflater().inflate(R.layout.share_popup_window, null);
		view_collect = pop_view.findViewById(R.id.collectview);
		view_share = pop_view.findViewById(R.id.shareview);
		
		ViewOncliclListenter listenter = new ViewOncliclListenter();
		view_collect.setOnClickListener(listenter);
		view_share.setOnClickListener(listenter);
		back_layout.setOnClickListener(listenter);
		bt_setting.setOnClickListener(listenter);
		tv_comment.setOnClickListener(listenter);
		bt_commment.setOnClickListener(listenter);
		
		replyId = news.getUser().getId();
		
		url = Config.BASE_URL+"news_detail.action?id="+news.getId();
		//加载文字
		TextView tv_title = (TextView) findViewById(R.id.tv_title);
		TextView tv_anthor = (TextView) findViewById(R.id.tv_anthor);
		TextView tv_date = (TextView) findViewById(R.id.tv_date);
		TextView tv_content = (TextView) findViewById(R.id.tv_content);
		
		tv_title.setText(news.getTitle());
		tv_date.setText(TimeUtil.dateToString(news.getPostTime(),TimeUtil.FORMAT_DATE_TIME));
		tv_anthor.setText(news.getAuthorName());
		tv_content.setText("     "+news.getContent());
		
		if(news.getNewsPics()!=null && news.getNewsPics()!=""){
			// 动态添加图片
			
			imgeLayout = (LinearLayout) findViewById(R.id.imageview);
			int width = (int) (PixelUtil.screenWidthpx(NewsDetailActivity .this)*0.8);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					width ,PixelUtil.dp2px(150));
			
			lp.bottomMargin = 10;
			lp.gravity = Gravity.CENTER_HORIZONTAL;
			ImageView imageView;
			String picture[] = news.getNewsPics().split(";");

			for (int index = 0; index < picture.length; index++) {

				imageView = new ImageView(NewsDetailActivity.this);
				imageView.setLayoutParams(lp);
				imageLoader.displayImage(Config.NEWS_URL +picture[index],
						imageView, options);
				imgeLayout.addView(imageView);
				final String imgUrl=Config.NEWS_URL +picture[index];
				imageView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(NewsDetailActivity.this, PhotoViewActivity.class);
						intent.putExtra("imageUrl",imgUrl);
						startActivity(intent);
					}
				});
			}
		}

		
		// 填充评价listview
		
		adapter = new CommentAdapter(this, commentList,
				R.layout.listitem_comment,et_comment);

		xListview = (XListView) findViewById(R.id.listview_comment);
		xListview.setPullLoadEnable(true);
		xListview.setPullRefreshEnable(false);
		xListview.setXListViewListener(this);
		xListview.setAdapter(adapter);
		//setListViewHeightBasedOnChildren(xListview);// 解决listview与scrollview冲突问题
		loadComment(1,true);

	}

	//加载评论
	private void loadComment(int page_num ,final boolean refesh){
		
		RequestParams params = new RequestParams();
		params.put("comment.news.id", news.getId());
		params.put("page", page_num); 
		TwitterRestClient.post(Config.AC_COMMENT_LIST, params, new AsyncHttpResponseHandler(){
			
			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				// TODO Auto-generated method stub
				onLoad();
				loadingLayout.setVisibility(View.GONE);
				loadNoneLayout.setVisibility(View.GONE);
			}

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				//加载成功
				loadingLayout.setVisibility(View.GONE);
				onLoad();
				Gson gson = new GsonBuilder()  
				  .setDateFormat("yyyy-MM-dd HH:mm:ss")  
				  .create();
				commentList  = gson.fromJson( new String(arg2), new TypeToken<List<Comment>>(){}.getType());
				adapter.appendToList(commentList);
				page = adapter.appendData(commentList,refesh);
				if(page==0){
					loadNoneLayout.setVisibility(View.VISIBLE);
				}
				// 是否有下一页
				if(commentList.size()!=0 && commentList.size()==10){
					xListview.setPullLoadEnable(true);
					page =page+1;
				}
				else{
					xListview.setPullLoadEnable(false);
				}
				setListViewHeightBasedOnChildren(xListview);
				
			}});
	}
	
	//添加评论
	private void addComment(){
		
		if(MyApplication.user==null){
			
			//ToastUtil.showInfoMsg(NewsDetailActivity.this, R.string.please_login);
			return;
		}
		if(TextUtils.isEmpty(et_comment.getText())){
			
			//ToastUtil.showErrorMsg(NewsDetailActivity.this, R.string.error_comment_null);
			return;
		}
		if(et_comment.getText().length()>150){
			
			//ToastUtil.showErrorMsg(NewsDetailActivity.this, R.string.error_comment_tolong);
			return;
		}
		final String content = et_comment.getText().toString();
		RequestParams params = new RequestParams();
		params.put("content", content);
		params.put("replyId", replyId);
		params.put("comment.news.id", news.getId());
		if(replyName!=null){
			params.put("replyName",replyName);
		}
		loadingLayout.setVisibility(View.VISIBLE);
		adapter.clear();
		setListViewHeightBasedOnChildren(xListview);
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(NewsDetailActivity.this.getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
		TwitterRestClient.post(Config.AC_COMMENT_ADD, params, new AsyncHttpResponseHandler(){
			

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				// TODO Auto-generated method stub
				onLoad();
			}

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {

				String result = new String(arg2);
				
				if(result.indexOf("success")!=-1){
					
/*					Comment comment = new Comment();
					comment.setAuthorId(MyApplication.user.getId());
					comment.setAuthorName(MyApplication.user.getName());
					comment.setContent(content);
					comment.setPostTime(new Date());
					comment.setReplyName(replyName);
					adapter.appendToFirst(comment);*/
					et_comment.setText("");
					et_comment.setHint(R.string.want_comment);
					loadNoneLayout.setVisibility(View.GONE);
					replyId = news.getUser().getId();
					replyName = null;

					loadComment(1,true);
					setListViewHeightBasedOnChildren(xListview);
				}
				if(result.indexOf("login")!=-1){
					//LoginUtil.Login(NewsDetailActivity.this);
				}
			}});
	}
	//添加收藏
	private void addCollect(){
		
		if(MyApplication.user==null){
			
			//ToastUtil.showInfoMsg(NewsDetailActivity.this, R.string.please_login);
			return;
		}

		RequestParams params = new RequestParams();
		params.put("collect.news.id", news.getId());

		TwitterRestClient.post(Config.AC_COLLECT_ADD, params, new AsyncHttpResponseHandler(){
			
			
			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				// TODO Auto-generated method stub
				onLoad();
			}
			
			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				
				String result = new String(arg2);
				
				if(result.indexOf("success")!=-1){
					
					//ToastUtil.showSuccessMsg(NewsDetailActivity.this, R.string.success_collect);
				}
				if(result.indexOf("login")!=-1){
					//LoginUtil.Login(NewsDetailActivity.this);
				}
			}});
	}
	
	// 解决listview与scrollview冲突问题
	private void setListViewHeightBasedOnChildren(ListView listView) {

		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}

		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount())+PixelUtil.dp2px(25));
		listView.setLayoutParams(params);
	}

	private void initImageOptions() {
		options = new DisplayImageOptions.Builder().cacheInMemory(true)
				.showImageOnLoading(R.drawable.bg_load_loading)
				.showImageOnFail(R.drawable.bg_load_fail)
				.cacheOnDisc(true).considerExifParams(true)
				.displayer(new FadeInBitmapDisplayer(200))
				.bitmapConfig(Bitmap.Config.ALPHA_8).build();
	}

	class ViewOncliclListenter implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.content_back_layout:
				finish();
				break;
			case R.id.collectview:
				addCollect();
				if(popupWindow.isShowing()){
					popupWindow.dismiss();
				}
				break;
			case R.id.shareview:
				if(popupWindow.isShowing()){
					popupWindow.dismiss();
				}
				showShare();
				break;
			case R.id.bt_commment:
				addComment();
				break;
			case R.id.tv_comment:
				replyId = news.getUser().getId();
				replyName = null;
				et_comment.setHint(R.string.want_comment);
				et_comment.setFocusableInTouchMode(true);
				et_comment.requestFocus();
				et_comment.setFocusable(true);
				InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.showSoftInput(et_comment,InputMethodManager.SHOW_IMPLICIT);
				break;
			case R.id.bt_setting:
				popupWindow = new PopupWindow(pop_view,
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
						true);

				popupWindow.setTouchable(true);
				popupWindow.setOutsideTouchable(true);
				// 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
				// 我觉得这里是API的一个bug
				popupWindow.setBackgroundDrawable(new BitmapDrawable(
						getResources(), (Bitmap) null));

				// 设置好参数之后再show
				popupWindow.showAsDropDown(bt_setting, 0, 0);
				break;

			default:
				break;
			}
		}
	}

	private void showShare() {
/*		ShareSDK.initSDK(this,"2bc69c2f243c");
		OnekeyShare oks = new OnekeyShare();
		// 关闭sso授权
		oks.disableSSOWhenAuthorize();

		// 分享时Notification的图标和文字
		oks.setNotification(R.drawable.icon_app,getString(R.string.app_name));
		// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用         // 标题
		oks.setTitle(news.getTitle());
		// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
		oks.setTitleUrl(url);
		// text是分享文本，所有平台都需要这个字段            //内容
		oks.setText(news.getContent());
		// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
		//加上会分享失败
		//oks.setImagePath("/sdcard/test.jpg");
		// url仅在微信（包括好友和朋友圈）中使用
		oks.setUrl(url);
		// comment是我对这条分享的评论，仅在人人网和QQ空间使用
		oks.setComment("我是测试评论文本");
		// site是分享此内容的网站名称，仅在QQ空间使用
		oks.setSite(getString(R.string.app_name));
		// siteUrl是分享此内容的网站地址，仅在QQ空间使用
		oks.setSiteUrl(url);

		// 启动分享GUI
		oks.show(this);*/
	}


	// 设置下拉刷新时间
	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub


	}

	// 设置loadmore刷新时间
	@Override
	public void onLoadMore() {
		
		loadComment(page+1,false);

	}

	private void onLoad() {
		xListview.stopRefresh();
		xListview.stopLoadMore();
	}

}
