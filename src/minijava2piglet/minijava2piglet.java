package minijava2piglet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.FileOutputStream;
import minijava.symbol.*;

public class minijava2piglet {
	
	private static File outputFile_;
	private static OutputStream pigletFile_;
	private static int tempIndex = 20; // 暴力搜索最小的没有被使用的TEMP值
	private static int labelIndex = 0;
	public static final String TEMP = "TEMP ";
	
	// 按照每个方法生成代码
	public minijava2piglet(String file_name) {
		file_name = file_name.replaceAll(".java", ".pg");
		System.out.println("Outputting to " + file_name);
		outputFile_ = new File(file_name);
		if(!outputFile_.exists()) {
			try {
				outputFile_.createNewFile();
			}catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			pigletFile_ = new FileOutputStream(outputFile_);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		generatePigletCode();
	}
	
	public static void generatePigletCode() {
		String code = "";
		for(MClass c : SymbolTable.getClassList()) {
			c.createView();
		}
		for(MClass c : SymbolTable.getClassList()) {
			for(String m : c.getMethod().keySet()) {
				MMethod method = c.queryMethod(m);
				code = method.generatePigletMethodCode();
				// writeCode(code);
			}
			code = c.generatePigletNewClassCode();
			writeCode(code);
		}
	}
	
	public static void writeCode(String code) {
		try {
			pigletFile_.write(code.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static int getTempIndex() {
		return tempIndex++;
	}
	
	public static int getLabelIndex() {
		return labelIndex++;
	}
}