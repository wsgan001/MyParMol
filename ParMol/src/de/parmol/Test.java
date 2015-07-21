package de.parmol;

import de.parmol.GSpan.Miner;

public class Test {
	public static void main(String[] args) throws Exception{
		String graphFile = "test/dot.dot";
		String outputFile = "-";
		String minimumFrequencies = "0.1";
		Settings settings = new Settings(new String[] {
				"-graphFile=" + graphFile, "-outputFile=" + outputFile,
				"-minimumFrequencies=" + minimumFrequencies });
		Miner miner = new Miner(settings);
		miner.setUp();
		miner.startMining();
		miner.printFrequentSubgraphs();
		System.out.println(miner.m_graphs.size());
	}
}
