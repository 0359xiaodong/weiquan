package copy.util;

import itstudio.instructor.config.MyApplication;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.easemob.chatuidemo.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


/**
 * 工具类
 * 
 * @author yzx
 * 
 */
public class Tools {
	private static Context context;
	private static Toast to;
	private static AlertDialog ad;
	/**
	 * 线程池最大并发数
	 */
	public static final int THREAD_POOL_MAX = 15;

	public static final int CAMERA = 1;
	public static final int PICLIB = 2;
	public static final int CUT = 3;

	/**
	 * 初始化注入Context对象
	 */
	public static void setContext(Context context) {
		Tools.context = context;
	}

	/**
	 * 显示Toast
	 * 
	 * @param str
	 *            要显示的内容
	 */
	public static void showToast(final String str) {
		if (str == null)
			return;
		if (to == null)
			to = Toast.makeText(context, str, Toast.LENGTH_SHORT);
		else
			to.setText(str);
		to.show();
	}

	/**
	 * 显示Toast
	 * 
	 * @param str
	 *            要显示的内容
	 */
	public static void showToast(int strId) {

		if (to == null)
			to = Toast.makeText(context, strId, Toast.LENGTH_SHORT);
		else
			to.setText(strId);
		to.show();
	}

	/**
	 * log (user error)
	 * 
	 * @param str
	 */
	public static void log(String str) {
		if (str != null)
			Log.e("--------->>", str);
	}

	/**
	 * 启动一个新的Activity
	 * 
	 * @param from
	 *            起始Activity
	 * @param to
	 *            跳转Activity
	 */
	public static void goActivity(Context from, Class<? extends Activity> to, String[] keys, String[] values) {
		Intent in = new Intent(from, to);
		if (keys != null && values != null)
			for (int i = 0; i < keys.length; i++)
				in.putExtra(keys[i], values[i]);
		from.startActivity(in);
	}

/*	*//**
	 * 显示一个dialog
	 * 
	 * @param view
	 *            dialog的contentView
	 * @param x
	 *            显示的x坐标
	 * @param y
	 *            显示的y坐标
	 * @param width
	 *            dialog的宽度
	 * @param height
	 *            dialog的高度
	 * @param animation
	 *            显示和消失的动画效果
	 * @param cancleAble
	 *            是否可以cancle掉
	 * @return Dialog本身,返回之前已经show()了
	 *//*
	public static AlertDialog showDialog(Context context, View view, int x, int y, int width, int height, int animation, boolean cancleAble, boolean isAnimation) {
		AlertDialog ad;
		ad = new AlertDialog.Builder(context).create();
		ad.setCancelable(cancleAble);
		LayoutParams lp = ad.getWindow().getAttributes();
		lp.x = x;
		lp.y = y;
		ad.show();
		ad.setContentView(view);
		ad.getWindow().setLayout(width, height);
		if (isAnimation) {
			if (animation != 0)
				ad.getWindow().setWindowAnimations(animation);
			else
				ad.getWindow().setWindowAnimations(R.style.head_in_out);
		}
		ad.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
		return ad;
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static AlertDialog showDialog_noDark(Context context, View view, int x, int y, int width, int height, int animation, boolean cancleAble, boolean isAnimation) {
		if (SystemTool.getSDKVersion() < 12)
			return showDialog(context, view, x, y, width, height, animation, cancleAble, isAnimation);
		AlertDialog ad;
		ad = new AlertDialog.Builder(context, R.style.no_dark).create();
		ad.setCancelable(cancleAble);
		LayoutParams lp = ad.getWindow().getAttributes();
		lp.x = x;
		lp.y = y;
		ad.show();
		ad.setContentView(view);
		ad.getWindow().setLayout(width, height);
		if (isAnimation) {
			if (animation != 0)
				ad.getWindow().setWindowAnimations(animation);
			else
				ad.getWindow().setWindowAnimations(R.style.head_in_out);
		}
		ad.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
		return ad;
	}*/

	/**
	 * 使用SharedPreferences存储值
	 * 
	 * @param key
	 *            存储值的key
	 * @param value
	 *            存储值的value
	 * @param fileName
	 *            文件名称
	 */
	public static void saveValues(String key, String value) {
		context.getSharedPreferences("default_golfer_sp", Context.MODE_PRIVATE).edit().putString(key, value).commit();
	}

	/**
	 * 使用SharedPreferences取值
	 * 
	 * @param key
	 *            key
	 * @param fileName
	 *            xml文件名称
	 * @return 如果值不存在 ,返回空串
	 */
	public static String getValues(String key) {
		return context.getSharedPreferences("default_golfer_sp", Context.MODE_PRIVATE).getString(key, "");
	}

	/**
	 * 线程池
	 * 
	 * @param run
	 */
	public static void exec(Runnable run) {
		new Thread(run).start();
	}

	/**
	 * 检测网络是否可用
	 */
	public static boolean isNetWorkConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
			if (mNetworkInfo != null)
				return mNetworkInfo.isAvailable();
		}
		return false;
	}

	/**
	 * md5算法
	 * 
	 * @param text
	 *            一个字符串
	 * @return md5值 失败了返回null
	 */
	public static String MD5(String text) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(text.getBytes());
			byte[] bs = md.digest();
			int temp;
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < bs.length; i++) {
				temp = bs[i];
				if (temp < 0)
					temp += 256;
				if (temp < 16) {
					sb.append("0");
				}
				sb.append(Integer.toHexString(temp) + "");
			}
			return sb.toString();
		} catch (Exception e) {
			return null;
		}
	}

/*	*//**
	 * 弹出提示用户等待的dialog
	 *//*
	public static void showWaitDialog(final Context context, String str) {
		if (ad != null && ad.isShowing())
			ad.dismiss();
		View vv = View.inflate(context, R.layout.dialog_wait, null);
		if (!Tools.isEmptyStr(str)) {
			TextView tv = (TextView) vv.findViewById(R.id.tv);
			tv.setText(str);
		}
		ad = showDialog(context, vv, 0, 0, Tools.getsx() / 2, Tools.getsy() / 7, 0, false, false);
		ad.setCanceledOnTouchOutside(false);
	}*/
	
/*	*//**
	 * 弹出提示用户等待的dialog
	 *//*
	public static void showWaitDialog(final Context context, String str,boolean canCancle) {
		if (ad != null && ad.isShowing())
			ad.dismiss();
		View vv = View.inflate(context, R.layout.dialog_wait, null);
		if (!Tools.isEmptyStr(str)) {
			TextView tv = (TextView) vv.findViewById(R.id.tv);
			tv.setText(str);
		}
		ad = showDialog(context, vv, 0, 0, Tools.getsx() / 2, Tools.getsy() / 7, 0, false, false);
		ad.setCanceledOnTouchOutside(canCancle);
	}
	*/
/*	*//**
	 * 弹出提示用户等待的dialog
	 *//*
	public static Dialog showDialog(final Context context) {
		if (ad != null && ad.isShowing())
			ad.dismiss();
		ad = showDialog(context, View.inflate(context, R.layout.dialog_wait, null), 0, 0, Tools.getsx() / 2, Tools.getsy() / 7, 0, false, false);
		ad.setCanceledOnTouchOutside(false);
		return ad;
	}*/

	
	
	
	
	/**
	 * 显示一个dialog
	 * 
	 * @param view
	 *            dialog的contentView
	 * @param x
	 *            显示的x坐标
	 * @param y
	 *            显示的y坐标
	 * @param width
	 *            dialog的宽度
	 * @param height
	 *            dialog的高度
	 * @param animation
	 *            显示和消失的动画效果
	 * @param cancleAble
	 *            是否可以cancle掉
	 * @return Dialog本身,返回之前已经show()了
	 */
	public static AlertDialog showDialog(Context context, View view, int x, int y, int width, int height, int animation, boolean cancleAble, boolean isAnimation) {
		AlertDialog ad;
		ad = new AlertDialog.Builder(context).create();
		ad.setCancelable(cancleAble);
		LayoutParams lp = ad.getWindow().getAttributes();
		lp.x = x;
		lp.y = y;
		ad.show();
		ad.setContentView(view);
		ad.getWindow().setLayout(width, height);
		if (isAnimation) {
			if (animation != 0)
				ad.getWindow().setWindowAnimations(animation);
			else
				ad.getWindow().setWindowAnimations(R.style.head_in_out);
		}
		ad.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
		return ad;
	}
	
	
	
	/**
	 * 弹出提示用户等待的dialog
	 */
	public static void showWaitDialog(final Context context, String str,boolean canCancle) {
		if (ad != null && ad.isShowing())
			ad.dismiss();
		View vv = View.inflate(context, R.layout.dialog_wait, null);
		if (!Tools.isEmptyStr(str)) {
			TextView tv = (TextView) vv.findViewById(R.id.tv);
			tv.setText(str);
		}
		ad = showDialog(context, vv, 0, 0, Tools.getsx() / 2, Tools.getsy() / 7, 0, false, false);
		ad.setCanceledOnTouchOutside(canCancle);
	}
	
	/**
	 * 弹出提示用户等待的dialog
	 */
	public static Dialog showDialog(final Context context) {
		if (ad != null && ad.isShowing())
			ad.dismiss();
		ad = showDialog(context, View.inflate(context, R.layout.dialog_wait, null), 0, 0, Tools.getsx() / 2, Tools.getsy() / 7, 0, false, false);
		ad.setCanceledOnTouchOutside(false);
		return ad;
	}

	
	/**
	 * 取消等待的dialog
	 */
	public static void dismissWaitDialog() {
		if (ad != null)
			ad.dismiss();
	}
	public static int getsx() {
		return context.getResources().getDisplayMetrics().widthPixels;
	}

	public static int getsy() {
		int px = Resources.getSystem().getDimensionPixelSize(Resources.getSystem().getIdentifier("status_bar_height", "dimen", "android"));
		return context.getResources().getDisplayMetrics().heightPixels - px;
	}
	public static boolean isEmptyStr(String str) {
		return (str == null || str.trim().length() < 1);
	}

	private static Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			if (msg.obj != null) {
				Runnable r = (Runnable) msg.obj;
				r.run();
			}
		};
	};

	/**
	 * 主线程回到使用
	 */
	public static void runOnUI(Runnable run) {
		Message me = handler.obtainMessage();
		me.obj = run;
		handler.sendMessage(me);
	}

	/**
	 * 在主线程中执行
	 */
	public static void runOnUiThread(Runnable run, Activity a) {
		a.runOnUiThread(run);
	}



	/**
	 * 弹出输入法
	 */
	public static void showSoftInput(EditText et) {
		SystemClock.sleep(100);
		InputMethodManager is = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		is.showSoftInput(et, 0);
	}

	/**
	 * 隐藏输入法
	 */
	public static void hideSoftInput(EditText view) {
		InputMethodManager is = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		is.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

	public static boolean isSoftInputShowing() {
		InputMethodManager is = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		return is.isActive();
	}


	/**
	 * 跳转到系统图库
	 */
	public static void goPicLib(Activity a, int requestCode) {
		Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		a.startActivityForResult(i, requestCode);
	}

	public static String getAutoName() {
		return new StringBuffer().append("ii").append(System.currentTimeMillis()).append(".jpg").toString();
	}

	/**
	 * 将图片转化成base64编码
	 */
	public static String bitmapToBase64Str(Bitmap b) {
		if (b == null)
			return null;
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		b.compress(CompressFormat.PNG, 100, bao);
		try {
			bao.flush();
			return Base64.encodeToString(bao.toByteArray(), Base64.DEFAULT);
		} catch (IOException e) {
			return null;
		} finally {
			b.recycle();
		}
	}

	/**
	 * 将file转化成base64编码
	 */
	public static String fileToBase64Str(String path) {
		if (path == null)
			return null;
		try {
			File file = new File(path);
			FileInputStream inputFile = new FileInputStream(file);
			byte[] buffer = new byte[(int) file.length()];
			inputFile.read(buffer);
			inputFile.close();
			return Base64.encodeToString(buffer, Base64.DEFAULT);
		} catch (IOException e) {
			return null;
		}
	}

	public static File photoTemp;

	/**
	 * 跳转到系统裁剪图片的界面
	 */
	public static void goCut(File file, Activity a) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(Uri.fromFile(file), "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 300);
		intent.putExtra("outputY", 300);
		photoTemp = Tools.getAutoImageFile(a, true);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoTemp));
		intent.putExtra("return-data", false);
		intent.putExtra("noFaceDetection", true);
		a.startActivityForResult(intent, CUT);
	}
	
	// 临时图片文件存放
	public static File getTempImageRootDir(Context c) {
		File f = new File(c.getExternalCacheDir(), "images");
		f.mkdirs();
		return f;
	}

	// 长期图片文件存放
	public static File getUsefulImageRootDir(Context c) {
		File f = new File(c.getExternalCacheDir(), "user_img");
		f.mkdirs();
		return f;
	}

	// image-loader图片文件存放
	public static File getLoaderImageRootDir(Context c) {
		File f = new File(c.getExternalCacheDir(), "iLoader");
		f.mkdirs();
		return f;
	}

	public static File getAutoImageFile(Context c, boolean isTemp) {
		if (isTemp)
			return new File(getTempImageRootDir(c), Tools.getAutoName());
		else
			return new File(getUsefulImageRootDir(c), Tools.getAutoName());
	}

	@SuppressLint("SimpleDateFormat")
	private static SimpleDateFormat ss = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	// 将2015-10-10 12:05:20 这种 变成 xxx天前 xx小时前
	public static String yyyymmddhhmmssToTimeAgo(String time) {
		try {
			Date dd = ss.parse(time);
			long t = dd.getTime();
			return timestampToTimeAgo(t);
		} catch (ParseException e) {
			return "";
		}
	}

	// 将142525648000 这种 变成 xxx天前 xx小时前
	public static String timestampToTimeAgo(long time) {
		try {
			long c = System.currentTimeMillis() - time;
			long chafen = c / 1000 / 60;// minute

			if (chafen <= 1)
				return "刚刚";

			if (chafen < 60)
				for (int i = 3; i < 60; i++) {
					if (chafen < i)
						return new StringBuffer().append(i).append("分钟前").toString();
				}

			long chaxiaoshi = chafen / 60;
			if (chaxiaoshi <= 23)
				for (int i = 1; i <= 23; i++) {
					if (chaxiaoshi <= i)
						return new StringBuffer().append(i).append("小时前").toString();
				}

			long chaday = chaxiaoshi / 24;
			if (chaday < 30)
				for (int i = 1; i < 30; i++) {
					if (chaday <= i)
						return new StringBuffer().append(i).append("天前").toString();
				}

			long chayue = chaday / 30;
			if (chayue < 12)
				for (int i = 1; i < 12; i++) {
					if (chayue <= i)
						return new StringBuffer().append(i).append("个月前").toString();
				}

			long chanian = chayue / 12;
			if (chanian <= 1)
				return new StringBuffer().append(1).append("年以前").toString();
			else
				return new StringBuffer().append(chanian).append("年以前").toString();

		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * 查询所有图片的File路径
	 */
	public static ArrayList<String> listAlldir() {
		Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		Uri uri = intent.getData();
		ArrayList<String> list = new ArrayList<String>();
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);
		while (cursor.moveToNext()) {
			String path = cursor.getString(0);
			list.add(new File(path).getAbsolutePath());
		}
		return list;
	}

	/**
	 * 退出全屏
	 */
	public static void quiteFullScreen(Context context) {
		final WindowManager.LayoutParams attrs = ((Activity) context).getWindow().getAttributes();
		attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
		((Activity) context).getWindow().setAttributes(attrs);
		((Activity) context).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
	}

	/**
	 * 判断一个list是否為空
	 */
	public static boolean isEmptyList(List<? extends Object> list) {
		return (list == null || list.isEmpty());
	}

	public static String getStrings(int res) {
		try {
			String ss = MyApplication.getInstance().getResources().getString(res);
			return ss;
		} catch (Exception e) {
			return "";
		}
	}





}
