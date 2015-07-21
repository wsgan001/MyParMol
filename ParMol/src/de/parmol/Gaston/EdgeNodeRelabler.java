/*
 * Created on Feb 17, 2005
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

/**
 * This interface describes the functionality for a bijective ...
 *
 * @author Marc Woerlein <marc.woerlein@gmx.de>
 */
public interface EdgeNodeRelabler {

	/**
	 * @param relabeledNodeLabel
	 * @return the corrsponding node label in the original Graph to the given node label 
	 */
    public int getRealNodeLabel(int relabeledNodeLabel);

	/**
	 * @param nodeLabel
	 * @return the relabeled node label to the given original node label 
	 */
    public int getNodeLabel(int nodeLabel);

    /**
	 * @param relabeledEdgeLabel
	 * @return the corrsponding edge label in the original Graph to the given edge label 
	 */
    public int getRealEdgeLabel(int relabeledEdgeLabel);

	/**
	 * @param edgeLabel
	 * @return the relabeled edge label to the given original edge label 
	 */
    public int getEdgeLabel(int edgeLabel);

}
