package com.zsy.master;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import com.zsy.OriTrajectory;

/**
 * 该类实现轨迹聚类 只要用轨迹数据初始化该类，就可以得到该轨迹的一个聚类
 * @author Administrator
 *
 */
public class KMeansClustering implements Cloneable{
	
	/*
	 * 实验轨迹数据
	 */
	private List<List<OriTrajectory>> finallist;
	private int k;//每个时刻最后分成的组数
	private int maxClusterTimes = 20;//最大迭代次数
	
	private List<List<List<OriTrajectory>>> clusterList;//聚类的结果
	private List<List<OriTrajectory>> clusteringCenter;//平均轨迹/中心轨迹
	 
	public Object clone() throws CloneNotSupportedException{
		KMeansClustering o = null;	
		
		List<List<OriTrajectory>> copy = new ArrayList<>(clusteringCenter.size());
		Iterator<List<OriTrajectory>> its = this.clusteringCenter.iterator();
		while(its.hasNext()){			
			List<OriTrajectory> list = its.next();
			List<OriTrajectory> listc = new ArrayList<>(list.size());
			for(OriTrajectory tra : list){
				OriTrajectory trac = new OriTrajectory();
				trac.setID(tra.getID());
				trac.setTime(tra.getTime());
				trac.setX(tra.getX());
				trac.setY(tra.getY());
				listc.add(trac);				
			}		
			copy.add(listc);
		}
		
		
		List<List<List<OriTrajectory>>> copy1 =new  ArrayList<>(clusterList.size());
		Iterator<List<List<OriTrajectory>>> iterator = this.clusterList.iterator();
		while(iterator.hasNext()){		
			List<List<OriTrajectory>> listp = iterator.next();
			List<List<OriTrajectory>> list1 = new ArrayList<>(listp.size());			
			for(List<OriTrajectory> list2 : listp){
				List<OriTrajectory> listq = list2;
				List<OriTrajectory> ori = new ArrayList<>(listq.size());
				for(OriTrajectory tra1 : listq){
					OriTrajectory o2 = new OriTrajectory();
					o2.setID(tra1.getID());
					o2.setTime(tra1.getTime());
					o2.setX(tra1.getX());
					o2.setY(tra1.getY());
					ori.add(o2);
				}
				list1.add(ori);
			}
			copy1.add(list1);
		}
		
		o=(KMeansClustering) super.clone();
		
		//o.setClusteringCenter(copy);
		//o.setClusterList(copy1);
		o.clusteringCenter=copy;			
		o.clusterList = copy1;
		
		return o;
	}
	
	//有参构造函数
	public KMeansClustering(List finallist,int k){
		this.finallist = finallist;
		this.k = k;
		clustering();
	}
	
	//无参构造函数
	public KMeansClustering(){
		
	}
	
	
	public int getK(){
		return k;
	}
	
	public void setK(int k){
		if(k<1){
		  throw new IllegalArgumentException("k 必须大于0");  
		}
		this.k = k;
	}
	
	public int getMaxClusterTimes(){
		return maxClusterTimes;
	}
	
	public void setMaxClusterTimes(int maxClusterTimes){
		if(maxClusterTimes <10){
			throw new IllegalArgumentException("最大迭代次数必须大于10");
		}
		this.maxClusterTimes = maxClusterTimes;
	}
	
	
	
	public List<List<List<OriTrajectory>>> getClusterList() {
		return clusterList;
	}


	public void setClusterList(List<List<List<OriTrajectory>>> clusterList) {
		this.clusterList = clusterList;
	}


	public List<List<OriTrajectory>> getClusteringCenter() {
		return clusteringCenter;
	}


	public void setClusteringCenter(List<List<OriTrajectory>> clusteringCenter) {
		this.clusteringCenter = clusteringCenter;
	}


	/**
	 *对数据进行聚类
	 * @return
	 */
	public void  clustering(){
		
		if(finallist == null){
			System.out.println("finallist为空，无法聚类。");
			return;
		}
		
		//初始化k个随机轨迹
		int size = k > finallist.size() ? finallist.size() : k;
		
		List<List<OriTrajectory>>  centerT = new ArrayList<List<OriTrajectory>>(size);
		
		//将轨迹数据进行打乱，提高聚类结果的质量
		Collections.shuffle(finallist);		
		for(int i = 0;i<size; i++){
			centerT.add(finallist.get(i));
		}
		
		List<List<List<OriTrajectory>>> clustersList = new ArrayList<List<List<OriTrajectory>>>();
		List<List<List<OriTrajectory>>> preclustersList = null;
		List<List<OriTrajectory>>  precenterT =null;
		int times = 0;
		while(true){			  
		    clustersList = clustering(centerT);//聚类结果 	   
			
		    boolean flag = false;
			for(int i = 0;i<clustersList.size();i++){
				if(clustersList.get(i).size() == 0){
					//System.out.println(times+"uuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuu");
					flag = true;
					this.clusterList = preclustersList;
					this.clusteringCenter = precenterT;	
					break;
				}			
			}
			if(flag){//该次聚类不可取	
				//System.out.println("该次聚类个数小于k,取上次的聚类结果");
				break;
			}				
			  
			//继续下次聚类，更新每组的平均轨迹		
		    List<List<OriTrajectory>> nowCenter = new ArrayList<List<OriTrajectory>>();					
	  		for(List<List<OriTrajectory>> list : clustersList){//根据聚类结果计算中心
	  			nowCenter.add(getCenterT(list));			
	  		}
	  		//System.out.println("中心是否改变==============="+isCenterChange(centerT,nowCenter));
	  		if(times >= this.maxClusterTimes || !isCenterChange(centerT,nowCenter)){			
				this.clusterList = clustersList;
				this.clusteringCenter = nowCenter;	
				break;
			}else{
				precenterT = new ArrayList<>(centerT);//将该聚类中心保留下来
				preclustersList = new ArrayList<>();//将该次聚类结果保留下来	
				preclustersList = deepCopy(clustersList);
				centerT = new ArrayList<List<OriTrajectory>>(nowCenter);
				times++;				
			    clear(clustersList);
			   }	  		
		}
		
		//return clustersList;	
	
	
	}
	
	
	/**
	 * 深拷贝一个list到另一个list中
	 * @param <T>
	 */

	private  List<List<List<OriTrajectory>>> deepCopy(List<List<List<OriTrajectory>>> list){
		 List<List<List<OriTrajectory>>> copy = new ArrayList<>();
		 
		 for(List<List<OriTrajectory>> list1 : list){
			 List<List<OriTrajectory>> copy1 = new ArrayList<>();
			 for(List<OriTrajectory> list2 : list1){
				 List<OriTrajectory> copy2 = new ArrayList<>();
				 for(OriTrajectory ori :list2){
					 OriTrajectory tra = ori;
					 copy2.add(tra);					 
				 }
				 copy1.add(copy2);
			 }
			 copy.add(copy1);			 
		 }
		 return copy;
		
	}
	
	/**
	 * 开始一轮聚类
	 * @param preCenter
	 * @param times
	 */
	private List<List<List<OriTrajectory>>> clustering(List<List<OriTrajectory>> preCenter) {		
		List<List<List<OriTrajectory>>> clusterList = getListT(preCenter.size());		
			
		Collections.shuffle(finallist);
		for(int j = 0;j<finallist.size();j++){
			//寻找与平均轨迹最接近的轨迹
			List<OriTrajectory> o1 = finallist.get(j);
			int min = 0;			
			double minScore = similarScore(o1,preCenter.get(0));
			for(int i = 1;i < preCenter.size();i++){
				if(minScore > similarScore(o1,preCenter.get(i))){
					minScore = similarScore(o1,preCenter.get(i));
				    min = i;
				}
			}
			clusterList.get(min).add(o1);			
		}	
		
		return clusterList;
	}

	/**
	 * 清除聚类结果，重新聚类
	 * @param clusterList
	 */
	private void clear(List<List<List<OriTrajectory>>> clusterList) {
		
		for (List<List<OriTrajectory>> list : clusterList) {  
             list.clear();  
        }  
		clusterList.clear();
	}

	/*
	 * 判断聚类中心是否发生改变
	 */
	private boolean isCenterChange(List<List<OriTrajectory>> preCenter, List<List<OriTrajectory>> nowCenter){
		// TODO Auto-generated method stub
		boolean bol = false;
			
			for(int i=0;i<nowCenter.size();i++){
				if(similarScore(preCenter.get(i),nowCenter.get(i))>300){
					bol = true;
					break;
				}		
			}
		
		return bol;
	}

	/**
	 * 根据该聚类组重新计算该组的平均轨迹,更新中心轨迹
	 * @param list
	 * @return
	 */
	private List<OriTrajectory> getCenterT(List<List<OriTrajectory>> list) {
		
		List<OriTrajectory> idealTra = new ArrayList<OriTrajectory>();
		double meanX = 0.0;
		double meanY = 0.0;
		int size = list.size();
		if(size == 0){
			System.out.println("聚类中心的个数为0，返回null");
			return null;
		}
		if(size == 1){
			//该聚类组中只有一条轨迹,返回该条
           // System.out.println("聚类中心的个数为1，返回该条中心");
            
			return list.get(0);
		}
				
		//寻找最中心的那条轨迹，但是这条轨迹不一定是真实存在的
			for(int i=0;i<list.get(0).size();i++){	//20个时间
				OriTrajectory tra = new OriTrajectory();
				for(int j=0;j<list.size();j++){//该组优多少条轨迹
				  meanX += list.get(j).get(i).getX();
				  meanY += list.get(j).get(i).getY();  
			    }
				
				meanX = meanX/list.size();
				meanY = meanY/list.size();
				
				tra.setX(meanX);
				tra.setY(meanY);
				idealTra.add(tra);
		}
			
	   //在该聚类组中寻找真实存在的，比较中心的轨迹
			/*int min = 0;
			double minScore = similarScore(idealTra,list.get(0));
			for(int i = 1;i<list.size();i++){				
				if(minScore>similarScore(idealTra,list.get(i))){
					minScore = similarScore(idealTra,list.get(i));
					min=i;					
				}						
		}		*/
	
			return idealTra;			
	}

	/**
	 * 判断两个轨迹之间的相似度.用轨迹的对应点之间的平均距离代表轨迹之间的距离
	 * @param o1
	 * @param o2
	 * @return
	 */
	public double similarScore(List<OriTrajectory> o1, List<OriTrajectory> o2) {
		double sum = 0;	
		
		if(o1.size()==0 || o1==null){
			System.out.println("判断两条轨迹相似度o1轨迹为空");
		}
		if(o2.size()==0 || o2==null){
			System.out.println("判断两条轨迹相似度o2轨迹为空");
		}
		for(OriTrajectory ori1:o1){
			if(ori1==null){
				System.out.println(o1.size()+"\to1o1o1o1oo1o1o1o1o1o1o1o1o1o1o1o1o1o1");
			}
		}
		for(OriTrajectory ori2 :o2){
			if(ori2 == null){
				System.out.println(o2.size()+"\to2o2o2o2oo2o2o2o2oo2o2o2o2o2o2o2oo2o2o2o2o2o");	
			}
		}
		
		
		for(int i = 0 ;i<o1.size();i++){			
			sum += o1.get(i).Distance(o2.get(i));	
		  //sum +=o1.get(i).getDistance(o2.get(i));
		}	
		
		double similar = sum*1.0/o1.size();		
		
		return similar;
	}

	/**
	 * 初始化一个聚类结果
	 * @param size
	 * @return
	 */
	private List<List<List<OriTrajectory>>> getListT(int size) {
		// TODO Auto-generated method stub
		List<List<List<OriTrajectory>>> list = new ArrayList<>(size);
		
		for(int i = 0;i<size; i++){
			list.add(new ArrayList<List<OriTrajectory>>());
		}
		return list;
	} 	
}
