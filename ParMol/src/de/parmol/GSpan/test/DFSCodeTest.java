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
import java.util.*;

/**
 * This ... 
 *
 * @author Marc Woerlein <marc.woerlein@gmx.de>
 */
public class DFSCodeTest extends TestCase {
    /** */
	public DFSCodeTest(){ super(); }
    /**
     * @param text
     */
	public DFSCodeTest(String text){ super(text); }
    
    /**
     *
     */
	public void testCompare(){
        TreeSet edges=new TreeSet();
        
        System.out.println(edges);
        
/*        DFSCode a=new DFSCode();
    public Extension (int fromNode, int fromLabel, int edgeLabel, int toNode, int nodeLabel,int edge){
        DFSCode b=new DFSCode();
        DFSCode c=new DFSCode();
        
        a.add(new GSpanEdge(0,1,0,0,1));
        a.add(new GSpanEdge(1,2,1,1,0));
        a.add(new GSpanEdge(2,0,0,0,0));
        a.add(new GSpanEdge(2,3,0,2,2));
        a.add(new GSpanEdge(3,1,2,1,1));
        a.add(new GSpanEdge(1,4,1,3,2));
        
        b.add(new GSpanEdge(0,1,1,0,0));
        b.add(new GSpanEdge(1,2,0,0,0));
        b.add(new GSpanEdge(2,0,0,1,1));
        b.add(new GSpanEdge(2,3,0,2,2));
        b.add(new GSpanEdge(3,0,2,1,1));
        b.add(new GSpanEdge(0,4,1,3,2));
        
        c.add(new GSpanEdge(0,1,0,0,0));
        c.add(new GSpanEdge(1,2,0,0,1));
        c.add(new GSpanEdge(2,0,1,1,0));
        c.add(new GSpanEdge(2,3,1,1,2));
        c.add(new GSpanEdge(3,0,2,2,0));
        c.add(new GSpanEdge(2,4,1,3,2));
        
        assertTrue("a<b",a.compareTo(b)<0);
        assertTrue("b>a",b.compareTo(a)>0);
        assertTrue("a>c",a.compareTo(c)>0);
        assertTrue("c<a",c.compareTo(a)<0);
        assertTrue("b>c",b.compareTo(c)>0);
        assertTrue("c<b",c.compareTo(b)<0);
        */
    }
}
