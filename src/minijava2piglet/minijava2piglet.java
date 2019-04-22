package minijava2piglet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.FileOutputStream;
import minijava.symbol.*;

public class minijava2piglet {
	
	private File outputFile_;
	private OutputStream pigletFile_;
	public static int tempIndex = 20; // ����������С��û�б�ʹ�õ�TEMPֵ
	public static final String TEMP = "TEMP ";
	
	// ����ÿ���������ɴ���
	public minijava2piglet(String file_name) {
		file_name = file_name.split(".")[0] + ".pg";
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
	
	public void generatePigletCode() {
		String code = "";
		for(MClass c : SymbolTable.getClassList()) {
			// TODO: ����new_classname����
			code = c.generatePigletNewClassCode();
			writeCode(code);
			for(String m : c.getMethod().keySet()) {
				MMethod method = c.queryMethod(m);
				code = method.generatePigletMethodCode();
				writeCode(code);
			}
		}
	}
	
	private void writeCode(String code) {
		try {
			pigletFile_.write(code.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}