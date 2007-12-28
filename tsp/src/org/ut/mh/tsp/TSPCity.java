package org.ut.mh.tsp;
/**
 *  A city in the TSP tour
 */

public class TSPCity 
{
    public Integer id;
    // Customer Location
    public Integer x;
    public Integer y;
    // Time window within which the customer can be visited
    public Integer minTime;
    public Integer maxTime;
    public Integer visitTime; // actualTime that customer is visited
    
    public TSPCity(Integer theId, 
    		       Integer theX,
				   Integer theY,
				   Integer mint,
				   Integer maxt)
    {
	    id = theId;
	    x = theX;
	    y = theY;
	    minTime=mint;
	    maxTime=maxt;
	    visitTime = new Integer(Integer.MAX_VALUE);
    }
    
    public String toString()
    {
    	StringBuffer buf = new StringBuffer();
    	buf.append(id).append(" (").append(x).append(",").append(y).append(")");
    	if (minTime!=null) {
    		buf.append(" ").append(minTime).append(" ").append(maxTime);
    	}
    	return buf.toString();
    }
    
    public double distance(TSPCity b)
    {
    	return distance(this,b);
    }
    
    static public double distance(TSPCity a,TSPCity b)
    {
	    if (a==null || b==null)
	        return 0;
	        
	    return Math.sqrt(
	             sqr(a.x.intValue()-b.x.intValue()) +
	             sqr(a.y.intValue()-b.y.intValue())
	           );    
    }
    
    static double sqr(double x)
    {
	    return (x*x);
    }
}
