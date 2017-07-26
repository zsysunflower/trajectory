package com.diffprivate;

public class Laplace {

	public Laplace() {
	}


	public double genNoise(double mu, double b) {
		double U = Math.random() - 0.5;// 通过均匀分布产生-0.5-0.5之间的数
		return mu - (b * Math.signum(U) * Math.log(1 - 2 * Math.abs(U))); // 拉普拉斯累积概率密度的逆函数
	}

	public double genNoise(double mu, double epsi,double sensibility) {
		return genNoise(mu, sensibility/epsi);
	}
	public double getAccumulate(double mu, double b,double x) {
		if (x<mu) {
			return 0.5*Math.exp((x-mu)/b);
		}
		return 1-0.5*Math.exp((mu-x)/b);
	}
	public double getAccumulate(double mu,double epsi,double sensibility,double x) {
		return getAccumulate(mu, sensibility/epsi,x);
	}
	public static void main(String[] args) {
		Laplace laplace=new Laplace();
		double t=0;
		double a=laplace.getAccumulate(0, 1, t+0.5)-laplace.getAccumulate(0, 1, t-0.5);
		t=2;
		double b=laplace.getAccumulate(0, 1, t+0.5)-laplace.getAccumulate(0, 1, t-0.5);
		t=1;
		double c=laplace.getAccumulate(0, 1, t+0.5)-laplace.getAccumulate(0, 1, t-0.5);
		System.out.println(b/a);
		System.out.println(c/b);
		System.out.println(laplace.getAccumulate(0, 1, t+0.5)-laplace.getAccumulate(0, 1, t-0.5));
	}
}
