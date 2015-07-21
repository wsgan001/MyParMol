/*
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
 */

package de.parmol.MoFa;

import de.parmol.graph.Graph;
import de.parmol.util.ObjectPool;

/**
 * This interface describes an extension of an existing embedding
 * 
 * @author Thorsten Meinl <Thorsten.Meinl@informatik.uni-erlangen.de>
 */
public interface Extension {
	/**
	 * Returns the parent embedding, i.e. the embedding that is to be extended
	 * @return a MoFaEmbedding
	 */
  public MoFaEmbedding getParentEmbedding();
  
  /**
   * Returns the extended embedding.
   * @return the extended MoFaEmbedding
   */
  public MoFaEmbedding getExtendedEmbedding();
  
  
  /**
   * Returns the extended embedding by using an already existing embedding object from the given ObjectPool.
   * @param objectPool an ObjectPool with reusable embedding objects
   * @return the extended MoFaEmbedding
   */
  public MoFaEmbedding getExtendedEmbedding(ObjectPool objectPool);
  
  /**
   * Returns the index if the node in the subgraph at which the extension takes place
   * @return the index of the extended node
   */
  public int getExtendedNodeIndex();
  
  /**
   * Recycles this object by putting it into the given object pool.
   * @param objectPool an ObjectPool for embeddings objects
   */
  public void freeInstance(ObjectPool objectPool);
  
  /**
   * Compares two extensions
   * @param ext an extension
   * @return <code>true</code> if the extensions are equal, <code>false</code> otherwise
   */
  public boolean equals(Extension ext);
  
  /**
   * Returns the subgraph represented by this extension.
   * @return the subgraph represents by this extension
   */
  public Graph getSubgraph();
}

