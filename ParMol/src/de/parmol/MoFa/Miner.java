/*
 * Created on Aug 16, 2004
 *  
 * This file is part of ParMol.
 * ParMol is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * ParMol is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with ParMol; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package de.parmol.MoFa;

import jackal.runtime.RuntimeSystem;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;

import de.parmol.AbstractMiner;
import de.parmol.Settings;
import de.parmol.graph.ClassifiedGraph;
import de.parmol.graph.Graph;
import de.parmol.graph.GraphEmbedding;
import de.parmol.graph.GraphFactory;
import de.parmol.graph.MutableGraph;
import de.parmol.graph.UndirectedMatrixGraph;
import de.parmol.graph.Util;
import de.parmol.parsers.GraphParser;
import de.parmol.search.SearchManager;


/**
 * This class is the user friendly shell around the MoFa algorithm. You can read in databases and search for frequent
 * discriminative fragments with it.
 * 
 * @author Thorsten Meinl <Thorsten.Meinl@informatik.uni-erlangen.de>
 *  
 */
public class Miner extends AbstractMiner {
	protected NodeFrequency[] m_nodeFrequencies;

	private GraphParser m_graphParser;

	/**
	 * Creates a new MoFa Miner.
	 * 
	 * @param settings
	 */
	public Miner(Settings settings) {
		super(settings);
	}
	
	/**
	 * Creates a new MoFa Miner
	 * 
	 * @param args the arguments for the MoFa miner which get passed to {@link Settings#Settings(String[])}
	 * @throws InstantiationException 
	 * @throws IllegalAccessException 
	 * @throws ClassNotFoundException 
	 */
	public Miner(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		this(new Settings(args));
	}

	private static class NodeFrequency {
		final int nodeLabel;
		final float[] classFrequencies;
		int supportedGraphs, occurences;


		NodeFrequency(int nodeLabel, int classes) {
			this.nodeLabel = nodeLabel;
			this.classFrequencies = new float[classes];
		}


		public boolean equals(Object obj) {
			return (((NodeFrequency) obj).nodeLabel == this.nodeLabel);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.parmol.AbstractMiner#getGraphFactory(de.parmol.graph.GraphParser)
	 */
	protected GraphFactory getGraphFactory(GraphParser parser) {
		int mask = parser.getDesiredGraphFactoryProperties() | GraphFactory.CLASSIFIED_GRAPH;

		return GraphFactory.getFactory(mask);
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see de.parmol.AbstractMiner#setUp()
	 */
	public void setUp() throws FileNotFoundException, IOException, ParseException {
		super.setUp();

		m_nodeFrequencies = computeNodeFrequencies(m_graphs);

		if (m_settings.ringSizes[0] > 2) {
			long t = 0;
			if (m_settings.debug > 0) {
				System.out.print("Marking rings...");
				t = System.currentTimeMillis();
			}

			for (Iterator it = m_graphs.iterator(); it.hasNext();) {
				RingGraph g = (RingGraph) it.next();
				try {
					g.markCycles(m_settings.ringSizes[0], m_settings.ringSizes[1]);
				} catch (Util.TooManyCyclesException ex) {
					ex.printStackTrace();
				}
			}

			if (m_settings.debug > 0) {
				System.out.println("done (" + (System.currentTimeMillis() - t) + "ms)");
			}
		}

		m_settings.graphCount = m_graphs.size();
	}


	/**
	 * Calculates the frequencies of each different node label in the graphs.
	 * 
	 * @param graphs the parsed graphs
	 * @return an array holding a NodeFrequency object for each node label that occurs in the graphs
	 */
	protected NodeFrequency[] computeNodeFrequencies(Collection graphs) {
		HashMap freqs = new HashMap(1024);
		final float[] TEMP = { 1.0f };

		HashSet labels = new HashSet(1024);
		for (Iterator it = graphs.iterator(); it.hasNext();) {
			Graph g = (Graph) it.next();

			for (int i = g.getNodeCount() - 1; i >= 0; i--) {
				Integer nodeLabel = new Integer(g.getNodeLabel(g.getNode(i)));

				NodeFrequency nf = (NodeFrequency) freqs.get(nodeLabel);
				if (nf == null) {
					nf = new NodeFrequency(nodeLabel.intValue(), m_settings.minimumClassFrequencies.length);
					freqs.put(nodeLabel, nf);
				}
				nf.occurences++;

				if (m_settings.countEmbeddings) {
					float[] cf = (g instanceof ClassifiedGraph) ? ((ClassifiedGraph) g).getClassFrequencies() : TEMP;
					for (int k = 0; k < cf.length; k++) {
						nf.classFrequencies[k] += cf[k];
					}
				} else {
					if (!labels.contains(nodeLabel)) {
						labels.add(nodeLabel);
						nf.supportedGraphs++;

						float[] cf = (g instanceof ClassifiedGraph) ? ((ClassifiedGraph) g).getClassFrequencies() : TEMP;
						for (int k = 0; k < cf.length; k++) {
							nf.classFrequencies[k] += cf[k];
						}
					}
				}
			}

			labels.clear();
		}

		NodeFrequency[] retVal = (NodeFrequency[]) freqs.values().toArray(new NodeFrequency[freqs.size()]);
		Arrays.sort(retVal, new Comparator() {
			public int compare(Object o1, Object o2) {
				return ((NodeFrequency) o1).occurences - ((NodeFrequency) o2).occurences;
			}
		});

		return retVal;
	}


	/**
	 * Starts the search.
	 */
	protected void startRealMining() {
		BlackNodeSet blackNodes = new BlackNodeSet(m_nodeFrequencies.length);

		m_settings.graphs = m_graphs;
		MoFaDFSSearch searcher = new MoFaDFSSearch(m_settings);
		SearchManager searchManager = getSearchManager(searcher);

		if (m_settings.seed != null) {
			try {
				Graph seed = m_settings.parser.parse(m_settings.seed, getGraphFactory(m_graphParser));

				long t = System.currentTimeMillis();
				if (m_settings.debug > 1) {
					System.out.print("Creating seed embeddings...");
				}

				final ArrayList completeEmbeddings = new ArrayList(1024);
				CompleteMoFaEmbedding.getCompleteEmbeddings(seed, m_graphs, completeEmbeddings);
				if (m_settings.debug > 1) {
					System.out.println("done in " + (System.currentTimeMillis() - t) + "ms, found " + completeEmbeddings.size()
							+ " embeddings");
				}

				final float[] classFrequencies = computeClassFrequencies(completeEmbeddings);
				MoFaSearchTreeNode startNode = new MoFaSearchTreeNode(seed, classFrequencies, completeEmbeddings,
						completeEmbeddings.size(), Graph.NO_NODE, false, blackNodes, 1);
				searchManager.addStartNode(startNode);

			} catch (ParseException e) {
				System.err.println("Could not parse seed '" + m_settings.seed + "'");
				e.printStackTrace();
				return;
			}
		} else {
			outer: for (int i = 0; i < m_nodeFrequencies.length; i++) {
				if (!m_settings.checkMinimumFrequencies(m_nodeFrequencies[i].classFrequencies)) {
					blackNodes.addNodeLabel(m_nodeFrequencies[i].nodeLabel);
					continue;
				}

				for (int k = 0; k < m_settings.ignoreSeeds.length; k++) {
					if (m_nodeFrequencies[i].nodeLabel == m_settings.ignoreSeeds[k]) continue outer;
				}


				MutableGraph oneNodeGraph = new UndirectedMatrixGraph();
				oneNodeGraph.addNode(m_nodeFrequencies[i].nodeLabel);
				MoFaSearchTreeNode startNode = new MoFaSearchTreeNode(oneNodeGraph, m_nodeFrequencies[i].classFrequencies,
						null, m_nodeFrequencies[i].occurences, Graph.NO_NODE, false, blackNodes, 1);
				searchManager.addStartNode(startNode);
				blackNodes = new BlackNodeSet(blackNodes);
				blackNodes.addNodeLabel(m_nodeFrequencies[i].nodeLabel);
			}
		}

		if (m_settings.debug > 3) RuntimeSystem.java_reset_stats("");
		searchManager.startSearch();
		if (m_settings.debug > 3) RuntimeSystem.java_print_stats();

		m_frequentSubgraphs = searcher.getFrequentSubgraphs();
	}

	private final static float[] TEMP = { 1.0f };


	protected float[] computeClassFrequencies(Collection embeddings) {
		float[] frequencies = new float[m_settings.minimumClassFrequencies.length];

		if (m_settings.countEmbeddings) {
			for (Iterator it = embeddings.iterator(); it.hasNext();) {
				GraphEmbedding emb = (GraphEmbedding) it.next();

				Graph g = emb.getSuperGraph();
				float[] temp = (g instanceof ClassifiedGraph) ? ((ClassifiedGraph) g).getClassFrequencies() : TEMP;
				for (int i = 0; i < temp.length; i++) {
					frequencies[i] += temp[i];
				}
			}
		} else {
			IdentityHashMap graphs = new IdentityHashMap((int) (embeddings.size() * 0.7));

			for (Iterator it = embeddings.iterator(); it.hasNext();) {
				GraphEmbedding emb = (GraphEmbedding) it.next();

				Graph g = emb.getSuperGraph();
				if (graphs.get(g) == null) {
					graphs.put(g, g);
					float[] temp = (g instanceof ClassifiedGraph) ? ((ClassifiedGraph) g).getClassFrequencies() : TEMP;
					for (int i = 0; i < temp.length; i++) {
						frequencies[i] += temp[i];
					}
				}
			}
		}
		return frequencies;
	}


	/**
	 * Well, thats the main method...
	 * @param args
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ParseException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 */
	public static void main(String[] args) throws FileNotFoundException, IOException, ParseException,
			InstantiationException, IllegalAccessException, ClassNotFoundException {
		if ((args.length == 0) || args[0].equals("--help")) {
			System.out.println("Usage: " + Miner.class.getName() + " options, where options are:\n");
			Settings.printUsage();
			System.exit(1);
		}

		Settings s = new Settings(args);
		if (s.directedSearch) {
			System.out.println(Miner.class.getName()+" does not implement the search for directed graphs");
			System.exit(1);
		}
		Miner m = new Miner(s);

		m.setUp();
		m.startMining();
		m.printFrequentSubgraphs();
	}

	protected void readGraphs(InputStream in, GraphParser parser) throws IOException, ParseException {
		m_graphParser = parser;
		super.readGraphs(in, parser);
	}
	
	
}