/**
 * 
 */
package itstudio.instructor.http;
import org.apache.http.impl.cookie.BasicClientCookie;
import itstudio.instructor.config.Config;
import com.easemob.chatuidemo.MyApplication;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

/**   
 * @Description  
 * @author MR.Wang  
 * @date 2014-7-14 
 * @version V1.0   
 */
public class TwitterRestClient {
	
	  private static final String BASE_URL = Config.BASE_URL_API; 
	  public static AsyncHttpClient client = new AsyncHttpClient();

	  public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
	      client.get(getAbsoluteUrl(url), params, responseHandler);
	  }

	  public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
		  //client.set
		  setCookie();
	      client.post(getAbsoluteUrl(url), params, responseHandler);
	  }

	  private static String getAbsoluteUrl(String relativeUrl) {
	      return BASE_URL + relativeUrl;
	  }
	  
	  private static void  setCookie() {
			PersistentCookieStore myCookieStore;
			myCookieStore = new PersistentCookieStore(MyApplication.getInstance());
			TwitterRestClient.client.setCookieStore(myCookieStore);
			BasicClientCookie newCookie = new BasicClientCookie("cookiesare", "awesome");
			newCookie.setVersion(1);
			newCookie.setDomain("http://59.70.149.8:8080/session");//随便写。。
			newCookie.setPath("/");
			myCookieStore.addCookie(newCookie);
			client.setCookieStore(myCookieStore);
	  }
	  public static void  keepCookie() {
		  PersistentCookieStore myCookieStore;
		  myCookieStore = new PersistentCookieStore(MyApplication.getInstance());
		  TwitterRestClient.client.setCookieStore(myCookieStore);
		  BasicClientCookie newCookie = new BasicClientCookie("cookiesare", "awesome");
		  newCookie.setVersion(1);
		  newCookie.setDomain("http://59.70.149.8:8080/session");//随便写。。
		  newCookie.setPath("/");
		  myCookieStore.addCookie(newCookie);
		  client.setCookieStore(myCookieStore);
	  }
}

