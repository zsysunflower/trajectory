package com.diffprivate;

abstract public class SelectBean<T> {
	private T obj;
	private Double val;
	public SelectBean(T obj) {
		// TODO Auto-generated constructor stub
		this.obj = obj;
		val=null;
	}
	public double getVal(){
		if (val==null) {
			val=countVal();
		}
		return val;
	}
	protected abstract double countVal();
	public T getObj() {
		return obj;
	}
}
