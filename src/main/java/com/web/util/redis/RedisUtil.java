package com.web.util.redis;

import com.google.gson.Gson;
import com.web.base.enums.BusinessErrorEnum;
import com.web.base.exceptions.BusinessException;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


public final class RedisUtil {
	private static final Gson gson = new Gson();

	private static RedisTemplate<String, String> redisTemplate;

	private static <T> T parse(String value, Class<T> clazz) {
		if (clazz == String.class) {
			return clazz.cast(value);
		} else {
			return gson.fromJson(value, clazz);
		}
	}

	private static <K, V> Map<K, V> parseMap(Map<String, String> map, Class<K> keyClass, Class<V> valueClass) {
		Map<K, V> result = new HashMap<>(map.size());
		map.forEach((key, value) -> result.put(parse(key, keyClass), parse(value, valueClass)));
		return result;
	}

	private static <T> String stringify(T value) {
		if (value instanceof String) {
			return (String) value;
		} else {
			return gson.toJson(value);
		}
	}

	private static <K, V> Map<String, String> stringifyMap(Map<K, V> map) {
		Map<String, String> tmpMap = new HashMap<>(map.size());
		map.keySet().forEach(key -> tmpMap.put(stringify(key), stringify(map.get(key))));
		return tmpMap;
	}

	/**
	 * 判断Redis键是否存在
	 *
	 * @param redisKey Redis键
	 * @return true: 存在 false: 不存在
	 */
	public static boolean exists(String redisKey) {
		return Objects.equals(redisTemplate.hasKey(redisKey), Boolean.TRUE);
	}

	/**
	 * 设置Redis键值
	 *
	 * @param redisKey Redis键
	 * @param value    值
	 */
	public static <T> void put(String redisKey, T value) {
		redisTemplate.opsForValue().set(redisKey, stringify(value));
	}

	/**
	 * 设置Redis键的字符串值（包含过期时间）
	 *
	 * @param redisKey Redis键
	 * @param value    值
	 * @param ttl      过期时间
	 * @param timeUnit 时间单位
	 */
	public static <T> void put(String redisKey, T value, long ttl, TimeUnit timeUnit) {
		redisTemplate.opsForValue().set(redisKey, stringify(value), ttl, timeUnit);
	}

	/**
	 * 获取Redis键值
	 *
	 * @param redisKey Redis键
	 * @param cls      类型
	 * @return 值
	 */
	public static <T> T get(String redisKey, Class<T> cls) {
		String value = redisTemplate.opsForValue().get(redisKey);
		return value != null ? parse(value, cls) : null;
	}

	/**
	 * 获取Redis键的字符串值
	 *
	 * @param redisKey Redis键
	 * @return 字符串值
	 */
	public static String get(String redisKey) {
		return redisTemplate.opsForValue().get(redisKey);
	}

	/**
	 * 移除Redis键
	 *
	 * @param redisKey Redis键
	 */
	public static void remove(String redisKey) {
		redisTemplate.delete(redisKey);
	}

	/**
	 * 设置Redis键的列表值
	 *
	 * @param redisKey Redis键
	 * @param value    列表值
	 */
	public static <T> void setList(String redisKey, List<T> value) {
		redisTemplate.delete(redisKey);
		if (value != null && value.size() > 0) {
			redisTemplate.opsForList().rightPushAll(redisKey, value.stream().map(RedisUtil::stringify).collect(Collectors.toList()));
		}
	}

	/**
	 * 在Redis键的列表值的末尾插入条目
	 *
	 * @param redisKey Redis键
	 * @param item     条目
	 */
	public static <T> void addListItem(String redisKey, T item) {
		redisTemplate.opsForList().rightPush(redisKey, stringify(item));
	}

	/**
	 * 在Redis键的列表值的末尾插入条目（包含过期时间）
	 *
	 * @param redisKey Redis键
	 * @param item     条目
	 * @param ttl      过期时间
	 * @param timeUnit 时间单位
	 */
	public static <T> void addListItem(String redisKey, T item, long ttl, TimeUnit timeUnit) {
		addListItem(redisKey, item);
		redisTemplate.expire(redisKey, ttl, timeUnit);
	}

	/**
	 * 填充Redis键的列表值
	 *
	 * @param redisKey Redis键
	 * @param item     条目
	 */
	public static <T> void fillListItem(String redisKey, T item) {
		Long size = redisTemplate.opsForList().size(redisKey);
		if (size != null) {
			byte[] redisKeyBytes = redisKey.getBytes(StandardCharsets.UTF_8);
			byte[] itemBytes = stringify(item).getBytes(StandardCharsets.UTF_8);
			redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
				for (long i = 0; i < size; i++) {
					connection.listCommands().lSet(redisKeyBytes, i, itemBytes);
				}
				return null;
			});
		}
	}

	/**
	 * 填充Redis键的列表值（包含过期时间）
	 *
	 * @param redisKey Redis键
	 * @param item     条目
	 * @param ttl      过期时间
	 * @param timeUnit 时间单位
	 */
	public static <T> void fillListItem(String redisKey, T item, long ttl, TimeUnit timeUnit) {
		fillListItem(redisKey, item);
		redisTemplate.expire(redisKey, ttl, timeUnit);
	}

	/**
	 * 填充Redis键的列表值
	 *
	 * @param redisKey Redis键
	 * @param item     填充条目
	 * @param size     列表大小
	 */
	public static <T> void fillListItem(String redisKey, T item, long size) {
		byte[] redisKeyBytes = redisKey.getBytes(StandardCharsets.UTF_8);
		byte[] itemBytes = stringify(item).getBytes(StandardCharsets.UTF_8);
		redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
			for (long i = 0; i < size; i++) {
				connection.listCommands().lPush(redisKeyBytes, itemBytes);
			}
			return null;
		});
	}

	/**
	 * 填充Redis键的列表值（包含过期时间）
	 *
	 * @param redisKey Redis键
	 * @param item     填充条目
	 * @param size     列表大小
	 * @param ttl      过期时间
	 * @param timeUnit 时间单位
	 */
	public static <T> void fillListItem(String redisKey, T item, long size, long ttl, TimeUnit timeUnit) {
		fillListItem(redisKey, item, size);
		redisTemplate.expire(redisKey, ttl, timeUnit);
	}

	/**
	 * 获取Redis键的列表值
	 *
	 * @param redisKey Redis键
	 * @param cls      类型
	 * @return 值
	 */
	public static <T> List<T> getList(String redisKey, Class<T> cls) {
		return getList(redisKey).stream().map(item -> parse(item, cls)).collect(Collectors.toList());
	}

	/**
	 * 设置Redis键列表值的指定条目
	 *
	 * @param redisKey Redis键
	 * @param index    索引
	 * @param value    值
	 */
	public static <T> void setListItem(String redisKey, Integer index, T value) {
		redisTemplate.opsForList().set(redisKey, index, stringify(value));
	}

	public static Long getExpire(String redisKey) {
		return redisTemplate.getExpire(redisKey, TimeUnit.SECONDS);
	}

	/**
	 * 获取Redis键的字符串列表值
	 *
	 * @param redisKey Redis键
	 * @return 列表值
	 */
	public static List<String> getList(String redisKey) {
		List<String> entireList = redisTemplate.opsForList().range(redisKey, 0, -1);
		return entireList == null ? new ArrayList<>() : entireList;
	}

	/**
	 * 获取Redis键的列表值的条目
	 *
	 * @param redisKey  Redis键
	 * @param itemIndex 条目索引
	 * @param itemClass 条目类型
	 * @return 列表值
	 */
	public static <T> T getListItem(String redisKey, Long itemIndex, Class<T> itemClass) {
		String item = redisTemplate.opsForList().index(redisKey, itemIndex);
		return item != null ? parse(item, itemClass) : null;
	}

	/**
	 * 修改Redis键的列表值的条目
	 *
	 * @param redisKey  Redis键
	 * @param itemValue 条目索引
	 */
	public static <T> int updateListItem(String redisKey, T itemValue) {
		List<String> entireList = getList(redisKey);
		for (int i = 0; i < entireList.size(); i++) {
			if (entireList.get(i).equals("Deleted")) {
				setListItem(redisKey, i, itemValue);
				return i;
			} else if (i == entireList.size() - 1) {
				redisTemplate.opsForList().rightPush(redisKey, stringify(itemValue));
				return entireList.size();
			}
		}
		return 0;
	}

	/**
	 * 获取Redis键的列表值的字符串条目
	 *
	 * @param redisKey  Redis键
	 * @param itemIndex 条目索引
	 * @return 列表值
	 */
	public static String getListItem(String redisKey, Long itemIndex) {
		return redisTemplate.opsForList().index(redisKey, itemIndex);
	}

	/**
	 * 移除Redis键的列表值的指定条目
	 *
	 * @param redisKey Redis键
	 * @param item     条目
	 */
	public static <T> void removeListItem(String redisKey, T item) {
		redisTemplate.opsForList().remove(redisKey, 0, gson.toJson(item));
	}

	public static <T> void removeListItemByIndex(String redisKey, Integer index) {
		String value = redisTemplate.opsForList().index(redisKey, index);
		System.out.println(value);
		if (value != null) {
			redisTemplate.opsForList().remove(redisKey, 0, value);
		} else {
			throw new BusinessException(BusinessErrorEnum.REDIS_KEY_NOT_EXIST);
		}
	}

	/**
	 * 设置Redis键的哈希表值
	 *
	 * @param redisKey Redis键
	 * @param map      哈希表值
	 */
	public static <K, V> void putMap(String redisKey, Map<K, V> map) {
		redisTemplate.delete(redisKey);
		redisTemplate.opsForHash().putAll(redisKey, stringifyMap(map));
	}

	/**
	 * 设置Redis键的哈希表值（包含过期时间）
	 *
	 * @param redisKey Redis键
	 * @param map      哈希表值
	 * @param ttl      过期时间
	 * @param timeUnit 时间单位
	 */
	public static <K, V> void putMap(String redisKey, Map<K, V> map, long ttl, TimeUnit timeUnit) {
		redisTemplate.delete(redisKey);
		redisTemplate.opsForHash().putAll(redisKey, stringifyMap(map));
		redisTemplate.expire(redisKey, ttl, timeUnit);
	}

	/**
	 * 在Redis键的哈希表值中插入键值
	 *
	 * @param redisKey Redis键
	 * @param key      键
	 * @param value    值
	 */
	public static <K, V> void putMapValue(String redisKey, K key, V value) {
		HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
		hashOperations.put(redisKey, stringify(key), stringify(value));
	}

	/**
	 * 获取Redis键的哈希表值
	 *
	 * @param redisKey   Redis键
	 * @param keyClass   键类型
	 * @param valueClass 值类型
	 * @return 值
	 */
	public static <K, V> Map<K, V> getMap(String redisKey, Class<K> keyClass, Class<V> valueClass) {
		HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
		Map<String, String> map = hashOperations.entries(redisKey);
		return parseMap(map, keyClass, valueClass);
	}

	/**
	 * 获取Redis键的哈希表值中的键值
	 *
	 * @param redisKey   Redis键
	 * @param key        键
	 * @param valueClass 值类型
	 * @return 值
	 */
	public static <K, V> V getMapValue(String redisKey, K key, Class<V> valueClass) {
		HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
		return parse(hashOperations.get(redisKey, stringify(key)), valueClass);
	}

	public static String generateUserTempAuthKey(Integer userId) {
		return String.format("User:Users:%d:TempAuth", userId);
	}

	public static String generateSendSMSCoolDownKey(String flag) {
		return String.format("Send-SMS-Cool-Down:%s", flag);
	}

	public static String generateVerifiedVaptchaTokenKey(String token) {
		return String.format("Available-Vaptcha-Tokens:%s", token);
	}

	public static String generateUserRequestKey(Integer userId) {
		return String.format("User:Users:%d:Requests", userId);
	}

	public static List<String> generateUserSequenceHistorySetUUIDKey(Integer userId) {
		List<String> strings = new ArrayList<>();
		UUID uuid = UUID.randomUUID();
		strings.add(String.format("User:Users:%d:SequenceHistory:%s", userId, uuid));
		strings.add(uuid.toString());
		return strings;
	}

	public static String generateUserSequenceHistoryGetUUIDKey(Integer userId, String uuid) {
		return String.format("User:Users:%d:SequenceHistory:%s", userId, uuid);
	}

	public static String generateUserSequenceSendCoolDownKey(Integer userId, Integer sequenceId) {
		return String.format("User:Users:%d:SequenceSendCoolDown:%d", userId, sequenceId);
	}

	public static TimeUnit convertStringToTimeUnit(String string) {
		return switch (string) {
			case "SECONDS" -> TimeUnit.SECONDS;
			case "MINUTES" -> TimeUnit.MINUTES;
			case "HOURS" -> TimeUnit.HOURS;
			case "DAYS" -> TimeUnit.DAYS;
			default -> null;
		};
	}

	public static String convertTimeUnitToString(TimeUnit timeUnit) {
		return switch (timeUnit) {
			case SECONDS -> "SECONDS";
			case MINUTES -> "MINUTES";
			case HOURS -> "HOURS";
			case DAYS -> "DAYS";
			default -> null;
		};
	}

	public static Integer convertTimeUnitToSeconds(TimeUnit timeUnit, Integer value) {
		return switch (timeUnit) {
			case SECONDS -> value;
			case MINUTES -> value * 60;
			case HOURS -> value * 60 * 60;
			case DAYS -> value * 60 * 60 * 24;
			default -> null;
		};
	}

	public static Integer convertTimeStringToSeconds(String timeUnit, Integer value) {
		return switch (timeUnit) {
			case "SECONDS" -> value;
			case "MINUTES" -> value * 60;
			case "HOURS" -> value * 60 * 60;
			case "DAYS" -> value * 60 * 60 * 24;
			default -> null;
		};
	}

	@Resource
	public void setRedisTemplate(RedisTemplate<String, String> redisTemplate) {
		RedisUtil.redisTemplate = redisTemplate;
	}
}