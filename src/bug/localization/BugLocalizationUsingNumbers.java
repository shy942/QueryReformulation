package bug.localization;

import java.util.ArrayList;
import java.util.HashMap;

import utility.ContentLoader;
import utility.MiscUtility;

public class BugLocalizationUsingNumbers {

	public String trainMapAddress;
	public String testSetAddress;
	public String bugIDKeywordMapAddress;
	
    public HashMap<Integer, ArrayList<Integer>> trainMap;
    public HashMap<Integer, ArrayList<Integer>> testSet;
    public HashMap<Integer, ArrayList<Integer>> bugIdKeywordMap;
    
    public BugLocalizationUsingNumbers(String trainMapAddress, String testSetAddress, String bugIDKeywordMapAddress)
    {
    	this.trainMapAddress=trainMapAddress;
    	this.trainMap= new HashMap<>();
    	this.testSetAddress=testSetAddress;
    	this.testSet= new HashMap<>();
    	this.bugIDKeywordMapAddress=bugIDKeywordMapAddress;
    	this.bugIdKeywordMap=new HashMap<>();
    }
    
	
    public HashMap<Integer, ArrayList<Integer>> loadHashMap(String address)
    {
    	HashMap<Integer, ArrayList<Integer>> temp=new HashMap<>();
        ArrayList<String> lines=ContentLoader.getAllLinesList(address);
        for(String line:lines)
        {
        	//System.out.println(line);
        	String[] spilter=line.split(":");
        	if(spilter.length>1)
        	{
        	int id=Integer.valueOf(spilter[0]);
        	String[] mapedID=spilter[1].split(" ");
        	
        	ArrayList<Integer> list=new ArrayList<>();
        	for(String content:mapedID)
        	{
        		int value=Integer.valueOf(content);
        		list.add(value);
        	}
        	temp.put(id, list);
        	}
        	
        }
        return temp;
    }
    
    
    public void bugLocator(BugLocalizationUsingNumbers obj)
    {
    	obj.trainMap=obj.loadHashMap(obj.trainMapAddress);
		obj.testSet=obj.loadHashMap(obj.testSetAddress);
		obj.bugIdKeywordMap=obj.loadHashMap(obj.bugIDKeywordMapAddress);
		//MiscUtility.showResult(10, obj.bugIdKeywordMap);
		
		for(int queryID:testSet.keySet())
		{
			obj.findBugForEachQuery(queryID);
		}
    }
    
    
    public void findBugForEachQuery(int queryID)
    {
    	ArrayList<Integer> keywordList=this.bugIdKeywordMap.get(queryID);
    	
    	HashMap<Integer,Integer> tempResultMap=new HashMap();
    	for(int keyword:keywordList)
    	{
    		if(this.trainMap.containsKey(keyword))
    		{
    			ArrayList<Integer> tempList=trainMap.get(keyword);
    		    for(int source:tempList)
    		    {
    		    	if(tempResultMap.containsKey(source))
    		    	{
    		    		int count=tempResultMap.get(source);
    		    		count+=1;
    		    		tempResultMap.put(source, count);
    		    	}
    		    	else
    		    	{
    		    		tempResultMap.put(source, 1);
    		    	}
    		    }
    		}
    	}
    	
    	
    	HashMap<Integer,Integer> sortedHashMap=MiscUtility.sortByValues(tempResultMap);
    	System.out.println(queryID);
    	System.out.println(sortedHashMap);
    }
    
    
    
	public static void main(String[] args) {
		// TODO Auto-generated method stub
        
		//Work on necessary inputs or maps
		BugLocalizationUsingNumbers obj=new BugLocalizationUsingNumbers("./data/FinalMap/TokenSourceMapTrainset1.txt", "./data/testset/test1.txt","./data/Bug-ID-Keyword-ID-Mapping.txt");
		obj.bugLocator(obj);
		
		
		//call the bug localizer
	}

	
	
}
