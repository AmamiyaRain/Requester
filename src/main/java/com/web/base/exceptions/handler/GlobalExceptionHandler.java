package com.web.base.exceptions.handler;

import com.web.base.common.CommonResponse;
import com.web.base.exceptions.BusinessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author NXY666
 */
@ControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(BusinessException.class)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public Object handleException(BusinessException exception) {
		exception.printStackTrace();
		return CommonResponse.create(exception.getErrCode(), null, exception.getErrMsg());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public Object handleException(MethodArgumentNotValidException exception) {
		FieldError fieldError = exception.getBindingResult().getFieldError();
		assert fieldError != null;
		return CommonResponse.create(500, null, fieldError.getDefaultMessage());
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public Object handleException(DataIntegrityViolationException exception) {
		if (exception instanceof DuplicateKeyException) {
			return CommonResponse.create(500, null, "数据已存在");
		}
		return CommonResponse.create(500, null, "数据异常");
	}
//	/**
//	 * 异常处理函数
//	 * 处理所有Controller类抛出的异常
//	 */
//	@ExceptionHandler(Exception.class)
//	@ResponseStatus(HttpStatus.OK)
//	@ResponseBody
//	public Object handlerException(Exception ex) {
//		return CommonResponse.create(500, new Object(), "请求出现异常");
//	}
}