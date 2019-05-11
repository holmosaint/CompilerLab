package minijava2spiglet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import minijava.symbol.MClass;
import minijava.symbol.MMethod;
import minijava.symbol.SymbolTable;

public class minijava2spiglet {
	
	private static File outputFile_;
	private static OutputStream spigletFile_;
	private static int tempIndex = 20; // 暴力搜索最小的没有被使用的TEMP值
	private static int labelIndex = 0;
	public static final String TEMP = "TEMP ";
	
	// 按照每个方法生成代码
	public minijava2spiglet(String file_name) {
		file_name = file_name.replaceAll(".java", ".spg");
		System.out.println("Generating spiglet codes to " + file_name);
		outputFile_ = new File(file_name);
		if(!outputFile_.exists()) {
			try {
				outputFile_.createNewFile();
			}catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			spigletFile_ = new FileOutputStream(outputFile_);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		generateSpigletCode();
	}
	
	public static void generateSpigletCode() {
		String code = "";
		/*for(MClass c : SymbolTable.getClassList()) {
			c.createView();
		}*/
		for(MClass c : SymbolTable.getClassList()) {
			for(String m : c.getMethod().keySet()) {
				MMethod method = c.queryMethod(m);
				method.generateSpigletMethodCode();
				// writeCode(code);
			}
			code = c.generateSpigletNewClassCode();
			writeCode(code);
		}
	}
	
	public static void writeCode(String code) {
		try {
			spigletFile_.write(code.getBytes());
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