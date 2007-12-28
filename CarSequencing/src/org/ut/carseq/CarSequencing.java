package org.ut.carseq;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

public class CarSequencing 
{
	public static void main(String[] args) 
	{
		CarSequencingUI.run();
	}

	static DescCarSequencingProblem readProblem(String filename)
	{
	    try {
	        BufferedReader in  = new BufferedReader(new FileReader(filename));
	        String line = null;

	        line = in.readLine();
	        StringTokenizer tok = new StringTokenizer(line," \t");
	        // read number of cars; number of options; number of classes
            int nCars = getTokenAsInteger(tok);
            int nOptions = getTokenAsInteger(tok);
            int nClasses = getTokenAsInteger(tok);

            DescCarSequencingProblem problem = new DescCarSequencingProblem(
            		nCars,
            		nOptions,
            		nClasses);
            
            // For each option, read X of the X in Y constraint
	        line = in.readLine();
	        tok = new StringTokenizer(line," \t");
	        for (int i=0;i<nOptions;i++) {
	        	int maxCars = getTokenAsInteger(tok);
	        	problem.options_[i].maxCars = maxCars;
	        }
            
            // For each option, read Y of the X in Y constraint
	        line = in.readLine();
	        tok = new StringTokenizer(line," \t");
	        for (int i=0;i<nOptions;i++) {
	        	int blockSize = getTokenAsInteger(tok);
	        	problem.options_[i].blockSize = blockSize;
	        }
	        
	        // For each car class, read what options are included
	        // and how many cars in class
	        for (int i=0;i<nClasses;i++) {
		        line = in.readLine();
	            tok = new StringTokenizer(line," \t");
	            
	            int id = getTokenAsInteger(tok);
	            int nCarsInClass = getTokenAsInteger(tok);
	            
	            DescCarClass cc = new DescCarClass(id,nCarsInClass,nOptions);
	            problem.carClasses_[i] = cc;
	            for (int j=0;j<nOptions;j++) {
	            	int optionRequired = getTokenAsInteger(tok);
	            	cc.requiredOpts[j] = optionRequired;
	            }
            }
	        
	        return problem;
        } 
        catch (Exception e) {
	        throw new RuntimeException(e);
        } 		
	}
	
    static CarSequence problemToSequence(DescCarSequencingProblem problem)
    {
    	Map<Integer,Integer> optionCnt = new HashMap<Integer,Integer>();

    	int nSlots = problem.nCars_;
    	Vector<CEHubXinY> xINyHubs = new Vector<CEHubXinY>();
    	for (int i=0;i<problem.nOptions_;i++) {
    		CEHubXinY hub = new CEHubXinY(nSlots);
    		hub.x_ = problem.options_[i].maxCars;
    		hub.y_ = problem.options_[i].blockSize;
    		xINyHubs.add(hub);
    		optionCnt.put(i,0);
    	}
    	
    	CarSequence retval = new CarSequence(xINyHubs);
    	
    	for (DescCarClass classDesc : problem.carClasses_) {
            for (int i=0;i<classDesc.requiredOpts.length;i++) {
            	int cnt = optionCnt.get(i);
            	cnt += classDesc.requiredOpts[i] * classDesc.nCars;
            	optionCnt.put(i,cnt);
            }
    	}
    	 
    	int maxOptionIdx = 0;
    	int maxOptionCnt = 0;
    	Iterator<Map.Entry<Integer,Integer>> it = 
    		optionCnt.entrySet().iterator();
    	while (it.hasNext()) {
    		Map.Entry<Integer,Integer> entry = it.next();
    		if (entry.getValue() > maxOptionCnt) {
    			maxOptionCnt = entry.getValue();
    			maxOptionIdx = entry.getKey();
    		}
    	}
    	
    	// Penalize most used option higher :
    	xINyHubs.get(maxOptionIdx).setConflict(3.0);
    	
    	for (DescCarClass classDesc : problem.carClasses_) {
    		 for (int i=0; i<classDesc.nCars; i++) {
    		     Car c = new Car(classDesc,xINyHubs);
    		     retval.addCar(c);
    		 }
    	}
    	
    	return retval;
    }
    
    static Integer getTokenAsInteger(StringTokenizer tok)
    {
	    return new Integer(tok.nextToken());
    }    
}
