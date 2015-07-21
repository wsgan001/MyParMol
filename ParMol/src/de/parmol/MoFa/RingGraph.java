/*
 * Created on Oct 4, 2004
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

import de.parmol.graph.ClassifiedGraph;
import de.parmol.graph.Graph;
import de.parmol.graph.Util;

/**
 * This interface describe a graph whose edges are specially marked if they occur in a ring (or cycle). 
 * 
 * @author Thorsten Meinl <Thorsten.Meinl@informatik.uni-erlangen.de>
 *
 */
public interface RingGraph extends Graph, ClassifiedGraph {
	/**
	 * The number of maximum cycles that can be marked.
	 */
	public final static int MAX_CYCLES = 32;

	/**
	 * Returns the rings in which the given egde is part of. Each set bit in the return value says that the edge is part of the ring
	 * with the id given by the bit number. 
	 * @param edge the edge for which the ring membership should be returned
	 * @return a bitmask with the ring membership
	 */
	public int getEdgeRingMembership(int edge);
	
	/**
	 * Adds the given edge to a ring. The ringID must be a value between 0 and 31 indicating the number of the ring.
	 * @param edge the edge which should be added to a ring
	 * @param ringID the id of the ring
	 */
	public void setEdgeRingMembership(int edge, int ringID);
	
	/**
	 * Marks the edges of all minimal cycles in this graph.
	 * @param minRingSize the minimal size of a cycle
	 * @param maxRingSize the maximal size of a cycle
	 * @return the number of marked cycles
	 * @throws Util.TooManyCyclesException if the graph contained too many cycles
	 */
	public int markCycles(int minRingSize, int maxRingSize) throws Util.TooManyCyclesException;
}
