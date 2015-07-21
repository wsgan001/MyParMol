/*
 * Created on 12.12.2004
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

import de.parmol.graph.ClassifiedDirectedListGraph;
import de.parmol.graph.ClassifiedGraph;
import de.parmol.graph.ClassifiedUndirectedListGraph;
import de.parmol.graph.MutableGraph;
import de.parmol.graph.Util;

/**
 * This class represents graphs that are classified, directed, hold information about existing rings and use adjacency lists.
 * 
 * @author Thorsten Meinl <Thorsten.Meinl@informatik.uni-erlangen.de>
 *
 */
public class DirectedListRingGraph extends ClassifiedDirectedListGraph implements RingGraph {
	protected int[] m_edgeRingMembership;
	
	/**
	 * Creates a new empty DirectedListRingGraph with the given class frequencies.
	 * 
	 * @param classFrequencies the frequencies of this graph in the different classes
	 */
	public DirectedListRingGraph(float[] classFrequencies) {
		super(classFrequencies);
		m_edgeRingMembership = new int[DEFAULT_SIZE];
	}


	/**
	 * Creates a new empty DirectedListRingGraph with the given id and class frequencies. 
	 * 
	 * @param id an id for the new graph
	 * @param classFrequencies the frequencies of this graph in the different classes
	 */
	public DirectedListRingGraph(String id, float[] classFrequencies) {
		super(id, classFrequencies);
		m_edgeRingMembership = new int[DEFAULT_SIZE];
	}


	/**
	 * Creates a new DirectedListRingGraph that is a copy of the given template graph.
	 * 
	 * @param template the graph that should be copied
	 */
	public DirectedListRingGraph(DirectedListRingGraph template) {
		super(template);
		m_edgeRingMembership = new int[template.m_edgeRingMembership.length];
		System.arraycopy(template.m_edgeRingMembership, 0, m_edgeRingMembership, 0, m_edgeRingMembership.length);
	}

	/* (non-Javadoc)
	 * @see de.parmol.MoFa.RingGraph#getEdgeRingMembership(int)
	 */
	public int getEdgeRingMembership(int edge) {
		return m_edgeRingMembership[edge];
	}


	/* (non-Javadoc)
	 * @see de.parmol.MoFa.RingGraph#setEdgeRingMembership(int, int)
	 */
	public void setEdgeRingMembership(int edge, int ringID) {
		if ((ringID < 0) || (ringID > 31)) throw new IllegalArgumentException("The ring id must be a value between 0 and 31");

		m_edgeRingMembership[edge] |= (1 << ringID);
	}

	/*
	 *  (non-Javadoc)
	 * @see de.parmol.graph.ListGraph#resizeGraph(int, int)
	 */
	protected void resizeGraph(int newNodeCount, int newEdgeCount) {
		super.resizeGraph(newNodeCount, newEdgeCount);
		
		if (newEdgeCount >= m_edgeRingMembership.length) {
			int[] temp = m_edgeRingMembership;
			m_edgeRingMembership = new int[newEdgeCount];
			System.arraycopy(temp, 0, m_edgeRingMembership, 0, temp.length);
		}
	}

	/* (non-Javadoc)
	 * @see de.parmol.MoFa.RingGraph#markCycles(int, int)
	 */
	public int markCycles(int minRingSize, int maxRingSize) throws Util.TooManyCyclesException {
		return Util.markCycles(this, minRingSize, maxRingSize);
	}	
	
	/**
	 * This factory creates new DirectedListRingGraphs.
	 * 
	 * @author Thorsten Meinl <Thorsten.Meinl@informatik.uni-erlangen.de>
	 */
	public static class Factory extends ClassifiedUndirectedListGraph.Factory {	
		/**
		 * The single instance of this factory.
		 */
		public final static Factory instance = new Factory();
		
		/*
		 *  (non-Javadoc)
		 * @see de.parmol.graph.GraphFactory#createGraph()
		 */
		public MutableGraph createGraph() {
			return new DirectedListRingGraph(DEFAULT_CLASS);
		}
		
		/*
		 *  (non-Javadoc)
		 * @see de.parmol.graph.ClassifiedGraphFactory#createGraph(float[])
		 */
		public ClassifiedGraph createGraph(float[] classFrequencies) {
			return new DirectedListRingGraph(classFrequencies);
		}
		
		/*
		 *  (non-Javadoc)
		 * @see de.parmol.graph.ClassifiedGraphFactory#createGraph(java.lang.String, float[])
		 */
		public ClassifiedGraph createGraph(String id, float[] classFrequencies) {
			if (id != null) return new DirectedListRingGraph(id, classFrequencies);
			return new DirectedListRingGraph(classFrequencies);
		}
		
		/*
		 *  (non-Javadoc)
		 * @see de.parmol.graph.GraphFactory#createGraph(java.lang.String)
		 */
		public MutableGraph createGraph(String id) {
			if (id != null) return new UndirectedListRingGraph(id, DEFAULT_CLASS);
			return new DirectedListRingGraph(DEFAULT_CLASS);
		}
	}
}
