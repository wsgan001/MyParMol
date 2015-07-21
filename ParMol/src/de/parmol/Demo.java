/*
 * Created on Jun 13, 2006
 * 
 * Copyright 2006 Marc W??rlein
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
package de.parmol;

import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import de.parmol.FFSM.Matrix;
import de.parmol.graph.Graph;
import de.parmol.graph.UndirectedListGraph;
import de.parmol.util.FragmentSet;
import de.parmol.util.FrequentFragment;
import de.parmol.visualization.ColorSchema;
import de.parmol.visualization.DataAnalyser;

/**
 * This class is a small demo tool to demonstrate the funktoinality of the parmol package
 *
 * @author Marc Woerlein <Woerlein@informatik.uni-erlangen.de>
 *
 */
public class Demo extends Thread implements ActionListener{
	
	private final Demo me;
	private static final String[] options=new String[]{"-findPathsOnly=flase","-findTreesOnly=true","-findPathsOnly=true"};

	private Demo() { me=this; }
	private JPanel createPane(){
		GridBagLayout gb=new GridBagLayout();
        JPanel pane = new JPanel(gb);
        pane.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        
        GridBagConstraints c = new GridBagConstraints();
        
        algo=new JComboBox(new String[]{"GSpan","MoFa","Gaston","FFSM"});
        database=new JTextField("");//("data/IC93.sln.gz");
        frequency=new JTextField("");//("10%");
        start=new JButton("start search");
        start.addActionListener(this);
        graphBox=new JComboBox(new String[]{});
        graphBox.addActionListener(this);
        fragmentBox=new JComboBox(new String[]{});
        fragmentBox.addActionListener(this);
        typeBox=new JComboBox(new String[]{"all graphs","trees only","paths only"});
        closed=new JCheckBox("closed Fragments");

        c.fill = GridBagConstraints.BOTH;
        c.anchor=GridBagConstraints.CENTER;
        c.weightx=2.0;
        c.weighty=6.0;
        c.gridwidth = 2;
        c.gridheight = 6;
        chemPane=new JPanel(new GridLayout(1,1));
        gb.setConstraints(chemPane,c);
        pane.add(chemPane);

        c.gridheight = 1;
        c.weighty=0.0;
        c.weightx=0.0;
        gb.setConstraints(algo,c);
        pane.add(algo);

        JLabel ack=new JLabel("algorithm: ");
        c.fill = GridBagConstraints.NONE;
        c.anchor=GridBagConstraints.EAST;
        c.gridwidth = GridBagConstraints.REMAINDER;
        gb.setConstraints(ack,c);
        pane.add(ack);
        
        
        c.fill = GridBagConstraints.BOTH;
        c.anchor=GridBagConstraints.CENTER;
        c.gridwidth = 1;
        Dimension textsize=new Dimension(130,25);
        database.setMaximumSize(textsize);
        database.setSize(textsize);
        database.setPreferredSize(textsize);
        gb.setConstraints(database,c);
        pane.add(database);
        
        ack=new JLabel("database: ");
        c.fill = GridBagConstraints.NONE;
        c.anchor=GridBagConstraints.EAST;
        c.gridwidth = GridBagConstraints.REMAINDER;
        gb.setConstraints(ack,c);
        pane.add(ack);

        
        c.fill = GridBagConstraints.BOTH;
        c.anchor=GridBagConstraints.CENTER;
        c.gridwidth = 1;
        frequency.setMaximumSize(textsize);
        frequency.setSize(textsize);
        frequency.setPreferredSize(textsize);
        gb.setConstraints(frequency,c);
        pane.add(frequency);

        ack=new JLabel("frequency: ");
        c.fill = GridBagConstraints.NONE;
        c.anchor=GridBagConstraints.EAST;
        c.gridwidth = GridBagConstraints.REMAINDER;
        gb.setConstraints(ack,c);
        pane.add(ack);
        


        c.fill = GridBagConstraints.BOTH;
        c.anchor=GridBagConstraints.CENTER;
        c.gridwidth = 1;
        gb.setConstraints(typeBox,c);
        pane.add(typeBox);

        ack=new JLabel("graph type: ");
        c.fill = GridBagConstraints.NONE;
        c.anchor=GridBagConstraints.EAST;
        c.gridwidth = GridBagConstraints.REMAINDER;
        gb.setConstraints(ack,c);
        pane.add(ack);
        
        
        c.fill = GridBagConstraints.NONE;
        c.anchor=GridBagConstraints.WEST;
        c.gridwidth = GridBagConstraints.REMAINDER;
        gb.setConstraints(closed,c);
        pane.add(closed);

        ack=new JLabel("");
        c.fill = GridBagConstraints.NONE;
        c.anchor=GridBagConstraints.EAST;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.weighty=1.0;
        gb.setConstraints(ack,c);
        pane.add(ack);

        c.fill = GridBagConstraints.BOTH;
        c.anchor=GridBagConstraints.CENTER;
        c.gridwidth = 1;
        c.weightx=1.0;
        c.weighty=0.0;
        gb.setConstraints(graphBox,c);
        pane.add(graphBox);

        gb.setConstraints(fragmentBox,c);
        pane.add(fragmentBox);
        
        c.fill = GridBagConstraints.BOTH;
        c.anchor=GridBagConstraints.CENTER;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.weightx=0.0;
        gb.setConstraints(start,c);
        pane.add(start);

        return pane;
	}
	
	Graph[] graphs;
	FrequentFragment[] ff;
	int lastFF=-1;

	private JPanel chemPane;
	private JComboBox algo;
	private JTextField database;
	private JTextField frequency;
	private JButton start;
	private JComboBox graphBox;
	private JComboBox fragmentBox;
	private JComboBox typeBox;
	private JCheckBox closed;
	
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent event){
		final String command=event.getActionCommand();
		if (command.equals("start search")){

			Thread t=new Thread(){
				public void run() {
					me.actionPerformed(new ActionEvent(this,0,"running"));
					
					try{
						Settings settings=new Settings(
								new String[]{"-graphFile="+database.getText(),
										"-minimumFrequencies="+frequency.getText(),
										options[typeBox.getSelectedIndex()],
										"-closedFragmentsOnly="+closed.isSelected(),
										"-debug=-1"
									});
						AbstractMiner miner;
						switch (algo.getSelectedIndex()) {						
						case 0: miner = new de.parmol.GSpan.Miner(settings);
							break;
						case 1: miner = new de.parmol.MoFa.Miner(settings);
							break;
						case 2: miner = new de.parmol.Gaston.Miner(settings);
							break;
						case 3: miner = new de.parmol.FFSM.Miner(settings);
							break;
						default:
							miner = new de.parmol.GSpan.Miner(settings);
						}
					miner.setUp();
					miner.startMining();
					FragmentSet frags=miner.getFrequentSubgraphs();
					int size=frags.size();
					for (Iterator it=frags.iterator();it.hasNext();){
						final FrequentFragment frag=(FrequentFragment) it.next();
						if (frag.getFragment().getNodeCount()==1) --size;
					}
					ff=new FrequentFragment[size];
					int i=-1;
					for (Iterator it=frags.iterator();it.hasNext();){
						final FrequentFragment frag=(FrequentFragment) it.next();
						if (frag.getFragment().getNodeCount()>1){
							ff[++i]=frag;
//							if (SimpleGraphComparator.instance.compare(detect,frag.getFragment())==0) 
//							System.err.println("Fragment "+(i+1));
						}
					}

					}catch(Exception e){
						me.actionPerformed(new ActionEvent(e,0,"EXCEPTION"));
					}catch(Error e){
						me.actionPerformed(new ActionEvent(e,0,"ERROR"));
					}

					me.actionPerformed(new ActionEvent(this,0,"search done"));
				}
			};
			t.start();
			
		}else if (command.equals("comboBoxChanged")){
			if (event.getSource()==fragmentBox) {
				int index=fragmentBox.getSelectedIndex();
				if (index>=0 && index<ff.length) {
					final FrequentFragment frag=ff[index];
					graphs=frag.getSupportedGraphs();
					if (index!=lastFF){ 
						graphBox.removeAllItems();
						for (int i=0;i<graphs.length;++i) graphBox.addItem(graphs[i].getName());
						lastFF=index;
					}
					showGraph(frag.getFragment());
				}
			} else {
				int index=graphBox.getSelectedIndex();
				if (lastFF!=-1 && graphs!=null && index>=0 && index<graphs.length) {
					showGraph(graphs[index]);
				}
			}
		}else if (command.equals("running")){
			start.setEnabled(false);
			graphBox.setEnabled(false);
			fragmentBox.setEnabled(false);
			start.setText("running");
			graphs=null;
			ff=null;
			lastFF=-1;
			graphBox.removeAllItems();
			fragmentBox.removeAllItems();
			showObject(null);
		}else if (command.equals("search done")){
			if (ff!=null){
				for (int i=0;i<ff.length;++i) fragmentBox.addItem("Fragment "+(i+1));
				fragmentBox.setSelectedIndex(0);
			}
			start.setText("start search");
			fragmentBox.setEnabled(true);
			graphBox.setEnabled(true);
			start.setEnabled(true);
		}else if (command.equals("EXCEPTION")){
			System.err.println("exception detected: "+event.getSource());
		}else if (command.equals("ERROR")){
			System.err.println("error detected: "+event.getSource());
		}else{ System.err.println("Unkown Event: "+command); }
	}
	
	
	private final ColorSchema schema=new ColorSchema("src/de/parmol/visualization/colorschema.props");
	
	private final void showObject(final JComponent p){
		chemPane.removeAll();//.remove(lastDemo);
		if (p!=null) {
			p.setSize(chemPane.getSize());
			chemPane.add(p);
		}
		chemPane.repaint();
	}
	private void showGraph(Graph g){
		if (g instanceof Matrix) g=new UndirectedListGraph((Matrix)g);
		showObject(new de.parmol.visualization.Demo(new DataAnalyser(g),schema,false, false));
	}
	
    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
	public void run(){
		System.out.close();
        //Make sure we have nice window decorations.
		JFrame.setDefaultLookAndFeelDecorated(true);
		//Create and set up the window.
		JFrame frame = new JFrame("ParMol - Paralelle Molecular mining");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.getContentPane().add(createPane(), BorderLayout.CENTER);
		frame.setSize(550,250);
		
		//Display the window.
		frame.setVisible(true);
		
    }


	/**
	 * start a new Demo
	 * @param args
	 */
    public static void main(String[] args){
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Demo());
	}

}
