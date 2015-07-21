/*
 * Created on Aug 17, 2004
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
package de.parmol.MoFa;

import de.parmol.graph.DirectedGraph;
import de.parmol.graph.DirectedListGraph;
import de.parmol.graph.Graph;
import de.parmol.graph.MutableGraph;
import de.parmol.graph.UndirectedGraph;
import de.parmol.graph.UndirectedListGraph;
import de.parmol.util.ObjectPool;

/**
 * This class represents an extension of an embedding that consists of an edge only, i.e. a cycle is closed.
 * 
 * @author Thorsten Meinl <Thorsten.Meinl@informatik.uni-erlangen.de>
 *
 */
public class EdgeExtension implements Extension {
  protected MoFaEmbedding m_parentEmbedding;
  protected int m_newEdge, m_extendedSubgraphNode, m_secondSubgraphNodeIndex, m_extendedSupergraphNode;

  /**
   * Creates a new EdgeExtension.
   * 
   * @param parentEmbedding the embedding of the current subgraph
   * @param extendedSubgraphNode the node in the subgraph to which the new edge is added
   * @param secondSubgraphNodeIndex the index of the node in the subgraph to which the new edge is leading
   * @param extendedSupergraphNode the node (from the supergraph) to which the new edge is added
   * @param newEdge the edge (from the supergraph) that is added
   */
  private EdgeExtension(MoFaEmbedding parentEmbedding, int extendedSubgraphNode, int secondSubgraphNodeIndex, int extendedSupergraphNode, int newEdge) {
  	setValues(parentEmbedding, extendedSubgraphNode, secondSubgraphNodeIndex, extendedSupergraphNode, newEdge);
  }
  
  /**
   * Sets the values of this object.
   *  
   * @param parentEmbedding the embedding of the current subgraph
   * @param extendedSubgraphNode the node in the subgraph to which the new edge is added
   * @param secondSubgraphNodeIndex the index of the node in the subgraph to which the new edge is leading
   * @param extendedSupergraphNode the node (from the supergraph) to which the new edge is added
   * @param newEdge the edge (from the supergraph) that is added
   */
  private void setValues(MoFaEmbedding parentEmbedding, int extendedSubgraphNode, int secondSubgraphNodeIndex, int extendedSupergraphNode, int newEdge) {
    m_parentEmbedding = parentEmbedding;
    m_extendedSubgraphNode = extendedSubgraphNode;
    m_secondSubgraphNodeIndex = secondSubgraphNodeIndex;
    m_extendedSupergraphNode = extendedSupergraphNode;
    m_newEdge = newEdge;      	
  }
  
	/* (non-Javadoc)
	 * @see de.parmol.MoFa.Extension#getParentEmbedding()
	 */
	public MoFaEmbedding getParentEmbedding() { return m_parentEmbedding;	}


	/* (non-Javadoc)
	 * @see de.parmol.MoFa.Extension#getExtendedEmbedding()
	 */
	public MoFaEmbedding getExtendedEmbedding(ObjectPool objectPool) {
		return m_parentEmbedding.extend(m_newEdge, Graph.NO_NODE, m_extendedSubgraphNode, objectPool);
	}

	/*
	 * (non-Javadoc)
	 * @see de.parmol.MoFa.Extension#getExtendedEmbedding()
	 */
	public MoFaEmbedding getExtendedEmbedding() {
		return getExtendedEmbedding(null);
	}

	
	/* (non-Javadoc)
	 * @see de.parmol.MoFa.Extension#getExtendedNodeIndex()
	 */
	public int getExtendedNodeIndex() { return m_parentEmbedding.getSubGraph().getNodeIndex(m_extendedSubgraphNode); }

	/**
	 * Returns the index of the node to which the new edge is added but that is not the extended node.
	 * @return the index of the second node to which an edge is added
	 */
	public int getSecondNodeIndex() { return m_parentEmbedding.getSubGraph().getNodeIndex(m_secondSubgraphNodeIndex); }
	
	/**
	 * Returns the label of the new edge
	 * @return the label of the new edge
	 */
  public final int getNewEdgeLabel() {
    return m_parentEmbedding.getSuperGraph().getEdgeLabel(m_newEdge);
  }

  /**
   * Returns the direction of the new edge as the extended node sees it.
   * @return {@link DirectedGraph#INCOMING_EDGE} or {@link DirectedGraph#OUTGOING_EDGE}
   * @throws UnsupportedOperationException if the underlying graph is not directed
   */
  public final int getNewEdgeDirection() {
  	if (m_parentEmbedding.isDirectedGraphEmbedding()) {
  		return ((DirectedGraph) m_parentEmbedding.getSuperGraph()).getEdgeDirection(m_newEdge, m_extendedSupergraphNode);
  	} else {
  		throw new UnsupportedOperationException("This method is only applicable for extensions of directed graphs");
  	}
  }
  
  /*
   *  (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
	public boolean equals(Object obj) {
		return equals((Extension) obj);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.parmol.MoFa.Extension#equals(de.parmol.MoFa.Extension)
	 */
  public boolean equals(Extension obj) {
		if (! (obj.getClass() == this.getClass())) return false;
		EdgeExtension ext = (EdgeExtension) obj;
		if (! (ext.getExtendedNodeIndex() == this.getExtendedNodeIndex())) return false;
		if (! (ext.getNewEdgeLabel() == this.getNewEdgeLabel())) return false;
		
		if (m_secondSubgraphNodeIndex != ext.m_secondSubgraphNodeIndex) {
			return false;
		}
		
		if (m_parentEmbedding.isDirectedGraphEmbedding()) {
			int directionA = ((DirectedGraph) m_parentEmbedding.getSuperGraph()).getEdgeDirection(m_newEdge, m_extendedSupergraphNode);
			int directionB = ((DirectedGraph) ext.m_parentEmbedding.getSuperGraph()).getEdgeDirection(ext.m_newEdge, ext.m_extendedSupergraphNode);
			
			if (directionA != directionB) return false;
		}
		
		return true;
	}

	/**
	 * Creates a new EdgeExtention. If the given ObjectPool is not <code>null</code> and not empty an object from the pool is reused and
	 * initialized with the proper values. Otherwise a new object is created.
   * @param parentEmbedding the embedding of the current subgraph
   * @param extendedSubgraphNode the node (from the subgraph) to which the new edge is added
   * @param secondSubgraphNodeIndex the index of the node in the subgraph to which the new edge is leading
   * @param extendedSupergraphNode the node (from the supergraph) to which the new edge is added
   * @param newEdge the edge (from the supergraph) that is added
	 * @param objectPool an ObjectPool for EdgeExtensions or <code>null</code>
	 * @return a new EdgeExtension
	 */
	public static EdgeExtension getInstance(MoFaEmbedding parentEmbedding, int extendedSubgraphNode, int secondSubgraphNodeIndex,
			int extendedSupergraphNode, int newEdge, ObjectPool objectPool)
	{
		if (objectPool != null) {			
			EdgeExtension instance = (EdgeExtension) objectPool.getObject();
			if (instance != null) {
				instance.setValues(parentEmbedding, extendedSubgraphNode, secondSubgraphNodeIndex, extendedSupergraphNode, newEdge);
			}	else {
				instance = new EdgeExtension(parentEmbedding, extendedSubgraphNode, secondSubgraphNodeIndex, extendedSupergraphNode, newEdge);
			}
			return instance;
		}
		return new EdgeExtension(parentEmbedding, extendedSubgraphNode, secondSubgraphNodeIndex, extendedSupergraphNode, newEdge);		
	}

	/* (non-Javadoc)
	 * @see de.parmol.MoFa.Extension#freeInstance()
	 */
	public void freeInstance(ObjectPool objectPool) {
		if (objectPool != null) {
			m_parentEmbedding = null;
			objectPool.repoolObject(this);
		}
	}	
	
	/*
	 *  (non-Javadoc)
	 * @see de.parmol.MoFa.Extension#getSubgraph()
	 */
	public Graph getSubgraph() {
		MutableGraph g;		
		int secondNode = m_parentEmbedding.getSubGraph().getNode(m_secondSubgraphNodeIndex);
		
		if (m_parentEmbedding.getSubGraph() instanceof MutableGraph) {		
			g = (MutableGraph) m_parentEmbedding.getSubGraph().clone();
		} else if (m_parentEmbedding.isDirectedGraphEmbedding()) {
			g = new DirectedListGraph((DirectedGraph) m_parentEmbedding.getSubGraph());
		} else {
			g = new UndirectedListGraph((UndirectedGraph) m_parentEmbedding.getSubGraph());
		}		

		if (m_parentEmbedding.isDirectedGraphEmbedding() && (getNewEdgeDirection() == DirectedGraph.INCOMING_EDGE)) {
			g.addEdge(secondNode, m_extendedSubgraphNode, getNewEdgeLabel());
		} else {
			g.addEdge(m_extendedSubgraphNode, secondNode, getNewEdgeLabel());
		}
		
		return g;
	}
}
