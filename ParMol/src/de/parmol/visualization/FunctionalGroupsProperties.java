/*
 * Created on Jun 16, 2005
 * 
 * Copyright 2005 Olga Urzova
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
package de.parmol.visualization;

import java.util.HashSet;

import de.parmol.graph.Graph;

/**
 * 
 * @author Olga Urzova <siolurzo@stud.informatik.uni-erlangen.de>
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class FunctionalGroupsProperties {
	Graph m_graph;
	
	/**
	 * a new FunctionalGroupsProperties 
	 * @param graph
	 */
	public FunctionalGroupsProperties(Graph graph) {
		this.m_graph = graph;
	}
	
	private static void addHashSets(HashSet[] headSets, HashSet[] newData, int[] groupsNumber) {
		for (int i = 0; i < headSets.length; i++) {
			headSets[i].addAll(newData[i]);
			groupsNumber[i] = headSets[i].size();
		}
	}
	
	private boolean removeNodeFromGroupsArray(Integer node, HashSet functionalGroup, HashSet[] groups){
		boolean result = false;
		if (functionalGroup.contains(node)) {
			for (int i = 0; i < groups.length; i++) {
				groups[i].remove(node);
			}
			result = true;
		}
		return result;
	}
	
	/**
	 * finds the functional group complex case
	 * @param nodeA
	 * @param degree
	 * @param nodes
	 * @param edges
	 * @param degrees
	 * @param groups
	 */
	public void findFunctionalGroupComplexCase(int nodeA, int degree, int[] nodes, int[] edges, int[] degrees, HashSet[] groups) {
		int nodeALabel = m_graph.getNodeLabel(nodeA);
		int[] groupsNumber = new int[24];
		Integer parentNode = new Integer(nodes[nodes.length - 1]);
		boolean isParentRemove = false;
		boolean currentIsPartOfGroup = false;
		Integer currentNode = new Integer(nodeA);
		HashSet[] functionalGroups = new HashSet[24];
		for (int j = 0; j < 24; j++) {
			functionalGroups[j] = new HashSet();
		}
		for (int i = 0; i < degree; i++) {
			HashSet[] nodeGroups = new HashSet[24];
			for (int j = 0; j < 24; j++) {
				nodeGroups[j] = new HashSet();
			}
			findFunctionalGroupSimpleCase(nodeA, nodes[i], edges[i], nodeGroups);
			addHashSets(functionalGroups, nodeGroups, groupsNumber);
		}
		
		switch(nodeALabel) {
		// Der Knoten A ist ein C-Atom
		case 6: 
			if (groupsNumber[7] > 0) {
					currentIsPartOfGroup = true;
					groupsNumber[7] -= 1;
					if (!isParentRemove) isParentRemove = removeNodeFromGroupsArray(parentNode, functionalGroups[7], groups);
					/*    O  Haloformylgruppe
					 *   //
					 * - C
					 *   \
					 *    X
					 */ 
					if (groupsNumber[4] > 0) {
						groupsNumber[4] -= 1;
						if (!isParentRemove) isParentRemove = removeNodeFromGroupsArray(parentNode, functionalGroups[4], groups);
						groups[14].add(currentNode);
					} else {
						/*    O  Carboxygruppe
						 *   //
						 * - C
						 *   \\
						 *    OH
						 */ 
						if (functionalGroups[5].size() > 0) {
							groupsNumber[5] -= 1;
							if (!isParentRemove) isParentRemove = removeNodeFromGroupsArray(parentNode, functionalGroups[5], groups);
							groups[13].add(currentNode);
						} else {
							/*    O    R-...oxycarbonylgruppe
							 *   //
							 * - C
							 *   \
							 *    OR
							 */ 
							if (functionalGroups[6].size() > 0) {
								groupsNumber[6] -= 1;
								if (!isParentRemove) isParentRemove = removeNodeFromGroupsArray(parentNode, functionalGroups[6], groups);
								groups[15].add(currentNode);
							} else {
								/*    O  Carbomoylgruppe
								 *   //
								 * - C
								 *   \
								 *    NH2
								 */ 
								if (functionalGroups[8].size() > 0) {
									groupsNumber[8] -= 1;
									if (!isParentRemove) isParentRemove = removeNodeFromGroupsArray(parentNode, functionalGroups[8], groups);
									groups[16].add(currentNode);
								} else {
									/*    O  Formylgruppe
									 *   //
									 * - C
									 *   \
									 *    H
									 */ 
									if (degree < 3){
										groups[17].add(currentNode);
										currentIsPartOfGroup = true;
									} else {
										/*   \     Oxogruppe
										 *    C = O
										 *   /
										 */
									
											currentIsPartOfGroup = true;
											groups[23].add(currentNode);
									}
								}
							}
						}
					}
				} else {
					if (groupsNumber[10] > 0) {
						groupsNumber[10] -= 1;
						currentIsPartOfGroup = true;
						groups[10].add(currentNode);
					} else {
						
						// Der Knoten gehoert zu keiner functionalen Gruppen
						currentIsPartOfGroup = false;
					}
					if (groupsNumber[5] > 0) currentIsPartOfGroup = false;
				}
			// den Status von parentAtom bestimmen
			if (!currentIsPartOfGroup) {
				groups[0].add(new Integer(nodeA));
			}
			break;
		case 16 : // Der Knoten ist ein S-Atom 
			
				if (functionalGroups[7].size() == 2){
					groupsNumber[7] -= 2;
					
					if (!isParentRemove) isParentRemove = removeNodeFromGroupsArray(parentNode, functionalGroups[7], groups);
						/*    O        Sulfogruppe
						 *    ||
						 *  - S - OH
						 *    ||
						 *    O
						 */
						if (functionalGroups[5].size() > 0) {
							groupsNumber[5] -= 1;
							if (!isParentRemove) isParentRemove = removeNodeFromGroupsArray(parentNode, functionalGroups[5], groups);
							groups[18].add(currentNode);
						} else {
							/*    O        Alkylsulfogruppe
							 *    ||
							 *  - S - O - R
							 *    ||
							 *    O 
							 */
							
							if (functionalGroups[6].size() > 0) {
								groupsNumber[6] -= 1;
								if (!isParentRemove) isParentRemove = removeNodeFromGroupsArray(parentNode, functionalGroups[6], groups);
								groups[19].add(currentNode);
								
							} else {
								/*    O        Sulfonylchloridgruppe
								 *    ||
								 *  - S - Cl
								 *    ||
								 *    O 
								 */
								if (functionalGroups[4].size() > 0) {
									groupsNumber[4] -= 1;
									if (!isParentRemove) isParentRemove = removeNodeFromGroupsArray(parentNode, functionalGroups[4], groups);
									groups[20].add(currentNode);
								} else {
									/*    O        Sulfonylgruppe
									 *    ||
									 *  - S - 
									 *    ||
									 *    O 
									 */
									groups[21].add(currentNode);
								}
							}
						}
				currentIsPartOfGroup = true;
				}
				if (!currentIsPartOfGroup) {
					groups[1].add(new Integer(nodeA));
				}
				break;
		}
		// Den Rest von funktionellen Gruppen speichern
		int begin;
		if (degree > 2) {
			begin = 4;
		} else begin = 0;
		for (int i = begin; i < 13; i++) {
			if (groupsNumber[i] > 0) {
				groups[i].addAll(functionalGroups[i]);
			}
		}
	}
	
	/**
	 * finds the functional group simple case
	 * @param nodeA
	 * @param nodeB
	 * @param edge
	 * @param groups
	 */
	public void findFunctionalGroupSimpleCase(int nodeA, int nodeB, int edge, HashSet[] groups) {
		Integer currentNeighbor = new Integer(nodeB);
		if (DataAnalyser.nodeCycleMembership[nodeB].size() > 0) {
			groups[22].add(currentNeighbor);
			return;
		}
		int degree = m_graph.getDegree(nodeB);
		int edgeLabel = m_graph.getEdgeLabel(edge);
		int nodeALabel = m_graph.getNodeLabel(nodeA);
		int nodeBLabel = m_graph.getNodeLabel(nodeB);
		
		
			switch (nodeBLabel) {
				// Der Nachbar ist ein C-Atom
				case 6: // inc. die Anzahl von C-Atomen 
						groups[0].add(currentNeighbor);
						
						// einfache bindung
						if (edgeLabel == 1) return;
						// doppelbindung
						if (edgeLabel == 2) groups[2].add(currentNeighbor);
						// dreifachbindung
						if (edgeLabel == 3) groups[3].add(currentNeighbor);
						break;
				// Der Nachbar ist ein N-Atom		
				case 7: 
						if (edgeLabel == 1) {
							// Amino-gruppe -NH2 | Diazo -N2 | Azido -N3 | Nitroso -NO | Nitro -NO2  
							if (degree == 1) {
								groups[8].add(currentNeighbor);
								
							} else {
								// inc. die Anzahl von nicht C-Atomen
								groups[1].add(currentNeighbor);
							}
						}
						if (edgeLabel == 2) {
							// imino-gruppe =NH
							if (degree == 1) {
								groups[9].add(currentNeighbor);
							} else {
								// Anzahl nicht C-Atomen
								groups[1].add(currentNeighbor);
								// Doppelbindung
								groups[2].add(currentNeighbor);
							}
						}
						
						if ((edgeLabel == 3) && (nodeALabel == 6)) {
							// Cyanogruppe #N
							if (degree == 1) {
								groups[10].add(currentNeighbor);
								
							} else {
								// -C#N- Der Fall kommt wahrsch. nie vor
								groups[3].add(currentNeighbor);
							}
						}
						
						break;
				// Der Nachbar ist ein O-Atom		
				case 8: 
						if (edgeLabel == 1) {
							// Hydroxy -OH
							if (degree == 1) {
								groups[5].add(currentNeighbor);
							} else {
								// Alkyloxy -RO
								groups[6].add(currentNeighbor);
							}
						}
						if (edgeLabel == 2) {
							// Oxogruppe =0
							if (degree == 1) {
								
								groups[7].add(currentNeighbor);
							} else {
								// inc. die Anzahl von Doppelbindungen
								groups[2].add(currentNeighbor);
								// inc. die Anzahl von nicht C-Atomen
								groups[1].add(currentNeighbor);
							}
						}
						
						break;
				// Der Nachbar ist ein S-Atom
				case 16:/*
					if (nodeALabel == 6) {
							groups[0].add(currentNeighbor);
						} else groups[1].add(currentNeighbor);
						*/
						if (edgeLabel == 1) {
							// Thiolgruppe -SH
							if (degree == 1) {
								groups[11].add(currentNeighbor);
							} else {
								// inc. die Anzahl von nicht C-Atomen
								if (degree == 2) groups[1].add(currentNeighbor);
							}
						}
						// -C=S- Der Fall kommt vermutl. nicht vor
						if (edgeLabel == 2) {
							if (degree > 1) {
								groups[1].add(currentNeighbor);
								groups[2].add(currentNeighbor);
							}
						}
						break;
				default: 
					
						if (degree == 1) {
							groups[4].add(currentNeighbor);
						} else {
							
							groups[1].add(currentNeighbor);
						}
						break;
		}
	}
}
