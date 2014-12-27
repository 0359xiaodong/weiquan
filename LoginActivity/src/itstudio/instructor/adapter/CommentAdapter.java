package itstudio.instructor.adapter;

import itstudio.instructor.entity.Comment;
import itstudio.instructor.ui.NewsDetailActivity;
import itstudio.instructor.util.TimeUtil;

import java.util.ArrayList;
import java.util.List;

import com.easemob.chatuidemo.R;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CommentAdapter extends BaseAdapter {
	
	
	private class ViewHolder {
		TextView itemName;
		TextView itemTime;
		TextView itemContent;
		TextView itemReplyName;
		TextView itemReply;
		LinearLayout itemCommentLayout;
	}

	// 单行的布局
	private int mResource;
	private Context context;
	private EditText  editText;
	List<Comment> commentList = new ArrayList<Comment>();


	/**
	 * @param context
	 * @param commentList
	 * @param resource
	 * @param editText
	 */
	public CommentAdapter(Context context,
			List<Comment> commentList, int resource,EditText  editText) {
		
		this.context = context;
		if(commentList==null){
			
			this.commentList =commentList;
		}
		mResource = resource;
		this.editText =editText;
	}

	@Override
	public int getCount() {
		return commentList ==null ? 0:commentList.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}
	public void appendToFirst(Comment comment) {

		if (comment == null) {
			return;
		}
		commentList.add(0, comment);
		notifyDataSetChanged();
	}
	@Override
	public long getItemId(int position) {
		return position;
	}
	public void appendToList(List<Comment> lists) {

		if (lists == null) {
			return;
		}
		commentList.addAll(lists);
		notifyDataSetChanged();
	}
		public void clear() {

			commentList.clear();  //  先清空
			notifyDataSetChanged();
	}
	
	public int appendData(List<Comment> addList,boolean refesh) {
		int result =0;
		if (addList == null || addList.size()==0) {
			result = commentList.size()/10;
			if(commentList.size()%10!=0){
				result++;
			}
			return result;
		}
		// 首次加载
		if(commentList.size()==0){
			commentList.addAll(addList);//在加载新的
			notifyDataSetChanged();
			return 1; //当前是第一页
		}
		// 有新的数据
		if(!commentList.get(0).getId().equals(addList.get(0).getId())){
			
			if(refesh){
				commentList.clear();  //  先清空
				commentList.addAll(addList);//在加载新的
				notifyDataSetChanged();
				return 1;
			}else{
				commentList.addAll(addList);//直接追加
				notifyDataSetChanged();
				result = commentList.size()/10;
				if(commentList.size()%10!=0){
					result++;
				}
				return result;
			}
		}
		// 没有新的数据
		result = commentList.size()/10;
		if(commentList.size()%10!=0){
			result++;
		}
		return result;
	
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		 ViewHolder holder;
/*		if (convertView == null) {  // 必须io 操作   否则错位
			convertView = LayoutInflater.from(context).inflate(mResource,parent, false);
			convertView = LayoutInflater.from(context).inflate(mResource,parent, false);
			holder = new ViewHolder();
			holder.itemName = (TextView) convertView.findViewById(R.id.cm_author_name);
			holder.itemTime = (TextView) convertView.findViewById(R.id.comment_time);
			holder.itemContent = (TextView) convertView.findViewById(R.id.comment_content );
			holder.itemCommentLayout = (LinearLayout) convertView.findViewById(R.id.comment_layout);
			holder.itemReplyName = (TextView) convertView.findViewById(R.id.reply_name);
			holder.itemReply = (TextView) convertView.findViewById(R.id.tv_reply);
			// 设置标记
			convertView.setTag(holder);
		}*/
		
		convertView = LayoutInflater.from(context).inflate(mResource,parent, false);
		holder = new ViewHolder();
		holder.itemName = (TextView) convertView.findViewById(R.id.cm_author_name);
		holder.itemTime = (TextView) convertView.findViewById(R.id.comment_time);
		holder.itemContent = (TextView) convertView.findViewById(R.id.comment_content );
		holder.itemCommentLayout = (LinearLayout) convertView.findViewById(R.id.comment_layout);
		holder.itemReplyName = (TextView) convertView.findViewById(R.id.reply_name);
		holder.itemReply = (TextView) convertView.findViewById(R.id.tv_reply);
		// 设置数据到view
		Comment comment = commentList.get(position);
		holder.itemName.setText(comment.getAuthorName());
		holder.itemTime.setText(TimeUtil.dateToString(comment.getPostTime(), TimeUtil.FORMAT_DATE));
		holder.itemContent.setText(commentList.get(position).getContent());
			
		String ss= comment.getReplyName();
		if(ss!=null)
		if(!TextUtils.isEmpty(comment.getReplyName())){
			holder.itemReply.setVisibility(View.VISIBLE);
			holder.itemReplyName.setText(comment.getReplyName());
		}else{
			holder.itemReply.setVisibility(View.VISIBLE);
			holder.itemReplyName.setVisibility(View.VISIBLE);
		}
		// 设置按钮事件
		final int finalPosition= position;
		holder.itemCommentLayout.setFocusable(false);
		holder.itemCommentLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				// 二级回复  取评论的作者id
				NewsDetailActivity.replyId = commentList.get(finalPosition).getAuthorId();
				NewsDetailActivity.replyName = commentList.get(finalPosition).getAuthorName();
				editText.setFocusableInTouchMode(true);
				editText.requestFocus();
				editText.setFocusable(true);
				editText.setHint("回复："+commentList.get(finalPosition).getAuthorName());
				InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.showSoftInput(editText,InputMethodManager.SHOW_IMPLICIT);
			}
		});
		return convertView;
	}

}
