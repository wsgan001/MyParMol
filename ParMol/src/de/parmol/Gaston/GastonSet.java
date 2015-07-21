/*
 * Created on Feb 11, 2005
 * 
 * Copyright 2005 Marc Wörlein
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
package de.parmol.Gaston;

import java.util.*;

import de.parmol.util.*;
import de.parmol.graph.SimpleEdgeComparator;
import de.parmol.graph.SimpleGraphComparator;
import de.parmol.graph.SimpleNodeComparator;

/**
 * This class representates the FragmentSet used by Gaston
 * It has two different adds for inserting unique paths and trees
 * and multiple cyclic Graphs seperatly
 *
 * @author Marc Woerlein <marc.woerlein@gmx.de>
 */
public class GastonSet extends FragmentSet {
	protected final SimpleGraphComparator m_comparator;
	private final int m_averageBinSize;
	private int m_duplicateCounter = 0;
	
    
	/** creates a new GastonSet */
	public GastonSet(){
        this(de.parmol.util.Math.PRIMES[16]
               ,new SimpleGraphComparator(SimpleNodeComparator.instance, SimpleEdgeComparator.instance),25);
    }
	private GastonSet(int initialSize, SimpleGraphComparator graphComparator, int averageBinSize) {
		int bins = initialSize / averageBinSize;
		int index = Arrays.binarySearch(de.parmol.util.Math.PRIMES, bins);
		if (index < 0) index = -index;
		if (index >= de.parmol.util.Math.PRIMES.length) index = de.parmol.util.Math.PRIMES.length - 1;
		
		m_map = new ArrayList[de.parmol.util.Math.PRIMES[index]];
		for (int i = 0; i < m_map.length; i++) {
			m_map[i] = new ArrayList((int) (averageBinSize * 1.25));
		}
		
		m_comparator = graphComparator;
		m_averageBinSize = averageBinSize;
	}
    
	/**
	 * Adds a new FrequentFragment to this set, without checking.
	 * @param fragment the new fragment to be added
	 * @return <code>true</code>, if correct added
	 */
	public boolean add(FrequentFragment fragment) {
		m_entries++;
	    return m_map[0].add(fragment);
	}

	/**
	 * Adds a new FrequentFragment to this set and checks before, if it is still inside.
	 * @param fragment the new fragment to be added
	 * @return <code>true</code> if first time added
	 */
	public boolean filteredadd(FrequentFragment fragment) {
		if (m_entries > m_averageBinSize * m_map.length) {
			resize((int) java.lang.Math.ceil(m_map.length * 1.23578));
		}
		
		final int hashCode = fragment.getFragment().hashCode();
		final int bin = 1+java.lang.Math.abs((hashCode ^ (hashCode >> 24) ^ (hashCode >> 15) ^ (hashCode >> 9)) % (m_map.length-1));
				
		for (Iterator it = m_map[bin].iterator(); it.hasNext();) {
			FrequentFragment temp = (FrequentFragment) it.next();
			
			if (temp.getFragment().hashCode() == hashCode) {				
				if (m_comparator.compare(fragment.getFragment(), fragment.getNodePartitions(), temp.getFragment(), temp.getNodePartitions()) == 0) {									
					m_duplicateCounter++;
					return false;
				}
			}
		}
		m_map[bin].add(fragment);
		m_entries++;
		return true;
	}
	/*
	 *  (non-Javadoc)
	 * @see de.parmol.util.FragmentSet#remove(de.parmol.util.FrequentFragment, int)
	 */
	protected void remove(FrequentFragment fragment, int bin) {
	    try{
		final int hashCode = fragment.getFragment().hashCode();
		bin = 1+java.lang.Math.abs((hashCode ^ (hashCode >> 24) ^ (hashCode >> 15) ^ (hashCode >> 9)) % (m_map.length-1));
		super.remove(fragment, bin);	
		
		if (m_entries < 0.1 * m_averageBinSize * m_map.length) {
			resize((int) (0.65321 * m_map.length));
		}
	    } catch (IllegalArgumentException e){
			super.remove(fragment, 0);	
	    }
	}
	
	
	protected void resize(int newSize) {
		if (newSize <= 0) newSize = 1;
		ArrayList[] temp = new ArrayList[newSize];
		for (int i = 0; i < temp.length; i++) {
			temp[i] = new ArrayList((int) (m_averageBinSize * 1.25));
		}
		
		for (int i = 0; i < m_map.length; i++) {
			for (Iterator it = m_map[i].iterator(); it.hasNext();) {
				FrequentFragment f = (FrequentFragment) it.next();
				final int hashCode = f.getFragment().hashCode();
				final int bin = 1+java.lang.Math.abs((hashCode ^ (hashCode >> 24) ^ (hashCode >> 15) ^ (hashCode >> 9)) % (temp.length-1));
				
				temp[bin].add(f);
			}
		}
		
		m_map = temp;
	}

}
