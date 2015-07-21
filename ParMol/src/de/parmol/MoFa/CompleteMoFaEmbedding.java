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
 * 
 */
package de.parmol.MoFa;

import java.util.Collection;
import java.util.IdentityHashMap;

import de.parmol.graph.CompleteEmbedding;
import de.parmol.graph.Graph;
import de.parmol.graph.GraphEmbedding;
import de.parmol.graph.Util;
import de.parmol.util.ObjectPool;

/**
 * This class extends CompleteEmbeddings by implementing MoFaEmbedding, i.e. it stores information about the most recently extended node
 * which is important for pruning the search tree.
 * 
 * @author Thorsten Meinl <Thorsten.Meinl@informatik.uni-erlangen.de>
 */
public class CompleteMoFaEmbedding extends CompleteEmbedding implements MoFaEmbedding {
  /**
   * Creates a new CompleteMoFaEmbedding which consists only of one node.
   * 
   * @param supergraph the supergraph of this embedding
   * @param node the node in the supergraph
   */
  private CompleteMoFaEmbedding(Graph supergraph, int node) {
    super(supergraph, node);
  }

  /**
   * Creates a new CompleteMoFaEmbeddings that is an extension of the given embedding by a new edge and
   * and a new node. Either the edge or the node can be NO_EDGE or NO_NODE, so that only a new node or a new edge are added. 
   * 
   * @param embedding the parent embedding that is to be extended
   * @param newEdge the new edge (from the supergraph)
   * @param newNode the new node (from the supergraph)
   * @param extendedSubgraphNode the node in the subgraph that was extended the last
   */
  private CompleteMoFaEmbedding(CompleteMoFaEmbedding embedding, int newEdge, int newNode, int extendedSubgraphNode) {
    super(embedding, newEdge, newNode);
  }

  /**
   * Creates a new CompleteMoFaEmbedding by extending the given embedding with the given nodes and edges.
   * @param embedding the embedding that should be extended
   * @param newEdges an array of new edges from the <b>super</b>graph
   * @param newNodes an array of new nodes from the <b>super</b>graph
   * @param extendedSubgraphNode the node in the <b>sub</b>graph that was extended the last
   */
  private CompleteMoFaEmbedding(CompleteMoFaEmbedding embedding, int[] newEdges, int[] newNodes, int extendedSubgraphNode) {
    super(embedding, newEdges, newNodes);
  }
  
  
  /**
   * Creates a new CompleteMoFaEmbedding that is a copy of the given embedding
   * 
   * @param template the embedding to be copied
   */
  protected CompleteMoFaEmbedding(CompleteMoFaEmbedding template) {
    super(template);
  }
  
  
  /**
   * Creates a new CompleteMoFaEmbedding from the given node map.
   * @param nodeMap a two dimensional array that hold the nodes of the <b>sub</b>graph in <code>nodeMap[0][i]</code> and the corresponding
   * nodes of the <b>super</b>graph in <code>nodeMap[1][i]</code>
   * @param subgraph the <b>sub</b>graph
   * @param supergraph the <b>super</b>graph
   */
	protected CompleteMoFaEmbedding(int[][] nodeMap, Graph subgraph, Graph supergraph) {
		super(nodeMap, subgraph, supergraph);
	}

/**
	 * @param nodeMap
	 * @param subgraph
	 * @param supergraph
	 */
	public CompleteMoFaEmbedding(int[] nodeMap, Graph subgraph, Graph supergraph) {
		super(nodeMap, subgraph, supergraph);
	}

//  /* (non-Javadoc)
//   * @see de.parmol.MoFa.MoFaEmbedding#getLastExtendedNodeIndex()
//   */
//  public int getLastExtendedNodeIndex() { return getNodeIndex(m_lastExtendedNode); }
//
//  
//  /* (non-Javadoc)
//   * @see de.parmol.MoFa.MoFaEmbedding#getLastExtendedNode()
//   */
//  public int getLastExtendedNode() { return m_lastExtendedNode; }

  /*
   * (non-Javadoc)
   * @see de.parmol.MoFa.MoFaEmbedding#extend(int, int, de.parmol.util.ObjectPool)
   */
  public MoFaEmbedding extend(int newEdge, int newNode, int extendedSubgraphNode, ObjectPool objectPool) {
  	return getInstance(this, newEdge, newNode, extendedSubgraphNode, objectPool);
  }

  /*
   * (non-Javadoc)
   * @see de.parmol.MoFa.MoFaEmbedding#extend(int, int)
   */
  public MoFaEmbedding extend(int newEdge, int newNode, int extendedSubgraphNode) {
  	return getInstance(this, newEdge, newNode, extendedSubgraphNode, null);
  }

  /**
   * Creates a new CompleteMoFaEmbedding. If the given ObjectPool is not <code>null</code> and not empty an object from the pool is reused
   * and initialized with the proper values. Otherwise a new object is created.
   * @param supergraph the supergraph of this embedding
   * @param node the node in the supergraph
   * @param objectPool an ObjectPool for CompleteMoFaEmbeddings
   * @return a new CompleteMoFaEmbedding
   */
	public static CompleteMoFaEmbedding getInstance(Graph supergraph, int node, ObjectPool objectPool) {		
		if (objectPool != null) {
			CompleteMoFaEmbedding instance = (CompleteMoFaEmbedding) objectPool.getObject(0);
			if (instance != null) {
				instance.m_superGraph = supergraph;
				instance.m_nodeMap[0] = node;				
				instance.m_nodeCount = 1;
				instance.m_edgeCount = 0;
			}	else {
				instance = new CompleteMoFaEmbedding(supergraph, node);
			}
			return instance;
		}
		return new CompleteMoFaEmbedding(supergraph, node);		
	}
	
	/**
   * Creates a new CompleteMoFaEmbedding. If the given ObjectPool is not <code>null</code> and not empty an object from the pool is reused
   * and initialized with the proper values. Otherwise a new object is created. 
   * @param embedding the parent embedding that is to be extended
   * @param newEdge the new edge (from the supergraph)
   * @param newNode the new node (from the supergraph)
   * @param extendedSubgraphNode the node in the subgraph that was extended by the extension that creates this new embedding
   * @param objectPool an ObjectPool for CompleteMoFaEmbeddings
   * @return a new CompleteMoFaEmbedding
	 */
	public static CompleteMoFaEmbedding getInstance(CompleteMoFaEmbedding embedding, int newEdge, int newNode, int extendedSubgraphNode, ObjectPool objectPool) {
		if (objectPool != null) {			
			int poolNr = ((embedding.m_nodeCount > embedding.m_edgeCount) ? embedding.m_nodeCount + 1 : embedding.m_edgeCount + 1);
			if (poolNr % 4 == 0) {
				poolNr = (poolNr / 4) - 1;
			} else {
				poolNr = (poolNr / 4);
			}
			
			
			CompleteMoFaEmbedding instance = (CompleteMoFaEmbedding) objectPool.getObject(poolNr);
			if (instance != null) {
				instance.m_superGraph = embedding.m_superGraph;
				
		    if (newNode != NO_NODE) {
		    	instance.m_nodeCount = embedding.m_nodeCount + 1;		    	
		      instance.m_nodeMap[instance.m_nodeCount - 1] = newNode;
		    } else {
		    	instance.m_nodeCount = embedding.m_nodeCount;      
		    }
		    System.arraycopy(embedding.m_nodeMap, 0, instance.m_nodeMap, 0, embedding.m_nodeCount);
		    
		    if (newEdge != NO_EDGE) {
		    	instance.m_edgeCount = embedding.m_edgeCount + 1;
		      instance.m_edgeMap[instance.m_edgeCount - 1] = newEdge;
		    } else {
		    	instance.m_edgeCount = embedding.m_edgeCount;		    	
		    }
		    System.arraycopy(embedding.m_edgeMap, 0, instance.m_edgeMap, 0, embedding.m_edgeCount);
		    
			}	else {
				instance = new CompleteMoFaEmbedding(embedding, newEdge, newNode, extendedSubgraphNode);
			}
			return instance;
		}
		return new CompleteMoFaEmbedding(embedding, newEdge, newNode, extendedSubgraphNode);		
	}
	

	/**
	 * Returns a new (or recycled) CompleteMoFaEmbedding that is an extension of the given embedding with the given nodes an edges.
	 * @param embedding the embedding that should be extended
	 * @param newEdges the new edges from the <b>super</b>graph in the new embedding
	 * @param newNodes the new nodes from the <b>super</b>graph in the new embedding
	 * @param extendedSubgraphNode the node in the <b>sub</graph> that is extended
	 * @param objectPool a pool for embedding objects or <code>null</code>
	 * @return an extended CompleteMoFaEmbedding
	 */
	public static CompleteMoFaEmbedding getInstance(CompleteMoFaEmbedding embedding, int[] newEdges, int[] newNodes, int extendedSubgraphNode, ObjectPool objectPool) {
		if (objectPool != null) {			
			int poolNr = Math.max(embedding.m_nodeCount + newNodes.length, embedding.m_edgeCount + newEdges.length);
			if (poolNr % 4 == 0) {
				poolNr = (poolNr / 4) - 1;
			} else {
				poolNr = (poolNr / 4);
			}
			
			
			CompleteMoFaEmbedding instance = (CompleteMoFaEmbedding) objectPool.getObject(poolNr);
			if (instance != null) {
				instance.m_superGraph = embedding.m_superGraph;
				
	    	instance.m_nodeCount = embedding.m_nodeCount + newNodes.length;
		    System.arraycopy(embedding.m_nodeMap, 0, instance.m_nodeMap, 0, embedding.m_nodeCount);
		    System.arraycopy(newNodes, 0, instance.m_nodeMap, embedding.m_nodeCount, newNodes.length);
		    
	    	instance.m_edgeCount = embedding.m_edgeCount + newEdges.length;
		    System.arraycopy(embedding.m_edgeMap, 0, instance.m_edgeMap, 0, embedding.m_edgeCount);
		    System.arraycopy(newEdges, 0, instance.m_edgeMap, embedding.m_edgeCount, newEdges.length);
			}	else {
				instance = new CompleteMoFaEmbedding(embedding, newEdges, newNodes, extendedSubgraphNode);
			}
			return instance;
		}
		return new CompleteMoFaEmbedding(embedding, newEdges, newNodes, extendedSubgraphNode);		
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see de.parmol.MoFa.MoFaEmbedding#freeInstance(de.parmol.util.ObjectPool)
	 */
	public void freeInstance(ObjectPool objectPool) {
		if (objectPool != null) {
			final int poolNr = ((m_nodeMap.length < m_edgeMap.length) ? m_nodeMap.length : m_edgeMap.length) / 4 - 1;
			objectPool.repoolObject(this, poolNr);
			m_superGraph = null;
		}		
	}

	/* (non-Javadoc)
	 * @see de.parmol.MoFa.MoFaEmbedding#extend(int[], int[], de.parmol.util.ObjectPool)
	 */
	public MoFaEmbedding extend(int[] newEdges, int[] newNodes, int extendedSubgraphNode, ObjectPool objectPool) {
		return getInstance(this, newEdges, newNodes, extendedSubgraphNode, objectPool);
	}

	/* (non-Javadoc)
	 * @see de.parmol.MoFa.MoFaEmbedding#extend(int[], int[])
	 */
	public MoFaEmbedding extend(int[] newEdges, int[] newNodes, int extendedSubgraphNode) {
		return extend(newEdges, newNodes, extendedSubgraphNode, null);		
	}

	/**
	 * Creates all CompleteMoFaEmbeddings from the given embedding.
	 * @param embedding the embedding from which all complete embeddings should be created
	 * @param completeEmbeddings a collection into which all complete embeddings are stored
	 * @return the number of complete embeddings created 
	 */
	public static int getCompleteEmbeddings(GraphEmbedding embedding, Collection completeEmbeddings) {
		return getCompleteEmbeddings(embedding, completeEmbeddings, Factory.instance);
	}
	
	/**
	 * Creates all complete MoFa embeddings for the given subgraph.
	 * @param subgraph the subgraph
	 * @param graphs a collection of graphs in which the embeddings should be created
	 * @param completeEmbeddings a collection into which the found embeddings are inserted
	 * @return the number of found embeddings
	 */
	public static int getCompleteEmbeddings(Graph subgraph, Collection graphs, Collection completeEmbeddings) {
		return getCompleteEmbeddings(subgraph, graphs, completeEmbeddings, Factory.instance);
	}
	
	protected static class Factory extends CompleteEmbedding.Factory {
		/**
		 * The only instance of the factory.
		 */
		public final static Factory instance = new Factory();
		
		private Factory() {}
		
		public CompleteEmbedding getInstance(int[][] nodeMap, Graph subgraph, Graph supergraph) {
			return new CompleteMoFaEmbedding(nodeMap, subgraph, supergraph);
		}
		
		public CompleteEmbedding getInstance(int[] nodeMap, Graph subgraph, Graph supergraph) {
			return new CompleteMoFaEmbedding(nodeMap, subgraph, supergraph);		}
	}

	/* (non-Javadoc)
	 * @see de.parmol.MoFa.MoFaEmbedding#setSubgraph(de.parmol.graph.Graph)
	 */	
	public void setSubgraph(Graph subgraph) {
		// do nothing here as the embedding can represents the subgraph by itself
	}	
	
	
	
	private static IdentityHashMap s_localSymmetries = new IdentityHashMap();
	
	/**
	 * Estimates the number of embeddings an extended fragment will have.
	 * @param lastExtendedNodeIndex the index of the last extended node
	 * @param blackNodes the black nodes
	 * @return an estimation of embeddings
	 */
	public int estimateChildEmbeddings(int lastExtendedNodeIndex, BlackNodeSet blackNodes) {
		final boolean[] visited = new boolean[m_superGraph.getNodeCount()];
		final int lastExtendedSupergraphNode = m_nodeMap[lastExtendedNodeIndex];
		
		int[] localSymmetries = (int[]) s_localSymmetries.get(m_superGraph);
		if (localSymmetries == null) {
			localSymmetries = Util.getLocalSymmetryPoints(m_superGraph);
			s_localSymmetries.put(m_superGraph, localSymmetries);
		}
		
		for (int i = 0; i < m_nodeCount; i++) {
			visited[m_superGraph.getNodeIndex(m_nodeMap[i])] = true;
		}
	
		
		int lastAddedEdgeLabel = m_superGraph.getEdgeLabel(m_edgeMap[m_edgeCount - 1]);
		int lastAddedNodeLabel = m_superGraph.getNodeLabel(m_nodeMap[m_nodeCount - 1]);
		
		
		int estimation = 1;
		for (int i = m_superGraph.getDegree(lastExtendedSupergraphNode) - 1; i >= 0; i--) {
			final int edge = m_superGraph.getNodeEdge(lastExtendedSupergraphNode, i);
			final int neighbour = m_superGraph.getOtherNode(edge, lastExtendedSupergraphNode);
			
			if (! visited[m_superGraph.getNodeIndex(neighbour)] && (lastAddedEdgeLabel <= m_superGraph.getEdgeLabel(edge)) &&
					(lastAddedNodeLabel <= m_superGraph.getNodeLabel(neighbour)) && ! blackNodes.contains(m_superGraph.getNodeLabel(neighbour)))
			{
				estimation *= localSymmetries[m_superGraph.getNodeIndex(neighbour)];
				estimation *= estimateChildEmbeddings(neighbour, visited, localSymmetries, blackNodes);
			}
		}
		
		
		
		for (int i = lastExtendedNodeIndex + 1; i < m_nodeCount; i++) {
			estimation *= estimateChildEmbeddings(m_superGraph.getNode(i), visited, localSymmetries, blackNodes);
		}
		
		return estimation;
	}

	
	private int estimateChildEmbeddings(int node, boolean[] visited, int[] localSymmetries, BlackNodeSet blackNodes) {
		visited[m_superGraph.getNodeIndex(node)] = true;
		int estimation = localSymmetries[m_superGraph.getNodeIndex(node)];
		
		for (int i = m_superGraph.getDegree(node) - 1; i >= 0; i--) {
			final int edge = m_superGraph.getNodeEdge(node, i);
			final int neighbour = m_superGraph.getOtherNode(edge, node);
			
			if (! visited[m_superGraph.getNodeIndex(neighbour)] && ! blackNodes.contains(m_superGraph.getNodeLabel(neighbour))) {
				estimation *= estimateChildEmbeddings(neighbour, visited, localSymmetries, blackNodes);
			}
		}
		
		return estimation;
	}
}