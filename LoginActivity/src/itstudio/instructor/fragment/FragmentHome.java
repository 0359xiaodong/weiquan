package itstudio.instructor.fragment;

import itstudio.instructor.adapter.NoticePagerAdapter;
import itstudio.instructor.adapter.NewsListAdapter;
import itstudio.instructor.config.Config;
import itstudio.instructor.config.ImageOptionsUtil;
import itstudio.instructor.entity.News;
import itstudio.instructor.entity.Notice;
import itstudio.instructor.http.TwitterRestClient;
import itstudio.instructor.ui.NewsDetailActivity;
import itstudio.instructor.ui.NoticeDetailActivity;
import itstudio.instructor.util.TimeUtil;
import itstudio.instructor.widget.ChildViewPager;
import itstudio.instructor.widget.ChildViewPager.OnSingleTouchListener;
import itstudio.instructor.xlistview.XListView;
import itstudio.instructor.xlistview.XListView.IXListViewListener;
/*import itstudio.instructor.util.FixedSpeedScroller;
import itstudio.instructor.util.LoginUtil;
import itstudio.instructor.util.SharedPreferencesUtil;
import itstudio.instructor.util.TimeUtil;
import itstudio.instructor.widget.ChildViewPager;
import itstudio.instructor.widget.ChildViewPager.OnSingleTouchListener;
import itstudio.instructor.xlistview.XListView;
import itstudio.instructor.xlistview.XListView.IXListViewListener;*/
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.apache.http.Header;

import com.easemob.chatuidemo.R;
import com.easemob.chatuidemo.utils.FixedSpeedScroller;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nhaarman.listviewanimations.swinginadapters.AnimationAdapter;
import com.nhaarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Animation.AnimationListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 
 * @Description 首页 Fragment
 * 
 * @author MR.Wang
 * 
 * @date 2014-7-5 上午12:32:06
 * 
 * @version V1.0
 */

@SuppressLint("ValidFragment")
public class FragmentHome extends Fragment implements IXListViewListener
		,OnSingleTouchListener  {

	// view
	public static View rootView;
	private XListView xListView;
	private Context context;
	private ChildViewPager viewPager;
	private List<View> dots;
	private TextView tv_title;
	private LinearLayout loadingLayout;
	private LinearLayout loadFaillayout;
	private Button bn_refresh;

	private ScheduledExecutorService scheduledExecutorService;
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	DisplayImageOptions options_pager, options_head;

	// adapter
	private NewsListAdapter adapter;

	// data
	private List<Notice> noticeList = new ArrayList<Notice>();
	private List<News> newsList;

	private boolean notice_success = false;// 通知是否加载成功
	private int page = 0;
	private int currentItem = 0;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		options_pager = ImageOptionsUtil.newsImageOptions();
		options_head = ImageOptionsUtil.headImageOptions();
		

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		context = getActivity();
		if (rootView == null) {
			rootView = inflater.inflate(R.layout.fragment_home, container,false);
			initView(rootView, inflater);
		}
		ViewGroup parent = (ViewGroup) rootView.getParent();
		if (parent != null) {
			parent.removeView(rootView);
		}
		return rootView;
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	/**
	 * 初始化布局文件中的控件
	 */
	private void initView(View view, LayoutInflater inflater) {

		// initViewPager
		View header = inflater.inflate(R.layout.notice_viewpaper, xListView,
				false);
		dots = new ArrayList<View>();
		dots.add(header.findViewById(R.id.v_dot0));
		dots.add(header.findViewById(R.id.v_dot1));
		dots.add(header.findViewById(R.id.v_dot2));
		tv_title = (TextView) header.findViewById(R.id.tv_titles);
		viewPager = (ChildViewPager) header.findViewById(R.id.notice_vp);
		setViewPagerScrollSpeed();
		viewPager.setAdapter(new NoticePagerAdapter(noticeList, context,
				options_pager));
		// 设置预加载3 修复切换白屏bug
		viewPager.setOffscreenPageLimit(3);
		viewPager.setOnPageChangeListener(new MyPageChangeListener());
		viewPager.setOnSingleTouchListener(this);

		// initListView
		xListView = (XListView) view.findViewById(R.id.list);
		xListView.addHeaderView(header);
		xListView.setPullLoadEnable(false);
		xListView.setPullRefreshEnable(true);
		adapter = new NewsListAdapter(context, newsList, options_head);
		AnimationAdapter animAdapter = new SwingBottomInAnimationAdapter(
				adapter);
		animAdapter.setAbsListView(xListView);
		animAdapter.setInitialDelayMillis(100);
		xListView.setAdapter(animAdapter);
		xListView.setXListViewListener(FragmentHome.this);
		xListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == 0) {

					return;// 0 是header
				}
				TextView title = (TextView) view.findViewById(R.id.txt_title);
				title.setTextColor(context.getResources().getColor(R.color.light_black));
				Intent intent = new Intent();
				intent.setClass(context, NewsDetailActivity.class);
				News news = (News) adapter.getItem(position - 2); // 减去header
																	// 还有xlistview的header
				intent.putExtra("news", news);
				context.startActivity(intent);

			}
		});
		xListView.setOnScrollListener((new PauseOnScrollListener(imageLoader,
				false, false)));

		// initView
		loadingLayout = (LinearLayout) view.findViewById(R.id.view_loading);
		loadFaillayout = (LinearLayout) view.findViewById(R.id.view_load_fail);
		bn_refresh = (Button) view.findViewById(R.id.bn_refresh);
		bn_refresh.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				loadNews(1, true);
				loadNotice();
				//LoginUtil.Login(context);
			}
		});

		loadNotice();
		loadNews(1, true);

	}

	// 加载通知
	private void loadNotice() {
		RequestParams params = new RequestParams();
		TwitterRestClient.post(Config.AC_NOTICE_LIST, params,
				new AsyncHttpResponseHandler() {

					@Override
					public void onFailure(int arg0, Header[] arg1, byte[] arg2,
							Throwable arg3) {
						// TODO Auto-generated method stub
						onLoad();
					}

					@Override
					public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
						// 加载成功
						Gson gson = new GsonBuilder().setDateFormat(
								"yyyy-MM-dd HH:mm:ss").create();
						noticeList = gson.fromJson(new String(arg2),
								new TypeToken<List<Notice>>() {
								}.getType());
						viewPager.setAdapter(new NoticePagerAdapter(noticeList,
								context, options_pager));
						notice_success = true;

					}
				});

	}

	// 加载news
	private void loadNews(int page_num, final boolean refesh) {

		RequestParams params = new RequestParams();
		params.put("page", page_num);
		TwitterRestClient.post(Config.AC_NEWS_LATEST, params,
				new AsyncHttpResponseHandler() {

					@Override
					public void onFailure(int arg0, Header[] arg1, byte[] arg2,
							Throwable arg3) {
						// TODO Auto-generated method stub
						onLoad();
						loadingLayout.setVisibility(View.GONE);
						if (page == 0) {
							loadFaillayout.setVisibility(View.VISIBLE);
						}
					}

					@Override
					public void onSuccess(int arg0, Header[] arg1, byte[] data) {
						// 加载成功
						onLoad();
						loadingLayout.setVisibility(View.GONE);
						loadFaillayout.setVisibility(View.GONE);
						Gson gson = new GsonBuilder().setDateFormat(
								"yyyy-MM-dd HH:mm:ss").create();
						String response = new String(data);
						newsList = gson.fromJson(new String(data),
								new TypeToken<List<News>>() {
								}.getType());
						page = adapter.appendData(newsList, refesh);

						if (newsList != null && newsList.size() == 10) {
							xListView.setPullLoadEnable(true);
						} else {
							xListView.setNoMore();
						}

					}
				});
	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			viewPager.setCurrentItem(currentItem, true);
		};
	};

	@Override
	public void onStart() {
		scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
		// 当Activity显示出来后，每两秒钟切换一次图片显示
		scheduledExecutorService.scheduleAtFixedRate(new ScrollTask(), 1, 3,
				TimeUnit.SECONDS);
		super.onStart();
	}

	@Override
	public void onStop() {
		// 当Activity不可见的时候停止切换
		scheduledExecutorService.shutdown();
		super.onStop();
	}

	private class ScrollTask implements Runnable {

		public void run() {
			synchronized (viewPager) {
				currentItem = (currentItem + 1) % noticeList.size();
				handler.obtainMessage().sendToTarget();
			}
		}

	}

	private class MyPageChangeListener implements OnPageChangeListener {
		private int oldPosition = 0;

		/**
		 * This method will be invoked when a new page becomes selected.
		 * position: Position index of the new selected page.
		 */
		public void onPageSelected(final int position) {
			currentItem = position;

			Animation animation = new AlphaAnimation(1.0f, 0);
			animation.setDuration(200);
			animation.setInterpolator(new DecelerateInterpolator());
			animation.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation animation) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onAnimationRepeat(Animation animation) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onAnimationEnd(Animation animation) {
					// TODO Auto-generated method stub
					tv_title.setText(noticeList.get(position).getTitle());
					Animation animation1 = new AlphaAnimation(0, 1.0f);
					animation1.setDuration(200);
					animation1.setInterpolator(new AccelerateInterpolator());
					tv_title.startAnimation(animation1);
				}
			});
			tv_title.startAnimation(animation);
			dots.get(oldPosition).setBackgroundResource(R.drawable.dot_normal);
			dots.get(position).setBackgroundResource(R.drawable.dot_focused);
			oldPosition = position;

		}

		public void onPageScrollStateChanged(int arg0) {

		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}
	}

	/**
	 * 设置ViewPager的滑动速度
	 * 
	 * */
	private void setViewPagerScrollSpeed() {
		try {
			Field mScroller = null;
			mScroller = ViewPager.class.getDeclaredField("mScroller");
			mScroller.setAccessible(true);
			FixedSpeedScroller scroller = new FixedSpeedScroller(
					viewPager.getContext());
			mScroller.set(viewPager, scroller);
		} catch (NoSuchFieldException e) {

		} catch (IllegalArgumentException e) {

		} catch (IllegalAccessException e) {

		}
	}

	@Override
	public void onRefresh() {
		loadNews(1, true);
		String curDate = TimeUtil.dateToString(new Date(), TimeUtil.FORMAT_MONTH_DAY_TIME_EN);
		SharedPreferencesUtil.add(context, "news_time", curDate);
		// 通知 不用刷新
		if (!notice_success) {
			loadNotice();
		}
	}

	@Override
	public void onLoadMore() {
		loadNews(page + 1, false);
	}

	private void onLoad() {
		xListView.stopRefresh();
		xListView.stopLoadMore();
		// 获取当前时间
		String curDate = SharedPreferencesUtil.get(context, "news_time");
		if(!"".equals(curDate)){
			xListView.setRefreshTime(curDate);
		}else{
			curDate = TimeUtil
					.dateToString(new Date(), TimeUtil.FORMAT_MONTH_DAY_TIME_EN);
			xListView.setRefreshTime(curDate);
		}
		

	}

    @Override
    public void onSingleTouch() {
        // TODO Auto-generated method stub
        if (notice_success) {

            Intent intent = new Intent();
            intent.setClass(context, NoticeDetailActivity.class);
            Notice notice = noticeList.get(currentItem);
            intent.putExtra("notice", notice);
            context.startActivity(intent);
        }
    }

/*	@Override
	public void onSingleTouch() {
		if (notice_success) {

			Intent intent = new Intent();
			intent.setClass(context, NoticeDetailActivity.class);
			Notice notice = noticeList.get(currentItem);
			intent.putExtra("notice", notice);
			context.startActivity(intent);
		}
	}*/
	
}
