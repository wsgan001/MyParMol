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
import de.parmol.graph.ClassifiedUndirectedMatrixGraph;
import de.parmol.graph.MutableGraph;
import de.parmol.graph.Util;


/**
 * @author THorsten Meinl <Thorsten.Meinl@informatik.uni-erlangen.de>
 *  
 */
public class UndirectedMatrixRingGraph extends ClassifiedUndirectedMatrixGraph implements RingGraph {
	protected int[] m_edgeRingMembership;


	/**
	 * @param classFrequencies
	 */
	public UndirectedMatrixRingGraph(float[] classFrequencies) {
		super(classFrequencies);
		m_edgeRingMembership = new int[DEFAULT_SIZE];
	}


	/**
	 * @param id
	 * @param classFrequencies
	 */
	public UndirectedMatrixRingGraph(String id, float[] classFrequencies) {
		super(id, classFrequencies);
		m_edgeRingMembership = new int[DEFAULT_SIZE];
	}


	/**
	 * @param template
	 */
	public UndirectedMatrixRingGraph(UndirectedMatrixRingGraph template) {
		super(template);
		m_edgeRingMembership = new int[template.m_edgeRingMembership.length];
		System.arraycopy(template.m_edgeRingMembership, 0, m_edgeRingMembership, 0, m_edgeRingMembership.length);
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
	 * This class is a factory for classified undirected matrix-based graphs.
	 * 
	 * @author Thorsten Meinl <Thorsten.Meinl@informatik.uni-erlangen.de>
	 */

	public static class Factory extends ClassifiedUndirectedMatrixGraph.Factory {
		/**
		 * An instance of the factory.
		 */
		public final static Factory instance = new Factory();


		public MutableGraph createGraph() {
			return new UndirectedMatrixRingGraph(DEFAULT_CLASS);
		}


		public ClassifiedGraph createGraph(float[] classFrequencies) {
			return new UndirectedMatrixRingGraph(classFrequencies);
		}


		public ClassifiedGraph createGraph(String id, float[] classFrequencies) {
			if (id != null) return new UndirectedMatrixRingGraph(id, classFrequencies);
			return new UndirectedMatrixRingGraph(classFrequencies);
		}


		public MutableGraph createGraph(String id) {
			if (id != null) return new UndirectedMatrixRingGraph(id, DEFAULT_CLASS);
			return new UndirectedMatrixRingGraph(DEFAULT_CLASS);
		}
	}

}