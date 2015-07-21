/*
 * Created on Dec 11, 2004
 * 
 * Copyright 2004, 2005 Marc Wörlein
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
 * 
 */
package de.parmol.GSpan;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.ParseException;
import java.util.Iterator;

import de.parmol.AbstractMiner;
import de.parmol.Settings;
import de.parmol.graph.GraphFactory;
import de.parmol.parsers.*;
import de.parmol.util.*;


//
/**
 * This class represents the Mining algorithm gSpan
 * 
 * @author Marc Woerlein <marc.woerlein@gmx.de>
 */
public class Miner extends AbstractMiner {


	//    PrintStream debug;
	GraphFactory factory = GraphFactory.getFactory(GraphFactory.LIST_GRAPH | GraphFactory.UNDIRECTED_GRAPH);


	/** 
	 * create a new Miner
	 * @param settings
	 */
	public Miner(Settings settings) {
		super(settings);
		this.m_frequentSubgraphs = new FragmentSet();
		GraphSet.length = settings.minimumClassFrequencies.length;
		empty = new float[settings.minimumClassFrequencies.length];
		Debug.out = System.out;
		Debug.dlevel = m_settings.debug;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see de.parmol.AbstractMiner#getGraphFactory(de.parmol.graph.GraphParser)
	 */
	protected GraphFactory getGraphFactory(GraphParser parser) {
		int mask = parser.getDesiredGraphFactoryProperties() | GraphFactory.CLASSIFIED_GRAPH;
		if (m_settings.ringSizes[0] > 2) mask |= GraphFactory.RING_GRAPH;
		return GraphFactory.getFactory(mask);
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see de.parmol.AbstractMiner#startMining()
	 */
	protected void startRealMining() {
		long start = System.currentTimeMillis();
		Debug.print(1, "renaming DataBase ... ");
		DataBase gs = new DataBase(m_graphs, m_settings.minimumClassFrequencies, m_frequentSubgraphs, factory);
		Debug.println(1, "done (" + (System.currentTimeMillis() - start) + " ms)");

		Debug.println(1, "minSupport: " + m_settings.minimumClassFrequencies[0]);
		Debug.println(1, "graphs    : " + m_graphs.size());

		graphSet_Projection(gs);
	}


	/**
	 * searches Subgraphs for each freqent edge in the DataBase
	 * @param gs
	 */
	private void graphSet_Projection(DataBase gs) {
		for (Iterator eit = gs.frequentEdges(); eit.hasNext();) {
			GSpanEdge edge = (GSpanEdge) eit.next();
			DFSCode code = new DFSCode(edge, gs); //create DFSCode for the
			// current edge
			long time = System.currentTimeMillis();
			Debug.print(1, "doing seed " + m_settings.serializer.serialize(code.toFragment().getFragment()) + " ...");
			Debug.println(2,"");
			subgraph_Mining(code); //recursive search
			eit.remove(); //shrink database
			Debug.println(1, "\tdone (" + (System.currentTimeMillis() - time) + " ms)");
			if (gs.size() < m_settings.minimumClassFrequencies[0] && gs.size() != 0) { //not needed
				Debug.println("remaining Graphs: " + gs.size());
				Debug.println("May not happen!!!");
				return;
			}
		}
		Debug.println(2, "remaining Graphs: " + gs.size());
	}

	private static float[] empty;


	private float[] getMax(float[] a, float[] b) {
		for (int i = 0; i < a.length; i++) {
			if (a[i] > b[i]) return a;
			if (b[i] > a[i]) return b;
		}
		return a;
	}


	private boolean unequal(float[] a, float[] b) {
		for (int i = 0; i < a.length; i++) {
			if (a[i] != b[i]) return true;
		}
		return false;
	}


	/**
	 * recursive search for frequent Subgraphs
	 * 
	 * @param code the DFSCode with is found and checked for childs
	 * @return the highest occuring frequency, of this branch  
	 */
	private float[] subgraph_Mining(DFSCode code) {
		if (!code.isMin()) {
			Debug.println(2, code.toString(m_settings.serializer)+" not min");
			m_settings.stats.duplicateFragments++;
			return empty;
		}
		float[] max = empty;

		float[] my = code.getFrequencies();
		Debug.println(2, "  found graph " + code.toString(m_settings.serializer));

		Iterator it = code.childIterator(m_settings.findTreesOnly, m_settings.findPathsOnly);
		for (; it.hasNext();) {
			DFSCode next = (DFSCode) it.next();
			if (next.isFrequent(m_settings.minimumClassFrequencies)) {
				float[] a = subgraph_Mining(next);
				max = getMax(max, a);
			}
		}
		if ((!m_settings.closedFragmentsOnly || max == empty || unequal(my, max))
				&& m_settings.checkReportingConstraints(code.getSubgraph(), code.getFrequencies())) {
			m_frequentSubgraphs.add(code.toFragment());
		} else {
			m_settings.stats.earlyFilteredNonClosedFragments++;
		}
		return my;
	}


	/**
     * The main program, for starting a mine-prozess
     * @param args parameters parsable by de.parmol.Settings 
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
			System.out.println("Usage: " + AbstractMiner.class.getName() + " options, where options are:\n");
			Settings.printUsage();
			System.exit(1);
		}

//		PrintStream out = System.out;

//		long startTime = System.currentTimeMillis();
		Settings s = new Settings(args);
		if (s.directedSearch) {
			System.out.println(Miner.class.getName()+" does not implement the search for directed graphs");
			System.exit(1);
		}
		Miner m = new Miner(s);
		m.setUp();
		m.startMining();
		if (Debug.dlevel < 0) Debug.println("" + m.m_frequentSubgraphs.size());
		if (s.outputFile != null) {
			PrintStream output;

			if (s.outputFile.equals("-")) {
				output = System.out;
			} else {
				output = new PrintStream(new FileOutputStream(s.outputFile));
			}
			for (Iterator it = m.m_frequentSubgraphs.iterator(); it.hasNext();) {
				output.println(((FrequentFragment) it.next()).toString(s.serializer));
			}
		}
	}

}