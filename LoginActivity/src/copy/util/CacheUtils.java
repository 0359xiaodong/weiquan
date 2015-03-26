package copy.util;

import android.support.v4.util.LruCache;


public class CacheUtils {
	/**
	 * 内存缓存,存储phone对应的昵称和头像url
	 */
	private static LruCache<String, NameUrl> nuCache = new LruCache<String, NameUrl>(1024 * 256) {
		@Override
		protected int sizeOf(String key, NameUrl value) {
			return 256;
		};
	};

	
	/**
	 * 存储phone,对应的昵称和头像url到内存
	 */
	public static void saveNameUrlToCache(NameUrl value) {
		Tools.log("存内存的nameurl " + "name : " + value.getName() + "id : " + value.getId() + "URL : " + value.getHeadUrl());
		if (value != null && !Tools.isEmptyStr(value.getId()))
			nuCache.put(value.getId(), value);
	}

	/**
	 * 根据phone,获取对应的昵称和url,从内存中
	 */
	public static NameUrl getNameUrl(String key) {
		if (!Tools.isEmptyStr(key))
			return nuCache.get(key);
		return null;
	}

}
