/*
 * Created on Dec 17, 2004
 * 
 * Copyright 2004, 2005 Marc Wörlein
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
 * This class is for saving label informations during the relabeling of the DataBase
 * 
 * @author Marc Woerlein <marc.woerlein@gmx.de>
 */
public class Relabler {
    private int def;
    private int[] labels;
    
    /**
     * creates a new relabler
     * @param maxLabel
     * @param def the default return label for undefined labels
     */
    public Relabler(int maxLabel, int def){
        this.def=def;
        this.labels=new int[maxLabel+1];
        for (int i=0;i<maxLabel+1;i++) labels[i]=def;
    }
    
    /**
     * add a new label to this relabler
     * @param orig
     * @param label
     */
    public void addLabel(int orig,int label){
        if (orig<labels.length) labels[orig]=label;
    }
    
    /**
     * @param orig
     * @return the stored label or the default label, if none is stored
     */
    public int getLabel(int orig){
        if (orig<labels.length) return labels[orig];
        return def;
    }

}
