package test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Integration {
	private double SimilarityThresholdValue=0.8;
	private ArrayList<Search> searchList;
	ArrayList<NewsPage> insideNewsPageList=new ArrayList<NewsPage>();
	ArrayList<NewsPage> outsideNewsPageList=new ArrayList<NewsPage>();
	ArrayList<NewsPage> specialOutsideNewsPageList=new ArrayList<NewsPage>();
	String insideStatistics="";
	String outsideStatistics="";
	String specialOutsideStatistics="";
	
	Integration(ArrayList<Search> searchList) throws IOException{
		this.searchList=searchList;
		searchList2newsPageList(searchList);
		getSpecialOutsideNewsPageList();
		outputFinalResult();
	}

	private void outputFinalResult() throws IOException {
		String outputFolder="./WebResult/"+searchList.get(0).topic;
		String insideNews=outputFolder+"/InsideNews.txt";
		String outsideNews=outputFolder+"/OutsideNews.txt";
		String specialNews=outputFolder+"/SpecialNews.txt";
		String insideStatisticsFile=outputFolder+"/InsideStatistics.txt";
		String outsideStatisticsFile=outputFolder+"/OutsideStatistics.txt";
		String specialOutsideStatisticsFile=outputFolder+"/SpecialOutsideStatistics.txt";
		
		File file=new File(outputFolder);
		file.mkdirs();
		pageList2File(insideNewsPageList,insideNews);
		pageList2File(outsideNewsPageList,outsideNews);
		pageList2File(specialOutsideNewsPageList,specialNews);
		
		statistics2File(insideStatistics,insideStatisticsFile);
		statistics2File(outsideStatistics,outsideStatisticsFile);
		statistics2File(specialOutsideStatistics,specialOutsideStatisticsFile);
	}

	private void statistics2File(String statistics,String filePath) throws IOException {
		// TODO Auto-generated method stub
		FileWriter out=new FileWriter(filePath);
		out.write(statistics);
		out.close();
	}

	private void pageList2File(ArrayList<NewsPage> newsPageList, String newsPath) throws IOException {
		// TODO Auto-generated method stub
		String fileStr="";
		for(NewsPage newsPage:newsPageList){
			fileStr+=newsPage.title+"\r\n"+newsPage.site.siteName+"\r\n"+newsPage.mainContent+"\r\n\r\n\r\n";
		}
		
		FileWriter out=new FileWriter(newsPath);
		out.write(fileStr);
		out.close();
	}

	private void getSpecialOutsideNewsPageList() {
		for(NewsPage outsideNewsPage:outsideNewsPageList){
			double maxSimilarity=0;
			for(NewsPage insideNewsPage:insideNewsPageList){
				double similarity=CosineSimilarAlgorithm.getSimilarity(outsideNewsPage.mainContent, insideNewsPage.mainContent);
				if(similarity>maxSimilarity) maxSimilarity=similarity;
				if(maxSimilarity>SimilarityThresholdValue) break;
			}
			if(maxSimilarity<SimilarityThresholdValue){
				specialOutsideNewsPageList.add(outsideNewsPage);
				specialOutsideStatistics+=outsideNewsPage.title+"\t"+outsideNewsPage.site.siteName+"\r\n";
			}	
		}
	}

	private void searchList2newsPageList(ArrayList<Search> searchList) {
		int searchListSize=searchList.size();
		for(int i=0;i<searchListSize;++i){
			Search search= searchList.get(i);
			if(search.site.state.contains("inside")){
				int newsPageListSize=search.newsPageList.size();
				insideStatistics+=search.site.siteName+":"+newsPageListSize+"\r\n";
				for(int j=0;j<newsPageListSize;++j){
					insideNewsPageList.add(search.newsPageList.get(j));
				}
			}else{
				int newsPageListSize=search.newsPageList.size();
				outsideStatistics+=search.site.siteName+":"+newsPageListSize+"\r\n";
				for(int j=0;j<newsPageListSize;++j){
					outsideNewsPageList.add(search.newsPageList.get(j));
				}
			}
		}
		return ;
	}

}
