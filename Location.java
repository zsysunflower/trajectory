package com.cjp.bean;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Location {

	private Integer ID;// 出租车编号
	private double time;// 签到时间
	private Double x;// 经度
	private Double y;// 纬度
	private String name;// 位置名称

	public Location() {
		// TODO Auto-generated constructor stub
	}

	public Location(String name) {
		// TODO Auto-generated constructor stub
		this.name = name;
	}

	public Location(Double x, Double y) {
		// TODO Auto-generated constructor stub
		this.x = x;
		this.y = y;
	}

	public Integer getID() {
		return ID;
	}

	public double getTime() {
		return time;
	}

	public void setTime(double time) {
		this.time = time;
	}

	public Double getX() {
		return x;
	}

	public void setX(Double x) {
		this.x = x;
	}

	public Double getY() {
		return y;
	}

	public void setY(Double y) {
		this.y = y;
	}

	public void setID(Integer iD) {
		ID = iD;
	}

	public double getDistance(Location l2) {
		return Math.sqrt((x - l2.x) * (x - l2.x) + (y - l2.y) * (y - l2.y));
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return name + ":(" + x + "," + y + ")";
	}

	public Location subs(Location b) {
		return new Location(x - b.x, y - b.y);
	}

	public Location rotate(double alpha) {
		return new Location(Math.cos(alpha) * x - Math.sin(alpha) * y,
				Math.sin(alpha) * x + Math.cos(alpha) * y);
	}

	/**
	 * 
	 * @return (-pi,pi]
	 */
	public double getAngle() {
		if (x == 0 && y == 0) {
			return 0;
		}
		if (y >= 0) {
			return Math.acos(x / Math.sqrt(x * x + y * y));
		}
		return -Math.acos(x / Math.sqrt(x * x + y * y));
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
