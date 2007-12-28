package org.ut.util;

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

		buf.append(msg).append(listToString(l,separator));
		Log.debug(buf.toString());
	}	
	
	public static String listToString(List l, String separator)
	{
		StringBuffer buf = new StringBuffer();

		buf.append("{");
		for (int i=0;i<l.size();i++) {
			if (i>0)
				buf.append(separator);
			buf.append(l.get(i).toString());
		}	    
		buf.append("}");
		return buf.toString();		
	}

	public static void swap(List l,int a,int b)
	{
		Object tmp = l.get(a);
		l.set(a,l.get(b));
		l.set(b,tmp);
	}	
}
