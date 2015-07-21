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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import de.parmol.visualization.BondAndLabelDrawing;

/**
 * 
 * @author Olga Urzova <siolurzo@stud.informatik.uni-erlangen.de>
 * 
 * Diese Klasse nimmt die berechneten Koordinaten entgegen, skaliert sie neu,
 * wenn die Groesse des Fensters geaendert wird und zeichnet das Molekuel.
 * 
 */
public class Demo extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2154638693335761967L;

	// static FontMetrics metrics;

	// static Font fontForSingleLetter, fontForTwoLetters;

	static int font_size;

	static float bondsLength = 1;

	static float bondsWidth;

	protected float xdifferenz, ydifferenz;

	private float minx, miny;

	private float optimize_xfactor, optimize_yfactor;

	private BondAndLabelDrawing bondAndLabelDrawer = null;

	private ColorSchema colorSchema = null;

	private Vector mainData;

	private Color bondColor;

	private boolean isMoleculeColored;

	private boolean setCarbonLabels = true;

	private boolean setShortBonds = true;

	private Color backgroundColor;

	private float lfactor = 1.0f / 3.0f;

	private float rfactor = 2.0f / 3.0f;

//	private Dimension m_dimension = new Dimension();

	private Image mImage = null;

	/**
	 * Constructor
	 * 
	 * @param analyser
	 *            enthaelt die berechneten Koordinaten
	 * @param schema
	 *            enthaelt die Farbeinstellungen aus colorschema.props
	 * @param setCarbonLabels
	 * @param setShortBonds
	 */
	public Demo(DataAnalyser analyser, ColorSchema schema,
			boolean setCarbonLabels, boolean setShortBonds) {
		bondAndLabelDrawer = new BondAndLabelDrawing();
		this.xdifferenz = analyser.getXDifference();
		this.ydifferenz = analyser.getYDifference();
		this.minx = analyser.getMinimumOfXValue();
		this.miny = analyser.getMinimumOfYValue();
		this.mainData = analyser.getDrawingData();
		this.setCarbonLabels = setCarbonLabels;
		this.setShortBonds = setShortBonds;
		colorSchema = schema;
		bondColor = colorSchema.getBondColor();
		isMoleculeColored = colorSchema.isMoleculeColored();
		backgroundColor = colorSchema.getBackgroundColor();
		this.setBackground(backgroundColor);
	}

	/**
	 * Diese Funktion rechnet alle fuer das Zeichnen benoetigten Groessen um in
	 * Abhaengigkeit von der Groesse der Swing-Componente.
	 * 
	 * @param s
	 *            die Groesse der Swing-Komponente
	 */
	public void initFramePropeties(Dimension s) {
		// Die Flaeche zum Zeichnen betraegt 80% von
		// der Gesamtflaeche
		float width_fact = s.width * 0.8f;
		float height_fact = s.height * 0.8f;
		// Die Laenge der Verbindungslinie(Bond) festlegen
		if (s.width > s.height) {
			bondsLength = width_fact / 10;
			if ((xdifferenz * bondsLength > width_fact)
					|| (ydifferenz * bondsLength > height_fact)) {
				if (((xdifferenz * bondsLength) - width_fact) > (ydifferenz
						* bondsLength - height_fact)) {
					bondsLength /= (xdifferenz * bondsLength) / width_fact;
				} else
					bondsLength /= (ydifferenz * bondsLength) / height_fact;
			}
		} else {
			bondsLength = height_fact / 10;
			if (((xdifferenz * bondsLength) > width_fact)
					|| ((ydifferenz * bondsLength) > height_fact)) {
				if (((xdifferenz * bondsLength) - width_fact) > (ydifferenz
						* bondsLength - height_fact)) {
					bondsLength /= (xdifferenz * bondsLength) / width_fact;
				} else
					bondsLength /= (ydifferenz * bondsLength) / height_fact;
			}
		}
		// Der Rand ist entsprechend jeweils 10%
		// von der Laenge in jede Richtung breit + das Bild zentrieren
		float xkorrektur = s.width * 0.1f
				+ (width_fact - xdifferenz * bondsLength) / 2f;
		float ykorrektur = s.height * 0.1f
				+ (height_fact - ydifferenz * bondsLength) / 2f;
		// Um die Koordinatenumrechnung zu optimieren
		// statt z.B. x1_kor = (d.x1 - minx) * bondsLength + xkorrektur;
		// x1_kor = d.x1 * bondsLength - optimize_xfactor;
		optimize_xfactor = minx * bondsLength - xkorrektur;
		optimize_yfactor = miny * bondsLength - ykorrektur;
		// Die Groesse des Schriftes hangt von der Breitw der Linie ab,
		// die Breite der Linie von ihrer eigenen Laenge
		bondsWidth = bondsLength / 35f;
		font_size = (int) (bondsWidth * 12);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	public void paintComponent(Graphics g) {
		Dimension d = getSize();
		// Bei jedem Aufruf von paintComponent() zuerst das alte Bild zeichnen
		if (mImage != null) {
			g.drawImage(mImage, 0, 0, null);
		}
		// Wenn die Groesse des Bildes sich geaendert hat oder
		// paintComponent() zum ersten Mal aufgerufen ist, dann
		// das Molekuel neu zeichnen.
		if (checkOffscreenImage(d)) {
			Graphics offG = mImage.getGraphics();
			paintOffscreen(offG, d);
			this.setBorder(new EtchedBorder(EtchedBorder.RAISED));
			g.drawImage(mImage, 0, 0, null);
		}
	}

	private boolean checkOffscreenImage(Dimension d) {
		if (mImage == null || mImage.getWidth(null) != d.width
				|| mImage.getHeight(null) != d.height) {
			mImage = createImage(d.width, d.height);
			return true;
		}
		return false;
	}

	/**
	 * Diese Funktion zeichnet das Molekuel auf der Swing-Komponente
	 * 
	 * @param g
	 * @param s
	 *            die aktuelle Bildgroesse
	 */
	public void paintOffscreen(Graphics g, Dimension s) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		float height = s.height;
		initFramePropeties(s);
		bondAndLabelDrawer.setProperties(bondsLength, bondsWidth, font_size,
				g2, bondColor);

		if (isMoleculeColored) {
			drawColoredMolecule(height);
		} else
			drawNonColoredMolecule(height);
		g2.setStroke(new BasicStroke());
	}

	/**
	 * Diese Hilfsfunktion berechnet neue Koordinate fuer die verkuerzte Bindung
	 * 
	 * @param x1
	 * @param x2
	 * @param factor
	 *            Der Vekleinungsfaktor
	 * @return something
	 */
	private float getShortBond(float x1, float x2, float factor) {
		return (x2 - x1) * factor + x1;
	}

	/**
	 * Diese Funktion ist fuer das mehrfarbige Zeichnen verantwortlich.
	 * 
	 * @param height
	 */
	private void drawColoredMolecule(float height) {
		float x1_kor, y1_kor, x2_kor, y2_kor;
		Color fromColor, toColor = null;
		int kindOfBond;
		double angleSizeInCycle;
		VectorElement d;
		// Die Kanten mit dem rechten Atomsymbol zeichnen
		for (int i = 1; i < mainData.size(); i++) {
			float newLength = bondsLength;
			d = (VectorElement) mainData.elementAt(i);
			x1_kor = d.x1 * bondsLength - optimize_xfactor;
			y1_kor = d.y1 * bondsLength - optimize_yfactor;
			x2_kor = d.x2 * bondsLength - optimize_xfactor;
			y2_kor = d.y2 * bondsLength - optimize_yfactor;

			y1_kor = height - y1_kor;
			y2_kor = height - y2_kor;
			// Die Laenge der Endkanten verkuerzen,
			// um die Kollisionen der Atomsymbole zu vermeiden
			if ((d.rdegree == 1) && (setShortBonds)) {
				x2_kor = getShortBond(x1_kor, x2_kor, rfactor);
				y2_kor = getShortBond(y1_kor, y2_kor, rfactor);
				newLength = rfactor * bondsLength;
			}
			if ((d.ldegree == 1) && (setShortBonds)) {
				x1_kor = getShortBond(x1_kor, x2_kor, lfactor);
				y1_kor = getShortBond(y1_kor, y2_kor, lfactor);
				newLength = rfactor * bondsLength;
			}

			angleSizeInCycle = d.angleSize;
			kindOfBond = d.kindOfBond;
			fromColor = colorSchema.getLabelColor(d.leftAtomIndex);
			toColor = colorSchema.getLabelColor(d.rightAtomIndex);
			// Das erste Atomsymbolsetzen
			if (i == 1) {
				if ((setCarbonLabels) || (d.leftAtomIndex != 6))
					bondAndLabelDrawer.centerText(((VectorElement) mainData
							.elementAt(0)).neighborLabel, fromColor, x1_kor,
							y1_kor);
			}

			switch (kindOfBond) {
			case 1:
				bondAndLabelDrawer.singleBondColored(x1_kor, y1_kor, x2_kor,
						y2_kor, d.getKindOfEnd(setCarbonLabels),
						d.neighborLabel, fromColor, toColor, newLength);

				break;
			case 2:
				if (angleSizeInCycle > 0) {
					bondAndLabelDrawer.aromaticBondDouble(x1_kor, y1_kor,
							x2_kor, y2_kor, d.getKindOfEnd(setCarbonLabels),
							d.neighborLabel, fromColor, toColor);
				} else {
					bondAndLabelDrawer.doubleBondColored(x1_kor, y1_kor,
							x2_kor, y2_kor, d.getKindOfEnd(setCarbonLabels),
							d.neighborLabel, fromColor, toColor, newLength);
				}
				break;
			case 3:

				bondAndLabelDrawer.tripleBondColored(x1_kor, y1_kor, x2_kor,
						y2_kor, d.getKindOfEnd(setCarbonLabels),
						d.neighborLabel, fromColor, toColor, newLength);
				break;
			case 4:
				bondAndLabelDrawer.singleBondColored(x1_kor, y1_kor, x2_kor,
						y2_kor, d.getKindOfEnd(setCarbonLabels),
						d.neighborLabel, fromColor, toColor, newLength);
				bondAndLabelDrawer.aromaticBondDashed(x1_kor, y1_kor, x2_kor,
						y2_kor, angleSizeInCycle, d.angleSizeR);
				break;
			// Der Ausnahmefall: nur die gestrichelte Linie fuer die aromatische
			// Bindung wird gezeichnet
			case 5:
				bondAndLabelDrawer.aromaticBondDashed(x1_kor, y1_kor, x2_kor,
						y2_kor, angleSizeInCycle, d.angleSizeR);
				break;
			}
		}
	}

	/**
	 * Diese Funktion wird von painComponent() aufgerufen, wenn keine
	 * mehrfarbige Darstellung des Molekuels erfolgen sollte
	 * 
	 * @param height
	 */
	private void drawNonColoredMolecule(float height) {
		float x1_kor, y1_kor, x2_kor, y2_kor;
		int kindOfBond;
		double angleSizeInCycle;
		VectorElement d;

		// Die Kanten mit dem rechten Atomsymbol zeichnen
		for (int i = 1; i < mainData.size(); i++) {
			float newLength = bondsLength;
			d = (VectorElement) mainData.elementAt(i);
			x1_kor = d.x1 * bondsLength - optimize_xfactor;
			y1_kor = d.y1 * bondsLength - optimize_yfactor;
			x2_kor = d.x2 * bondsLength - optimize_xfactor;
			y2_kor = d.y2 * bondsLength - optimize_yfactor;
			y1_kor = height - y1_kor;
			y2_kor = height - y2_kor;

			// Die Laenge der Endkanten verkuerzen,
			// um die Kollisionen der Atomsymbole zu vermeiden
			if ((d.rdegree == 1) && (setShortBonds)) {
				x2_kor = getShortBond(x1_kor, x2_kor, rfactor);
				y2_kor = getShortBond(y1_kor, y2_kor, rfactor);
				newLength = rfactor * bondsLength;
			}
			if ((d.ldegree == 1) && (setShortBonds)) {
				x1_kor = getShortBond(x1_kor, x2_kor, lfactor);
				y1_kor = getShortBond(y1_kor, y2_kor, lfactor);
				newLength = rfactor * bondsLength;
			}

			if (i == 1) {
				// Das erste AtomSymbol setzen
				if ((setCarbonLabels) || (d.leftAtomIndex != 6))
					bondAndLabelDrawer.centerText(((VectorElement) mainData
							.elementAt(0)).neighborLabel, colorSchema
							.getBondColor(), x1_kor, y1_kor);

			}

			angleSizeInCycle = d.angleSize;
			kindOfBond = d.kindOfBond;
			switch (kindOfBond) {
			case 1:

				bondAndLabelDrawer.singleBond(x1_kor, y1_kor, x2_kor, y2_kor, d
						.getKindOfEnd(setCarbonLabels), d.neighborLabel,
						newLength);
				break;
			case 2:
				if (angleSizeInCycle > 0) {
					bondAndLabelDrawer.aromaticBondDouble(x1_kor, y1_kor,
							x2_kor, y2_kor, d.getKindOfEnd(setCarbonLabels),
							d.neighborLabel, colorSchema.getBondColor(),
							colorSchema.getBondColor());
				} else
					bondAndLabelDrawer.doubleBond(x1_kor, y1_kor, x2_kor,
							y2_kor, d.getKindOfEnd(setCarbonLabels),
							d.neighborLabel, newLength);
				break;
			case 3:
				bondAndLabelDrawer.tripleBond(x1_kor, y1_kor, x2_kor, y2_kor, d
						.getKindOfEnd(setCarbonLabels), d.neighborLabel,
						newLength);
				break;
			case 4:
				bondAndLabelDrawer.singleBond(x1_kor, y1_kor, x2_kor, y2_kor, d
						.getKindOfEnd(setCarbonLabels), d.neighborLabel,
						newLength);
				bondAndLabelDrawer.aromaticBondDashed(x1_kor, y1_kor, x2_kor,
						y2_kor, angleSizeInCycle, d.angleSizeR);
				break;
			// Der Ausnahmefall: nur die gestrichelte Linie fuer die aromatische
			// Bindung wird gezeichnet
			case 5:
				bondAndLabelDrawer.aromaticBondDashed(x1_kor, y1_kor, x2_kor,
						y2_kor, angleSizeInCycle, d.angleSizeR);
				break;
			}
		}
	}

}