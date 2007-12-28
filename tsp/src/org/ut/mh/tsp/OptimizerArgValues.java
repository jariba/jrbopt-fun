package org.ut.mh.tsp;

import java.util.*;

public class OptimizerArgValues 
    extends HashMap
{
    public int getInt(String name)
    {
    	return new Integer((String)get(name)).intValue();
    }
    
    public double getDouble(String name)
    {
    	return new Double((String)get(name)).doubleValue();
    }
}
