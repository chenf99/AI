import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class AStar {
	String start;
	String end;
	public AStar(String start, String end) {
		this.start = start;
		this.end = end;
	}
	
	public AStar(String start) {
		this.start = start;
		this.end = "123804765";
	}
	
	public AStar() {
		this.start = "283164705";
		this.end = "123804765";
	}
	
	//判断八数码问题是否有解
	/* 一个状态表示成一维的形式，求出除0之外所有数字的逆序数之和，
	也就是每个数字前面比它大的数字的个数的和，称为这个状态的逆序。

	若两个状态的逆序奇偶性 相同，则可相互到达，否则不可相互到达。 
	*/
	boolean solvable(String digital) {
	    int count1 = 0, count2 = 0;
	    for (int i = 0; i < 9; ++i) {
	        for (int j = 0; j < i; ++j) {
	            if (digital.charAt(i) != '0' && digital.charAt(j) > digital.charAt(i)) 
	                count1++;
	            if (end.charAt(i) != '0' && end.charAt(j) > end.charAt(i))
	                count2++;
	        }
	    }
	    return (count1 % 2 == count2 % 2);
	}
	
	//输出矩阵形式的节点的八数码状态
	void print(String digital) {
	    for (int i = 0; i < 9; ++i) {
	        if (i % 3 == 0) System.out.println(); 
	        System.out.print(digital.charAt(i) + " ");
	    }
	    System.out.println();
	}
	
	//评估函数1，以放错位置的数字个数为估计
	int evaluation1(String digital) {
	    int eval = 0;
	    //估计放错位置的数字个数
	    for (int i = 0; i < 9; ++i) {
	        if (digital.charAt(i) != end.charAt(i)) eval++;
	    }
	    return eval;
	}

	//评估函数2，以放错位置的数字移动到正确位置所需步数为估计
	//方法为计算横、纵坐标之差的绝对值的和
	int evaluation2(String digital) {
	    int eval = 0;
	    for (int i = 0; i < 9; ++i) {
	        if (digital.charAt(i) != end.charAt(i)) {
	            for (int j = 0; j < 9; ++j) {
	                if (digital.charAt(i) == end.charAt(j)) {
	                    //x为横坐标，y为纵坐标
	                    int x_i = i % 3, y_i = i / 3;
	                    int x_j = j % 3, y_j = j / 3;
	                    eval += Math.abs(x_i - x_j) + Math.abs(y_i - y_j);
	                }
	            }
	        }
	    }
	    return eval;
	}
	
	//产生下一步所有可能的八数码状态(一切后继)
	List<String> generateAllSuccessor(String digital) {
	    List<String> successors = new ArrayList<>();
	    int index = digital.indexOf('0');
	    //得到0的横坐标和纵坐标
	    int x = index % 3, y = index / 3;
	    //0上移
	    if (y != 0) {
	        StringBuilder result = new StringBuilder(digital);
	        char tmp = result.charAt(index);
	        result.setCharAt(index, result.charAt(index - 3));
	        result.setCharAt(index - 3, tmp);
	        successors.add(result.toString());
	    }
	    //0下移
	    if (y != 2) {
	    	StringBuilder result = new StringBuilder(digital);
	        char tmp = result.charAt(index);
	        result.setCharAt(index, result.charAt(index + 3));
	        result.setCharAt(index + 3, tmp);
	        successors.add(result.toString());
	    }
	    //0左移
	    if (x != 0) {
	    	StringBuilder result = new StringBuilder(digital);
	        char tmp = result.charAt(index);
	        result.setCharAt(index, result.charAt(index - 1));
	        result.setCharAt(index - 1, tmp);
	        successors.add(result.toString());
	    }
	    //0右移
	    if (x != 2) {
	    	StringBuilder result = new StringBuilder(digital);
	        char tmp = result.charAt(index);
	        result.setCharAt(index, result.charAt(index + 1));
	        result.setCharAt(index + 1, tmp);
	        successors.add(result.toString());
	    }
	    return successors;
	}
	
	//递归更新子节点深度
	void update(List<Node> successor, int depth) {
	    for (Node node : successor) {
	        if (node.depth > depth) {
	            node.depth = depth;
	            update(node.successor, depth);
	        }
	    }
	}
	
	//回溯得到成功的路径
	Stack<String> getPath(Node n) {
	    Stack<String> path = new Stack<>();
	    while (n != null) {
	        path.push(n.digital);
	        n = n.parent;
	    }
	    return path;
	}
	
	int evaluation(int select, String digital) {
	    return (select == 1) ? evaluation1(digital) : evaluation2(digital);
	}
	
	//参数表示选择哪种评估函数
	Stack<String> AStarSearch(int select) {
	    System.out.println("初始八数码状态");
	    print(start);
	    //首先判断是否有解
	    if (solvable(start) == false) {
	        System.out.println("No Solution");
	        return null;
	    }
	    //open表保存尚未考察过的节点
	    //close表保存已经考察过的节点
	    //搜索图由这两张表组成
	    List<Node> open = new ArrayList<>();
	    List<Node> close = new ArrayList<>();

	    //生成初始状态
	    Node S0 = new Node(start, null, new ArrayList<>(), new ArrayList<>(), 1, evaluation(select, start));
	    open.add(S0);

	    //存放成功的路径的栈
	    Stack<String> path;

	    while (true) {
	        //失败退出
	        if (open.isEmpty()) {
	            System.out.println("搜索失败");
	            return null;
	        }

	        //在open表上取f值最小的节点n
	        //n放入close表，并从open表移除
	        Collections.sort(open);
	        Node n = open.get(0);
	        open.remove(0);
	        close.add(n);
	        
	        //若n是目标状态，则成功退出
	        if (n.digital.equals(end)) {
	            System.out.println("搜索成功");
	            //从n开始回溯得到路径
	            path = getPath(n);
	            break;
	        }

	        //产生n的一切后继
	        List<String> successors = generateAllSuccessor(n.digital);
	        //后继的前驱节点
	        List<String> precursor = new ArrayList<>();
	        for (String string : n.precursor) {
	        	precursor.add(string);
	        }
	        precursor.add(n.digital);
	        for (int i = 0; i < successors.size(); ++i) {
	        	String successor = successors.get(i);
	            //对不是n的前驱节点的后继进行操作
	            if (n.isPrecursor(successor) == false) {
	                boolean isInGraph = false;
	                //在open表中寻找该后继
	                for (int j = 0; j < open.size(); ++j) {
	                	Node it = open.get(j);
	                    //后继在open表中
	                    if (it.digital.equals(successor)) {
	                        //当前路径较好，修改后继的指针，使其指向n
	                        if (n.depth + 1 < it.depth) {
	                            //从后继原来父节点的后继列表中删去这个后继
	                        	for (Node iNode : it.parent.successor) {
	                        		if (iNode.digital.equals(successor)) {
	                        			it.parent.successor.remove(iNode);
	                        			break;
	                        		}
	                        	}       
	                            //修改后继的父节点指针
	                            it.parent = n;
	                            it.depth = n.depth + 1;
	                            it.precursor = precursor;
	                        }
	                        isInGraph = true;
	                        break;
	                    }
	                }
	                //在close表中寻找该后继
	                for (int j = 0; j < close.size(); ++j) {
	                	Node it = close.get(j);
	                    //后继在close表中
	                    if (it.digital.equals(successor)) {
	                        //当前路径较好，修改后继的指针，使其指向n
	                        if (n.depth + 1 < it.depth) {
	                            //从后继原来父节点的后继列表中删去这个后继
	                        	for (Node iNode : it.parent.successor) {
	                        		if (iNode.digital.equals(successor)) {
	                        			it.parent.successor.remove(iNode);
	                        			break;
	                        		}
	                        	}
	                            //修改后继的父节点指针
	                            it.parent = n;
	                            it.depth = n.depth + 1;
	                            it.precursor = precursor;
	                            //更改其子节点的指针及费用
	                            update(it.successor, n.depth + 1);
	                        }
	                        isInGraph = true;
	                        break;
	                    }
	                }
	                //后继不在open表也不在close表
	                if (isInGraph == false) {
	                    Node child = new Node(successor, n, precursor, new ArrayList<>(), n.depth + 1, evaluation(select, successor));
	                    n.successor.add(child);
	                    open.add(child);
	                }
	            }
	        }
	    }

	    //返回成功的路径
	    return path;
	}
	public static void main(String[] argv) {
		AStar aStar = new AStar();
		/*aStar.print(aStar.start);
		System.out.println(aStar.solvable(aStar.start));
		System.out.println(aStar.evaluation1(aStar.start));
		System.out.println(aStar.evaluation2("123856074"));
		System.out.println(aStar.generateAllSuccessor("123406785"));
		
		Node node1 = new Node("12", null, null, null, 0, 0);
		Node node2 = new Node("123", node1, null, null, 0, 0);
		Stack<String> stack = aStar.getPath(node2);
		while (!stack.empty()) {
			System.out.println(stack.pop());
		}
		*/
		Stack<String> path = aStar.AStarSearch(2);
		while (!path.empty()) aStar.print(path.pop());
	}
}
