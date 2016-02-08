package com.avg.app_similarity.tokenization;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import joptsimple.OptionSet;

import com.avg.app_similarity.util.Sorter;

//TODO language detection
public class Tokenizer {
			
	private static final String[] marks = { "(r)", "(sm)", "(c)", "(tm)" };	
	private static final String[] plainMarks = { "r", "sm", "c", "tm" };
	
	private static final String preRemove = "^[^a-z0-9]+";
	private static final String postRemove = "[`;\\.?:'\\]\\*\\+<>-_]+$";
	private static final String wordSplit = "(/)|(\\.{2,})|([()])|([*>]+)";
	
	
	private static PorterStemmer porterStemmer = new PorterStemmer();
	private final Stemmer stemmer;

	private final OptionSet options;
	
	private final List<File> appDescriptors;	
	
	private final Stopwords stopwords;		
	
	public Tokenizer(final String[] args) throws Exception {
		Arguments arguments = new Arguments(args);
		options = arguments.options;		
		appDescriptors = arguments.appDescriptors;
		stopwords = (!options.has("s")) ? new Stopwords((File) options.valueOf("stoppath")) : new Stopwords();
		stemmer = new Stemmer((File) options.valueOf("stemmerpath"));
	}
	
	//	private static final String[] forbiddenPrefixes = {"http://", "http://", "www.", "android.permission."};
		
	public Map<String, Freq> batchJob() {
		
		Map<String, Freq> wordsFreq = new HashMap<String, Freq>();

		for (File appDescriptor : appDescriptors) {
			BufferedReader input = null;
			FileWriter outputFile = null;
			Map<String, Integer> wordsFreqInDoc = new HashMap<String, Integer>();
			try {
				input = new BufferedReader(new FileReader(appDescriptor));
				if (!Output.NONE.equals(options.valueOf("output"))) outputFile = new FileWriter(new File((File) options.valueOf("outputdir"), appDescriptor.getName()));								
				for (int i = 0; i < 3; ++i) input.readLine(); // skip the first 3 lines;
				
				String appName = input.readLine();
				tokenize(appName, outputFile, wordsFreqInDoc);
				Map<String, Integer> appNameWordCounts = new HashMap<String, Integer>(wordsFreqInDoc);
				
				String descText = null;
				// The order of the tokenization operations below is critical!
				while ((descText = input.readLine()) != null) {
					tokenize(descText, outputFile, wordsFreqInDoc);
				}
				if (Output.WORDFREQ.equals(options.valueOf("output")) || options.has("f") || options.has("d")) {
					for (Entry<String, Integer> wordFreqInDocEntry : wordsFreqInDoc.entrySet()) {						
						if (options.has("f") || options.has("d")) {
							Freq freq = wordsFreq.get(wordFreqInDocEntry.getKey());
							if (freq != null) {
								freq.incrWordDocFreq(); freq.addToWordFreq(wordFreqInDocEntry.getValue());
							} else wordsFreq.put(wordFreqInDocEntry.getKey(), new Freq(wordFreqInDocEntry.getValue()));
						}
						if (Output.WORDFREQ.equals(options.valueOf("output"))) {
							Integer appNameWordCount = appNameWordCounts.get(wordFreqInDocEntry.getKey());
							Integer count = appNameWordCount == null ? wordFreqInDocEntry.getValue() : wordFreqInDocEntry.getValue() + (((Integer) options.valueOf("weight")) - 1) * appNameWordCount;
							outputFile.append(wordFreqInDocEntry.getKey()).append("\t").append(count.toString()).append("\n");
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {				
				try {
					if (input != null) input.close();
					if (outputFile != null) outputFile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}				
			}
		}
		return wordsFreq;
	}
	
	// writing sorted word counts and word document counts to file
	public void writeFreqFiles(Map<String, Freq> wordsFreq) {
		if (options.has("f") || options.has("d")) {
			
			HashMap<String, Long> wordFreqMap = new HashMap<String, Long>();
			HashMap<String, Long> wordDocFreqMap = new HashMap<String, Long>();			
			
			for (Entry<String, Freq> wordFreqEntry : wordsFreq.entrySet()) {
				if (options.has("f")) wordFreqMap.put(wordFreqEntry.getKey(), wordFreqEntry.getValue().wordFreq);
				if (options.has("d")) wordDocFreqMap.put(wordFreqEntry.getKey(), Long.valueOf(wordFreqEntry.getValue().wordDocFreq));
			}				
			
			FileWriter fwWordsFreq = null;
			FileWriter fwWordsDocFreq = null;
			
			try {
				if (options.has("f")) {
					fwWordsFreq = new FileWriter((File) options.valueOf("wordfreqpath"));					
					TreeMap<String, Long> sortedByWordFreq = Sorter.sortByValue(wordFreqMap);
					for (Entry<String, Long> wordFreq : sortedByWordFreq.descendingMap().entrySet()) {
						fwWordsFreq.write(wordFreq.getKey() + "\t" + wordFreq.getValue() + "\n");
					}
					fwWordsFreq.close();
				}
				if (options.has("d")) {
					fwWordsDocFreq = new FileWriter((File) options.valueOf("docfreqpath"));										
					TreeMap<String, Long> sortedByWordDocFreq = Sorter.sortByValue(wordDocFreqMap);
					for (Entry<String, Long> wordDocFreq : sortedByWordDocFreq.descendingMap().entrySet()) {
						fwWordsDocFreq.write(wordDocFreq.getKey() + "\t" + wordDocFreq.getValue() + "\n");
					}
					fwWordsDocFreq.close();
				}				

			} catch (IOException e) {			
				e.printStackTrace();
			}
		}

	}
	
	private void tokenize(String str, FileWriter outputFile, Map<String, Integer> wordsFreqInDoc) throws IOException {
		String[] tokens = str.toLowerCase().split("[\\s,\"!]+");
		for (String token : tokens) {
			String word = token.replaceAll(preRemove, "").replaceAll(postRemove, "");					
			if (word.startsWith("http://") || word.startsWith("https://") || word.startsWith("android.permission.")) continue;
			if (word.endsWith(")")) {
				for (int i = 0; i < marks.length; ++i) {
					if (word.endsWith(marks[i])) {
						word = word.replace(marks[i], plainMarks[i]);
						break;
					}
				}
			}
			if (word.endsWith("'s")) {
				word = word.substring(0, word.length() - 2);
			}
			String[] words = word.split(wordSplit);						
			for (String _word : words) {
				if (words.length >= 2) {
					_word = _word.replaceAll(preRemove, "").replaceAll(postRemove, "");													
				}
				if (_word.startsWith("www.") || _word.endsWith(".com")) continue;
				
				_word = _word.replaceFirst("^\\d\\.", "").replaceFirst("^v\\d\\.", ""); //.replaceAll("\\.", "");
				
				if (stopwords.contains(_word) || _word.length() <=1 || _word.matches("[\\d\\.]+%?") || _word.contains("_")) continue;	
				_word = porterStemmer.stem(_word);
				//_word = stemmer.stem(_word);
				
				if (Output.TOKENIZED.equals(options.valueOf("output"))) outputFile.append(_word).append(" ");
				
				if (Output.WORDFREQ.equals(options.valueOf("output")) || options.has("f") || options.has("d")) {
					Integer wordFreq = wordsFreqInDoc.get(_word);
					if (wordFreq != null) {
						wordFreq = Integer.valueOf(wordFreq.intValue() + 1);
					} else wordFreq = Integer.valueOf(1);
					wordsFreqInDoc.put(_word, wordFreq);
				}
			}
		}				

	}
	
	public static void main(String[] args) throws Exception {
		
/*		time java -jar lib/app_similarity.jar -f -d --output=WORDFREQ ../AppDescriptions

				real	11m51.732s
				user	2m52.648s
				sys	2m43.496s
*/
		
		Tokenizer tokenizer = new Tokenizer(args);
		tokenizer.writeFreqFiles(tokenizer.batchJob());
		
	}

}
