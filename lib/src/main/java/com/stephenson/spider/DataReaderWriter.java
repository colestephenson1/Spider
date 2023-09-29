package com.stephenson.spider;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import javax.net.ssl.SSLSocket;

public class DataReaderWriter {
	
	final String DIRECTORY = "/home/cole/SpiderFiles";
	final String ADMINURL = "https://smt-stage.qa.siliconmtn.com/sb/admintool";
	final String USERNAME = "cole.stephenson@siliconmtn.com";
	final String PASSWORD = "Revoffthetop1!";
	final String CACHESTATS = "https://smt-stage.qa.siliconmtn.com/sb/admintool?cPage=stats&actionId=FLUSH_CACHE";
	final String CACHESTATSPATH = "/sb/admintool?cPage=stats&actionId=FLUSH_CACHE";
	private String url;
	
	private StringBuilder cookies = new StringBuilder();
	
	public DataReaderWriter(String url) {
		this.url = url;
	}
	
	/**
	 * Method to open a stream, connect to the website with a get request,
	 * and read the data stream into a string builder
	 * @param socket
	 * @param convertedLink
	 * @return
	 * @throws IOException
	 */
	public StringBuilder makeRequestAndReadInStreamDataGET(SSLSocket socket, URL convertedLink) throws IOException {
			//Open an  output stream utilizing the socket output stream
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			//Create a reader utilizing the socket input stream
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			//Make a GET request using the URL path and host
			if(convertedLink.getPath().equals("/sb/admintool")) {
				out.writeBytes("GET " + CACHESTATSPATH + " HTTP/1.1\r\n");
			} else {				
				out.writeBytes("GET " + convertedLink.getPath() + " HTTP/1.1\r\n");
			}
			out.writeBytes("Host: " + convertedLink.getHost() + "\r\n");
			out.writeBytes("Cookie: " + cookies.toString() + "\r\n");
			out.writeBytes("Connection: close\r\n\r\n");
			out.writeBytes("\r\n");
			//Flush the stream
			out.flush();
			//Instantiate a string that represents each byte being read into the string builder
			String inData = null;
			//Instantiate the string builder to append stream data to
			StringBuilder builder = new StringBuilder();
			//Loop to append all the stream data into the builder
			while((inData = in.readLine()) != null) {
				builder.append(inData);
			}
			//Close the stream
			in.close();
			
			return builder;
	}
	
	public void makePOSTRequestAndSetCookies(SSLSocket socket, URL convertedLink) throws IOException {
		
		//Make post body to send in the request. Refer to admin page network request for formatting
		final String POSTBODY = "requestType=" + URLEncoder.encode("reqBuild", "UTF-8") +
				"&pmid=" + URLEncoder.encode("ADMIN_LOGIN", "UTF-8") + 
				"&emailAddress=" + URLEncoder.encode(USERNAME, "UTF-8") +
				"&password=" + URLEncoder.encode(PASSWORD, "UTF-8") +
				"&l=" + URLEncoder.encode("", "UTF-8");
		
		//Open an  output stream utilizing the socket output stream
		DataOutputStream out = new DataOutputStream(socket.getOutputStream());
		//Create a reader utilizing the socket input stream
		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		
		//Create a string for headers of the request
		String headers = "POST " + CACHESTATSPATH + " HTTP/1.1\r\n" +
				"Host: " + convertedLink.getHost() + "\r\n" +
				"Content-Type: application/x-www-form-urlencoded\r\n" +
				"Content-Length: " + POSTBODY.length() + "\r\n\r\n" +
				POSTBODY + "\r\n" + "Connection: close\r\n" + "\r\n";
		
		out.write(headers.getBytes());
		//Flush the stream
		out.flush();
		//Instantiate a string that represents each byte being read into the string builder
		String inData = null;
		//Instantiate the string builder to append stream data to
		StringBuilder builder = new StringBuilder();
		//Loop to append all the stream data into the builder
		while((inData = in.readLine()) != null) {
			builder.append(inData);
			if(inData.contains("Cookie")) {
				cookies.append(restructureCookie(inData));	
			}
		}
		//Close the stream
		in.close();
	}
	
	/**
	 * Method to get all of the parsed links of the website from the parser
	 * @return
	 */
	public List<String> getParsedLinks() {
		
		//Initialize a JsoupParser with the website link
		Parser parser = new Parser(url);
		//Parse all the links from the site
		parser.connectToUrlAndParseLinks();
		//Return a List of the parsed links from the Jsoup parser
		return parser.getLinks();
	}
	
	
	/**
	 * method to write HTML of each link to a local file
	 * @throws IOException 
	 */
	public void downloadHTML(String method) throws IOException {
		SocketManager manager = new SocketManager();
		if(method == "GET ") {
			List<String> parsedLinks = getParsedLinks();
			createSocketsAndWriteWithGET(parsedLinks);
		} else {
			//else, invoke the workflow posting credentials and using the cookies in the response to get the desired page
			SSLSocket socket;
			URL convertedLink = new URL(ADMINURL);
			socket = manager.makeSocketForConnection(convertedLink.getHost(), convertedLink.getDefaultPort());
			downloadCacheStatsHTML(socket, convertedLink);
		}
		
	}
	
	/**
	 * Method to make post request with credentials
	 * append cookies to the stringbuilder, and make a 
	 * GET request using those cookies to the cache stats page
	 * @param socket
	 * @param convertedLink
	 * @throws IOException
	 */
	public void downloadCacheStatsHTML(SSLSocket socket, URL convertedLink) throws IOException {
		//Make post request to admin tool passing creds
		makePOSTRequestAndSetCookies(socket, convertedLink);
		//Add the cachestats link to a list of links as the only link
		List<String> cacheStats = List.of(CACHESTATS);
		//Download the HTML
		createSocketsAndWriteWithGET(cacheStats);
	}
	
	public void createSocketsAndWriteWithGET(List<String> parsedLinks) throws MalformedURLException {
		SocketManager manager = new SocketManager();
		//Loop over the parsed links
			for(int i = 0; i < parsedLinks.size(); i++) {
				//Convert each link into a new URL
				URL convertedLink = new URL(parsedLinks.get(i));
				try{
					//Instantiate a socket
					SSLSocket socket;
					//If the port is -1, make the socket with the default port
					socket = manager.makeSocketForConnection(convertedLink.getHost(), convertedLink.getDefaultPort());			
						
					//Instantiate a string builder with makeRequestAndReadInStreamData to be written into file
					StringBuilder builder = makeRequestAndReadInStreamDataGET(socket, convertedLink);
					//Instantiate a writer using the home directory, assigning the name of the file to
					//write to from the path of the link
					BufferedWriter writer;
					if(convertedLink.getPath() == "/") {
						writer = new BufferedWriter(new FileWriter(new File(DIRECTORY, "home.txt" )));
					} else {
						writer = new BufferedWriter(new FileWriter(new File(DIRECTORY, convertedLink.getPath().replace("/", "") + ".txt" )));
					}
					//Write into the file using the builder toString
					writer.write(builder.toString());
					//Close the writer
					writer.close();
					//Close the socket
					socket.close();
					
				} catch (IOException e) {
					System.out.println(e.getStackTrace());
				}
			}
	}
	
	/**
	 * Method to grab the cookie from the line of data
	 * @param inData
	 * @return
	 */
	public String restructureCookie(String inData) {
		//Grab only the cookie value from the string
		return inData.split(" ")[1];
	}
	
}
