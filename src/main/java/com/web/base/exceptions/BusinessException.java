package com.web.base.exceptions;

import com.web.base.common.CommonError;
import lombok.ToString;

import java.util.Objects;

/**
 * 业务异常
 *
 * @author NXY666
 */
@ToString
public class BusinessException extends RuntimeException implements CommonError {
	private final Exception exception;

	private final CommonError commonError;

	public BusinessException(CommonError commonError) {
		this(null, commonError);
	}

	public BusinessException(CommonError commonError, String msg) {
		this(null, commonError, commonError.getErrMsg());
	}

	public BusinessException(Exception e, CommonError commonError) {
		this(e, commonError, commonError.getErrMsg());
	}

	public BusinessException(Exception e, CommonError commonError, String msg) {
		super();
		this.exception = e;
		this.commonError = commonError;
		this.commonError.setErrMsg(msg);
	}

	@Override
	public int getErrCode() {
		return commonError.getErrCode();
	}

	@Override
	public String getErrMsg() {
		return commonError.getErrMsg();
	}

	@Override
	public void setErrMsg(String msg) {
		commonError.setErrMsg(msg);
	}

	public String getExceptionMsg() {
		StringBuilder builder = new StringBuilder();
		for (StackTraceElement stackTraceElement : exception.getStackTrace()) {
			builder.append(stackTraceElement.toString()).append("\n");
		}
		return builder.toString();
	}

	public boolean isExceptionNull() {
		return Objects.isNull(this.exception);
	}
}