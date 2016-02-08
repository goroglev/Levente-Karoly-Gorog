package com.avg.app_similarity.tokenization;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;

public class Stopwords {
	
	private final HashSet<String> stopwords = new HashSet<String>();
	
	public Stopwords(File stopwordsFile) {
		BufferedReader input = null;
		try {
			input = new BufferedReader(new FileReader(stopwordsFile));
			String stopword = null;
			while ((stopword = input.readLine()) != null) this.stopwords.add(stopword); 
		} catch (IOException e) {
			// print error and exit
			e.printStackTrace();
			System.exit(-1);
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
					System.out.println("Couldn't close 'resources/stopwords.txt' file reader. Program will continue.");
				}
			}
		}
	}
	
	public Stopwords() {
		
	}
	
	public boolean contains(String stopword) {
		return stopwords.contains(stopword);
	}

}
