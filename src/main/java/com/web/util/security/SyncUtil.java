package com.web.util.security;


import com.alibaba.fastjson.JSON;
import org.apache.commons.collections4.map.LRUMap;


public class SyncUtil {
	private static final LRUMap<String, Long> reqCache = new LRUMap<>(64);

	private static long timeOutTimestamp = 3000;

	public static long getTimeOutTimestamp() {
		return timeOutTimestamp;
	}

	public static void setTimeOutTimestamp(long timeOutTimestamp) {
		SyncUtil.timeOutTimestamp = timeOutTimestamp;
	}

	public static boolean start(Object object) {
		return start(JSON.toJSONString(object));
	}

	public static boolean start(String operateId) {
		synchronized (SyncUtil.class) {
			if (reqCache.getOrDefault(operateId, 0L) > System.currentTimeMillis()) {
				return false;
			} else {
				reqCache.put(operateId, System.currentTimeMillis() + timeOutTimestamp);
				return true;
			}
		}
	}

	public static void finish(Object object) {
		finish(JSON.toJSONString(object));
	}

	public static void finish(String operateId) {
		synchronized (SyncUtil.class) {
			reqCache.remove(operateId);
		}
	}
}