package bug.localization;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import LuceneBasedBL.BugLocatorLuceneBased;
import bug.locator.provide.MasterBLScoreProvider;
import rvsm.calculator.CosineMeasure;
import simi.score.calculator.CosineSimilarity;
import utility.ContentLoader;
import utility.ContentLoaderQR;
import utility.ContentWriter;
import utility.CosineSimilarity2;
import utility.JaccardIndexSimilarity;
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
    	this.SidMatchWoord=this.loadSidMatchMap(SidMatchWordAddress);
    	
    }
    
	
    public BugLocalizationUsingNumbers(String trainMapTokenSourceAddress, String testSetAddress, String bugIDKeywordMapAddress, String IdSourceAddress, String IDKeywordAddress) {
		// TODO Auto-generated constructor stub
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
    public HashMap<Integer, ArrayList<String>> loadSidMatchMap(String address)
    {
    	HashMap<Integer, ArrayList<String>> temp=new HashMap<>();
        ArrayList<String> lines=ContentLoader.getAllLinesList(address);
       
        for(int index=0;index<lines.size();index++)
        {
        	//System.out.println(line);
        	int id=Integer.valueOf(lines.get(index));
        	index++;
        	int no_of_method=Integer.valueOf(lines.get(index));
            ArrayList<String> list=new ArrayList<>(); 
        	for(int i=index+1;i<=index+no_of_method;i++)
        	{
        		list.add(lines.get(i));
        	}
        	index=index+no_of_method;
        	temp.put(id, list);
        }
        return temp;
    }
  
    public HashMap<Integer, ArrayList<Integer>> loadTrainMap(String address)
    {
    	HashMap<Integer, ArrayList<Integer>> temp=new HashMap<>();
        ArrayList<String> lines=ContentLoader.getAllLinesList(address);
       
        for(int index=0;index<lines.size();index++)
        {
        	//System.out.println(line);
        	String id=lines.get(index);
        	id=id.substring(0, id.length()-1);
        	int tokenID=Integer.valueOf(id);
        	index++;
        	String line=lines.get(index);
        	String[] spilter=line.split(",");
        	 ArrayList<Integer> list=new ArrayList<>();
        	 String Idstr="";
        	if(spilter.length>1)
        	{
           
             Idstr=spilter[0].trim();
            Idstr=Idstr.substring(1, Idstr.length());
            list.add(Integer.valueOf(Idstr));
        	for(int i=1;i<spilter.length-1;i++)
        	{
        		Idstr=spilter[i].trim();
        		list.add(Integer.valueOf(Idstr));
        	}
        	 Idstr=spilter[spilter.length-1];
             Idstr=Idstr.substring(0, Idstr.length()-1);
             list.add(Integer.valueOf(Idstr.trim()));
        	
        	}
        	else
        	{
        		Idstr=spilter[0].trim();
                Idstr=Idstr.substring(1, Idstr.length()-1);
                list.add(Integer.valueOf(Idstr));
        	}
        	//System.out.println(tokenID+" "+list);
        	temp.put(tokenID, list);
        }
        return temp;
    }
    
    public void bugLocator(BugLocalizationUsingNumbers obj, String outputFilePath, String sourceFolder, String bugReportFolder, String goldsetFile)
    {
    	double ALPHA=0.8;
		double BETA=0.2;
		int TOPK_SIZE=200;
		obj.buglocatorRESULT=new MasterBLScoreProvider(sourceFolder, bugReportFolder, goldsetFile)
				.produceBugLocatorResultsForMyTool(ALPHA, BETA, TOPK_SIZE );
    	obj.trainMapTokenSource=obj.loadTrainMap(obj.trainMapTokenSourceAddress);
    	//MiscUtility.showResult(10, obj.trainMapTokenSource);
		obj.testSet=obj.loadHashMap(obj.testSetAddress);
		obj.bugIdKeywordMap=obj.loadHashMap(obj.bugIDKeywordMapAddress);
		//MiscUtility.showResult(10, obj.bugIdKeywordMap);
		//MiscUtility.showResult(10, obj.testSet);
		ArrayList<String> finalResult=new ArrayList<>();
		int i=0;
		for(int queryID:testSet.keySet())
		{
			
			//System.out.println(queryID);
				HashMap<Integer,Double> resultBugLocator=new HashMap<>();
				if(obj.buglocatorRESULT.containsKey(queryID)){
					System.out.println(++i);
					//if(i>100) break;
					resultBugLocator=obj.convertSIDtoNum(queryID,obj.buglocatorRESULT);
				
			
				HashMap<Integer, Double> SortedBLresult=MiscUtility.sortByValues(resultBugLocator);
				
				//HashMap<Integer,Double> sortedResultMyTool
				//=obj.findBugForEachQueryCosineSimBased(queryID);
				//=obj.ResultBasedOnTF(queryID);
				
				HashMap<Integer, Double> resultMap
				//=sortedResultMyTool;
				=SortedBLresult;
				//=obj.CombileScoreMaker(queryID,SortedBLresult, sortedResultMyTool, 0.2);
				
				String result=queryID+",";
				int count=0;
				for(int key:resultMap.keySet())
				{
					count++;
					if(count>10)break; 
					finalResult.add(queryID+","+this.SourceIDMap.get(key)+","+resultMap.get(key));
				}
			}	
			//ContentWriter.writeContent("./data/Results/finalResultTest2Aug16.txt", finalResult);
		}
		ContentWriter.writeContent(outputFilePath, finalResult);
    }
   
    
    private HashMap<Integer, Double> findBugForEachQueryCosineSimBased(int queryID) {
		// TODO Auto-generated method stub
    	ArrayList<Integer> keywordList=this.bugIdKeywordMap.get(queryID);
    	//System.out.println("findBugForEachQueryCosineSimBased");
    	ArrayList <Integer> tempResultList=new ArrayList<>();
    	
    	for(int keyword:keywordList)
    	{
    		if(this.trainMapTokenSource.containsKey(keyword))
    		{
    			ArrayList<Integer> tempList=this.trainMapTokenSource.get(keyword);
    			//System.out.println("==========================\n"+tempList);
    		    for(int source:tempList)
    		    {
    		    	if(!tempResultList.contains(source))
    		    	{
    		    		tempResultList.add(source);
    		    		
    		    	}
    		    }
    		}
    	}
    	
    	
    	//System.out.println(tempResultList);
    	//Normalize term frequency
    	HashMap<Integer,Double> normalizedAndSortedResult=this.ResultBasedOnCocineSimi(tempResultList, queryID);
    	//Sort the result
    	//HashMap<Integer,Double> sortedHashMap=MiscUtility.sortByValues(normalizedResult);
    	//System.out.println(queryID);
    	//System.out.println(normalizedAndSortedResult);
    	return normalizedAndSortedResult;
	}


	public HashMap<Integer, Double> CombileScoreMaker(int queryID, HashMap<Integer,Double> resultBugLocator,HashMap<Integer,Double> resultMyTool, double ALPHA, HashMap<Integer, Double> hmQueryClassFileScore)
    {
    	//first key from VSM
		int firstkey=0;
		for(int key:resultBugLocator.keySet())
		{
			firstkey=key;
			System.out.println("First result: "+firstkey+" "+resultBugLocator.get(key));
			break;
		}
    	HashMap<Integer, Double> tempCombineResult=new HashMap<>();
    	int count = 0;
    	System.out.println(queryID);
    	int firstime=0;
    	for(int key:resultMyTool.keySet())
    	{
    	    
            if(resultBugLocator.containsKey(key)&& firstime==0)
                {
                    System.out.println("Result from my tool");
                    MiscUtility.showResult(10, resultMyTool);
                    System.out.println("Result from BugLocator");
                    MiscUtility.showResult(10, resultBugLocator);
                }
            firstime++;
    		if(resultBugLocator.containsKey(key))
    		{
    		    
    		    //System.out.println("key= "+key);
    		    double combineScore=0.0;
    		   if(hmQueryClassFileScore.containsKey(key))
    		    {
    		        count++;
    		        combineScore=resultMyTool.get(key)*0.2+resultBugLocator.get(key)*0.6+hmQueryClassFileScore.get(key)*0.2;
    		    }
    		    else
    		    {
    		        count++;
    		        combineScore=resultMyTool.get(key)*ALPHA+resultBugLocator.get(key)*(1-ALPHA);
    		    }
    			//System.out.println(key+" "+resultMyTool.get(key)*BETA+" "+resultBugLocator.get(key)*(1-BETA)+" "+combineScore);
    			/*if(key==firstkey) 
    				{
    			        System.out.println(key+" is found "+resultBugLocator.get(key));
    			        //tempCombineResult.put(key, resultBugLocator.get(key));
    			        tempCombineResult.put(key, resultBugLocator.get(key));
    			    }
    			else*/
    				tempCombineResult.put(key, combineScore);
    		}
    		else
    		{
    		    if(hmQueryClassFileScore.containsKey(key)) tempCombineResult.put(key, resultMyTool.get(key)*0.4+hmQueryClassFileScore.get(key)*0.4);
    		    else tempCombineResult.put(key, resultMyTool.get(key)*ALPHA);
    			//System.out.println(key+" "+resultMyTool.get(key)*BETA);
    		}
    	}
    	
    	for(int key:resultBugLocator.keySet())
    	{
    		count++;
    		
    		if(!tempCombineResult.containsKey(key))
    		{
    		    /*
    		    if(key==firstkey) 
                {
                    System.out.println(key+" is found "+resultBugLocator.get(key));
                    //tempCombineResult.put(key, resultBugLocator.get(key));
                    tempCombineResult.put(key, resultBugLocator.get(key));
                }
    		    else*/
    		    if(hmQueryClassFileScore.containsKey(key)) tempCombineResult.put(key, resultBugLocator.get(key)*0.6+hmQueryClassFileScore.get(key)*0.4);
    		    else tempCombineResult.put(key, resultBugLocator.get(key)*(1-ALPHA));
    		}
    	}
    	HashMap<Integer, Double> sortedCombineResult=MiscUtility.sortByValues(tempCombineResult);
        System.out.println("=========================================");
        MiscUtility.showResult(10, sortedCombineResult);
    	return sortedCombineResult;
    }
    
    
    
    public HashMap<Integer,Double> ResultBasedOnTF(int queryID)
    {
    	ArrayList<Integer> keywordList=this.bugIdKeywordMap.get(queryID);
    	//System.out.println(keywordList);
    	HashMap<Integer,Double> tempResultMap=new HashMap<>();
    	HashMap<Integer, Double> tempSizeMap=new HashMap<>();
    	for(int keyword:keywordList)
    	{
    		if(this.trainMapTokenSource.containsKey(keyword))
    		{
    			ArrayList<Integer> tempList=this.trainMapTokenSource.get(keyword);
    		    //System.out.println(tempList);
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
    	HashMap<Integer, Double> normalizedMap=normalizeTFandSorted(tempResultMap);
    	//Sort the result
    	HashMap<Integer,Double> sortedHashMap=MiscUtility.sortByValues(normalizedMap);
    	
    	//System.out.println(queryID);
    	System.out.println(sortedHashMap);
    	
    	return sortedHashMap;
    	
    	
    }
   
    public HashMap<Integer,Double> normalizeTFandSorted(HashMap<Integer,Double> tempResult)
    {
    	//ArrayList<Integer> keywordList=this.bugIdKeywordMap.get(queryID);
    	HashMap<Integer,Double> normalizedResult=new HashMap<>();
    	//Find maximum term frequency
    	double maxTF=0.0;
    	double minTF=12000.0;
    	//double a=0.4;
    	for(int key:tempResult.keySet())
    	{
    		double tf=tempResult.get(key);
    		if(tf>maxTF)maxTF=tf;
    	}
    	for(int key:tempResult.keySet())
        {
            double tf=tempResult.get(key);
            if(tf<minTF)minTF=tf;
        }
    	System.out.println(maxTF+"----"+minTF);
    	for(int key:tempResult.keySet())
    	{
    		double tf=tempResult.get(key);
    		//double normalizedTF=a+(1-a)*(tf/maxTF);
    		double normalizedTF=(tf-minTF)/(maxTF-minTF);
    
    		normalizedResult.put(key, normalizedTF);
    	}
    	//System.out.println("From normalizeTFandSorted ");
    	//MiscUtility.showResult(10, normalizedResult);
    	HashMap <Integer, Double> sortedHashMap=MiscUtility.sortByValues(normalizedResult);
    	
    	return sortedHashMap;
    }
    
    public HashMap<Integer,Double> ResultBasedOnCocineSimi(ArrayList<Integer> resultList, int queryID)
    {
    	//System.out.println("queryID "+queryID);
    	//System.out.println(resultList);
    	ArrayList<Integer> keywordList=this.bugIdKeywordMap.get(queryID);
    	HashMap<Integer, Double> hmCosineScore=new HashMap<>();
    	for(int Sid:resultList)
    	{
    		
    		double maxScore=this.returnMaxCosine(keywordList, Sid);
    		if(maxScore>0.0)hmCosineScore.put(Sid, maxScore);
    		
    	}
    	//Normalize the cosine score
    	HashMap<Integer, Double> normalizedHMCos=normalizeTFandSorted(hmCosineScore);
    	//MiscUtility.showResult(10, normalizedHMCos);
    	//Return first top 10 results
    	//HashMap<Integer, Double> justGetTop10Results=getTop10Result(normalizedHMCos);
    	//System.out.println("------------------------------------------------------------------------------------");
    	//MiscUtility.showResult(10, MiscUtility.sortByValues(justGetTop10Results));
    	return   normalizedHMCos;
    }
    public HashMap<Integer, Double> getTop10Result(HashMap <Integer, Double> sortedHashMap)
    {
    	HashMap<Integer, Double> justGetTop10Result=new HashMap<>();
    	int count=0;
    	for(int Sid: sortedHashMap.keySet())
    	{
    		count++;
    		if(count>10) break;
    		justGetTop10Result.put(Sid, sortedHashMap.get(Sid));
    	}
    	
    	return justGetTop10Result;
    }
    public double returnMaxCosine(ArrayList<Integer> keywordList, int Sid)
    {
    	
    	double maxCosineSim=0.0;
    	String queryContent=MiscUtility.listInt2Str(keywordList);
    	ArrayList<String> queryList=MiscUtility.ListIntTOListStr(keywordList);
       
  
    	if(this.SidMatchWoord.containsKey(Sid)){
    		ArrayList<String> SidMatch=this.SidMatchWoord.get(Sid);
    		//System.out.println("Sid "+Sid+" SidMatch "+SidMatch);
    		for(int i=0;i<SidMatch.size()-1;i++)
    		{
    			String content=SidMatch.get(i);
    			//System.out.println(content);
    			ArrayList<String> contentList=MiscUtility.str2ListMukta(content);
    			ArrayList<Integer> contentListInt=MiscUtility.str2ListInt(content);
    			//System.out.println("contentListInt "+contentListInt+"\n"+keywordList);
    			double cosineSimScore=0.0;
    			//if(!content.equals("")) cosineSimScore=new JaccardIndexSimilarity(queryList, contentList).ComputeJaccardIndexSimilarity();
    			//if(!content.equals("")) cosineSimScore= new CosineSimilarity2().Cosine_Similarity_Score(queryContent, content);
    			
    			if(!content.equals("")) cosineSimScore= CosineMeasure.getCosineSimilarity(keywordList, contentListInt);
    			if(cosineSimScore>maxCosineSim && cosineSimScore>0.0)
    			{
    				//System.out.println(queryList+"\n"+contentList+"\n"+cosineSimScore);
    				maxCosineSim=cosineSimScore;
    			}
    	
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
    	    //System.out.println(line);
    		String[] spilter=line.split(":");
    		int value=Integer.valueOf(spilter[0]);
    		String Sid=spilter[1].trim();
    		this.KeywordIDMap.put(Sid, value);
    		this.IdKeywordMap.put(value, Sid);
    	}
    	
		return this.IdKeywordMap;
    	
    }
   

    
	public static void main(String[] args) {
		// TODO Auto-generated method stub
        
		//Work on necessary inputs or maps
		int total_test=2;
		double alpha=0.4;
		ArrayList<String> resultAll=new ArrayList<String>();
		String corpus="Apache";
		for(int i=1;i<=total_test;i++)
		{
			
			int test=i;
		    String project="HBASE";
	        String version="1_2_4";
	        String base= "E:\\PhD\\Repo\\"+corpus+"\\"+project+"\\"+version;
	        
			//String base="E:\\PhD\\Repo\\"+corpus+"\\"; 
			
			BugLocalizationUsingNumbers obj=new BugLocalizationUsingNumbers(base+"\\data\\FinalMap\\TokenSourceMapTrainset"+test+".txt",base+"\\data\\testset\\test"+test+".txt",base+"\\data\\Bug-ID-Keyword-ID-Mapping.txt",base+"\\data\\changeset-pointer\\ID-SourceFile.txt",base+"\\data\\ID-Keyword.txt");
			String bugReportFolder = base+"\\data\\testsetForBL\\test"+test;
			//For Mac
			//String sourceFolder = "/Users/user/Documents/Ph.D/2018/Data/ProcessedSourceForBL/";
			//ForWindows
			String sourceFolder = base+"\\ProcessedSourceCorpusJuly2019\\";
				
			//String goldsetFile = base+"\\data\\"+corpus+"All.txt";
			
			String outputFilePath
			//="./data/Results/Aug24BLTest"+test+".txt";
			=base+"\\data\\Results\\"
			        + "VSMandMeJuly10"+alpha+"-"
			+test
			+".txt";
			//="./data/Results/Aug24TFbasedTest"+test+".txt";
		
			//System.out.println(bugReportFolder);
			
			//Actual working function
			obj.bugLocatorLuceneAndMe(base,project, version,alpha,corpus,obj, outputFilePath, bugReportFolder,test);
		    //For list return
			//resultAll.add(obj.bugLocatorLuceneAndMeReturnList(alpha,corpus,obj, outputFilePath, bugReportFolder));
		
		}
		//ContentWriter.writeContent("E:\\PhD\\BugLocatorP2\\results\\"+corpus+"\\BLuAMIR\\results"+corpus+"AsSc.txt", resultAll);
	}

	
	 public HashMap<Integer, Double> queryClassNameScoreLoader(String address, int queryID)
	 {
	       HashMap<Integer, Double> hmQueryClassFileScore=new HashMap<>();
	       String content=ContentLoader.readContentSimple(address+String.valueOf(queryID)+".txt.txt");
	       if(content.length()>0){
	       String[] lines=content.split("\n");
	      // if(lines.length>0){
	           
	           for(String line:lines)
	           {
	               System.out.println(address+" "+queryID+" "+line);
	               String[] spilter=line.split(":");
	               int sid=Integer.valueOf(spilter[0]);
	               Double score=Double.valueOf(spilter[1]);
	               hmQueryClassFileScore.put(sid, score);
	           }
	       }
	       
	       return hmQueryClassFileScore;
	 }
	 public void bugLocatorLuceneAndMe(String base, String project, String version, double ALPHA,String corpus,BugLocalizationUsingNumbers obj, String outputFilePath, String bugReportFolder, int test)
	    {
		 	//double ALPHA=0.7;
		 	double BETA=1-ALPHA;
		 	
		 	//For Eclipse
		 	//String indexDir="C:\\Users\\Mukta\\Workspace-2018\\BigLocatorRVSM\\Data\\Index\\";
		 	//ForSWT
		 	String indexDir=base+"\\data\\Index"+corpus+project+version;
		 	//String indexDir="E:\\PhD\\Repo\\"+corpus+"\\data\\Index"+corpus;
			obj.buglocatorRESULT=new BugLocatorLuceneBased(indexDir, bugReportFolder )
					.getLuceneBasedScore();
			System.out.println(obj.buglocatorRESULT+"                99999999999999999999999999999999999999999999999999999999999");
	    	obj.trainMapTokenSource=obj.loadTrainMap(obj.trainMapTokenSourceAddress);
	    	//MiscUtility.showResult(10, obj.trainMapTokenSource);
			obj.testSet=obj.loadHashMap(obj.testSetAddress);
			obj.bugIdKeywordMap=obj.loadHashMap(obj.bugIDKeywordMapAddress);
			//MiscUtility.showResult(10, obj.bugIdKeywordMap);
			//MiscUtility.showResult(10, obj.testSet);
			ArrayList<String> finalResult=new ArrayList<>();
			ArrayList<String> workingExample=new ArrayList<>();
			int i=0;
			String querypath="";
			for(int queryID:testSet.keySet())
			{
				
			        HashMap<Integer, Double> hmQueryClassFileScore=queryClassNameScoreLoader(base+"\\data\\module2\\test"+test+"\\", queryID);
			        System.out.println(hmQueryClassFileScore);
					HashMap<Integer,Double> resultBugLocator=new HashMap<>();
					if(obj.buglocatorRESULT.containsKey(queryID)){
						System.out.println(++i);
						//if(i>1) break;
						resultBugLocator=obj.convertSIDtoNum(queryID,obj.buglocatorRESULT);
					
				
					HashMap<Integer, Double> SortedBLresult=MiscUtility.sortByValues(resultBugLocator);
					
					
					//System.out.println("===========================BL");
					//MiscUtility.showResult(10,SortedBLresult );
					HashMap<Integer,Double> sortedResultMyTool
					//=obj.findBugForEachQueryCosineSimBased(queryID);
					=obj.ResultBasedOnTF(queryID);
					//System.out.println("===========================My");
					//MiscUtility.showResult(10, sortedResultMyTool);
					HashMap<Integer, Double> resultMap
					//=sortedResultMyTool;
					//=SortedBLresult;
					=obj.CombileScoreMaker(queryID,SortedBLresult, sortedResultMyTool, ALPHA, hmQueryClassFileScore);
					//System.out.println("===========================Comp");
					//MiscUtility.showResult(10, resultMap);
					querypath=String.valueOf(queryID);
					String result=queryID+",";
					int count=0;
					for(int key:resultMap.keySet())
					{
					    {
					    count++;
						if(count>10)break; 
						finalResult.add(queryID+","+this.SourceIDMap.get(key)+","+resultMap.get(key)+","+sortedResultMyTool.get(key)+","+SortedBLresult.get(key));
						//finalResult.add(queryID+","+this.SourceIDMap.get(key)+","+resultMap.get(key)+","+SortedBLresult.get(key));
						//finalResult.add(queryID+","+this.SourceIDMap.get(key));
					    }
						
					}
					
				}						//ContentWriter.writeContent(outputFilePath+queryID, finalResult);
			}
			
			ContentWriter.writeContent(outputFilePath, finalResult);
			//ContentWriter.appendContent("E:\\PhD\\BugLocatorP2\\results\\Eclipse\\BLiAMIR\\Eclipse3071_BLuAMIR.txt", finalResult);
	    }
	 public String bugLocatorLuceneAndMeReturnList(double ALPHA,String corpus,BugLocalizationUsingNumbers obj, String outputFilePath, String bugReportFolder)
     {
         //double ALPHA=0.7;
         double BETA=1-ALPHA;
         
         //For Eclipse
         //String indexDir="C:\\Users\\Mukta\\Workspace-2018\\BigLocatorRVSM\\Data\\Index\\";
         //ForSWT
         String indexDir="E:\\PhD\\Repo\\"+corpus+"\\data\\Index"+corpus;
         obj.buglocatorRESULT=new BugLocatorLuceneBased(indexDir, bugReportFolder )
                 .getLuceneBasedScore(BETA);
         System.out.println(obj.buglocatorRESULT+"                99999999999999999999999999999999999999999999999999999999999");
         obj.trainMapTokenSource=obj.loadTrainMap(obj.trainMapTokenSourceAddress);
         //MiscUtility.showResult(10, obj.trainMapTokenSource);
         obj.testSet=obj.loadHashMap(obj.testSetAddress);
         obj.bugIdKeywordMap=obj.loadHashMap(obj.bugIDKeywordMapAddress);
         //MiscUtility.showResult(10, obj.bugIdKeywordMap);
         //MiscUtility.showResult(10, obj.testSet);
         //ArrayList<String> finalResult=new ArrayList<>();
         String finalResult="";
       
         ArrayList<String> workingExample=new ArrayList<>();
         int i=0;
         for(int queryID:testSet.keySet())
         {
             
             
                 HashMap<Integer,Double> resultBugLocator=new HashMap<>();
                 if(obj.buglocatorRESULT.containsKey(queryID)){
                     System.out.println(++i);
                     //if(i>5) break;
                     resultBugLocator=obj.convertSIDtoNum(queryID,obj.buglocatorRESULT);
                 
             
                 HashMap<Integer, Double> SortedBLresult=MiscUtility.sortByValues(resultBugLocator);
                 
                 //MiscUtility.showResult(10,SortedBLresult );
                 
                 //HashMap<Integer,Double> sortedResultMyTool
                 //=obj.findBugForEachQueryCosineSimBased(queryID);
                 //=obj.ResultBasedOnTF(queryID);
                 
                 HashMap<Integer, Double> resultMap
                 //=sortedResultMyTool;
                 =SortedBLresult;
                 //=obj.CombileScoreMaker(queryID,SortedBLresult, sortedResultMyTool, ALPHA);
             
                 String result=queryID+",";
                 int count=0;
                 int resultMapSize=resultMap.size();
                 int lastkey=0;
                 for(int key:resultMap.keySet())
                 {
                     count++;
                     if(count==100||count==resultMapSize-1)
                     {
                         lastkey=key;
                         break; 
                     }
                     //finalResult.add(queryID+","+this.SourceIDMap.get(key)+","+resultMap.get(key)+","+sortedResultMyTool.get(key)+","+SortedBLresult.get(key));
                     //finalResult.add(queryID+","+this.SourceIDMap.get(key)+","+resultMap.get(key)+","+SortedBLresult.get(key));
                     //finalResult.add(queryID+","+this.SourceIDMap.get(key));
                     if(count<100)finalResult=finalResult+queryID+","+this.SourceIDMap.get(key)+","+resultMap.get(key)+"\n";
                     //else if(count==50) finalResult=finalResult+queryID+","+this.SourceIDMap.get(key)+","+resultMap.get(key);
                     System.out.println(queryID+","+this.SourceIDMap.get(key)+","+resultMap.get(key));
                 }
                 finalResult=finalResult+queryID+","+this.SourceIDMap.get(lastkey)+","+resultMap.get(lastkey);
                 System.out.println(queryID+","+this.SourceIDMap.get(lastkey)+","+resultMap.get(lastkey));
                 
             }   
                 ContentWriter.writeContent(outputFilePath, finalResult);
         }
         
         //ContentWriter.writeContent(outputFilePath, finalResult);
        return finalResult;
     }
	 public void bugLocatorLuceneAndMeTech2(double alpha,String corpus,BugLocalizationUsingNumbers obj, String outputFilePath, String bugReportFolder)
     {
         double ALPHA=0.4;
         double BETA=1-ALPHA;
         
         //For Eclipse
         //String indexDir="C:\\Users\\Mukta\\Workspace-2018\\BigLocatorRVSM\\Data\\Index\\";
         //ForSWT
         String indexDir="E:\\PhD\\Repo\\"+corpus+"\\data\\Index"+corpus;
         obj.buglocatorRESULT=new BugLocatorLuceneBased(indexDir, bugReportFolder )
                 .getLuceneBasedScore(BETA);
         System.out.println(obj.buglocatorRESULT+"                99999999999999999999999999999999999999999999999999999999999");
         obj.trainMapTokenSource=obj.loadTrainMap(obj.trainMapTokenSourceAddress);
         //MiscUtility.showResult(10, obj.trainMapTokenSource);
         obj.testSet=obj.loadHashMap(obj.testSetAddress);
         obj.bugIdKeywordMap=obj.loadHashMap(obj.bugIDKeywordMapAddress);
         //MiscUtility.showResult(10, obj.bugIdKeywordMap);
         //MiscUtility.showResult(10, obj.testSet);
         ArrayList<String> finalResult=new ArrayList<>();
         ArrayList<String> workingExample=new ArrayList<>();
         int i=0;
         for(int queryID:testSet.keySet())
         {
             
             
                 HashMap<Integer,Double> resultBugLocator=new HashMap<>();
                 if(obj.buglocatorRESULT.containsKey(queryID))
                 {
                     System.out.println(++i);
                     System.out.println(queryID);
                     
                 
             
                 //HashMap<Integer, Double> SortedBLresult=MiscUtility.sortByValues(obj.buglocatorRESULT);
                 HashMap<String,Double> processedResultBL=ProcessResultBL(obj.buglocatorRESULT.get(queryID));
                 //System.out.println(processedResultBL.size());
                 //MiscUtility.showResult(10,SortedBLresult );
                 
                 HashMap<Integer,Double> sortedResultMyTool
                 //=obj.findBugForEachQueryCosineSimBased(queryID);
                 =obj.ResultBasedOnTF(queryID);
                 HashMap<String,Double> processedResultMytool=ProcessResultMyTool(sortedResultMyTool);
                 //System.out.println(processedResultMytool);
                 //HashMap<Integer, Double> resultMap
                 //=sortedResultMyTool;
                 //=SortedBLresult;
                 //=obj.CombileScoreMaker(queryID,SortedBLresult, sortedResultMyTool, ALPHA);
                 HashMap<String, Double> combineResult=ComibineResultByMethod(processedResultBL, processedResultMytool, ALPHA);
                 String result=queryID+",";
                 int count=0;
                 for(String key:combineResult.keySet())
                 {
                     {
                     count++;
                    if(count>1000)break; 
                    finalResult.add(queryID+","+key+","+combineResult.get(key));
                     }
                     
                 }
                 
             }   
             
         }
         //ArrayList<String> processedResult=ProcessFinalResult(finalResult);
        ContentWriter.writeContent(outputFilePath, finalResult);
         //ContentWriter.appendContent("E:\\PhD\\Repo\\SWT\\data\\Results\\swtSep24ALLVSMandMe.txt", finalResult);
     }
	 
	 
	 
	 public HashMap<String, Double> ComibineResultByMethod(HashMap<String,Double> processedResultBL, HashMap<String,Double> processedResultMytool, double ALPHA)
	 {
	     
	     //System.out.println(processedResultBL);
	     HashMap<String, Double> tempResult=new HashMap<>();
	     HashMap<String,Double> sortedBL=MiscUtility.sortByValues(processedResultBL);
	     HashMap<String,Double> sortedMyTool=MiscUtility.sortByValues(processedResultMytool);
	    
	     System.out.println(sortedMyTool);
	     //first key from VSM
	     String firstkey="";
	     for(String key:sortedBL.keySet())
	     {
	          firstkey=key;
	          System.out.println(sortedBL);
	          System.out.println("First result: "+firstkey+" "+sortedBL.get(key)/0.6);
	          break;
	      }
	     int count=0;
	     for(String key:sortedMyTool.keySet())
	     {
	         if(sortedBL.containsKey(key))
	            {
	                count++;
	                double combineScore=sortedMyTool.get(key)*ALPHA+sortedBL.get(key);
	                System.out.println(key+" "+sortedMyTool.get(key)*ALPHA+" "+sortedBL.get(key)+" "+combineScore);
	                if(key==firstkey){
	                    tempResult.put(key, sortedBL.get(key)/0.6);
	                }
	                else{
	                tempResult.put(key, combineScore);
	                }
	            }
	            else
	            {
	                tempResult.put(key, sortedMyTool.get(key)*ALPHA);
	                System.out.println(key+" "+sortedMyTool.get(key)*ALPHA);
	            }
	     }
	     for(String key:sortedBL.keySet())
	     {
	          count++;
	          if(!tempResult.containsKey(key)){
	              if(key==firstkey){
                      tempResult.put(key, sortedBL.get(key)/0.6);
                  }
	              else{
	                  tempResult.put(key, sortedBL.get(key));
	              }
	          }
	     }
	     
	     HashMap<String,Double> sortedFinalResult=MiscUtility.sortByValues(tempResult);
	     System.out.println(sortedFinalResult);
	     return sortedFinalResult;
	 }
	 public HashMap<String,Double> ProcessResultBL(HashMap<String,Double> map)
     {
	     HashMap<String,Double> processedResulthm=new HashMap();
         int count=0;
         ArrayList<String> tempResult=new ArrayList<>();
         
         //System.out.println(this.SourceIDMap);
         //System.out.println("=========           ===================           ===============");
         for(String key:map.keySet())
         {
            
             //System.out.println(key+",,,,,,,,,"+map.get(key));
             //System.out.println("");
             
             String processedSid=ProcessSourceID(key);
             if(tempResult.contains(processedSid)){
                 //Do nothing
             }else{
                 count++;
                 tempResult.add(processedSid);
                 processedResulthm.put(processedSid,map.get(key));
             }
         }
         
         return  processedResulthm;
     }
	 
	     public HashMap<String, Double> ProcessResultMyTool(HashMap<Integer,Double> resultMyTool)
	     {
	         HashMap<String, Double> processedResult=new HashMap<>();
	         int count=0;
	         ArrayList<String> tempResult=new ArrayList<>();
	         for(int key:resultMyTool.keySet())
	         {
	             String sourceID=this.SourceIDMap.get(key);
	             //String processedSid=ProcessSourceID(sourceID);
	             double score=resultMyTool.get(key);
	             if(tempResult.contains(sourceID)){
	                 //Do nothing
	             }else{
	                 count++;
	                 tempResult.add(sourceID);
	                 processedResult.put(sourceID,score);
	             }
	         }
	         
	         return processedResult;
	     }
	
	     public String ProcessSourceID(String Sid)
	     {
	        // System.out.println("Sid               "+Sid);
	         String processedSid="";
	         String[] spilter2=Sid.split("\\.");
	         int len=spilter2.length;
	         //System.out.println("Sid               "+Sid+"         "+len);
	         String lastPart=spilter2[len-2]+"."+spilter2[len-1];
	         String firstPart="";
	         for(int i=0;i<len-3;i++)
	         {
	             firstPart=firstPart+"."+spilter2[i];
	         }
	         processedSid=lastPart;
	        // System.out.println(processedSid);
	         return processedSid;
	     }
}
