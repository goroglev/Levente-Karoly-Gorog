package com.avg.app_similarity.feature_selection;

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
	
	public final List<File> termFreqFiles;
	
	public final File outputFile;
	
	public final File wordDocFreqFile;
	
	public final int k;
	
	public final double threshold;
	
	public final int minDocsNr;
	
	public final int nrDocs;
	
	public final boolean append;
	
	public final File origInputDir;
	
	public final File annotOutputDir;
	
	
	private final OptionParser parser = new OptionParser() {
		{			
/*			accepts("inputdir", "input directory with app description files containing terms / term-freqs").withRequiredArg().ofType(File.class).defaultsTo(new File("../terms-AppDescriptions/"));
			accepts("outputdir", "output directory with top k terms per app description file").withRequiredArg().ofType(File.class).defaultsTo(new File("../topterms-AppDescriptions/"));
*/			accepts("worddocfreq", "word-document frequency input file").withRequiredArg().ofType(File.class).defaultsTo(new File("../data/inv-doc-freq/select.txt"));
			accepts("k", "the number of terms with the top tf-idf scores to retain").withRequiredArg().ofType(Integer.class).defaultsTo(6);
			accepts("threshold", "tf-idf threshold (only terms with a greater or equal values will be retained").withRequiredArg().ofType(Double.class).defaultsTo(.0);
			accepts("mindocs", "The min number of docs a term should occur in in order to be considered.").withRequiredArg().ofType(Integer.class).defaultsTo(50);			
			accepts("o", "append the top k terms to the end of the original app description texts");
			accepts("originputdir", "original app descriptions input directory").withRequiredArg().ofType(File.class).defaultsTo(new File("../data/desc/select/"));
			accepts("annotoutputdir", "output dir for files with orig app desc text and top k tf-idf terms").withRequiredArg().ofType(File.class).defaultsTo(new File("../data/annotated/select/"));
			accepts("max", "max nr of app description files to process").withRequiredArg().ofType(Integer.class);
			nonOptions("inputoutput" ). ofType( String.class ).describedAs( "input (terms/term freq) directory / output file (top k tf-idf terms per app)" );
			acceptsAll( Arrays.asList( "h", "?" ), "show help" ).forHelp();
		}
	};

	public void usage() {
		System.out.println("Usage: FeatureSelector [-opts] <input dir> <output dir>");
		System.out.println("last execution time: 12m1.639s");
		try {
			parser.printHelpOn(System.out);
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			System.exit(-1);
		}
	}
	
	public Arguments(String... args) {
		OptionSet options = null;
		try {
			options = parser.parse(args);
		} catch (OptionException e) {
			System.out.println(e.getMessage());
			usage();
		}
		
		List<?> nonOptionArguments = options.nonOptionArguments();
		if (nonOptionArguments.size() != 2) usage();
		boolean haveArgumentsCorrectSyntax = Arrays.equals(Arrays.copyOfRange(args, args.length - 2, args.length), nonOptionArguments.toArray(new String[]{}));
		if (!haveArgumentsCorrectSyntax) {
			usage();
		}		

		List<File> inputDirs = new ArrayList<File>();	
		File inputDir = new File((String) nonOptionArguments.get(0));
		inputDirs.add(inputDir);
		
		List<File> outputDirs = new ArrayList<File>();		
		outputFile = new File((String) nonOptionArguments.get(1));
		outputDirs.add(outputFile.getParentFile());

		k = (Integer) options.valueOf("k");
		threshold = (Double) options.valueOf("threshold");
		minDocsNr = (Integer) options.valueOf("mindocs");
		append = options.has("o");
		origInputDir = (File) options.valueOf("originputdir");
		inputDirs.add(origInputDir);
		if (append) {						
			annotOutputDir = (File) options.valueOf("annotoutputdir");
			outputDirs.add(annotOutputDir);					
		} else {
			annotOutputDir = null;
		}
		for (File _inputDir : inputDirs) {
			if (!_inputDir.isDirectory()) {
				try {
					System.out.println("'" + _inputDir.getCanonicalPath() + "' is not a directory!");
				} catch (IOException e) {
					e.printStackTrace();					
				} finally {
					System.exit(-1);
				}
			}
		}
		
		wordDocFreqFile = (File) options.valueOf("worddocfreq");
		if (!wordDocFreqFile.exists() || wordDocFreqFile.isDirectory()) {
			try {
				System.out.println("Word document freq file '" + wordDocFreqFile.getCanonicalPath() + "' doesn't exist or is a directory!");
			} catch (IOException e) {
				e.printStackTrace();					
			} finally {
				System.exit(-1);
			}
		}
			
		for (File outputDir : outputDirs) {
			if (!outputDir.exists()) {
				boolean dirCreated = outputDir.mkdirs();
				if (!dirCreated) {
					System.out.println("Couldn't create dir " + outputDir + " !");
					System.exit(-1);
				} else System.out.println("Directory '" + outputDir + "' successfully created.");
			} else if (!outputDir.isDirectory()) {
				System.out.println(outputDir + " is not a directory!");
				System.exit(-1);
			}
		}
		
		termFreqFiles = new ArrayList<File>();
		int max = options.has("max") ? ((Integer)options.valueOf("max")).intValue() : 0;
		if (max <= 0) max = Integer.MAX_VALUE;
		File[] _files = inputDir.listFiles(new FilenameFilter() {														
			public boolean accept(File dir, String name) {
				return name != null && name.matches("\\d+"); // only list files with numbers as file names
			}
		});
		nrDocs = _files.length;
		if (_files != null) {
			int newLength = _files.length <= max ? _files.length : max;   
			termFreqFiles.addAll(Arrays.asList(Arrays.copyOf(_files, newLength)));
		}		
	}
}
