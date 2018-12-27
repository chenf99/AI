package algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class GA {
	private List<City> datas;	// 城市数据
	private int cityNum;	// 城市数目
	private int N = 100;		// 种群大小
	private List<Individual> group;		// 种群
	private int[] bestPath;
	private double bestPathLen;			// 最优个体的路径长度
	private double bestFitness; 		// 最优个体的适应值
	private int T = 8000; 			// 接受代数
	private double Pcross = 0.95;		// 交叉概率
	private double Pmutation = 0.1;	// 变异概率
	private List<Individual> new_group;	// 下一代种群
	private int L = 0;
	
	// 最优解长度
	public double answer = 9352;
	

	double p;			//概率p
	boolean isFinished = false;
	
	// 基于排名的转盘赌选择
	double a = 1.1;		//参数a		
	double b = 0.2;		//参数b
	double[] proList;	//个体选中概率区间

	// 构造函数，传输城市TSP数据
	public GA(List<City> datas) {
		this.datas = datas;
		this.cityNum = datas.size();
		group = new ArrayList<>();
		new_group = new ArrayList<>();
		initProList();
		initGroup();
	}
	
	// 遗传算法演绎进化过程
	public void evolution() {
		Date begin = new Date();
		List<Individual> temp = group;
		while(++L < T) {
			System.out.println("-------" + L + "------" + "currentBest: " + group.get(0).len + "--best: " + bestPathLen);
			if(L%1000==999) showGroup(group);
			select();
			cross();
			mutation();
			/*Collections.sort(new_group);
			temp = group;
			group = new_group;
			new_group = temp;*/
			//System.out.println("after");
			//showGroup(group);
			Collections.sort(group);
			double len = group.get(0).len;
			if(len < bestPathLen) {
				bestPath = group.get(0).path;
				bestPathLen = group.get(0).len;
				bestFitness = group.get(0).fitness;
			}
		}
		isFinished = true;
		System.out.println(T);
		Date end = new Date();
		long dur = end.getTime() - begin.getTime();
		System.out.println("[Time] " + dur + "ms");
		System.out.println("[Result] " + bestPathLen);
		double eff = (bestPathLen/9352 - 1) * 100;
		System.out.println("[Effect] " + eff + "%");
		System.out.println("[Path] ");
		showPath(bestPath);
		//showGroup(group);
	}
	
	/*---------选择: 保留10%优良个体的轮盘赌选择法---------*/
	public void select() {
		//System.out.println("before:");
		//showGroup(group);
		new_group.clear();
		Individual individual;
		// 保留10%的精英个体
		for(int i = 0; i < N/10; i++) {
			individual = group.get(i).clone();
			new_group.add(individual);
		}
		// 其余90%依照排名的轮盘赌进行选择
		while(new_group.size() < N) {
			p = new Random().nextDouble();
			for(int j = 0; j < N; j++) {
				if(p <= proList[j]) {
					individual = group.get(j).clone();
					new_group.add(individual);
					break;
				}
			}
		}
		Collections.sort(new_group);
	}
	
	/*------交叉操作: 保留10%优良个体的OX交叉 ------*/
	public void cross() {
		// 选择后的交配池new_group进行交叉操作到group
		group.clear();
		// 保留10%的优良解到子代
		for(int i = 0; i < N/10; i++) {
			group.add(new_group.get(i).clone());
		}
		// 总共生成共2N个子代
		while(group.size() < 2*N) {
			p = new Random().nextDouble();
			int n = N;
			int s1 = new Random().nextInt(n);
			int s2 = new Random().nextInt(n);
			while(s2 == s1) s2 = new Random().nextInt(n);
			int temp = Math.max(s1, s2);
			s1 = Math.min(s1,s2);
			s2 = temp;
			int[] path1 = new_group.get(s1).path;
			int[] path2 = new_group.get(s2).path;
			if(p < Pcross) {
				int p1 = new Random().nextInt(cityNum);
				int p2 = new Random().nextInt(cityNum);
				while(p2 == p1) p2 = new Random().nextInt(cityNum);
				int min = Math.min(p1, p2);
				int max = Math.max(p1, p2);
				int[] childPath = ox(path1, path2, min, max);
				int[] childPath2 = ox(path2, path1, min, max);
				double len1 = getPathLen(childPath, datas);
				double len2 = getPathLen(childPath2, datas);
				Individual ind = new Individual(childPath, len1, answer/len1);
				Individual ind2 = new Individual(childPath2, len2, answer/len2);
				group.add(ind);
				group.add(ind2);
			}
		}
		// 淘汰末尾N个劣解子代(贪心选择)
		Collections.sort(group);
		for(int i = N; i < group.size(); i++) {
			group.remove(i);
		}
	}

	// OX Order Crossover两点顺序交换交叉操作
	private int[] ox(int[] path1, int[] path2, int min, int max) {
		int[] res = new int[cityNum];
		int index = 0;
		for(int i = 0; i < cityNum; i++) {
			if(i >= min && i <= max) {
				res[i] = path1[i];
				continue;
			}
			for(int j = index; j < cityNum; j++) {
				boolean isConflict = false;
				for(int k = min; k <= max; k++) {
					if(path1[k] == path2[j]) {
						isConflict = true;
						break;
					}
				}
				index++;
				if(isConflict) {
					continue;
				}
				else {
					res[i] = path2[j];
					break;
				}
			}
		}
		return res;
	}

	/*------ 变异操作：保留最优不变异，并保证新种群无重复个体(维持多样性)的三种领域变异------*/
	public void mutation() {
		double preLen = 0;
		double Len = 0;
		// 三种策略变异
		// 保留最优解不变异
		for(int i = 1; i < N; i++) {
			p = new Random().nextDouble();
			Len = group.get(i).len;
			if(p < Pmutation || Len == preLen) {
				int type = new Random().nextInt(3);
				int p1 = new Random().nextInt(cityNum);
				int p2 = new Random().nextInt(cityNum);
				int[] path = group.get(i).path;
				switch(type) {
					case 0:
						Tools.swap(path, p1, p2);
					case 1:
						Tools.reverse(path, p1, p2);
					case 2:
						Tools.rightShift(path, p1, p2);
				}
				double len = getPathLen(path, datas);
				double fitness = answer / len;
				group.get(i).setPath(path);
				group.get(i).setLen(len);
				group.get(i).setFitness(fitness);
			}
			preLen = Len;
		}
	}
	
	// 计算选择过程中每个排位个体的选择区间
	public void initProList() {
		proList = new double[cityNum];
		double temp = 0;
		for(int i = 0; i < N; i++) {
			temp += getProbability(i+1);
			proList[i] = temp;
		}
	}
	
	// 初始种群
	public void initGroup() {
		// 无差别随机生成种群
		int[] path;
		double len;
		double fitness;
		while(group.size() < N) {
			path = Tools.create_random_permutation(cityNum);
			len = getPathLen(path, datas);
			fitness = answer / len;
			Individual individual = new Individual(path, len, fitness);
			group.add(individual);
		}
		// 按适应度给种群排序
		Collections.sort(group);
		bestPath = group.get(0).path;
		bestPathLen = group.get(0).len;
		bestFitness = group.get(0).fitness;
	}
	
	// 计算路径总距离
	public double getPathLen(int[] path_, List<City> data_) {
		double res = 0;
		for(int i = 0; i < path_.length; i++) {
			int p1 = path_[i] - 1;
			int p2 = path_[(i+1)%path_.length] - 1;
			res += City.getDistance(data_.get(p1), data_.get(p2));
		}
		return res;
	}
	
	public void showGroup(List<Individual> group) {
		for(int i = 0; i < N; i++) {
			System.out.println("len: " + group.get(i).len + " pro: " + getProbability(i+1));
		}
	}
	
	// 根据种群中适应度排名，得到轮盘赌概率
	public double getProbability(int i) {
		return (a - b*i/(N+1))/N;
	}
	
	// 获取最佳路径
	public int[] getPath() {
		return bestPath;
	}
	
	public boolean getFinished() {
		return this.isFinished;
	}
	
	// 展示路径
	public void showPath(int[] path_) {
		for(int i = 0; i < path_.length-1; i++) {
			System.out.print(String.format(path_[i] + "->"));
			//if(i % 10 == 9) System.out.println();
		}
		System.out.println(path_[path_.length-1]);
	}
	
}



