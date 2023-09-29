package com.stephenson.spider;
import java.io.IOException;

public class Spider {
	
	final String WEBSITE = "https://smt-stage.qa.siliconmtn.com/";
	
	/**
	 * Instantiate the spider and crawl, executing all functionality of the applicattion
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		
		Spider shelob = new Spider();
		shelob.crawl();
		
	}
	
	/**
	 * Instantiate a new SocketManagerAndReaderWriter and invoke downloadHTML with both a GET and POST
	 * @throws IOException
	 */
	public void crawl() throws IOException {
		DataReaderWriter drw = new DataReaderWriter(WEBSITE);
		drw.downloadHTML("GET ");
		drw.downloadHTML("POST ");
	}
}
