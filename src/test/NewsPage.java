package test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.lexer.Lexer;
import org.htmlparser.lexer.Page;
import org.htmlparser.util.DefaultParserFeedback;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.htmlparser.util.SimpleNodeIterator;

import com.spreada.utils.chinese.ZHConverter;

import test.GooglePage.NewsItem;
import test.WebsiteModel.Website;

public class NewsPage {
	public String url;
	public String title;
	public Website site;
	public String html;
	public String mainContent;
	
	
	NewsPage(NewsItem newsItem, Website site) throws ParserException, IOException, InterruptedException{
		url=newsItem.newsUrl;
		this.site=site;
		int tryTimes=0;
		while(html==null&&tryTimes<10){
			try{
				html=preProcess(getHtmlstr(url,site));
			}catch(Exception e){
				System.out.println("Exception happens in NewsPage.getHtmlStr！"+tryTimes);
				e.printStackTrace();
				Thread.sleep(3000);
			}
			++tryTimes;				
			if(tryTimes==10) html=url;
		}
		System.out.println("NewsPage.html ######## "+html);
		if(html.contains(site.identifyTag)){
			title=getTitle(html);
			mainContent=postProcess(getContent(html,site));
		}
		System.out.println("NewsPage.mainContent ######## "+mainContent);
	}

	private String postProcess(String content) {
		String regEx_kg = " |\t"; // 定义空格、换行的正则表达式  
	    Pattern p_kg = Pattern.compile(regEx_kg, Pattern.CASE_INSENSITIVE);  
        Matcher m_kg = p_kg.matcher(content);  
        content = m_kg.replaceAll(""); // 过滤空格、换行        
        
        String regEx_h = "\r\n\r\n"; // 定义空格、换行的正则表达式  
	    Pattern p_h = Pattern.compile(regEx_h, Pattern.CASE_INSENSITIVE);  
        Matcher m_h = p_h.matcher(content);  
        content = m_h.replaceAll("\r\n"); // 过滤空格、换行        
        
        return content;
	}

	private String getContent(String html, final Website site) throws ParserException {
		Lexer mLexer = new Lexer(new Page(html));
        Parser parser = new Parser(mLexer, new DefaultParserFeedback(DefaultParserFeedback.QUIET));
		NodeFilter tag_Filter = new NodeFilter() {
			public boolean accept(Node node) {
				if (node.getText().contains(site.contentTag)) {//||node.getText().contains("Content")
//					System.out.println(node.getText());
					return true;
				} else {
					return false;
				}
			}
		};
		
		NodeList list = parser.extractAllNodesThatMatch(tag_Filter);
		if(list.size()==0)	System.out.println("contentTag isn't contained！");
		Node tag = list.elementAt(0);
		String content=processNodeList(tag);
		
		return content;
	}

	private String processNodeList(Node node) {
		String result="";
		//得到该节点的子节点列表
		NodeList childList = node.getChildren();
		//孩子节点为空，说明是值节点
		if (null == childList)
		{
			//得到值节点的值
			result = node.toPlainTextString();
//			System.out.println(result);
		} //end if
		//孩子节点不为空，继续迭代该孩子节点
		else 
		{
			SimpleNodeIterator iterator = childList.elements();
			while (iterator.hasMoreNodes()) {
				Node childNode = iterator.nextNode();
				result+=processNodeList(childNode);
			}
		}//end else
		return result;
	}

	private String getTitle(String html){
		int beginIndex=html.indexOf("<title>");
		int endIndex=html.indexOf("</title>");
		String title=html.substring(beginIndex+7, endIndex);
		
		String regEx_blank="  |\t"; 
	    Pattern p_blank = Pattern.compile(regEx_blank, Pattern.CASE_INSENSITIVE);  
	    Matcher m_blank = p_blank.matcher(title);  
	    title = m_blank.replaceAll(" "); // 处理标题中的空格
      
		return title;
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
        
//        String regEx_z="<!--.*-->"; 
//        Pattern p_z = Pattern.compile(regEx_z, Pattern.CASE_INSENSITIVE);  
//        Matcher m_z = p_z.matcher(htmlStr);  
//        htmlStr = m_z.replaceAll(""); // 过滤注释标签 <!---注释--->
        
        String regEx_zy="&[a-zA-Z]*;"; 
        Pattern p_zy = Pattern.compile(regEx_zy, Pattern.CASE_INSENSITIVE);  
        Matcher m_zy = p_zy.matcher(htmlStr);  
        htmlStr = m_zy.replaceAll(""); // 过滤html转义字符
        
        ZHConverter converter = ZHConverter.getInstance(ZHConverter.SIMPLIFIED);
		htmlStr = converter.convert(htmlStr);
        
        return htmlStr;
	}

	private String getHtmlstr(String urlstr,Website site) throws IOException{
		URL url = new URL(urlstr);
		System.setProperty("java.protocol.handler.pkgs", "javax.net.ssl");
		System.setProperty("sun.net.client.defaultConnectTimeout", "30000");
		System.setProperty("sun.net.client.defaultReadTimeout", "30000");
		HttpURLConnection conn =(HttpURLConnection)url.openConnection();
	    String [] user_agents = {"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:23.0) Gecko/20130406 Firefox/23.0", 
                "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:18.0) Gecko/20100101 Firefox/18.0",  
                "IBM WebExplorer /v0.94", "Galaxy/1.0 [en] (Mac OS X 10.5.6; U; en)", 
                "Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; WOW64; Trident/6.0)", 
                "Opera/9.80 (Windows NT 6.0) Presto/2.12.388 Version/12.14", 
                "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.0; Trident/5.0; TheWorld)"};
	    Random random=new Random();
	    int index=random.nextInt(6);
	    conn.setRequestProperty("User-agent",user_agents[index]);
	    if(conn==null){
			System.out.println("conn is null");
		}
	    String htmlStr =convertInputStream2String(conn.getInputStream(),site.charset);
//	    System.out.println("NewsPage.getHtmlstr########"+htmlStr);
	    return htmlStr;
	}
	
	private String convertInputStream2String(InputStream is,String charset) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(is,charset)); 
        String line="";
        String string="";
        while ((line = br.readLine()) != null) 	string = string + line;
		return string;	
	}

	public static void main(String args[]) throws ParserException, IOException, InterruptedException{
		String url="http://www.21ccom.net/articles/china/ggcx/20150504124298.html";
		
		NewsItem newsItem=new NewsItem();
		newsItem.newsUrl=url;
		
		WebsiteModel wb=new WebsiteModel();
		Website site=wb.websites.get(31);
		
		NewsPage np=new NewsPage(newsItem,site);
		
	}
}
