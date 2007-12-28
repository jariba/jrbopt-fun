package org.ut.mh.tsp.grasp;
import java.util.List;

/*
 * Created on Feb 7, 2005
 *
 */

/**
 * @author Javier
 *
 */
public interface CandidateBuilder 
{
	/*
	 * Generates a candidate Solution using  a 
	 * Greedy Myopic Measure of Goodness
	 * GMMOG 
	 */
	public List getCandidate(List cities);
}
