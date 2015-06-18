package test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class WebsiteModel {
	public  ArrayList<Website> websites=new ArrayList<Website>();
	
	public class Website{
		public String siteName;
		public String siteUrl;
		public String state;
		public String charset;
		public String identifyTag;
		public String contentTag;
	}
	
	public WebsiteModel(){
		String modelFile;
		try {
			modelFile = readFileStr("./WebsiteModel.csv");//-test
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("WebsiteModel.csv can't be read!");
			return ;
		}
//		System.out.println(modelFile);
		String[] lines=modelFile.split("\n");
		for(int i=1;i<lines.length;++i){
			Website wb=new Website();
			String[] items= lines[i].split(",");
			items[4]=processQuotes(items[4]);
			items[5]=processQuotes(items[5]);
			wb.siteName=items[0];
			wb.siteUrl=items[1];
			wb.state=items[2];
			wb.charset=items[3];
			wb.identifyTag=items[4];
			wb.contentTag=items[5];
			websites.add(wb);
//			System.out.println(items[3]);
		}
//		System.out.println(websites.size());
	}
	private String processQuotes(String item){
		if(item.startsWith("\"")) item=item.substring(1);
		if(item.endsWith("\"")) item=item.substring(0, item.length()-1);
		item=item.replace("\"\"", "\"");
		return item;
	}

	private String readFileStr(String filePath) throws IOException{
		StringBuffer fileStr=new StringBuffer(); 
		InputStreamReader isr = new InputStreamReader(new FileInputStream(filePath), "gb2312");
	   	BufferedReader inBufRd = new BufferedReader(isr);
	   	String lineStr="";
	   	while((lineStr=inBufRd.readLine())!=null){
	   		fileStr.append(lineStr+"\n");
	   	}
	   	inBufRd.close();
	   	isr.close();			
		return fileStr.toString();
	}
	
	public static void main(String args[]) throws IOException{
		WebsiteModel wm=new WebsiteModel();
	}  

}
