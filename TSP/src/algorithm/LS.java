package algorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class LS {
	private final List<City> datas;
	private volatile int[] path;
	Random random = new Random();
	private int t = 0;	// 迭代轮数
	private boolean isFinished;
	
	// 构造函数
	public LS(List<City> datas_) {
		this.datas = datas_;
		initPath();
		//showPath(path);
		isFinished = false;
		//showPath(path);
		//showPath(path);
	}
	
	//局部搜索
	public void search() {
		boolean searchEnd = false;
		while(!searchEnd) {
			//System.out.println("------Current Path: t=" + t + "------");
			double old_len = getPathLen(path, datas);
			//System.out.println("------Current Len: " + old_len + "------");
			double new_len;
			t++;
			// 根据不同的邻居选择策略进行搜索
			List<int[]> neighbors = getNeighbor(path);
			/*for(int i = 0; i < neighbors.size(); i++) {
				System.out.println("Show neighbor " + i);
				showPath(neighbors.get(i));
				
			}*/
			// 采用第一适应,碰到第一个更优解就选择
			int index;
			for(index = 0; index < neighbors.size(); index++) {
				new_len = getPathLen(neighbors.get(index), datas);
				//System.out.print("temp_len: " + new_len);
				if(new_len < old_len) {
					path = neighbors.get(index);
					
					break;
				}
			}
			// 无更优解,即进入局部最优值,结束搜索
			if(index == neighbors.size()) searchEnd = true;
		}
		isFinished = true;
		showPath(path);
		//System.out.println(t);
	}
	
	// get & set method
	public List<City> getDatas() {
		return this.datas;
	}
	public int[] getPath() {
		return this.path;
	}
	
	// 展示路径
	public void showPath(int[] path_) {
		System.out.println("[Result] " + getPathLen(path_, datas));
		double eff = ((getPathLen(path_, datas)/9352) - 1) * 100;
		System.out.println("[Effect] " + eff);
		System.out.println("[Path] ");
		for(int i = 0; i < path_.length-1; i++) {
			System.out.print(String.format(path_[i] + "->"));
			//if(i % 10 == 9) System.out.println();
		}
		System.out.println(path_[path_.length-1]);
	}
	
	// 初始一个路径,即局部搜索中的第一个解
	public void initPath() {
		int n = datas.size();
		path = new int[n];
		for(int i = 0; i < n; i++) {
			path[i] = datas.get(i).getTag();
		}
		for(int i = 0; i < n; i++) {
			int j = new Random().nextInt(n-i) + i + 1;
			Tools.swap(path, i, j);
		}
	}
	
	// 根据当前解生成邻居解集即新的可能解集
	public List<int[]> getNeighbor(int[] path_) {
		List<int[]> res = new ArrayList<>();
		while(res.size() < 1000) {
			int type = new Random().nextInt(3);
			int[] newPath = createNewPath(path_, type);
			if(!res.contains(newPath)) res.add(newPath);
		}
		return res;
		
	}
	
	// 产生新的解
	public int[] createNewPath(int[] old_path, int type) {
		int[] res = old_path.clone();
		int p1 = new Random().nextInt(old_path.length);
		int p2 = new Random().nextInt(old_path.length);
		//int p3 = new Random().nextInt(old_path.length);
		switch(type) {
			case 0:
				Tools.swap(res, p1, p2);
				return res;
			case 1:
				Tools.reverse(res, p1, p2);
				return res;
			case 2:
				//Tools.threeChange(res, p1, p2, p3);
				Tools.rightShift(res, p1, p2);
				return res;
		}
		//System.out.println(getPathLen(copy, datas));
		return res;
	}
	
	// 计算总距离
	public double getPathLen(int[] path_, List<City> data_) {
		double res = 0;
		for(int i = 0; i < path_.length; i++) {
			int p1 = path_[i] - 1;
			int p2 = path_[(i+1)%path_.length] - 1;
			res += City.getDistance(data_.get(p1), data_.get(p2));
		}
		return res;
	}

	public boolean getFinished() {
		return this.isFinished;
	}
	
	
}
