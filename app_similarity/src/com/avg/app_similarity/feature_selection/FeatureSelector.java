package com.avg.app_similarity.feature_selection;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.avg.app_similarity.util.Brain;
import com.avg.app_similarity.util.Format;
import com.avg.app_similarity.util.Reader;
import com.avg.app_similarity.util.Sorter;

// term quantifiers (given document)
class TermQuantifiers implements Comparable<TermQuantifiers> {
	public TermQuantifiers(final double tfIdf, final int freq, final int inverseDocFreq) {
		this.tfIdf = tfIdf;
		this.freq = freq;
		this.inverseDocFreq = inverseDocFreq;
	}
	public Double tfIdf;
	public int freq;
	public int inverseDocFreq;

	@Override
	public int compareTo(TermQuantifiers o) {
		if (o != null && this.tfIdf != null) return this.tfIdf.compareTo(o.tfIdf);
		return 0;
	}
}

public class FeatureSelector {	
	
	private final HashMap<String, Integer> inverseDocFreqs; 
	
	private final Arguments args;		
		
	public FeatureSelector(String[] arguments) throws Exception {
		args = new Arguments(arguments);
		inverseDocFreqs = Reader.readIntoHashMap(args.wordDocFreqFile, "\t", new HashMap<String, Integer>(), new Brain<Integer>() {
			
			@Override
			public Integer compute(String str1, String str2) {				
				return Integer.valueOf(str2);
			}
		});		
	}
	
	public void batchJob() throws IOException {
		
		FileWriter outFw = new FileWriter(args.outputFile);

		long startTime = System.currentTimeMillis();
		// write header for controlled experiments
		outFw.write("Max. nr of terms: " + args.k + "\n");
		outFw.write("Min nr. of docs a term should occur in: " + args.minDocsNr + "\n");
		outFw.write("TF-IDF threshold: " + args.threshold + "\n");
		if (args.append) outFw.write("Appended to orig. descriptions.\n");
		outFw.write("\n");
		
		double avgDocLength = 0;
		
		for (File inputFile : args.termFreqFiles) {			
			FileWriter annotatedFw = null;
			BufferedReader input = null;
			try {
				input = new BufferedReader(new FileReader(inputFile));
				final HashMap<String, TermQuantifiers>  termsWithQuantifiers = new HashMap<String, TermQuantifiers>();
				String line = null;
				int docLength = 0;
				while ((line = input.readLine()) != null) {
					String[] splits = line.split("\t");
					if (splits.length != 2) throw new Exception("File '" + inputFile.getCanonicalPath() + "' contains an illegal line: '" + line + "'");
					String term = splits[0];
					int inverseDocFreq =  inverseDocFreqs.get(term); int freq = Integer.valueOf(splits[1]); docLength += freq;
					if (inverseDocFreq >= args.minDocsNr) {						
						double x = (double)args.nrDocs / inverseDocFreq;
						double tfIdf = freq * Math.log(x);
						termsWithQuantifiers.put(term, new TermQuantifiers(tfIdf, freq, inverseDocFreq));
					}
				}
				
				avgDocLength += docLength;
				double cubeRootDocLength = Math.pow(docLength, 1./3);
				TreeMap<String, TermQuantifiers> sortedQuantifiedTerms = Sorter.sortByValue(termsWithQuantifiers);				
				int i = 0;
				double tfidfSum = 0;
				for (Entry<String, TermQuantifiers> quantifiedTerm : sortedQuantifiedTerms.descendingMap().entrySet()) {
					//TODO check if term is not a plural or verb form (3rd person, singular) of a previous term
					double normalizedTfIdf = quantifiedTerm.getValue().tfIdf / cubeRootDocLength;
					quantifiedTerm.getValue().tfIdf = normalizedTfIdf;
					if (normalizedTfIdf < args.threshold || i >= args.k) break;
					tfidfSum += normalizedTfIdf; ++i;				
				}
				// String[] id_package = inputFile.getName().replaceFirst("\\.txt$", "").split("_", 2); TODO
				// StringBuilder sb = new StringBuilder(id_package[0]).append("\t").append(id_package[1]).append("\t"); 
				StringBuilder sb = new StringBuilder(inputFile.getName()).append("\t");
				File descFile = new File(args.origInputDir, inputFile.getName());				
				ArrayList<String> features = Reader.readLinesIntoList(descFile, 3);
				for (String feature : features) sb.append(feature).append("\t");				
				if (args.append) {					
					annotatedFw = new FileWriter(new File(args.annotOutputDir, inputFile.getName()));		
					annotatedFw.write(docLength + "\t");
				}
				int j = 0;
				for (Entry<String, TermQuantifiers> quantifiedTerm : sortedQuantifiedTerms.descendingMap().entrySet()) {
					if (j++ >= i) break;
					sb.append(quantifiedTerm.getKey()).append(" ").append(Format.df3.format(quantifiedTerm.getValue().tfIdf / tfidfSum)).append(" ").append(Format.df3.format(quantifiedTerm.getValue().freq / cubeRootDocLength)).append(" ");
					if (args.append) {
						annotatedFw.write(new StringBuilder(quantifiedTerm.getKey())
								.append("/").append(quantifiedTerm.getValue().freq)
								.append("/").append(quantifiedTerm.getValue().inverseDocFreq)
								.append("/").append(Format.df3.format(quantifiedTerm.getValue().tfIdf))
								.append("\t").toString());
					}					
				}
				if (i > 0) {
					outFw.write(sb.append("\n").toString());
					if (args.append) {
						annotatedFw.write("\n\n\n");
						String origDesc = Reader.readIntoString(descFile, 0);
						annotatedFw.write(origDesc);
					}
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (input != null) input.close();
					if (annotatedFw != null) annotatedFw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}				
			}			
		}		
		outFw.write("Avg. doc length: " + avgDocLength / args.termFreqFiles.size() + "\n");
		long stopTime = System.currentTimeMillis();
		outFw.write("Elapsed time (sec): " + (stopTime - startTime) / 1000.);
		if (outFw != null) outFw.close();		
	}
	
	public static void main(String[] args) {
		
		FeatureSelector tfidf = null;
		try {
			tfidf = new FeatureSelector(args);
			tfidf.batchJob();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			System.exit(-1);
		}
	}

}