/*
 * Created on Feb 12, 2005
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
import java.util.*;

/**
 * This class is for efficient finding of "corresponding" legs
 * It uses arrays for constant access to the possible legs 
 *
 * @author Marc Woerlein <marc.woerlein@gmx.de>
 */
public class LegSet {
    private Leg node_depth[][];
    private Leg cycle[][];
    private Collection node_depth_set,cycle_set;
    
    /**
     * creates a LegSet dimensioned for the given sizes
     * @param maxEdgeLabels
     * @param maxNodeLabels
     * @param maxNodes
     */
    public LegSet(int maxEdgeLabels, int maxNodeLabels, int maxNodes){
        node_depth=new Leg[maxEdgeLabels][maxNodeLabels];
        cycle=new Leg[maxEdgeLabels][maxNodes];
        node_depth_set=new ArrayList();
        cycle_set=new ArrayList();
    }
    
    /**
     * searches or creates a Leg in the LegSeg for a node refinement
     * @param fromNode
     * @param edgeLabel
     * @param nodeLabel
     * @return the Leg for the corresponding refinement
     */
    public final Leg getNode(int fromNode,int edgeLabel, int nodeLabel){
        Leg l=node_depth[edgeLabel][nodeLabel];
        if (l==null){
            l=new Leg(new Refinement(fromNode,edgeLabel,nodeLabel));
            node_depth[edgeLabel][nodeLabel]=l;
            node_depth_set.add(l);
        }
        return l;
    }
    
    /**
     * searches or creates a Leg in the LegSeg for a death refinement
     * @param depth
     * @param edgeLabel
     * @param nodeLabel
     * @return the Leg for the corresponding refinement
     */
    public final Leg getDepth(int depth,int edgeLabel, int nodeLabel){
        Leg l=node_depth[edgeLabel][nodeLabel];
        if (l==null){
            l=new Leg(new DepthRefinement(depth,edgeLabel,nodeLabel));
            node_depth[edgeLabel][nodeLabel]=l;
            node_depth_set.add(l);
        }
        return l;
    }
    
    /**
     * searches or creates a Leg in the LegSeg for a cycle refinement
     * @param fromNode
     * @param edgeLabel
     * @param toNode
     * @return the Leg for the corresponding refinement
     */
    public final Leg getCycle(int fromNode,int edgeLabel, int toNode){
        Leg l=cycle[edgeLabel][toNode];
        if (l==null){
            l=new Leg(new CycleRefinement(fromNode,edgeLabel,toNode));
            cycle[edgeLabel][toNode]=l;
            cycle_set.add(l);
        }
        return l;
    }
    
    /**
     * searches the LegSet for all frequent Legs, 
     * and reset the structure for futher use 
     * @param minFreq the minimum frequencies
     * @param ret the collection for inserting allowed Legs
     * @param max the current max suport oth the set
     * @return the maximal support with occors in the set
     */
    public final float[] clearAndGetFrequentLegs(float[] minFreq,Collection ret, float[] max){
        for (Iterator it=node_depth_set.iterator();it.hasNext();){
            Leg ack=(Leg) it.next();
            if (ack.isFrequent(minFreq)) {
                ret.add(ack);
                max=ack.getMax(max);
            }
            node_depth[ack.getRef().getEdgeLabel()][ack.getRef().getToLabel()]=null;
        }
        node_depth_set.clear();
        for (Iterator it=cycle_set.iterator();it.hasNext();){
            Leg ack=(Leg) it.next();
            if (ack.isFrequent(minFreq)) {
                ret.add(ack);
                max=ack.getMax(max);
            }
            cycle[ack.getRef().getEdgeLabel()][ack.getRef().getNodeB()]=null;
        }
        cycle_set.clear();
        return max;
    }

}
