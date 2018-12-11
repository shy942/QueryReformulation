package performance.calculator;

import java.util.*;
import java.util.HashMap;

import utility.ContentLoader;

public class RankComparison {

	int totalBug;
	int totalImprovement;
	int totalWorsen;
	int totalPreserve;
	ArrayList<Long> improveList=new ArrayList<>();
	ArrayList<Long> worsenList=new ArrayList<>();
	public RankComparison()
	{
		this.totalBug=0;
		this.totalImprovement=0;
		this.totalWorsen=0;
		this.totalPreserve=0;
		this.improveList=new ArrayList<>();
		this.worsenList=new ArrayList<>();
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String basePart="E:\\PhD\\EclipseAll";
		RankComparison obj=new RankComparison();
		for(int i=1;i<=10;i++)
		{
		
			HashMap<String, Long> baselineMap=obj.LoadRankMap(basePart+"\\data\\"+"bestRankVSM1000"+i+".txt");
			HashMap<String, Long> BLuAMIRmap=obj.LoadRankMap(basePart+"\\data\\"+"bestRankVSMAndme1000"+i+".txt");
			//System.out.println(baselineMap.size());
			//System.out.println(BLuAMIRmap.size());
			obj.RanKComp(obj,baselineMap, BLuAMIRmap);
		}
		System.out.println("Total: "+obj.totalBug+" Improvement: "+obj.totalImprovement+" Worsen: "+obj.totalWorsen+" Preserve: "+obj.totalPreserve);
		System.out.println("Improvement Mean: "+FindMean(obj.improveList)+" min: "+FindMin(obj.improveList)+" max: "+FindMax(obj.improveList));
		System.out.print("Worsen Mean: "+FindMean(obj.worsenList)+" min: "+FindMin(obj.improveList)+" max: "+FindMax(obj.worsenList));
	}

	public static double FindMean(ArrayList<Long> list)
	{
		double mean=0;
		long sum=0;
		for(long key:list)
		{
			sum+=key;
		}
		mean=Double.valueOf(sum)/Double.valueOf(list.size());
		return mean;
	}
	
	public static long FindMin(ArrayList<Long> list)
	{
		long min=1000;
		for(long key:list)
		{
			if(key<min)min=key; 
		}
		return min;
	}
	
	public static long FindMax(ArrayList<Long> list)
	{
		long max=0;
		for(long key:list)
		{
			if(key>max)max=key; 
		}
		return max;
	}
	
	public HashMap<String, Long> LoadRankMap(String filePath)
	{
		HashMap<String, Long> rankMap=new HashMap<>();
		ArrayList<String> list=ContentLoader.getAllLinesList(filePath);
		for(String line:list)
		{
			String[] spilter=line.split(":");
			String bugID=spilter[0];
			Long bestRank=Long.valueOf(spilter[1]);
			rankMap.put(bugID, bestRank);
		}
		return rankMap;
	}
	
	public static void RanKComp(RankComparison obj,HashMap<String, Long> baselineMap, HashMap<String, Long> BLuAMIRmap)
	{
		int improvement=0;
		int worsen=0;
		int preserve=0;
		
		for(String bugID: BLuAMIRmap.keySet())
		{
			if(baselineMap.containsKey(bugID))
			{
				Long baseRank=Long.valueOf(baselineMap.get(bugID));
				Long BLuAMIRrank=Long.valueOf(BLuAMIRmap.get(bugID));
				Long difference=baseRank-BLuAMIRrank;
				//System.out.println(difference);
				if(difference==0)preserve++;
				else if(difference>0) 
					{
						improvement++;
						obj.improveList.add(difference);
					}
				else if(difference<0) 
					{
						worsen++;
						obj.worsenList.add(-difference);
					}
				//System.out.println(bugID+" "+improvement+" "+worsen+" "+preserve);
			}
		}
		int total=improvement+worsen+preserve;
		//System.out.println("Total Bug: "+total);
		//System.out.println("Improvement"+" "+worsen+" "+preserve);
		//System.out.println(improvement+" "+worsen+" "+preserve);
		//System.out.println(Double.valueOf(improvement)/Double.valueOf(total)*100.00+"% ");
		obj.totalBug+=total;
		obj.totalImprovement+=improvement;
		obj.totalWorsen+=worsen;
		obj.totalPreserve+=preserve;
	}
	
}