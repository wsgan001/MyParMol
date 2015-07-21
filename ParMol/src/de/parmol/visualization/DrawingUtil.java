/*
 * Created on Jun 3, 2005
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
import java.util.Vector;

/**
 * 
 * @author Olga Urzova <siolurzo@stud.informatik.uni-erlangen.de>
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class DrawingUtil {

	/**
	 * prints the cycles data to System.out
	 * @param cyclesData
	 */
	public static void printCyclesData(int[][] cyclesData) {
		System.out.println("Jetzt kommt die cyclesData");
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < cyclesData[i].length; j++) {
				System.out.print(cyclesData[i][j] + " ");
			}
			System.out.print("\n");
		}
		System.out.println("Ende von cyclesData");
	}

	/**
	 * prints the cycle vectores to System.out
	 * @param cyclesVector
	 */
	public static void printCyclesVector(Vector cyclesVector) {
		System.out.println("Jetzt kommt cyclesVector");
		for (int i = 0; i < cyclesVector.size(); i++) {
			System.out.print(cyclesVector.get(i));
		}
		System.out.println("Ende von cyclesVector");
	}

	/**
	 * prints the node cycle membership to System.out
	 * @param cyclesData
	 * @param nodeCycleMembership
	 */
	public static void printNodeCycleMembership(int[][] cyclesData,
			ArrayList[] nodeCycleMembership) {
		for (int i = 0; i < cyclesData[0].length; i++) {
			System.out.print("Knoten " + i + ":");
			for (int j = 0; j < nodeCycleMembership[i].size(); j++) {
				System.out.print(((Integer) nodeCycleMembership[i].get(j))
						.intValue()
						+ " ");
			}
			System.out.println();
		}
		System.out.println();
	}

	/**
	 * prints edge cycle membership to System.out
	 * @param cyclesData
	 * @param nodeCycleMembership
	 */
	public static void printEdgeCycleMembership(int[][] cyclesData,
			ArrayList[] nodeCycleMembership) {
		for (int i = 0; i < cyclesData[1].length; i++) {
			System.out.print("Kante " + i + ":");
			for (int j = 0; j < nodeCycleMembership[i].size(); j++) {
				System.out.print(((Integer) nodeCycleMembership[i].get(j))
						.intValue()
						+ " ");
			}
			System.out.println();
		}
		System.out.println();
	}

	/**
	 * prints the visited array to System.out
	 * @param visited
	 * @param nodesNumber
	 */
	public void VisitedArray(byte[] visited, int nodesNumber) {
		for (int i = 0; i < nodesNumber; i++) {
			System.out.print(visited[i] + " ");
		}
		System.out.println();
	}

	/**
	 * prints the visited edges to System.out
	 * @param array
	 */
	public static void printVisitedEdges(byte[] array) {
		for (int i = 0; i < array.length; i++) {
			System.out.print(array[i] + "");
		}
		System.out.println();
	}

	/**
	 * prints the chain to System.out
	 * @param chains
	 */
	public static void printChains(ArrayList chains) {
		System.out.println("CHAINS::");
		for (int i = 0; i < chains.size(); i++) {

			System.out.println("[" + i + "] " + chains.get(i));
			System.out.println();
		}
	}

	/**
	 * prints the cross point to System.out
	 * @param points
	 */
	public static void printCrossPoints(HashMap points) {
		System.out.println(points);
	}

	/**
	 * prints the groups to System.out
	 * @param groups
	 */
	public static void printGroups(ArrayList groups) {
		for (int i = 0; i < groups.size(); i++) {
			HashSet[] current = (HashSet[]) groups.get(i);
			System.out.print("[" + i + "]");
			System.out.println(current);
		}
	}

	/**
	 * prints the int matrix to System.out
	 * @param matrix
	 * @param x
	 * @param y
	 */
	public static void printIntMatrix(int[][] matrix, int x, int y) {
		System.out.println("Matrix commt:");
		for (int i = 0; i < y; i++) {
			for (int j = 0; j < x; j++) {
				System.out.print(matrix[i][j] + " ");
			}
			System.out.println();
		}
		System.out.println();
	}

	/**
	 * prints the hashset array to System.out
	 * @param array
	 */
	public static void printHashSetArray(HashSet[] array) {
		for (int i = 0; i < array.length; i++) {
			System.out.print(array[i] + ", ");
		}
		System.out.println();
	}

	/**
	 * prints the int array to System.out
	 * @param a
	 */
	public static void printIntArray(int[] a) {
		for (int i = 0; i < a.length; i++) {
			System.out.print(a[i] + " ");
		}
		System.out.println();
	}

	/**
	 * prints the current chain data to System.out
	 * @param data
	 */
	public static void printCurrentChainData(ArrayList data) {
		for (int i = 0; i < data.size(); i++) {
			System.out.println(data.get(i));
		}
	}
}
