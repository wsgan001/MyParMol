/*
 * Created on Sep 19, 2005
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

/**
 * @author Olga Urzova <siolurzo@stud.informatik.uni-erlangen.de>
 * 
 * Diese Klasse enthaelt Informationen ueber einen Block, d.h. die miteinander
 * verknuepften Ringe
 * 
 */

public class Block implements DrawingUnit {

	private int blockIndex;

	private int blockSize;

	private ArrayList blockMembers;

	private int beginOfBlock;

	private HashMap blockPoints;

	private HashMap regularInteriorAngles;

	HashSet[] groupsData;

	/**
	 * @author siolurzo Eine Hilfsklasse um die in einem Block berechnete
	 *         Koordinaten zu speichern
	 */
	class BlockPoint {
		float x, y;

		BlockPoint(float cur_x, float cur_y) {
			x = cur_x;
			y = cur_y;
		}

		public String toString() {
			return "[" + x + ", " + y + "]";
		}
	}

	/**
	 * Constructor
	 * 
	 * @param blockIndex
	 * @param members
	 *            Ringe aus diesem Block
	 */
	public Block(int blockIndex, ArrayList members) {
		this.blockIndex = blockIndex;
		blockMembers = members;
		blockSize = blockMembers.size();
		blockPoints = new HashMap();
		regularInteriorAngles = new HashMap(blockSize);
		initGroups();
	}

	/**
	 * @return the block index
	 */
	public int getBlockIndex() { return blockIndex; }
	
	/**
	 * @return the begin of the block
	 */
	public int getBeginOfBlock() { return beginOfBlock; }
	/**
	 * sets the begin of the block
	 * @param bob
	 */
	public void setBeginOfBlock(int bob) { beginOfBlock=bob; }

	/**
	 * @return the cycles from this block
	 */
	public ArrayList getCyclesFromThisBlock() {
		return blockMembers;
	}

	/**
	 * berechnet die Summe der Innenwinkeln fuer die bestimmten Ringe aus diesem
	 * Block
	 * 
	 * @param cycles
	 * @return sums of interior angels
	 */
	public double getInteriorAngleForBeginOfBlock(ArrayList cycles) {
		double result = 0;
		for (int i = 0; i < cycles.size(); i++) {
			Integer cycleIndex = (Integer) cycles.get(i);
			result += ((Double) regularInteriorAngles.get(cycleIndex))
					.doubleValue();
		}
		return result;
	}

	/**
	 * gibt den vorher gespeicherten Innenwinkel eines Ringes zurueck
	 * 
	 * @param cycleIndex
	 * @return an interior angle
	 */
	public double getInteriorAngle(Integer cycleIndex) {
		return ((Double) regularInteriorAngles.get(cycleIndex)).doubleValue();
	}

	/**
	 * Diese Funktion liefert den Index eines Ringes aus diesem Block, der als
	 * erster bei der Kordinatenvergabe bearbeitet wird.
	 * 
	 * @return the first index
	 */
	public Integer getBeginCycle() {
		int edge, nodeB;
		for (int i = 0; i < DataAnalyser.m_graph.getDegree(beginOfBlock); i++) {
			edge = DataAnalyser.m_graph.getNodeEdge(beginOfBlock, i);
			nodeB = DataAnalyser.m_graph.getOtherNode(edge, beginOfBlock);
			// Der Knoten B ist noch nicht besucht
			if ((DataAnalyser.visitedNodes[nodeB] == 0)
					&& (DataAnalyser.edgeCycleMembership[edge].size() == 1)) {
				return (Integer) DataAnalyser.edgeCycleMembership[edge].get(0);
			}
		}
		return null;
	}

	/**
	 * Diese Funktion gibt alle Knoten mit den Kindern zurueck, wenn diese
	 * Kinder nicht zu diesem Block gehoeren
	 * 
	 * @return something
	 */
	public HashMap getNeighborsNotInThisBlock() {
		Integer cycleIndex;
		HashSet helper = new HashSet();
		for (int i = 0; i < blockSize; i++) {
			cycleIndex = (Integer) blockMembers.get(i);
			Cycle cycle = (Cycle) DataAnalyser.cyclesVector.get(cycleIndex
					.intValue() - 1);
			ArrayList nodes = cycle.nodesList;
			helper.addAll(nodes);
			regularInteriorAngles.put(cycleIndex, new Double(cycle
					.getInteriorAngle()));
		}
		HashMap result = new HashMap(helper.size());
		Iterator iter = helper.iterator();
		while (iter.hasNext()) {
			ArrayList list = new ArrayList(10);
			int nodeA = ((Integer) iter.next()).intValue();
			int degree = DataAnalyser.m_graph.getDegree(nodeA);
			for (int d = 0; d < degree; d++) {
				int edge = DataAnalyser.m_graph.getNodeEdge(nodeA, d);
				int nodeB = DataAnalyser.m_graph.getOtherNode(edge, nodeA);
				if (DataAnalyser.visitedNodes[nodeB] == 0) {
					// Knoten ausschliesen, die zu cycles[]
					// gehoeren
					int bitmask = DataAnalyser.cyclesData[0][nodeB];
					if (bitmask > 0) {
						int j = 0;
						while ((j < blockSize)
								&& ((bitmask & (1 << (((Integer) blockMembers
										.get(j)).intValue() - 1))) != (1 << (((Integer) blockMembers
										.get(j)).intValue() - 1)))) {
							j++;
						}
						if (j == blockSize)
							list.add(new Integer(nodeB));

					} else
						list.add(new Integer(nodeB));
				}
			}
			if (!list.isEmpty())
				result.put(new Integer(nodeA), list);
		}
		return result;
	}

	public HashSet[] getGroups() {
		return groupsData;
	}

	/**
	 * speichert Kinder eines Knotens, die nicht zu diesem Block gehoeren
	 * 
	 * @param node
	 * @param children
	 */
	public void saveChild(Integer node, int[][] children) {
		ArrayList cycles = DataAnalyser.nodeCycleMembership[node.intValue()];
		for (int i = 0; i < cycles.size(); i++) {
			Integer cycleIndex = (Integer) cycles.get(i);
			Cycle cycle = (Cycle) DataAnalyser.cyclesVector.get(cycleIndex
					.intValue() - 1);
			cycle.saveChild(node, children);
		}
	}

	/**
	 * insert a new point for the given node n
	 * @param n
	 * @param x
	 * @param y
	 */
	public void setNodeCoordinates(int n, float x, float y) {
		blockPoints.put(new Integer(n), new BlockPoint(x, y));

	}

	/**
	 * Die Funktion gibt die schon berechneten Koordinaten eines Knotens aus
	 * diesem Block
	 * 
	 * @param node
	 * @return coordinates of a node
	 */
	public float[] getXY(int node) {
		float result[] = new float[2];
		BlockPoint p = (BlockPoint) blockPoints.get(new Integer(node));
		if (p == null) {
			return null;
		} else
			result[0] = p.x;
		result[1] = p.y;
		return result;
	}

	/* (non-Javadoc)
	 * @see de.parmol.visualization.DrawingUnit#initGroups()
	 */
	public void initGroups() {
		groupsData = new HashSet[24];
		for (int i = 0; i < 24; i++) {
			groupsData[i] = new HashSet();
		}
		// groupsData[22].add(new Integer(blockIndex));
		groupsData[22].addAll(blockMembers);
	}

	/**
	 * ueberprueft ob ein Ring aus diesem Block stamm
	 * 
	 * @param cycleIndex
	 * @return <code>true</code>, if the given ring is part of the block
	 */
	public boolean isInThisBlock(Integer cycleIndex) {
		if (blockMembers.contains(cycleIndex))
			return true;
		return false;
	}

	/**
	 * Diese Funktion generiert die aromatischen Bindungen innenhalb eines
	 * regulaeren oder nichtregulaeren Rings aus dem Block
	 * 
	 * @param list
	 * @return something
	 */
	public Vector setAromaticBonds(int[] list) {
		Vector result = new Vector();
		int size = list.length;
		BlockPoint p0_init = (BlockPoint) blockPoints.get(new Integer(
				list[list.length - 1]));
		BlockPoint p1_init = (BlockPoint) blockPoints.get(new Integer(list[0]));
		BlockPoint p0 = p0_init;
		BlockPoint p1 = p1_init;
		BlockPoint p2 = null;
		double[] angles = new double[list.length];
		for (int i = 0; i < size; i++) {
			p2 = (BlockPoint) blockPoints
					.get(new Integer(list[(i + 1) % size]));
			angles[i] = getAlpha(p1.x, p1.y, p0.x, p0.y, p2.x, p2.y) / 2.0;
			p0 = p1;
			p1 = p2;
		}
		p1 = p1_init;
		for (int i = 1; i < size; i++) {
			p2 = (BlockPoint) blockPoints.get(new Integer(list[i]));
			result.add(new VectorElement(p1.x, p1.y, p2.x, p2.y, 5, 0, 0, 0, 0,
					angles[i - 1], angles[i]));
			p1 = p2;
		}
		p2 = p1_init;
		result.add(new VectorElement(p1.x, p1.y, p2.x, p2.y, 5, 0, 0, 0, 0,
				angles[size - 1], angles[0]));
		return result;
	}

	/**
	 * Diese Hilfsfunktion berechnet den Winkel zwischen zwei Vektoren.
	 * 
	 * @param x0
	 * @param y0
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return an angle
	 */
	public double getAlpha(double x0, double y0, double x1, double y1,
			double x2, double y2) {
		double[] vector1 = { x1 - x0, y1 - y0 };
		double[] vector2 = { x2 - x0, y2 - y0 };
		double skalarproduct = vector1[0] * vector2[0] + vector1[1]
				* vector2[1];
		double length1 = Math.sqrt(vector1[0] * vector1[0] + vector1[1]
				* vector1[1]);
		double length2 = Math.sqrt(vector2[0] * vector2[0] + vector2[1]
				* vector2[1]);
		return Math.acos(skalarproduct / (length1 * length2));
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String out = "blockIndex:" + blockIndex + ", Cycles: ";
		for (int i = 0; i < blockMembers.size(); i++) {
			out += blockMembers.get(i) + " ";
		}
		out += "\n";
		return out;
	}

}
