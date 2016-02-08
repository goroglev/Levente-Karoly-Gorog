package com.avg.app_similarity.similar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

import com.avg.app_similarity.eval.Cost;
import com.avg.app_similarity.eval.Eval;

public class Arguments {
	
	public final LinkedHashSet<String> startNodes;
	
	public final File similarApps;
	
	public final File appKeywordFile;
	
	public final File rankFile;
	
	public final boolean rank;
	
	public final boolean probDistrib;
	
	public final int top;
	
	public final int maxIterations;
	
	public final double convergenceThreshold;
	
	public final double restartProb;
	
	public final int max;
	
	public final List<Double> featureWeights;
	
	public final Cost cost; 
	
	private final OptionParser parser = new OptionParser() {
		{			
			accepts("appkeywordfile", "File containing app names with top k most descriptive terms per app").withRequiredArg().ofType(File.class).defaultsTo(new File("../data/graph/current.txt"));
			accepts("top", "the number of most similar apps returned").withRequiredArg().ofType(Integer.class).defaultsTo(10);
			accepts("max", "max nr of app nodes to include in the graph").withRequiredArg().ofType(Integer.class);
			accepts("r", "rank input ids");
			accepts("d", "print node probability distribution along with the node id");
			accepts("rankfile", "file ranked input ids are written to").withRequiredArg().ofType(File.class).defaultsTo(new File("../data/output/378.ranked"));
			accepts("l", "limit RWR by a max number of iterations");
			accepts("maxiterations", "the max number of iterations in RWR").withRequiredArg().ofType(Integer.class).defaultsTo(100);
			accepts("t", "convergence threshold").withRequiredArg().ofType(Double.class).defaultsTo(0.000009);
			accepts("restartp", "restart probability of RWR").withRequiredArg().ofType(Double.class).defaultsTo(0.01);
			accepts("featureweights", "the weights of category nodes and term nodes, in this order, separated by comma (,)").withRequiredArg().ofType(Double.class)
			.withValuesSeparatedBy(",").defaultsTo(.9, .1);
			accepts("eval", "evaluate similarity accuracy based on different metrics").withRequiredArg().ofType(Eval.class).defaultsTo(Eval.RankedScoring);
			accepts("goldstandardfile", "gold standard file used for evaluation").withRequiredArg().ofType(File.class).defaultsTo(new File("../data/input/ubergridSimilar.txt"));
			nonOptions("inputoutput" ). ofType( String.class ).describedAs( "input file (containing start nodes) / output file (similar apps per start node)" );
			acceptsAll( Arrays.asList( "h", "?" ), "show help" ).forHelp();
		}
	};

	public void usage() {
		System.out.println("Usage: RWR [-opts] <input file /w start nodes> <output file with similar apps>");
		try {
			parser.printHelpOn(System.out);
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			System.exit(-1);
		}
	}
	
	@SuppressWarnings("unchecked")
	public Arguments(String... args) throws Exception {
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

		appKeywordFile = (File) options.valueOf("appkeywordfile");
		File _startNodes = new File((String) nonOptionArguments.get(0));	
		
		File[] inputFiles = new File[] {appKeywordFile, _startNodes};
		for (File inputFile : inputFiles) {
			if (!inputFile.exists() || inputFile.isDirectory()) {
				try {
					System.out.println("Input file '" + inputFile.getCanonicalPath() + "' doesn't exist or is a directory!");
				} catch (IOException e) {
					e.printStackTrace();					
				} finally {
					System.exit(-1);
				}
			}
		}
		
		// TODO what is BufferedReader? what is FileReader?
		BufferedReader input = null;
		startNodes = new LinkedHashSet<String>();
		try {			
			input = new BufferedReader(new FileReader(_startNodes));
			String node = null;
			while ((node = input.readLine()) != null) {
				startNodes.add(node);
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		} finally {
			if (input != null)
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}			
		}
		
		top = options.has("top") ? ((Integer)options.valueOf("top")).intValue() : Integer.MAX_VALUE;
		max = options.has("max") ? ((Integer)options.valueOf("max")).intValue() : Integer.MAX_VALUE;

		similarApps = new File((String) nonOptionArguments.get(1));

		ArrayList<File> outputDirs = new ArrayList<File>();
		rank = options.has("r");
		probDistrib = options.has("d");
		rankFile = (File) options.valueOf("rankfile");
		if (rank) outputDirs.add(rankFile.getParentFile());
		outputDirs.add(similarApps.getParentFile());
		for (File outputDir : outputDirs) {
			if (!outputDir.exists()) {
				boolean dirCreated = outputDir.mkdirs();
				if (!dirCreated) {
					System.out.println("Couldn't create dir " + outputDir + " !");
					System.exit(-1);
				} else System.out.println("Directory '" + outputDir + "' successfully created.");
			}
		}
		
		maxIterations = options.has("l") ? ((Integer)options.valueOf("maxiterations")).intValue() : Integer.MAX_VALUE;
		
		convergenceThreshold = ((Double)options.valueOf("t")).doubleValue();
		
		restartProb = ((Double)options.valueOf("restartp")).doubleValue();
		
		featureWeights = (List<Double>) options.valuesOf("featureweights");
		
		Eval eval = (Eval) options.valueOf("eval"); 
		
		cost = (Cost) (eval.equals(Eval.NONE) ? null : eval.clazz.getConstructor(File.class).newInstance((File) options.valueOf("goldstandardfile")));
	}
}
