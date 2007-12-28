package org.ut.mh.tsp.ga;

import java.util.List;
import org.ut.mh.tsp.TSPSolution;

/**
 */
public interface GAPopulation 
{
    public TSPSolution getBestMember();
    public boolean addMember(TSPSolution m);
    public boolean deleteMember(TSPSolution m);
    public List getParentCandidates(int cnt);
    public List getDeleteCandidates(int cnt);
}
