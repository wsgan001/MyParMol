/*
 * Created on Feb 18, 2005
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

import java.util.Collection;
import java.util.Iterator;
import java.util.TreeSet;

import de.parmol.Settings;
import de.parmol.graph.Graph;
import de.parmol.graph.MutableGraph;
import de.parmol.util.Debug;

/**
 * This class implements the recursiv search for trees in the Gaston algorithm.
 * It also starts the search for cyclic graphs, if necessary
 * 
 * the two sepearated rooted-Trees which build a whole tree were combined in a single list
 * by the way that the depth of the left tree is shifted beyond the depth of the rigth tree:
 * 
 * depth: 5 : 4 : 3 :: 0 : 1 : 2
 *        C---C---C-::-C---C---C
 *          : | :   :: | :   :
 *        C---+ :   :: +---C :
 *          :   :   ::   :   :
 * 
 * there is an array for the backbone Refinements (bb),
 * one with the Refinements of the rigth most paths (rmp) and
 * one with the GraphNodes of the rigth most path (rmpNodes)
 * the pseudoNextPrefixNode is used to avoid duplications by the extension of symmetric paths 
 * (like the nextPrefixNode to avoid duplications for similiar branches)
 *  
 * This implementation differs from the original one, because the description of Gaston differs.
 * 
 * @author Marc Woerlein <marc.woerlein@gmx.de>
 */
public class GastonTree {
    protected GastonSet fragments;
    protected EdgeNodeRelabler enr;
    protected LegSet ls;
    protected boolean doCycles;
    protected float[] minFreq;
    protected GastonCycle cycle;
    private Settings m_settings;
    
    /**
     * creates a new object, responsible for the serach of trees
     * @param relabel for relabeling database labels to real labels
     * @param ls a set for storing ang sorting new found legs
     * @param fragments the set for found fragments
     * @param minFreq the minimum frequencies
     * @param doCycles
     * @param settings the whole settings for this run
     */
    public GastonTree(EdgeNodeRelabler relabel, LegSet ls, GastonSet fragments
            ,float[] minFreq, boolean doCycles, Settings settings){
        this.ls=ls;
        this.enr=relabel;
        this.fragments=fragments;
        this.minFreq=minFreq;
        this.doCycles=doCycles;
	this.m_settings=settings;
        if (doCycles) cycle=new GastonCycle(relabel,fragments,minFreq,settings);
    }
    
    /**
     * calculates new legs extending the last node Leg
     * it uses the LegSet class as saving structures
     * @param bb
     * @param maxRigthDepth the shifting value between the two subtrees
     * @param leftArrayLegth
     * @param npn the nextPrefixNode
     * @param pnpn the pseudoNextPrefixNode
     * @param maxDepth the allowed maximal depth
     * @param last the last inserted Refinement
     * @param ret the set for new Legs
     * @param max the maximum support which occors in the initial Set 
     * @param me a copy of the local <code> me </code>
     * @return the maximum support which occors in the Set
     */
    private float[] extend(DepthRefinement[] bb, int maxRigthDepth, int leftArrayLegth
            , DepthRefinement npn, DepthRefinement pnpn, int maxDepth
            , Leg last, Collection ret, float[] max,Graph me){
        int fromNode=last.cor;
//        int nodeA=last.getNodeA();
        int depth=last.getRef().getDepth()+1;
        boolean test=(depth<=maxDepth && depth!=maxRigthDepth+1); 
		//checks if last is on maximal depth. No further noderefinements are allowed
        boolean npntest=test && (npn==null || (depth!=maxDepth && bb[depth]==npn));
		//checks if a test against the next prefix node ist neccessary
        boolean pnpntest=test && (pnpn==null || depth>maxRigthDepth);
		//checks if a test against the pseudo next prefix node ist neccessary
        for (Embedding e=last.first;e!=null;e=e.next){//*/
            Graph g=e.graph;
            for (int i=0;i<g.getDegree(e.getNode());i++){
                int edge=g.getNodeEdge(e.getNode(),i);
                int oNode=g.getOtherNode(edge,e.getNode());
                int cNode=e.getCorrespondingNode(oNode);
                if (cNode==Graph.NO_NODE){
                    int el=g.getEdgeLabel(edge),nl=g.getNodeLabel(oNode);
                    if (test && (npntest || npn.compareTo(depth, el, nl)>=0)
                            && (pnpntest || pnpn.compareTo(depth, el, nl, leftArrayLegth)>=0)){
                        Leg n=(Leg)ls.getDepth(depth,el,nl);
                        n.add(new Embedding(e,oNode,g,e.orig));
                    }
                }else if ( doCycles && me.getEdge(fromNode,cNode)==Graph.NO_EDGE){
                    Leg n=(Leg)ls.getCycle(fromNode,g.getEdgeLabel(edge),cNode);
                    n.add(new Embedding(e,Graph.NO_NODE,g,e.orig));
                }
            }
        }
        return ls.clearAndGetFrequentLegs(minFreq,ret,max);
}
    
    /**
     * calculates the extensions of the given Tree and 
     * add it to the fragmentSet if necessary
     * @param bb
     * @param maxRigthDepth
     * @param leftArrayLength
     * @param npn the nextPrefixNode
     * @param pnpn the pseudoNextPrefixNode
     * @param maxDepth the allowed maximal depth
     * @param last the last inserted Refinement
     * @param tree the tree, which will be added as Fragment
     * @param legs a Set of possible Extensions of the old tree
     * @return a set of all possible Extensions of the new tree
     */
    private Collection getNewLegsAndAddFragment(DepthRefinement[] bb, int maxRigthDepth, int leftArrayLength, DepthRefinement npn
            , DepthRefinement pnpn, int maxDepth, Leg last, Graph tree, Collection legs){
        float[] max=Leg.empty;
        
        Collection newLegs=new TreeSet();
        int depth=last.getRef().getDepth();
        boolean npntest=(npn==null ||(depth!=maxDepth && bb[depth]==npn));
        for (Iterator lit=legs.iterator();lit.hasNext();){
            Leg n;
            Leg l1=(Leg)lit.next();
 	    //checks if a test against the pseudo next prefix node ist neccessary
	    boolean pnpntest=((pnpn==null || l1.getRef().getDepth()>maxRigthDepth 
                    	|| (l1.getRef().getDepth()==1 && bb.length%2==1 && bb[pnpn.getDepth()]==pnpn)));
		    
            if (l1.getRef().isCycleRefinement() || (l1.compareTo(last)<=0 
                    && (npntest || npn.compareTo(l1.ref)>=0)
                    && (pnpntest || pnpn.compareTo(l1.ref,leftArrayLength)>=0)
					   )){
                n=l1.join(last);
                if ((n).isFrequent(minFreq)) {
                    newLegs.add(n);
                    max=n.getMax(max);
                }
            }
        }
        
        max=extend(bb,maxRigthDepth,leftArrayLength
                ,npn,pnpn,maxDepth,last,newLegs,max,tree);


        if (max==null || !last.isFrequent(max,max)){
		fragments.add(last.getFragment((Graph)tree.clone()));
	}
        return newLegs;
    }
    
    /**
     * recursiv search for trees 
     * @param me
     * @param bb
     * @param maxRigthDepth
     * @param leftArrayLength
     * @param myLegs
     * @param maxDepth
     * @param rmpNodes
     * @param rmp
     * @param lend
     * @param rend
     * @param splittNode
     * @param npn
     * @param pnpn
     */
    private void findTrees(MutableGraph me, DepthRefinement[] bb, int maxRigthDepth, int leftArrayLength
            , Collection myLegs, int maxDepth, int[] rmpNodes, DepthRefinement[] rmp
            , DepthRefinement lend, DepthRefinement rend, DepthRefinement splittNode //the first node of the rigth most path with differs for backbone labelling (used to decide the maximal depth of the rigth most path)
            , DepthRefinement npn, DepthRefinement pnpn){
    	
        if (Debug.dlevel>=2) {
        	Debug.println("doing "+m_settings.serializer.serialize(me));
        }

        //try extending with all given legs 
        for (Iterator lit=myLegs.iterator();lit.hasNext();){
            Leg ack=(Leg) lit.next();
            if (ack.getRef().isCycleRefinement()){
                cycle.findCycles(me,ack,myLegs);
            } else {
                int depth=ack.getRef().getDepth();

                //save changing status values
                int old=rmpNodes[depth];
                DepthRefinement leftSibling=rmp[depth-1].right,ackr=(DepthRefinement) ack.ref
                		,olddepth=rmp[depth];
                
                rmpNodes[depth]=ack.cor=me.addNodeAndEdge(rmpNodes[depth-1]
                        ,enr.getRealNodeLabel(ack.getRef().getToLabel())
                        ,enr.getRealEdgeLabel(ack.getRef().getEdgeLabel()));
                
                //calculate new status
                DepthRefinement splittN,nl,nr,nnpn,npnpn;
                int maxD;//,maxD2;
                
                if (depth>maxRigthDepth) { //extension at the left tree
                    nl=lend.next=ackr; nr=rend;
                    maxD=maxRigthDepth+leftArrayLength;
                    npnpn=pnpn;
                } else { //extension at the rigth tree
                    nl=lend; nr=rend.next=ackr;
                    maxD=maxRigthDepth;
                    //calculate pseudoNextPrefixNode
                    if (pnpn!=null && pnpn.compareTo(ackr,leftArrayLength)==0){ 
                        if (pnpn.next==null) npnpn=bb[maxRigthDepth+1];
                        else npnpn=pnpn.next;
                    }else npnpn=null;
                }

                //calculate nextPrefixNode
                if (npn!=null && npn.compareTo(ackr)==0) nnpn=npn.next;
                else if (leftSibling!=null && leftSibling.compareTo(ackr)==0) 
                        nnpn=leftSibling.next;
                else if (rmp[depth-1]==bb[depth-1] && bb[depth].compareTo(ackr)==0)
                    nnpn=bb[depth].next;
                else nnpn=null;

                //calculate maxDepth and splittNode
                if ((splittNode.compareTo(ackr)>=0)
                        ||(rmp[depth-1]==splittNode && bb[depth-1].compareTo(splittNode)==0)){
                    splittN=ackr; if (bb[depth].compareTo(ackr)<0) maxD--;
                } else { splittN=splittNode; maxD=maxDepth; }
                
                rmp[depth]=rmp[depth-1].right=ackr;
                
                // recursiv call
                findTrees(me,bb,maxRigthDepth,leftArrayLength
                        ,getNewLegsAndAddFragment(bb,maxRigthDepth,leftArrayLength,nnpn,npnpn,maxD,ack,me,myLegs)
                        ,maxD,rmpNodes,rmp,nl,nr,splittN,nnpn,npnpn);
                
                // restore status
                me.removeNode(rmpNodes[depth]);
                rmp[depth]=olddepth;
                rmp[depth-1].right=leftSibling;
                rmpNodes[depth]=old;
            }
        }
    }
    
    /**
     * starts the search for all trees and cyclic graphs,
     * which depends on the given path extended by the given Leg 
     * @param path
     * @param frontNode the node nummer of one end of the path
     * @param backNode the node nummer of the other end of the path
     * @param ack the Leg which had to be added first 
     * @param legs a Set of possible Extensions of the tree
     */
    public void findTrees(MutableGraph path, int frontNode, int backNode, Leg ack, Collection legs){
        int ln=frontNode;
        int rn=backNode;
        int le=path.getNodeEdge(ln,0);
        int re=path.getNodeEdge(rn,0);
        {   //
        	// simple check if ack is a valid tree extension
            //
            int iln=path.getOtherNode(le,ln),irn=path.getOtherNode(re,rn);
            int lel=enr.getEdgeLabel(path.getEdgeLabel(le)),rel=enr.getEdgeLabel(path.getEdgeLabel(re));
            int lnl=enr.getNodeLabel(path.getNodeLabel(ln)),rnl=enr.getNodeLabel(path.getNodeLabel(rn));
            if ((ack.getRef().getNodeA()==iln && (lel<ack.getRef().getEdgeLabel() 
                    || (lel==ack.getRef().getEdgeLabel() && lnl<ack.getRef().getToLabel())))
                || (ack.getRef().getNodeA()==irn && (rel<ack.getRef().getEdgeLabel() 
                        || (rel==ack.getRef().getEdgeLabel() && rnl<ack.getRef().getToLabel())))) return;
        }
        
        //
        //create needed Arrays and calculate constants
        //
        int length=path.getNodeCount();
        DepthRefinement nodes[]=new DepthRefinement[length];
        DepthRefinement bb[]=new DepthRefinement[length];
        DepthRefinement rmp[]=new DepthRefinement[length];
        int rmpNodes[]=new int[length];
        int leftArrayLength=path.getNodeCount() / 2;
        int lastRigthDepth=(path.getNodeCount()-1) / 2;
        
        int sym=0;
        int tmp,count;
        
        //
        //create DepthRefinements for the backbone
        //
        DepthRefinement lend,rend,lack,rack,tmpr;
        nodes[ln]=lack=lend=new DepthRefinement(-1
                ,enr.getEdgeLabel(path.getEdgeLabel(le))
                ,enr.getNodeLabel(path.getNodeLabel(ln)));
        nodes[rn]=rack=rend=new DepthRefinement(-1
                ,enr.getEdgeLabel(path.getEdgeLabel(re))
                ,enr.getNodeLabel(path.getNodeLabel(rn)));
        tmp=lack.compareTo(rack); if (tmp!=0) sym=tmp;
        lack.nodeA=ln; rack.nodeA=rn;
        
        //iterativ creating
        while(re!=le){
            //step left
            ln=path.getOtherNode(le,ln);
            rn=path.getOtherNode(re,rn);
            if (rn==ln) break;

            count=0;while((tmp=path.getNodeEdge(ln,count++))==le); le=tmp;
            count=0;while((tmp=path.getNodeEdge(rn,count++))==re); re=tmp;

            nodes[ln]=tmpr=new DepthRefinement(-1
                    ,enr.getEdgeLabel(path.getEdgeLabel(le))
                    ,enr.getNodeLabel(path.getNodeLabel(ln)));
            tmpr.right=tmpr.next=lack;lack=tmpr;
            //step rigth
            nodes[rn]=tmpr=new DepthRefinement(-1
                    ,enr.getEdgeLabel(path.getEdgeLabel(re))
                    ,enr.getNodeLabel(path.getNodeLabel(rn)));
            tmpr.right=tmpr.next=rack;rack=tmpr;
            //get sym                
            tmp=lack.compareTo(rack); if (tmp!=0) sym=tmp;
            lack.nodeA=ln; rack.nodeA=rn;
        }
        
        if (sym==0 && (rn!=ln || ack.getRef().getNodeA()!=rn)) for (tmpr=rack;tmpr!=null;tmpr=tmpr.next) 
            if (tmpr==nodes[ack.getRef().getNodeA()]) return;
        //swap if rigth backbone < left backbone
        if (sym<0){
            tmpr=lack; lack=rack;rack=tmpr;
            tmpr=lend; lend=rend;rend=tmpr;
        }
        if (rn==ln){
            nodes[rn]=tmpr=new DepthRefinement(-1
                    ,enr.getEdgeLabel(path.getEdgeLabel(re))
                    ,enr.getNodeLabel(path.getNodeLabel(rn)));
            tmpr.right=tmpr.next=rack;rack=tmpr;tmpr.nodeA=rn;
        }
        rend.next=lack;tmpr=rack;
        
        //
        //set correct depth numbers to backbone refinements and fill arrays
        //
        for (int i=0;tmpr!=null;tmpr=tmpr.next,i++){
            rmpNodes[i]=tmpr.nodeA;
            tmpr.nodeA=i; //nodeA is the Depth in a DepthRefinement;
            rmp[i]=bb[i]=tmpr;
        }
        
        //
        //convert old legs to depthRefinements
        //
        Collection newLegs=new TreeSet();
        
        for (Iterator lit=legs.iterator();lit.hasNext();){
            Leg cur=(Leg)lit.next();
            if (cur.getRef().isCycleRefinement()) newLegs.add(cur);
            else {
                int cdepth=nodes[cur.getRef().getNodeA()].nodeA+1;
                DepthRefinement blub=new DepthRefinement(cdepth,cur.getRef().getEdgeLabel(),cur.getRef().getToLabel());
                if (cdepth!=length && cdepth!=lastRigthDepth+1 && (cdepth!=length-1 || bb[length-1].compareTo(blub)>=0)
                 && (cdepth!=lastRigthDepth || bb[lastRigthDepth].compareTo(blub)>=0)){
                    cur.ref=blub;
                    newLegs.add(cur);
                }
            }
        }
        
        //
        // add the given leg
        //
        int ackDepth=ack.getRef().getDepth();
        ack.cor=rmpNodes[ackDepth]=path.addNodeAndEdge(rmpNodes[ackDepth-1]
                ,enr.getRealNodeLabel(ack.getRef().getToLabel()),enr.getRealEdgeLabel(ack.getRef().getEdgeLabel()));
        DepthRefinement npn=null,pnpn=null,splittNode=null,ackr=(DepthRefinement)ack.ref;
        int maxDepth;

        if (ackDepth>lastRigthDepth) { //extension at the left tree
        	if (sym==0) pnpn=ackr;
            lend=lend.next=ackr;
            maxDepth=lastRigthDepth+leftArrayLength;
        } else { //extension at the rigth tree
            rend=rend.next=ackr;
            maxDepth=lastRigthDepth;
        }
        tmp=bb[ackDepth].compareTo(ackr);
        splittNode=ackr;
        if (tmp==0) npn=bb[ackDepth].next;
        else if (tmp<0) maxDepth--;
        
        rmp[ackDepth]=rmp[ackDepth-1].right=(DepthRefinement) ack.ref;
        
        if (pnpn!=null) Debug.println(2,"ackr.next= "+pnpn.next);
        //start recursiv search
        findTrees(path,bb,lastRigthDepth,leftArrayLength
                ,getNewLegsAndAddFragment(bb,lastRigthDepth,leftArrayLength,npn,pnpn,maxDepth,ack,path,newLegs)
                ,maxDepth,rmpNodes,rmp,lend,rend,splittNode,npn,pnpn);

        //
        //restore old legs
        //
        for (Iterator lit=legs.iterator();lit.hasNext();){
            Leg cur=(Leg)lit.next();
            cur.ref=cur.tmpref;
        }
        
        path.removeNode(rmpNodes[ackDepth]);
    }

}
