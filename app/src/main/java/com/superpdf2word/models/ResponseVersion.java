package com.superpdf2word.models;

public class ResponseVersion
{
	private boolean stat;
	private int errcode;
	private ModelAppUpdate version;
	public boolean isStat() {
		return stat;
	}
	public void setStat(boolean stat) {
		this.stat = stat;
	}
	public int getErrcode() {
		return errcode;
	}
	public void setErrcode(int errcode) {
		this.errcode = errcode;
	}
	public ModelAppUpdate getVersion() {
		return version;
	}
	public void setVersion(ModelAppUpdate version) {
		this.version = version;
	}
}