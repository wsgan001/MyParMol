/*
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

import de.parmol.graph.DirectedGraph;
import de.parmol.graph.DirectedListGraph;
import de.parmol.graph.Graph;
import de.parmol.graph.MutableGraph;
import de.parmol.graph.UndirectedGraph;
import de.parmol.graph.UndirectedListGraph;
import de.parmol.util.ObjectPool;

/**
 * This class represents an extension of an embedding that consists of an edge and a node connected to the new edge.
 * 
 * @author Thorsten Meinl <Thorsten.Meinl@informatik.uni-erlangen.de>
 */
public class EdgeNodeExtension implements Extension {
  protected MoFaEmbedding m_parentEmbedding;
  protected int m_newEdge, m_newNode, m_extendedSubgraphNode;
  
  /**
   * Creates a new EdgeNodeExtension.
   * @param parentEmbedding the embedding of the current subgraph
   * @param extendedSubgraphNode the node in the subgraph to which the new edge is added
   * @param newEdge the edge in the supergraph that should be added to the embedding
   * @param newNode the node in the supergraph that should be added to the embedding 
   */
  private EdgeNodeExtension(MoFaEmbedding parentEmbedding, int extendedSubgraphNode, int newEdge, int newNode) {
  	setValues(parentEmbedding, extendedSubgraphNode, newEdge, newNode);
  }

  /**
   * Sets the values of this object.
   * @param parentEmbedding the embedding of the current subgraph
   * @param extendedSubgraphNode the node in the subgraph to which the new edge is added
   * @param newEdge the edge in the supergraph that should be added to the embedding
   * @param newNode the node in the supergraph that should be added to the embedding 
   */
  private void setValues(MoFaEmbedding parentEmbedding, int extendedSubgraphNode, int newEdge, int newNode) {
    m_parentEmbedding = parentEmbedding;
    m_extendedSubgraphNode = extendedSubgraphNode;
    m_newEdge = newEdge;
    m_newNode = newNode;      	
  }
  
  /* (non-Javadoc)
   * @see de.parmol.MoFa.Extension#getParentEmbedding()
   */
  public MoFaEmbedding getParentEmbedding() { return m_parentEmbedding; }

  /* (non-Javadoc)
   * @see de.parmol.MoFa.Extension#getExtendedEmbedding()
   */
  public MoFaEmbedding getExtendedEmbedding() {
    return getExtendedEmbedding(null);
  }

  /*
   * (non-Javadoc)
   * @see de.parmol.MoFa.Extension#getExtendedEmbedding(de.parmol.util.ObjectPool)
   */
  public MoFaEmbedding getExtendedEmbedding(ObjectPool objectPool) {
    return m_parentEmbedding.extend(m_newEdge, m_newNode, m_extendedSubgraphNode, objectPool);
  }

  
  /* (non-Javadoc)
   * @see de.parmol.MoFa.Extension#getExtendedNodeIndex()
   */
  public final int getExtendedNodeIndex() {
    return m_parentEmbedding.getSubGraph().getNodeIndex(m_extendedSubgraphNode);    
  }
  
  /**
   * Returns the label of the new edge
   * @return the label of the new edge
   */
  public final int getNewEdgeLabel() {
    return m_parentEmbedding.getSuperGraph().getEdgeLabel(m_newEdge);
  }
  
  /**
   * Returns the label of the new node
   * @return the label of the new node
   */
  public final int getNewNodeLabel() {
    return m_parentEmbedding.getSuperGraph().getNodeLabel(m_newNode);
  }
  
  /**
   * Return the new node in the <b>super</b>graph
   * @return the new node in the supergraph
   */
  public final int getNewSupergraphNode() {
  	return m_newNode;
  }

  /**
   * Returns the new edge in the <b>super</b>graph
   * @return the new edge in the <b>super</b>graph
   */
  public final int getNewSupergraphEdge() {
  	return m_newEdge;
  }

  /*
   *  (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
	public boolean equals(Object obj) {
		return equals((Extension) obj);
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Extension obj) {
		if (! (obj.getClass() == this.getClass())) return false;
		EdgeNodeExtension ext = (EdgeNodeExtension) obj;
		if (! (ext.getExtendedNodeIndex() == this.getExtendedNodeIndex())) return false;
		if (! (ext.getNewEdgeLabel() == this.getNewEdgeLabel())) return false;
		if (! (ext.getNewNodeLabel() == this.getNewNodeLabel())) return false;
		
		
		if (m_parentEmbedding.isDirectedGraphEmbedding()) {
			// if the edge has the same direction in both extensions then the source node is in both extensions either the new node or not
			if ((m_parentEmbedding.getSuperGraph().getNodeA(m_newEdge) == m_newNode) ^ (ext.m_parentEmbedding.getSuperGraph().getNodeA(ext.m_newEdge) == ext.m_newNode)) {
				return false;
			}
			
		}
		return true;
	}

	/**
	 * Returns a new EdgeNodeExtension. If the given ObjectPool is not <code>null</code> and not empty an object from the pool is reused and
	 * initialized properly. Otherwise a new object is created.
   * @param parentEmbedding the embedding of the current subgraph
   * @param extendedSubgraphNode the node in the subgraph to which the new edge is added
   * @param newEdge the edge in the supergraph that should be added to the embedding
   * @param newNode the node in the supergraph that should be added to the embedding 
	 * @param objectPool an ObjectPool for EdgeNodeExtensions
	 * @return a new EdgeNodeExtension
	 */
	public static EdgeNodeExtension getInstance(MoFaEmbedding parentEmbedding, int extendedSubgraphNode, int newEdge, int newNode, ObjectPool objectPool) {
		if (objectPool != null) {			
			EdgeNodeExtension instance = (EdgeNodeExtension) objectPool.getObject();
			if (instance != null) {
				instance.setValues(parentEmbedding, extendedSubgraphNode, newEdge, newNode);
			}	else {
				instance = new EdgeNodeExtension(parentEmbedding, extendedSubgraphNode, newEdge, newNode);
			}
			return instance;
		}
		return new EdgeNodeExtension(parentEmbedding, extendedSubgraphNode, newEdge, newNode);		
	}

	/*
	 *  (non-Javadoc)
	 * @see de.parmol.MoFa.Extension#freeInstance(de.parmol.util.ObjectPool)
	 */
	public void freeInstance(ObjectPool objectPool) { 
		if (objectPool != null) {
			m_parentEmbedding = null;
			objectPool.repoolObject(this);
		}	
	}

  /**
   * Returns the direction of the new edge as the extended node sees it.
   * @return {@link DirectedGraph#INCOMING_EDGE} or {@link DirectedGraph#OUTGOING_EDGE}
   * @throws UnsupportedOperationException if the underlying graph is not directed
   */
  public final int getNewEdgeDirection() {
  	if (m_parentEmbedding.isDirectedGraphEmbedding()) {
  		// This is the edge direction as seen from the new node! So the edge direction as seen from the extended node
  		// is the opposite.
  		if (((DirectedGraph) m_parentEmbedding.getSuperGraph()).getEdgeDirection(m_newEdge, m_newNode) == DirectedGraph.INCOMING_EDGE) {
  			return DirectedGraph.OUTGOING_EDGE;
  		} else {
  			return DirectedGraph.INCOMING_EDGE;
  		}
  	} else {
  		throw new UnsupportedOperationException("This method is only applicable for extensions of directed graphs");
  	}
  }
	
	/* (non-Javadoc)
	 * @see de.parmol.MoFa.Extension#getSubgraph()
	 */
	public Graph getSubgraph() {
		MutableGraph g;
		if (m_parentEmbedding.getSubGraph() instanceof MutableGraph) {		
			g = (MutableGraph) m_parentEmbedding.getSubGraph().clone();
		} else if (m_parentEmbedding.isDirectedGraphEmbedding()) {
			g = new DirectedListGraph((DirectedGraph) m_parentEmbedding.getSubGraph());			
		} else {
			g = new UndirectedListGraph((UndirectedGraph) m_parentEmbedding.getSubGraph());
		}
		
		int newNode = g.addNode(getNewNodeLabel());
		
		if (m_parentEmbedding.isDirectedGraphEmbedding() && (getNewEdgeDirection() ==	DirectedGraph.INCOMING_EDGE)) {
			g.addEdge(newNode, m_extendedSubgraphNode, getNewEdgeLabel());
		} else {
			g.addEdge(m_extendedSubgraphNode, newNode, getNewEdgeLabel());			
		}
		
		return g;
	}
}

