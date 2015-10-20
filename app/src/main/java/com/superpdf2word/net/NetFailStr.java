package com.superpdf2word.net;

public class NetFailStr
{
	public static final int PARAM_ERROR	 = 100;
	public static final int NOT_PDF = 101;
	public static final int MD5_ERROR = 102;
	public static final int FILE_NOT_EXIST = 103;
	
	public static final int SERVER_ERROR = 201;
	public static final int VERSION_NOTALLOW = 202;
	
	public static String getReson(int errcode) {
		String str = null;
		switch(errcode) {
		case 100:
			str = "参数错误";
			break;
		case 101:
			str = "不是有效的PDF文件";
			break;
		case 102:
			str = "文件md5错误";
			break;
		case 103:
			str = "服务器无此文件";
			break;
		case 201:
			str = "服务器错误";
			break;
		case 202:
			str = "该版本已经停止提供服务，请更新APP到最新版本";
			break;
		}
		
		return str;
	}
}