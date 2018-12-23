package algorithm;

import java.util.List;

public class City {
	private int tag;
	private double x;
	private double y;
	
	public City(int tag, double x, double y) {
		this.tag = tag;
		this.x = x;
		this.y = y;
	}
	
	public static double getDistance(City p1, City p2) {
		double x_offset = Math.abs(p1.x - p2.x);
		double y_offset = Math.abs(p1.y - p2.y);
		return Math.pow(Math.pow(x_offset, 2) + Math.pow(y_offset, 2), 0.5);
	}
	
	public int getTag() {
		return this.tag;
	}
	public double getX() {
		return this.x;
	}
	public double getY() {
		return this.y;
	}
	
	public static double getMaxX(List<City> datas) {
		if(datas == null || datas.size() == 0) return 0;
		double res = datas.get(0).getX();
		double temp;
		for(int i = 1; i < datas.size(); i++) {
			temp = datas.get(i).getX();
			res = (res < temp) ? temp : res;
		}
		return res;
	}
	
	public static double getMaxY(List<City> datas) {
		if(datas == null || datas.size() == 0) return 0;
		double res = datas.get(0).getY();
		double temp;
		for(int i = 1; i < datas.size(); i++) {
			temp = datas.get(i).getY();
			res = (res < temp) ? temp : res;
		}
		return res;
	}
	
	public static double getMinX(List<City> datas) {
		if(datas == null || datas.size() == 0) return 0;
		double res = datas.get(0).getX();
		double temp;
		for(int i = 1; i < datas.size(); i++) {
			temp = datas.get(i).getX();
			res = (res > temp) ? temp : res;
		}
		return res;
	}
	
	public static double getMinY(List<City> datas) {
		if(datas == null || datas.size() == 0) return 0;
		double res = datas.get(0).getY();
		double temp;
		for(int i = 1; i < datas.size(); i++) {
			temp = datas.get(i).getY();
			res = (res > temp) ? temp : res;
		}
		return res;
	}
	
	// ¼ÆËãÂ·¾¶×Ü¾àÀë
	public static double getPathLen(int[] path_, List<City> data_) {
		double res = 0;
		for(int i = 0; i < path_.length; i++) {
			int p1 = path_[i] - 1;
			int p2 = path_[(i+1)%path_.length] - 1;
			res += City.getDistance(data_.get(p1), data_.get(p2));
		}
		return res;
	}
}
