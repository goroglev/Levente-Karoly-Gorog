package com.avg.app_similarity.tokenization;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ArgumentsTest {

	@Test
	public void testExistenceOfStopWordsPath() {
		new Arguments("--stoppath=resources/stopwords.txt");
		new Arguments("--stoppath=/bla/bla/bla");
	}
	
	@Test
	public void  testInvalidOptions() {
		new Arguments("--stoppath=resources/stopwords.txt", "-f");
		new Arguments("--stoppath=resources/stopwords.txt", "-k");
	}
	
	@Test
	public void testInvalidSyntax() {
		new Arguments("bla", "--stoppath=resources/stopwords.txt", "-f");
	}
	
	@Test
	public void testInvalidInput() {		
		new Arguments("--stoppath=resources/stopwords.txt", "-f", "bla");
	}
	
	@Test
	public void testMaxFiles() {
		Arguments args = new Arguments("--stoppath=resources/stopwords.txt", "-f", "--max=20", "../AppDescriptions");		
		assertEquals(20, args.appDescriptors.size());
		args = new Arguments("--stoppath=resources/stopwords.txt", "-f", "--max=4", "../IOTools.py", "../articles");	
	}
	
	@Test
	public void TestOutputDirective() {
		Arguments args = new Arguments("--output=WORDFREQ");
		assertEquals(Output.WORDFREQ, (Output) args.options.valueOf("output"));
		System.out.println(Output.WORDFREQ.equals(args.options.valueOf("output")));
	}
	
		
//		accepts("s", "do not skip stopwords");
//		accepts("stoppath", "stopwords filepath").withRequiredArg().ofType(File.class).defaultsTo(new File("resources/stopwords.txt"));
//		accepts("f", "compute overall word frequency");
//		accepts("wordfreqpath", "word frequency output filepath").withRequiredArg().ofType(File.class).defaultsTo(new File("output/word-freq.txt"));
//		accepts("w", "do not write tokenized words to file");
//		accepts("tokenizeddir", "tokenized app descriptions output directory").withRequiredArg().ofType(File.class).defaultsTo(new File("../tokenized-AppDescriptions/"));
//		accepts("d", "compute document frequency per word");
//		accepts("docfreqpath", "document frequency output filepath").withRequiredArg().ofType(File.class).defaultsTo(new File("output/doc-freq.txt"));
//		accepts("max", "max nr of app description files to process").withRequiredArg().ofType(Integer.class);
//		accepts("ratio", "ratio of nr of app description files to process").withRequiredArg().ofType(Float.class);
//		nonOptions("app description files / directory" ).ofType( String.class ).describedAs( "input files" );
//		acceptsAll( Arrays.asList( "h", "?" ), "show help" ).forHelp();		
//		fail("Not yet implemented"); // TODO

}
