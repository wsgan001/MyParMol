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
import de.parmol.util.*;

import java.util.*;

/**
 * This class representates a Leg 
 * This is the combination of an Refinement and all its possible embeddings
 *
 * @author Marc Woerlein <marc.woerlein@gmx.de>
 */
public class Leg implements Comparable {
    /** the length of the frequencies Array */
	public static int length;
	/** a global empty frequencies array */
    public static float[] empty;

    protected Refinement ref,tmpref;
//    private Embedding[] arr=null; //the sorted array of inserted Embeddings
    protected int cor;
    
    private float[] freq;
    private Collection graphs;
    
    protected Embedding first;
    private Embedding last=null;

    /**
     * creates an initial Leg for the given label
     * @param toLabel
     */
    public Leg(int toLabel){
        this.tmpref=this.ref=new Refinement(toLabel);
        this.freq=new float[length];
        cor=Graph.NO_NODE;
        graphs=new ArrayList();
    }

    /**
     * creates a leg for the given refinement
     * @param ref
     */
    public Leg(Refinement ref){
        this.tmpref=this.ref=ref;
        this.freq=new float[length];
        cor=Graph.NO_NODE;
        graphs=new ArrayList();
    }
    
    private Leg(Refinement ref,Embedding first,Embedding last, Collection graphs,float[] freq,int cor){
        this.tmpref=this.ref=ref;
        this.freq=freq;
        this.first=first;
        this.last=last;
        this.graphs=graphs;
        this.cor=cor;
    }
    
    /** @return the corresponding Refinement of this leg */
    public final Refinement getRef(){ return ref; }

    /**
     * @param ref
     * @return a leg corresponding to the given DepthRefienment
     */
    public Leg getDepthLeg(DepthRefinement ref){
        return new Leg(ref,first,last,graphs,freq,cor);
    }
    
    public int compareTo(Object o){ return ref.compareTo(((Leg)o).ref); }
    
    /**
     * adds the given embedding to this leg
     * @param e
     */
    public void add(Embedding e){
        ClassifiedGraph g=e.orig;
        if (last==null){
            first=last=e;
            graphs.add(g);
            float[] f=g.getClassFrequencies();
            for (int i=0;i<length;i++) freq[i]+=f[i];
        } else {
            if (last.orig!=g){
                graphs.add(g);
                float[] f=g.getClassFrequencies();
                for (int i=0;i<length;i++) freq[i]+=f[i];
            }
            last=last.next=e;
        }
        e.correspondingLeg=this;
    }

    /**
     * checks if this leg is frequent 
     * @param minFreq
     * @return <code> true </code> if frequent
     */
    public boolean isFrequent(float[] minFreq){
        for (int i=0;i<length;i++) 
            if (freq[i]<minFreq[i]) return false;
        return true;
    }
    /**
     * checks if this leg is frequent 
     * @param minFreq
     * @param maxFreq
     * @return <code> true </code> if frequent
     */
    public boolean isFrequent(float[] minFreq,float[] maxFreq){
        for (int i=0;i<length;i++) 
            if ((freq[i]<minFreq[i]) || (freq[i]>maxFreq[i])) return false;
        return true;
    }
    
    /** @return an iterator over all added embeddings */
    public Iterator iterator(){ 
        return new Iterator(){
            Embedding ack=first;
            public boolean hasNext(){return (ack!=null);}
            public Object next(){ Embedding tmp=ack; ack=ack.next; return tmp; }
            public void remove(){}
        };
    }
    
    /**
     * joins this leg with the given
     * @param leg1
     * @return the new leg, resulting from the join
     */
    public Leg join(Leg leg1){
	/* the optimized joining algorithm of th e original implementation (invariant test are seperated) */
        Leg nl=new Leg(ref);
        Embedding a1=leg1.first;
        Embedding a2=this.first;
        if (ref.isCycleRefinement()&&(this!=leg1)){
            while (a1!=null && a2!=null){
                int id2=a2.getParent().id;
                while (a1!=null && a1.getParent().id<id2) a1=a1.next;
                if (a1!=null) {
                    int id1=a1.getParent().id;
                    while (a2!=null && a2.getParent().id<id1) a2=a2.next;
                    Embedding m2=a2;
                    for (;a2!=null && a2.getParent().id==id1;a2=a2.next){
                        nl.add(new Embedding(a1,a2.getNode(),a1.graph,a1.orig));
                    }
                    if (a2!=m2) for (a1=a1.next;a1!=null && a1.getParent().id==id1;a1=a1.next)
                        for (Embedding e=m2;e!=a2;e=e.next)
                            nl.add(new Embedding(a1,e.getNode(),a1.graph,a1.orig));
                }
            }
        }else{
            while (a1!=null && a2!=null){
                int id2=a2.getParent().id;
                while (a1!=null && a1.getParent().id<id2) a1=a1.next;
                if (a1!=null) {
                    int id1=a1.getParent().id;
                    while (a2!=null && a2.getParent().id<id1) a2=a2.next;
                    Embedding m2=a2;
                    for (;a2!=null && a2.getParent().id==id1;a2=a2.next){
                        if (a1.getNode()!=a2.getNode()) nl.add(new Embedding(a1,a2.getNode(),a1.graph,a1.orig));
                    }
                    if (a2!=m2) for (a1=a1.next;a1!=null && a1.getParent().id==id1;a1=a1.next)
                        for (Embedding e=m2;e!=a2;e=e.next)
                            if (a1.getNode()!=e.getNode()) nl.add(new Embedding(a1,e.getNode(),a1.graph,a1.orig));
                }
            }

        }
        return nl;
    }
    
    /**
     * @param g the graph this Leg belong to
     * @return a FrequnetFragment corresponding to this Leg
     */
    public FrequentFragment getFragment(Graph g){
        return new FrequentFragment(g,graphs,freq);
    }
    
    public String toString(){ return ref.toString(); }
    
    /**
     * for simple closed Fragment reduction
     * @param other
     * @return tha maximal frequencies of this and the <code> other </code> leg 
     */
    public final float[] getMax(float[] other){
        if (other==null) return null;
        for (int i=0;i<length;i++) {
            if (freq[i]>other[i]) return freq;
            if (other[i]>freq[i]) return other;
        }
        return other;
    }
    
}
