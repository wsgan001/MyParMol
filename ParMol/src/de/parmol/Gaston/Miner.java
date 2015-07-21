/*
 * Created on Oct 30, 2004
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
package de.parmol.Gaston;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.ParseException;
import java.util.Iterator;

import de.parmol.AbstractMiner;
import de.parmol.Settings;
import de.parmol.graph.GraphFactory;
import de.parmol.graph.UndirectedListGraph;
import de.parmol.parsers.GraphParser;
import de.parmol.util.Debug;
import de.parmol.util.FrequentFragment;

/**
 * This class represents the Mining algorithm Gaston
 *
 * @author Marc Woerlein <marc.woerlein@gmx.de>
 */
public class Miner extends AbstractMiner {
    
    /**
     * Creates a new Gaston Miner
     * @param settings
     */
    public Miner(Settings settings){
        super(settings);
        this.m_frequentSubgraphs=new GastonSet();
        Leg.length=m_settings.minimumClassFrequencies.length;
        if (m_settings.closedFragmentsOnly){
            Leg.empty=new float[Leg.length];
            for (int i=0;i<Leg.length;i++) Leg.empty[i]=0;
        } else Leg.empty=null;

        Debug.dlevel=m_settings.debug;
    }
    
    /* (non-Javadoc)
	 * @see de.parmol.AbstractMiner#getGraphFactory(de.parmol.graph.GraphParser)
	 */
	protected GraphFactory getGraphFactory(GraphParser parser) {
		int mask = parser.getDesiredGraphFactoryProperties() | GraphFactory.CLASSIFIED_GRAPH;
		if (m_settings.ringSizes[0] > 2) mask |= GraphFactory.RING_GRAPH;
		return GraphFactory.getFactory(mask);
	}
	
    
    /*
     *  (non-Javadoc)
     * @see de.parmol.AbstractMiner#startMining()
     */
    public void startRealMining(){
        
        long start=System.currentTimeMillis();
        Debug.print(1,"renaming DataBase ... ");
        DataBase db=new DataBase(m_graphs,m_settings.minimumClassFrequencies,getGraphFactory(m_settings.parser));
        GastonPath path=new GastonPath(UndirectedListGraph.Factory.instance.createGraph(),
                db,db.getLegSet(),(GastonSet)m_frequentSubgraphs,m_settings.minimumClassFrequencies,
                m_settings.findPathsOnly,!m_settings.findPathsOnly && !m_settings.findTreesOnly, m_settings);
        Leg[] fl=db.getFreqLegs();
        Debug.println(1,"done ("+(System.currentTimeMillis()-start)+" ms)");

        for (int i=0;i<fl.length;i++){
            path.findPaths(fl[i]);
        }
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
    public static void main(String[] args) 
       throws FileNotFoundException, IOException, ParseException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		if ((args.length == 0) || args[0].equals("--help")) {
			System.out.println("Usage: " + AbstractMiner.class.getName() + " options, where options are:\n");
			Settings.printUsage();
			System.exit(1);
		}
		
//        long startTime=System.currentTimeMillis();
		Settings s = new Settings(args);
		if (s.directedSearch) {
			System.out.println(Miner.class.getName()+" does not implement the search for directed graphs");
			System.exit(1);
		}
		Miner m = new Miner(s);
		m.setUp();
		m.startMining();
		m.printFrequentSubgraphs();		

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
