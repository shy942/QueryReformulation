package bug.localization;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import bug.locator.provide.MasterBLScoreProvider;
import simi.score.calculator.CosineSimilarity;
import utility.ContentLoader;
import utility.ContentLoaderQR;
import utility.ContentWriter;
import utility.CosineSimilarity2;
import utility.MiscUtility;
import bug.locator.provide.*;
import simi.score.calculator.*;
public class BugLocalizationUsingNumbers {

	public String trainMapTokenSourceAddress;
	//public String trainMapSTA;
	public String testSetAddress;
	public String bugIDKeywordMapAddress;
	public String IdSourceAddress;
	public String IDKeywordAddress;
	
    public HashMap<Integer, ArrayList<Integer>> trainMapTokenSource;
    //public HashMap<Integer, ArrayList<Integer>> trainMapST;
    public HashMap<Integer, ArrayList<Integer>> testSet;
    public HashMap<Integer, ArrayList<Integer>> bugIdKeywordMap;
    public  HashMap<String,Integer> IdsourceMap;
    public  HashMap<Integer,String> SourceIDMap;
    public  HashMap<String,Integer> KeywordIDMap;
    public  HashMap<Integer,String>  IdKeywordMap;
    HashMap<Integer, HashMap<String, Double>> buglocatorRESULT;
    HashMap<Integer, ArrayList<String>> SidMatchWoord;
    public BugLocalizationUsingNumbers(String trainMapTokenSourceAddress, String trainMapSTA, String testSetAddress, String bugIDKeywordMapAddress, String IdSourceAddress, String IDKeywordAddress, String SidMatchWordAddress)
    {
    	this.trainMapTokenSourceAddress=trainMapTokenSourceAddress;
    	//this.trainMapSTA=trainMapSTA;
    	this.trainMapTokenSource= new HashMap<>();
    	//this.trainMapST=new HashMap<>();
    	this.testSetAddress=testSetAddress;
    	this.IDKeywordAddress=IDKeywordAddress;
    	this.testSet= new HashMap<>();
    	this.bugIDKeywordMapAddress=bugIDKeywordMapAddress;
    	this.bugIdKeywordMap=new HashMap<>();
    	this.IdSourceAddress=IdSourceAddress;
    	this.IdsourceMap=new HashMap<>();
    	this.SourceIDMap=new HashMap<>();
    	this.IdKeywordMap=new HashMap<>();
    	this.KeywordIDMap=new HashMap<>();
    	this.IdsourceMap=this.LoadIdSourceMap(this.IdSourceAddress);
    	this.IdKeywordMap=this.LoadIdKeywordMap(this.IDKeywordAddress);
    	this.buglocatorRESULT=new HashMap<>();
    	this.SidMatchWoord=new HashMap<>();
    	this.SidMatchWoord=this.loadHashMapComma(SidMatchWordAddress);
    	
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
    public HashMap<Integer, ArrayList<String>> loadHashMapComma(String address)
    {
    	HashMap<Integer, ArrayList<String>> temp=new HashMap<>();
        ArrayList<String> lines=ContentLoader.getAllLinesList(address);
        for(String line:lines)
        {
        	//System.out.println(line);
        	String[] spilter=line.split(":");
        	if(spilter.length>1)
        	{
        	int id=Integer.valueOf(spilter[0]);
        	String[] mapedID=spilter[1].split(",");
        	
        	ArrayList<String> list=new ArrayList<>();
        	for(String content:mapedID)
        	{
        		
        		list.add(content);
        	}
        	temp.put(id, list);
        	}
        	
        }
        return temp;
    }
  
    
    
    public void bugLocator(BugLocalizationUsingNumbers obj)
    {
    	
    	obj.trainMapTokenSource=obj.loadHashMap(obj.trainMapTokenSourceAddress);
    	//obj.trainMapST=obj.loadHashMap(obj.trainMapSTA);
		obj.testSet=obj.loadHashMap(obj.testSetAddress);
		obj.bugIdKeywordMap=obj.loadHashMap(obj.bugIDKeywordMapAddress);
		
		
		ArrayList<String> finalResult=new ArrayList<>();
		int i=0;
		for(int queryID:testSet.keySet())
		{
			System.out.println(++i);
			
			//if(buglocatorRESULT.containsKey(queryID))
			{	
				HashMap<Integer,Double> resultBugLocator=new HashMap<>();
				if(obj.buglocatorRESULT.containsKey(queryID)) resultBugLocator=obj.convertSIDtoNum(queryID,obj.buglocatorRESULT);
			
				HashMap<Integer,Double> resultMyTool=obj.findBugForEachQuery(queryID);
			
				HashMap<Integer, Double> resultMap=obj.CombileScoreMaker(queryID,resultBugLocator, resultMyTool);
				String result=queryID+",";
				int count=0;
				for(int key:resultMap.keySet())
				{
					count++;
					if(count>10)break; 
					finalResult.add(queryID+","+this.SourceIDMap.get(key)+","+resultMap.get(key));
				}
			}
			
			ContentWriter.writeContent("./data/Results/finalResultTest1Aug14.txt", finalResult);
		}
		//obj.TestingFileBugLocatorResult("./data/buglocator/test1Result.txt",finalResult);
		//do this once
		//obj.convertInputFile("./data/buglocator/eclipseoutput.txt", "./data/changeset-pointer/ID-SourceFile.txt");
    }
   
    
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
    	for(int key:resultBugLocator.keySet())
    	{
    		count++;
    		if(!tempCombineResult.containsKey(key))tempCombineResult.put(key, resultBugLocator.get(key));
    	}
    	HashMap<Integer, Double> sortedCombineResult=MiscUtility.sortByValues(tempCombineResult);
    	//System.out.println(sortedCombineResult);
    	//System.out.println(queryID+" : "+count);
    	return sortedCombineResult;
    }
    
    
    
    public HashMap<Integer,Double> findBugForEachQuery(int queryID)
    {
    	ArrayList<Integer> keywordList=this.bugIdKeywordMap.get(queryID);
    	
    	HashMap<Integer,Double> tempResultMap=new HashMap();
    	for(int keyword:keywordList)
    	{
    		if(this.trainMapTokenSource.containsKey(keyword))
    		{
    			ArrayList<Integer> tempList=this.trainMapTokenSource.get(keyword);
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
    	HashMap<Integer,Double> normalizedResult=this.ResultBasedOnCocineSimi(tempResultMap, queryID);
    	//Sort the result
    	HashMap<Integer,Double> sortedHashMap=MiscUtility.sortByValues(normalizedResult);
    	//System.out.println(queryID);
    	//System.out.println(sortedHashMap);
    	return sortedHashMap;
    	
    }
    
    public HashMap<Integer,Double> normalizeTF(HashMap<Integer,Double> sortedHashMap, int queryID)
    {
    	ArrayList<Integer> keywordList=this.bugIdKeywordMap.get(queryID);
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
    
    public HashMap<Integer,Double> ResultBasedOnCocineSimi(HashMap<Integer,Double> resultMap, int queryID)
    {
    	
    	HashMap<Integer, Double> hm=new HashMap<>();
    	for(int Sid:resultMap.keySet())
    	{
    		
    		double maxScore=this.returnMaxCosine(queryID, Sid);
    		hm.put(Sid, maxScore);
    		
    	}
    	return hm;
    }
    
    public double returnMaxCosine(int queryID, int Sid)
    {
    	ArrayList<Integer> keywordList=this.bugIdKeywordMap.get(queryID);
    	double maxCosineSim=0.0;
    	String queryContent=MiscUtility.listInt2Str(keywordList);
    	
    	if(this.SidMatchWoord.containsKey(Sid)){
    	ArrayList<String> SidMatch=this.SidMatchWoord.get(Sid);
    	//System.out.println(Sid+" "+SidMatch);
    	for(String content:SidMatch)
    	{
    		double cosineSimScore=CosineSimilarity.similarity(queryContent, content);
    		
    		if(cosineSimScore>maxCosineSim) maxCosineSim=cosineSimScore;
    	}
    	}
    	
    	//System.out.println("maxcosineSimScore     " +maxCosineSim);
    	return maxCosineSim;
	
    }
    
    public HashMap<Integer,Double> convertSIDtoNum(int queryID, HashMap<Integer, HashMap<String, Double>> buglocatorRESULT)
    {
    	
    	HashMap<Integer, Double> convertedResult=new HashMap<>();
    	HashMap<String, Double> resultBLforThisQuery=buglocatorRESULT.get(queryID);
       
    	//System.out.println("=========\n"+resultBLforThisQuery);
    	for(String Sid:resultBLforThisQuery.keySet())
    	{
    		if(this.IdsourceMap.containsKey(Sid))
    		{
    			convertedResult.put(this.IdsourceMap.get(Sid), resultBLforThisQuery.get(Sid));
    		}
    	}

    	return convertedResult;
    }
    
    public HashMap<String, Integer> LoadIdSourceMap(String inFile)
    {
    	ArrayList<String> content=ContentLoader.getAllLinesList(inFile);
    	for(String line:content)
    	{
    		String[] spilter=line.split(":");
    		int value=Integer.valueOf(spilter[0]);
    		String Sid=spilter[1].trim();
    		this.IdsourceMap.put(Sid, value);
    		this.SourceIDMap.put(value, Sid);
    	}
    	
		return this.IdsourceMap;
    	
    }
    
    public HashMap<Integer, String> LoadIdKeywordMap(String inFile)
    {
    	ArrayList<String> content=ContentLoader.getAllLinesList(inFile);
    	for(String line:content)
    	{
    		String[] spilter=line.split(":");
    		int value=Integer.valueOf(spilter[0]);
    		String Sid=spilter[1].trim();
    		this.KeywordIDMap.put(Sid, value);
    		this.IdKeywordMap.put(value, Sid);
    	}
    	
		return this.IdKeywordMap;
    	
    }
    public HashMap<String, ArrayList<String>> LoadSourceCodes(String folderPath)
    {
    	HashMap<String, ArrayList<String>> hm = new HashMap<>();
    	File[] files = new File(folderPath).listFiles();
		HashMap<String, ArrayList<String>> docMap = new HashMap<>();
		for (File file : files) {
			if(!file.getName().equals(".DS_Store")){
			    ArrayList<String> content=ContentLoader.getAllLinesList(file.getAbsolutePath());
				hm.put(file.getName(), content);
			}
		}
		MiscUtility.showResult(10, hm);
    	return hm;
    }

    
	public static void main(String[] args) {
		// TODO Auto-generated method stub
        
		//Work on necessary inputs or maps
		BugLocalizationUsingNumbers obj=new BugLocalizationUsingNumbers("./data/FinalMap/TokenSourceMapTrainset1.txt", "./data/FinalMap/SourceTokenMapTrainset1.txt","./data/testset/test1.txt","./data/Bug-ID-Keyword-ID-Mapping.txt","./data/changeset-pointer/ID-SourceFile.txt","./data/ID-Keyword.txt","./data/Sid-MatchWord.txt");
		String bugReportFolder = "./data/testsetForBL/test1/";
		//For Mac
		//String sourceFolder = "/Users/user/Documents/Ph.D/2018/Data/ProcessedSourceForBL/";
		//ForWindows
		String sourceFolder = "E:\\PhD\\Data\\ProcessedSourceMethodLevel\\";
		String goldsetFile = "./data/gitInfoNew.txt";
		
		obj.buglocatorRESULT=new MasterBLScoreProvider(sourceFolder, bugReportFolder, goldsetFile)
				.produceBugLocatorResults();
	
		obj.bugLocator(obj);
		
		
		//call the bug localizer
	}

	
	
}
