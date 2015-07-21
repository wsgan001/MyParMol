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

import java.util.*;
import de.parmol.graph.*;

/**
 * This class orders the Labels of the given graphs by frequency,
 * builds renamed graphs and creates initial Embeddings 
 *
 * @author Marc Woerlein <marc.woerlein@gmx.de>
 */
public class DataBase implements EdgeNodeRelabler{
    
    private FreqLabel edges[];	//maps ordered edgeLabel to real edgeLabel
    private FreqLabel nodes[];	//maps ordered nodeLabel to real nodeLabel
    private int reNodes[];		//maps real nodeLabel to ordered nodeLabel
    private int reEdges[];		//maps real edgeLabel to ordered edgeLabel
    private Leg[] freqLegs;
//    private Collection relabeledGraphs;
    private int maxNodeLabel;	//the highest label occured in the database 
    private int maxEdgeLabel;
    private int maxNodes;
    private int maxNewNodeLabel,maxNewEdgeLabel;
    
    /**
     * This class is for counting the frequencies for each node/edge label
     * and defines the order of these taged labels
     * @author Marc Woerlein (simawoer@stud.informatik.uni-erlangen.de)
     */
    private class FreqLabel implements Comparable{
        private int label;
        private float[] freq;
        private Graph last;
        
        public int compareTo(Object o){
            FreqLabel other=(FreqLabel) o;
            int f=(int)(other.freq[0]-freq[0]); //or some other order
            if (f==0) return label-other.label;
            return f;
        }
        public String toString(){
            String ret=label+"["+freq[0];
            for (int i=1;i<freq.length;i++) ret+=", "+freq[i];
            return ret+"]";
        }
        FreqLabel(int label,int length){
            this.label=label;
            this.freq=new float[length];
        }
    }
    
    /**
     * This Function reads the whole set of graphs, 
     * counts the occurences of each label and fills the renaming arrays
     * @param graphs the set of original classified graphs
     * @param minFreq the minimal frequencies for pruning infrequent labels
     */
    private void getOrderdFrequentLabels(Collection graphs, float[] minFreq){
        Map nodeMap=new HashMap();
        Map edgeMap=new HashMap();
        int length=minFreq.length;
        for (Iterator git=graphs.iterator();git.hasNext();){
            ClassifiedGraph ack=(ClassifiedGraph) git.next();
            for (int i=0;i<ack.getNodeCount();i++){
                int label=ack.getNodeLabel(ack.getNode(i));
                FreqLabel l=(FreqLabel) nodeMap.get(new Integer(label));
                if (l==null){
                    l=new FreqLabel(label,length);
                    nodeMap.put(new Integer(label),l);
                    if (l.label>maxNodeLabel) maxNodeLabel=l.label;
                }
                if (l.last!=ack){
                    l.last=ack;
                    for (int j=0;j<length;j++) l.freq[j]+=ack.getClassFrequencies()[j];
                }
            }
            for (int i=0;i<ack.getEdgeCount();i++){
                int label=ack.getEdgeLabel(ack.getEdge(i));
                FreqLabel l=(FreqLabel) edgeMap.get(new Integer(label));
                if (l==null){
                    l=new FreqLabel(label,length);
                    edgeMap.put(new Integer(label),l);
                    if (l.label>maxEdgeLabel) maxEdgeLabel=l.label;
                }
                if (l.last!=ack){
                    l.last=ack;
                    for (int j=0;j<length;j++) l.freq[j]+=ack.getClassFrequencies()[j];
                }
            }
        }
        Set nodeSet=new TreeSet();
        for (Iterator nit=nodeMap.values().iterator();nit.hasNext();){
            boolean freq=true;
            FreqLabel l=(FreqLabel) nit.next();
            for (int i=0;i<length;i++) freq &=(l.freq[i]>=minFreq[i]);
            if (freq) {
                nodeSet.add(l);
            }
        }
        maxNewNodeLabel=nodeSet.size();
        nodes=(FreqLabel[])(nodeSet.toArray(new FreqLabel[maxNewNodeLabel]));
        reNodes=new int[maxNodeLabel+1];
        for (int i=0;i<reNodes.length;i++) reNodes[i]=Graph.NO_NODE;
        for (int i=0;i<nodes.length;i++) reNodes[nodes[i].label]=i;
        
        Set edgeSet=new TreeSet();
        for (Iterator eit=edgeMap.values().iterator();eit.hasNext();){
            boolean freq=true;
            FreqLabel l=(FreqLabel) eit.next();
            for (int i=0;i<length;i++) freq &=(l.freq[i]>=minFreq[i]);
            if (freq) {
                edgeSet.add(l);
            }
        }
        maxNewEdgeLabel=edgeSet.size();
        edges=(FreqLabel[])(edgeSet.toArray(new FreqLabel[maxNewEdgeLabel]));
        reEdges=new int[maxEdgeLabel+1];
        for (int i=0;i<reEdges.length;i++) reEdges[i]=Graph.NO_EDGE;
        for (int i=0;i<edges.length;i++) reEdges[edges[i].label]=i;
    }
    
    /**
     * This function is for building renamed graphs out of the original graphs
     * and creats for each frequent node the corresponding initial embedding 
     * @param graphs the set of original classified graphs
     * @param factory to build renamed graphs
     */
    private void buildRenamedGraphs(Collection graphs, GraphFactory factory){
//        relabeledGraphs=new HashSet();
//        Map map=new HashMap();
        freqLegs=new Leg[maxNewNodeLabel];
        for (int i=0;i<maxNewNodeLabel;i++){
            freqLegs[i]=new Leg(i);
        }
        for (Iterator git=graphs.iterator();git.hasNext();){
            ClassifiedGraph ack=(ClassifiedGraph) git.next();
            MutableGraph mut=factory.createGraph(ack.getName()); //TODO: copy class frequencies
            int ackn=ack.getNodeCount();
            if (ackn>maxNodes) maxNodes=ackn;
            int[] ackNodes=new int[ackn];
            /*relabel Nodes*/
            for (int i=0;i<ackn;i++){
                int lab=ack.getNodeLabel(ack.getNode(i));
                if (reNodes[lab]!=Graph.NO_NODE){
                    ackNodes[i]=mut.addNode(reNodes[lab]);
                } else { 
                    ackNodes[i]=Graph.NO_NODE;
                }
            }
            /*relabel Edges*/
            for (int i=0;i<ack.getEdgeCount();i++){
                int e=ack.getEdge(i);
                int lab=ack.getEdgeLabel(e);
                if (reEdges[lab]!=Graph.NO_EDGE 
                        && ackNodes[ack.getNodeA(e)]!=Graph.NO_NODE
                        && ackNodes[ack.getNodeB(e)]!=Graph.NO_NODE){
                    mut.addEdge(ackNodes[ack.getNodeA(e)],ackNodes[ack.getNodeB(e)]
                                                                   ,reEdges[lab]);
                }
            }
            /*create Initial Embeddings for the new Graph*/
            Embedding parent=new Embedding(mut,ack);
            for (int i=0;i<mut.getNodeCount();i++){
                int node=mut.getNode(i);
                int label=mut.getNodeLabel(node);
                freqLegs[label].add(new Embedding(parent,node,mut,ack));
            }
            //relabeled Graph done
        }
    }
    
    /**
     * creats the whole renamed DataBase and its initial legs
     * @param graphs the set of original classified graphs
     * @param minFreq the minimal frequencies for pruning infrequent labels
     * @param factory to build renamed graphs
     */
    public DataBase(Collection graphs, float[] minFreq, GraphFactory factory){
        maxNodeLabel=-1;
        maxEdgeLabel=-1;
        maxNodes=-1;
        getOrderdFrequentLabels(graphs,minFreq);
        buildRenamedGraphs(graphs,factory);
    }

    /**
     * @return an array of initial and frequent legs
     */
    public Leg[] getFreqLegs(){ return freqLegs; }
    
    /**
     * @return a new LegSet of with the correct size for these DataBase
     */
    public final LegSet getLegSet(){ return new LegSet(maxNewEdgeLabel,maxNewNodeLabel,maxNodes); }
    
    /*
     *  (non-Javadoc)
     * @see de.parmol.EdgeNodeRelabler
     */
    public int getRealNodeLabel(int orderedLabel){ return nodes[orderedLabel].label; }
    public int getRealEdgeLabel(int orderedLabel){ return edges[orderedLabel].label; }
    public int getNodeLabel(int origLabel){ return reNodes[origLabel]; }
    public int getEdgeLabel(int origLabel){ return reEdges[origLabel]; }
    
    /*
     *  (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString(){
        String ret="DataBase\n"+nodes.length+": "+nodes[0].toString();
        for (int i=1;i<nodes.length;i++) ret+=", "+nodes[i].toString();
        ret+="\n"+edges[0].toString();
        for (int i=1;i<edges.length;i++) ret+=", "+edges[i].toString();
        return ret;
    }
}
