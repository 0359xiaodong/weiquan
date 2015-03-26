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
import itstudio.instructor.widget.DynamicBox;
import itstudio.instructor.xlistview.XListView;
import itstudio.instructor.xlistview.XListView.IXListViewListener;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.apache.http.Header;
import com.easemob.chatuidemo.R;
import com.easemob.chatuidemo.db.NewsDao;
import com.easemob.chatuidemo.db.NoticeDao;
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
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Animation.AnimationListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
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
public class HomeFragment extends Fragment implements IXListViewListener, OnSingleTouchListener {

	// view
	public static View rootView;
	private XListView xListView;
	private Context context;
	private ChildViewPager viewPager;
	private List<View> dots;
	private TextView tv_title;
	private ScheduledExecutorService scheduledExecutorService;
	protected ImageLoader imageLoader = ImageLoader.getInstance();

	// adapter
	private NewsListAdapter adapter;
	// data
	private List<Notice> noticeList = new ArrayList<Notice>();
	private List<News> newsList;
	private boolean notice_success = false;// 通知是否加载成功
	private int page = 0;
	private int currentItem = 0;
	private DynamicBox box;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
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
		viewPager.setAdapter(new NoticePagerAdapter(noticeList, context));
		// 设置预加载3 修复切换白屏bug
		viewPager.setOffscreenPageLimit(3);
		viewPager.setOnPageChangeListener(new MyPageChangeListener());
		viewPager.setOnSingleTouchListener(this);

		// initListView
		xListView = (XListView) view.findViewById(R.id.list);
		xListView.addHeaderView(header);
		xListView.setPullLoadEnable(false);
		xListView.setPullRefreshEnable(true);
		adapter = new NewsListAdapter(context, newsList);
		AnimationAdapter animAdapter = new SwingBottomInAnimationAdapter(adapter);
		animAdapter.setAbsListView(xListView);
		animAdapter.setInitialDelayMillis(100);
		xListView.setAdapter(animAdapter);
		xListView.setXListViewListener(HomeFragment.this);
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
				intent.putExtra("news", news);
				context.startActivity(intent);

			}
		});
		
		xListView.setOnScrollListener((new PauseOnScrollListener(imageLoader,false, false)));
		
		//loadNews(1, true);
        //loadNotice();
        //LoginUtil.Login(context);
		String notice = new NoticeDao(getActivity()).getNitice();
        if (notice != null) {
            notice_success = true;
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
            noticeList = gson.fromJson(new String(notice),new TypeToken<List<Notice>>() {}.getType());
            viewPager.setAdapter(new NoticePagerAdapter(noticeList, context));
        }
        String news = new NewsDao(getActivity()).getNewsJson();
        if(news!=null){
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
            newsList = gson.fromJson(news,new TypeToken<List<News>>() {}.getType());
            adapter.appendData(newsList, true);
            xListView.autoRefresh();// 自动刷新
        }
        else{
            box = new DynamicBox(getActivity(),xListView);
            box.setDefaultLoadView();
            box.showLoadingLayout();
            //加载
            loadNews(1, true);
        }
		loadNotice();
		
	}

	/*加载的逻辑
	 * 先从数据库曲 有的话就展现 然后去主动刷新 存数据库
	 * 没有的话 展示正在加载中
	 * */
	// 加载通知
	private void loadNotice() {
		RequestParams params = new RequestParams();
		TwitterRestClient.post(Config.AC_NOTICE_LIST, params, new AsyncHttpResponseHandler() {

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				// TODO Auto-generated method stub
				onLoad();
			}

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				// 加载成功
				new NoticeDao(getActivity()).saveNitice(new String(arg2));
				Gson gson = new GsonBuilder().setDateFormat(TimeUtil.FORMAT_DATE_TIME_S).create();
				noticeList = gson.fromJson(new String(arg2), new TypeToken<List<Notice>>() {
				}.getType());
				viewPager.setAdapter(new NoticePagerAdapter(noticeList, context));
				notice_success = true;

			}
		});

	}

	// 加载news
	private void loadNews(final int page_num, final boolean refesh) {

		RequestParams params = new RequestParams();
		params.put("page", page_num);
		TwitterRestClient.post(Config.AC_NEWS_LATEST, params,
				new AsyncHttpResponseHandler() {

					@Override
					public void onFailure(int arg0, Header[] arg1, byte[] arg2,
							Throwable arg3) {
						// TODO Auto-generated method stub
						onLoad();
						if (page == 0) {
						    if(box!=null){
						        box.showExceptionLayout();
						    }
						}
					}

					@Override
					public void onSuccess(int arg0, Header[] arg1, byte[] data) {
						// 加载成功
					    if(box!=null){
					         box.hideAll();
					    }
						onLoad();
                        Gson gson = new GsonBuilder().setDateFormat(TimeUtil.FORMAT_DATE_TIME_S).create();
						String response = new String(data);
						newsList = gson.fromJson(new String(data),new TypeToken<List<News>>() {}.getType());
						
						if(page_num==1){
						    new NewsDao(getActivity()).saveNews(response);
						    adapter.clearAll();
						}
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
			curDate = TimeUtil.dateToString(new Date(), TimeUtil.FORMAT_MONTH_DAY_TIME_EN);
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

	
}
