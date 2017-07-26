package com.bean;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.kmeans.Cluster;
import com.kmeans.Partition;

public class ChangeBean implements Comparable<ChangeBean>{
	private Set<Trajectory> tras=new HashSet<Trajectory>();
	private List<ChangeUnit> ls=new ArrayList<ChangeUnit>();
	private Double dist=null;
	public List<ChangeUnit> getLs() {
		return ls;
	}
	public ChangeBean() {
		// TODO Auto-generated constructor stub
	}
	public ChangeBean(double t) {
		// TODO Auto-generated constructor stub
		dist=t;
	}
	public void addChangeUnit(ChangeUnit cu) {
		tras.add(cu.getTra());
		ls.add(cu);
		dist=null;
	}
	public double getDeltaDist() {
		if (dist!=null) {
			return dist;
		}
		dist=0.0;
		for (ChangeUnit cu : ls) {
			dist+=cu.getDist();
		}
		return dist;
	}
	public boolean checkTrajectoryExisted(Trajectory tra) {
		return tras.contains(tra);		
	}
	@Override
	public int compareTo(ChangeBean o) {
		// TODO Auto-generated method stub
		double res=getDeltaDist()-o.getDeltaDist();
		if (res<0) {
			return 1;
		}else if (res>0) {
			return -1;
		}
		return 0;
	}
	@Override
	public String toString() {
		// TODO Auto-generated metho d stub
		return ls.toString();
	}
	public List<Cluster<Trajectory>> getNewClusters(Partition<Trajectory> oldPartition) {
		List<Cluster<Trajectory>> newClus=new ArrayList<Cluster<Trajectory>>();
		List<Cluster<Trajectory>> oldClus=oldPartition.getClus();
		for (Cluster<Trajectory> cluster : oldClus) {
			newClus.add(cluster);
		}
		for (ChangeUnit cu : ls) {
			int t1=oldPartition.getClusterByObj(cu.getTra());//原来属于t1簇
			int t2=cu.getNewClu();//移动到t2簇
			Cluster<Trajectory> clu1=newClus.get(t1);
			Cluster<Trajectory> clu2=newClus.get(t2);
			Cluster<Trajectory> nClu1=new Cluster<Trajectory>();
			Cluster<Trajectory> nClu2=new Cluster<Trajectory>();
			List<Trajectory> ls=new ArrayList<Trajectory>();
			for (Trajectory tra : clu1.getList()) {
				if (tra.getId()==cu.getTra().getId()) {
					continue;
				}
				ls.add(tra);
			}
			nClu1.setList(ls);
			ls=new ArrayList<Trajectory>();
			for (Trajectory tra : clu2.getList()) {
				ls.add(tra);
			}
			ls.add(cu.getTra());
			nClu2.setList(ls);
			nClu1.setCenter(new Trajectory());
			nClu2.setCenter(new Trajectory());
			nClu1.setCenter(nClu1.getNewCenter());
			nClu2.setCenter(nClu2.getNewCenter());
			newClus.set(t1, nClu1);
			newClus.set(t2, nClu2);
		}
		return newClus;
	}
}
