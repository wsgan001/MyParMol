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

import de.parmol.graph.*;

/**
 * This class is for distinguish a Node Refinement from a cycle Refinement.
 *
 * @author Marc Woerlein <marc.woerlein@gmx.de>
 */
public class CycleRefinement extends Refinement {
	/**
	 * creates a new CycleRefinement
	 * @param nodeA node this refinement connects 
	 * @param edgeLabel the label of the new edge
	 * @param nodeB node this refinement connects
	 */
    public CycleRefinement(int nodeA, int edgeLabel, int nodeB) {
        super(nodeA, edgeLabel, nodeB);
    }
    /*
     *  (non-Javadoc)
     * @see de.parmol.Gaston.Refinement#getNodeB()
     */
    public int getNodeB(){ return nodeB; }
    /*
     *  (non-Javadoc)
     * @see de.parmol.Gaston.Refinement#getToLabel()
     */
    public int getToLabel(){ return Graph.NO_NODE; }
    
    /*
     *  (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object o){
        Refinement other=(Refinement) o;
        if (!other.isCycleRefinement()) return 1;
        if (other.nodeA!=nodeA) return other.nodeA-nodeA;
        if (other.nodeB!=nodeB) return other.nodeB-nodeB;
        return other.edgeLabel-edgeLabel;
    }
    /*
     *  (non-Javadoc)
     * @see de.parmol.Gaston.Refinement#isCycleRefinement()
     */
    public boolean isCycleRefinement(){ return true; }

}
