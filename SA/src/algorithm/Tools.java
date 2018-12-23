package algorithm;
import java.util.Random;

public class Tools {
	// 判断是否为数字0-9
	public static boolean isDigit(char c) {
		return ((int)c >= 48 && (int)c <= 57);
	}
	
	// 交叉策略: 交换数组元素
	public static boolean swap(int[] path, int p1, int p2) {
		if(p1 >= path.length || p2 >= path.length || p1 < 0 || p2 < 0) return false;
		int temp = path[p2];
		path[p2] = path[p1];
		path[p1] = temp;
		//System.out.println("swap");
		return true;
	}
	
	// 交叉策略: 倒序片段
	public static boolean reverse(int[] path, int p1, int p2) {
		if(p1 >= path.length || p2 >= path.length || p1 < 0 || p2 < 0 || p1 == p2) return false;
		int u = p1, v = p2;
		if(p1 > p2) {
			u = p2;
			v = p1;
		}
		for(int i = u, j = v; i < j; i++, j--) {
			int temp = path[j];
			path[j] = path[i];
			path[i] = temp;
		}
		//System.out.println("reverse");
		return true;
	}
	
	// 交叉策略: 片段循环右移一位 -> 三变换法
	public static boolean threeChange(int[] path, int p1, int p2, int p3) {
		if(p1 >= path.length || p2 >= path.length || p3 >= path.length ||p1 < 0 || p2 < 0 || p3 < 0 || p1 == p2 || p2 == p3 || p1==p3) return false;
		int u = p1, v = p2, w = p3;
		if(u > v) {
			u = p2;
			v = p1;
		}
		if(v > w) {
			int temp = w;
			w = v;
			v = temp;
		}
		if(u > v) {
			int temp = v;
			v = u;
			u = temp;
		}
		int[] copy = path.clone();
		for(int i = 0; i < path.length; i++) {
			if(i<u) path[i] = copy[i];
			else if(i>=u && i<u+w-v) path[i] = copy[v+i+1-u];
			else if(i>=u+w-v && i<w+1) path[i] = copy[i-w+v];
			else path[i] = copy[i];
		}
		//System.out.println("rightshift");
		return true;
	}
	
	public static boolean rightShift(int[] path, int p1, int p2) {
		if(p1 >= path.length || p2 >= path.length || p1 < 0 || p2 < 0 || p1 == p2) return false;
		int u = p1, v = p2;
		if(p1 > p2) {
			u = p2;
			v = p1;
		}
		int temp = path[v];
		for(int i = v; i > u; i--) {
			path[i] = path[i-1];
		}
		path[u] = temp;
		//System.out.println("reverse");
		return true;
		
	}
	
	
	// 生成1-n的随机排列
	public static int[] create_random_permutation(int n) {
		int[] res = new int[n];
		for(int i = 0; i < n; i++) {
			res[i] = i+1;
		}
		for(int i = 0; i < n; i++) {
			int j = new Random().nextInt(n-i) + i + 1;
			Tools.swap(res, i, j);
		}
		return res;
	}
	
	public static void main(String[] args) {
		int[] path = new int[200];
		for(int i = 0; i < 200; i++) {
			path[i] = i;
		}
		Tools.threeChange(path, 156, 85, 85);
		for(int i = 0; i < 200; i++) {
			System.out.print(path[i] + " ");
		}
	}
	
	
}
