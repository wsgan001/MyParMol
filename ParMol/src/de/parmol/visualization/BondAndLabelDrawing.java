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

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.BasicStroke;
import java.awt.geom.Line2D;
import java.awt.FontMetrics;

/**
 * @author Olga Urzova <siolurzo@stud.informatik.uni-erlangen.de>
 * 
 * Diese Klasse zeichnet die unterschiedlischen Bindungsarten.
 * 
 */

public class BondAndLabelDrawing {
	float bondsLength;

	float bondsWidth;

	FontMetrics metricsSingleLetter, metricsTwoLetters;

	Font fontForSingleLetter, fontForTwoLetters;

	Graphics2D g;

	int singleLetterFontSize, twoLettersFontSize;

	private Line2D.Float line1, line2;

	private Color bondColor;

	// Ein von der Laenge der Kante unabhaengiges Stroke-Objekt
	private Stroke StrokeWithoutLabels;

	// Hilfsvariablen fuer die Fragmentierung einer zweifarbiger Bindung:
	private float small_distance, big_distance;

	private int heightSL, heightTL;

	/**
	 * a new BondAndLabelDrawing
	 *
	 */
	public BondAndLabelDrawing() {

	}

	/**
	 * Diese Funktion setzt die fuer das Zeichnen benoetigten Parameter neu.
	 * 
	 * @param bl
	 *            die Laenge der Bindungen
	 * @param bw
	 *            die die Breite der Bindungen
	 * @param fontSize
	 *            die Groesse des Schrifts
	 * @param g2
	 *            das aktuelle Graphiics2D-Objekt
	 * @param bc
	 *            die Standardfarbe
	 */
	public void setProperties(float bl, float bw, int fontSize, Graphics2D g2,
			Color bc) {
		bondsLength = bl;
		bondsWidth = bw;
		singleLetterFontSize = fontSize;
		twoLettersFontSize = fontSize - 2;

		fontForSingleLetter = new Font("SansSerif", Font.PLAIN,
				singleLetterFontSize);
		fontForTwoLetters = new Font("SansSerif", Font.PLAIN, twoLettersFontSize);

		metricsSingleLetter = g2.getFontMetrics(fontForSingleLetter);
		metricsTwoLetters = g2.getFontMetrics(fontForTwoLetters);
		g = g2;
		g.setFont(fontForSingleLetter);
		bondColor = bc;
		// Hilfsgroessen fuer die Labels setzen
		heightSL = metricsSingleLetter.getHeight();
		heightTL = metricsTwoLetters.getHeight();
		// Hilfsgroessen fuer die Stroke-Objekten initialisieren
		small_distance = 2 * bondsWidth;
		big_distance = 4 * bondsWidth;
		StrokeWithoutLabels = getStrokeWithoutLabels();
	}

	// Fragmente einer zweifarbiger Bindung:
	/**
	 * 
	 * @param length
	 * @param dist
	 * @return something
	 */
	public Stroke getStrokeForRightFragmentShort(float length, float dist) {
		return new BasicStroke(bondsWidth, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_BEVEL, 0, new float[] { dist, length }, length
						- dist);
	}

	/**
	 * 
	 * @param length
	 * @param dist
	 * @return something
	 */
	public Stroke getStrokeForLeftFragmentShort(float length, float dist) {
		return new BasicStroke(bondsWidth, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_BEVEL, 0, new float[] { dist, length }, length);
	}

	/**
	 * 
	 * @param length
	 * @param dist
	 * @return something
	 */
	public Stroke getStrokeForLeftFragmentLong(float length, float dist) {
		return new BasicStroke(bondsWidth, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_BEVEL, 0, new float[] { dist, length }, 0);
	}

	/**
	 * 
	 * @param length
	 * @param dist
	 * @return something
	 */
	public Stroke getStrokeForRightFragmentLong(float length, float dist) {
		return new BasicStroke(bondsWidth, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_BEVEL, 0, new float[] { dist, length }, length);
	}

	// Die Verbindung von einem Knoten ohne Label zu einem anderen mit Label,
	// sieht z.B. so aus =C
	/**
	 * 
	 * @param length
	 * @return something
	 */
	public Stroke getStrokeToLabel(float length) {
		return new BasicStroke(bondsWidth, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_BEVEL, 0,
				new float[] { length, length / 2.0f }, length / 4.0f);

	}

	// Die Verbindung zwischen zwei Knoten mit Labels, sieht z.B. so aus C=C
	/**
	 * 
	 * @param length
	 * @return something
	 */
	public Stroke getStrokeFromLabelToLabel(float length) {
		length = length / 2.0f;
		return new BasicStroke(bondsWidth, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_BEVEL, 0, new float[] { length, length },
				length + length / 2.0f);
	}

	// Die Verbindung von einem Knoten mit Label zu einem anderen ohne, sieht
	// z.B. so aus C=
	/**
	 * 
	 * @param length
	 * @return something
	 */
	public Stroke getStrokeFromLabel(float length) {
		return new BasicStroke(bondsWidth, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_BEVEL, 0,
				new float[] { length, length / 4.0f }, length);
	}

	// Die Verbindung zwischen zwei Knoten ohne Label, z.B. = .
	/**
	 * 
	 * @return something
	 */
	public Stroke getStrokeWithoutLabels() {
		return new BasicStroke(bondsWidth, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_BEVEL);
	}

	// Diese Funktion generiert die gestrichelte Linie fuer die aromatische
	// Bindung
	/**
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @return something
	 */
	public Stroke getAromaticBondStroke(float x, float y, float z) {
		float[] stroke;
		float d_unit = bondsLength / 7f;
		int size = (int) Math.floor(y / d_unit);
		if (size % 2 == 0) {
			stroke = new float[4 + size];
		} else
			stroke = new float[3 + size];
		int j = 2;
		stroke[0] = 0f;
		stroke[1] = x;
		for (int i = 0; i < size; i++) {
			stroke[j++] = d_unit;
		}
		stroke[stroke.length - 1] = 2 * z;
		return new BasicStroke(bondsWidth / 2, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_ROUND, 0, stroke, 0);
	}

	/**
	 * Diese Funktion zentriert die Knotenbeschriftung.
	 * 
	 * @param s
	 *            das Atomsymbol
	 * @param labelColor
	 *            die Farbe des Atomsymbols
	 * @param x
	 * @param y
	 */
	public void centerText(String s, Color labelColor, float x, float y) {
		float height, width;
		int x_new, y_new;
		switch (s.length()) {
		case 0:
			break;
		case 1:
			height = heightSL;
			width = metricsSingleLetter.stringWidth(s);
			x_new = (int) (x - (width / 2f)) + 1;
			y_new = (int) (y + (height / 4f))
					+ (heightSL - singleLetterFontSize) / 2;
			g.setColor(labelColor);
			g.drawString(s, x_new, y_new);
			g.setColor(bondColor);
			break;
		default:
			height = heightTL;
			width = metricsTwoLetters.stringWidth(s);
			x_new = (int) (x - (width / 2f)) + 1;
			y_new = (int) (y + (height / 4f))
					+ ((int) height - twoLettersFontSize) / 2;
			g.setFont(fontForTwoLetters);
			g.setColor(labelColor);
			g.drawString(s, x_new, y_new);
			// Einstellungen zuruecksetzen
			g.setFont(fontForSingleLetter);
			g.setColor(bondColor);
			break;

		}
	}

	/**
	 * Diese Funktion zeichnet eine aromatische (gestrichelte) Bindung innerhalb
	 * des Ringes. Die Laenge der Kante muss neu berechnet werden, weil der Ring
	 * nichtregulaer sein kann.
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @param alpha1
	 *            der Ringinnenwinkel an der linken Seite der Kante
	 * @param alpha2
	 *            der Ringinnenwinkel an der rechten Seite der Kante
	 */
	public void aromaticBondDashed(float x1, float y1, float x2, float y2,
			double alpha1, double alpha2) {
		float y, z;
		double newLength = Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1)
				* (y2 - y1));
		double theta = DataAnalyser.PIdividedByTwo - alpha1;
		float x = (float) (big_distance * Math.sin(theta) / Math.sin(alpha1));
		y = (float) newLength - x;
		if (alpha1 == alpha2) {
			y -= x;
			z = x;
		} else {
			theta = DataAnalyser.PIdividedByTwo - alpha2;
			z = (float) (big_distance * Math.sin(theta) / Math.sin(alpha2));
			y -= z;
		}
		Line2D line = getAromaticBond(x1, y1, x2, y2, big_distance, newLength);
		if ((y < 0) || (x < 0) || (z < 0)) {
			g.setStroke(getAromaticBondStroke(1, 1, 1));
		} else
			g.setStroke(getAromaticBondStroke(x, y, z));
		g.draw(line);

	}

	/**
	 * Diese Funktion zeichnet eine Doppelbindung in einem regulaeren oder
	 * nichtregulaeren Ring.
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @param kindOfEnd
	 *            die Art die Bindung zu zeichnen
	 * @param label
	 *            die Beschriftung des rechten Knotens
	 * @param fromColor
	 *            die Farbe des linken Atomsymbols
	 * @param toColor
	 *            die Farbe des rechten Atomsymbols
	 */
	public void aromaticBondDouble(float x1, float y1, float x2, float y2,
			int kindOfEnd, String label, Color fromColor, Color toColor) {
		double newLength = Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1)
				* (y2 - y1));
		float length = (float) newLength;
		// Die erste Linie der Bindung zeichnen
		singleBondColored(x1, y1, x2, y2, kindOfEnd, label, fromColor, toColor,
				length);
		// Die innerhalb des Ringes liegende Linie zeichnen
		Line2D line = getAromaticBond(x1, y1, x2, y2, big_distance, newLength);
		if ((kindOfEnd == 1) || (kindOfEnd == 2)) {
			g.setColor(fromColor);
			g.setStroke(getStrokeForLeftFragmentShort(length, length / 4.0f));
			g.draw(line);

			g.setColor(toColor);
			g.setStroke(getStrokeForRightFragmentShort(length, length / 4.0f));
			g.draw(line);
		} else {
			g.setColor(fromColor);
			g.setStroke(getStrokeForLeftFragmentShort(length, length / 4.0f));
			g.draw(line);

			g.setColor(toColor);
			g.setStroke(getStrokeForRightFragmentShort(length, length / 4.0f));
			g.draw(line);
		}

	}

	/**
	 * Diese Funktion zeichnet eine einfache einfarbige Bindung zwischen zwei
	 * Knoten, mit oder ohne Knotenbeschriftungen.
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @param kindOfEnd
	 *            die Art die Bindung zu zeichnen in der Abhaengigkeit davon, ob
	 *            beide Atomsymbole gesetzt sind.
	 * @param label
	 *            die Beschriftung des rechten Knotens
	 * @param length
	 *            die Laenge der Bindung
	 */
	public void singleBond(float x1, float y1, float x2, float y2,
			int kindOfEnd, String label, float length) {
		Line2D line = new Line2D.Float(x1, y1, x2, y2);
		switch (kindOfEnd) {
		case 0:
			g.setStroke(StrokeWithoutLabels);
			break;
		case 1:
			g.setStroke(getStrokeToLabel(length));
			centerText(label, bondColor, x2, y2);
			break;
		case 2:
			g.setStroke(getStrokeFromLabel(length));
			break;
		case 3:
			g.setStroke(getStrokeFromLabelToLabel(length));
			centerText(label, bondColor, x2, y2);
			break;
		default:
			System.err.println("singleBond:: failed kindOfEnd " + kindOfEnd);
			break;
		}
		g.draw(line);
	}

	/**
	 * Diese Funktion zeichnet eine einfache mehrfarbige Bindung zwischen zwei
	 * Knoten, mit oder ohne Knotenbeschriftungen.
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @param kindOfEnd
	 * @param label
	 *            die Beschriftung des rechten Knotens
	 * @param fromColor
	 *            die Farbe des linken Atomsymbols
	 * @param toColor
	 *            die Farbe des rechten Atomsymbols
	 * @param l
	 *            die Laenge der Bindung
	 */
	public void singleBondColored(float x1, float y1, float x2, float y2,
			int kindOfEnd, String label, Color fromColor, Color toColor,
			double l) {
		float length = (float) l;
		Line2D line = new Line2D.Float(x1, y1, x2, y2);
		g.setColor(fromColor);
		switch (kindOfEnd) {
		case 0:
			g.setStroke(StrokeWithoutLabels);
			g.draw(line);
			break;
		case 1:
			g.setStroke(getStrokeForLeftFragmentLong(length, length / 2.0f));
			g.draw(line);

			g.setColor(toColor);
			g.setStroke(getStrokeForRightFragmentShort(length, length / 4.0f));
			g.draw(line);
			centerText(label, toColor, x2, y2);
			break;
		case 2:
			g.setStroke(getStrokeForLeftFragmentShort(length, length / 4.0f));
			g.draw(line);

			g.setColor(toColor);
			g.setStroke(getStrokeForRightFragmentLong(length, length / 2.0f));
			g.draw(line);
			break;
		case 3:
			g.setStroke(getStrokeForLeftFragmentShort(length, length / 4.0f));
			g.draw(line);

			g.setColor(toColor);
			g.setStroke(getStrokeForRightFragmentShort(length, length / 4.0f));
			g.draw(line);
			centerText(label, toColor, x2, y2);
			break;
		default:
			System.err.println("singleBond:: failed kindOfEnd");

		}
	}

	/**
	 * Diese Funktion zeichnet eine doppelte einfarbige Bindung zwischen zwei
	 * Knoten, mit oder ohne Knotenbeschriftungen.
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @param kindOfEnd
	 * @param label
	 *            die Beschriftung des rechten Atomsymbols
	 * @param length
	 *            die Laenge der Bindung
	 */
	public void doubleBond(float x1, float y1, float x2, float y2,
			int kindOfEnd, String label, float length) {
		Line2D middle, line = null;
		line1 = null;
		line2 = null;
		g.setStroke(getStrokeFromLabelToLabel(length));
		middle = new Line2D.Float(x1, y1, x2, y2);
		if (kindOfEnd < 3) {
			line = this.getAromaticBond(x1, y1, x2, y2, big_distance, length);
		} else
			displaceLine(x1, y1, x2, y2, small_distance, length);

		switch (kindOfEnd) {
		case 0:
			g.draw(line);
			g.setStroke(StrokeWithoutLabels);
			g.draw(middle);
			break;
		case 1:
			g.draw(line);
			g.setStroke(getStrokeToLabel(length));
			g.draw(middle);
			centerText(label, bondColor, x2, y2);
			break;
		case 2:
			g.draw(line);
			g.setStroke(getStrokeFromLabel(length));
			g.draw(middle);
			break;
		case 3:
			g.draw(line1);
			g.draw(line2);
			centerText(label, bondColor, x2, y2);
			break;
		// Zwei besonderere Faelle, wenn die Doppelbindung central verlaeuft
		// Ein Label ist "C" und CarbonLabels sind nicht gesetzt
		case 4:
			g.setStroke(getStrokeToLabel(length));
			g.draw(line1);
			g.draw(line2);
			centerText(label, bondColor, x2, y2);
			break;
		case 5:
			g.setStroke(getStrokeFromLabel(length));
			g.draw(line1);
			g.draw(line2);
			break;

		default:
			System.err.println("doubleBond:: failed kindOfEnd");
		}
	}

	/**
	 * Diese Funktion zeichnet eine doppelte mehrfarbige Bindung zwischen zwei
	 * Knoten, mit oder ohne Knotenbeschriftungen.
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @param kindOfEnd
	 * @param label
	 *            die Beschriftung des rechten Atoms
	 * @param fromColor
	 *            die Farbe des linken Atomsymbols
	 * @param toColor
	 *            die Farbe des rechten Atomsymbols
	 * @param length
	 *            die Laenge der Biindung
	 */
	public void doubleBondColored(float x1, float y1, float x2, float y2,
			int kindOfEnd, String label, Color fromColor, Color toColor,
			float length) {
		Line2D middle, line = null;
		line1 = null;
		line2 = null;
		g.setStroke(getStrokeFromLabelToLabel(length));
		middle = new Line2D.Float(x1, y1, x2, y2);
		if (kindOfEnd < 3) {
			line = this.getAromaticBond(x1, y1, x2, y2, big_distance, length);
		} else
			displaceLine(x1, y1, x2, y2, small_distance, length);
		g.setColor(fromColor);
		switch (kindOfEnd) {
		case 0:
			g.setStroke(getStrokeForLeftFragmentShort(length, length / 4.0f));
			g.draw(line);
			g.setStroke(getStrokeForLeftFragmentLong(length, length / 2.0f));
			g.draw(middle);

			g.setColor(toColor);
			g.setStroke(getStrokeForRightFragmentShort(length, length / 4.0f));
			g.draw(line);
			g.setStroke(getStrokeForRightFragmentLong(length, length / 2.0f));
			g.draw(middle);
			break;
		case 1:
			g.setStroke(getStrokeForLeftFragmentShort(length, length / 4.0f));
			g.draw(line);
			g.setStroke(getStrokeForLeftFragmentLong(length, length / 2.0f));
			g.draw(middle);

			g.setColor(toColor);
			g.setStroke(getStrokeForRightFragmentShort(length, length / 4.0f));
			g.draw(line);
			g.draw(middle);
			centerText(label, toColor, x2, y2);
			break;
		case 2:
			g.setStroke(getStrokeForLeftFragmentShort(length, length / 4.0f));
			g.draw(line);
			g.draw(middle);

			g.setColor(toColor);
			g.setStroke(getStrokeForRightFragmentShort(length, length / 4.0f));
			g.draw(line);
			g.setStroke(getStrokeForRightFragmentLong(length, length / 2.0f));
			g.draw(middle);
			break;
		case 3:
			g.setStroke(getStrokeForLeftFragmentShort(length, length / 4.0f));
			g.draw(line1);
			g.draw(line2);

			g.setColor(toColor);
			g.setStroke(getStrokeForRightFragmentShort(length, length / 4.0f));
			g.draw(line1);
			g.draw(line2);
			centerText(label, toColor, x2, y2);
			break;
		// Zwei besonderere Faelle, wenn die Doppelbindung central verlaeuft
		// Ein Label ist "C" und CarbonLabels sind nicht gesetzt
		case 4:

			g.setStroke(getStrokeForLeftFragmentLong(length, length / 2.0f));
			g.draw(line1);
			g.draw(line2);

			g.setColor(toColor);
			g.setStroke(getStrokeForRightFragmentShort(length, length / 4.0f));
			g.draw(line1);
			g.draw(line2);
			centerText(label, toColor, x2, y2);
			break;
		case 5:
			g.setStroke(getStrokeForLeftFragmentShort(length, length / 4.0f));
			g.draw(line1);
			g.draw(line2);

			g.setColor(toColor);
			g.setStroke(getStrokeForRightFragmentLong(length, length / 2.0f));
			g.draw(line1);
			g.draw(line2);
			break;
		default:
			System.err.println("doubleBond:: failed kindOfEnd");
		}
	}

	/**
	 * Diese Funktion zeichnet eine dreifache einfarbige Bindung zwischen zwei
	 * Knoten, mit oder ohne Knotenbeschriftungen.
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @param kindOfEnd
	 * @param label
	 *            die Beschriftung des rechten Atoms
	 * @param length
	 *            die Laenge der Bindung
	 */
	public void tripleBond(float x1, float y1, float x2, float y2,
			int kindOfEnd, String label, float length) {
		Line2D middle = new Line2D.Float(x1, y1, x2, y2);
		line1 = null;
		line2 = null;
		g.setStroke(getStrokeFromLabelToLabel(length));
		displaceLine(x1, y1, x2, y2, big_distance, length);
		g.draw(line1);
		g.draw(line2);
		switch (kindOfEnd) {
		case 0:
			g.setStroke(StrokeWithoutLabels);
			g.draw(middle);
			break;
		case 1:
			g.setStroke(getStrokeToLabel(length));
			g.draw(middle);
			centerText(label, bondColor, x2, y2);
			break;
		case 2:
			g.setStroke(getStrokeFromLabel(length));
			g.draw(middle);
			break;
		case 3:
			g.draw(middle);
			centerText(label, bondColor, x2, y2);
			break;
		default:
			System.err.println("tripleBond:: failed kindOfEnd");
		}

	}

	/**
	 * Diese Funktion zeichnet eine dreifache mehrfarbige Bindung zwischen zwei
	 * Knoten, mit oder ohne Knotenbeschriftungen.
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @param kindOfEnd
	 *            die Art der Bindung in der Abhaengigkeit davon, ob die beiden
	 *            Atomsymbole gesetzt sind oder nicht
	 * @param label
	 *            die rechte Knotenbeschriftung
	 * @param leftLabelColor
	 *            die Farbe des linken Atomsymbols
	 * @param rightLabelColor
	 *            die Farbe des rechten Atomsymbols
	 * @param length
	 *            die Laenge der Bindung
	 */
	public void tripleBondColored(float x1, float y1, float x2, float y2,
			int kindOfEnd, String label, Color leftLabelColor,
			Color rightLabelColor, float length) {
		Line2D middle = new Line2D.Float(x1, y1, x2, y2);
		line1 = null;
		line2 = null;
		displaceLine(x1, y1, x2, y2, big_distance, length);

		// Das linke Fragment der Bindung zeichnen
		g.setColor(leftLabelColor);
		g.setStroke(getStrokeForLeftFragmentShort(length, length / 4.0f));
		g.draw(line1);
		g.draw(line2);

		// Das rechte Fragment zeichnen
		g.setColor(rightLabelColor);
		g.setStroke(getStrokeForRightFragmentShort(length, length / 4.0f));
		g.draw(line1);
		g.draw(line2);
		// Keine von Labels ist gesetzt
		if (kindOfEnd == 0) {
			g.setColor(leftLabelColor);
			g.setStroke(getStrokeForLeftFragmentLong(length, length / 2.0f));
			g.draw(middle);
			g.setStroke(getStrokeForRightFragmentLong(length, length / 2.0f));
			g.draw(middle);
			return;
		}
		// Mittlere Teil der Bindung zeichnen
		switch (kindOfEnd) {
		case 1:
			// Das rechte Label ist gesetzt, das linke nicht
			g.setColor(leftLabelColor);
			g.setStroke(getStrokeForLeftFragmentLong(length, length / 2.0f));
			g.draw(middle);

			g.setColor(rightLabelColor);
			g.setStroke(getStrokeForRightFragmentShort(length, length / 4.0f));
			g.draw(middle);
			centerText(label, rightLabelColor, x2, y2);
			break;
		case 2:
			// Das linke Label ist gesetzt, der rechte nicht
			g.setColor(leftLabelColor);
			g.setStroke(getStrokeForLeftFragmentShort(length, length / 4.0f));
			g.draw(middle);

			g.setColor(rightLabelColor);
			g.setStroke(getStrokeForRightFragmentLong(length, length / 2.0f));
			g.draw(middle);
			break;
		case 3:
			// Beide Labels sind gesetzt
			g.setColor(leftLabelColor);
			g.setStroke(getStrokeForLeftFragmentShort(length, length / 4.0f));
			g.draw(middle);

			g.setColor(rightLabelColor);
			g.setStroke(getStrokeForRightFragmentShort(length, length / 4.0f));
			g.draw(middle);
			centerText(label, rightLabelColor, x2, y2);
			break;
		default:
			System.err.println("tripleBond:: failed kindOfEnd");
		}

	}

	/**
	 * Diese Hilfsfunktion verschiebt eine Linie.
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @param distance
	 *            die Distanz zwischen zwei Linien aus einer Bindung
	 * @param length
	 *            die Laenge der Bindung
	 */
	public void displaceLine(float x1, float y1, float x2, float y2,
			float distance, float length) {
		float simple = distance - 0.5f;
		double alfa = Math.acos(Math.abs(x2 - x1) / length);
		float xdistance = (float) (distance * Math.sin(alfa));
		float ydistance = (float) (distance * Math.cos(alfa));
		if ((int) x1 == (int) x2) {
			line1 = new Line2D.Float(x1 - simple, y1, x2 - simple, y2);
			line2 = new Line2D.Float(x1 + simple, y1, x2 + simple, y2);
		} else {
			if ((int) y1 == (int) y2) {
				line1 = new Line2D.Float(x1, y1 - simple, x2, y2 - simple);
				line2 = new Line2D.Float(x1, y1 + simple, x2, y2 + simple);
			} else {
				// Die Kette wird von links nach rechts gezeichnet
				if (x2 > x1) {
					if (y2 > y1) {
						line1 = new Line2D.Float(x1 + xdistance,
								y1 - ydistance, x2 + xdistance, y2 - ydistance);
						line2 = new Line2D.Float(x1 - xdistance,
								y1 + ydistance, x2 - xdistance, y2 + ydistance);
					} else {
						line1 = new Line2D.Float(x1 - xdistance,
								y1 - ydistance, x2 - xdistance, y2 - ydistance);
						line2 = new Line2D.Float(x1 + xdistance,
								y1 + ydistance, x2 + xdistance, y2 + ydistance);
					}
				} else {
					// von rechts nach links
					if (y2 < y1) {
						line1 = new Line2D.Float(x1 + xdistance,
								y1 - ydistance, x2 + xdistance, y2 - ydistance);
						line2 = new Line2D.Float(x1 - xdistance,
								y1 + ydistance, x2 - xdistance, y2 + ydistance);
					} else {
						line1 = new Line2D.Float(x1 - xdistance,
								y1 - ydistance, x2 - xdistance, y2 - ydistance);
						line2 = new Line2D.Float(x1 + xdistance,
								y1 + ydistance, x2 + xdistance, y2 + ydistance);
					}
				}
			}
		}
	}

	/**
	 * Diese Hilfsfunktion berechnet Koordinaten fuer die aromatische Bindung in
	 * einem Ring.
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @param distance
	 *            die Distanz zwischen zwei Linien aus der Bindung
	 * @param newLength
	 *            die Laenge der Bindung
	 * @return something
	 */
	public Line2D getAromaticBond(float x1, float y1, float x2, float y2,
			float distance, double newLength) {
		float simple = distance - 0.5f;
		double alfa = Math.acos(Math.abs(x2 - x1) / newLength);
		float xdistance = (float) (distance * Math.sin(alfa));
		float ydistance = (float) (distance * Math.cos(alfa));
		if ((int) x1 == (int) x2) {
			if (y2 > y1) {
				line2 = new Line2D.Float(x1 - simple, y1, x2 - simple, y2);
			} else
				line2 = new Line2D.Float(x1 + simple, y1, x2 + simple, y2);
		} else {
			if ((int) y1 == (int) y2) {
				if (x2 > x1) {
					line2 = new Line2D.Float(x1, y1 + simple, x2, y2 + simple);
				} else
					line2 = new Line2D.Float(x1, y1 - simple, x2, y2 - simple);

			} else {
				// Die Kette wird von links nach rechts gezeichnet
				if (x2 > x1) {
					if (y2 > y1) {
						line2 = new Line2D.Float(x1 - xdistance,
								y1 + ydistance, x2 - xdistance, y2 + ydistance);
					} else {
						line2 = new Line2D.Float(x1 + xdistance,
								y1 + ydistance, x2 + xdistance, y2 + ydistance);
					}
				} else {
					// von rechts nach links
					if (y2 < y1) {

						line2 = new Line2D.Float(x1 + xdistance,
								y1 - ydistance, x2 + xdistance, y2 - ydistance);
					} else {
						line2 = new Line2D.Float(x1 - xdistance,
								y1 - ydistance, x2 - xdistance, y2 - ydistance);
					}
				}
			}
		}
		return line2;
	}
}