package copy.util;

import itstudio.instructor.config.Config;
import itstudio.instructor.http.TwitterRestClient;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class NetUtils {


	/**
	 * 联网,获取phone对应的昵称和头像url,存内存
	 */
	public static void getNameAndUrl(final String phone, final ObjectRunnable success, final ExceptionRunnable failed) {

		RequestParams rr = new RequestParams();
		rr.put("id", phone);
		System.out.println("phone" + phone);
		TwitterRestClient.post(Config.AC_FINDUSER, rr, new AsyncHttpResponseHandler() {
			public void onSuccess(int arg0, Header[] arg1, byte[] bs) {
				try {

					String json = new String(bs).trim();
					System.out.println("json" + json);
					Tools.log("获取头像url和姓名:" + json);
					JSONObject jo = new JSONObject(json);// 成功了,将json转化成Name_HeadUrl对象,传入到接口中
					String id = jo.getString("id");
					String name = jo.getString("name");
					String headUrl = jo.getString("headUrl");
					NameUrl nu = new NameUrl();
					nu.setName(name);
					nu.setHeadUrl(headUrl);
					nu.setId(phone);
					if (!Tools.isEmptyStr(name)) {
						NameUrlUtils.saveNameUrlToLocal(nu);
						success.run(nu);// 调用接口
					} else
						onFailure(0, null, new byte[] {}, null);

				} catch (JSONException e) {
					// failed(null, "获取数据失败", failed);
				}
			}
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				// failed(null, null, failed);
			}
		});
	}





}
