package com.avg.app_similarity.util;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashSet;

public class CategoryDistribution {
	
	public static void main(String[] args) throws Exception {
		File inputDir = new File("../data/annotated/aggregate_res/google.full/");
		File[] files = inputDir.listFiles(new FilenameFilter() {														
			public boolean accept(File dir, String name) {
				return name != null && name.matches("\\d+"); // only list files with numbers as file names
			}
		});
		
		int i = 0;
		int sumIndex = 0;
		for (File file : files) {
			ArrayList<String> categories = Reader.readLinesIntoList(file, 20);
			HashSet<String> uniqCat = new HashSet<String>(categories);
			if (uniqCat.size() > 1 ) {
				++i;
				System.out.print(file.getName() + ": " + categories.get(0));
				uniqCat.remove(categories.get(0));
				for (int j = 1; j < categories.size(); ++j) {
					if (uniqCat.contains(categories.get(j))) {
						sumIndex += j;
						break;
					}
				}
				for (String cat : uniqCat) {
					System.out.print(" | " + cat);
				}
				System.out.println();
			}
		}
		System.out.println("===========================");
		System.out.println(Format.df3.format((double)i / files.length * 100) +"% (" + i + " out of " + files.length + ") of the top 20 similar app entries fall within more than one category." );
		System.out.println("In these " +i + " cases the first deviation in category occurs on average at the " + sumIndex / i + " position (out of 19 elements)" );
	}

}
