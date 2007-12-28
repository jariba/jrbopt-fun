package org.ut.mh.tsp.sa;

import java.util.List;
import org.ut.mh.tsp.TSPSolution;


/**
 */
public interface SAProblemMapper 
{
    public double getInitialTemperature();
    public int getIterationsAtTemperature(double temperature);
    public double decreaseTemperature(double temperature);
    public TSPSolution getInitialSolution(List cities);
    public TSPSolution getNextNeighbor(TSPSolution solution);
}
