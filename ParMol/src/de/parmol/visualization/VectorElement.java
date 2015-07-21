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

import de.parmol.parsers.SLNParser;

/**
 * 
 * @author Olga Urzova <siolurzo@stud.informatik.uni-erlangen.de>
 * 
 * Diese Klasse verwaltet die fuer das Zeichnen verwendeten Daten.
 * 
 */
public class VectorElement {
	/**
	 * Die Art der Bindung: 1 ist die einfache Bindung. 2 ist die Doppelbindung.
	 * 3 ist die Dreifachbindung. 4 ist die aromatische Bindung in einem Ring
	 * die aus zwei Linien besteht, wobei die Linie innerhalb des Ringes
	 * gestrichelt ist. 5 steht fuer eine gestrichelte Linie. Diese Art wird nur
	 * bei dem Zeichnen der Bloecken verwendet.
	 * 
	 */
	int kindOfBond;

	/**
	 * Der Index des Atoms rechts von der Kante
	 */
	int rightAtomIndex;

	/**
	 * Der Index des Atoms links von der Kante
	 */
	int leftAtomIndex;

	/**
	 * Der Grad der beiden Knoten. Dieser Wert wird bei dem Zeichnen verkuerzter
	 * Bindungen am Ende einer Kette gebraucht.
	 */
	int ldegree, rdegree;

	/**
	 * Die berechneten Koordinaten
	 */
	float x1, y1, x2, y2;

	/**
	 * Die Beschriftung des Atoms rechts von der Kante
	 */
	String neighborLabel;

	/**
	 * Der Winkel in dem Ring and beiden Seiten der Kante. Diese Werte werden
	 * bei dem Zeichnen der nichtregulaeren und regulaeren Ringen gebraucht.
	 */
	double angleSize = 0;

	double angleSizeR = 0;

	VectorElement(float x_begin, float y_begin, float x_end, float y_end,
			int b, int lindex, int rindex, int ldegree, int rdegree,
			double lsize, double rsize) {
		x1 = x_begin;
		y1 = y_begin;
		x2 = x_end;
		y2 = y_end;
		this.ldegree = ldegree;
		this.rdegree = rdegree;
		neighborLabel = SLNParser.ATOM_SYMBOLS[rindex];
		kindOfBond = b;
		leftAtomIndex = lindex;
		rightAtomIndex = rindex;
		angleSize = lsize;
		angleSizeR = rsize;
	}

	/**
	 * Diese Funktion bestimmt wie die Bindung gezeichnet wird (in der
	 * Abhaengigkeit davon, ob die Kohlenstoffatomsymbole ausgeschrieben sind
	 * oder nicht).
	 * 
	 * @param isCarbonLabelSet
	 * @return something
	 */
	public int getKindOfEnd(boolean isCarbonLabelSet) {
		if (isCarbonLabelSet)
			return 3;
		if ((leftAtomIndex != 6) && (rightAtomIndex != 6))
			return 3;
		if ((leftAtomIndex == 6) && (rightAtomIndex == 6)) {
			return 0;
		} else {
			if (leftAtomIndex != 6) {
				if ((kindOfBond == 2) && (rdegree > 2) && (angleSize == 0))
					return 5;

				return 2;
			}
			if ((kindOfBond == 2) && (ldegree > 2) && (angleSize == 0))
				return 4;
			return 1;
		}
	}

	public String toString() {
		return "PunktA (" + x1 + ", " + y1 + ") mit index " + leftAtomIndex
				+ ", PunktB (" + x2 + ", " + y2 + ") mit index "
				+ rightAtomIndex + " \n";
	}
}