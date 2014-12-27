package itstudio.instructor.adapter;

import itstudio.instructor.config.Config;
import itstudio.instructor.entity.Notice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

/**
 *  @author miss
 */
public class NoticePagerAdapter extends PagerAdapter {
	private Context context;
	private  List<Notice> notices;
	DisplayImageOptions options;
	private ImageLoader imageLoader;
	
	//private HashMap  hashmap = new HashMap();;
	public NoticePagerAdapter( List<Notice> notices, Context context,DisplayImageOptions options) {
		this.context = context;
		this.notices = notices;
		this.options = options;
		imageLoader = ImageLoader.getInstance();
	};

	@Override
	public int getCount() {
		return notices.size();
	}

	@Override
	public Object instantiateItem(View convertView, final int arg1) {

		
		PicModelHodler  holder = new PicModelHodler();

		holder.imageView.setScaleType(ScaleType.CENTER_CROP);
		//holder.imageView.setImageBitmap(result);
		
		/*if(hashmap.containsKey(arg1)){
		    holder.imageView.setImageBitmap((Bitmap) hashmap.get(arg1));
		}else{*/
		    
		
		imageLoader.displayImage(Config.IMG_URL+"carousel"+arg1+".png",holder.imageView, options, new SimpleImageLoadingListener() {
			@Override
			public void onLoadingStarted(String imageUri, View view) {
				// TODO Auto-generated method stub
				super.onLoadingStarted(imageUri, view);
			}

			@Override
			public void onLoadingFailed(String imageUri, View view,
					FailReason failReason) {
				// TODO Auto-generated method stub
				super.onLoadingFailed(imageUri, view, failReason);
			}

			@Override
			public void onLoadingComplete(String imageUri, View view,
					Bitmap loadedImage) {
				// TODO Auto-generated method stub
				super.onLoadingComplete(imageUri, view, loadedImage);
/*				if(hashmap.size()<3 && !hashmap.containsValue(loadedImage)){
				    hashmap.put(arg1,loadedImage);
				}*/
			}

			@Override
			public void onLoadingCancelled(String imageUri, View view) {
				// TODO Auto-generated method stub
				super.onLoadingCancelled(imageUri, view);
			}

			@Override
			protected Object clone() throws CloneNotSupportedException {
				// TODO Auto-generated method stub
				return super.clone();
			}

			@Override
			public boolean equals(Object o) {
				// TODO Auto-generated method stub
				return super.equals(o);
			}

			@Override
			protected void finalize() throws Throwable {
				// TODO Auto-generated method stub
				super.finalize();
			}

			@Override
			public int hashCode() {
				// TODO Auto-generated method stub
				return super.hashCode();
			}

			@Override
			public String toString() {
				// TODO Auto-generated method stub
				return super.toString();
			}
			
			
		});
		//}
		((ViewPager) convertView).addView(holder.imageView);

		return holder.imageView;
	}

	@Override
	public void destroyItem(View arg0, int arg1, Object arg2) {
		ImageView image = (ImageView) arg2;
		image.setImageBitmap(null);
		((ViewPager) arg0).removeView(image);
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}
	private final class PicModelHodler {
		public ImageView imageView = new ImageView(context);
	}

}