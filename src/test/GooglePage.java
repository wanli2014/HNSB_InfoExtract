package test;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.lexer.Lexer;
import org.htmlparser.lexer.Page;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.DefaultParserFeedback;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.SimpleNodeIterator;

public class GooglePage {
	public String url;
	public String html;
	public ArrayList<NewsItem> newsItems;
	
	public static class NewsItem{
		public String newsUrl;
//		public String newsTitle;
	};
	
	GooglePage(String url) throws Exception{
		this.url=url;
		int tryTimes=0;
		while(html==null&&tryTimes<10){
			try{
				html=preProcess(getHtmlstr(url));
			}catch(Exception e){
				System.out.println("Exception happens in GooglePage.getHtmlStr！"+tryTimes);
				e.printStackTrace();
				Thread.sleep(3000);
			}
			++tryTimes;				
			if(tryTimes==10) html=url;
		}
		System.out.println("GooglePage.getHtmlstr########"+html);
		newsItems=getNewsUrl(html);
	}

	private ArrayList<NewsItem> getNewsUrl(String html) throws Exception {
		ArrayList<NewsItem> newsList=new ArrayList<NewsItem>();
		Lexer mLexer = new Lexer(new Page(html));
        Parser parser = new Parser(mLexer, new DefaultParserFeedback(DefaultParserFeedback.QUIET));
     // 过滤 <frame >标签的 filter，用来提取 frame 标签里的 src 属性所表示的链接
		NodeFilter h3_Filter = new NodeFilter() {
			public boolean accept(Node node) {
				if (node.getText().startsWith("h3")) {
//					System.out.println(node.getText());
					return true;
				} else {
					return false;
				}
			}
		};
		
		NodeList list = parser.extractAllNodesThatMatch(h3_Filter);
		SimpleNodeIterator iterator = list.elements();
		while (iterator.hasMoreNodes()) {
			Node h3_Node = iterator.nextNode();
			Node h3_child= h3_Node.getChildren().elementAt(0);
			if(h3_child.toHtml().contains("[PDF]"))	continue;
			LinkTag link = (LinkTag) h3_child;
			String linkUrl = preProcessLink(link.getLink());//url
			String text = link.getLinkText();//链接文字
			if(linkUrl.equals("")||linkUrl.endsWith(".pdf")||linkUrl.endsWith(".PDF")) continue;
			System.out.println(linkUrl + " ********** " + text);
			
			NewsItem ni=new NewsItem();
			ni.newsUrl=linkUrl;
//			ni.newsTitle=text;
			newsList.add(ni);
		}
		return newsList;	
	}

	private String preProcessLink(String url) throws Exception{
		url=URLDecoder.decode(url,"utf-8");
		url=URLDecoder.decode(url,"utf-8");
		if(!url.contains("http://")) return "";
		if(url.contains("&amp;")){
//			System.out.println("preProcessLink ######## "+url);
			url=url.substring(0, url.indexOf("&amp;"));
			url=url.substring(url.lastIndexOf("http://"));
//			System.out.println(url);
		}
		
		return url;
	}
	
	private String preProcess(String htmlStr) {
		String regEx_script = "<script[^>]*?>[\\s\\S]*?<\\/script>"; // 定义script的正则表达式  
	    Pattern p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);  
        Matcher m_script = p_script.matcher(htmlStr);  
        htmlStr = m_script.replaceAll(""); // 过滤script标签
        
        String regEx_style = "<style[^>]*?>[\\s\\S]*?<\\/style>"; // 定义style的正则表达式
        Pattern p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);  
        Matcher m_style = p_style.matcher(htmlStr);  
        htmlStr = m_style.replaceAll(""); // 过滤style标签
        
        String regEx_z="<![^>]*>"; 
        Pattern p_z = Pattern.compile(regEx_z, Pattern.CASE_INSENSITIVE);  
        Matcher m_z = p_z.matcher(htmlStr);  
        htmlStr = m_z.replaceAll(""); // 过滤注释标签 <!--注释-->
//        System.out.println("preProcess########"+htmlStr);
        return htmlStr;
	}
	
	private String getHtmlstr(String urlstr) throws IOException{
		HttpsURLConnection conn = null;
		System.setProperty("java.protocol.handler.pkgs", "javax.net.ssl");
		System.setProperty("sun.net.client.defaultConnectTimeout", "30000");
		System.setProperty("sun.net.client.defaultReadTimeout", "30000");
		HostnameVerifier hv = new HostnameVerifier() {
			@Override
			public boolean verify(String hostname, SSLSession session) {
				return true;
			}
	    };
	    URL url = new URL(urlstr);
	    conn=(HttpsURLConnection)url.openConnection();
	    String [] user_agents = {"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:23.0) Gecko/20130406 Firefox/23.0", 
                "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:18.0) Gecko/20100101 Firefox/18.0",  
                "IBM WebExplorer /v0.94", "Galaxy/1.0 [en] (Mac OS X 10.5.6; U; en)", 
                "Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; WOW64; Trident/6.0)", 
                "Opera/9.80 (Windows NT 6.0) Presto/2.12.388 Version/12.14", 
                "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.0; Trident/5.0; TheWorld)"};
	    Random random=new Random();
	    int index=random.nextInt(6);
	    conn.setRequestProperty("User-agent",user_agents[index]);
	    conn.setHostnameVerifier(hv);
	    if(conn==null){
			System.out.println("conn is null");
		}
	    String htmlStr =convertInputStream2String(conn.getInputStream());
//	    System.out.println("getHtmlstr########"+htmlStr);
	    return htmlStr;
	}
	
	private String convertInputStream2String(InputStream is) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(is,"utf-8")); 
        String line="";
        String string="";
        while ((line = br.readLine()) != null) 	string = string + line;
		return string;		
	}
	
	public static void main(String args[]) throws Exception{
		String str=URLEncoder.encode("习近平会见国民党主席朱立伦", "utf-8");
		String url="https://www.google.com/search?tbs=qdr:d7&num=10&q="+str+"%20site:www.21ccom.net";//%E5%8D%9A%E9%B3%8C%E8%AE%BA%E5%9D%9B
		GooglePage gp=new GooglePage(url);
		
	}
}
