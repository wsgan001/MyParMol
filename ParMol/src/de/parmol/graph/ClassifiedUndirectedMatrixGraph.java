/*
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
package de.parmol.graph;


/**
 * This class extends Matrix Graph with information for class frequencies. Each graph can be part of one or more classes to some degree between 0 and 1.
 * 
 * @author Thorsten Meinl <Thorsten.Meinl@informatik.uni-erlangen.de>
 */
public class ClassifiedUndirectedMatrixGraph extends UndirectedMatrixGraph implements ClassifiedGraph {
	protected float[] m_classFrequencies = { 1 };

	/**
	 * Creates a new empty ClassifiedUndirectedMatrixGraph with a default frequency of 1. 
	 */
	public ClassifiedUndirectedMatrixGraph() {
		super();
	}

	/**
	 * Creates a new empty ClassifiedUndirectedMatrixGraph with the given class frequencies.
	 * @param classFrequencies the frequencies of this graph in the classes 
	 */
	public ClassifiedUndirectedMatrixGraph(float[] classFrequencies) {
		super();
		m_classFrequencies = classFrequencies;
	}

	/**
	 * Creates a new empty ClassifiedUndirectedMatrixGraph with the given id and class frequencies.
	 * @param id the id of the new graph
	 * @param classFrequencies the frequencies of this graph in the classes

	 */
	public ClassifiedUndirectedMatrixGraph(String id, float[] classFrequencies) {
		super(id);
		m_classFrequencies = classFrequencies;
	}

	/**
	 * Creates a new empty ClassifiedUndirectedMatrixGraph that is a copy of the given template graph (except for the id).
	 * @param template the graph to be copied
	 */
	public ClassifiedUndirectedMatrixGraph(ClassifiedUndirectedMatrixGraph template) {
		super(template);
		m_classFrequencies = template.m_classFrequencies;
	}

	/**
	 * Creates a new empty ClassifiedUndirectedMatrixGraph that is a copy of the given template graph (except for the id).
	 * @param template the graph to be copied
	 */
	public ClassifiedUndirectedMatrixGraph(ClassifiedGraph template) {
		super((UndirectedGraph) template);
		m_classFrequencies = template.getClassFrequencies();
	}

	/* (non-Javadoc)
	 * @see de.parmol.graph.ClassifiedGraph#getClassFrequencies()
	 */
	public float[] getClassFrequencies() { return m_classFrequencies;	}

	/* (non-Javadoc)
	 * @see de.parmol.graph.ClassifiedGraph#setClassFrequencies(float[])
	 */
	public void setClassFrequencies(float[] frequencies) {
		m_classFrequencies = frequencies;
	}

	/**
	 * This class is a factory for ClassifiedMatrixGraphs.
	 * 
	 * @author Thorsten.Meinl@informatik.uni-erlangen.de
	 *
	 */
	public static class Factory extends ClassifiedGraphFactory {
		/**
		 * The single instance for this factory.
		 */		
		public final static Factory instance = new Factory();
		protected final static float[] DEFAULT_CLASS = { 1 };
		
		protected Factory() { super(UNDIRECTED_GRAPH | MATRIX_GRAPH | CLASSIFIED_GRAPH); }

		/* (non-Javadoc)
		 * @see de.parmol.graph.ClassifiedGraphFactory#createGraph(float[])
		 */
		public ClassifiedGraph createGraph(float[] classFrequencies) {
			return new ClassifiedUndirectedMatrixGraph(classFrequencies);
		}

		/* (non-Javadoc)
		 * @see de.parmol.graph.ClassifiedGraphFactory#createGraph(java.lang.String, float[])
		 */
		public ClassifiedGraph createGraph(String id, float[] classFrequencies) {
			if (id != null) return new ClassifiedUndirectedMatrixGraph(id, classFrequencies);
			return new ClassifiedUndirectedMatrixGraph(classFrequencies);
		}

		/* (non-Javadoc)
		 * @see de.parmol.graph.GraphFactory#createGraph()
		 */
		public MutableGraph createGraph() {
			return new ClassifiedUndirectedMatrixGraph(DEFAULT_CLASS);
		}

		/* (non-Javadoc)
		 * @see de.parmol.graph.GraphFactory#createGraph(java.lang.String)
		 */
		public MutableGraph createGraph(String id) {
			if (id != null) return new ClassifiedUndirectedMatrixGraph(id, DEFAULT_CLASS);
			return new ClassifiedUndirectedMatrixGraph(DEFAULT_CLASS);
		}
	}	
}

