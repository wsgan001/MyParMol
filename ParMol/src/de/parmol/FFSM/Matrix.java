/*
 * Created on 25.03.2005
 *
 * Copyright 2005 Thorsten Meinl
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
package de.parmol.FFSM;

import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.NoSuchElementException;

import de.parmol.graph.ClassifiedGraph;
import de.parmol.graph.Graph;
import de.parmol.graph.MutableGraph;
import de.parmol.graph.SimpleGraphComparator;
import de.parmol.graph.UndirectedGraph;
import de.parmol.util.HalfIntMatrix;


/**
 * This class represents a matrix how FFSM needs it.
 * 
 * @author Thorsten Meinl <Thorsten.Meinl@informatik.uni-erlangen.de>
 */
public final class Matrix implements UndirectedGraph, MutableGraph, Comparable, ClassifiedGraph {
	private final HalfIntMatrix m_matrix;
	private int m_edgeCount;
	private boolean m_isCAM, m_isSuboptimalCAM, m_CAMComputed, m_SubOptCAMComputed;
	private float[] m_classFrequencies;
	private EmbeddingList m_embeddings;

	private final int m_id = ++s_id;
	private static int s_id;


	/**
	 * Creates a new matrix that contains just one node.
	 * 
	 * @param nodeLabel the label of the node
	 * @param embeddings an embedding list for this graph
	 */
	public Matrix(int nodeLabel, EmbeddingList embeddings) {
		m_matrix = new HalfIntMatrix(1, Graph.NO_EDGE);
		m_matrix.setValue(0, 0, nodeLabel);
		m_embeddings = embeddings;
	}


	/**
	 * Creates a new matrix that is a copy of the given template matrix. Optionally additional space can be reserved if
	 * new nodes are added in the near future.
	 * 
	 * @param template the matrix that should be copied
	 * @param embeddings an embedding list of the matrix
	 * @param reserveNewNodes the number of nodes that will be added in the near future
	 */
	public Matrix(Matrix template, EmbeddingList embeddings, int reserveNewNodes) {
		m_matrix = new HalfIntMatrix(template.m_matrix, reserveNewNodes);
		m_edgeCount = template.m_edgeCount;
		m_embeddings = embeddings;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see de.parmol.graph.Graph#getNodeCount()
	 */
	public int getNodeCount() {
		return m_matrix.getSize();
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see de.parmol.graph.Graph#getEdgeCount()
	 */
	public int getEdgeCount() {
		return m_edgeCount;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see de.parmol.graph.Graph#getID()
	 */
	public int getID() {
		return m_id;
	}


	/*
	 *  (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public Object clone() {
		return new Matrix(this, this.m_embeddings, 0);
	}


	/*
	 *  (non-Javadoc)
	 * @see de.parmol.graph.Graph#getEdge(int, int)
	 */
	public int getEdge(int nodeA, int nodeB) {
		if ((m_matrix.getValue(nodeA, nodeB) == Graph.NO_EDGE) || (nodeA == nodeB)) return Graph.NO_EDGE;

		if (nodeA > nodeB) {
			return (nodeA << 16) | (nodeB & 0xffff);
		} else {
			return (nodeB << 16) | (nodeA & 0xffff);
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see de.parmol.graph.Graph#getEdge(int)
	 */
	public int getEdge(int index) {
		for (int row = 0; row < m_matrix.getSize(); row++) {
			for (int col = 0; col < row; col++) {
				if (m_matrix.getValue(row, col) != Graph.NO_EDGE) {
					if (index-- == 0) {
						if (row > col) {
							return (row << 16) | (col & 0xffff);
						} else {
							return (col << 16) | (row & 0xffff);
						}
					}
				}
			}
		}

		throw new NoSuchElementException("No more edges");
	}


	/*
	 *  (non-Javadoc)
	 * @see de.parmol.graph.Graph#getNode(int)
	 */
	public int getNode(int index) {
		return index;
	}


	/*
	 *  (non-Javadoc)
	 * @see de.parmol.graph.Graph#getNodeLabel(int)
	 */
	public int getNodeLabel(int node) {
		return m_matrix.getValue(node, node);
	}


	/*
	 *  (non-Javadoc)
	 * @see de.parmol.graph.Graph#getEdgeLabel(int)
	 */
	public int getEdgeLabel(int edge) {
		return m_matrix.getValue(edge >> 16, edge & 0xffff);
	}


	/*
	 *  (non-Javadoc)
	 * @see de.parmol.graph.Graph#getDegree(int)
	 */
	public int getDegree(int node) {
		int deg = 0;
		for (int i = m_matrix.getSize() - 1; i >= 0; i--) {
			if ((i != node) && (m_matrix.getValue(node, i) != Graph.NO_EDGE)) deg++;
		}

		return deg;
	}

	/*
	 *  (non-Javadoc)
	 * @see de.parmol.graph.Graph#getNodeEdge(int, int)
	 */
	public int getNodeEdge(int node, int index) {
		for (int i = m_matrix.getSize() - 1; i >= 0; i--) {
			if ((i != node) && (m_matrix.getValue(node, i) != Graph.NO_EDGE)) {
				if (index-- == 0) {
					if (node > i) {
						return (node << 16) | (i & 0xffff);
					} else {
						return (i << 16) | (node & 0xffff);
					}
				}
			}
		}

		throw new NoSuchElementException("No more edges");
	}


	/*
	 *  (non-Javadoc)
	 * @see de.parmol.graph.Graph#getNodeIndex(int)
	 */
	public int getNodeIndex(int node) {
		return node;
	}


	/*
	 *  (non-Javadoc)
	 * @see de.parmol.graph.Graph#getEdgeIndex(int)
	 */
	public int getEdgeIndex(final int edge) {
		int index = 0;
		for (int row = 0; row < m_matrix.getSize(); row++) {
			for (int col = 0; col < row; col++) {
				if ((row == (edge >> 16)) && (col == (edge & 0xffff))) {
					return index;
				} else if (m_matrix.getValue(row, col) != Graph.NO_EDGE) {
					index++;
				}
			}
		}

		throw new NoSuchElementException("No such edge");
	}


	/*
	 *  (non-Javadoc)
	 * @see de.parmol.graph.Graph#getNodeA(int)
	 */
	public int getNodeA(int edge) {
		return (edge >> 16);
	}


	/*
	 *  (non-Javadoc)
	 * @see de.parmol.graph.Graph#getNodeB(int)
	 */
	public int getNodeB(int edge) {
		return (edge & 0xffff);
	}


	/*
	 *  (non-Javadoc)
	 * @see de.parmol.graph.Graph#getOtherNode(int, int)
	 */
	public int getOtherNode(int edge, int node) {
		return (node == (edge >> 16)) ? (edge & 0xffff) : (edge >> 16);
	}


	/*
	 *  (non-Javadoc)
	 * @see de.parmol.graph.Graph#isBridge(int)
	 */
	public boolean isBridge(int edge) {
		throw new UnsupportedOperationException("Not yet implemented");
	}


	/*
	 *  (non-Javadoc)
	 * @see de.parmol.graph.Graph#setNodeObject(int, java.lang.Object)
	 */
	public void setNodeObject(int node, Object o) {
		throw new UnsupportedOperationException("Not yet implemented");
	}


	/*
	 *  (non-Javadoc)
	 * @see de.parmol.graph.Graph#getNodeObject(int)
	 */
	public Object getNodeObject(int node) {
		return null;
		//		throw new UnsupportedOperationException("Not yet implemented");
	}


	/*
	 *  (non-Javadoc)
	 * @see de.parmol.graph.Graph#setEdgeObject(int, java.lang.Object)
	 */
	public void setEdgeObject(int edge, Object o) {
		throw new UnsupportedOperationException("Not yet implemented");
	}


	/*
	 *  (non-Javadoc)
	 * @see de.parmol.graph.Graph#getEdgeObject(int)
	 */
	public Object getEdgeObject(int edge) {
		return null;
		//		throw new UnsupportedOperationException("Not yet implemented");
	}


	/*
	 *  (non-Javadoc)
	 * @see de.parmol.graph.Graph#saveMemory()
	 */
	public void saveMemory() {
		// nothing to do here
	}


	/**
	 * Checks if this matrix is canonical.
	 * 
	 * @return <code>true</code> if the matrix is in canonical form, <code>false</code> otherwise
	 */
	public boolean isCAM() {
		if (!m_CAMComputed) {
			m_isCAM = computeCAM(m_matrix, new HalfIntMatrix(m_matrix), 0);
			if (m_isCAM) m_SubOptCAMComputed = m_isSuboptimalCAM = true;
			m_CAMComputed = true;
		}

		return m_isCAM;
	}


	/**
	 * Checks if the biggest matrix is really the biggest representation of the graph
	 * 
	 * @param biggestMatrix the assumed biggest matrix
	 * @param currentMatrix a copy of the biggest matrix that ist permutated during the check
	 * @param row the index of the row that should be exchanged with another row
	 * @return <code>true<code> if the biggest matrix is indeed the biggest one, <code>false</code> otherwise
	 */
	private static boolean computeCAM(HalfIntMatrix biggestMatrix, HalfIntMatrix currentMatrix, final int row) {
		if (row == 0) { // do a quick check
			final int firstLabel = biggestMatrix.getValue(0, 0);
			for (int i = 1; i < biggestMatrix.getSize(); i++) {
				if (biggestMatrix.getValue(i, i) > firstLabel) return false;
			}
		}

		for (int i = row; i < biggestMatrix.getSize(); i++) {
			if (row == i) {
				final int res = biggestMatrix.compareTo(currentMatrix, 0, row);
				if (res < 0) {
					return false; // the "biggest" matrix is not biggest one
				} else if (res == 0) {
					if (!computeCAM(biggestMatrix, currentMatrix, row + 1)) { return false; }
				}
			} else {
				currentMatrix.exchangeRows(row, i);

				final int res = biggestMatrix.compareTo(currentMatrix, 0, row);
				if (res < 0) {
					return false; // the "biggest" matrix is not biggest one
				} else if (res == 0) {
					if (!computeCAM(biggestMatrix, currentMatrix, row + 1)) { return false; }
				}
				currentMatrix.exchangeRows(row, i);
			}
		}

		return true;
	}


	/*
	 *  (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return SimpleGraphComparator.getHashCode(this);
	}


	/**
	 * Checks if the matrix is suboptimal.
	 * 
	 * @return <code>true</code> if the matrix is suboptimal, <code>false</code> otherwise
	 */
	public boolean isSuboptimalCAM() {
		if (!m_CAMComputed) isCAM();
		if (!m_SubOptCAMComputed) {

			final HalfIntMatrix properSubmatrix = new HalfIntMatrix(m_matrix);

			// delete the last edge, this results in the proper submatrix
			int edgeCount = 0;
			for (int col = properSubmatrix.getSize() - 2; col >= 0; col--) {
				if (properSubmatrix.getValue(properSubmatrix.getSize() - 1, col) != Graph.NO_EDGE) {
					if (edgeCount == 0) {
						properSubmatrix.setValue(properSubmatrix.getSize() - 1, col, Graph.NO_EDGE);
						edgeCount++;
					} else {
						edgeCount++;
						break;
					}
				}
			}

			if (edgeCount == 1) {
				properSubmatrix.resize(properSubmatrix.getSize() - 1);
			}

			m_isSuboptimalCAM = computeCAM(properSubmatrix, new HalfIntMatrix(properSubmatrix), 0);
			m_SubOptCAMComputed = true;
		}

		return m_isSuboptimalCAM;
	}


	/*
	 *  (non-Javadoc)
	 * @see de.parmol.graph.MutableGraph#addNode(int)
	 */
	public int addNode(int nodeLabel) {
		m_matrix.resize(m_matrix.getSize() + 1);
		m_matrix.setValue(m_matrix.getSize() - 1, m_matrix.getSize() - 1, nodeLabel);
		m_CAMComputed = m_SubOptCAMComputed = false;
		return m_matrix.getSize() - 1;
	}


	/*
	 *  (non-Javadoc)
	 * @see de.parmol.graph.MutableGraph#addEdge(int, int, int)
	 */
	public int addEdge(int nodeA, int nodeB, int edgeLabel) {
		if ((nodeA != nodeB) && (m_matrix.getValue(nodeA, nodeB) == Graph.NO_EDGE)) {
			m_matrix.setValue(nodeA, nodeB, edgeLabel);
			m_CAMComputed = m_SubOptCAMComputed = false;
			m_edgeCount++;
			return (nodeA > nodeB) ? ((nodeA << 16) | (nodeB & 0xffff)) : ((nodeB << 16) | (nodeA & 0xffff));
		} else {
			throw new IllegalArgumentException("Self loops or double edges are not allowed");
		}
	}


	/*
	 *  (non-Javadoc)
	 * @see de.parmol.graph.MutableGraph#addNodeAndEdge(int, int, int)
	 */
	public int addNodeAndEdge(int nodeA, int nodeLabel, int edgeLabel) {
		final int node = addNode(nodeLabel);
		addEdge(node, nodeA, edgeLabel);
		return node;
	}


	/*
	 *  (non-Javadoc)
	 * @see de.parmol.graph.MutableGraph#removeNode(int)
	 */
	public void removeNode(int node) {
		throw new UnsupportedOperationException("Not implemented yet");
	}


	/*
	 *  (non-Javadoc)
	 * @see de.parmol.graph.MutableGraph#removeEdge(int)
	 */
	public void removeEdge(int edge) {
		throw new UnsupportedOperationException("Not implemented yet");
	}


	/*
	 *  (non-Javadoc)
	 * @see de.parmol.graph.MutableGraph#setNodeLabel(int, int)
	 */
	public void setNodeLabel(int node, int newLabel) {
		m_matrix.setValue(node, node, newLabel);
	}


	/*
	 *  (non-Javadoc)
	 * @see de.parmol.graph.MutableGraph#setEdgeLabel(int, int)
	 */
	public void setEdgeLabel(int edge, int newLabel) {
		assert (m_matrix.getValue(edge >> 16, edge & 0xffff) != Graph.NO_EDGE);
		m_matrix.setValue(edge >> 16, edge & 0xffff, newLabel);
	}


	/**
	 * Checks if this matrix is an inner matrix or not (i.e. has more than one edge in the last row or not).
	 * 
	 * @return <code>true</code> if this matrix is an inner matrix, <code>false</code> otherwise
	 */
	public boolean isInnerMatrix() {
		int count = 0;
		final int lastRow = m_matrix.getSize() - 1;
		for (int i = 0; i < lastRow; i++) {
			if (m_matrix.getValue(lastRow, i) != Graph.NO_EDGE) {
				if (++count >= 2) return true;
			}
		}

		return false;
	}


	/**
	 * Returns the last edge of the matrix.
	 * 
	 * @return the last edge or <code>Graph.NO_EDGE</code> if the matrix has no edges
	 */
	public int getLastEdge() {
		final int lastRow = m_matrix.getSize() - 1;
		for (int i = lastRow - 1; i >= 0; i--) {
			if (m_matrix.getValue(lastRow, i) != Graph.NO_EDGE) { return (lastRow << 16) | (i & 0xffff); }
		}

		assert (m_edgeCount == 0);
		return Graph.NO_EDGE;
	}


	/*
	 *  (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object o) {
		return m_matrix.compareTo(((Matrix) o).m_matrix);
	}

	private final static float[] EMPTY_CLASS_FREQUENCIES = { Integer.MIN_VALUE };


	/*
	 *  (non-Javadoc)
	 * @see de.parmol.graph.ClassifiedGraph#getClassFrequencies()
	 */
	public float[] getClassFrequencies() {
		return m_classFrequencies != null ? m_classFrequencies : EMPTY_CLASS_FREQUENCIES;
	}


	/**
	 * Updates the class frequencies of this subgraph with the values of the given graph.
	 * 
	 * @param g a graph in which this subgraph occurs
	 */
	public void addSupportedGraph(Graph g) {
		if (m_classFrequencies == null) {
			if (g instanceof ClassifiedGraph) {
				m_classFrequencies = (float[]) ((ClassifiedGraph) g).getClassFrequencies().clone();
			} else {
				m_classFrequencies = new float[] { 1 };
			}
		} else {
			if (g instanceof ClassifiedGraph) {
				for (int i = 0; i < m_classFrequencies.length; i++) {
					m_classFrequencies[i] += ((ClassifiedGraph) g).getClassFrequencies()[i];
				}
			} else {
				m_classFrequencies[0] += 1;
			}
		}
	}


	/**
	 * Returns a collection with all graphs in which this subgraph occurs.
	 * 
	 * @return a collection with all supported graphs
	 */
	public Collection getSupportedGraphs() {
		final IdentityHashMap graphs = new IdentityHashMap();

		for (int j = 0; j < m_embeddings.size(); j++) {
			final Graph supergraph = m_embeddings.get(j).getSuperGraph();

			graphs.put(supergraph, supergraph);
		}

		return new ArrayList(graphs.values());
	}


	/**
	 * Compares this graph's maximal proper submatrix with the maximal proper submatrix of the given graph.
	 * 
	 * @param matrix a matrix
	 * @return <code>true</code> if the maximal proper submatrices of both graphs are identical, <code>false</code>
	 *         otherwise
	 */
	public int compareMaximalProperSubmatrix(Matrix matrix) {
		final HalfIntMatrix properSubmatrixA = new HalfIntMatrix(m_matrix);

		// delete the last edge, this results in the proper submatrix
		int edgeCount = 0;
		for (int col = properSubmatrixA.getSize() - 2; col >= 0; col--) {
			if (properSubmatrixA.getValue(properSubmatrixA.getSize() - 1, col) != Graph.NO_EDGE) {
				if (edgeCount == 0) {
					properSubmatrixA.setValue(properSubmatrixA.getSize() - 1, col, Graph.NO_EDGE);
					edgeCount++;
				} else {
					edgeCount++;
					break;
				}
			}
		}

		if (edgeCount == 1) { // there was only one edge in the last row
			properSubmatrixA.resize(properSubmatrixA.getSize() - 1);
		}


		final HalfIntMatrix properSubmatrixB = new HalfIntMatrix(matrix.m_matrix);

		// delete the last edge, this results in the proper submatrix
		edgeCount = 0;
		for (int col = properSubmatrixB.getSize() - 2; col >= 0; col--) {
			if (properSubmatrixB.getValue(properSubmatrixB.getSize() - 1, col) != Graph.NO_EDGE) {
				if (edgeCount == 0) {
					properSubmatrixB.setValue(properSubmatrixB.getSize() - 1, col, Graph.NO_EDGE);
					edgeCount++;
				} else {
					edgeCount++;
					break;
				}
			}
		}

		if (edgeCount == 1) { // there was only one edge in the last row
			properSubmatrixB.resize(properSubmatrixB.getSize() - 1);
		}

		return properSubmatrixA.compareTo(properSubmatrixB);
	}


	/**
	 * Returns the embedding list for this subgraph.
	 * 
	 * @return the embedding list for this subgraph
	 */
	public EmbeddingList getEmbeddings() {
		return m_embeddings;
	}


	/**
	 * Removes the embedding list of this graph to save memory.
	 */
	public void removeEmbeddings() {
		m_embeddings = null;
	}


	/*
	 *  (non-Javadoc)
	 * @see de.parmol.graph.Graph#getName()
	 */
	public String getName() {
		return Integer.toString(m_id);
	}


	/*
	 *  (non-Javadoc)
	 * @see de.parmol.graph.ClassifiedGraph#setClassFrequencies(float[])
	 */
	public void setClassFrequencies(float[] frequencies) {
		m_classFrequencies = frequencies;
	}
}