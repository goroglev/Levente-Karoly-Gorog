package com.avg.app_similarity.tokenization;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

public class Arguments {
	
	public OptionSet options;
	
	public final List<File> appDescriptors;	
	
	private final OptionParser parser = new OptionParser() {
		{
			accepts("s", "do not skip stopwords");
			accepts("stoppath", "stopwords filepath").withRequiredArg().ofType(File.class).defaultsTo(new File("../data/resources/stopwords.txt"));
			accepts("stemmerpath", "stems filepath").withRequiredArg().ofType(File.class).defaultsTo(new File("../data/inv-doc-freq/stems"));
			accepts("f", "compute overall word frequency");
			accepts("wordfreqpath", "word frequency output filepath").withRequiredArg().ofType(File.class).defaultsTo(new File("../data/word-freq/select.txt"));
			String outputDesc = "how to handle tokenized text per doc: NONE: do nothing; TOKENIZED: write space-separated tokens to file; WORDFREQ: write unique words per doc with their local freq to file";
			accepts("output", outputDesc).withRequiredArg().ofType(Output.class).defaultsTo(Output.WORDFREQ);
			accepts("outputdir", "app descriptions output directory (tokenized text / word freq per document)").withRequiredArg().ofType(File.class).defaultsTo(new File("../data/tokenized/select/"));
			accepts("d", "compute document frequency per word");
			accepts("docfreqpath", "document frequency output filepath").withRequiredArg().ofType(File.class).defaultsTo(new File("../data/inv-doc-freq/select.txt"));
			accepts("max", "max nr of app description files to process").withRequiredArg().ofType(Integer.class);
			accepts("weight", "the multiplier of the word freq in the app name").withRequiredArg().ofType(Integer.class).defaultsTo(3);
			//accepts("ratio", "ratio of nr of app description files to process").withRequiredArg().ofType(Float.class);
			nonOptions("app description files / directory" ). ofType( String.class ).describedAs( "input files" );
			acceptsAll( Arrays.asList( "h", "?" ), "show help" ).forHelp();	
		}
	};

	public void usage() {
		System.out.println("Usage: Tokenizer [-opts] <app description input file(s) | dir>");
		System.out.println("last execution time: 13m56.849s");
		try {
			parser.printHelpOn(System.out);
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			System.exit(-1);
		}
	}
	
	public Arguments(String... args) {
		try {
			options = parser.parse(args);
		} catch (OptionException e) {
			System.out.println(e.getMessage());
			usage();
		}		
		
		List<?> nonOptionArguments = options.nonOptionArguments();
		if (nonOptionArguments.size() > 0) {
			int offset = args.length - nonOptionArguments.size();
			
			boolean haveArgumentsCorrectSyntax = Arrays.equals(Arrays.copyOfRange(args, offset, args.length), nonOptionArguments.toArray(new String[]{}));
			if (!haveArgumentsCorrectSyntax) {
				usage();
			}
		} else usage();
		
		List<File> inputFiles = new ArrayList<File>();
		
		for (Object obj : nonOptionArguments) {
			inputFiles.add(new File((String)obj));
		}
		if (!options.has("s")) inputFiles.add((File) options.valueOf("stoppath"));
		
		for (File inputFile : inputFiles) {
			if (!inputFile.exists()) {
				try {
					System.out.println("Input file/directory '" + inputFile.getCanonicalPath() + "' doesn't exist!");
				} catch (IOException e) {
					e.printStackTrace();					
				} finally {
					System.exit(-1);
				}
			}
		}
		
		if (!options.has("s")) inputFiles.remove(inputFiles.size() - 1); // remove stopwords file from the input files to be tokenized
			
		List<File> outputDirs = new ArrayList<File>();
		if (options.has("f")) outputDirs.add(((File) options.valueOf("wordfreqpath")).getParentFile());
		if (!Output.NONE.equals(options.valueOf("output"))) outputDirs.add((File) options.valueOf("outputdir"));
		if (options.has("d")) outputDirs.add(((File) options.valueOf("docfreqpath")).getParentFile());
		for (File outputDir : outputDirs) {
			if (!outputDir.exists()) {
				boolean dirCreated = outputDir.mkdirs();
				if (!dirCreated) {
					System.out.println("Couldn't create dir " + outputDir + " !");
					System.exit(-1);
				} else System.out.println("Directory '" + outputDir + "' successfully created.");
			}
		}
		
		appDescriptors = new ArrayList<File>();
		int max = options.has("max") ? ((Integer)options.valueOf("max")).intValue() : 0;
		if (max <= 0) max = Integer.MAX_VALUE;
		for (File inputFile : inputFiles) {
			if (appDescriptors.size() == max) break;
			if (inputFile.isDirectory()) {
				File[] _files = inputFile.listFiles(new FilenameFilter() {														
					public boolean accept(File dir, String name) {
						return name != null && name.matches("\\d+"); // only list files with numbers as file names
					}
				});
				if (_files != null) {
					int newLength = appDescriptors.size() + _files.length <= max ? _files.length : (max - appDescriptors.size());   
					appDescriptors.addAll(Arrays.asList(Arrays.copyOf(_files, newLength)));
				}
						
			} else {
				if (appDescriptors.size() + 1 <= max) appDescriptors.add(inputFile); // doesn't look at extension, accept it as is
			}
		}		
	}
}
