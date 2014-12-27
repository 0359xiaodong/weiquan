package itstudio.instructor.config;

import android.graphics.Bitmap;

import com.easemob.chatuidemo.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

public class ImageOptionsUtil {

	public static DisplayImageOptions  headImageOptions() {

		DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)  //设置下载的图片是否缓存在内存中
				.showImageOnLoading(R.drawable.user_image_load_loading)
				.showImageOnFail(R.drawable.user_image_load_fail)
				.cacheOnDisc(true).considerExifParams(true)  //设置下载的图片是否缓存在内存中
				.displayer(new FadeInBitmapDisplayer(200))
				.bitmapConfig(Bitmap.Config.ALPHA_8).build();  //设置图片的解码类型
		return options;
		
	}
	
	public static DisplayImageOptions  newsImageOptions() {
		DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)  //设置下载的图片是否缓存在内存中
				.showImageOnLoading(R.drawable.bg_load_loading)
				.showImageOnFail(R.drawable.bg_load_fail)
				.cacheOnDisc(true).considerExifParams(true)  //设置下载的图片是否缓存在内存中
				.displayer(new FadeInBitmapDisplayer(200))
				.bitmapConfig(Bitmap.Config.ALPHA_8).build();  //设置图片的解码类型
		return options;
	}
}
