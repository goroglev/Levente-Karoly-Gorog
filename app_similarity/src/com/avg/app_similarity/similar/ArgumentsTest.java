package com.avg.app_similarity.similar;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.avg.app_similarity.eval.AUC;

public class ArgumentsTest {

	@Test
	public void testInputOutputDir() throws Exception {
		new Arguments("../output-AppDescriptions", "../tfidf-AppDescriptions");
	}
	
	@Test
	public void  testInvalidOptions() throws Exception {
		new Arguments("--k=4", "../output-AppDescriptions", "../tfidf-AppDescriptions");
		new Arguments("--stoppath=resources/stopwords.txt", "-k");
	}
	
	@Test
	public void testInvalidSyntax() throws Exception {
		new Arguments("../output-AppDescriptions", "../tfidf-AppDescriptions", "--k=4");
	}
	
	@Test
	public void testInvalidInput() throws Exception {		
		new Arguments("--worddocfreq=output/bla", "../output-AppDescriptions", "../tfidf-AppDescriptions");
	}
	
	@Test
	public void testEval() throws Exception {
		Arguments args = new Arguments("--eval=AUC", "../data/input/ubergrid.cool", "/tmp/bla");
		assertEquals((args.cost instanceof AUC), true);
	}
	
	
/*	@Test
	public void testMaxFiles() {
		Arguments args = new Arguments("--max=20", "../output-AppDescriptions", "../tfidf-AppDescriptions");		
		assertEquals(20, args.termFreqFiles.size());		
	}	
*/}
