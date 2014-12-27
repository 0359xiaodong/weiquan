/**
 * 
 */
package itstudio.instructor.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**   
 * @Description  不能滑动的GridView
 * @author MR.Wang  
 * @date 2014-7-15 
 * @version V1.0   
 */
public class NoScrollGridView  extends GridView {


	/**
	 * @param context
	 */
	public NoScrollGridView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}         
	/**
	 * @param context
	 * @param attrs
	 */
	public NoScrollGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public NoScrollGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec){

		int expandSpec = MeasureSpec.makeMeasureSpec( Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST); 

	super.onMeasure(widthMeasureSpec, expandSpec);
	}

}
