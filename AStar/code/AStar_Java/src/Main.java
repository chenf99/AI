import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import java.util.Stack;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Main extends JFrame{
	String startStr = "854321607";//"854321607";
	AStar aStar;
	Stack<Node> result;
	
	//定义组件
	JPanel jpControl, jpDigital;
    int size = 9;
    JButton jbs[] = new JButton[size];
    
    JButton randomBtn, startBtn1, startBtn2;
    
    public JButton getJButtons(int i) {
    	return jbs[i];
    }
	
	//构造图形化界面
    // 构造函数
    public Main() {
    	aStar = new AStar(startStr);
        // 创建组件
    	jpControl = new JPanel();
    	jpDigital = new JPanel();
        for (int i = 0; i < size; i++) {
            jbs[i] = new JButton(String.valueOf(i));
            jbs[i].setBackground(Color.GRAY);					//设置按钮背景
            jbs[i].setBorderPainted(false);						//去掉按钮边框
            jbs[i].setForeground(Color.WHITE);					//设置字体颜色
            jbs[i].setFont(new Font("宋体", Font.BOLD, 32));		//设置字体样式
            jbs[i].setFocusPainted(false);						//设置点击按钮后不出现焦点边框
        }
        randomBtn = new JButton("random initialization");
        randomBtn.setPreferredSize(new Dimension(150, 30));
        randomBtn.setBackground(Color.ORANGE);
        randomBtn.setForeground(Color.BLACK);
        randomBtn.setFocusPainted(false);
        
        startBtn1 = new JButton("start by h1");
        startBtn1.setPreferredSize(new Dimension(150, 30));
        startBtn1.setBackground(Color.ORANGE);
        startBtn1.setForeground(Color.BLACK);
        startBtn1.setFocusPainted(false);
        
        startBtn2 = new JButton("start by h2");
        startBtn2.setPreferredSize(new Dimension(150, 30));
        startBtn2.setBackground(Color.ORANGE);
        startBtn2.setForeground(Color.BLACK);
        startBtn2.setFocusPainted(false);
        
        // 设置网格布局,这里只有前两个参数（行/列）3和3 的话，网格没有空隙
        jpDigital.setLayout(new GridLayout(3, 3, 10, 10));
        //JPanel布局默认是BorderLoyout

        // 添加组件
        this.add(jpControl, BorderLayout.SOUTH);
        this.add(jpDigital, BorderLayout.CENTER);
        for (int i = 0; i < size; i++) {
            jpDigital.add(jbs[i]);
        }
        jpControl.add(randomBtn, BorderLayout.WEST);
        jpControl.add(startBtn1, BorderLayout.CENTER);
        jpControl.add(startBtn2, BorderLayout.EAST);
        
        //添加点击监听器
        addClickListener();
        
        
        // 设置窗体属性
        this.setTitle("八数码");
        this.setSize(500, 500);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        int windowWidth = this.getWidth(); //获得窗口宽
        int windowHeight = this.getHeight();//获得窗口高
        Toolkit kit = Toolkit.getDefaultToolkit(); //定义工具包
        Dimension screenSize = kit.getScreenSize(); //获取屏幕的尺寸
        int screenWidth = screenSize.width; //获取屏幕的宽
        int screenHeight = screenSize.height; //获取屏幕的高
        this.setLocation(screenWidth/2-windowWidth/2, screenHeight/2-windowHeight/2);//设置窗口居中显示

        // 显示
        this.setVisible(true);
        //设置窗口大小不可变
        this.setResizable(false);
    }
    
    private void addClickListener() {
		randomBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {				
				//随机生成初始状态并显示
				StringBuilder sb = new StringBuilder(startStr);
				int count = 0;
				while (count != 5) {
					for (int i = 0; i < 9; ++i) {
						int a = (int)(Math.random() * 9);
						int b = (int)(Math.random() * 9);
						char tmp = sb.charAt(a);
						sb.setCharAt(a, sb.charAt(b));
						sb.setCharAt(b, tmp);
					}
					startStr = sb.toString();
					if (aStar.solvable(startStr)) break;
					count++;
				}
				printStr(startStr);
			}
		});
		
		startBtn1.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				action(1);
			}
		});
		
		startBtn2.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				action(2);
			}
		});
	}
    
    void action(int select) {
    	//开始AStar算法求解八数码
		aStar = new AStar(startStr);
		boolean solvable = aStar.solvable(startStr);
		if (solvable == false) {
			JOptionPane.showMessageDialog(null, "No Solution", "Info", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		result = aStar.AStarSearch(select);
		startStr = aStar.end;
		//显示结果
		new Thread(new Runnable() {
			@Override
			public void run() {
				randomBtn.setEnabled(false);
				startBtn1.setEnabled(false);
				startBtn2.setEnabled(false);
				System.out.println("最佳路径:");
				while (!result.empty()) {
					System.out.print("节点:");
					aStar.print(result.peek().digital);
					int depth = result.peek().depth;
					int eval = result.peek().eval;
					System.out.println("评估函数值: g: " + depth + " h: " + eval + " f: " + (depth + eval) + "\n");
					printStr(result.peek().digital);
					result.pop();
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				randomBtn.setEnabled(true);
				startBtn1.setEnabled(true);
				startBtn2.setEnabled(true);
			}
		}).start();
    }
    
    void printStr(String str) {
    	for (int i = 0; i < 9; ++i) {
        	String text = str.substring(i, i + 1);
        	if (text.equals("0")) {
        		getJButtons(i).setBackground(Color.WHITE);
        		getJButtons(i).setText("");
        		getJButtons(i).setBorder(BorderFactory.createRaisedBevelBorder());
        	}
        	else {
        		jbs[i].setBackground(Color.GRAY);
        		getJButtons(i).setText(text);
        		getJButtons(i).setBorder(null);
        	}
       }
    }
    
    public static void main(String[] args) {
        // 创建实例
        Main main = new Main();
        main.printStr(main.startStr);
    }

}
