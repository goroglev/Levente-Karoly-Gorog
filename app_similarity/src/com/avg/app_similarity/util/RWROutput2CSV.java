package com.avg.app_similarity.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;

public class RWROutput2CSV {

	public static void main(String[] args) throws Exception {
		BufferedReader input = new BufferedReader(new FileReader("/media/sf_workspace/app_permissions/data/output/ubergrid.full2"));
		FileWriter writer = new FileWriter("/media/sf_workspace/app_permissions/data/output/select.appcrawlr.benchmark.results.csv");
		
		String line = null;
		while ((line = input.readLine()) != null) {
			String splits[] = line.split(" ");
			// writer.write(splits[0] + "," + splits[0] + "," + splits[1] + "\r\n");
			for (int i = 2; i < splits.length; i += 2) {
				writer.write(splits[0] + "," + splits[i] + "," + splits[i+1] + "\r\n");
			}
		}
		
		input.close();
		writer.close();

	}

}
