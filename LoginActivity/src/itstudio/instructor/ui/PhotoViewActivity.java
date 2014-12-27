package itstudio.instructor.ui;

import itstudio.instructor.widget.ProgressWheel;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import com.easemob.chatuidemo.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class PhotoViewActivity extends Activity{
	String imageUrl;
	PhotoView photoView;
	ProgressWheel progressWheel;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_imageview);
		CheckBox checkBox;
		checkBox = (CheckBox) findViewById(R.id.child_checkbox);
		checkBox.setVisibility(View.GONE);

		// 获取imageUrl
		imageUrl = getIntent().getStringExtra("imageUrl");
		//System.out.println("imageUrl=====" + imageUrl);
		photoView = (PhotoView) findViewById(R.id.photoView);
		progressWheel = (ProgressWheel) findViewById(R.id.progressWheel);

		photoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float x, float y) {
                finish();
            }
        });
		
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.cacheOnDisc(true).considerExifParams(true).build();
		ImageLoader.getInstance().displayImage(imageUrl, photoView, options,
				new SimpleImageLoadingListener() {
					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {
						progressWheel.setVisibility(View.GONE);

					}
				}, new ImageLoadingProgressListener() {
					@Override
					public void onProgressUpdate(String imageUri, View view,
							int current, int total) {
						progressWheel.setProgress(360 * current / total);
						
					}
				});

	}

}
