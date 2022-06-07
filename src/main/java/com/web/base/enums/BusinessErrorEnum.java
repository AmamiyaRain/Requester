package com.web.base.enums;


import com.web.base.common.CommonError;

/**
 * @author YBW&NXY666
 */
public enum BusinessErrorEnum implements CommonError {
	/**
	 * 一般错误 （1xx）
	 */
	UNKNOWN_ERROR(10001, "未知错误"),
	ABNORMAL_DATA(10002, "数据异常"),
	REQUEST_IS_HANDLING(10003, "已有相似的请求正在处理，请稍后再试"),
	MISSING_REQUIRED_PARAMETERS(10004, "缺少必要的参数"),
	PAGE_PARAMETER_ERROR(10005, "分页参数错误"),
	DATABASE_QUERY_FAILED(10006, "数据库查询失败"),
	DATABASE_INSERT_FAILED(10007, "数据库插入失败"),
	DATABASE_DELETE_FAILED(10008, "数据库删除失败"),
	DATABASE_UPDATE_FAILED(10009, "数据库更新失败"),
	REDIS_SAVE_FAILED(10007, "Redis保存失败"),
	REDIS_GET_FAILED(10008, "Redis获取失败"),
	REDIS_DELETE_FAILED(10009, "Redis删除失败"),
	REDIS_KEY_NOT_EXIST(10010, "Redis key不存在"),

	/**
	 * 用户相关 （2xx）
	 */
	NOT_LOGGED_IN(20001, "未登录，请先登录"),
	USER_ALREADY_EXISTS(20002, "当前用户名已被使用"),
	REGISTER_ERROR(20003, "注册失败，请重试"),
	LOGIN_FAILED(20004, "帐号或密码错误"),
	USER_NOT_EXISTS(20005, "用户不存在"),
	TOKEN_EXPIRED(20006, "登录状态已失效，请重新登录"),
	PASSWORD_NOT_CHANGE(20007, "密码未发生改变"),
	CHECK_OLD_PASSWORD_FAILED(20008, "旧密码错误"),
	MODIFY_PASSWORD_FAILED(20009, "密码修改失败，请重试"),
	GENERATE_TOKEN_FAILED(20010, "身份标识生成失败"),
	PARSE_TOKEN_FAILED(20011, "身份标识解析失败"),
	PHONE_NOT_CHANGE(20012, "手机号码未发生改变"),
	PHONE_ALREADY_EXISTS(20013, "当前手机号已被使用"),
	TEMP_AUTH_EXPIRED(20014, "身份认证已过期，请重新认证"),
	CANNOT_DELETE_OWN_ACCOUNT(20015, "不能删除自己的帐号"),
	USER_NOT_CORRECT(20016, "不正确的用户"),
	MISSING_USER_PHONE(20017, "缺少已绑定手机号"),

	/**
	 * 权限相关 （3xx）
	 */
	PERMISSION_DENIED(30001, "权限不足"),
	PERMISSION_ALREADY_EXISTS(30002, "权限已存在"),
	ROLE_ALREADY_EXISTS(30003, "角色已存在"),
	ROLE_NOT_EXISTS(30004, "角色不存在"),
	DELETED_PERMISSION_FAILED(30005, "权限删除失败"),
	MODIFY_OWN_ROLE_FAILED(30006, "无法编辑自己的角色"),
	MODIFY_ROLE_DENIED(30007, "当前角色禁止修改"),
	DELETE_PERMISSION_DENIED(30008, "当前权限禁止删除"),

	/**
	 * 文件相关 （4xx）
	 */
	FILE_DAMAGED(40001, "文件损坏"),
	UNRECOGNIZED_FILE_TYPE(40002, "文件类型无法识别"),
	FILE_TYPE_ERROR(40003, "文件格式错误"),
	UPLOAD_IMAGE_ERROR(40004, "文件上传失败"),

	/**
	 * Vaptcha相关 （5xx）
	 */
	VAPTCHA_CHECK_FAILED(50001, "手势确认未通过，请重试"),
	VAPTCHA_LOW_SCORE(50002, "手势可信度过低，请重试"),
	VAPTCHA_CHECK_ERROR(50003, "手势确认时出现错误，请稍后再试"),
	CHECK_VERIFICATION_CODE_ERROR(50004, "验证码校验失败"),
	CHECK_VERIFICATION_CODE_FAILED(50005, "验证码错误"),
	SEND_VERIFICATION_CODE_ERROR(50006, "短信验证码发送失败，请稍后再试"),
	SEND_VERIFICATION_CODE_COOLING(50007, "距上次发送未超过 2 分钟，请稍后再试"),

	/**
	 * 请求相关（6xx）
	 */
	GENERATE_HTTP_REQUEST_FAILED(60001, "生成HTTP请求失败"),
	SEND_HTTP_REQUEST_FAILED(60002, "发送HTTP请求失败"),
	METHOD_CANT_BE_SUPPORTED(60004, "不被支持的请求方法"),
	GET_METHOD_CANNOT_HAVE_BODY(60005, "GET方法不应有请求体"),
	REQUEST_INDEX_OUT_OF_RANGE(60006, "序列号超限"),
	DELETING_WORKING_REQUEST(60007, "该请求正在使用中"),
	REQUEST_BODY_TOO_LONG(60008, "请求体过长"),
	/**
	 * 序列相关（7xx）
	 */
	SEQUENCE_COUNT_EXCEEDS_LIMIT(70001, "序列数量超限"),
	SEQUENCE_REQUEST_NOT_EXIST(70002, "请求序列不正确"),
	SEQUENCE_NOT_EXIST(70003, "序列不存在"),

	SEQUENCE_DOES_NOT_HAVE_REQUEST(70004, "序列需包含有效请求"),

	SEQUENCE_REPEAT_TIME_NOT_VALID(70005, "序列循环时间不合法"),
	SEQUENCE_REPEAT_TIME_UNIT_ERROR(70006, "序列循环时间单位不合法"),
	SEQUENCE_REPEAT_TIME_TOO_FAST(70007, "序列循环时间太短，请至少设置为 1 小时"),
	SEQUENCE_REPEAT_TIME_TOO_LONG(70008, "序列循环时间太长，请至多设置为 24 小时"),

	/**
	 * 短信相关（10xx）
	 */
	SENDING_MESSAGE_ERROR(100001, "验证码发送失败，请稍后重试"),
	SENDING_MESSAGE_COOLING(100002, "距上次发送未超过 2 分钟，请稍后再试");


	private final int errCode;

	private String errMsg;

	BusinessErrorEnum(int errCode, String errMsg) {
		this.errCode = errCode;
		this.errMsg = errMsg;
	}

	@Override
	public int getErrCode() {
		return this.errCode;
	}

	@Override
	public String getErrMsg() {
		return this.errMsg;
	}

	@Override
	public void setErrMsg(String msg) {
		this.errMsg = msg;
	}
}