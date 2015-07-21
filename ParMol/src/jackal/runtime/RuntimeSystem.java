/*
 * Created on Aug 2, 2004
 *
 * Copyright 2004 Thorsten Meinl
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
package jackal.runtime;

/**
 * This class is only a dummy class so that the Eclipse compiler can built
 * projects that use the Manta internal functions. The only work as expected, if
 * the classes are built with the Manta compiler.
 * 
 * @author Thorsten Meinl <Thorsten.Meinl@informatik.uni-erlangen.de>
 */
public class RuntimeSystem {
	/**
	 * Sets the target node on which a new thread should be allocated
	 * 
	 * @param target
	 *          the number of the node
	 */
	public static void setTarget(int target) {
	}

	/**
	 * The number of this machine in the cluster, a value between 0 and nrMachines() - 1 
	 * @return the number of this machine in the cluster
	 */
	public static int machineNr() {
		return 0;
	}

	/**
	 * Total number of machines in the cluster
	 * @return the total number of machines in the cluster
	 */
	public static int nrMachines() {
		return 1;
	}

	/**
	 * the total number of cpus over all machines
	 * @return the total number of cpus over all machines
	 */
	public static int total_CPU_Count() {
		return 1;//Runtime.getRuntime().availableProcessors();
	}
	
	/**
	 * Returns the size of the given object in bytes. If recursive is specified the size including all referenced objects is returned.
	 * @param o an Object
	 * @param recursive <code>true</code> if all referenced objects should be visited, too
	 * @return the size in bytes
	 */
	public static int object_size(Object o,  boolean recursive) {
		return -1;
	}
	  
  
  /**
   * Prints statistics about sent objects, call-frequencies, locks, etc.
   */
  public static void java_print_stats() {}

  /**
   * Resets the statistics counters.
   * @param s ???
   */
  public static void java_reset_stats(String s) {}
}
