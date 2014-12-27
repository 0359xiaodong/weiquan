package itstudio.instructor.ui;

import itstudio.instructor.config.Config;
import itstudio.instructor.config.ImageOptionsUtil;
import itstudio.instructor.entity.Notice;
import itstudio.instructor.util.PixelUtil;
import itstudio.instructor.util.TimeUtil;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.easemob.chatuidemo.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class NoticeDetailActivity extends Activity {

	//view
	private LinearLayout layout;
	private View content_back_layout;
	//config
	DisplayImageOptions options;
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notice_detail);

		content_back_layout = findViewById(R.id.content_back_layout);
		ViewOncliclListenter listenter = new ViewOncliclListenter();
		content_back_layout.setOnClickListener(listenter);
		
		Notice notice = (Notice) getIntent().getExtras().get("notice");
		//加载文字
		TextView tv_title = (TextView) findViewById(R.id.tv_title);
		TextView tv_anthor = (TextView) findViewById(R.id.tv_anthor);
		TextView tv_date = (TextView) findViewById(R.id.tv_date);
		TextView tv_content = (TextView) findViewById(R.id.tv_content);
		tv_title.setText(notice.getTitle());
		tv_date.setText(TimeUtil.dateToString(notice.getPostTime(),TimeUtil.FORMAT_DATE_TIME));
		tv_anthor.setText(notice.getAuthorName());
		tv_content.setText("     "+notice.getContent());
		
		
	      CharSequence text = tv_content.getText();
	        if (text instanceof Spannable) {
	            int end = text.length();
	            Spannable sp = (Spannable) tv_content.getText();
	            URLSpan[] urls = sp.getSpans(0, end, URLSpan.class);
	            SpannableStringBuilder style = new SpannableStringBuilder(text);
	            style.clearSpans();
	            for (URLSpan url : urls) {
	                MyURLSpan myURLSpan = new MyURLSpan(url.getURL());
	                style.setSpan(myURLSpan, sp.getSpanStart(url),
	                        sp.getSpanEnd(url), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
	            }
	            tv_content.setText(style);
	        }

		options =ImageOptionsUtil.newsImageOptions();
		 if(notice.getNoticePics()!=null && notice.getNoticePics()!=""){
				// 动态添加图片
				layout = (LinearLayout) findViewById(R.id.imageview);
				int width = (int) (PixelUtil.screenWidthpx(NoticeDetailActivity.this)*0.8);
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
						width ,PixelUtil.dp2px(150));
				lp.bottomMargin = 10;
				lp.gravity = Gravity.CENTER_HORIZONTAL;
				ImageView imageView;
			String picture[] = notice.getNoticePics().split(";");

			for (int index = 0; index < picture.length; index++) {

				imageView = new ImageView(NoticeDetailActivity.this);
				imageView.setLayoutParams(lp);
				imageLoader.displayImage(Config.NOTICE_URL +picture[index],
						imageView, options);
				layout.addView(imageView);
				final String imgUrl=Config.NOTICE_URL +picture[index];
				imageView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						/*Intent intent = new Intent(NoticeDetailActivity.this, PhotoViewActivity.class);
						intent.putExtra("imageUrl",imgUrl);
						startActivity(intent);*/
					}
				});
			}
		}

	}

	class ViewOncliclListenter implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.content_back_layout:
				finish();
				break;
			default:
				break;
			}
		}
	}
	private class MyURLSpan extends ClickableSpan {
        private String mUrl;

        MyURLSpan(String url) {
            mUrl = url;
        }

        @Override
        public void onClick(View widget) {
            Toast.makeText(NoticeDetailActivity.this, mUrl, Toast.LENGTH_LONG).show();
            widget.setBackgroundColor(Color.parseColor("#00000000"));
        }
    }

}
