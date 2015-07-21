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

import de.parmol.graph.Graph;
import de.parmol.graph.GraphEmbedding;
import de.parmol.util.ObjectPool;

/**
 * This interface describes a GraphEmbeddings with additional methods needed for MoFa's pruning strategies.
 * 
 * @author Thorsten Meinl <Thorsten.Meinl@informatik.uni-erlangen.de>
 */
public interface MoFaEmbedding extends GraphEmbedding {
  /**
   * Creates a new MoFaEmbedding by extending this embeddings by a new edge and a new node. Either the edge or the node can be NO_NODE or NO_EDGE
   * if only a new new node or a new edge should be added.
   * @param newEdge the new edge in the supergraph, or NO_EDGE if no edge should be added
   * @param newNode the new node in the supergraph, or NO_NODE if no node should be added
   * @param extendedSubgraphNode the node in the subgraph that has been extended by this extension
   * @return a new extended MoFaEmbedding
   */
  public MoFaEmbedding extend(int newEdge, int newNode, int extendedSubgraphNode);

  /**
   * Creates a new MoFaEmbedding by extending this embeddings by a new edge and a new node. Either the edge or the node can be NO_NODE or NO_EDGE
   * if only a new new node or a new edge should be added.
   * @param newEdge the new edge in the supergraph, or NO_EDGE if no edge should be added
   * @param newNode the new node in the supergraph, or NO_NODE if no node should be added
   * @param extendedSubgraphNode the node in the subgraph that has been extended by this extension
   * @param objectPool an ObjectPool for MoFaEmbeddings; objects in this pool are reused
   * @return a new extended MoFaEmbedding
   */
  public MoFaEmbedding extend(int newEdge, int newNode, int extendedSubgraphNode, ObjectPool objectPool);

  /**
   * Extends this embedding by adding new edges and/or nodes. If no edges or no nodes should be added, the corresponding
   * parameter must not be <code>null</code> but an empty array. If the objectPool is not <code>null</code> embeddings
   * inside the pool are recyled.
   * 
   * @param newEdges the new edges from the <b>super</b> graph that should be added to the extended embedding
   * @param newNodes the new node from the <b>super</b> graph that should be added to the extended embedding
   * @param extendedSubgraphNode the node in the <b>sub</b>graph that is extended
   * @param objectPool an object pool for embedding or <code>null</code>
   * @return an extended MoFaEmbedding
   */
  public MoFaEmbedding extend(int[] newEdges, int[] newNodes, int extendedSubgraphNode, ObjectPool objectPool);
  
  /**
   * Extends this embedding by adding new edges and/or nodes. If no edges or no nodes should be added, the corresponding
   * parameter must not be <code>null</code> but an empty array.
   * @param newEdges the new edges from the <b>super</b> graph that should be added to the extended embedding
   * @param newNodes the new node from the <b>super</b> graph that should be added to the extended embedding
   * @param extendedSubgraphNode the node in the <b>sub</b>graph that is extended
   * @return an extended MoFaEmbedding
   */
  public MoFaEmbedding extend(int[] newEdges, int[] newNodes, int extendedSubgraphNode);
  
  /**
   * Puts this object into the given pool for later reuse. 
   * @param objectPool an ObjectPool for MoFaEmbeddings
   */
  public void freeInstance(ObjectPool objectPool);
  
  /**
   * Sets the subgraph that is associated with this embedding.
   * @param subgraph the subgraph
   */
  public void setSubgraph(Graph subgraph);
}

