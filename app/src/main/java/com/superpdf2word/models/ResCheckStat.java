package com.superpdf2word.models;

public class ResCheckStat
{
	private boolean stat;
	private int errcode;
	private int statcode;
	public ResCheckStat(boolean stat,int errcode,int statcode) {
		this.stat = stat;
		this.errcode = errcode;
		this.statcode =  statcode;
	}
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
	public int getStatcode() {
		return statcode;
	}
	public void setStatcode(int statcode) {
		this.statcode = statcode;
	}
}