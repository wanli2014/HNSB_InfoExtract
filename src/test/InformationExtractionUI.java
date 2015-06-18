/*
 * InformationExtractionUI.java
 *
 * Created on __DATE__, __TIME__
 */

package test;

import java.awt.Cursor;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import test.GooglePage.NewsItem;
import test.WebsiteModel.Website;

/**
 *
 * @author  __USER__
 */
public class InformationExtractionUI extends javax.swing.JFrame implements
		ActionListener, PropertyChangeListener {

	private Task task;

	class Task extends SwingWorker<Void, Void> {
		/*
		 * Main task. Executed in background thread.
		 */
		@Override
		public Void doInBackground() {
			//Initialize progress property.
			setProgress(0);
			int progress = 0;
			try {
				int day = Integer.parseInt(jTextField1.getText());
				int requestNewsNum = Integer.parseInt(jTextField2.getText());
				String[] topicArray = jTextArea1.getText().split("\n");

				WebsiteModel webModel = new WebsiteModel();
				for (int i = 0; i < topicArray.length; ++i) {
					String topic = topicArray[i]; //Every  topic
					ArrayList<Search> searchList = new ArrayList<Search>();
					int webNum = webModel.websites.size();
					for (int j = 0; j < webNum; ++j) {
						Website site = webModel.websites.get(j); //Every  website
						String encodedTopic = URLEncoder.encode(topic, "utf-8");
						String googleUrl = "https://www.google.com.hk/search?tbs=qdr:d"+ day
								+ "&num="+ requestNewsNum+"&q="+ encodedTopic
								+ "site:" + site.siteUrl;
						GooglePage googlePage = new GooglePage(googleUrl);

						jTextArea2.append("正在google： " + topic + " ---> "
								+ site.siteName + "\r\n");

						int newsNum = googlePage.newsItems.size();
						ArrayList<NewsPage> newsPageList = new ArrayList<NewsPage>();
						for (int k = 0; k < newsNum; ++k) {
							NewsItem newsItem = googlePage.newsItems.get(k); //Every  news
							NewsPage newspage = new NewsPage(newsItem, site);
							if (newspage.mainContent!= null&&newspage.mainContent.trim().length()>0)
								newsPageList.add(newspage);

							jTextArea2.append((k+1)+"/"+newsNum+": "+newspage.title+"\r\n");
							jTextArea2.selectAll();
							progress = 100*(i*webNum+j*requestNewsNum+k+1)
									/(webNum*topicArray.length*requestNewsNum);
							setProgress(progress);
						}
						Search search = new Search(topic, day, site,
								googlePage, newsPageList);
						searchList.add(search);
					}
					setProgress(100);
					jTextArea2.append("正在整理搜索结果....\r\n");
					Integration integration = new Integration(searchList);
					jTextArea2.append("工作完成！！！\r\n");
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			return null;
		}

		/*
		 * Executed in event dispatching thread
		 */
		@Override
		public void done() {
			Toolkit.getDefaultToolkit().beep();
			jButton1.setEnabled(true);
			setCursor(null); //turn off the wait cursor
			//			setProgress(0);
		}
	}

	/** Creates new form InformationExtractionUI */
	public InformationExtractionUI() {
		initComponents();
	}

	//GEN-BEGIN:initComponents
	// <editor-fold defaultstate="collapsed" desc="Generated Code">
	private void initComponents() {

		jLabel1 = new javax.swing.JLabel();
		jLabel2 = new javax.swing.JLabel();
		jTextField1 = new javax.swing.JTextField();
		jLabel3 = new javax.swing.JLabel();
		jScrollPane1 = new javax.swing.JScrollPane();
		jTextArea1 = new javax.swing.JTextArea();
		jButton1 = new javax.swing.JButton();
		jProgressBar1 = new javax.swing.JProgressBar();
		jScrollPane2 = new javax.swing.JScrollPane();
		jTextArea2 = new javax.swing.JTextArea();
		jButton2 = new javax.swing.JButton();
		jLabel4 = new javax.swing.JLabel();
		jTextField2 = new javax.swing.JTextField();

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
		setName("");
		setResizable(false);

		jLabel1.setFont(new java.awt.Font("微软雅黑", 0, 18));
		jLabel1
				.setText("\u7f51\u7edc\u4fe1\u606f\u641c\u96c6\u7cfb\u7edfBeta\u6d4b\u8bd5\u7248");

		jLabel2.setText("\u8bf7\u8f93\u5165\u65f6\u95f4(day)\uff1a");

		jTextField1.setText("9");

		jLabel3.setText("\u8bf7\u8f93\u5165\u4e3b\u9898\uff1a");

		jTextArea1.setColumns(20);
		jTextArea1.setRows(5);
		jScrollPane1.setViewportView(jTextArea1);

		jButton1.setText("\u641c\u7d22");
		jButton1.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				InformationExtractionUI.this.actionPerformed(evt);
			}
		});

		jTextArea2.setColumns(20);
		jTextArea2.setRows(5);
		jScrollPane2.setViewportView(jTextArea2);

		jButton2.setText("\u67e5\u770b\u7ed3\u679c");
		jButton2.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				jButton2MouseClicked(evt);
			}
		});

		jLabel4.setText("\u65b0\u95fb\u6761\u76ee\uff1a");

		jTextField2.setText("10");

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(
				getContentPane());
		getContentPane().setLayout(layout);
		layout
				.setHorizontalGroup(layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								layout
										.createSequentialGroup()
										.addGap(91, 91, 91)
										.addGroup(
												layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(
																jScrollPane1,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																448,
																Short.MAX_VALUE)
														.addComponent(jLabel3)
														.addGroup(
																layout
																		.createSequentialGroup()
																		.addComponent(
																				jLabel2)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(
																				jTextField1,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				77,
																				javax.swing.GroupLayout.PREFERRED_SIZE)
																		.addGap(
																				26,
																				26,
																				26)
																		.addComponent(
																				jLabel4)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(
																				jTextField2,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				62,
																				javax.swing.GroupLayout.PREFERRED_SIZE))
														.addGroup(
																layout
																		.createSequentialGroup()
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addGroup(
																				layout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.LEADING)
																						.addComponent(
																								jScrollPane2,
																								javax.swing.GroupLayout.DEFAULT_SIZE,
																								448,
																								Short.MAX_VALUE)
																						.addGroup(
																								layout
																										.createSequentialGroup()
																										.addComponent(
																												jButton1)
																										.addPreferredGap(
																												javax.swing.LayoutStyle.ComponentPlacement.RELATED,
																												javax.swing.GroupLayout.DEFAULT_SIZE,
																												Short.MAX_VALUE)
																										.addComponent(
																												jButton2,
																												javax.swing.GroupLayout.PREFERRED_SIZE,
																												90,
																												javax.swing.GroupLayout.PREFERRED_SIZE)
																										.addGap(
																												26,
																												26,
																												26)
																										.addComponent(
																												jProgressBar1,
																												javax.swing.GroupLayout.PREFERRED_SIZE,
																												237,
																												javax.swing.GroupLayout.PREFERRED_SIZE)
																										.addGap(
																												31,
																												31,
																												31)))))
										.addGap(83, 83, 83)).addGroup(
								javax.swing.GroupLayout.Alignment.TRAILING,
								layout.createSequentialGroup().addGap(176, 176,
										176).addComponent(jLabel1,
										javax.swing.GroupLayout.DEFAULT_SIZE,
										259, Short.MAX_VALUE).addGap(187, 187,
										187)));
		layout
				.setVerticalGroup(layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								layout
										.createSequentialGroup()
										.addContainerGap()
										.addComponent(
												jLabel1,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												33,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(jLabel2)
														.addComponent(
																jTextField1,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(jLabel4)
														.addComponent(
																jTextField2,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(jLabel3)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addComponent(
												jScrollPane1,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												114,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addGap(26, 26, 26)
										.addGroup(
												layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addGroup(
																layout
																		.createParallelGroup(
																				javax.swing.GroupLayout.Alignment.BASELINE)
																		.addComponent(
																				jButton1)
																		.addComponent(
																				jButton2))
														.addComponent(
																jProgressBar1,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																24,
																javax.swing.GroupLayout.PREFERRED_SIZE))
										.addGap(27, 27, 27)
										.addComponent(
												jScrollPane2,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												139,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addContainerGap(48, Short.MAX_VALUE)));

		pack();
	}// </editor-fold>
	//GEN-END:initComponents

	private void jButton2MouseClicked(java.awt.event.MouseEvent evt) {
		File anchorfile=new File("./WebResult/anchor.txt");
		String resultPath=anchorfile.getParent();
		try {
			Runtime.getRuntime().exec("cmd /c start "+resultPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void actionPerformed(java.awt.event.ActionEvent evt) {
		jButton1.setEnabled(false);
		jProgressBar1.setStringPainted(true);
		jProgressBar1.setValue(0);
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		task = new Task();
		task.addPropertyChangeListener(this);
		task.execute();
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if ("progress" == evt.getPropertyName()) {
			int progress = (Integer) evt.getNewValue();
			jProgressBar1.setValue(progress);
		}
	}

	/**
	 * @param args the command line arguments
	 */
	public static void main(String args[]) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				String deadline="2015-07-03";
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
				Date deadDate = null;
				try {
					deadDate = df.parse(deadline);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Date nowDate=new Date();
				if(nowDate.before(deadDate))
					new InformationExtractionUI().setVisible(true);
//				else JOptionPane.showMessageDialog(null, "版本已过测试期，请联系开发人员！", "警告！", JOptionPane.ERROR_MESSAGE);
			}
		});
	}

	//GEN-BEGIN:variables
	// Variables declaration - do not modify
	private javax.swing.JButton jButton1;
	private javax.swing.JButton jButton2;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JLabel jLabel3;
	private javax.swing.JLabel jLabel4;
	private javax.swing.JProgressBar jProgressBar1;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JScrollPane jScrollPane2;
	private javax.swing.JTextArea jTextArea1;
	private javax.swing.JTextArea jTextArea2;
	private javax.swing.JTextField jTextField1;
	private javax.swing.JTextField jTextField2;
	// End of variables declaration//GEN-END:variables

}