package org.ut.mh.tsp.ga;

import java.util.List;
import org.ut.mh.tsp.TSPSolution;


/**
 */
public interface GAProblemMapper 
{
    public GAPopulation getInitialPopulation(List cities);
    public TSPSolution makeNewMember(GAPopulation population);
    public void deleteMember(GAPopulation population);
}
