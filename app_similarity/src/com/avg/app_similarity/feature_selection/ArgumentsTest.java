package com.avg.app_similarity.feature_selection;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ArgumentsTest {

	@Test
	public void testInputOutputDir() {
		new Arguments("../output-AppDescriptions", "../tfidf-AppDescriptions");
	}
	
	@Test
	public void  testInvalidOptions() {
		new Arguments("--k=4", "../output-AppDescriptions", "../tfidf-AppDescriptions");
		new Arguments("--stoppath=resources/stopwords.txt", "-k");
	}
	
	@Test
	public void testInvalidSyntax() {
		new Arguments("../output-AppDescriptions", "../tfidf-AppDescriptions", "--k=4");
	}
	
	@Test
	public void testInvalidInput() {		
		new Arguments("--worddocfreq=output/bla", "../output-AppDescriptions", "../tfidf-AppDescriptions");
	}
	
	@Test
	public void testMaxFiles() {
		Arguments args = new Arguments("--max=20", "../output-AppDescriptions", "../tfidf-AppDescriptions");		
		assertEquals(20, args.termFreqFiles.size());		
	}	
}
