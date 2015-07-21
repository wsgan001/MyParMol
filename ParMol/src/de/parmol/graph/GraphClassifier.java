/*
 * Created on Dec 7, 2004
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
package de.parmol.graph;

/**
 * This interface returns the class frequencies of given graph id. 
 * 
 * @author Thorsten Meinl <Thorsten.Meinl@informatik.uni-erlangen.de>
 *
 */
public interface GraphClassifier {
	/**
	 * Returns the class frequencies of the graph with the given id
	 * @param graphID the id of the graph
	 * @return the class frequencies of the graph
	 */
	public float[] getClassFrequencies(String graphID);
}
