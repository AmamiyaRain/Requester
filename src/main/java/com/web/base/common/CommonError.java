package com.web.base.common;

/**
 * @author YBW
 */


public interface CommonError {
	int getErrCode();

	String getErrMsg();

	void setErrMsg(String msg);
}