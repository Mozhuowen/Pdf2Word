package com.superpdf2word.db;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.superpdf2word.PWApplication;
import com.superpdf2word.db.beans.PWFile;

public class DataBaseUtil
{
	public static PWFile addFile(String filename,String filesource,String filemd5,int filesize,int stat,long upoadtime,String filecode) {
		PWFile file = new PWFile();
		 DataBaseHelper helper = DataBaseHelper.getHelper(PWApplication.getContext());
		 file.setSourcepath(filesource);
		 file.setFilemd5(filemd5);
		 file.setFilesize(filesize);
		 file.setUploadtime(upoadtime);
		 file.setFilecode(filecode);
		 file.setFilename(filename);
		 file.setStat(1);
		 
		 try {
			helper.getPWFileDao().create(file);
//			helper.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return file;
	}
	
	public static void updateObject(PWFile object) {
		DataBaseHelper helper = DataBaseHelper.getHelper(PWApplication.getContext());
		try {
			helper.getPWFileDao().update(object);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**检查文件是否重复，有重复返回false否则返回true*/
	public static boolean checkRepeatFile(String md5) {
		DataBaseHelper helper = DataBaseHelper.getHelper(PWApplication.getContext());
		Map<String,Object> query = new HashMap<String,Object>();
		query.put("filemd5", md5);
		List<PWFile> files = null;
		try {
			files = helper.getDao(PWFile.class).queryForFieldValues(query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return files.size() == 0 ? true:false;
	}
	
	public static void updatStat(String filecode,int stat) {
		DataBaseHelper helper = DataBaseHelper.getHelper(PWApplication.getContext());
		Map<String,Object> query = new HashMap<String,Object>();
		query.put("filecode", filecode);
		PWFile file = null;
		try {
			file = helper.getDao(PWFile.class).queryForFieldValues(query).get(0);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		file.setStat(stat);
		try {
			helper.getPWFileDao().update(file);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		helper.close();
	}
	
	public static List<PWFile> getallTask() {
		DataBaseHelper helper = DataBaseHelper.getHelper(PWApplication.getContext());
//		helper.getDao(PWFile.class).queryBuilder();

		QueryBuilder<PWFile, ?> queryBuilder = null;
		try {
			queryBuilder = helper.getDao(PWFile.class).queryBuilder();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		queryBuilder.orderBy("uploadtime", false);

		List<PWFile> list = null;
		try {
			list = queryBuilder.query();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
}