/*******************************************************************************
 * Copyright 2013 Lars Behnke
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.apporiented.algorithm.clustering;

import java.util.ArrayList;
import java.util.List;

public class DefaultClusteringAlgorithm implements ClusteringAlgorithm {
	
	@Override
	public Cluster performClustering(double[][] distances,
	        String[] clusterNames, LinkageStrategy linkageStrategy) {

		HierarchyBuilder builder = getHierarchyBuilder(distances, clusterNames, linkageStrategy);
		while (!builder.isTreeComplete()) {
			builder.agglomerate(linkageStrategy);
		}

		return builder.getRootCluster();
	}
	
	protected HierarchyBuilder getHierarchyBuilder(double[][] distances,
	        String[] clusterNames, LinkageStrategy linkageStrategy) {
		/* Argument checks */
		if (distances == null || distances.length == 0
		        || distances[0].length != distances.length) {
			throw new IllegalArgumentException("Invalid distance matrix");
		}
		if (distances.length != clusterNames.length) {
			throw new IllegalArgumentException("Invalid cluster name array");
		}
		if (linkageStrategy == null) {
			throw new IllegalArgumentException("Undefined linkage strategy");
		}

		/* Setup model */
		List<Cluster> clusters = createClusters(clusterNames);
		List<ClusterPair> linkages = createLinkages(distances, clusters);

		/* Process */
		return new HierarchyBuilder(clusters, linkages);
	}

	protected List<ClusterPair> createLinkages(double[][] distances,
	        List<Cluster> clusters) {
		List<ClusterPair> linkages = new ArrayList<ClusterPair>();
		for (int col = 0; col < clusters.size(); col++) {
			for (int row = col + 1; row < clusters.size(); row++) {
				ClusterPair link = new ClusterPair();
				link.setLinkageDistance(distances[col][row]);
				link.setlCluster(clusters.get(col));
				link.setrCluster(clusters.get(row));
				linkages.add(link);
			}
		}
		return linkages;
	}

	protected List<Cluster> createClusters(String[] clusterNames) {
		List<Cluster> clusters = new ArrayList<Cluster>();
        for (String clusterName : clusterNames) {
            Cluster cluster = new Cluster(clusterName);
            clusters.add(cluster);
        }
		return clusters;
	}

}