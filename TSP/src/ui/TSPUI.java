package ui;

import javax.swing.*;

import algorithm.City;
import algorithm.Tools;

import java.util.List;
import java.awt.*;
import java.io.*;

@SuppressWarnings("serial")
public class TSPUI extends JPanel {
	private List<City> datas;
	private int[] path;
	
	public void setDatas(List<City> datas) {
		this.datas = datas;
	}
	
	public void setPath(int[] path) {
		this.path = path;
	}
	
	
	public TSPUI(List<City> testdatas, int[] path) {
		this.datas = testdatas;
		this.path = path;
		/*for(int i = 0; i < datas.size(); i++) {
			System.out.println(datas.get(i).getTag() + "---" + datas.get(i).getX() + "---" + datas.get(i).getY());
		}
		System.out.println(path.length);*/
	}
	
	// 方法名不能改，覆盖了父类的paintComponent方法，重绘时会自动调用该方法
	public void paintComponent(Graphics g) { 
		super.paintComponent(g);
		int MAX_WIDTH = super.getWidth();
		int MAX_HEIGHT = super.getHeight();
		double city_max_x = City.getMaxX(datas);
		double city_min_x = City.getMinX(datas);
		double city_max_y = City.getMaxY(datas);
		double city_min_y = City.getMinY(datas);
		double CITY_WIDTH = city_max_x - city_min_x;
		double CITY_HEIGHT = city_max_y - city_min_y;
		double scaleX = (CITY_WIDTH / MAX_WIDTH);
		double scaleY = (CITY_HEIGHT / MAX_HEIGHT);
		int n = datas.size();

		
		
		try {
			String temp = "路径长度:" + City.getPathLen(path, datas);
			g.drawString(temp, 30, MAX_HEIGHT - 40);

			String pathStr = "";
			/*for(int i = 0; i < path.length-1; i++) {
				pathStr = pathStr + path[i] + "->";
				if(i % 10 == 9) pathStr+="\n";
			}
			pathStr += path[path.length-1];*/
			double eff = ((City.getPathLen(path, datas)/9352) - 1) * 100;
			g.drawString("超过最优解: " + eff + "%", 30, MAX_HEIGHT - 20);
			
			g.setColor(Color.RED); // 设置颜色
			// 保证框体大于500*500才画城市TSP连线图
			// 城市TSP连线图的边框margin
			int marginX = 100;
			int marginY = 180;
			int PIC_WIDTH = MAX_WIDTH - marginX;
			int PIC_HEIGHT = MAX_HEIGHT - marginY;
			double PIC_SCALE_X = (CITY_WIDTH) / PIC_WIDTH;
			double PIC_SCALE_Y = (CITY_HEIGHT) / PIC_HEIGHT;
			/*System.out.println("Windows: " + MAX_WIDTH + " " + MAX_HEIGHT);
			System.out.println("PIC: " + PIC_WIDTH + " " + PIC_HEIGHT);
			System.out.println("City: " + CITY_WIDTH + " " + CITY_HEIGHT);
			System.out.println("Scale: " + scaleX + " " + scaleY);
			System.out.println("PIC_SCALE: " + PIC_SCALE_X + " " + PIC_SCALE_Y);*/
			if (MAX_WIDTH > 100 && MAX_HEIGHT > 100) {
				// 画点
				for(int i = 0; i < n; i++) {
					int x = (int)((datas.get(i).getX()-city_min_x)/PIC_SCALE_X + marginX/2);
					int y = (int)((datas.get(i).getY()-city_min_y)/PIC_SCALE_Y + marginY/2);
					g.fillOval(x, y, 4, 4);
					//g.drawString(String.valueOf(i), x, y);
				}

				// 画路径
				g.setColor(Color.BLUE); // 设置颜色
				for(int i = 0; i < path.length; i++) {
					// 城市c1 -> c2
					int c1, c2;
					if(i == path.length-1) {
						c1 = path[i]-1;
						c2 = path[0]-1;
					}
					else {
						c1 = path[i]-1;
						c2 = path[i+1]-1;
					}
					int x1 = (int)((datas.get(c1).getX()-city_min_x)/PIC_SCALE_X + marginX/2+2);
					int y1 = (int)((datas.get(c1).getY()-city_min_y)/PIC_SCALE_Y + marginY/2+2);
					int x2 = (int)((datas.get(c2).getX()-city_min_x)/PIC_SCALE_X + marginX/2+2);
					int y2 = (int)((datas.get(c2).getY()-city_min_y)/PIC_SCALE_Y + marginY/2+2);
					g.drawLine(x1,y1,x2,y2);
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace(); // 异常处理？
		}
	}

	public static void main(String[] args) throws Exception {
		JFrame f = new JFrame();
		f.setTitle("GA算法");
		f.getContentPane().add(new TSPUI(null, new int[]{1,2}));
		f.setSize(1000, 640);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
		f.setResizable(true);
	}
}

