package itstudio.instructor.util;

import itstudio.instructor.config.Config;

import com.easemob.chatuidemo.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import android.graphics.Bitmap;
import android.widget.ImageView;
public class FileUtils {



	/**
	 * 设置图片头像
	 */
	public static void setImageHead(String url, ImageView iv) {
		if (url == null) {
			url = "drawable://" + R.drawable.head_default_local;
		} else{
		    
		    url = Config.HEAD_URL + url;
		} 
		
		ImageLoader.getInstance().displayImage(url, iv, headImageOptions());
	}
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
