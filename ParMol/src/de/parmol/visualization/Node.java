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

/**
 * 
 * @author Olga Urzova <siolurzo@stud.informatik.uni-erlangen.de>
 *
 * Diese Klasse enthaelt Informationen ueber einen Knoten aus dem Graph
 * 
 */

public class Node {
	int value;

	float x, y;

	double angle;

	double directionAngle;
	
	int mainDirection;

	
	/**
	 * Constructor
	 * @param index
	 * @param x_new
	 * @param y_new
	 * @param angle
	 * @param directionAngle 
	 * @param mainDirection die Richtung, in welche das Molekuel gezeichnet wird
	 */
	Node(int index, float x_new, float y_new, double angle,
			double directionAngle, int mainDirection) {
		this.value = index;
		this.x = x_new;
		this.y = y_new;
		this.angle = angle;
		this.directionAngle = directionAngle;
		this.mainDirection = mainDirection;
	}

	public String toString() {
		return "[" + value + ", " + x + ", " + y + ", " + angle
				+ "]";
	}
}