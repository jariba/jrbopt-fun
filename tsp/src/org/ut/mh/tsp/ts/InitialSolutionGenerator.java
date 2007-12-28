/*
 * Created on Apr 12, 2005
 *
 */
package org.ut.mh.tsp.ts;

import java.util.List;
import org.ut.mh.tsp.TSPSolution;

/**
 * @author Javier
 */
public interface InitialSolutionGenerator 
{
	public TSPSolution getInitialSolution(List cities);
}
