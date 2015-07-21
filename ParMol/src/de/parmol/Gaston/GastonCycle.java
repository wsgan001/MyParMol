/*
 * Created on Mar 5, 2005
 * 
 * Copyright 2005 Marc Wörlein
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

import de.parmol.graph.*;
import de.parmol.Settings;
import java.util.*;

/**
 * This class implements the recursive search for cyclic Graphs in the Gaston algorithm.
 *
 * @author Marc Woerlein <marc.woerlein@gmx.de>
 */
public class GastonCycle {
    private EdgeNodeRelabler enr;
    private GastonSet fragments;
    private float[] minFreq;
    private Settings m_settings;
    
    /**
     * creates a new object, reposible for the search of cyclic graphs
     * @param relabel for relabeling database labels to real labels
     * @param fragments the set for found fragments
     * @param minFreq the minimum frequencies
     * @param settings the whole settings for this run
     */
    public GastonCycle(EdgeNodeRelabler relabel, GastonSet fragments,float[] minFreq,Settings settings){
        this.enr=relabel;
        this.fragments=fragments;
        this.minFreq=minFreq;
	    this.m_settings=settings;
    }
    
    /**
     * extends the given cyclic Graph recursivly with all given Legs
     * @param cyclic
     * @param legs
     */
    private void findCycles(MutableGraph cyclic, Collection legs){
        for (Iterator it=legs.iterator();it.hasNext();){
            Leg ack=(Leg)it.next();
            int edge=cyclic.addEdge(ack.getRef().getNodeA(),ack.getRef().getNodeB(),enr.getRealEdgeLabel(ack.getRef().getEdgeLabel()));

            //only if this graph is not created earlyer
            if (fragments.filteredadd(ack.getFragment((Graph)cyclic.clone()))){
                Collection newLegs=new ArrayList();
                //search for allowable Legs in the given Set
                for (Iterator lit=legs.iterator();lit.hasNext();){
                    Leg ackl=(Leg)lit.next();
                    if (ackl.compareTo(ack)>=0){
                        Leg next=ackl.join(ack);
                        if (next!=null && next.isFrequent(minFreq)) newLegs.add(next);
                    }
                }
                //do further possible extensions
                findCycles(cyclic,newLegs);
            } else {
		 m_settings.stats.duplicateFragments++;
	    }
            cyclic.removeEdge(edge);
        }
    }
    
    /**
     * starts the search for all cyclic graphs for the given tree 
     * @param tree the graph representation of the current tree 
     * @param cycle the initial cycle closing leg
     * @param otherLegs the set of other possible extensions
     */
    public void findCycles(MutableGraph tree, Leg cycle, Collection otherLegs){
        int edge=tree.addEdge(cycle.getRef().getNodeA(),cycle.getRef().getNodeB(),enr.getRealEdgeLabel(cycle.getRef().getEdgeLabel()));
        
        //only if this graph is not created earlyer
        if (fragments.filteredadd(cycle.getFragment((Graph)tree.clone()))){
            Collection newLegs=new TreeSet();
            //search for allowable Legs in the given Set
            for (Iterator lit=otherLegs.iterator();lit.hasNext();){
                Leg ackl=(Leg)lit.next();
                if (ackl.getRef().isCycleRefinement() && ackl.compareTo(cycle)>=0){
                    Leg next=ackl.join(cycle);
                    if (next!=null && next.isFrequent(minFreq)) {
                        newLegs.add(next);
                    }
                }
            }
            //do all other extensions
            findCycles(tree,newLegs);
        }
        
        tree.removeEdge(edge);
    }

}
