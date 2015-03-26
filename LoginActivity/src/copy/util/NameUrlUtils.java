package copy.util;

import itstudio.instructor.config.MyApplication;
import itstudio.instructor.entity.User;
import itstudio.instructor.util.FileUtils;

import java.util.HashMap;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.chatuidemo.R;
import com.easemob.chatuidemo.db.NameUrlDao;
import com.easemob.chatuidemo.db.UserDao;

import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

public class NameUrlUtils {

	private static final HashMap<String, Long> failedMap = new HashMap<String, Long>();

	public static void clearKey(String key) {
		failedMap.remove(key);
	}

	public static final int delayTime = 1000 * 60 * 2;
	public static final int stayTime = 1000 * 60 * 5;

	private static Runnable run = new Runnable() {
		@Override
		public void run() {
			Tools.exec(new Runnable() {
				@Override
				public void run() {

					Tools.log("clean url work doing");
					failedMap.clear();
					handler.postDelayed(this, delayTime);

				}
			});
		}
	};

	private static final Handler handler = new Handler();

	public static void startDealNameUrlMap() {
		handler.removeCallbacks(run);
		handler.postDelayed(run, delayTime);
	}

	/**
	 * 本地/网络 获取nameUrl对象
	 */
	public static void setNickNameAndHead(final TextView tv, final ImageView iv, final String mobile) {
		if (iv != null)
			iv.setImageResource(R.drawable.head_default_local);
		// 自己的头像
		if (MyApplication.user != null)
			if (MyApplication.user.getId().equals(mobile)) {
				if (tv != null)
					tv.setText(MyApplication.user.getName());
				if (iv != null)
					FileUtils.setImageHead(MyApplication.user.getHeadUrl(), iv);
				return;
			}

		NameUrl nn = CacheUtils.getNameUrl(mobile);
		if (nn != null) {
			if (tv != null)
				tv.setText(nn.getName());
			if (!Tools.isEmptyStr(nn.getHeadUrl()) && iv != null)
				FileUtils.setImageHead(nn.getHeadUrl(), iv);
			return;
		}

		NameUrl nh = NameUrlDao.getInstance().selectNameUrl(mobile);
		if (nh != null && !Tools.isEmptyStr(nh.getName())) {
			CacheUtils.saveNameUrlToCache(nh);
			if (tv != null)
				tv.setText(nh.getName());
			if (iv != null && !Tools.isEmptyStr(nh.getHeadUrl()))
				FileUtils.setImageHead(nh.getHeadUrl(), iv);
			return;
		}
		User user = new UserDao(MyApplication.applicationContext).getContactById(mobile);
		if (user != null && !Tools.isEmptyStr(user.getHeadUrl())) {
			NameUrl nh1 = new NameUrl();
			nh1.setId(user.getId());
			nh1.setName(user.getName());
			nh1.setHeadUrl(user.getHeadUrl());
			CacheUtils.saveNameUrlToCache(nh1);
			if (tv != null)
				tv.setText(user.getName());
			if (iv != null && !Tools.isEmptyStr(user.getHeadUrl()))
				FileUtils.setImageHead(user.getHeadUrl(), iv);
			return;
		}
		if (failedMap.containsKey(mobile))
			return;

		if (Tools.isNetWorkConnected(MyApplication.applicationContext))
			failedMap.put(mobile, System.currentTimeMillis());
		else
			return;

		NetUtils.getNameAndUrl(mobile, new ObjectRunnable() {
			@Override
			public void run(Object o) {
				NameUrl n = (NameUrl) o;
				if (iv != null)
					FileUtils.setImageHead(n.getHeadUrl(), iv);
				if (tv != null)
					tv.setText(n.getName());
			}
		}, null);
	}

	public static void setNickName(final String mobile, final StringRunnable run) {
		NameUrl nn = CacheUtils.getNameUrl(mobile);
		if (nn != null) {
			run.run(nn.getName());
			return;
		}

		
		NameUrl nh = NameUrlDao.getInstance().selectNameUrl(mobile);
		if (nh != null && !Tools.isEmptyStr(nh.getName())) {
			CacheUtils.saveNameUrlToCache(nh);
			run.run(nh.getName());
			return;
		}
		
		if (failedMap.containsKey(mobile))
			return;
		
		if (Tools.isNetWorkConnected(MyApplication.applicationContext))
			failedMap.put(mobile, System.currentTimeMillis());
		else
			return;
		NetUtils.getNameAndUrl(mobile, new ObjectRunnable() {
			@Override
			public void run(Object o) {
				run.run(((NameUrl) o).getName());
			}
		}, new ExceptionRunnable() {
			@Override
			public void run(Throwable e, String str) {
				run.run(mobile);
			}
		});
	}


	public static void saveNameUrlToLocal(NameUrl n) {
	    NameUrlDao.getInstance().saveNameUrl(n);
		CacheUtils.saveNameUrlToCache(n);
	}

}
