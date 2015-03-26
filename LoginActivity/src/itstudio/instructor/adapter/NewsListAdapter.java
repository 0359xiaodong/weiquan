package itstudio.instructor.adapter;

import java.util.ArrayList;
import java.util.List;
import itstudio.instructor.entity.News;
import itstudio.instructor.util.FileUtils;
import itstudio.instructor.util.TimeUtil;
import itstudio.instructor.widget.RoundedImageView;
import com.easemob.chatuidemo.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class NewsListAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private List<News> newsList =new ArrayList<News>();

	public NewsListAdapter(Context context, List<News> newsList) {
		if(newsList!=null){
			this.newsList = newsList;
		}
		this.mInflater = LayoutInflater.from(context);
		
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub

		return newsList==null ? 0:newsList.size() ;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return newsList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public void appendToList(List<News> addList) {

		if (addList == null) {
			return;
		}
		if (newsList == null) {
			newsList = new ArrayList<News>();
		}
		newsList.addAll(addList);
		notifyDataSetChanged();
	}
	public void removeList(List<News> addList) {
		
		if (addList == null) {
			return;
		}
		if (newsList == null) {
			newsList = new ArrayList<News>();
			newsList.addAll(addList);//在加载新的
			notifyDataSetChanged();
			return;
		}
		if(!addList.get(0).getId().equals(newsList.get(0).getId())){
			
			newsList.clear();//  先清空
			newsList.addAll(addList);//在加载新的
			notifyDataSetChanged();
		}
		
	}
	public void clearAll() {
	    newsList.clear();
	    notifyDataSetChanged();
	}
	
	public int appendData(List<News> addList,boolean refesh) {
		int result =0;
		if (addList == null || addList.size()==0) {
			result = newsList.size()/10;
			if(newsList.size()%10!=0){
				result++;
			}
			return result;
		}
		// 首次加载
		if(newsList.size()==0){
			newsList.addAll(addList);//在加载新的
			notifyDataSetChanged();
			return 1; //当前是第一页
		}
		// 有新的数据
		if(!newsList.get(0).getId().equals(addList.get(0).getId())){
			if(refesh){
				newsList.clear();  //  先清空
				newsList.addAll(addList);//在加载新的
				notifyDataSetChanged();
				return 1;
			}else{
				newsList.addAll(addList);//直接追加
				notifyDataSetChanged();
				result = newsList.size()/10;
				if(newsList.size()%10!=0){
					result++;
				}
				return result;
			}
		}
		// 没有新的数据
		result = newsList.size()/10;
		if(newsList.size()%10!=0){
			result++;
		}
		return result;
	
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final PicModelHodler holder ;
		News news = newsList.get(position);
		
		if(convertView == null){
			holder = new PicModelHodler();
			convertView = mInflater.inflate(R.layout.listitem_news, null);
			holder.imageView = (RoundedImageView) convertView.findViewById(R.id.image);
			holder.txt_author = (TextView) convertView.findViewById(R.id.txt_author);
			holder.txt_title = (TextView) convertView.findViewById(R.id.txt_title);
			holder.txt_short_content = (TextView) convertView.findViewById(R.id.txt_short_content);
			holder.txt_date = (TextView) convertView.findViewById(R.id.txt_date);
			convertView.setTag(holder);
			//给ImageView设置路径Tag,这是异步加载图片的小技巧
		}
		else{
			holder = (PicModelHodler) convertView.getTag();
		}
		holder.txt_author.setText(news.getAuthorName());
		holder.txt_title.setText(news.getTitle());
		holder.txt_short_content.setText(news.getContent());
		holder.txt_date.setText(TimeUtil.dateToString(news.getPostTime(),TimeUtil.FORMAT_DATE));
		FileUtils.setImageHead(news.getUser().getHeadUrl(), holder.imageView);
		return convertView;
	}

	private final class PicModelHodler {
		public RoundedImageView imageView;
		public TextView txt_author;
		public TextView txt_title;
		public TextView txt_date;
		public TextView txt_short_content;
	}
}
