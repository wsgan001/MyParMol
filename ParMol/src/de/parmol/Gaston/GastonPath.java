/*
 * Created on Feb 11, 2005
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeSet;

import de.parmol.Settings;
import de.parmol.graph.Graph;
import de.parmol.graph.MutableGraph;
import de.parmol.util.Debug;

/**
 * This class implements the recursiv search for paths (and rings) in the Gaston algorithm.
 * It also starts the search for trees, if necessary
 *
 * @author Marc Woerlein <marc.woerlein@gmx.de>
 */
public class GastonPath {
    private final static int UNKNOWN=-2;
    private MutableGraph me;	//The corresponding path with real labels
    private GastonSet fragments;//The set for found frequent Fragments 
    private EdgeNodeRelabler enr;
    private LegSet ls;			//for efficient? extending
    private boolean pathsOnly,doCycles;
    private float[] minFreq;
    private GastonTree tree;
    private Settings m_settings;
    
    /**
     * initialise the local variables
     * @param me an empty Graph with will be mutated to the found fragments 
     * @param relabel for renaming database labels to real labels
     * @param ls a set for storing ang sorting new found legs
     * @param fragments the set for found fragments
     * @param minFreq the minimum frequencies
     * @param pathsOnly
     * @param doCycles
     * @param settings the whole settings for this run
     */
    public GastonPath(MutableGraph me, EdgeNodeRelabler relabel,LegSet ls, GastonSet fragments
            ,float[] minFreq,boolean pathsOnly, boolean doCycles, Settings settings){
        this.me=me;
        this.ls=ls;
        this.enr=relabel;
        this.fragments=fragments;
        this.minFreq=minFreq;
        this.pathsOnly=pathsOnly;
        this.doCycles=doCycles;
	this.m_settings=settings;
        if (!pathsOnly) this.tree=new GastonTree (relabel,ls,fragments,minFreq,doCycles,settings); 
    }

    /**
     * calculates new legs extending the last node Leg
     * it uses the LegSet class as saving structures
     * @param last the last inserted Leg
     * @param ret the set for new Legs
     * @param max the maximum support which occors in the initial Set 
     * @param me a copy of the local <code> me </code>
     * @return the maximum support which occors in the Set
     */
    private float[] extend(Leg last,Collection ret,float[] max,Graph me){
//        int nodeA=last.getNodeA();
        int fromNode=last.cor;
        for (Embedding e=last.first;e!=null;e=e.next){
            Graph g=e.graph;
            for (int i=0;i<g.getDegree(e.getNode());i++){
                int edge=g.getNodeEdge(e.getNode(),i);
                int oNode=g.getOtherNode(edge,e.getNode());
                int cNode=e.getCorrespondingNode(oNode);
                if (cNode==Graph.NO_NODE){
                    Leg n=(Leg)ls.getNode(fromNode,g.getEdgeLabel(edge),g.getNodeLabel(oNode));
                    n.add(new Embedding(e,oNode,g,e.orig));
                }else if ( doCycles && me.getEdge(fromNode,cNode)==Graph.NO_EDGE){
                    Leg n=(Leg)ls.getCycle(fromNode,g.getEdgeLabel(edge),cNode);
                    n.add(new Embedding(e,Graph.NO_NODE,g,e.orig));
                }
            }
        }
        return ls.clearAndGetFrequentLegs(minFreq,ret,max);
    }
    
    /**
     * calculates (if necessary) the frontSymmetry for the given path
     * @param path
     * @param frontSymmetry the precalculatet frontSymmetry  
     * @param frontNode
     * @param backNode
     * @return the true frontSymmetery
     */
    private int getFrontSymmetry(Graph path,int frontSymmetry, int frontNode, int backNode){
        if (frontSymmetry==UNKNOWN){
            int tmp,c;
            int fn=frontNode, fe=path.getNodeEdge(fn,0);
            int bn=backNode, be=path.getNodeEdge(bn,0);
            //step backnode
            bn=path.getOtherNode(be,bn);
            c=0; while ((tmp=path.getNodeEdge(bn,c++))==be); be=tmp;
            do{
                c=enr.getNodeLabel(path.getNodeLabel(bn))-enr.getNodeLabel(path.getNodeLabel(fn));
                if (c>0) return frontSymmetry=1; if (c<0) return frontSymmetry=-1;
                c=enr.getEdgeLabel(path.getEdgeLabel(be))-enr.getEdgeLabel(path.getEdgeLabel(fe));
                if (c>0) return frontSymmetry=1; if (c<0) return frontSymmetry=-1;
                //step Nodes
                fn=path.getOtherNode(fe,fn); if (fn==bn) return frontSymmetry=0;
                bn=path.getOtherNode(be,bn); if (fn==bn) return frontSymmetry=0;
                //step edges
                c=0; while ((tmp=path.getNodeEdge(fn,c++))==fe); fe=tmp;
                c=0; while ((tmp=path.getNodeEdge(bn,c++))==be); be=tmp;
            } while(frontSymmetry==UNKNOWN);
        }
        return frontSymmetry;
    }
    /**
     * calculates (if necessary) the backSymmetry for the given path
     * @param path
     * @param backSymmetry the precalculatet backSymmetry
     * @param frontNode
     * @param backNode
     * @return the true backSymmetry
     */
    private int getBack(Graph path,int backSymmetry, int frontNode, int backNode){
        if (backSymmetry==UNKNOWN){
            int tmp,c;
            int fn=frontNode, fe=path.getNodeEdge(fn,0);
            int bn=backNode, be=path.getNodeEdge(bn,0);
            //step front node
            fn=path.getOtherNode(fe,fn);
            c=0; while ((tmp=path.getNodeEdge(fn,c++))==fe); fe=tmp;
            do{
                c=enr.getNodeLabel(path.getNodeLabel(bn))-enr.getNodeLabel(path.getNodeLabel(fn));
                if (c>0) return backSymmetry=1; if (c<0) return backSymmetry=-1;
                c=enr.getEdgeLabel(path.getEdgeLabel(be))-enr.getEdgeLabel(path.getEdgeLabel(fe));
                if (c>0) return backSymmetry=1; if (c<0) return backSymmetry=-1;
                //step Nodes
                fn=path.getOtherNode(fe,fn); if (fn==bn) return backSymmetry=0;
                bn=path.getOtherNode(be,bn); if (fn==bn) return backSymmetry=0;
                //step edges
                c=0; while ((tmp=path.getNodeEdge(fn,c++))==fe); fe=tmp;
                c=0; while ((tmp=path.getNodeEdge(bn,c++))==be); be=tmp;
            } while(backSymmetry==UNKNOWN);
        }
        return backSymmetry;
    }
    
    /**
     * calculates the extensions of the given Path and 
     * add it to the fragmentSet if necessary
     * @param last the last Leg, which builds the given path
     * @param path the path, which will be added as Fragment
     * @param legs a Set of possible Extensions of the old path
     * @return a set of all possible Extensions of the new path  
     */
    private Collection getNewLegsAndAddFragment(Leg last,Graph path,Collection legs){
        float[] max=Leg.empty; //the maximal frequency of the children

        Collection newLegs=new TreeSet();

        for (Iterator lit=legs.iterator();lit.hasNext();){
            Leg n;
            Leg l1=(Leg)lit.next();
            if (!pathsOnly || path.getDegree(l1.getRef().getNodeA())<=1){
                n=l1.join(last);
                if ((n).isFrequent(minFreq)) {
                    newLegs.add(n);
                    max=n.getMax(max);
                }
            }
        }
        
	max=extend(last,newLegs,max,path);
        if (max==null || !last.isFrequent(max,max)){
		fragments.add(last.getFragment((Graph)path.clone()));
	}

        return newLegs;
    }
    
    /**
     * recursive search of paths
     * @param myLegs
     * @param me
     * @param frontNode
     * @param backNode
     * @param fnLabel
     * @param feLabel
     * @param bnLabel
     * @param beLabel
     * @param ts
     * @param fs
     * @param bs
     */
    private void findPaths(Collection myLegs, MutableGraph me,
            int frontNode, int backNode, 
            int fnLabel,int feLabel, int bnLabel, int beLabel,
            int ts, int fs, int bs){
    	if (Debug.dlevel==2) Debug.println("doing "+m_settings.serializer.serialize(me));
    	if (Debug.dlevel>2) Debug.println("doing "+m_settings.serializer.serialize(me)+" (ts "+ts+", fs "+fs+", bs "+bs+")");
    	
    	//try extending with all given legs 
        for (Iterator lit=myLegs.iterator();lit.hasNext();){
            Leg ack=(Leg) lit.next();
            
            if (ack.getRef().isCycleRefinement()){ 
                int nodeA=ack.getRef().getNodeA();
                int nodeB=ack.getRef().getNodeB();
                // only Rings will be created out of paths
                if (((nodeA==frontNode)&&(nodeB==backNode))
                        || ((nodeB==frontNode)&&(nodeA==backNode))){
                    int edge=me.addEdge(nodeA,nodeB,enr.getRealEdgeLabel(ack.getRef().getEdgeLabel()));
                    
                    fragments.filteredadd(ack.getFragment((Graph)me.clone()));
                    
                    me.removeEdge(edge);
                }
            } else if (ack.getRef().getNodeA()==frontNode){ //path extension at the front 
            	
                int tnl=ack.getRef().getToLabel();
                int tel=ack.getRef().getEdgeLabel();
                if (frontNode==backNode){ //adding first edge
                    if (tnl>=bnLabel){
                        int node=ack.cor=me.addNodeAndEdge(frontNode
                                ,enr.getRealNodeLabel(tnl),enr.getRealEdgeLabel(tel));
                        if (tnl>bnLabel) 
                            findPaths(getNewLegsAndAddFragment(ack,me,myLegs),me,
                                    node,backNode,
                                    tnl,tel,bnLabel,tel,
                                    -1,0,0);
                        else findPaths(getNewLegsAndAddFragment(ack,me,myLegs),me,
                                node,backNode,
                                tnl,tel,bnLabel,tel,
                                0,0,0);
                        me.removeNode(node);
                    }
                } else {
                    if ((tnl>bnLabel) || ((tnl==bnLabel)&&(tel>beLabel))
                            || ((tnl==bnLabel) && (tel==beLabel) && ((fs=getFrontSymmetry(me,fs,frontNode,backNode))<=0))){
                        int node=ack.cor=me.addNodeAndEdge(frontNode
                                ,enr.getRealNodeLabel(tnl),enr.getRealEdgeLabel(tel));
                        if ((tnl==bnLabel) && (tel==beLabel) && (fs==0)) 
                            findPaths(getNewLegsAndAddFragment(ack,me,myLegs),me,
                                    node,backNode,
                                    tnl,tel,bnLabel,beLabel,
                                    0,UNKNOWN,ts);
                        else findPaths(getNewLegsAndAddFragment(ack,me,myLegs),me,
                                node,backNode,
                                tnl,tel,bnLabel,beLabel,
                                -1,UNKNOWN,ts);
                        
                        me.removeNode(node);
                    }
                    
                }
            } else if (ack.getRef().getNodeA()==backNode){//path extention at the back
                if (ts!=0){
                    int tnl=ack.getRef().getToLabel();
                    int tel=ack.getRef().getEdgeLabel();
                    if ((tnl>fnLabel) || ((tnl==fnLabel)&&(tel>feLabel))
                            || ((tnl==fnLabel) && (tel==feLabel) && ((bs=getBack(me,bs,frontNode,backNode))>=0))){

                        int node=ack.cor=me.addNodeAndEdge(backNode
                                ,enr.getRealNodeLabel(tnl),enr.getRealEdgeLabel(tel));
                        if ((tnl==fnLabel) && (tel==feLabel) && (bs==0))
                            findPaths(getNewLegsAndAddFragment(ack,me,myLegs),me,
                                    frontNode,node,
                                    fnLabel,feLabel,tnl,tel,
                                    0,ts,UNKNOWN);
                        else findPaths(getNewLegsAndAddFragment(ack,me,myLegs),me,
                                frontNode,node,
                                fnLabel,feLabel,tnl,tel,
                                1,ts,UNKNOWN);
                        
                        me.removeNode(node);
                    }
                }
            } else { //all other extensions will lead to trees
                tree.findTrees(me,frontNode,backNode,ack,myLegs);
            }
        }
    }
    
    /**
     * starts the search for all paths, trees and cyclic graphs, 
     * which depends on the given leg
     * @param initialLeg
     */
    public void findPaths(Leg initialLeg){
        long start=System.currentTimeMillis();
        int label=initialLeg.getRef().getToLabel();
        int node=initialLeg.cor=me.addNode(enr.getRealNodeLabel(label));
        
        Debug.print(1,"doing "+m_settings.serializer.serialize(me)+" ...");
        fragments.add(initialLeg.getFragment((Graph)me.clone()));
        
        //initial optimized extend
        
        for (Embedding e=initialLeg.first;e!=null;e=e.next){
             Graph g=e.getGraph();
             for (int i=0;i<g.getDegree(e.getNode());i++){
                 int edge=g.getNodeEdge(e.getNode(),i);
                 int oNode=g.getOtherNode(edge,e.getNode());
                     Leg n=(Leg)ls.getNode(node,g.getEdgeLabel(edge),g.getNodeLabel(oNode));
                     n.add(new Embedding(e,oNode,g,e.orig));
             }
        }
        Collection legs=new ArrayList();
//        float[] max=
        	ls.clearAndGetFrequentLegs(minFreq,legs,Leg.empty);

        findPaths(legs,me,node,node,
                label,Graph.NO_EDGE,label,Graph.NO_EDGE,
                0,0,0);
        me.removeNode(node);
        Debug.println(1," done ("+(System.currentTimeMillis()-start)+" ms)");
    }

}
