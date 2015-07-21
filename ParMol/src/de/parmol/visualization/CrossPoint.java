/*
 * Created on Jun 20, 2005
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

import java.util.HashMap;
import java.util.HashSet;

/**
 * @author Olga Urzova <siolurzo@stud.informatik.uni-erlangen.de>
 * 
 * Diese Klasse enthaelt Informationen ueber eine Kreuzung mehrerer Ketten
 */

class CrossPoint implements DrawingUnit {

	HashMap crossPointNeighbors;

	HashMap chainCodes;

	HashSet groupsElements;

	HashSet[] groupsData;

	int[] nodes;

	int[] sizes;

	int count;

	int degree;

	int maxSize = -1;

	int secondMaxSize = -1;

	int indexOfMaxNode = -1;

	int indexOfSecondMax = -1;

	/**
	 * Constructor
	 * 
	 * @param degree
	 */
	CrossPoint(int degree) {
		this.degree = degree;
		count = 0;
		nodes = new int[degree];
		sizes = new int[degree];
		crossPointNeighbors = new HashMap(degree);
		chainCodes = new HashMap(degree);
		groupsElements = new HashSet(degree);
		initGroups();
	}

	/**
	 * Diese Funktion spreichert ein Kind des aktuellen Knotens und die Groesse
	 * der Seitenkette, die mit diesem Kind anfaengt.
	 * 
	 * @param node
	 * @param size
	 */
	public void saveChild(int node, int size) {
		nodes[count] = node;
		if (size > maxSize) {
			secondMaxSize = maxSize;
			indexOfSecondMax = indexOfMaxNode;
			maxSize = size;
			indexOfMaxNode = count;
		} else {
			if (size > secondMaxSize) {
				secondMaxSize = size;
				indexOfSecondMax = count;
			}
		}
		sizes[count++] = size;
		crossPointNeighbors.put(new Integer(node), new Integer(size));
	}

	/**
	 * add node to group elements
	 * @param node
	 */
	public void setGroupsElement(int node) {
		groupsElements.add(new Integer(node));
	}

	/**
	 * set teh groups data
	 * @param data
	 */
	public void setGroupsData(HashSet[] data) {
		groupsData = data;
	}

	/**
	 * Diese Funktion liefert die Kinder des aktuellen Knotens in einer
	 * bestimmten, fuer die horizontale Ausrichtung der Hauptkette berechneten
	 * Reihenfolge
	 * 
	 * @param middlePoint
	 * @return children of the current node 
	 */
	public int[] getNodesListForHeadChain(int middlePoint) {
		int node;
		int[] result = new int[degree];
		boolean isMiddleSet = false;
		for (int i = 0, j = 0; i < degree; i++) {
			node = nodes[i];
			if (DataAnalyser.visitedNodes[node] == 0) {
				if ((DataAnalyser.headChain.containsNode(node))
						&& (!isMiddleSet)) {
					result[middlePoint] = node;
					isMiddleSet = true;
				} else {
					if (j == middlePoint) {
						j = (j + 1) % degree;
					} else
						j = j % degree;
					result[j++] = node;
				}
			} else {

				if (j == middlePoint) {
					j = (j + 1) % degree;
				} else
					j = j % degree;
				result[j++] = -1;

			}
		}
		return result;
	}

	/**
	 * Die Funktion liefert die Kinder des Knotens
	 * 
	 * @param middlePoint
	 * @return children of the node
	 */
	public int[] getNodesList(int middlePoint) {
		int node;
		int[] result = new int[degree];
		int localIndexOfMaxNode;
		if (DataAnalyser.visitedNodes[nodes[indexOfMaxNode]] == 1) {
			localIndexOfMaxNode = indexOfSecondMax;
		} else
			localIndexOfMaxNode = indexOfMaxNode;
		for (int i = 0, j = 0; i < degree; i++) {
			node = nodes[i];
			if (DataAnalyser.visitedNodes[node] == 0) {
				if (i == localIndexOfMaxNode) {
					result[middlePoint] = node;
				} else {
					if (j == middlePoint) {
						j = (j + 1) % degree;
					} else
						j = j % degree;
					result[j++] = node;
				}
			} else {
				if (j == middlePoint) {
					j = (j + 1) % degree;
				} else {
					j = j % degree;
				}
				result[j++] = -1;
			}
		}
		return result;
	}

	public HashSet[] getGroups() {
		return groupsData;
	}

	/**
	 * @param node
	 * @return the size of the Branch
	 */
	public Integer getSizeOfBranch(int node) {
		return (Integer) crossPointNeighbors.get(new Integer(node));
	}

	/**
	 * @param node
	 * @return the size of the Branch
	 */
	public Integer getSizeOfBranch(Integer node) {
		return (Integer) crossPointNeighbors.get(node);
	}

	public void initGroups() {
		groupsData = new HashSet[24];
		for (int i = 0; i < 24; i++) {
			groupsData[i] = new HashSet();
		}
	}

	public String toString() {
		String out = "{";
		for (int i = 0; i < degree; i++) {
			out += nodes[i] + "/" + sizes[i] + " ";
		}
		return out + "}, groups " + groupsElements;
	}
}
