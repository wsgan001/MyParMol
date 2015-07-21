/*
 * Created on Jun 15, 2005
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

/**
 * @author Olga Urzova <siolurzo@stud.informatik.uni-erlangen.de>
 * 
 * Diese Klasse verwaltet Informationen ueber eine unverzweigte Kette
 * 
 */
public class Chain {
	
	private int beginOfChain;
	
	private int endOfChain;

	HashSet chain;

	HashSet[] groups;

	private int[] priority;
	
	/**
	 * a new Chain
	 */
	public Chain() {
		chain = new HashSet();
		initGroups();
	}

	/**
	 * a new Chain
	 * @param begin
	 */
	public Chain(int begin) {
		beginOfChain = begin;
		chain = new HashSet();
		chain.add(new Integer(begin));
		chain = new HashSet();
		initGroups();

	}

	/**
	 * a new Chain
	 * @param c
	 */
	public Chain(Chain c) {
		chain = c.chain;
		groups = c.groups;
		beginOfChain = c.getBeginOfChain();
		endOfChain = c.getEndOfChain();
	}

	/**
	 * initilaize the groups
	 */
	public void initGroups() {
		groups = new HashSet[24];
		for (int i = 0; i < 24; i++) {
			groups[i] = new HashSet();
		}
	}

	/**
	 * adds a node to the chain
	 * @param node
	 */
	public void addNodeToChain(int node) {
		chain.add(new Integer(node));
	}

	/**
	 * Diese Funktion verknuepft zwei Ketten miteinander
	 * @param nextChain
	 */
	public void addChainToChain(Chain nextChain) {
		if (beginOfChain == -1) beginOfChain = nextChain.getBeginOfChain();
		endOfChain = nextChain.getEndOfChain();
		chain.addAll(nextChain.chain);
		addTwoGroups(nextChain.groups);
	}
	
	/**
	 * adds groups
	 * @param data
	 */
	public void addGroups(HashSet[] data) {
		addTwoGroups(data);
	}


	private void addTwoGroups(HashSet[] nextGroups) {
		for (int i = 0; i < groups.length; i++) {
			groups[i].addAll(nextGroups[i]);
		}
	}

	/**
	 * liefert die Laenge der Kette
	 * @return the size of the chain
	 */
	public int getSize() {
		return chain.size();
	}

	
	/**
	 * @return the priority
	 */
	public int[] getPriority() {
		priority = new int[7];
		priority[0] = this.getSize();
		
		for (int i = 13; i < groups.length; i++) {
			priority[1] += groups[i].size();
		}
		// anzahl von Zyklen dazu addieren
		priority[1] += groups[22].size();
		priority[1] += groups[23].size();
		priority[2] = groups[3].size() + groups[4].size();
		priority[3] = groups[1].size();
		priority[4] = groups[3].size();
		for (int i = 7; i < 18; i++) {
			if (i == 12)
				continue;
			priority[5] += groups[i].size();
		}
		priority[5] += groups[5].size();
		priority[6] += groups[1].size();
		
		return priority;
	}

	/**
	 * prints the priority to System.out
	 *
	 */
	public void printPriority() {
		int[] priority = getPriority();
		System.out.println("Priority");
		for (int i = 0; i < 6; i++) {
			System.out.print(priority[i] + " ");
		}
		System.out.println();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "Size:" + getSize() + ", Nodes:" + chain
				+ ", Groups:" + groupsToPrint();
	}

	/**
	 * @return a string representing the groups
	 */
	public String groupsToPrint() {
		String out = "";
		for (int i = 0; i < groups.length; i++) {
			out += groups[i].toString() + ", ";
		}
		return out;
	}

	/**
	 * @param node
	 * @return <code>true</code>, if the chain contains the given node
	 */
	public boolean containsNode(int node) {
		return chain.contains(new Integer(node));
	}

	/**
	 * @param node
	 * @return <code>true</code>, if the chain contains the given node
	 */
	public boolean containsNode(Integer node) {
		return chain.contains(node);
	}
	
	/**
	 * @return the groups
	 */
	public HashSet[] getGroups() {
		return groups;
	}

	/**
	 * @return the begin of teh chain
	 */
	public int getBeginOfChain() {
		return beginOfChain;
	}

	/**
	 * sets the end of the chain
	 * @param end
	 */
	public void setEndOfChain(int end) {
		endOfChain = end;
	}
	
	/**
	 * sets the end of the chain
	 * @param begin
	 */
	public void setBeginOfChain(int begin) {
		beginOfChain = begin;
	}
	
	/**
	 * @return the end of the chain
	 */
	public int getEndOfChain() {
		return endOfChain;
	}
	
	
}