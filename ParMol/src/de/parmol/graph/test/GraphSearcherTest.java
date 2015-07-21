/*
 * Copyright 2004,2005 Thorsten Meinl
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

package de.parmol.graph.test;

import junit.framework.TestCase;
import de.parmol.graph.Graph;
import de.parmol.graph.GraphSearcher;
import de.parmol.util.GraphGenerator;

/**
 * Tests for the GraphSearcher.
 * @author Thorsten Meinl <Thorsten.Meinl@informatik.uni-erlangen.de>
 */
public class GraphSearcherTest extends TestCase {
  public GraphSearcherTest() { super(); }

  public GraphSearcherTest(String name) { super(name); }

  public void testDFS() {
  	GraphGenerator.instance.setConnectedGraph(false);
    Graph g = GraphGenerator.instance.generateGraph(100, 1000);
    
    final int[] visits = new int[g.getNodeCount()];
    
    GraphSearcher searcher = new GraphSearcher() {
      public boolean enteredNode(Graph graph, int node) {
        visits[graph.getNodeIndex(node)]++;
        return true;
      }
    };
    searcher.dfs(g);
    
    for (int i = visits.length - 1; i >= 0; i--) {
      assertEquals(1, visits[i]);
    }    
  }
  
  public void testBFS() {
  	GraphGenerator.instance.setConnectedGraph(false);
    Graph g = GraphGenerator.instance.generateGraph(2000, 20000);
    
    final int[] visits = new int[g.getNodeCount()];
    
    GraphSearcher searcher = new GraphSearcher() {
      public boolean enteredNode(Graph graph, int node) {
        visits[graph.getNodeIndex(node)]++;
        return true;
      }
    };
    searcher.bfs(g);
    
    for (int i = visits.length - 1; i >= 0; i--) {
      assertEquals(1, visits[i]);
    }    
  }  
}

