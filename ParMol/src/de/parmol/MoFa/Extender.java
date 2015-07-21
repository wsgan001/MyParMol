/*
 * Created on 11.06.2005
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
 */
package de.parmol.MoFa;

import java.util.Collection;


/**
 * @author Thorsten Meinl <Thorsten.Meinl@informatik.uni-erlangen.de>
 *
 */
public interface Extender {
	/**
	 * Extends the given embeddings in all possible ways according to the pruning rules.
	 * 
	 * @param emb the embeddings that should be extended
	 * @param lastExtendedNodeIndex the index of the latest extended node in the subgraph
	 * @param lastExtensionWasPerfect indicated if the last extension was a perfect one
	 * @param blackNodes a set of node labels that must not be used for extensions
	 * @param extensions a container for all discovered extensions; it must created with the right parameters
	 */
	public abstract void extend(CompleteMoFaEmbedding emb, int lastExtendedNodeIndex, boolean lastExtensionWasPerfect,
			BlackNodeSet blackNodes, ExtensionContainer extensions);


	/**
	 * Calls freeInstance of the given extension with the right ObjectPool as parameter.
	 * 
	 * @param ext the extension that should be freed
	 */
	public abstract void freeExtension(Extension ext);


	/**
	 * Marks the extension in the given collection reusable.
	 * 
	 * @param extensions a collection with extensions
	 */
	public abstract void freeExtensions(Collection extensions);
}