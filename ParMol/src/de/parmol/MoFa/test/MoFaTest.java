/*
 * Created on 12.12.2004
 *  
 */
package de.parmol.MoFa.test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.Iterator;

import junit.framework.TestCase;
import de.parmol.Settings;
import de.parmol.MoFa.Miner;
import de.parmol.graph.Graph;
import de.parmol.graph.SimpleEdgeComparator;
import de.parmol.graph.SimpleNodeComparator;
import de.parmol.graph.SimpleSubgraphComparator;
import de.parmol.parsers.SLNParser;
import de.parmol.util.FragmentSet;
import de.parmol.util.FrequentFragment;


/**
 * @author Thorsten.Meinl@informatik.uni-erlangen.de
 *  
 */
public class MoFaTest extends TestCase {
	public MoFaTest() {
		super();
	}


	public MoFaTest(String name) {
		super(name);
	}



	public void testIC93() throws FileNotFoundException, IOException, ParseException, InstantiationException,
			IllegalAccessException, ClassNotFoundException {
		Settings settings = new Settings(new String[0]);
		settings.minimumClassFrequencies[0] = 40;
		settings.closedFragmentsOnly = false;
		settings.graphFile = "data/IC93.test";
		settings.debug = 0;
		settings.maxThreads = 1;

		Miner miner = new Miner(settings);
		miner.setUp();

		miner.startMining();
		FragmentSet fragments = miner.getFrequentSubgraphs();

		assertEquals(fragments.size(), 61842);


		SimpleSubgraphComparator comp = new SimpleSubgraphComparator(SimpleNodeComparator.instance,
				SimpleEdgeComparator.instance);
		for (Iterator it = fragments.iterator(); it.hasNext();) {
			FrequentFragment frag = (FrequentFragment) it.next();

			System.out.println("Checking " + SLNParser.instance.serialize(frag.getFragment()));

			int count = 0;
			for (Iterator it2 = miner.getGraphs().iterator(); it2.hasNext();) {
				Graph g = (Graph) it2.next();

				if (comp.compare(frag.getFragment(), g) == 0) count++;
			}

			assertEquals((int) frag.getClassFrequencies()[0], count);
		}
	}


	public void testPerfectExtensionPruning() throws FileNotFoundException, IOException, ParseException,
			InstantiationException, IllegalAccessException, ClassNotFoundException {
		Settings settings = new Settings(new String[0]);
		settings.minimumClassFrequencies[0] = 100;
		settings.closedFragmentsOnly = true;
		settings.graphFile = "data/IC93.test";
		settings.debug = 0;
		settings.maxThreads = 1;

		Miner miner = new Miner(settings);
		miner.setUp();
		miner.startMining();
		FragmentSet fragments = miner.getFrequentSubgraphs();

		assertEquals(fragments.size(), 836);

		SimpleSubgraphComparator comp = new SimpleSubgraphComparator(SimpleNodeComparator.instance,
				SimpleEdgeComparator.instance);

		for (Iterator it = fragments.iterator(); it.hasNext();) {
			FrequentFragment frag = (FrequentFragment) it.next();

			System.out.println("Checking " + SLNParser.instance.serialize(frag.getFragment()));

			int count = 0;
			for (Iterator it2 = miner.getGraphs().iterator(); it2.hasNext();) {
				Graph g = (Graph) it2.next();

				if (comp.compare(frag.getFragment(), g) == 0) count++;
			}

			assertEquals((int) frag.getClassFrequencies()[0], count);
		}
	}
}