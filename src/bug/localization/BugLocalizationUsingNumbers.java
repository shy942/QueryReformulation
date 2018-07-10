package bug.localization;

import java.util.ArrayList;
import java.util.HashMap;

import utility.ContentLoader;
import utility.ContentWriter;
import utility.MiscUtility;

public class BugLocalizationUsingNumbers {

	public String trainMapAddress;
	public String testSetAddress;
	public String bugIDKeywordMapAddress;
	public String bugLocatorOutput;
	
    public HashMap<Integer, ArrayList<Integer>> trainMap;
    public HashMap<Integer, ArrayList<Integer>> testSet;
    public HashMap<Integer, ArrayList<Integer>> bugIdKeywordMap;
    public HashMap<Integer, ArrayList<String>> bugLocatorOutputMap;
    
    public BugLocalizationUsingNumbers(String trainMapAddress, String testSetAddress, String bugIDKeywordMapAddress, String bugLocatorOutput)
    {
    	this.trainMapAddress=trainMapAddress;
    	this.trainMap= new HashMap<>();
    	this.testSetAddress=testSetAddress;
    	this.testSet= new HashMap<>();
    	this.bugIDKeywordMapAddress=bugIDKeywordMapAddress;
    	this.bugIdKeywordMap=new HashMap<>();
    	this.bugIDKeywordMapAddress=bugLocatorOutput;
    	this.bugLocatorOutputMap=new HashMap<>();
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
    
    public HashMap<Integer, ArrayList<String>> loadHashMapCommaSep(String address)
    {
    	HashMap<Integer, ArrayList<String>> temp=new HashMap<>();
        ArrayList<String> lines=ContentLoader.getAllLinesList(address);
        for(String line:lines)
        {
        	//System.out.println(line);
        	String[] spilter=line.split(",");
        	int id=Integer.valueOf(spilter[0]);
        	int Sid=Integer.valueOf(spilter[1]);
        	int rank=Integer.valueOf(spilter[2]);
        	Double score=Double.valueOf(spilter[3]);
        	
        	if(temp.containsKey(id))
        	{
        		ArrayList<String> tempList=temp.get(id);
        		tempList.add(Sid+","+rank+","+score);
        		temp.put(id, tempList);
        	}
        	else
        	{
        		ArrayList<String> tempList=new ArrayList<>();
        		tempList.add(Sid+","+rank+","+score);
        		temp.put(id, tempList);
        	}
        	
        }
        return temp;
    }
    
    
    public void bugLocator(BugLocalizationUsingNumbers obj)
    {
    	
    	obj.trainMap=obj.loadHashMap(obj.trainMapAddress);
		obj.testSet=obj.loadHashMap(obj.testSetAddress);
		obj.bugIdKeywordMap=obj.loadHashMap(obj.bugIDKeywordMapAddress);
		obj.bugLocatorOutputMap=obj.loadHashMapCommaSep(obj.bugLocatorOutput);
		MiscUtility.showResult(10, obj.bugLocatorOutputMap);
		
		/*
		for(int queryID:testSet.keySet())
		{
			obj.findBugForEachQuery(queryID);
		}*/
		//do this once
		//obj.convertInputFile("./data/buglocator/eclipseoutput.txt", "./data/changeset-pointer/ID-SourceFile.txt");
    }
    
    public void findRVSMscore(String queryID)
    {
    	
    }
    
    public void convertInputFile(String inputfileAddress, String IDaddress)
    {
    	ArrayList<String> lines=ContentLoader.getAllLinesList(IDaddress);
    	HashMap<String, Integer> IDmap=new HashMap<>();
    	for(String line:lines)
    	{
    		String[] spilter=line.split(":");
    		String sourceID=spilter[0];
    		String sourceFile=spilter[1];
    		IDmap.put(sourceFile.trim(), Integer.valueOf(sourceID));
    	}
    	System.out.println(IDmap);
        lines=ContentLoader.getAllLinesList(inputfileAddress);
        ArrayList<String> saveContent=new ArrayList<>();
        for(String line:lines)
        {
        	String[] spilter=line.split(",");
        	String fileaddress=spilter[1];
        	System.out.println(fileaddress);
        	System.out.println(IDmap.get(fileaddress.trim()));
        	if(IDmap.containsKey(fileaddress))
        	{
        		int Sid=IDmap.get(fileaddress);
        		saveContent.add(spilter[0]+","+Sid+","+spilter[2]+","+spilter[3]);
        	}
        }
        ContentWriter.writeContent("./data/buglocator/eclipseOutputInNumbers.txt", saveContent);
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
		BugLocalizationUsingNumbers obj=new BugLocalizationUsingNumbers("./data/FinalMap/TokenSourceMapTrainset1.txt", "./data/testset/test1.txt","./data/Bug-ID-Keyword-ID-Mapping.txt",".data/buglocator/eclipseOutputInNumbers.txt");
		obj.bugLocator(obj);
		
		
		//call the bug localizer
	}

	
	
}
