package com.flyingmedia.digidoc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.formula.atp.Switch;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ReportGen {

	private static XSSFWorkbook workbook;
	private static XSSFSheet spreadsheet;
	private static XSSFRow row,headrow;
	private static int rowid;
	private static FileInputStream fis = null;

	public static void init() {
		try {
			fis = new FileInputStream("C:\\elections\\voters.xlsx");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			workbook = new XSSFWorkbook(fis);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		spreadsheet = workbook.getSheetAt(0);
		// spreadsheet = workbook.createSheet("Report");
		// row = spreadsheet.createRow(rowid++);
		/*
		 * row.createCell(0, CellType.STRING).setCellValue("Report Date");
		 * row.createCell(1, CellType.STRING).setCellValue("OLC Id"); row.createCell(2,
		 * CellType.STRING).setCellValue("SwitchPoints Status"); row.createCell(3,
		 * CellType.STRING).setCellValue("Energy Yesterday"); row.createCell(4,
		 * CellType.STRING).setCellValue("Energy Day Before Yest."); row.createCell(5,
		 * CellType.STRING).setCellValue("Energy Status"); row.createCell(6,
		 * CellType.STRING).setCellValue("Test Status");
		 */
	}
	
	ReportGen(String fName) {
		try {
			fis = new FileInputStream("C:\\elections\\"+fName+".xlsx");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			workbook = new XSSFWorkbook(fis);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		spreadsheet = workbook.getSheetAt(0);
		// spreadsheet = workbook.createSheet("Report");
		// row = spreadsheet.createRow(rowid++);
		/*
		 * row.createCell(0, CellType.STRING).setCellValue("Report Date");
		 * row.createCell(1, CellType.STRING).setCellValue("OLC Id"); row.createCell(2,
		 * CellType.STRING).setCellValue("SwitchPoints Status"); row.createCell(3,
		 * CellType.STRING).setCellValue("Energy Yesterday"); row.createCell(4,
		 * CellType.STRING).setCellValue("Energy Day Before Yest."); row.createCell(5,
		 * CellType.STRING).setCellValue("Energy Status"); row.createCell(6,
		 * CellType.STRING).setCellValue("Test Status");
		 */
	}


	public static List<Map<String,String>> populateDs() throws IOException {
		List<Map<String,String>> ds=new ArrayList<Map<String,String>>();
		int rcnt,ccnt=0;
		rcnt = spreadsheet.getLastRowNum();
		headrow=spreadsheet.getRow(0);
		ccnt=headrow.getLastCellNum();
		for(int i=1;i<rcnt;i++)
		{
			Map<String,String> map= new HashMap<String, String>();
			row=spreadsheet.getRow(i);
			for(int j=0;j<ccnt;j++)
			{
				
				String key=headrow.getCell(j).getStringCellValue();
				String value="";
				switch (row.getCell(j).getCellType()) {
				case STRING:
					value=row.getCell(j).getStringCellValue();
					break;
				case NUMERIC:
					value=String.valueOf((int)row.getCell(j).getNumericCellValue());
					break;
				case FORMULA:
					value=row.getCell(j).getStringCellValue();
					break;
				}
				map.put(key, value);
			}
			ds.add(map);
		}
		fis.close();
		return ds;
		
	}



}