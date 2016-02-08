package com.avg.app_similarity.util;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;

public class ContentRatingDistribution {

	public static void main(String[] args) throws Exception {
		HashMap<String, LinkedHashSet<String>> all = Reader.readMapSet(new File(args[0]), ",", Integer.MAX_VALUE);
		ArrayList<String> train = Reader.readLinesIntoList(new File(args[1]), Integer.MAX_VALUE);
		FileWriter fw = new FileWriter(new File(args[2]));
		for (String trainItem : train) {
			fw.append(trainItem);
			for (String id : all.get(trainItem)) {
				fw.append(" ").append(id);
			}
			fw.append("\n");
		}
		fw.close();

	}

}
