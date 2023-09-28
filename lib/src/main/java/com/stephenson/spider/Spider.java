package com.stephenson.spider;
import java.io.IOException;
import java.util.List;

public class Spider {
	
	final String WEBSITE = "https://smt-stage.qa.siliconmtn.com/";
	
	public static void main(String[] args) throws IOException {
		
		Spider shelob = new Spider();
		shelob.crawl();
		
	}
	
	public void crawl() throws IOException {
		SocketManagerAndReaderWriter socketManager = new SocketManagerAndReaderWriter(WEBSITE);
		socketManager.downloadHTML("GET ");
		socketManager.downloadHTML("POST ");
	}
}
