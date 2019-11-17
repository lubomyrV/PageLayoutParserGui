import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class MainFrame extends JFrame{
	
	
	private TextPanel textPanel;
	private Toolbar toolbar;
	private FormPanel formPanel;
	
	public MainFrame() {
		super("Page layout parser");
		setLayout(new BorderLayout());
		
		
		toolbar = new Toolbar();
		
		textPanel = new TextPanel();
		formPanel = new FormPanel();
		
		toolbar.setStringListener(new StringListener() {
			public void textEmitted(String text) {
				textPanel.appendText(text);
			}
		});
		
		formPanel.setFormListener(new FormListener() {
			public void formEventOccurred(FormEvent event) {
				String source = event.getSource();
				String destination = event.getDestination();

				try {
					Map<String, List<String>> sectionByFields = doParsing(source, destination);
					textPanel.appendText("\n");
					textPanel.appendText("Source: "+source + "\n");
					textPanel.appendText("Destination: "+ destination + "\n");
					textPanel.appendText("---\n");
					for (String key : sectionByFields.keySet()) {
						List<String> fields = sectionByFields.get(key);
						textPanel.appendText(key + " : "+ fields + "\n");
						textPanel.appendText("\n");
					}
					textPanel.appendText("---\n");
				} catch (Exception e) {
					textPanel.appendText("Provide path, example:\n");
					textPanel.appendText("       Source: /home/user/Object__c-Name Layout.layout\n");
					textPanel.appendText("Destination: /home/user/myFiel.txt\n");
				}
			}
		});
		
		add(formPanel, BorderLayout.WEST);
		//add(toolbar, BorderLayout.NORTH);
		add(textPanel, BorderLayout.CENTER);
		
		setSize(800, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	private Map<String, List<String>> doParsing(String source, String destination){
		System.out.println("Parsing...");
		int startField = 0;
		int endField = 0;
		int startSection = 0;
		int endSection = 0;
		String section = "";
		String field = "";
		Map<String, List<String>> sectionByFields = new LinkedHashMap<>();
		StringBuilder strBuild = new StringBuilder();
		
		Reader fileReader = null;
		try {
			fileReader = new FileReader(source);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		System.out.println("src : "+source);
		int data = 0;
		try {
			data = fileReader.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
		while(data != -1) {
			try {
				data = fileReader.read();
			} catch (IOException e) {
				e.printStackTrace();
			}
			strBuild.append((char) data);
		}
		try {
			fileReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
				
		char[] charArr = new char[strBuild.length()];
		for (int i = 0; i < strBuild.length(); i++) {
			charArr[i] = strBuild.charAt(i);
		}
		
		for (int i = 0; i < charArr.length; i++) {
			//<label>
			if(i+6 < charArr.length && charArr[i] == '<' && charArr[i+1] == 'l' && charArr[i+2] == 'a' 
					&& charArr[i+3] == 'b' && charArr[i+4] == 'e' && charArr[i+5] == 'l' && charArr[i+6] == '>') {
				i += 6;
				startSection = i;
				if(field.length() > 0 ) {
					//System.out.println("add to map");
					List<String> sectionList = new ArrayList<String>();
					String[] sectionsArr = section.split(",");
					for (String sectionStr : sectionsArr) {
						sectionList.add(sectionStr);
					}
					//System.out.println("sectionList > "+sectionList);
					List<String> fieldList = new ArrayList<String>();
					String[] fieldsArr = field.split(",");
					for (String fieldStr : fieldsArr) {
						fieldList.add(fieldStr);
					}
					//System.out.println("fieldList > "+fieldList);
					sectionByFields.put(sectionList.get(0), fieldList);
					//System.out.println("sectionByFields > "+sectionByFields);
					section = "";
					field = "";
				}
				//System.out.println("startSection > "+startSection);
				continue;
			} else if(i+7 < charArr.length && charArr[i] == '<'&& charArr[i+1] == '/' && charArr[i+2] == 'l' 
					&& charArr[i+3] == 'a' && charArr[i+4] == 'b' && charArr[i+5] == 'e' && charArr[i+6] == 'l' && charArr[i+7] == '>') {
				i += 7;
				endSection = i;
				//System.out.println("endSection > "+endSection);
				section +=",";
				continue;
			} else if(startSection > endSection){
				section += charArr[i];
				//System.out.println("section > "+section+" | i = "+i);
			}
			
			//<field>
			if(i+6 < charArr.length && charArr[i] == '<' && charArr[i+1] == 'f' && charArr[i+2] == 'i' 
					&& charArr[i+3] == 'e' && charArr[i+4] == 'l' && charArr[i+5] == 'd' && charArr[i+6] == '>') {
				i += 6;
				startField = i;
				//System.out.println();
				continue;
			} else if(i+7 < charArr.length && charArr[i] == '<'&& charArr[i+1] == '/' && charArr[i+2] == 'f' 
					&& charArr[i+3] == 'i' && charArr[i+4] == 'e' && charArr[i+5] == 'l' && charArr[i+6] == 'd' && charArr[i+7] == '>') {
				i += 7;
				endField = i;
				field +=",";
				//System.out.println();
				continue;
			} else if(startField > endField){
				field += charArr[i];
				//System.out.println("field > "+field+" | endField = "+endField);
			}
		}
		
		if(field.length() > 0 ) {
			//System.out.println("add to map");
			List<String> sectionList = new ArrayList<String>();
			String[] sectionsArr = section.split(",");
			for (String sectionStr : sectionsArr) {
				sectionList.add(sectionStr);
			}
			//System.out.println("sectionList > "+sectionList);			
			List<String> fieldList = new ArrayList<String>();
			String[] fieldsArr = field.split(",");
			for (String fieldStr : fieldsArr) {
				fieldList.add(fieldStr);
			}
			//System.out.println("fieldList > "+fieldList);
			sectionByFields.put(sectionList.get(0), fieldList);
			//System.out.println("sectionByFields > "+sectionByFields);
		}
		
		//System.out.println("sectionByFields > "+sectionByFields);
		if(destination.length() > 0) {
			
			System.out.println();
			Writer fileWriter = null;
			try {
				fileWriter = new FileWriter(destination);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			System.out.println("des : "+destination);
			System.out.println();
			//System.out.println("Result:");
			//System.out.println();
			for (String key : sectionByFields.keySet()) {
				List<String> fields = sectionByFields.get(key);
				try {
					fileWriter.write(key +" : "+fields+"\n\n");
				} catch (IOException e) {
					e.printStackTrace();
				}	
				//System.out.println(key +" : "+fields+"\n");
			}
			try {
				fileWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		System.out.println("done!");
		System.out.println();
		return sectionByFields;
	}
	
}
