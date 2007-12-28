package org.ut.mh.tsp;

import java.util.*;
import java.io.*;
import org.ut.mh.tsp.util.SimpleTimer;
import org.ut.mh.tsp.util.Log;


/**
 * @author Javier
 */
public abstract class OptimizerBase
    implements Optimizer
{
	protected String name_;
	protected List listeners_ = new Vector();
	protected SolutionInfo bestSolutionInfo_;
	protected SimpleTimer solutionTimer_; // TODO: this is not a good place for this
	
    public OptimizerBase(String name)
    {
    	name_=name;
    	listeners_ = new Vector();
    }
    
    public String getName() { return name_; }
    
    public void addOptimizerListener(OptimizerListener o)
    {
    	listeners_.add(o);
    }
    
    public void removeOptimizerListener(OptimizerListener o)
    {
    	listeners_.remove(o);    	
    }
    
    protected void notifyNewSolution(int iteration,TSPSolution solution)
    {
    	long elapsedTime = solutionTimer_.getElapsed();
    	for (int i=0;i<listeners_.size();i++) {
    		OptimizerListener listener = (OptimizerListener)listeners_.get(i);
    		listener.newSolutionFound(iteration,solution);
    	}
    	logNewSolution(iteration,elapsedTime,solution);
    	bestSolutionInfo_ = new SolutionInfo(iteration,elapsedTime,solution);
    }

    protected void notifyIterationCompleted(int n)	
    {
    	for (int i=0;i<listeners_.size();i++) {
    		OptimizerListener listener = (OptimizerListener)listeners_.get(i);
    		listener.iterationCompleted(n);
    	}
    } 
    
    protected void logNewSolution(int iteration,long elapsedTime,TSPSolution solution)
    {
    	try {
    		TimeWindowInfo twi = getTWInfo(solution);
    		String msg = "Improving Solution Found at (iteration,time in msecs, total lateness, lateness count, travel time) "+
  			             iteration+" "+
			             elapsedTime+" "+
						 twi.totLateness+" "+
						 twi.latenessCnt+" "+
			             solution;
    		Log.debug(msg);
    		msg+="\n"+twi.txt;
    		
    		PrintWriter out = new PrintWriter(new BufferedWriter(
    				new FileWriter("output/"+getName()+"-delta.txt",true)));
    		out.println(msg);
    		out.close();
    	}
    	catch (Exception e) {
    		throw new RuntimeException(e);
    	}
    }
    
    protected void logBestSolution()
    {
    	try {
    		TimeWindowInfo twi = getTWInfo(bestSolutionInfo_.solution);
    		String msg="Best Solution Found at (iteration,time in msecs, total lateness, lateness count, travel time) "+
  			    bestSolutionInfo_.iteration+" "+
			    bestSolutionInfo_.msecs+" "+
			    twi.totLateness+" "+
			    twi.latenessCnt+" "+
			    bestSolutionInfo_.solution;
    		
    		Log.debug(msg);
    		PrintWriter out = new PrintWriter(new BufferedWriter(
    				new FileWriter("output/"+getName()+"-delta.txt",true)));
    		out.println(msg+"\n");
    		out.close();
    		
    		out = new PrintWriter(new BufferedWriter(
    				new FileWriter("output/"+getName()+"-best.txt",true)));
    		out.println(msg);
    		out.close();
    	}
    	catch (Exception e) {
    		throw new RuntimeException(e);
    	}
    }
    
    protected void resetBestSolution()
    {
    	bestSolutionInfo_=null;
    	solutionTimer_=new SimpleTimer();  
    	solutionTimer_.start();
    }

    protected TimeWindowInfo getTWInfo(TSPSolution s)
    {
    	TimeWindowInfo twi = new TimeWindowInfo();
    	twi.latenessCnt=0;
    	twi.totLateness=0;
    	twi.txt="";
    	
        for (int i=0;i<s.getTourCnt();i++) {
        	TimeWindowInfo childTWI = getTWInfo(s.getTour(i));
        	twi.latenessCnt+=childTWI.latenessCnt;
        	twi.totLateness+=childTWI.totLateness;
        	twi.txt+=childTWI.txt;
        }
        
        s.latenessCnt = twi.latenessCnt;
        s.totalLateness = (int)twi.totLateness;
        
        return twi;
    }
    
	protected TimeWindowInfo getTWInfo(List tour)
	{
		StringBuffer buf = new StringBuffer();
		
		buf.append("City Coords MinTW MaxTW ServiceTime Wait Lateness TravelTime\n");
		double totLateness=0;
		int latenessCnt=0;
		double t=0;
		TSPCity lastCity = (TSPCity)tour.get(0);
		for (int i=0;i<tour.size();i++) {
			TSPCity c = (TSPCity)tour.get(i);
			t += lastCity.distance(c);
			double wait = Math.max(0,c.minTime.intValue()-t);
			c.visitTime = new Integer((int)t);
			double lateness = Math.max(0,t-c.maxTime.intValue());
			if (lateness>0) {
				latenessCnt++;
				totLateness+=lateness;
			}
			t = Math.max(t,c.minTime.intValue());			
			buf.append(c+" "+" "+(int)t+" "+(int)wait+" "+(int)lateness+" "+lastCity.distance(c)+"\n");
			lastCity=c;
		}			
		buf.append("Late Visits:"+latenessCnt+"\n");
		buf.append("Total Lateness:"+totLateness+"\n");
		TimeWindowInfo retval= new TimeWindowInfo();
		retval.totLateness=totLateness;
		retval.latenessCnt=latenessCnt;
		retval.txt = buf.toString();
        return retval;
	}    

	private static class TimeWindowInfo
	{
	    public double totLateness;
	    public int latenessCnt;
	    public String txt;
	}
	
    protected static class SolutionInfo
	{
    	public int iteration;
    	public long msecs;
    	public TSPSolution solution;
    	
    	public SolutionInfo(int it,long t, TSPSolution s)
    	{
    		iteration = it;
    		msecs = t;
    		solution =s;
    	}
	}
}
