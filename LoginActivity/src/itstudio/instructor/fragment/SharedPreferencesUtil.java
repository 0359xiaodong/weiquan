package itstudio.instructor.fragment;

import java.util.Map;
import java.util.Set;
import itstudio.instructor.config.Config;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * @author miss
 *
 */
public class SharedPreferencesUtil {
	
	public static boolean readIsFirstUse(Context context){
		SharedPreferences preferences=context.getSharedPreferences(Config.APP_NAME, Context.MODE_PRIVATE );
		boolean isUse=preferences.getBoolean(Config.IS_USE, false);
		return isUse;
	}
	
	public static void writeIsFirstUse(Context context){
		SharedPreferences preferences=context.getSharedPreferences(Config.APP_NAME, Context.MODE_PRIVATE );
		Editor editor=preferences.edit();
		editor.putBoolean(Config.IS_USE, true);
		editor.commit();
	}
    public static void add(Context context,Map<String, String> map) {
        Set<String> set = map.keySet();
        SharedPreferences preferences= context.getSharedPreferences(Config.APP_NAME, Context.MODE_PRIVATE );
        Editor editor=preferences.edit();
        for (String key : set) {
            editor.putString(key, map.get(key));
        }
        editor.commit();
    }
    
    public static void add(Context context,String key,String content) {
    	SharedPreferences preferences= context.getSharedPreferences(Config.APP_NAME, Context.MODE_PRIVATE );
    	Editor editor=preferences.edit();
    	editor.putString(key, content);
    	editor.commit();
    }
 
    /**
     * 删除信息
     */
    public static void deleteAll(Context context) {
    	
        SharedPreferences preferences= context.getSharedPreferences(Config.APP_NAME, Context.MODE_PRIVATE );
        Editor editor=preferences.edit();
        editor.clear();
        editor.commit();
    }
 
    /**
     * 删除一条信息
     */
    public static void delete(Context context,String key){
        SharedPreferences preferences= context.getSharedPreferences(Config.APP_NAME, Context.MODE_PRIVATE );
        Editor editor=preferences.edit();
        editor.remove(key);
        editor.commit();
    }
 
    /**
     * 获取信息
     * @param key
     * @return
     */
    public static String get(Context context,String key){
    	SharedPreferences preferences= context.getSharedPreferences(Config.APP_NAME, Context.MODE_PRIVATE );
    	if (preferences != null) {
            return preferences.getString(key, "");
        }
        return "";
    }
 
}
