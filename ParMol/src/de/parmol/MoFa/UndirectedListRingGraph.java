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
import de.parmol.graph.ClassifiedUndirectedListGraph;
import de.parmol.graph.MutableGraph;
import de.parmol.graph.UndirectedListGraph;
import de.parmol.graph.Util;


/**
 * @author Thorsten Meinl <Thorsten.Meinl@informatik.uni-erlangen.de>
 *  
 */
public class UndirectedListRingGraph extends ClassifiedUndirectedListGraph implements RingGraph {
	protected int[] m_edgeRingMembership;


	/**
	 * Creates a new empty UndirectedListRingGraph with the given class frequencies and a unique id.
	 * 
	 * @param classFrequencies the frequencies of the graph in the different classes of the database
	 */
	public UndirectedListRingGraph(float[] classFrequencies) {
		super(classFrequencies);
		m_edgeRingMembership = new int[DEFAULT_SIZE];
	}


	/**
	 * Creates a new empty UndirectedListRingGraph with the given id and class frequencies.
	 * 
	 * @param id the id of the graph
	 * @param classFrequencies the frequencies of the graph in the different classes of the database
	 */
	public UndirectedListRingGraph(String id, float[] classFrequencies) {
		super(id, classFrequencies);
		m_edgeRingMembership = new int[DEFAULT_SIZE];
	}


	/**
	 * Creates a new UndirectedListRingGraph from the given UndirectedListGraph. The edge ring membership is also copied.
	 * 
	 * @param template the graph that should be copied
	 */
	public UndirectedListRingGraph(UndirectedListRingGraph template) {
		super(template);
		m_edgeRingMembership = new int[template.m_edgeRingMembership.length];
		System.arraycopy(template.m_edgeRingMembership, 0, m_edgeRingMembership, 0, m_edgeRingMembership.length);
	}


	/**
	 * Creates a new UndirectedListRingGraph from the given UndirectedListGraph. No edge has any ring information yet, of
	 * course.
	 * 
	 * @param template the graph that should be copied
	 */
	public UndirectedListRingGraph(UndirectedListGraph template) {
		super(template);
		m_edgeRingMembership = new int[m_edgeCount];
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see de.parmol.MoFa.RingGraph#getEdgeRingMembership(int)
	 */
	public int getEdgeRingMembership(int edge) {
		return m_edgeRingMembership[edge];
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see de.parmol.MoFa.RingGraph#setEdgeRingMembership(int, int)
	 */
	public void setEdgeRingMembership(int edge, int ringID) {
		if ((ringID < 0) || (ringID > 31))
				throw new IllegalArgumentException("The ring id must be a value between 0 and 31");

		m_edgeRingMembership[edge] |= (1 << ringID);
	}


	protected void resizeGraph(int newNodeCount, int newEdgeCount) {
		super.resizeGraph(newNodeCount, newEdgeCount);

		if (newEdgeCount >= m_edgeRingMembership.length) {
			int[] temp = m_edgeRingMembership;
			m_edgeRingMembership = new int[newEdgeCount];
			System.arraycopy(temp, 0, m_edgeRingMembership, 0, temp.length);
		}
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see de.parmol.MoFa.RingGraph#markCycles(int, int)
	 */
	public int markCycles(int minRingSize, int maxRingSize) throws Util.TooManyCyclesException {
		return Util.markCycles(this, minRingSize, maxRingSize);
	}

	/**
	 * This class is a factory for classified undirected list-based graphs.
	 * 
	 * @author Thorsten Meinl <Thorsten.Meinl@informatik.uni-erlangen.de>
	 */
	public static class Factory extends ClassifiedUndirectedListGraph.Factory {
		/**
		 * An instance of the factory.
		 */
		public final static Factory instance = new Factory();


		public MutableGraph createGraph() {
			return new UndirectedListRingGraph(DEFAULT_CLASS);
		}


		public ClassifiedGraph createGraph(float[] classFrequencies) {
			return new UndirectedListRingGraph(classFrequencies);
		}


		public ClassifiedGraph createGraph(String id, float[] classFrequencies) {
			if (id != null) return new UndirectedListRingGraph(id, classFrequencies);
			return new UndirectedListRingGraph(classFrequencies);
		}


		public MutableGraph createGraph(String id) {
			if (id != null) return new UndirectedListRingGraph(id, DEFAULT_CLASS);
			return new UndirectedListRingGraph(DEFAULT_CLASS);
		}
	}


}