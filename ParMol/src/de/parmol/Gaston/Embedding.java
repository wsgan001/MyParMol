/*
 * Created on Feb 10, 2005
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
package de.parmol.Gaston;

import de.parmol.graph.*;

/**
 * This class representates the embedding into a renamed DataBase graph 
 *
 * @author Marc Woerlein <marc.woerlein@gmx.de>
 */
public class Embedding implements Comparable{
    private Embedding parent;
    private int node;
    protected Graph graph;
    protected ClassifiedGraph orig;

    protected Leg correspondingLeg=null;
    protected Embedding next=null;
    
    /** for order and fast joining like the original code */ 
    public int id=idc++;//hashCode();
    private static int idc=0;

    
    /**
     * creats an initial embedding for a single renamed graph/original graph
     * @param graph
     * @param orig
     */
    public Embedding(Graph graph, ClassifiedGraph orig){
        parent=null;
        this.graph=graph;
        this.orig=orig;
        this.node=Graph.NO_NODE;
    }
    
    /** @return the renamed Graph, this embedding is for */
    public final Graph getGraph(){ return graph; }
    /** @return the original Graph, this embedding is for */
    public final ClassifiedGraph getOrig(){ return orig; }
    /** @return the ID of the parent Embedding */
    public final int getParentID(){ return parent.id; }
    /** @return the last inserted node */
    public final int getNode(){ return node; }
    /** @return the parrent embedding */
    public final Embedding getParent(){ return parent; }
    
    protected Embedding(Embedding parent, int node, Graph graph, ClassifiedGraph orig){
        this.parent=parent;
        this.node=node;
        this.graph=graph;
        this.orig=orig;
    }
    
    
    /**
     * Defines which Leg this embedding corresponds to
     * @param leg
     */
    public void setCorrespondingLeg(Leg leg){ correspondingLeg=leg; }
    
    /**
     * iterativ search for the node which corresponds to the given node
     * @param node the node of the Database Graph 
     * @return the corresponding node (or Graph.NO_NODE)
     */
    public int getCorrespondingNode(int node){
        for (Embedding ack=this;ack!=null;ack=ack.parent){
            if (ack.node==node) return ack.correspondingLeg.cor;
        }
        return Graph.NO_NODE;
    }
    
    /*
     *  (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object o){
        Embedding other=(Embedding) o;
        if (parent.id!=other.parent.id) return parent.id-other.parent.id;
        return id-other.id;
    }

    /*
     *  (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString(){ return node+"["+graph.getName()+"]"+"("+parent.id+")"; }
   
}
