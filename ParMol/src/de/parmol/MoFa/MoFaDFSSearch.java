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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import de.parmol.Settings;
import de.parmol.graph.ClassifiedGraph;
import de.parmol.graph.CompleteEmbedding;
import de.parmol.graph.Graph;
import de.parmol.graph.GraphEmbedding;
import de.parmol.graph.NodeLabelDegreeComparator;
import de.parmol.graph.SimpleEdgeComparator;
import de.parmol.graph.SimpleGraphComparator;
import de.parmol.graph.SimpleNodeComparator;
import de.parmol.graph.SimpleSubgraphComparator;
import de.parmol.search.DFSSearchable;
import de.parmol.search.SearchTreeNode;
import de.parmol.util.DefaultObjectPool;
import de.parmol.util.FilteredFragmentSet;
import de.parmol.util.FragmentSet;
import de.parmol.util.FrequentFragment;
import de.parmol.util.MutableInteger;
import de.parmol.util.ObjectPool;


/**
 * This class extends ThreadedDFSSearch with MoFa specific parts.
 * 
 * @author Thorsten Meinl <Thorsten.Meinl@informatik.uni-erlangen.de>
 */
public class MoFaDFSSearch implements DFSSearchable {
	protected final MoFaExtender m_extender;
	protected final FilteredFragmentSet m_frequentLocalSubgraphs = new FilteredFragmentSet();
	protected int m_largestProblemSize = 1;
	protected ObjectPool m_embeddingPool;
	private double m_sigmoidC;
	private final Settings m_settings;
	private MoFaDFSSearch m_nextWorker;

	protected FilteredFragmentSet m_frequentGlobalSubgraphs;
	protected MutableInteger m_globalSubgraphsLock;
	protected final static int FRAGMENT_FLUSH_THRESHOLD = 5000;
	private int m_retryWaits = 1;


	/**
	 * Creates a new MoFaDFSSearch.
	 * 
	 * @param settings the parameters for the search
	 */
	public MoFaDFSSearch(Settings settings) {
		m_settings = settings;
		if (settings.useExtensionPooling) {
			final int size = (int) (m_settings.graphs.size() / 5000.0 * (1 << 18));

			m_extender = new MoFaExtender(m_settings, new DefaultObjectPool(size / m_settings.maxThreads, 1),
					new DefaultObjectPool((size >> 2) / m_settings.maxThreads, 1));
		} else {
			m_extender = new MoFaExtender(settings);
		}

		if (settings.useEmbeddingPooling) {
			m_embeddingPool = new DefaultObjectPool((1 << 16) / m_settings.maxThreads, 20);
			m_embeddingPool.setName("EmbeddingPool");
		}

		m_sigmoidC = 1.5 * (m_settings.minimumClassFrequencies[0] / m_settings.graphCount) - 1.5;
	}


	/**
	 * Creates a new MoFaDFSSearch. This constructor is called from newInstance.
	 * 
	 * @param firstWorker
	 */
	private MoFaDFSSearch(MoFaDFSSearch firstWorker) {
		m_settings = firstWorker.m_settings;
		if (m_settings.useExtensionPooling) {
			final int size = (int) (m_settings.graphs.size() / 5000.0 * (1 << 18));

			m_extender = new MoFaExtender(m_settings, new DefaultObjectPool(size / m_settings.maxThreads, 1),
					new DefaultObjectPool((size >> 2) / m_settings.maxThreads, 1));
		} else {
			m_extender = new MoFaExtender(m_settings);
		}

		if (m_settings.useEmbeddingPooling) {
			m_embeddingPool = new DefaultObjectPool((1 << 16) / m_settings.maxThreads, 20);
			m_embeddingPool.setName("EmbeddingPool");
		}

		m_sigmoidC = 1.5 * (m_settings.minimumClassFrequencies[0] / m_settings.graphCount) - 1.5;
		if (firstWorker.m_globalSubgraphsLock == null) {
			firstWorker.m_globalSubgraphsLock = m_globalSubgraphsLock = new MutableInteger(0);
			firstWorker.m_frequentGlobalSubgraphs = m_frequentGlobalSubgraphs = new FilteredFragmentSet(30000, 50);
		} else {
			m_globalSubgraphsLock = firstWorker.m_globalSubgraphsLock;
			m_frequentGlobalSubgraphs = firstWorker.m_frequentGlobalSubgraphs;
		}
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see de.parmol.search.BFSSearch#leaveLevel(de.parmol.search.SearchTreeNode, de.parmol.search.SearchTreeNode)
	 */
	public void leaveNode(SearchTreeNode currentNode) {
		currentNode.clear();
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see de.parmol.search.BFSSearch#generateChildren(de.parmol.search.SearchTreeNode, de.parmol.search.SearchTreeNode)
	 */
	public void generateChildren(SearchTreeNode currentNode) {
		Collection embeddings = ((MoFaSearchTreeNode) currentNode).getEmbeddings();
		if (embeddings == null) {
			embeddings = createEmbeddings(m_settings.graphs, ((MoFaSearchTreeNode) currentNode).getSubgraph());
			((MoFaSearchTreeNode) currentNode).setEmbeddings(embeddings);
		}

		final int lastExtendedNodeIndex = ((MoFaSearchTreeNode) currentNode).getLastExtendedNodeIndex();
		final boolean lastExtensionWasPerfect = ((MoFaSearchTreeNode) currentNode).extensionWasPerfect();

		ExtensionContainer extensions = new ExtensionContainer(lastExtendedNodeIndex >= 0 ? lastExtendedNodeIndex : 0,
				((MoFaSearchTreeNode) currentNode).getSubgraph().getNodeCount());
		for (Iterator it = embeddings.iterator(); it.hasNext();) {
			CompleteMoFaEmbedding emb = (CompleteMoFaEmbedding) it.next();

			m_extender.extend(emb, lastExtendedNodeIndex, lastExtensionWasPerfect, ((MoFaSearchTreeNode) currentNode)
					.getBlackNodes(), extensions);
		}

		Collection groups = extensions.getGroups();

		extensions = null;

		final IdentityHashMap frequencies = new IdentityHashMap();		
		
		// do a pre-filtering of unfrequent extensions
		for (Iterator it = groups.iterator(); it.hasNext();) {
			Collection group = (Collection) it.next();
			float[] classFrequencies = computeClassFrequencies(group);
			if (! m_settings.embeddingEstimation && ! m_settings.countEmbeddings && ! m_settings.checkMinimumFrequencies(classFrequencies)) {
				m_extender.freeExtensions(group);
				group.clear(); // just to save memory
				it.remove();
			} else {
				frequencies.put(group, classFrequencies);
			}
		}

		equivalentSiblingPruning(embeddings, groups);
		boolean perfectExtension = perfectExtensionPruning(embeddings, groups);
		// boolean perfectExtension = false;

		createExtendedEmbeddings(groups, (MoFaSearchTreeNode) currentNode, perfectExtension, lastExtendedNodeIndex,
				frequencies);

		((MoFaSearchTreeNode) currentNode).clearEmbeddings(m_embeddingPool);

		if ((m_globalSubgraphsLock != null) && (m_frequentLocalSubgraphs.size() >= FRAGMENT_FLUSH_THRESHOLD)
				&& (--m_retryWaits <= 0)) {
			boolean lockIsFree = false;

			synchronized (m_globalSubgraphsLock) {
				if (m_globalSubgraphsLock.intValue() == 0) {
					m_globalSubgraphsLock.inc();
					lockIsFree = true;
				}
			}

			if (lockIsFree) {
				long t = System.currentTimeMillis();
				synchronized (m_frequentGlobalSubgraphs) {
					m_frequentGlobalSubgraphs.add(m_frequentLocalSubgraphs);
				}

				synchronized (m_globalSubgraphsLock) {
					m_globalSubgraphsLock.dec();
				}
				m_frequentLocalSubgraphs.clear();

				if (m_settings.debug > 2) {
					System.out.println("[" + Thread.currentThread().getName()
							+ "] Merged local fragment set with global fragment set in " + (System.currentTimeMillis() - t)
							+ "ms; size is now " + m_frequentGlobalSubgraphs.size());
				}
			} else {
				m_retryWaits = (int) (Math.random() * 100);
				if (m_settings.debug > 2) {
					System.out.println("[" + Thread.currentThread().getName()
							+ "] Merging delayed because of blocked global set, retrying after " + m_retryWaits + " iterations");
				}
			}
		}
	}


	/**
	 * Creates complete embeddings from the found extensions.
	 * 
	 * @param groups all extension grouped into several classes
	 * @param currentNode the current node in the search tree
	 * @param perfectExtension <code>true</code>, if a perfect extension was made, <code>false</code> otherwise
	 * @param lastExtendedNodeIndex the index of the last extended node
	 * @param frequencies a map with the computed class frequencies for all extension groups
	 */
	protected void createExtendedEmbeddings(Collection groups, MoFaSearchTreeNode currentNode, boolean perfectExtension,
			int lastExtendedNodeIndex, Map frequencies) {
		boolean oneChildHasSameSupport = false;

		for (Iterator it = groups.iterator(); it.hasNext();) {
			Collection group = (Collection) it.next();
			float[] classFrequencies = (float[]) frequencies.get(group);
			final ArrayList newEmbeddings = new ArrayList(group.size());

			int newLastExtendedNodeIndex = (perfectExtension ? lastExtendedNodeIndex : -1);
			Graph subgraph = null;
			Class extensionType = null;

			for (Iterator it2 = group.iterator(); it2.hasNext();) {
				Extension ext = (Extension) it2.next();

				if (newLastExtendedNodeIndex == -1) {
					newLastExtendedNodeIndex = ext.getExtendedNodeIndex();
				} else if (!perfectExtension) {
					assert (newLastExtendedNodeIndex == ext.getExtendedNodeIndex());
				}

				if (subgraph == null) {
					subgraph = ext.getSubgraph();
				}

				if (extensionType == null) {
					extensionType = ext.getClass();
				}

				newEmbeddings.add(ext.getExtendedEmbedding(m_embeddingPool));
				m_extender.freeExtension(ext);
			}
			group.clear(); // just to save memory

			
			// ======================
			if (m_settings.embeddingEstimation) {
				if (m_settings.countEmbeddings && ! m_settings.checkMinimumFrequencies(classFrequencies)) {
					int estimation = 0;
					for (Iterator it2  = newEmbeddings.iterator(); it2.hasNext();) {
						CompleteMoFaEmbedding emb = (CompleteMoFaEmbedding) it2.next();
						
						estimation += emb.estimateChildEmbeddings(newLastExtendedNodeIndex, currentNode.m_blackNodes);
					}
					if (m_settings.debug > 3) {
						System.out.println("Child embeddings estimation: " + estimation);
					}
					
					if (estimation < m_settings.minimumClassFrequencies[0])
						continue;
				}
				
	
				if (! m_settings.checkMinimumFrequencies(classFrequencies)) continue;
			}
			// ======================			
			
			if (m_settings.countEmbeddings && ! m_settings.countSymmetricEmbeddings) {
				fixClassFrequencies(newEmbeddings, classFrequencies);
				if (!m_settings.checkMinimumFrequencies(classFrequencies)) continue;
			}


			
			MoFaSearchTreeNode newNode = new MoFaSearchTreeNode(currentNode, subgraph, classFrequencies, newEmbeddings,
					newEmbeddings.size(), newLastExtendedNodeIndex, perfectExtension, currentNode.getLevel() + 1);

			if (!oneChildHasSameSupport) {
				assert(new SimpleSubgraphComparator(SimpleNodeComparator.instance, SimpleEdgeComparator.instance).
						compare(currentNode.getSubgraph(), subgraph) == 0);				
				
				final float[] temp = currentNode.getFrequencies();
				boolean found = true;
				for (int i = 0; i < classFrequencies.length; i++) {
					if (temp[i] != classFrequencies[i]) {
						found = false;
						break;
					}
				}
				if (found) {
					oneChildHasSameSupport = true;
				}
			}

			currentNode.addChild(newNode);
		}


		if ((!m_settings.closedFragmentsOnly || !oneChildHasSameSupport)
				&& m_settings.checkReportingConstraints(currentNode.getSubgraph(), currentNode.getFrequencies())) {
			if (!m_frequentLocalSubgraphs.add(new FrequentFragment(currentNode.getEmbeddings(), currentNode.getFrequencies(),
					m_settings.storeEmbeddings))) {
				m_settings.stats.duplicateFragments++;
			}
		} else if (oneChildHasSameSupport) {
			m_settings.stats.earlyFilteredNonClosedFragments++;
		}
	}


	/**
	 * Checks for embeddings that overlap completely (symmetric embeddings) in the list and updates the class frequencies
	 * so that only one of the overlapping embeddings is counted.
	 * 
	 * @param newEmbeddings a collection with CompleteEmbeddings
	 * @param classFrequencies the class frequencies
	 */
	private void fixClassFrequencies(Collection newEmbeddings, float[] classFrequencies) {
		for (Iterator it = newEmbeddings.iterator(); it.hasNext();) {
			final CompleteEmbedding emb = (CompleteEmbedding) it.next();

			final boolean[] nodes = new boolean[emb.getSuperGraph().getNodeCount()];
			for (int i = emb.getNodeCount() - 1; i >= 0; i--) {
				nodes[emb.getSuperGraph().getNodeIndex(emb.getSupergraphNode(emb.getNode(i)))] = true;
			}

			for (Iterator it2 = newEmbeddings.iterator(); it2.hasNext();) {
				final CompleteEmbedding emb2 = (CompleteEmbedding) it2.next();

				if ((System.identityHashCode(emb) >= System.identityHashCode(emb2)) || (emb.getSuperGraph() != emb2.getSuperGraph())) continue;

				int overlappedNodes = 0;
				for (int i = emb2.getNodeCount() - 1; i >= 0; i--) {
					if (nodes[emb2.getSuperGraph().getNodeIndex(emb2.getSupergraphNode(emb2.getNode(i)))]) {
						// this node is already used by the outer embedding				
						overlappedNodes++;
					}
				}
				
				if (overlappedNodes == emb2.getNodeCount()) {
					m_settings.stats.uncountedSymmetricEmbeddings++;
					float[] temp = (emb.getSuperGraph() instanceof ClassifiedGraph) ? ((ClassifiedGraph) emb.getSuperGraph())
							.getClassFrequencies() : TEMP;

					for (int k = 0; k < temp.length; k++) {
						classFrequencies[k] -= temp[k];
					}
					break;
				}
			}
		}
	}

	protected final static SimpleGraphComparator GRAPH_COMPARATOR = new SimpleGraphComparator(
			NodeLabelDegreeComparator.instance, SimpleEdgeComparator.instance);


	protected void equivalentSiblingPruning(Collection currentEmbeddings, Collection extensionGroups) {
		// do equivalent sibling pruning
		// that means, if any of the siblings of the currently investigated extensions
		// represents the same graph but has weaker restrictions on new extensions (i.e. the last extended
		// node has a lower index), this group can be deleted from the search tree
		int i = 0;
		for (Iterator it = extensionGroups.iterator(); it.hasNext(); i++) {
			final List l1 = (List) it.next();
			final Extension ext1 = (Extension) l1.get(0);

			int k = i - 1;
			for (Iterator it2 = extensionGroups.iterator(); it2.hasNext() && k >= 0; k--) {
				final Extension ext2 = (Extension) ((List) it2.next()).get(0);

				if (ext1.getClass().equals(ext2.getClass()) && (ext1.getExtendedNodeIndex() != ext2.getExtendedNodeIndex())) {
					Graph g1 = ext1.getSubgraph();
					Graph g2 = ext2.getSubgraph();

					if (GRAPH_COMPARATOR.compare(g1, g2) == 0) {
						it.remove();
						i--;
						m_settings.stats.equivalentSiblingPrunedExtensions += l1.size();
						m_extender.freeExtensions(l1);
						break;
					}
				}
			}
		}
	}


	protected boolean perfectExtensionPruning(Collection currentEmbeddings, Collection extensionGroups) {
		if (m_settings.closedFragmentsOnly && m_settings.perfectExtensionPruning) {
			//			ArrayList perfectExtensions = new ArrayList(extensionGroups.size());

			// do perfect extension pruning
			final int parentSupportedMolecules = getSupportedMolecules(currentEmbeddings);
			for (Iterator it = extensionGroups.iterator(); it.hasNext();) {
				final List extensions = (List) it.next();

				if (extensions.size() % currentEmbeddings.size() != 0) continue;
				final int supportedMolecules = getSupportedMolecules(extensions);
				if (supportedMolecules != parentSupportedMolecules) continue;


				boolean degreeIsOK = true, edgeIsBridge = true;
				if (extensions.get(0) instanceof EdgeNodeExtension) {
					// check for the degree of the new node
					for (Iterator it2 = extensions.iterator(); it2.hasNext();) {
						EdgeNodeExtension ext = (EdgeNodeExtension) it2.next();

						if (ext.getParentEmbedding().getSuperGraph().getDegree(ext.getNewSupergraphNode()) > 2) {
							degreeIsOK = false;
							break;
						}
					}

					if (!degreeIsOK) {
						for (Iterator it2 = extensions.iterator(); it2.hasNext();) {
							EdgeNodeExtension ext = (EdgeNodeExtension) it2.next();

							if (!ext.getParentEmbedding().getSuperGraph().isBridge(ext.getNewSupergraphEdge())) {
								edgeIsBridge = false;
								break;
							}
						}
					}
				}


				if (!(degreeIsOK || edgeIsBridge)) {
					// System.out.println("Skipped potentially buggy PE");
					continue;
				}

				if ((supportedMolecules == 1) || (supportedMolecules == extensions.size())
						|| checkEmbeddingsPerMolecule(currentEmbeddings, extensions)) {
					for (Iterator iter = extensionGroups.iterator(); iter.hasNext();) {
						Collection col = (Collection) iter.next();
						m_settings.stats.perfectExtensionPrunedExtensions += col.size();
					}
					m_settings.stats.perfectExtensionPrunedExtensions -= extensions.size();

					extensionGroups.clear();
					extensionGroups.add(extensions);
					return true;
				}
			}

			//			if ((perfectExtensions.size() > 0) && (perfectExtensions.size() < extensionGroups.size())) {
			//				extensionGroups.clear();
			//				extensionGroups.addAll(perfectExtensions);
			//				return true;
			//			} else {
			//				return false;
			//			}
		}
		return false;
	}


	private boolean checkEmbeddingsPerMolecule(Collection parentEmbeddings, Collection newExtensions) {
		IdentityHashMap embeddingSet = new IdentityHashMap(parentEmbeddings.size());

		for (Iterator it = parentEmbeddings.iterator(); it.hasNext();) {
			Object o = it.next();
			embeddingSet.put(o, o);
		}

		for (Iterator it = newExtensions.iterator(); it.hasNext();) {
			Extension ext = (Extension) it.next();

			if (embeddingSet.remove(ext.getParentEmbedding()) == null) {
				embeddingSet.put(ext.getParentEmbedding(), ext.getParentEmbedding());
			}
		}

		return ((embeddingSet.size() == 0) || (embeddingSet.size() == parentEmbeddings.size()));
	}


	private static int getSupportedMolecules(Collection extensionsOrEmbeddings) {
		HashSet set = new HashSet(extensionsOrEmbeddings.size());

		for (Iterator it = extensionsOrEmbeddings.iterator(); it.hasNext();) {
			Object o = it.next();
			if (o instanceof Extension) {
				set.add(((Extension) o).getParentEmbedding().getSuperGraph());
			} else {
				set.add(((GraphEmbedding) o).getSuperGraph());
			}
		}

		return set.size();
	}

	private final static Comparator ExtensionComparator = new Comparator() {
		public int compare(Object o1, Object o2) {
			Extension ext1 = (Extension) ((List) o1).get(0);
			Extension ext2 = (Extension) ((List) o2).get(0);

			int diff = ext1.getClass().hashCode() - ext2.getClass().hashCode();
			if (diff != 0) return diff;

			diff = ext1.getExtendedNodeIndex() - ext2.getExtendedNodeIndex();
			if (diff != 0) return diff;

			if (ext1 instanceof EdgeNodeExtension) {
				diff = ((EdgeNodeExtension) ext1).getNewEdgeLabel() - ((EdgeNodeExtension) ext2).getNewEdgeLabel();
				if (diff != 0) return diff;

				diff = ((EdgeNodeExtension) ext1).getNewNodeLabel() - ((EdgeNodeExtension) ext2).getNewNodeLabel();
				if (diff != 0) return diff;
			} else if (ext1 instanceof EdgeExtension) {
				diff = ((EdgeExtension) ext1).getNewEdgeLabel() - ((EdgeExtension) ext2).getNewEdgeLabel();
				if (diff != 0) return diff;
			}

			return 0;
		}
	};


	/**
	 * Groups the given extensions by their type, exteneded node index, new edge label and new node label.
	 * 
	 * @param preGroupedExtensions all extensions
	 * @return a collection of collections each holding equal embeddings
	 */
	protected Collection groupExtensions(Collection preGroupedExtensions) {
		final LinkedList allGroups = new LinkedList();

		for (Iterator it3 = preGroupedExtensions.iterator(); it3.hasNext();) {
			final Collection col = (Collection) it3.next();
			if (col == null) {
				continue;
			}
			
			final LinkedList groups = new LinkedList();
			for (Iterator it = col.iterator(); it.hasNext();) {
				Extension ext = (Extension) it.next();
	
				List group = null;
				for (Iterator it2 = groups.iterator(); it2.hasNext();) {
					List l = (List) it2.next();
					Extension temp = (Extension) l.get(0);
	
					if (ext.equals(temp)) {
						group = l;
						break;
					}
				}
	
				if (group == null) {
					group = new ArrayList();
					group.add(ext);
	
					boolean added = false;
					for (ListIterator it2 = groups.listIterator(); it2.hasNext();) {
						List l = (List) it2.next();
						if (ExtensionComparator.compare(group, l) <= 0) {
							it2.previous();
							it2.add(group);
							added = true;
							break;
						}
					}
	
					if (!added) {
						groups.add(group);
					}
				} else {
					group.add(ext);
				}
			}
			
			allGroups.addAll(groups);
		}
		return allGroups;
	}


	private final static float[] TEMP = { 1.0f };


	/**
	 * Calculates the frequency of each subgraph (denoted by a collection of equal embeddings)
	 * 
	 * @param extensions a collection of equal embeddings
	 * @return an array of floats with the frequencies in each class
	 */
	protected float[] computeClassFrequencies(Collection extensions) {
		float[] frequencies = new float[m_settings.minimumClassFrequencies.length];

		if (m_settings.countEmbeddings) {
			for (Iterator it = extensions.iterator(); it.hasNext();) {
				Extension ext = (Extension) it.next();

				Graph g = ext.getParentEmbedding().getSuperGraph();
				float[] temp = (g instanceof ClassifiedGraph) ? ((ClassifiedGraph) g).getClassFrequencies() : TEMP;
				for (int i = 0; i < temp.length; i++) {
					frequencies[i] += temp[i];
				}
			}
		} else {
			IdentityHashMap graphs = new IdentityHashMap((int) (extensions.size() * 0.7));

			for (Iterator it = extensions.iterator(); it.hasNext();) {
				Extension ext = (Extension) it.next();

				Graph g = ext.getParentEmbedding().getSuperGraph();
				if (graphs.get(g) == null) {
					graphs.put(g, g);
					float[] temp = (g instanceof ClassifiedGraph) ? ((ClassifiedGraph) g).getClassFrequencies() : TEMP;
					for (int i = 0; i < temp.length; i++) {
						frequencies[i] += temp[i];
					}
				}
			}
		}
		return frequencies;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see de.parmol.search.BFSSearch#enterLevel(de.parmol.search.SearchTreeNode, de.parmol.search.SearchTreeNode)
	 */
	public void enterNode(SearchTreeNode currentNode) {
		if ((currentNode.getLevel() == 1) && (m_settings.debug > 0)) {
			System.out.println("[" + Thread.currentThread().getName() + "] Starting search with seed "
					+ m_settings.serializer.serialize(((MoFaSearchTreeNode) currentNode).getSubgraph()) /*
																																															 * + " (" +
																																															 * ((MoFaSearchTreeNode)
																																															 * currentNode).getEmbeddings().size() + "
																																															 * embeddings)"
																																															 */);
		}
	}


	/**
	 * Returns an estimation of the problem size of the given search tree node.
	 * 
	 * @param node a search tree node
	 * @return a value between 0 and 1
	 */
	public double estimateProblemSize(SearchTreeNode node) {
		if (((MoFaSearchTreeNode) node).getEmbeddings().size() > m_largestProblemSize) {
			m_largestProblemSize = ((MoFaSearchTreeNode) node).getEmbeddings().size();
		}

		// calculates sigmoid(1, m_largestProblemSize, c) with the number of embeddings as x-value
		double value = (1 / (1 + m_largestProblemSize
				* Math.pow(((MoFaSearchTreeNode) node).getEmbeddings().size(), m_sigmoidC)));
		//		if (m_settings.debug > 5) {
		//			System.out.println("estimateProblemSize(" + ((MoFaSearchTreeNode) node).getEmbeddings().size() + ", " +
		// m_sigmoidC + ") = " + value);
		//		}

		return value;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see de.parmol.search.ThreadedDFSSearch#newInstance(de.parmol.search.ThreadedDFSSearch.Settings,
	 *      de.parmol.search.ThreadedDFSSearch[], java.util.ArrayList)
	 */
	public DFSSearchable newInstance(DFSSearchable previousWorker) {
		return (m_nextWorker = new MoFaDFSSearch((MoFaDFSSearch) previousWorker));
	}


	/**
	 * Returns a set of all found frequent subgraphs.
	 * 
	 * @return a GraphSet
	 */
	public FragmentSet getFrequentSubgraphs() {
		// note that all existing workers have to be queried for their fragments
		// and the the sets have to be merged

		if (m_nextWorker != null) {
			long t = System.currentTimeMillis();

			int size = 0;
			for (MoFaDFSSearch worker = this; worker != null; worker = worker.m_nextWorker) {
				size += worker.m_frequentLocalSubgraphs.size();
			}

			if (m_settings.debug > 0) {
				System.out.print("Merging fragments sets (" + size + " fragments)...");
			}

			for (MoFaDFSSearch worker = this; worker != null; worker = worker.m_nextWorker) {
				m_frequentGlobalSubgraphs.add(worker.m_frequentLocalSubgraphs);
			}


			if (m_settings.debug > 0) {
				System.out.println("filtered out " + m_frequentGlobalSubgraphs.getDuplicateCounter() + " duplicates...done ("
						+ (System.currentTimeMillis() - t) + "ms)");
			}

			return m_frequentGlobalSubgraphs;
		} else {
			if (m_settings.debug > 0) {
				System.out.println("Filtered out " + m_frequentLocalSubgraphs.getDuplicateCounter() + " duplicates");
			}
			return m_frequentLocalSubgraphs;
		}
	}


	/**
	 * Creates one-node embeddings in all graphs
	 * 
	 * @param graphs the graphs
	 * @param subgraph the subgraph which should be embedded into the graphs
	 * @return a collection of MoFaEmbeddings
	 */
	private Collection createEmbeddings(Collection graphs, Graph subgraph) {
		ArrayList embeddings = new ArrayList();

		if (subgraph.getNodeCount() == 1) {
			final int label = subgraph.getNodeLabel(subgraph.getNode(0));

			for (Iterator it = graphs.iterator(); it.hasNext();) {
				Graph g = (Graph) it.next();

				for (int i = g.getNodeCount() - 1; i >= 0; i--) {
					if (g.getNodeLabel(g.getNode(i)) == label) {
						embeddings.add(CompleteMoFaEmbedding.getInstance(g, g.getNode(i), null));
					}
				}
			}
		} else {
			throw new UnsupportedOperationException("Currently only embeddings for graphs with one node may be created");
		}

		return embeddings;
	}
}


