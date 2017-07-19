package com.diffprivate;

import java.util.List;

import com.bean.Trajectory;
import com.kmeans.Cluster;

public class PartitionSelectionBean extends SelectBean<List<Cluster<Trajectory>>>{
	private double bestDist;
	public PartitionSelectionBean(List<Cluster<Trajectory>> tra,double bestDist) {
		super(tra);
		// TODO Auto-generated constructor stub
		this.bestDist=bestDist;
	}

	@Override
	protected double countVal() {
		// TODO Auto-generated method stub
		double allD=0;
		for (int i = 0; i < getObj().size(); i++) {
			double d=getObj().get(i).getVal();
			allD+=d;
		}
		return bestDist/allD;
	}
	
}
