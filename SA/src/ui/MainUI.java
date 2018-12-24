package ui;

import algorithm.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.*;  

import java.awt.*;  
import java.awt.event.ActionEvent;  
import java.awt.event.ActionListener;  
import java.awt.geom.Line2D;

import javax.swing.JFrame;

public class MainUI extends JFrame implements ActionListener {

	
	//定义界面组件
	JLabel jlb1, jlb2, jlb3 = null;
	JButton jb1, jb2, jb3, jb4= null;
	JPanel jp1, jp2, jp3, jp4, jp5, jp6, jp7 = null;
	JTextField jtf = null;
	
	public JFrame f;
	public TSPUI tspui;
	public SA sa;
	public static volatile List<City> datas;
	
	public JFrame lsf;
	public TSPUI lstspui;
	public LS ls;
	
	public JFrame gaf;
	public TSPUI gatspui;
	public GA ga;
	
	
	public MainUI() {
		
		jb1 = new JButton("LS");
		jb2 = new JButton("SA");
		jb3 = new JButton("GA");
		//jb4 = new JButton("GA");
		
		//设置监听
		jb1.addActionListener(this);
		jb2.addActionListener(this);
		jb3.addActionListener(this);
		//jb3.addActionListener(this);
		//jb4.addActionListener(this);
		
		jp1 = new JPanel();
		jp2 = new JPanel();
		jp3 = new JPanel();
		jp1.add(jb1);
		jp2.add(jb2);
		jp3.add(jb3);
		
		this.add(jp1);
		this.add(jp2);
		this.add(jp3);
		
		
		//设置布局管理器  
        this.setLayout(new GridLayout(3,1,200,30));  
        //给窗口设置标题  
        this.setTitle("人工智能TSP");  
        //设置窗体大小  
        this.setSize(360,240);  
        //设置窗体初始位置  
        //this.setLocation(1000, 150);  
        //设置当关闭窗口时，保证JVM也退出  
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
        //显示窗体  
        this.setVisible(true);  
        this.setResizable(true); 
	}
	
	public static void main(String[] args) {
		MainUI m = new MainUI();
		// 城市抽象的点集
		String filePath = "Qatar.txt";
		datas = new ArrayList<>();
		// 读取文件数据
		try {
			FileInputStream fis = new FileInputStream(filePath);
			int buffer_size = fis.available();
			byte[] buffer = new byte[buffer_size];
			int read_size = fis.read(buffer);
			String str = new String(buffer);
			String[] lines = str.split("\n");
			for(int i = 0; i < lines.length; i++) {
				if(Tools.isDigit(lines[i].charAt(0))) {
					String[] line_data = lines[i].split(" ");
					int tag = Integer.parseInt(line_data[0]);
					double x = Double.valueOf(line_data[1]);
					double y = Double.valueOf(line_data[2]);
					datas.add(new City(tag, x, y));
				}
				else if(i != lines.length-1) System.out.print(lines[i]);
			}
			
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void actionPerformed(ActionEvent c) {
		if (c.getActionCommand() == "LS") {
			jb1.setEnabled(false);
			Thread lsThread = new Thread(new Runnable(){
				@Override
				public void run() {
					lsf = new JFrame();
					lsf.setTitle("LS算法");
					System.out.println("-------------LS Solution----------");
					ls = new LS(datas);
					int[] path;
					path = ls.getPath();
					lstspui = new TSPUI(datas, path);
					lsf.getContentPane().add(lstspui);
					lsf.setSize(600, 480);
					lsf.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
					lsf.setVisible(true);
					lsf.setResizable(true);
					Thread updateThread = new Thread(new Runnable(){
						@Override
						public void run() {
							try {
								while(!ls.getFinished()) {
									int[] current_path = ls.getPath();
									Thread.sleep(100);
									lsf.getContentPane().setVisible(false);
									lsf.getContentPane().remove(lstspui);
									lstspui = new TSPUI(datas, current_path);
									lsf.getContentPane().add(lstspui);
									lsf.getContentPane().repaint();
									lsf.getContentPane().setVisible(true);
								}
								int[] current_path = ls.getPath();
								lsf.getContentPane().setVisible(false);
								lsf.getContentPane().remove(lstspui);
								lstspui = new TSPUI(datas, current_path);
								lsf.getContentPane().add(lstspui);
								lsf.getContentPane().repaint();
								lsf.getContentPane().setVisible(true);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					});
					updateThread.start();
					ls.search();
					jb1.setEnabled(true);
					JOptionPane.showConfirmDialog(null, "LS", "完成搜索", JOptionPane.CANCEL_OPTION);
				}
				
			});
			lsThread.start();
		}
		if (c.getActionCommand() == "SA") {
			jb2.setEnabled(false);
			Thread saThread = new Thread(new Runnable(){
				@Override
				public void run() {
					f = new JFrame();
					f.setTitle("SA算法");
					System.out.println("-------------SA Solution----------");
					sa = new SA(datas);
					int[] path;
					path = sa.getPath();
					//System.out.println(path.length);
					tspui = new TSPUI(datas, path);
					f.getContentPane().add(tspui);
					f.setSize(600, 480);
					f.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
					f.setVisible(true);
					f.setResizable(true);
					Thread updateThread = new Thread(new Runnable(){
						@Override
						public void run() {
							try {
								while(!sa.getFinished()) {
									int[] current_path = sa.getPath();
									Thread.sleep(100);
									f.getContentPane().setVisible(false);
									f.getContentPane().remove(tspui);
									tspui = new TSPUI(datas, current_path);
									f.getContentPane().add(tspui);
									f.getContentPane().repaint();
									f.getContentPane().setVisible(true);
								}
								int[] current_path = sa.getPath();
								f.getContentPane().setVisible(false);
								f.getContentPane().remove(tspui);
								tspui = new TSPUI(datas, current_path);
								f.getContentPane().add(tspui);
								f.getContentPane().repaint();
								f.getContentPane().setVisible(true);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					});
					updateThread.start();
					sa.search();
					jb2.setEnabled(true);
					JOptionPane.showConfirmDialog(null, "SA", "完成搜索", JOptionPane.CANCEL_OPTION);
				}
				
			});
			saThread.start();
		}
		
		if (c.getActionCommand() == "GA") {
			jb3.setEnabled(false);
			Thread gaThread = new Thread(new Runnable(){
				@Override
				public void run() {
					gaf = new JFrame();
					gaf.setTitle("GA算法");
					System.out.println("-------------GA Solution----------");
					ga = new GA(datas);
					int[] path;
					path = ga.getPath();
					gatspui = new TSPUI(datas, path);
					gaf.getContentPane().add(gatspui);
					gaf.setSize(600, 480);
					gaf.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
					gaf.setVisible(true);
					gaf.setResizable(true);
					Thread updateThread = new Thread(new Runnable(){
						@Override
						public void run() {
							try {
								while(!ga.getFinished()) {
									int[] current_path = ga.getPath();
									Thread.sleep(100);
									gaf.getContentPane().setVisible(false);
									gaf.getContentPane().remove(gatspui);
									gatspui = new TSPUI(datas, current_path);
									gaf.getContentPane().add(gatspui);
									gaf.getContentPane().repaint();
									gaf.getContentPane().setVisible(true);
								}
								int[] current_path = ga.getPath();
								gaf.getContentPane().setVisible(false);
								gaf.getContentPane().remove(gatspui);
								gatspui = new TSPUI(datas, current_path);
								gaf.getContentPane().add(gatspui);
								gaf.getContentPane().repaint();
								gaf.getContentPane().setVisible(true);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					});
					updateThread.start();
					ga.evolution();
					jb3.setEnabled(true);
					JOptionPane.showConfirmDialog(null, "GA", "完成演绎", JOptionPane.CANCEL_OPTION);
				}
				
			});
			gaThread.start();
		}
	}
}