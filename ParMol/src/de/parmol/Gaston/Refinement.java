/*
 * Created on Feb 15, 2005
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

import de.parmol.graph.Graph;

/**
 * This class representates a refinement for teh Gaston Algorithm
 * it also defines an order on the refinements.
 *
 * @author Marc Woerlein <marc.woerlein@gmx.de>
 */
public class Refinement implements Comparable{
    protected int nodeA,nodeB,edgeLabel;
    /**
     * creates a new initial Refinement for a first Node
     * @param toLabel
     */
    public Refinement(int toLabel){
        this(Graph.NO_NODE,Graph.NO_EDGE,toLabel);
    }
    /**
     * creates a node Refinement
     * @param nodeA the node the refinements is connected to
     * @param edgeLabel the label of the new edge
     * @param toLabel the label of the new node
     */
    public Refinement(int nodeA,int edgeLabel, int toLabel){
        this.nodeA=nodeA;
        this.edgeLabel=edgeLabel;
        this.nodeB=toLabel;
    }
    /**
     * @return the number of the node A
     */
    public int getNodeA(){ return nodeA; }
    /**
     * @return the number of the node B 
     * (for cycle refinements else <code> Graph.NO_NODE </code>)
     */
    public int getNodeB(){ return Graph.NO_NODE; }
    /**
     * @return the label of the new node (<code> Graph.NO_NODE </code> for cycles)
     */
    public int getToLabel(){ return nodeB; }
    /**
     * @return the edge label
     */
    public int getEdgeLabel(){ return edgeLabel; }
    /**
     * @return the depth of the DepthRefinements (else -1)
     */
    public int getDepth(){ return -1; }
    /**
     * @return <code> true </code> if this refinement is a CycleRefinement
     */
    public boolean isCycleRefinement(){ return false; }
    
    /*
     *  (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object o){
        Refinement other=(Refinement) o;
        if (other.isCycleRefinement()) return -1;
        if (other.nodeA!=nodeA) return nodeA-other.nodeA;
        if (other.edgeLabel!=edgeLabel) return edgeLabel-other.edgeLabel;
        return nodeB-other.nodeB;
    }
    
    public String toString(){
        return getNodeA()+" "+getEdgeLabel()+" "+getNodeB()+"/"+getToLabel();
    }
    

}
