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

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import de.parmol.parsers.SLNParser;

/**
 *
 * @author Olga Urzova <siolurzo@stud.informatik.uni-erlangen.de>
 * 
 * 		Diese Klasse speichert die von dem Benutzer in der Datei
 *         colorschema.props definierten Farbeneinstellungen.
 */

public class ColorSchema {
	// Die Standardeinstellung fuer die Hintergrundfarbe
	private Color backgroundColor = Color.white;

	// Die Standardeinstellung fuer die Farbe der Bindungen
	private Color bondColor = Color.black;

	// Die Standardeinstellung fuer die Verwendung der Farben
	private boolean doColoredLabels = false;

	private Properties props = null;

	private Color[] atomColors;
	String defaultcolor="doColoredLabels:true\nbondColor:0 0 0\nbackgroundColor:255 255 255\nH:0 0 255\nHe:0 0 0\nLi:0 0 0\nBe:0 0 0\nB:0 0 0\nC:0 0 0\nN:0 0 255\nO:255 0 0\nF:255 0 255\nNe:0 0 0\nNa:0 0 0\nMg:0 0 0\nAl:0 0 0\nSi:0 0 0\nP:0 0 0\nS:240 240 0\nCl:0 255 0\nAr:0 0 0\nK:0 0 0\nCa:0 0 0\nSc:0 0 0\nTi:0 0 0\nV:0 0 0\nCr:0 0 0\nMn:0 0 0\nFe:0 0 0\nCo:0 0 0\nNi:0 0 0\nCu:0 0 0\nZn:0 0 0\nGa:0 0 0\nGe:0 0 0\nAs:0 0 0\nSe:0 0 0\nBr:255 0 255\nKr:0 0 0\nRb:0 0 0\nSr:0 0 0\nY:0 0 0\nZr:0 0 0\nNb:0 0 0\nMo:0 0 0\nTc:0 0 0\nRu:0 0 0\nRh:0 0 0\nPd:0 0 0\nAg:0 0 0\nCd:0 0 0\nIn:0 0 0\nSn:0 0 0\nSb:0 0 0\nTe:0 0 0\nI:0 0 0\nXe:0 0 0\nCs:0 0 0\nBa:0 0 0\nLa:0 0 0\nCe:0 0 0\nPr:0 0 0\nNd:0 0 0\nPm:0 0 0\nSm:0 0 0\nEu:0 0 0\nGd:0 0 0\nTb:0 0 0\nDy:0 0 0\nHo:0 0 0\nEr:0 0 0\nTm:0 0 0\nYb:0 0 0\nLu:0 0 0\nHf:0 0 0\nTa:0 0 0\nW:0 0 0\nRe:0 0 0\nOs:0 0 0\nIr:0 0 0\nPt:0 0 0\nAu:0 0 0\nHg:0 0 0\nTl:0 0 0\nPb:0 0 0\nBi:0 0 0\nPo:0 0 0\nAt:0 0 0\nRn:0 0 0\nFr:0 0 0\nRa:0 0 0\nAc:0 0 0\nTh:0 0 0\nPa:0 0 0\nU:0 0 0\nNp:0 0 0\nPu:0 0 0\nAm:0 0 0\nCm:0 0 0\nBk:0 0 0\nCf:0 0 0\nEs:0 0 0\nFm:0 0 0\nMd:0 0 0\nNo:0 0 0\nLr:0 0 0\nRf:0 0 0\nDb:0 0 0\nSg:0 0 0\nBh:0 0 0\nHs:0 0 0\nMt:0 0 0\nUun:0 0 0\nUuu:0 0 0\nUub:0 0 0\nUut:0 0 0\nUuq:0 0 0";
	
	private void read(InputStream io) throws IOException{
		props = new Properties();
		String[] colorCode;
		props.load(io);
		doColoredLabels = props.getProperty("doColoredLabels").equals(
		"true");
		colorCode = props.getProperty("backgroundColor").split(" ");
		backgroundColor = new Color(Integer.parseInt(colorCode[0]), Integer
				.parseInt(colorCode[1]), Integer.parseInt(colorCode[2]));
		colorCode = props.getProperty("bondColor").split(" ");
		bondColor = new Color(Integer.parseInt(colorCode[0]), Integer
				.parseInt(colorCode[1]), Integer.parseInt(colorCode[2]));
		if (doColoredLabels) {
			atomColors = new Color[SLNParser.ATOM_SYMBOLS.length];
			for (int i = 1; i < SLNParser.ATOM_SYMBOLS.length; i++) {
				try {
					colorCode = props
							.getProperty(SLNParser.ATOM_SYMBOLS[i]).split(
									" ");
					atomColors[i] = new Color(Integer
							.parseInt(colorCode[0]), Integer
							.parseInt(colorCode[1]), Integer
							.parseInt(colorCode[2]));
				} catch (NullPointerException e) {
					atomColors[i] = bondColor;
				}
			}
		}
	}

	/**
	 * Constructor speichert die Farbeneinstellungen aus der .props-Datei (die
	 * Standarddatei ist colorschema.props)
	 * 
	 * @param fileName
	 */
	public ColorSchema(String fileName) {
		try {
			read(new FileInputStream(fileName));
		} catch (IOException e) {
			try{
			read(new ByteArrayInputStream(defaultcolor.getBytes()));
			}catch (IOException e2) {
				this.doColoredLabels=false;
			}
		}
	}

	/**
	 * Diese Funktion gibt die Farbe des Atomsymbols zurueck.
	 * 
	 * @param atomLabelIndex
	 * @return the color of the given atom
	 */
	public Color getLabelColor(int atomLabelIndex) {
		if (doColoredLabels) {
			return atomColors[atomLabelIndex];
		} else {
			return bondColor;
		}
	}

	/**
	 * Diese Funktion gibt die Farbe der Bindungen zurueck
	 * 
	 * @return the color of the bond 
	 */
	public Color getBondColor() {
		return bondColor;
	}

	/**
	 * Die Funktion gibt die Hintergrundfarbe zurueck
	 * 
	 * @return the background color
	 */
	public Color getBackgroundColor() {
		return backgroundColor;
	}

	/**
	 * gibt true zurueck, wenn das Molekuel mehrrfarbig dargestellt werden soll
	 * 
	 * @return <code>true</code>, if the molecules shall be colored
	 */
	public boolean isMoleculeColored() {
		return doColoredLabels;
	}
	

}
