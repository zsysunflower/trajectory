package com.cjp.btadp;

/**
 * <p>文件名称：DpBta.java</p>
 * <p>文件描述：树状数组实现差分隐私动态发布</p>
 * <p>学校：陕西师范大学 </p>
 * <p>作者：张双越 </p>
 * <p>内容摘要： </p>
 * <p>其他说明： </p>
 */
import java.util.Scanner;

import com.cjp.util.LapNoise;

public class DpBta2 {
	private double pn[];
	private double NoiPn[];
	private int maxM;
	private int p, n;
	private MakeCoefficient coefficient;
	private double e;

	private static int lowBit(int x) {
		return x & (-x);
	}

	public int getP() {
		return p;
	}

	public DpBta2(int maxM, double e) {
		// TODO Auto-generated constructor stub
		setMaxM(maxM);
		this.e = e;
		coefficient = new MakeCoefficient(maxM);
	}

	public DpBta2(int maxM, double e, MakeCoefficient coefficient) {
		// TODO Auto-generated constructor stub
		setMaxM(maxM);
		this.e = e;
		this.coefficient = coefficient;
	}

	public int getMaxM() {
		return maxM;
	}

	public void setMaxM(int maxM) {
		this.maxM = maxM;
		n = (int) Math.pow(2, maxM);
		pn = new double[maxM + 1];
		NoiPn = new double[maxM + 1];
		p = 0;
	}
	
	public double addAndQuery(double v) {
		if (p >= n - 1) {
			System.out.println("达到容量上界，不能再添加");
			return -1;
		}
		p++;
		double res=0;
		int lp=(int) (Math.log(lowBit(p))/Math.log(2.0));
		double t1=0; 
		for (int i = 0; i < lp; i++) {
			t1+=pn[i];
			pn[i]=0;
		}
		pn[lp]=v+t1;
		double lb=coefficient.getCof(p);
//		System.out.println("lb:"+lb);
		NoiPn[lp]=lb*pn[lp]+LapNoise.getLap(0, e);
		int k=p;
		while (k>0) {
			int q=(int) (Math.log(lowBit(k))/Math.log(2.0));
			lb=coefficient.getCof(k);
//			System.out.println("k:"+k+" lb:"+lb+" NoiPn[]:"+q+","+NoiPn[q]);
			res+=NoiPn[q]/lb;
			k-=lowBit(k);
		}
		return res;
	}
	public static void main(String[] args) {
		System.out.println("请输入数据容量参数k(容量为2^k-1):");
		Scanner cin = new Scanner(System.in);
		int n = cin.nextInt();
		System.out.println("请输入隐私参数e:");
		double e = cin.nextDouble();
		DpBta2 bta = new DpBta2(n, e);
		System.out.println("请输入要添加的数据:");
		int r=0;
		while (cin.hasNext()) {
			int x = cin.nextInt();
			r+=x;
			System.out.println("第" + bta.getP() + "次添加数据后，结果是" + bta.addAndQuery(x)
					+ "，真实值为" +r);
		}
	}
}
