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
	private int tempIndex = 20; // ����������С��û�б�ʹ�õ�TEMPֵ
	private String TEMP = "TEMP ";
	
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
		for(MClass c : SymbolTable.getClassList()) {
			for(String m : c.getMethod().keySet()) {
				MMethod method = c.queryMethod(m);
				generatePigletMethodCode(method);
			}
		}
	}
	
	public void generatePigletMethodCode(MMethod method) {
		MClass c = method.getOwner();
		String code = c.getName() + "_" + method.getName();
		int parameterLength = method.getParams().keySet().size();
		++parameterLength; // ��һ��������VTable
		code += " [" + parameterLength + "]\n";
		
		String returnTemp = null;
		if(!c.isMainClass())
			returnTemp = TEMP + tempIndex++;
		
		// ����TEMP�������ֲ�����
		for(String var_name : method.getVarMap().keySet()) {
			MVar var = method.queryVar(var_name);
			var.setTempID(tempIndex++);
		}
		
		int tab = 1; // tab������
		for(MBlock block : method.getBlockList()) {
			
		}
		
		if(!c.isMainClass())
			code += "RETURN " + returnTemp + "\n";
		
		code += "END\n";
		try {
			pigletFile_.write(code.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}