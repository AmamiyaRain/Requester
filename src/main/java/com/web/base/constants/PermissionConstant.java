package com.web.base.constants;

import java.math.BigInteger;

/**
 * @author NXY666
 */
public class PermissionConstant {
	/**
	 * 权限类型
	 */
	public static final BigInteger NORMAL_USER = new BigInteger("1", 2);

	public static final BigInteger USER_MANAGEMENT = new BigInteger("10", 2);

	public static final BigInteger UNLIMITED_REQUEST_REPEAT = new BigInteger("100", 2);
}