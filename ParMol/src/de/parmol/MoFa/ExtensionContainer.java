/*
 * Created on 01.06.2005
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

import java.util.ArrayList;
import java.util.Collection;

import de.parmol.Util;
import de.parmol.graph.DirectedGraph;


/**
 * This class implements an efficient container for extension of subgraphs. Groups of equal extension are built
 * automatically by using some kind of specialized hash structure. The use of objects of this class speeds up the
 * extension process considerably.
 * 
 * @author Thorsten Meinl <Thorsten.Meinl@informatik.uni-erlangen.de>
 */
public class ExtensionContainer {
	private final int m_extendedNodeOffset;

	private SubContainer[] m_extendedNodeBins;


	/**
	 * Creates a new container for extensions.
	 * 
	 * @param extendedNodeOffset the lowest index of all extedable nodes (the <i>last extended node index </i>)
	 * @param maxExtendedNodes the maximum number of extedable nodes (i.e. the size of the extended subgraph)
	 */
	public ExtensionContainer(int extendedNodeOffset, int maxExtendedNodes) {
		m_extendedNodeOffset = extendedNodeOffset;
		m_extendedNodeBins = new SubContainer[maxExtendedNodes - extendedNodeOffset];
		for (int i = 0; i < m_extendedNodeBins.length; i++) {
			m_extendedNodeBins[i] = new SubContainer();
		}
	}


	/**
	 * Adds a new edge-node extension to the container.
	 * 
	 * @param ext the extension
	 * @param extendedNodeIndex the index of the extended node
	 * @param edgeLabel the label of the new edge
	 * @param nodeLabel the label of the new node
	 */
	public void add(EdgeNodeExtension ext, int extendedNodeIndex, int edgeLabel, int nodeLabel) {
		final SubContainer combinedLists = m_extendedNodeBins[extendedNodeIndex - m_extendedNodeOffset];
		combinedLists.add(ext, edgeLabel, nodeLabel);
	}


	/**
	 * Adds a new edge extension to the container.
	 * 
	 * @param ext the extension
	 * @param extendedNodeIndex the index of first extended node
	 * @param edgeLabel the label of the new edge
	 * @param secondNodeIndex the index of the second node to which the new edge leads
	 */
	public void add(EdgeExtension ext, int extendedNodeIndex, int edgeLabel, int secondNodeIndex) {
		final SubContainer combinedLists = m_extendedNodeBins[extendedNodeIndex - m_extendedNodeOffset];
		combinedLists.add(ext, edgeLabel, -secondNodeIndex - 1);
	}


	/**
	 * Returns all extensions in this container as a collection of collections. Each collection holds a group of equal
	 * extensions.
	 * 
	 * @return a collection of collection with extensions
	 */
	public Collection getGroups() {
		ArrayList groups = new ArrayList(m_extendedNodeBins.length * 32);

		for (int i = 0; i < m_extendedNodeBins.length; i++) {
			m_extendedNodeBins[i].addExtensionLists(groups);
		}

		return groups;
	}

	private static class SubContainer {
		private long[] m_combinedLabelMap = new long[32];
		private int m_combinedLabelCount;

		private final ArrayList m_extensions = new ArrayList();


		void add(Extension ext, int edgeLabel, int nodeLabel) {
			long combinedLabel = ((long) edgeLabel << 32) + nodeLabel;
			
			if (ext.getParentEmbedding().isDirectedGraphEmbedding()) {
				if (ext instanceof EdgeNodeExtension) {
					final int newNode = ((EdgeNodeExtension) ext).getNewSupergraphNode();
					final int newEdge = ((EdgeNodeExtension) ext).getNewSupergraphEdge();
					
					// if the new edge is an incoming edge for the extened node it must be made different
					// from an the same extension but with an outgoing edge
					if (((DirectedGraph) ext.getParentEmbedding().getSuperGraph()).getEdgeDirection(newEdge, newNode) ==
						DirectedGraph.OUTGOING_EDGE)
					{
						combinedLabel ^= 0xfedcba987654321fL;
					}
				} else if (ext instanceof EdgeExtension) {
					// if the new edge is an incoming edge for the extened node it must be made different
					// from an the same extension but with an outgoing edge
					if (((EdgeExtension) ext).getNewEdgeDirection() == DirectedGraph.OUTGOING_EDGE) {
						combinedLabel ^= 0xfedcba987654321fL;
					}
				}
			}
					

			int index;
			if (m_combinedLabelCount < 32) {
				for (index = 0; index < m_combinedLabelCount; index++) {
					if (m_combinedLabelMap[index] == combinedLabel) {
						assert(ext.equals(((ArrayList) m_extensions.get(index)).get(0)));
						((ArrayList) m_extensions.get(index)).add(ext);
						return;
					} else if (m_combinedLabelMap[index] > combinedLabel) {
						break;
					}
				}
			} else {
				index = Util.binarySearch(m_combinedLabelMap, combinedLabel, m_combinedLabelCount);
				if (index >= 0) {
					assert(ext.equals(((ArrayList) m_extensions.get(index)).get(0)));
					((ArrayList) m_extensions.get(index)).add(ext);
					return;
				}

				index = -index - 1;
			}

			ArrayList list = new ArrayList();
			m_extensions.add(index, list);
			list.add(ext);

			if (++m_combinedLabelCount >= m_combinedLabelMap.length) {
				long[] temp = new long[m_combinedLabelMap.length + 32];
				System.arraycopy(m_combinedLabelMap, 0, temp, 0, index);
				temp[index] = combinedLabel;
				System.arraycopy(m_combinedLabelMap, index, temp, index + 1, m_combinedLabelMap.length - index);
				m_combinedLabelMap = temp;
			} else {
				for (int k = m_combinedLabelCount - 1; k > index; k--) {
					m_combinedLabelMap[k] = m_combinedLabelMap[k - 1];
				}
				m_combinedLabelMap[index] = combinedLabel;
			}
		}


		void addExtensionLists(Collection allExtensionLists) {
			allExtensionLists.addAll(m_extensions);
		}
	}
}