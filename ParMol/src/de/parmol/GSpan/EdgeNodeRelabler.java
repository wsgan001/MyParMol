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
package de.parmol.GSpan;

/**
 * This interface is for renaming real node/edge labels to renamed ones
 *
 * @author Marc Woerlein <marc.woerlein@gmx.de>
 */
public interface EdgeNodeRelabler {
	/**
	 * @param nodeLabel
	 * @return the real node label corresponding to the given nodeLabel
	 */
    public int getRealNodeLabel(int nodeLabel);
	/**
	 * @param realNodeLabel
	 * @return the node label corresponding to the given realNodeLabel
	 */
    public int getNodeLabel(int realNodeLabel);
	/**
	 * @param edgeLabel
	 * @return the real edge label corresponding to the given edgeLabel
	 */
    public int getRealEdgeLabel(int edgeLabel);
	/**
	 * @param realEdgeLabel
	 * @return the edge label corresponding to the given realEdgeLabel
	 */
    public int getEdgeLabel(int realEdgeLabel);

}
