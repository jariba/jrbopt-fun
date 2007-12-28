package org.ut.mh.tsp.util;

import java.util.*;

public class Util 
{
	/*
	 * randomly picks a number between 0 and n-1
	 */
	public static int randomPick(int n)
	{
		if (n<=0)
			return 0;
		
		return (int)Math.round(Math.random()*(n-1));
	}	
	
	public static void printList(String msg,List l,String separator)
	{
		StringBuffer buf = new StringBuffer();

		buf.append(msg).append("{");
		for (int i=0;i<l.size();i++) {
			if (i>0)
				buf.append(separator);
			buf.append(l.get(i).toString());
		}	    
		buf.append("}");
		Log.debug(buf.toString());
	}	

	public static void swap(List cities,int a,int b)
	{
		Object tmp = cities.get(a);
		cities.set(a,cities.get(b));
		cities.set(b,tmp);
	}	
}
