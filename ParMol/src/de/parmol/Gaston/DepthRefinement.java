/*
 * Created on Feb 21, 2005
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

/**
 * This class represents a Depth extension for Trees in the depth/edgeLabel/NodeLabel notation
 *
 * @author Marc Woerlein <marc.woerlein@gmx.de>
 */
public class DepthRefinement extends Refinement {
    protected DepthRefinement next; // the next Refinement in the DepthSequence 
    protected DepthRefinement right;// the rigthmost child of the represented node 
    								// = next Refinement on the rigth most path
    /**
     * creates a new DepthRefinement 
     * @param depth the depth of the new rfinement
     * @param edgeLabel the label of th e new edge
     * @param nodeLabel the label of the new node
     */
    public DepthRefinement(int depth, int edgeLabel, int nodeLabel){
        super(depth, edgeLabel, nodeLabel);
        next=right=null;
    }

    public int getNodeA(){ return -1; }
    public int getDepth(){ return nodeA; }
    public String toString(){
        return getDepth()+" "+getEdgeLabel()+" "+getNodeB()+"/"+getToLabel();
    }
    public int compareTo(Object o){
        Refinement other=(Refinement) o;
        if (other.isCycleRefinement()) return -1;
        if (other.nodeA!=nodeA) return nodeA-other.nodeA;
        if (other.edgeLabel!=edgeLabel) return edgeLabel-other.edgeLabel;
        return nodeB-other.nodeB;
    }

    /**
     * compares this refinement with a refinement with the given values
     * @param depth
     * @param edgeLabel
     * @param toLabel
     * @return <code> -1 </code> if this is lesser,<code> 1 </code> if greater else <code> 0 </code>
     */
    public int compareTo(int depth, int edgeLabel, int toLabel){
        if (this.nodeA!=depth) return this.nodeA-depth;
        if (this.edgeLabel!=edgeLabel) return this.edgeLabel-edgeLabel;
        return this.nodeB-toLabel;
    }
    
    /**
     * compares this refinement with an other, whoes depth is shiftet
     * @param other
     * @param cmov the number added to others depth
     * @return <code> -1 </code> if this is lesser,<code> 1 </code> if greater else <code> 0 </code>
     */
    public int compareTo(Refinement other, int cmov){
        if (other.isCycleRefinement()) return -1;
        if (other.nodeA+cmov!=nodeA) return nodeA-other.nodeA-cmov;
        if (other.edgeLabel!=edgeLabel) return edgeLabel-other.edgeLabel;
        return nodeB-other.nodeB;
    }

    /**
     * compares this refinement with a refinement with the given values
     * @param depth
     * @param edgeLabel
     * @param toLabel
     * @param cmov the number added to depth
     * @return <code> -1 </code> if this is lesser,<code> 1 </code> if greater else <code> 0 </code>
     */
    public int compareTo(int depth, int edgeLabel, int toLabel,int cmov){
        if (this.nodeA!=depth+cmov) return this.nodeA-depth-cmov;
        if (this.edgeLabel!=edgeLabel) return this.edgeLabel-edgeLabel;
        return this.nodeB-toLabel;
    }
}
