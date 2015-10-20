package com.superpdf2word.tools.encrypt;

import java.nio.charset.Charset;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.superpdf2word.tools.LogUtil;

import android.util.Base64;


public class MsgCrypt {
	static Charset CHARSET = Charset.forName("utf-8");
//	static Base64 base64 = new Base64();
	static byte[] aesKey;
	static String timestamp;
	static String content;
	static String encodingAesKey = "bacdefghijklmnopqrstuvwxzy1023456798BACDEGF";

	static byte[] encrypt(String time, String text) throws AesException {
//		aesKey = Base64.decodeBase64(encodingAesKey + "=");
		aesKey = Base64.decode(encodingAesKey + "=", Base64.DEFAULT);
//		System.out.println(aesKey.length);
		
		ByteGroup byteCollector = new ByteGroup();
		byte[] timestamp = time.getBytes(CHARSET);
		byte[] textBytes = text.getBytes(CHARSET);

		byteCollector.addBytes(timestamp);
		byteCollector.addBytes(textBytes);

		// ... + pad: 使用自定义的填充方式对明文进行补位填充
		byte[] padBytes = PKCS7Encoder.encode(byteCollector.size());
		byteCollector.addBytes(padBytes);

		// 获得最终的字节流, 未加密
		byte[] unencrypted = byteCollector.toBytes();

		try {
			// 设置加密模式为AES的CBC模式
			Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
			SecretKeySpec keySpec = new SecretKeySpec(aesKey, "AES");
			IvParameterSpec iv = new IvParameterSpec(aesKey, 0, 16);
			cipher.init(Cipher.ENCRYPT_MODE, keySpec, iv);

			// 加密
			byte[] encrypted = cipher.doFinal(unencrypted);

			// 使用BASE64对加密后的字符串进行编码
//			String base64Encrypted = base64.encodeToString(encrypted);
//			String base64Encrypted = Base64.encodeToString(encrypted, Base64.DEFAULT);

			return encrypted;
		} catch (Exception e) {
			e.printStackTrace();
			throw new AesException(AesException.EncryptAESError);
		}
	}
	
	static byte[] encrypt(String text) throws AesException
	{
		LogUtil.v("MsgCrypt info: ", "to be encrypt text: "+text);
//		aesKey = Base64.decodeBase64(encodingAesKey + "=");
		aesKey = Base64.decode(encodingAesKey + "=", Base64.DEFAULT);
		
		ByteGroup byteCollector = new ByteGroup();
		byte[] textBytes = text.getBytes(CHARSET);

		byteCollector.addBytes(textBytes);

		// ... + pad: 使用自定义的填充方式对明文进行补位填充
		byte[] padBytes = PKCS7Encoder.encode(byteCollector.size());
		byteCollector.addBytes(padBytes);

		// 获得最终的字节流, 未加密
		byte[] unencrypted = byteCollector.toBytes();

		try {
			// 设置加密模式为AES的CBC模式
			Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
			SecretKeySpec keySpec = new SecretKeySpec(aesKey, "AES");
			IvParameterSpec iv = new IvParameterSpec(aesKey, 0, 16);
			cipher.init(Cipher.ENCRYPT_MODE, keySpec, iv);

			// 加密
			byte[] encrypted = cipher.doFinal(unencrypted);

			return encrypted;
		} catch (Exception e) {
			e.printStackTrace();
			throw new AesException(AesException.EncryptAESError);
		}
	}

	static String decrypt(String text) throws AesException {
		byte[] original;
		try {
			// 设置解密模式为AES的CBC模式
			Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
			SecretKeySpec key_spec = new SecretKeySpec(aesKey, "AES");
			IvParameterSpec iv = new IvParameterSpec(Arrays.copyOfRange(aesKey, 0, 16));
			cipher.init(Cipher.DECRYPT_MODE, key_spec, iv);

			// 使用BASE64对密文进行解码
//			byte[] encrypted = Base64.decodeBase64(text);
			byte[] encrypted = Base64.decode(text, Base64.DEFAULT);

			// 解密
			original = cipher.doFinal(encrypted);
		} catch (Exception e) {
			e.printStackTrace();
			throw new AesException(AesException.DecryptAESError);
		}

		String xmlContent;
		try {
			// 去除补位字符
			byte[] bytes = PKCS7Encoder.decode(original);

			xmlContent = new String(bytes, CHARSET);
		} catch (Exception e) {
			e.printStackTrace();
			throw new AesException(AesException.IllegalBuffer);
		}

		return xmlContent;

	}
	public static String encryptMsg(String timeStamp,String content) throws AesException {
		// 加密
		byte[] encrypt = encrypt(timeStamp, content);

		// 生成安全签名
		String signature = SHA1.getSHA1(timeStamp,encrypt);

		// System.out.println("发送给平台的签名是: " + signature[1].toString());
		// 生成发送的xml
		return signature;
	}
	public static String encryptMsg(String context) throws AesException {
		// 加密
		byte[] encrypt = encrypt(context);

		// 生成安全签名
		String signature = SHA1.getSHA1(encrypt);
		// 生成发送的xml
		return signature;
	}

}