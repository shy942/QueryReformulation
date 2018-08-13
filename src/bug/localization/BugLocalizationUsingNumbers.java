package bug.localization;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import bug.locator.provide.MasterBLScoreProvider;
import simi.score.calculator.CosineSimilarity;
import utility.ContentLoader;
import utility.ContentWriter;
import utility.CosineSimilarity2;
import utility.MiscUtility;
import bug.locator.provide.*;
import simi.score.calculator.*;
public class BugLocalizationUsingNumbers {

	public String trainMapTokenSourceAddress;
	public String trainMapSTA;
	public String testSetAddress;
	public String bugIDKeywordMapAddress;
	public String IdSourceAddress;
	
    public HashMap<Integer, ArrayList<Integer>> trainMapTokenSource;
    public HashMap<Integer, ArrayList<Integer>> trainMapST;
    public HashMap<Integer, ArrayList<Integer>> testSet;
    public HashMap<Integer, ArrayList<Integer>> bugIdKeywordMap;
    public  HashMap<String,Integer> IdsourceMap;
    public  HashMap<Integer,String> SourceIDMap;
    HashMap<Integer, HashMap<String, Double>> buglocatorRESULT;
    public BugLocalizationUsingNumbers(String trainMapTokenSourceAddress, String trainMapSTA, String testSetAddress, String bugIDKeywordMapAddress, String IdSourceAddress)
    {
    	this.trainMapTokenSourceAddress=trainMapTokenSourceAddress;
    	this.trainMapSTA=trainMapSTA;
    	this.trainMapTokenSource= new HashMap<>();
    	this.trainMapST=new HashMap<>();
    	this.testSetAddress=testSetAddress;
    	this.testSet= new HashMap<>();
    	this.bugIDKeywordMapAddress=bugIDKeywordMapAddress;
    	this.bugIdKeywordMap=new HashMap<>();
    	this.IdSourceAddress=IdSourceAddress;
    	this.IdsourceMap=new HashMap<>();
    	this.SourceIDMap=new HashMap<>();
    	this.IdsourceMap=this.LoadIdSourceMap(this.IdSourceAddress);
    	this.buglocatorRESULT=new HashMap<>();
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
    
   /* public HashMap<Integer, ArrayList<String>> loadHashMapCommaSep(String address)
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
    }*/
    
    
    public void bugLocator(BugLocalizationUsingNumbers obj)
    {
    	
    	obj.trainMapTokenSource=obj.loadHashMap(obj.trainMapTokenSourceAddress);
    	obj.trainMapST=obj.loadHashMap(obj.trainMapSTA);
		obj.testSet=obj.loadHashMap(obj.testSetAddress);
		obj.bugIdKeywordMap=obj.loadHashMap(obj.bugIDKeywordMapAddress);
		
		
		ArrayList<String> finalResult=new ArrayList<>();
		
		for(int queryID:testSet.keySet())
		{
			
			//if(buglocatorRESULT.containsKey(queryID))
			{	
				HashMap<Integer,Double> resultBugLocator=new HashMap<>();
				if(obj.buglocatorRESULT.containsKey(queryID)) resultBugLocator=obj.convertSIDtoNum(queryID,obj.buglocatorRESULT);
				//System.out.println("BL result");
				//MiscUtility.showResult(20, resultBugLocator);
				//System.out.println("My result");
				HashMap<Integer,Double> resultMyTool=obj.findBugForEachQuery(queryID);
				//MiscUtility.showResult(20, resultMyTool);
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
			
			ContentWriter.writeContent("./data/Results/finalResultTest1Aug13.txt", finalResult);
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
    			double combineScore=resultMyTool.get(key)*0.4+resultBugLocator.get(key);
    			tempCombineResult.put(key, combineScore);
    		}
    		else
    		{
    			tempCombineResult.put(key, resultMyTool.get(key)*0.4);
    		}
    	}
    	for(int key:resultBugLocator.keySet())
    	{
    		count++;
    		if(!tempCombineResult.containsKey(key))tempCombineResult.put(key, resultBugLocator.get(key));
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
    	HashMap<Integer,Double> normalizedResult=this.normalizeTF(tempResultMap, queryID);
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
    		CosineSimilarity2 cs1 = new CosineSimilarity2();
    		double cosineSimScore=cs1.Cosine_Similarity_Score(String.valueOf(key), String.valueOf(queryID));
    		
    		//double cosineSimScore=CosineSimilarity.similarity(String.valueOf(key), String.valueOf(queryID));
    		//System.out.println("----------------------------"+cosineSimScore+" "+normalizedTF+" "+(normalizedTF*cosineSimScore));
    		normalizedResult.put(key, cosineSimScore);
    	}
    	return normalizedResult;
    }
    
    
    public HashMap<Integer,Double> convertSIDtoNum(int queryID, HashMap<Integer, HashMap<String, Double>> buglocatorRESULT)
    {
    	
    	HashMap<Integer, Double> convertedResult=new HashMap<>();
    	HashMap<String, Double> resultBLforThisQuery=buglocatorRESULT.get(queryID);
       
    	System.out.println("=========\n"+resultBLforThisQuery);
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
    
	public static void main(String[] args) {
		// TODO Auto-generated method stub
        
		//Work on necessary inputs or maps
		BugLocalizationUsingNumbers obj=new BugLocalizationUsingNumbers("./data/FinalMap/TokenSourceMapTrainset1.txt", "./data/FinalMap/SourceTokenMapTrainset1.txt","./data/testset/test1.txt","./data/Bug-ID-Keyword-ID-Mapping.txt","./data/changeset-pointer/ID-SourceFile.txt");
		String bugReportFolder = "./data/testsetForBL/test1/";
		String sourceFolder = "/Users/user/Documents/Ph.D/2018/Data/ProcessedSourceForBL/";
		String goldsetFile = "./data/gitInfoNew.txt";
		obj.buglocatorRESULT=new MasterBLScoreProvider(sourceFolder, bugReportFolder, goldsetFile)
				.produceBugLocatorResults();
		//MiscUtility.showResult(10, obj.buglocatorRESULT);
		obj.bugLocator(obj);
		
		
		//call the bug localizer
	}

	
	
}
