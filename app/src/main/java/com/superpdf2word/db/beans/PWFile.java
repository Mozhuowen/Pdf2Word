package com.superpdf2word.db.beans;

import com.j256.ormlite.field.DatabaseField;  
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "pwfile")
public class PWFile
{
	@DatabaseField(generatedId = true) 
	private int id;
	@DatabaseField(columnName = "filename") 
	private String filename;
	@DatabaseField(columnName = "filemd5") 
	private String filemd5;
	@DatabaseField(columnName = "filesize") 
	private int filesize;
	@DatabaseField(columnName = "stat") 
	private int stat;
	@DatabaseField(columnName = "uploadtime") 
	private long uploadtime;
	@DatabaseField(columnName = "downloadtime") 
	private long downloadtime;
	@DatabaseField(columnName = "filecode") 
	private String filecode;
	@DatabaseField(columnName = "errorcode") 
	private int errorcode;
	@DatabaseField(columnName = "wordpath") 
	private String wordpath;
	@DatabaseField(columnName = "sourcepath") 
	private String sourcepath;
	public PWFile(){}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getFilemd5() {
		return filemd5;
	}
	public void setFilemd5(String filemd5) {
		this.filemd5 = filemd5;
	}
	public int getFilesize() {
		return filesize;
	}
	public void setFilesize(int filesize) {
		this.filesize = filesize;
	}
	public int getStat() {
		return stat;
	}
	public void setStat(int stat) {
		this.stat = stat;
	}
	public long getUploadtime() {
		return uploadtime;
	}
	public void setUploadtime(long uploadtime) {
		this.uploadtime = uploadtime;
	}
	public long getDownloadtime() {
		return downloadtime;
	}
	public void setDownloadtime(long downloadtime) {
		this.downloadtime = downloadtime;
	}
	public String getFilecode() {
		return filecode;
	}
	public void setFilecode(String filecode) {
		this.filecode = filecode;
	}
	public int getErrorcode() {
		return errorcode;
	}
	public void setErrorcode(int errorcode) {
		this.errorcode = errorcode;
	}
	public String getWordpath() {
		return wordpath;
	}
	public void setWordpath(String wordpath) {
		this.wordpath = wordpath;
	}
	public String getSourcepath() {
		return sourcepath;
	}
	public void setSourcepath(String sourcepath) {
		this.sourcepath = sourcepath;
	}	
	
	public boolean equals(Object pwfile) {
		PWFile object = (PWFile)pwfile;
		if (object.getFilecode().equals(this.filecode))
			return true;
		else
			return false;
	}
}