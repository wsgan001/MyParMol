/*
 * Created on Dec 13, 2004
 * 
 * Copyright 2004, 2005 Marc W??rlein
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
package de.parmol.GSpan.test;

import junit.framework.TestCase;
import de.parmol.GSpan.*;

/**
 * This ... 
 *
 * @author Marc Woerlein <marc.woerlein@gmx.de>
 */
public class GSpanEdgeTest extends TestCase {
    /** */
	public GSpanEdgeTest(){ super(); }
    /**
     * @param text
     */
	public GSpanEdgeTest(String text){ super(text); }
    
    /**
     *
     */
	public void testCompare(){
        GSpanEdge[] edges=new GSpanEdge[6];
        
        edges[0]=new GSpanEdge(0,1,0,0,1);
        edges[1]=new GSpanEdge(1,2,1,1,0);
        edges[2]=new GSpanEdge(2,0,0,0,0);
        edges[3]=new GSpanEdge(2,3,0,2,2);
        edges[4]=new GSpanEdge(3,1,2,1,1);
        edges[5]=new GSpanEdge(1,4,1,3,2);
        
        for (int i=0; i<6; i++){
            for (int j=0; j<6; j++){
                if (i==j) assertTrue("not equal 1: "+i+" "+j,edges[i].compareTo(edges[j])==0);
                if (i<j) assertTrue("not lesser 1: "+i+" "+j,edges[i].compareTo(edges[j])<0);
                if (i>j) assertTrue("not greater 1: "+i+" "+j,edges[i].compareTo(edges[j])>0);
            }
        }
        
        edges[0]=new GSpanEdge(0,1,1,0,0);
        edges[1]=new GSpanEdge(1,2,0,0,0);
        edges[2]=new GSpanEdge(2,0,0,1,1);
        edges[3]=new GSpanEdge(2,3,0,2,2);
        edges[4]=new GSpanEdge(3,0,0,2,2);
        edges[5]=new GSpanEdge(0,4,1,3,2);
        
        for (int i=0; i<6; i++){
            for (int j=0; j<6; j++){
                if (i==j) assertTrue("not equal 2: "+i+" "+j,edges[i].compareTo(edges[j])==0);
                if (i<j) assertTrue("not lesser 2: "+i+" "+j,edges[i].compareTo(edges[j])<0);
                if (i>j) assertTrue("not greater 2: "+i+" "+j,edges[i].compareTo(edges[j])>0);
            }
        }
        
        edges[0]=new GSpanEdge(0,1,0,0,0);
        edges[1]=new GSpanEdge(1,2,0,0,1);
        edges[2]=new GSpanEdge(2,0,1,1,0);
        edges[3]=new GSpanEdge(2,3,1,1,2);
        edges[4]=new GSpanEdge(3,0,2,2,0);
        edges[5]=new GSpanEdge(2,4,1,3,2);
        
        for (int i=0; i<6; i++){
            for (int j=0; j<6; j++){
                if (i==j) assertTrue("not equal 3: "+i+" "+j,edges[i].compareTo(edges[j])==0);
                if (i<j) assertTrue("not lesser 3: "+i+" "+j,edges[i].compareTo(edges[j])<0);
                if (i>j) assertTrue("not greater 3: "+i+" "+j,edges[i].compareTo(edges[j])>0);
            }
        }
    }
}
