/*
 * Created on May 17, 2004
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

import de.parmol.Settings;
import de.parmol.graph.DirectedGraph;
import de.parmol.graph.Graph;
import de.parmol.util.ObjectPool;


/**
 * This class extends a Graph or a GraphEmbedding in the way how the MoFa-algorithm wants it.
 * 
 * @author Thorsten Meinl <Thorsten.Meinl@informatik.uni-erlangen.de>
 *  
 */
public class MoFaExtender implements Extender {
	protected final ObjectPool m_edgeNodePool, m_edgePool;
	protected Settings m_settings;


	/**
	 * Creates a new MoFaExtender.
	 * 
	 * @param settings settings for the extension process
	 * @param edgeNodePool an ObjectPool for EdgeNodeExtensions
	 * @param edgePool an ObjectPool for EdgeExtensions
	 */
	public MoFaExtender(Settings settings, ObjectPool edgeNodePool, ObjectPool edgePool) {
		super();
		m_settings = settings;

		m_edgeNodePool = edgeNodePool;
		if (edgeNodePool != null) m_edgeNodePool.setName("EdgeNodeExtensionPool");

		m_edgePool = edgePool;
		if (edgePool != null) m_edgePool.setName("EdgeExtensionPool");
	}


	/**
	 * Creates a new MoFaExtender.
	 * 
	 * @param settings settings for the extension process
	 */
	public MoFaExtender(Settings settings) {
		this(settings, null, null);
	}


	/**
	 * Extends the given embeddings in all possible ways according to the pruning rules.
	 * 
	 * @param emb the embeddings that should be extended
	 * @param lastExtendedNodeIndex the index of the latest extended node in the subgraph
	 * @param lastExtensionWasPerfect indicated if the last extension was a perfect one
	 * @param blackNodes a set of node labels that must not be used for extensions
	 * @param extensions a container for all discovered extensions; it must created with the right parameters
	 */
	public void extend(CompleteMoFaEmbedding emb, int lastExtendedNodeIndex, boolean lastExtensionWasPerfect,
			BlackNodeSet blackNodes, ExtensionContainer extensions) {
		final Graph superGraph = emb.getSuperGraph();
		final Graph subGraph = emb.getSubGraph();
		
		final int from = (lastExtendedNodeIndex >= 0) ? lastExtendedNodeIndex : 0;
		final int to = subGraph.getNodeCount();

		for (int nodeIndex = from; nodeIndex < to; nodeIndex++) {
			final int subgraphNode = subGraph.getNode(nodeIndex);
			final int supergraphNode = emb.getSupergraphNode(subgraphNode);
			int degree = superGraph.getDegree(supergraphNode);

			for (int k = 0; k < degree; k++) {
				final int supergraphEdge = superGraph.getNodeEdge(supergraphNode, k);
				
				if (emb.getSubgraphEdge(supergraphEdge) == Graph.NO_EDGE) {
					if (checkExtension(emb, lastExtendedNodeIndex, lastExtensionWasPerfect, subgraphNode, supergraphNode,
							supergraphEdge, blackNodes)) {
						createExtension(emb, subgraphNode, supergraphNode, supergraphEdge, nodeIndex, extensions);
					}
				}
			}


//			if (emb.isDirectedGraphEmbedding()) {
//				degree = ((DirectedGraph) superGraph).getInDegree(supergraphNode);
//
//				for (int k = 0; k < degree; k++) {
//					final int supergraphEdge = ((DirectedGraph) superGraph).getIncomingNodeEdge(supergraphNode, k);
//					
//					if (emb.getSubgraphEdge(supergraphEdge) == Graph.NO_EDGE) {
//						if (checkExtension(emb, lastExtendedNodeIndex, lastExtensionWasPerfect, subgraphNode, supergraphNode,
//								supergraphEdge, blackNodes)) {
//							createExtension(emb, subgraphNode, supergraphNode, supergraphEdge, nodeIndex, extensions);
//						}
//					}
//				}
//			}
		}
	}


	/**
	 * Checks if a new potential extension is OK
	 * 
	 * @param emb the embedding of the subgraph to be extended
	 * @param lastExtendedNodeIndex the index of the latest extended node in the subgraph
	 * @param lastExtensionWasPerfect indicated if the last extension was a perfect one
	 * @param extendedSubgraphNode the node in the <b>sub </b>graph to which a new edge is added
	 * @param extendedSupergraphNode the node in the <b>super </b>graph that corresponds to the extendedSubgraphNode
	 * @param newEdge the edge in the <b>super </b>graph that should be added to the subgraph
	 * @param blackNodes a set of node labels that must not be used for extensions
	 * @return <code>true</code> if the potential extension is permitted according to MoFa's pruning rules,
	 *         <code>false</code> otherwise
	 */
	protected boolean checkExtension(CompleteMoFaEmbedding emb, int lastExtendedNodeIndex,
			boolean lastExtensionWasPerfect, int extendedSubgraphNode, int extendedSupergraphNode, int newEdge,
			BlackNodeSet blackNodes) {
		final int extendedNodeIndex = emb.getSubGraph().getNodeIndex(extendedSubgraphNode);
		assert(extendedNodeIndex >= lastExtendedNodeIndex);

		final int newNode = emb.getSuperGraph().getOtherNode(newEdge, extendedSupergraphNode); 
		// second pruning rule: check for already processed atoms
		final int newNodeLabel = emb.getSuperGraph().getNodeLabel(newNode);
		if (blackNodes.contains(newNodeLabel)) {
			m_settings.stats.structuralPrunedExtensions++;
			return false;
		}

		// check if only paths should be searched => reject all extension that add a third edge to a node
		if (m_settings.findPathsOnly && (emb.getSubGraph().getDegree(extendedSubgraphNode) >= 2)) {
			m_settings.stats.structuralPrunedExtensions++;
			return false;
		}

		// third pruning rule: check extension at the restricted extendable node
		// if the last extension was perfect the node must be unrestricted extendable (hint: O-P=O and P=O is perfect!)
		if (!lastExtensionWasPerfect && (extendedNodeIndex == lastExtendedNodeIndex)) {
			final int newEdgeLabel = emb.getSuperGraph().getEdgeLabel(newEdge);
			
			int lastAddedEdge = emb.getSubGraph().getNodeEdge(extendedSubgraphNode,
					emb.getSubGraph().getDegree(extendedSubgraphNode) - 1);

			// if (emb.isDirectedGraphEmbedding() && (((DirectedGraph) emb.getSubGraph()).getInDegree(extendedSubgraphNode) > 0)) {
			if (emb.isDirectedGraphEmbedding() &&
					(((DirectedGraph) emb.getSubGraph()).getEdgeDirection(lastAddedEdge, extendedSubgraphNode) == DirectedGraph.INCOMING_EDGE) &&
					(((DirectedGraph) emb.getSuperGraph()).getEdgeDirection(newEdge, extendedSupergraphNode) == DirectedGraph.OUTGOING_EDGE))
			{
					// outgoing edges are lower in the order of edges than incoming edges
					return false;
			}

			int el = emb.getSubGraph().getEdgeLabel(lastAddedEdge);
			if (el > newEdgeLabel) {
				m_settings.stats.structuralPrunedExtensions++;
				return false;
			}
			
			if ((el == newEdgeLabel) &&
					((! emb.isDirectedGraphEmbedding()) ||
					(((DirectedGraph) emb.getSuperGraph()).getEdgeDirection(newEdge, extendedSupergraphNode) ==
						emb.getEdgeDirection(lastAddedEdge, extendedSubgraphNode))))					
			{
				int nl = emb.getSubGraph().getNodeLabel(emb.getSubGraph().getOtherNode(lastAddedEdge, extendedSubgraphNode));
				if (nl > newNodeLabel) {
					m_settings.stats.structuralPrunedExtensions++;
					return false;
				}
			}
		}


		// fourth pruning rule: forbid edge extensions that go from a lower node to a higher node; the edge can be added
		// going from the higher to the lower node only, without loosing anything
		final int secondSubgraphNode = emb.getSubgraphNode(newNode);
		if (secondSubgraphNode != Graph.NO_NODE) {
			final int secondSubgraphNodeIndex = emb.getNodeIndex(secondSubgraphNode);
		
			if (extendedNodeIndex < secondSubgraphNodeIndex) {			
				if (! emb.isDirectedGraphEmbedding() || (emb.getEdge(extendedSubgraphNode, secondSubgraphNode) != Graph.NO_EDGE)) { 
					// for directed graphs an extension must not be forbidden if an incoming edge is added
					// because the same extension from the bigger node is already pruned in step three above 
					m_settings.stats.structuralPrunedExtensions++;
					return false;
				}
			}
		}
		
		return true;
	}


	/**
	 * Creates a new Extension out of the given embedding. It can be either a EdgeNodeExtension if the node on the other
	 * end of the new edge is not part of the embedding, or an EdgeExtension of both nodes are already part of the
	 * embedding.
	 * 
	 * @param emb the embedding that should be extended
	 * @param extendedSubgraphNode the node in the <b>sub </b>graph to which a new edge is added
	 * @param extendedSupergraphNode the node in the <b>super </b>graph that corresponds to the extendedSubgraphNode
	 * @param newEdge the edge in the <b>super </b>graph that should be added to the subgraph
	 * @param extendedSubgraphNodeIndex the index of the extended Node
	 * @param extensions a collection into which the new extension should be inserted
	 * @return the number of created extensions
	 */
	protected int createExtension(CompleteMoFaEmbedding emb, int extendedSubgraphNode, int extendedSupergraphNode,
			int newEdge, int extendedSubgraphNodeIndex, ExtensionContainer extensions) {

		final int newNode = emb.getSuperGraph().getOtherNode(newEdge, extendedSupergraphNode);

		// edge-node extension
		if (emb.getSubgraphNode(newNode) == Graph.NO_NODE) {
			extensions.add(EdgeNodeExtension.getInstance(emb, extendedSubgraphNode, newEdge, newNode, m_edgeNodePool),
					extendedSubgraphNodeIndex, emb.getSuperGraph().getEdgeLabel(newEdge),
					emb.getSuperGraph().getNodeLabel(emb.getSuperGraph().getOtherNode(newEdge, extendedSupergraphNode)));
			return 1;
		}

		// edge extension
		if (!m_settings.findPathsOnly && !m_settings.findTreesOnly) {
			final int secondNode = emb.getSubgraphNode(newNode);
			extensions.add(EdgeExtension.getInstance(emb, extendedSubgraphNode, secondNode,
					extendedSupergraphNode, newEdge, m_edgePool),
					extendedSubgraphNodeIndex, emb.getSuperGraph().getEdgeLabel(newEdge), emb.getNodeIndex(secondNode));

			return 1;
		} else {
			return 0;
		}
	}


	/**
	 * Calls freeInstance of the given extension with the right ObjectPool as parameter.
	 * 
	 * @param ext the extension that should be freed
	 */
	public void freeExtension(Extension ext) {
		if (ext instanceof EdgeNodeExtension) {
			((EdgeNodeExtension) ext).freeInstance(m_edgeNodePool);
		} else if (ext instanceof EdgeExtension) {
			((EdgeExtension) ext).freeInstance(m_edgePool);
		}
	}


	/**
	 * Marks the extension in the given collection reusable.
	 * 
	 * @param extensions a collection with extensions
	 */
	public void freeExtensions(Collection extensions) {
		for (Iterator it = extensions.iterator(); it.hasNext();) {
			freeExtension((Extension) it.next());
		}
	}
}