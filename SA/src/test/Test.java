package test;

import algorithm.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import algorithm.*;


public class Test {
	public static void main(String[] args) {
		// 获取文件路径参数
		if(args.length < 0 || args.length > 1) {
			System.out.println("Args Invalid!");
			return;
		}
		String filePath = (args.length==1)?args[0]:"Qatar.txt";
		// 城市抽象的点集
		List<City> datas = new ArrayList<>();
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
			System.out.println("-------------SA Solution----------");
			SA sa = new SA(datas);
			sa.search();
			System.out.println("-------------LS Solution----------");
			//LS ls = new LS(datas);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	
}
