package BugLocator;

import java.util.ArrayList;
import java.util.HashMap;

import utility.ContentLoader;
import utility.MiscUtility;



public class BLperformanceCalculator {

	//public static HashMap<String, ArrayList<String>> TrueSetResults;
	public static HashMap<String, ArrayList<String>> BLresults;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		BLperformanceCalculator obj=new BLperformanceCalculator();
		//String filePath="./data/Results/100-1000-rankedResult.txt";
		String filePath="/Users/user/Downloads/buglocator1/output/eclipseoutput.txt";
		obj.ComputeBugLocatorPerformance(filePath);
		MiscUtility.showResult(10, BLresults);
		System.out.println("No of Bug: "+BLresults.size());
		for(String BugID:BLresults.keySet())
		{
			ArrayList<String> resultList=BLresults.get(BugID);
			System.out.println(resultList.size());
		} 
	}

	public BLperformanceCalculator()
	{
		this.BLresults=new HashMap<>();
	}
	
	public void ComputeBugLocatorPerformance(String resultFromBugLocator)
	{
		this.readBugLocatorResult(resultFromBugLocator);
		this.CalculateMAP();
	}
	
	public void CalculateMAP()
	{
		Double AP=0.0;
		
		for(String BugID:BLresults.keySet())
		{
			double AP_i=0.0;
			ArrayList<String> resultList=BLresults.get(BugID);
			for(int i=0;i<resultList.size();i++)
			{
			
				String[] spilter=resultList.get(i).split(",");
				int rank=Integer.valueOf(spilter[1]);
				Double upperpart=Double.valueOf(i)+1;
				Double lowerPart=Double.valueOf(rank)+1;
				AP_i+=upperpart/lowerPart;
				
			}
			AP+=AP_i/resultList.size();
		}
		
		Double MAP=AP/BLresults.size();
		System.out.println("MAP: "+MAP+" BLresults.size(): "+BLresults.size());
	}
	
	
	public void CalculateMRR()
	{
		
	}
	
	public static void readBugLocatorResult(String filePath)
	{
		ArrayList<String> resultList=ContentLoader.getAllLinesList(filePath);
		int count=0;
		for(int i=0;i<resultList.size();i++)
		{
			String line=resultList.get(i);
			String [] spilter=line.split(",");
			String bugID=spilter[0];
	        String file=spilter[1];
	        int rank=Integer.valueOf(spilter[2]);
	        Double RVSMscore=Double.valueOf(spilter[3]);
	       
	        //if(rank<10) 
	        {
	        	count++;
	        	System.out.println(count+" "+bugID+" "+file+" "+rank+" "+RVSMscore);
	        	if(BLresults.containsKey(bugID))
	        	{
	        		ArrayList<String> tempList=BLresults.get(bugID);
	        		tempList.add(file+","+rank+","+RVSMscore);
	        		BLresults.put(bugID, tempList);
	        	}
	        	else
	        	{
	        		ArrayList<String> tempList=new ArrayList<>();
	        		tempList.add(file+","+rank+","+RVSMscore);
	        		BLresults.put(bugID, tempList);
	        	}
	        }

		}
	
	}
}
