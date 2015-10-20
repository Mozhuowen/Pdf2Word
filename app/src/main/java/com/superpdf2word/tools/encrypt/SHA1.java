/**
 * 对公众平台发送给公众账号的消息加解密示例代码.
 * 
 * @copyright Copyright (c) 1998-2014 Tencent Inc.
 */

// ------------------------------------------------------------------------

package com.superpdf2word.tools.encrypt;

import java.security.MessageDigest;

public class SHA1 {
	public static String getSHA1(String timestamp,String encrypt) throws AesException
			  {
		try {
			String[] array = new String[] {timestamp, encrypt };
			StringBuffer sb = new StringBuffer();
//			// 字符串排序
//			Arrays.sort(array);
			for (int i = 0; i < 2; i++) {
				sb.append(array[i]);
			}
			String str = sb.toString();
			// SHA1签名生成
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			md.update(str.getBytes());
			byte[] digest = md.digest();

			StringBuffer hexstr = new StringBuffer();
			String shaHex = "";
			for (int i = 0; i < digest.length; i++) {
				shaHex = Integer.toHexString(digest[i] & 0xFF);
				if (shaHex.length() < 2) {
					hexstr.append(0);
				}
				hexstr.append(shaHex);
			}
			return hexstr.toString().toUpperCase();
		} catch (Exception e) {
			e.printStackTrace();
			throw new AesException(AesException.ComputeSignatureError);
		}
	}
	public static String getSHA1(String timestamp,byte[] encrypt) throws AesException
	  {
		try {
			byte[] time = timestamp.getBytes();
			byte[] allbytes = new byte[time.length+encrypt.length];			
			for (int i=0;i<time.length;i++) {
				allbytes[i] = time[i];
			}
			for (int i=time.length;i<time.length+encrypt.length;i++) {
				allbytes[i] = encrypt[i-time.length];
			}
//			LogUtil.v("sHA-1 to encrypt str: ", new String(allbytes,"UTF-8"));
			// SHA1签名生成
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			md.update(allbytes);
			byte[] digest = md.digest();
		
			StringBuffer hexstr = new StringBuffer();
			String shaHex = "";
			for (int i = 0; i < digest.length; i++) {
				shaHex = Integer.toHexString(digest[i] & 0xFF);
				if (shaHex.length() < 2) {
					hexstr.append(0);
				}
				hexstr.append(shaHex);
			}
			return hexstr.toString().toUpperCase();
		} catch (Exception e) {
			e.printStackTrace();
			throw new AesException(AesException.ComputeSignatureError);
		}
	  }
	public static String getSHA1(byte[] encrypt) throws AesException
	{
		try {
//			LogUtil.v("sHA-1 to encrypt str: ", new String(allbytes,"UTF-8"));
			// SHA1签名生成
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			md.update(encrypt);
			byte[] digest = md.digest();
		
			StringBuffer hexstr = new StringBuffer();
			String shaHex = "";
			for (int i = 0; i < digest.length; i++) {
				shaHex = Integer.toHexString(digest[i] & 0xFF);
				if (shaHex.length() < 2) {
					hexstr.append(0);
				}
				hexstr.append(shaHex);
			}
			return hexstr.toString().toUpperCase();
		} catch (Exception e) {
			e.printStackTrace();
			throw new AesException(AesException.ComputeSignatureError);
		}
	}
}
