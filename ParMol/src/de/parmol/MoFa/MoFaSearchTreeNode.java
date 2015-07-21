/*
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

import java.util.Collection;
import java.util.Iterator;

import de.parmol.graph.Graph;
import de.parmol.search.SearchTreeNode;
import de.parmol.util.ObjectPool;


/**
 * This class represents a node in the search tree created by MoFa.
 * 
 * @author Thorsten Meinl <Thorsten.Meinl@informatik.uni-erlangen.de>
 */
public class MoFaSearchTreeNode extends SearchTreeNode {
	protected final Graph m_subgraph;
	protected final float[] m_frequencies;
	protected Collection m_embeddings;
	protected final int m_lastExtendedNodeIndex;
	protected final int m_completeEmbeddingCount;
	protected final BlackNodeSet m_blackNodes;
	protected final boolean m_extensionWasPerfect;


	//	protected final int[][] m_code;

	/**
	 * Creates a new MoFaSearchTreeNode.
	 * 
	 * @param parent the parent of this node
	 * @param subgraph the subgraph representing this node
	 * @param classFrequencies the frequencies of the subgraph
	 * @param embeddings the embeddings of the subgraph
	 * @param completeEmbeddingCount the number of all embeddings in all molecules of the subgraph represented by this
	 *          search tree node
	 * @param lastExtendedNodeIndex the index of the latest extended node
	 * @param extensionWasPerfect <code>true</code> if the last extension (the one that produced the subgraph) was a
	 *          perfect extension, <code>false</code> otherwise
	 * @param level the level in the search tree
	 */
	public MoFaSearchTreeNode(MoFaSearchTreeNode parent, Graph subgraph, float[] classFrequencies, Collection embeddings,
			int completeEmbeddingCount, int lastExtendedNodeIndex, boolean extensionWasPerfect, int level) {
		super(parent, level);
		m_subgraph = subgraph;
		m_frequencies = classFrequencies;
		m_embeddings = embeddings;
		m_completeEmbeddingCount = completeEmbeddingCount;
		m_lastExtendedNodeIndex = lastExtendedNodeIndex;

		m_blackNodes = parent.m_blackNodes;
		m_extensionWasPerfect = extensionWasPerfect;
		//		if (parent != null) {
		//			m_code = new int[parent.m_code.length + 1][];
		//			System.arraycopy(parent.m_code, 0, m_code, 0, parent.m_code.length);
		//			
		//			
		//			final int newEdge = m_subgraph.getEdge(m_subgraph.getEdgeCount() - 1);
		//			final int nodeA = m_subgraph.getNodeA(newEdge);
		//			final int nodeB = m_subgraph.getNodeB(newEdge);
		//			
		//			if (parent.getSubgraph().getNodeCount() == m_subgraph.getNodeCount()) { // last extension was an edge extension
		//				// edge extension always add an edge from the "bigger" node to the "smaller" node
		//				if (m_subgraph.getNodeIndex(nodeA) < m_subgraph.getNodeIndex(nodeB)) {
		//					m_code[m_code.length - 1] = new int[] {
		//							m_subgraph.getNodeLabel(nodeB), m_subgraph.getNodeIndex(nodeB),
		//							m_subgraph.getNodeLabel(nodeA), m_subgraph.getNodeIndex(nodeA),
		//							m_subgraph.getEdgeLabel(newEdge)
		//					};
		//				} else {
		//					m_code[m_code.length - 1] = new int[] {
		//							m_subgraph.getNodeLabel(nodeA), m_subgraph.getNodeIndex(nodeA),
		//							m_subgraph.getNodeLabel(nodeB), m_subgraph.getNodeIndex(nodeB),
		//							m_subgraph.getEdgeLabel(newEdge)
		//					};
		//				}
		//			} else { // last extension was an edge-node extension
		//				final int newNode = m_subgraph.getNode(m_subgraph.getNodeCount() - 1);
		//				
		//				if (newNode == nodeA) {
		//					m_code[m_code.length - 1] = new int[] {
		//							m_subgraph.getNodeLabel(nodeB), m_subgraph.getNodeIndex(nodeB),
		//							m_subgraph.getNodeLabel(nodeA), m_subgraph.getNodeIndex(nodeA),
		//							m_subgraph.getEdgeLabel(newEdge)
		//					};
		//				} else {
		//					m_code[m_code.length - 1] = new int[] {
		//							m_subgraph.getNodeLabel(nodeA), m_subgraph.getNodeIndex(nodeA),
		//							m_subgraph.getNodeLabel(nodeB), m_subgraph.getNodeIndex(nodeB),
		//							m_subgraph.getEdgeLabel(newEdge)
		//					};
		//				}
		//				
		//			}
		//			
		//		} else {
		//			m_code = new int[0][];
		//		}
	}


	/**
	 * Creates a new MoFaSearchTreeNode
	 * 
	 * @param subgraph the subgraph representing this node
	 * @param classFrequencies the frequencies of the subgraph
	 * @param embeddings the embeddings of the subgraph
	 * @param completeEmbeddingCount the number of all embeddings in all molecules of the subgraph represented by this
	 *          search tree node
	 * @param lastExtendedNodeIndex the index of the latest extended node
	 * @param extensionWasPerfect <code>true</code> if the last extension (the one that produced the subgraph) was a
	 *          perfect extension, <code>false</code> otherwise
	 * @param blackNodes a set of black nodes that must not be added by further extensions
	 * @param level the level in the search tree
	 */
	public MoFaSearchTreeNode(Graph subgraph, float[] classFrequencies, Collection embeddings,
			int completeEmbeddingCount, int lastExtendedNodeIndex, boolean extensionWasPerfect, BlackNodeSet blackNodes,
			int level) {
		super(null, level);
		m_subgraph = subgraph;
		m_frequencies = classFrequencies;
		m_embeddings = embeddings;
		m_completeEmbeddingCount = completeEmbeddingCount;
		m_lastExtendedNodeIndex = lastExtendedNodeIndex;
		m_extensionWasPerfect = extensionWasPerfect;

		m_blackNodes = blackNodes;
		//		m_code = new int[0][]; // TODO this is not right!
	}


	/**
	 * Returns the embeddings of the subgraph represented by this node.
	 * 
	 * @return a collection of embeddings
	 */
	public Collection getEmbeddings() {
		return m_embeddings;
	}


	/**
	 * Clears the list of embeddings and puts all objects in it into the given ObjectPool for later reuse. After calling
	 * this method the return value of MoFaSearchTreeNode#getEmbeddings() is undefined.
	 * 
	 * @param objectPool an ObjectPool for MoFaEmbeddings
	 */
	public void clearEmbeddings(ObjectPool objectPool) {
		for (Iterator it = m_embeddings.iterator(); it.hasNext();) {
			((MoFaEmbedding) it.next()).freeInstance(objectPool);
		}
		m_embeddings = null;
	}


	/**
	 * Clears the list of embeddings. After calling this method the return value of MoFaSearchTreeNode#getEmbeddings() is
	 * undefined.
	 */
	public void clearEmbeddings() {
		clearEmbeddings(null);
	}


	/**
	 * Returns the index of the last extended node.
	 * 
	 * @return the index of the last extended node.
	 */
	public int getLastExtendedNodeIndex() {
		return m_lastExtendedNodeIndex;
	}


	/**
	 * Returns the number of all embeddings of the subgraph represented by this search tree node.
	 * 
	 * @return the number of all embeddings of the subgraph represented by this search tree node.
	 */
	public int getCompleteEmbeddingCount() {
		return m_completeEmbeddingCount;
	}


	/**
	 * Returns the frequencies of the subgraph represented by this node.
	 * 
	 * @return the class frequencies
	 */
	public float[] getFrequencies() {
		return m_frequencies;
	}


	/**
	 * Returns the subgraph represented by this node.
	 * 
	 * @return a subgraph
	 */
	public Graph getSubgraph() {
		return m_subgraph;
	}


	/**
	 * Return the black nodes for this search tree node.
	 * 
	 * @return the black nodes
	 */
	public BlackNodeSet getBlackNodes() {
		return m_blackNodes;
	}


	/**
	 * Sets the embeddings for this node but only if it does not already have embeddings. Then a RuntimeException is
	 * thrown.
	 * 
	 * @param embeddings the embeddings for this node
	 */
	public void setEmbeddings(Collection embeddings) {
		if (m_embeddings != null) { throw new RuntimeException("This node already has embeddings"); }
		m_embeddings = embeddings;
	}


	/**
	 * Returns if the extension that produced the subgraph was a perfect one.
	 * 
	 * @return <code>true</code> if the last extension was perfect, <code>false</code> otherwise
	 */
	public boolean extensionWasPerfect() {
		return m_extensionWasPerfect;
	}

	//	 public int[][] getCode() { return m_code; }
	//	
	//	
	//	private static Comparator CODE_COMPARATOR = new Comparator() {
	//		public int compare(Object o1, Object o2) {
	//			final int[] a = (int[]) o1;
	//			final int[] b = (int[]) o2;
	//			
	//			for (int i = 0; i < 5; i++) {
	//				int diff = a[i] - b[i];
	//				if (diff != 0) return diff;
	//			}
	//			
	//			return 0;
	//		}
	//	};
	//	
	//	private static int compare(int[][] codeA, int[][] codeB) {
	//		Arrays.sort(codeB, CODE_COMPARATOR);
	//		
	//		for (int i = 1; i < codeA.length; i++) { // the first node need not have the smallest label, because it is the seed
	//			int diff = CODE_COMPARATOR.compare(codeA[i], codeB[i]);
	//			if (diff != 0) return diff;
	//		}
	//		
	//		return 0;
	//	}
	//	
	//	public boolean isCodeMinimal() {
	//		return false;
	//	}
}

