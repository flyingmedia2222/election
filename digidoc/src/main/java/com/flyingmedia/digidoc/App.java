package com.flyingmedia.digidoc;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class App {

	public static void main(String[] args) throws IOException, DocumentException, URISyntaxException {
		String imFile = "C:\\elections\\logo.jpg";
		Image img = Image.getInstance(imFile);
		String temploc = "C:\\elections\\template.txt", op = "", content, data, orig;
		int cnt = 1;
		ReportGen.init();
		List<Map<String, String>> ds = ReportGen.populateDs();
		BufferedReader bis = new BufferedReader(new FileReader(temploc));
		while ((data = bis.readLine()) != null)
			op += data + "\n";
		orig = op;
		for (Map<String, String> item : ds) {
			op = orig;
			if (op.contains("<table>")) {
				String temp = op.substring(op.indexOf("<table>") + 7, op.indexOf("</table>"));
				temp = formatTab(temp.trim(), item);
				temp = "<t>" + temp + "</t>";
				op = op.replace(op.substring(op.indexOf("<table>"), op.indexOf("</table>")), temp);
				op = op.replace("</table>", "");
			}
			content = "";
			String[] lines = op.split("\n");
			for (String line : lines) {
				if (line.contains("${")) {
					line = line.replaceAll("\\s+", " ");
					String[] words = line.split(" ");
					for (int i = 0; i < words.length; i++) {
						if (words[i].contains("${"))
							words[i] = item.get(words[i].substring(2, words[i].length() - 1));
						content += words[i] + " ";
					}
					content += "\n";

				} else
					content += line + "\n";
			}
			//mobile logic
			String mob=item.get("MOBILE_NO");
			if(mob.startsWith("+91"))
				mob=mob.substring(3);
			Pattern p=Pattern.compile("[0-9]{10}");
			if(p.matcher(mob).matches())
				genPdf(content, mob + ".pdf", img);
		}
	}

	private static void genPdf(String data, String fileName, Image obj)
			throws DocumentException, URISyntaxException, MalformedURLException, IOException {
		Document document = new Document();
		PdfWriter.getInstance(document, new FileOutputStream("C:\\elections\\docs\\" + fileName));
		document.open();
		Font font = FontFactory.getFont(FontFactory.COURIER, 10, BaseColor.BLACK);
		String[] lines = data.split("\n");
		PdfPTable table = new PdfPTable(6);
		for (String item : lines) {
			Paragraph p = null;
			if (item == ""||item.length()==0)
				p = new Paragraph("\n", font);
			else if (item.contains("<t>")) {
				item = item.replace("<t>", "");
				item = item.replaceAll("\\s+", " ");
				String[] arr = item.split(" ");

				addTableHeader(table, arr);
				continue;
			} else if (item.contains("</t>")) {

				item = item.replace("</t>", "");
				item = item.replaceAll("\\s+", " ");
				String[] arr = item.split("&");
				addRows(table, arr);
				document.add(table);
				continue;
			} else
				p = new Paragraph(item, font);
			document.add(p);
		}
		document.add(obj);

		document.close();
	}

	private static void addTableHeader(PdfPTable table, String[] arr) {
		Stream.of(arr).forEach(columnTitle -> {
			PdfPCell header = new PdfPCell();
			header.setBackgroundColor(BaseColor.PINK);
			header.setBorderWidth(1);
			header.setPhrase(new Phrase(columnTitle, FontFactory.getFont(FontFactory.COURIER, 10, BaseColor.BLACK)));
			header.setHorizontalAlignment(Element.ALIGN_LEFT);
			table.addCell(header);
		});
	}

	private static void addRows(PdfPTable table, String[] arr) {
		for (String item : arr) {
			PdfPCell cell = new PdfPCell();
			cell.setPhrase(new Phrase(item, FontFactory.getFont(FontFactory.COURIER, 8, BaseColor.BLACK)));
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			table.addCell(cell);
		}
	}

	private static String formatTab(String ip, Map<String, String> map) {
		String op1 = "", op2 = "";
		String[] lines = ip.split("\n");
		lines[0] = lines[0].replaceAll("\\s+", " ");
		lines[1] = lines[1].replaceAll("\\s+", " ");
		List<String> header = Arrays.asList(lines[0].split(" "));
		List<String> data = Arrays.asList(lines[1].split(" "));
		int diff;
		for (int i = 0; i < data.size(); i++)
			data.set(i, map.get(data.get(i).substring(2, data.get(i).length() - 1)));
		for (int i = 0; i < header.size(); i++) {
			if (header.get(i).length() < data.get(i).length()) {
				diff = data.get(i).length() - header.get(i).length();
				String var = header.get(i);
				for (int j = 0; j < diff; j++)
					var += " ";
				header.set(i, var);
			} else if (header.get(i).length() > data.get(i).length()) {
				diff = header.get(i).length() - data.get(i).length();
				String var = data.get(i);
				for (int j = 0; j < diff; j++)
					var += " ";
				data.set(i, var);
			}
			op1 += header.get(i) + "    ";
			op2 += data.get(i) + "&";
		}
		op1 = op1 + "\n" + op2;

		return op1;
	}
}

