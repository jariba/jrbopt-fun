package org.ut.carseq;

public class CEHubXinY 
{
	static int idCnt=0;
	
	int id_;
    int x_;
	int y_;
	int capacity_[];
	double conflict_;
	
	public CEHubXinY(int slotCnt)
	{
		id_ = idCnt++;
		
	    capacity_ = new int[slotCnt];
	    
	    for (int i=0;i<slotCnt; i++) {
	    	capacity_[i] = 1;
	    }
	    
	    conflict_ = 1.0;
	}
	
	public double getConflict() { return conflict_; }
	public void setConflict(double c) { conflict_ = c; }
	
	public int getId() { return id_; }
	
	public void useCapacity(int idx)
	{
		capacity_[idx]--;
	}
	
	public void freeCapacity(int idx)
	{
		capacity_[idx]++;
	}	
	
	public boolean isInViolation(int idx)
	{
		if (capacity_[idx]>0) {
			return false;
		}
		else {
			int lb = Math.max(0, idx-(y_-1));
			int ub = Math.min(capacity_.length-1,idx+(y_-1));

			int cnt = 0;  
			for (int i=lb;i<=ub;i++) {
				if (capacity_[i]==0)
					cnt++;
				else
					cnt=0;				
				
				if (cnt > x_)
					return true;
			}
			
			
			/*
			int lb = idx-(y_-1);
			if (lb >= 0) { 
				int cnt = 0;  
				for (int i=lb;i<=idx;i++) 
					cnt += 1-capacity_[i];
				if (cnt > x_)
					return true;
			}

			int ub = idx+(y_-1);
			if (ub < capacity_.length) {
				int cnt = 0;  
				for (int i=idx;i<=ub;i++) 
					cnt += 1-capacity_[i];
				if (cnt > x_)
					return true;
			}
            */
			return false;
		}
	}
}
