/*
 * Created on Mar 21, 2005
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
package de.parmol.MoFa;

import java.util.Arrays;

/**
 * This class is a set of nodes that must not be used by new extensions.
 * 
 * @author Thorsten Meinl <Thorsten.Meinl@informatik.uni-erlangen.de>
 *
 */
public class BlackNodeSet {
	private final int[] m_nodeLabels;
	private int m_count;
	
	private final static int SWITCH_SIZE = 4;
	
	/**
	 * Create an new BlackNodeSet with the given size.
	 * @param size the size of the set
	 */
	public BlackNodeSet(int size) {
		m_nodeLabels = new int[size];
	}

	/**
	 * Create a new BlackNodeSet that is a copy of the passed set.
	 * @param copy the node set to be copied
	 */
	public BlackNodeSet(BlackNodeSet copy) {
		m_nodeLabels = new int[copy.m_nodeLabels.length];
		System.arraycopy(copy.m_nodeLabels, 0, m_nodeLabels, 0, copy.m_count);
		m_count = copy.m_count;
	}
	
	/**
	 * Adds a new label to the node set.
	 * @param nodeLabel the label that should be added
	 */
	public void addNodeLabel(int nodeLabel) {
		if (m_count < SWITCH_SIZE) {
			if (! contains(nodeLabel)) m_nodeLabels[m_count++] = nodeLabel;
		} else {
			if (m_count == SWITCH_SIZE) {		
				Arrays.sort(m_nodeLabels, 0, m_count - 1);
			}
			
			int index = binSearch(nodeLabel);
			if (index < 0) {
				index = -index - 1;
				for (int i = m_count - 1; i >= index; i--) {
					m_nodeLabels[i + 1] = m_nodeLabels[i];
				}
				m_nodeLabels[index] = nodeLabel;
				m_count++;
			}
		}
	}
	
	/**
	 * Returns if the given label is in the set
	 * @param nodeLabel a node label
	 * @return <code>true</code> if the label is in the set, <code>false</code> otherwise
	 */
	public boolean contains(int nodeLabel) {
		if (m_count <= SWITCH_SIZE) {
			for (int i = 0; i < m_count; i++) {
				if (m_nodeLabels[i] == nodeLabel) return true;
			}
			return false;
		} else {
			return (binSearch(nodeLabel) >= 0);	
		}
	}
	
	/**
	 * Does a binary search in m_nodeLabels with the same semantics as java.util.Arrays.binarySearch
	 * @param key the key that should be found
	 * @return index of the search key, if it is contained in the list;
   *	       otherwise, <tt>(-(<i>insertion point</i>) - 1)</tt>.  The
   *	       <i>insertion point</i> is defined as the point at which the
   *	       key would be inserted into the list: the index of the first
   *	       element greater than the key, or <tt>list.size()</tt>, if all
   *	       elements in the list are less than the specified key.  Note
   *	       that this guarantees that the return value will be &gt;= 0 if
   *	       and only if the key is found.
	 */
	private int binSearch(int key) {
		int left = 0; int right = m_count - 1;
		
		while (left <= right) {
			final int mid = (right + left) / 2;
			
			if (m_nodeLabels[mid] > key) {
				right = mid - 1;
			} else if (m_nodeLabels[mid] < key) {
				left = mid + 1;
			} else {
				return mid;
			}
		}
		
		return -(left + 1);
	}
}
