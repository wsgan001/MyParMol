/*
 * Created on Mar 30, 2005
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
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class Settings {

	/**
	 * new Settings
	 */
	public Settings() {
	}

	/**
	 * Die Groesse von dem Fenster setsen, in dem das Molekuel gezeichnet wird.
	 * Die Standardeinstellung ist 500
	 */
	public int FrameSize = 0;

	/**
	 * Die Labels fuer die Kohlenstoffatome setzen
	 */
	public boolean setCarbonLabels = false;

	/** */
	public boolean setShortBonds = true;

	/** */
	public String ColorSchemaFileName = "src/de/parmol/visualization/colorschema.props";

	/**
	 * Diese Variable auf true setzen, um das Molekuelbild in einer Datei zu
	 * speichern
	 */
	public boolean doExportInRasterFormat = true;

	/**
	 * Wenn der Pfad fuer den Export nicht gesetzt ist, wird die Datei in dem Arbeitsverzeichnis
	 * gespeichert
	 */
	public String exportPath = null;

	/**
	 * Den Name der Datei eingeben, sonst heisst es "newimage"
	 */
	public String exportFileName = null;

	/**
	 * Die Endung(en) fuer die Datei(en) eingeben, sonst wird eine .png-Datei
	 * erzeugt
	 */
	public String[] exportFileFormats = null;

	/**
	 * Die Breite des Bildes setzen. Die Standardeinstellung ist 500
	 */
	public int imageWidth = 0;

	/**
	 * Die Hoehe des Bildes setzen. Die Standardeinstellung ist 500
	 */
	public int imageHeight = 0;

}
