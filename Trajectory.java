package com.cjp.bean;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

public class Trajectory {

	/*
	 * 每条轨迹包含20个位置点
	 */
	private List<Location> locations;
	public Trajectory getNewCenter(List<Trajectory> list) {
		// TODO Auto-generated method stub
		Trajectory res = new Trajectory();

		// //新增加判断条////
		if (list.size() == 1) {
			// System.out.println("该簇只有�?��轨迹");
			return list.get(0);
		}

		if (list.size() == 0) {
			System.out.println("该簇�?��轨迹没有^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
			return null;
		}
		// ////////

		res.setLocations(new ArrayList<Location>());
		int m = list.get(0).size();
		for (int i = 0; i < m; i++) {
			double x = 0, y = 0;
			for (int j = 0; j < list.size(); j++) {
				x += list.get(j).getLocations().get(i).getX();
				y += list.get(j).getLocations().get(i).getY();
			}
			res.getLocations().add(
					new Location(x / list.size(), y / list.size()));
		}
		return res;
	}

	public int getId() {
		return locations.get(0).getID();
	}

	public int size() {
		if (locations==null) {
			return 0;
		}
		return locations.size();
	}


	public List<Location> getLocations() {
		return locations;
	}

	public void setLocations(List<Location> locations) {
		this.locations = locations;
	}

	public void addLocation(Location location) {
		if (locations == null) {
			locations = new ArrayList<Location>();
		}
		locations.add(location);
	}

	public static void main(String[] args) {
		List<String> ls = new ArrayList<String>();
		ls.add("asdf");
		ls.add("dd3");
		ls.add("uty");
		ls.add("zzdsfa");
		ls.add("hfdsg");
		Collections.sort(ls, new Comparator<String>() {
			public int compare(String o1, String o2) {
				return o1.compareTo(o2);
			}
		});
		for (int i = 0; i < ls.size(); i++) {
			System.out.println(ls.get(i));
		}
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder();
		sb.append("{" + locations.size() + "}[");
		for (int i = 0; i < locations.size(); i++) {
			if (i > 0) {
				sb.append(",");
			}
			sb.append(locations.get(i).toString());
		}
		sb.append("]");
		return sb.toString();
	}
}
