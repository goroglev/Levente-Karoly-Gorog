package com.avg.app_similarity.tokenization;

public class Freq implements Comparable<Freq> {
	long wordFreq;
	int wordDocFreq;
	
	public Freq(long wordFreq) {
		this.wordFreq = wordFreq;
		this.wordDocFreq = 1;
	}
	
	void incrWordDocFreq() {
		++wordDocFreq;
	}
	
	void addToWordFreq(int wordFreqInDoc) {
		wordFreq += wordFreqInDoc;
	}

	public int compareTo(Freq o) {
		
		return 0;
	}
}