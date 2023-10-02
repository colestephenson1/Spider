package com.stephenson.spider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class Parser {
	
	private String url;
	private List<String> links = new ArrayList<>();
	
	/**
	 * Constructor. Takes in URL to parse
	 * @param url
	 */
	public Parser(String url) {
		this.url = url;
	};
	
	/**
	 * Get all the links in the links member variable
	 * @return links
	 */
	public List<String> getLinks() {
		return links;
	};
	
	/**
	 * Method using Jsoup to connect to website URL
	parse all the anchor tags, grab their href,
	plug them into a list.
	 */
	public void connectToUrlAndParseLinks() {
		try {
			//Connect to url with Jsoup
			Document doc = Jsoup.connect(url).get();
			for(Element link : doc.select("a")) {
				//Use boolean method to prevent duplicates in the list
				if(link.attr("abs:href").startsWith("http") && linkIsNotInList(link.attr("abs:href"))) {				
					links.add(link.attr("abs:href"));
				};
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Method to prevent duplicate links ending up in the list
	 * @param link
	 * @return boolean
	 */
	public boolean linkIsNotInList(String link) {
		//Iterate across the links, if it is already present in the list,
		//return false
		for(String storedLink : links) {
			if(storedLink.equals(link)) {
				return false;
			}
		}
		return true;
	}
}
