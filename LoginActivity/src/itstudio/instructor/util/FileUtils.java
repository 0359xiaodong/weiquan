package itstudio.instructor.util;

import itstudio.instructor.config.Config;
import com.easemob.chatuidemo.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
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
	/**
	 * 设置图片news
	 */
	public static void setNewsImg(String url, ImageView iv) {
		if (url == null) {
			return;
		} else{
		    
		    url = Config.NEWS_URL + url;
		} 
		ImageLoader.getInstance().displayImage(url, iv, newsImageOptions());
	}
	/**
	 * 设置图片notice
	 */
	public static void setNoticeImg(String url, ImageView iv) {
		if (url == null) {
			return;
		} else{
		    url = Config.IMG_URL + url;
		} 
		ImageLoader.getInstance().displayImage(url, iv, newsImageOptions());
	}
	public static DisplayImageOptions  headImageOptions() {

        return new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.user_image_load_loading)
                .showImageOnFail(R.drawable.user_image_load_fail)
				.cacheInMemory(true)// 设置下载的图片是否缓存在内存中
				.cacheOnDisk(true)// 设置下载的图片是否缓存在SD卡中
				.considerExifParams(false) // 是否考虑JPEG图像EXIF参数（旋转，翻转）
				.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)// 设置图片以如何的编码方式显示
				.bitmapConfig(Bitmap.Config.RGB_565)// 设置图片的解码类型
				.resetViewBeforeLoading(false)// 设置图片在下载前是否重置，复位
				// .displayer(new RoundedBitmapDisplayer(0))// 是否设置为圆角，弧度为多少
				.displayer(new FadeInBitmapDisplayer(0))// 是否图片加载好后渐入的动画时间
				.build();// 构建完成
        
        
    }
    public static DisplayImageOptions  newsImageOptions() {
      return  new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.bg_load_loading)
                .showImageOnFail(R.drawable.bg_load_fail)
				.cacheInMemory(true)// 设置下载的图片是否缓存在内存中
				.cacheOnDisk(true)// 设置下载的图片是否缓存在SD卡中
				.considerExifParams(false) // 是否考虑JPEG图像EXIF参数（旋转，翻转）
				.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)// 设置图片以如何的编码方式显示
				.bitmapConfig(Bitmap.Config.RGB_565)// 设置图片的解码类型
				.resetViewBeforeLoading(false)// 设置图片在下载前是否重置，复位
				// .displayer(new RoundedBitmapDisplayer(0))// 是否设置为圆角，弧度为多少
				.displayer(new FadeInBitmapDisplayer(0))// 是否图片加载好后渐入的动画时间
				.build();// 构建完成
    }

}
