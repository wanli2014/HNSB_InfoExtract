package test;

import java.util.ArrayList;
import test.WebsiteModel.Website;

public class Search {
	public String topic;
	private int day;
	public Website site;
	public GooglePage googlePage;
	public ArrayList<NewsPage> newsPageList=new ArrayList<NewsPage>();
	
	public Search(String topic,int day,Website site, GooglePage googlePage, ArrayList<NewsPage> newsPageList) {
		this.topic=topic;
		this.day=day;
		this.site=site;
		this.googlePage=googlePage;
		this.newsPageList=newsPageList;
	}
	
	
}
