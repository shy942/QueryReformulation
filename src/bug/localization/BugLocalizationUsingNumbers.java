package bug.localization;

import java.util.ArrayList;
import java.util.Collections;
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
    	this.bugLocatorOutput=bugLocatorOutput;
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
		//MiscUtility.showResult(10, obj.bugLocatorOutputMap);
		ArrayList<String> finalResult=new ArrayList<>();
		ArrayList<String> bugLocatorResultForTestingk=new ArrayList<>();
		for(int queryID:testSet.keySet())
		{
			
			if(this.bugLocatorOutputMap.containsKey(queryID))
			{	
				//HashMap<Integer,Double> resultBugLocator=obj.findRVSMscore(queryID);
				//MiscUtility.showResult(20, resultBugLocator);
				HashMap<Integer,Double> resultMyTool=obj.findBugForEachQuery(queryID);
				//MiscUtility.showResult(20, resultMyTool);
				//HashMap<Integer, Double> resultMap=obj.CombileScoreMaker(queryID,resultBugLocator, resultMyTool);
				String result=queryID+",";
				int count=0;
				//for(int key:resultMap.keySet())
				{
					count++;
					if(count>10)break; 
					//finalResult.add(queryID+","+key+","+resultMap.get(key));
				}
			}
			
			ContentWriter.writeContent("./data/Results/finalResultTest1.txt", finalResult);
		}
		//obj.TestingFileBugLocatorResult("./data/buglocator/test1Result.txt",finalResult);
		//do this once
		//obj.convertInputFile("./data/buglocator/eclipseoutput.txt", "./data/changeset-pointer/ID-SourceFile.txt");
    }
    /*
    public void TestingFileBugLocatorResult(String filepath, ArrayList<String> finalResult)
    {
    	ArrayList<String> list=new ArrayList<>();
    	
    	for(String line:finalResult)
    	{
    		int queryID=Integer.valueOf(line.split(",")[0]);
			if(this.bugLocatorOutputMap.containsKey(queryID))
			{	
				ArrayList<String> tempList=this.bugLocatorOutputMap.get(queryID);
        		for(String temp:tempList)
        		{
        			list.add(queryID+","+temp);
        		}
			}
    	}
			
		
    	ContentWriter.writeContent(filepath, list);
    }*/
    
    public HashMap<Integer, Double> CombileScoreMaker(int queryID, HashMap<Integer,Double> resultBugLocator,HashMap<Integer,Double> resultMyTool)
    {
    	
    	HashMap<Integer, Double> tempCombineResult=new HashMap<>();
    	int count = 0;
    	for(int key:resultMyTool.keySet())
    	{
    		if(resultBugLocator.containsKey(key))
    		{
    			count++;
    			double combineScore=resultMyTool.get(key)+resultBugLocator.get(key);
    			tempCombineResult.put(key, combineScore);
    		}
    		else
    		{
    			tempCombineResult.put(key, resultMyTool.get(key));
    		}
    	}
    	HashMap<Integer, Double> sortedCombineResult=MiscUtility.sortByValues(tempCombineResult);
    	//System.out.println(sortedCombineResult);
    	System.out.println(queryID+" : "+count);
    	return sortedCombineResult;
    }
    
    
    
    public HashMap<Integer,Double> findBugForEachQuery(int queryID)
    {
    	ArrayList<Integer> keywordList=this.bugIdKeywordMap.get(queryID);
    	
    	HashMap<Integer,Double> tempResultMap=new HashMap();
    	for(int keyword:keywordList)
    	{
    		if(this.trainMap.containsKey(keyword))
    		{
    			ArrayList<Integer> tempList=trainMap.get(keyword);
    		    for(int source:tempList)
    		    {
    		    	if(tempResultMap.containsKey(source))
    		    	{
    		    		double count=tempResultMap.get(source);
    		    		count+=1.0;
    		    		tempResultMap.put(source, count);
    		    	}
    		    	else
    		    	{
    		    		tempResultMap.put(source, 1.0);
    		    	}
    		    }
    		}
    	}
    	
    	
    	
    	//Normalize term frequency
    	HashMap<Integer,Double> normalizedResult=this.normalizeTF(tempResultMap);
    	//Sort the result
    	HashMap<Integer,Double> sortedHashMap=MiscUtility.sortByValues(normalizedResult);
    	//System.out.println(queryID);
    	//System.out.println(sortedHashMap);
    	return sortedHashMap;
    	
    }
    
    public HashMap<Integer,Double> normalizeTF(HashMap<Integer,Double> sortedHashMap)
    {
    	HashMap<Integer,Double> normalizedResult=new HashMap<>();
    	//Find maximum term frequency
    	double maxTF=0.0;
    	double a=0.4;
    	for(int key:sortedHashMap.keySet())
    	{
    		double tf=sortedHashMap.get(key);
    		if(tf>maxTF)maxTF=tf;
    	}
    	
    	for(int key:sortedHashMap.keySet())
    	{
    		double tf=sortedHashMap.get(key);
    		//double normalizedTF=a+(1-a)*(tf/maxTF);
    		double normalizedTF=tf/maxTF;
    		normalizedResult.put(key, normalizedTF);
    	}
    	return normalizedResult;
    }
    
    
   /* public HashMap<Integer,Double> findRVSMscore(int queryID)
    {
    	HashMap<Integer,Double> RVSMresult=new HashMap<>();
    	ArrayList<String> listFromBugLocator=this.bugLocatorOutputMap.get(queryID);
        for(String line:listFromBugLocator)
        {
        	String[] spilter=line.split(",");
        	int Sid=Integer.valueOf(spilter[0]);
        	int rank=Integer.valueOf(spilter[1]);
        	double score=Double.valueOf(spilter[2]);
        	RVSMresult.put(Sid, score);
        }
    	//System.out.println(listFromBugLocator);
    	HashMap<Integer,Double> normalizedResult=this.normalizeTF(RVSMresult);
    	return normalizedResult;
    }*/
    
	public static void main(String[] args) {
		// TODO Auto-generated method stub
        
		//Work on necessary inputs or maps
		BugLocalizationUsingNumbers obj=new BugLocalizationUsingNumbers("./data/FinalMap/TokenSourceMapTrainset1.txt", "./data/testset/test1.txt","./data/Bug-ID-Keyword-ID-Mapping.txt","./data/buglocator/eclipseOutputInNumbers.txt");
		obj.bugLocator(obj);
		
		
		//call the bug localizer
	}

	
	
}
