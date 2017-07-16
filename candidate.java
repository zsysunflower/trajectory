package com.zsy.master;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zsy.OriTrajectory;

import ExpMechanism.ExpMechanism;
import ExpMechanism.Rouletter;

public class candidate {
	
	/*
	 * 聚类的簇数
	 */
	int k ;
	
	/*
	 * 通过修改最优聚类得到的候选划分个数
	 */
	int f;
	
	/*
	 * 敏感度
	 */
	double sensible ;
	
	/*
	 * 隐私预算
	 */
	double epsi;
	
	/*
	 * 每个候选项的标号
	 */
	 int  count = 0;
	
	/*
	 * 原始数据
	 */
   List<List<OriTrajectory>> finallist;
   
    /*
     * 最优划分
     */   
    KMeansClustering km ;
    
    /*
     * 存放所有候选划分的中心轨迹
     */
    List<List<List<OriTrajectory>>> center = new ArrayList<>();
   
   /*
    * 构造函数
    */
   public candidate(int k,int f,double sensible,double epsi,List finallist,KMeansClustering km){
	   this.k = k;
	   this.f = f;
	   this.sensible = sensible;
	   this.epsi = epsi;
	   this.finallist = finallist;
	   this.km = km;
   }
   
   /**
	 *得到所有的候选划分项Map<候选划分标号，该划分对应的打分函数值>
	 * @return
 * @throws CloneNotSupportedException 
	 */	
	private Map<String,Double> getAllcandi(List<List<Individual>> result) throws CloneNotSupportedException{
		Map<String,Double> candidates = new HashMap<>();
		Map<String,Double> kmeanCandidate = new HashMap<>();
   
		List<List<OriTrajectory>> list = finallist;//复制原始的轨迹数据		
		ExpMechanism exp = new ExpMechanism(km,epsi,sensible);
		//将最优的划分存入候选项
		candidates.put("k",exp.scores(km));
		System.out.println("通过聚类得到候选划分开始=======");
		kmeanCandidate=exp.allsocres();	
		candidates.putAll(kmeanCandidate);
		System.out.println("聚类得到候选划分结束=======");
		
	    System.out.println("修改得到候选划分开始========");		
			    
		//调用f个最终的修改轨迹方法
	 	
		
		for(int i=0;i<result.size();i++){
			List<Individual> modi = (List<Individual>)result.get(i);			
			
			//在最优划分上按照修改方法进行修改
			 KMeansClustering kmp =(KMeansClustering) km.clone();			  
			 List<List<List<OriTrajectory>>> optPartition = kmp.getClusterList();//最优划分集合
			
			  int group = 0;
			  int Origroup =0;
			  List<OriTrajectory> tra = null;
			for(Individual partion : modi){
				 group = partion.getGroup();//将该轨迹添加到该组
				 Origroup = partion.getOrigroup();//将该轨迹从该组中移除
			     tra = partion.getTralist();			     
				optPartition.get(Origroup).remove(tra);
				optPartition.get(group).add(tra);
			}			
		
			candidates.put("m\t"+String.valueOf(i),exp.scores(kmp));		
		}		
			
		
			System.out.println("修改得到候选划分结束===========");
		
		return candidates;
	}
	
	/**
	 * 从若干候选划分中利用指数机制选择一种划分
	 * @return
	 * @throws CloneNotSupportedException 
	 */
	public KMeansClustering chooseCandidate() throws CloneNotSupportedException{
		
		List<List<Individual>> result = modifaication();
		//得到所有的划分候选项及其对应的打分函数值
		Map<String,Double> mapofscore = getAllcandi(result);
		//得到所有候选划分的概率
		Map<String,Double> mapofpro = new HashMap<String,Double>();
		ExpMechanism exp = new ExpMechanism(km,epsi,sensible);
		mapofpro = exp.probability2(mapofscore);
		
		//用赌轮盘算法选择出一个划分标号
		Rouletter<String> rl = new Rouletter<String>(mapofpro);		
		String  choice = rl.pickOut();//无重复选择
		
		//根据划分标号得到该候选划分
		KMeansClustering partition = new KMeansClustering();
		
		//最优划分
		if(choice.equals("k")){
			partition = km;
			return partition;
		}
		
		//通过修改最优聚类得到的划分
		String[] strArr = choice.split("\t");		
		if(strArr[0].equals("m")){
			int modification = Integer.parseInt(strArr[1]);
			List<Individual> modi = result.get(modification);
			//在最优划分上按照修改方法进行修改
			 KMeansClustering kmp =(KMeansClustering) km.clone();			  
			 List<List<List<OriTrajectory>>> optPartition = kmp.getClusterList();//最优划分集合
			
			  int group = 0;
			  int Origroup =0;
			  List<OriTrajectory> tra = null;
			for(Individual partion : modi){
				 group = partion.getGroup();//将该轨迹添加到该组
				 Origroup = partion.getOrigroup();//将该轨迹从该组中移除
			     tra = partion.getTralist();			     
				optPartition.get(Origroup).remove(tra);
				optPartition.get(group).add(tra);
			}			
			partition = kmp;
			
			return partition;
		}
		
		//通过聚类得到的划分
		int k1 = Integer.parseInt(strArr[0]);
		int k2 = Integer.parseInt(strArr[1]);
	    KMeansClustering kmp =(KMeansClustering) km.clone();			
		
	    List<List<OriTrajectory>> clusteri = kmp.getClusterList().get(k1);
		clusteri.remove(k2);
		partition=kmp;
		
		return partition;
	}
	
	/**
	 * 逐个修改f个轨迹
	 * f：自定义参数10
	 * @return
	 */
	private List<Individual> subOptIndividual(){
		
		List<Individual> modifiIndiv = new ArrayList<>();		
		List<List<List<OriTrajectory>>> optiPartition = km.getClusterList();//最优划分
		List<List<OriTrajectory>> optiCenter = km.getClusteringCenter();//最优平均轨迹
		
		for(int i=0;i<optiPartition.size();i++){//向该聚类中放入一条轨迹
			
			List<OriTrajectory> keyTra = optiCenter.get(i);
			
			for(int j=0;j<optiPartition.size();j++){	//从该聚类中取出一条轨迹		
				
				if(i==j){
					continue;
				}
				List<List<OriTrajectory>> currCluster = optiPartition.get(j);
				
				List<OriTrajectory> OriTra = optiCenter.get(j);//当前聚类组的平均轨迹
				
				for(List<OriTrajectory> tra :currCluster){
					Individual indi = new Individual();
					indi.setTralist(tra);
					indi.setGroup(i);//放入第i类
					indi.setOrigroup(j);//该条轨迹原来属于哪一个聚类
					double dis = km.similarScore(tra, keyTra)-km.similarScore(tra, OriTra);
					indi.setDis(dis);					
					modifiIndiv.add(indi);					
				}				
			}
		}		
		//将修改集合按照dis进行排序
		Collections.sort(modifiIndiv,new Comparator<Individual>(){
			public int compare(Individual o1,Individual o2){				
				return o1.getDis().compareTo(o2.getDis());							
			}			
		});
		
		//返回前f个
		int size = f < modifiIndiv.size() ? f : modifiIndiv.size();
		List<Individual> subModifi = new ArrayList<>(size);
		subModifi = modifiIndiv.subList(0,size);		
		return subModifi;
	}
	
	/**
	 * 
	 * @param f ：自定义参数
	 * @param km ：最优聚类
	 * @return  f个修改划分
	 */
	@SuppressWarnings("unchecked")
	private List<List<Individual>> modifaication (){			
		//修改结果集合
		List<List<Individual>> result = new ArrayList<>();		
		
		List<Individual> indvSub = subOptIndividual();			
		//将第一个元素包装进result
		List<Individual> p0 = new ArrayList<>();
		p0.add(indvSub.get(0));
		result.add(p0);
		
		List<List<Individual>> temp = new ArrayList<>();//中间变量		
		for(int i=1;i<indvSub.size();i++){
			List<Individual> p1 = new ArrayList<>();
			Individual a = indvSub.get(i);
			p1.add(a);			
			temp.add(p1);
			for(List<Individual> mod :result){	
				int count = 0;
				for(Individual indi :mod){					
					
					if(indi.getTralist().equals(a.getTralist())){
						break;					
					}else{
						count++;
					}
				}
				if(count==mod.size()){//mod中不存在a代表的轨迹
					//合并操作
					mod.add(a);//将元素a放入mod中
					temp.add(mod);				
				}								
			}//endfor(List<Individual> mod :result){	
			
			result.addAll(temp);			
			temp.clear();
			result = sort(result);
            if(result.size()>f){
               List<List<Individual>> subList = new ArrayList<>(result.subList(0, f));
               result.clear();
               result = subList;
            }						
		}		
		return result;		
	}
	
	/**
	 * 对修改集合中的f个元素进行排序
	 * @param result
	 * @return
	 */
	private List<List<Individual>> sort(List<List<Individual>> result){
		Map<Integer,Double> map = new HashMap<Integer,Double>();
		
		List<List<Individual>> result01 = new ArrayList<>(result);
		List<List<Individual>> result02 = new ArrayList<>();
		
		for(int i = 0;i<result01.size();i++){				
			List<Individual> list = list = result01.get(i);			
			double sum = 0.0;
			for(int j=0;j<list.size();j++ ){
				sum +=list.get(j).getDis();
			}
			map.put(i,sum);
		}
		
		List<Map.Entry<Integer, Double>> listofmap = new ArrayList<>(map.entrySet());
		
		Collections.sort(listofmap,new Comparator<Map.Entry<Integer, Double>>(){
			public int compare(Map.Entry<Integer, Double> o1,Map.Entry<Integer, Double> o2){			
				return o1.getValue().compareTo(o2.getValue());
			}			
		});				
		for(Map.Entry<Integer, Double> sortmap:listofmap){
			List<Individual> sortlist = result01.get(sortmap.getKey());					
			result02.add(sortlist);					
		}		
		return result02;
	}
   
	
   
   
}
