package com.avg.app_similarity.util;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map.Entry;

public class CategoryInfo370 {

	public static void main(String[] args) throws Exception {
		ArrayList<String> appIds = Reader.readLinesIntoList(new File(args[0]), Integer.MAX_VALUE);
		HashMap<String, LinkedHashSet<String>> categories = Reader.readMapSet(new File(args[1]), "\t", Integer.MAX_VALUE);
		FileWriter fw = new FileWriter(args[3]);
		for (String id : appIds) {
			try {
				String appName = Reader.readLine(new File(args[2], id), 4);
				boolean found = false;
				for (Entry<String, LinkedHashSet<String>> entry : categories.entrySet()) {
					if (entry.getValue().contains(appName)) {
						fw.write(id + "\t" + entry.getKey() + "\n");
						found = true;
						break;
					}
				}
				if (!found) {
					System.out.println(id + " " + appName + " couldn't be matched!");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		fw.close();
	}

}
