package com.diffprivate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.utils.Rouletter;

public class ExpMechanism<T extends SelectBean<?>> {
	private double epsi;
	private double sensitive;
	Map<T, Double> mp;
	public ExpMechanism(double epsi,double sensitive) {
		// TODO Auto-generated constructor stub
		this.epsi=epsi;
		this.sensitive=sensitive;
		init();
	}
	public void init() {
		mp=new HashMap<T, Double>();		
	}
	public void addSelectBean(T obj) {
		double val=Math.exp((epsi/(2*sensitive))*obj.getVal());
		mp.put(obj, val);
	}
	public List<T> randomSelect(int n){
		Rouletter<T> rt=new Rouletter<T>(mp);
		List<T> res=new ArrayList<T>();
		while (((n--)>0)&&rt.hasNext()) {
			res.add(rt.pickOut());
		}
		return res;
	}
}
