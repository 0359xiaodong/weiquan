package itstudio.instructor.adapter;

import itstudio.instructor.entity.Notice;
import itstudio.instructor.util.FileUtils;
import java.util.List;
import android.content.Context;
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
	
	public NoticePagerAdapter( List<Notice> notices, Context context) {
		this.context = context;
		this.notices = notices;
	};

	@Override
	public int getCount() {
		return notices.size();
	}

	@Override
	public Object instantiateItem(View convertView, int position) {
		PicModelHodler  holder = new PicModelHodler();
		holder.imageView.setScaleType(ScaleType.CENTER_CROP);
		FileUtils.setNoticeImg("carousel"+position+".png", holder.imageView);
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