package parameter.tuning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.io.*;

import performance.calculator.CalculateLocalizationPerformance;
import utility.ContentLoader;
import utility.MiscUtility;

public class TuningParamMain {
	
	public static HashMap<String, ArrayList<String>> TrueSetResults;
	public static HashMap<String, ArrayList<String>> retActualResultSets;
	static //To Store no of bug matched for each alpha-beta combination
	HashMap<String, Integer> hmMatchedbug=new HashMap<String, Integer>();
	
	public TuningParamMain(HashMap<String, ArrayList<String>> TrueSetResults, HashMap<String, ArrayList<String>> retActualResultSets, HashMap<String, Integer> hmMatchedbug)
	{
		this.TrueSetResults=TrueSetResults;
		this.retActualResultSets=retActualResultSets;
		this.hmMatchedbug=hmMatchedbug;
	}
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		File fp=new File("./data/ParamTuning/100-1000/");
		String base="./data/ParamTuning/100-1000/";
        //Read the data
		TuningParamMain objTP=new TuningParamMain(TrueSetResults, retActualResultSets,hmMatchedbug);
		CalculateLocalizationPerformance obj=new CalculateLocalizationPerformance(objTP.TrueSetResults);
		
		//Obtain truthSet 
		objTP.TrueSetResults=obj.RetrieveTrueSetsType2("./data/TruthSetFromGit.txt");
		//Obtain resuts in a folder
		objTP.retActualResultSets=objTP.listFilesForFolder(fp, objTP, base);
		
	    //Do the param tuning
		Double alpha=0.1;
		Double beta=0.9;
		Double inc=0.2;
		AlphaBetaTuning(objTP.TrueSetResults, objTP.retActualResultSets, alpha, beta, inc);
		
		//call other functions
	}

	
	public static void AlphaBetaTuning(HashMap<String, ArrayList<String>> TrueSetResults, HashMap<String, ArrayList<String>> retActualResultSets, Double alpha, Double beta, Double increment)
	{
		int count=0;
		int matched_count=0;
		int m=0;
		//Work on each bug
		for(String bugId:retActualResultSets.keySet())
		{
			if(TrueSetResults.containsKey(bugId))
			{
			count++;
			
			//if(count>100)break;
			System.out.print(count+" "+bugId);
			
			//Now for each bug
			ArrayList<String> resultsList=retActualResultSets.get(bugId);
			int matched=DoTheTuneforEachBug(TrueSetResults, bugId, resultsList, alpha, beta, increment);
			
		    if(matched==1)
		    	{
		    		matched_count++;
		    		System.out.print(" "+(++m)+"\n");
		    	}
			}
			
		}
		
		System.out.println("Total bug: "+count+" matched: "+matched_count);
	}
	
	
	public static int DoTheTuneforEachBug(HashMap<String, ArrayList<String>> TrueSetResults, String bugId, ArrayList<String> resultList, Double alpha, Double beta, Double increment)
	{
		//Work on alpha and beta
		//bugId: id of the bug
		//resultList: contains the resulted source code where each line contains scores for two different source.
		//System.out.println(bugId);
		int m=0;
		
		Double b=beta;
	    Double a=alpha;
		for(a=alpha; a<=0.9; )
		{
			
			
			b=1-a;
			HashMap<String, Double> temp=new HashMap<String, Double>();
			for(int i=0;i<resultList.size();i++)
				//for(int i=0;i<1;i++)
			{
				
				String[] spilter=resultList.get(i).split(":");
				String SClinek=spilter[0];
				Double value1=Double.valueOf(spilter[1]);
				Double value2=Double.valueOf(spilter[2]);
				Double totalScore=a*value1+b*value2;
				//System.out.println(SClinek+" "+value1+" "+value2+" "+totalScore);
				temp.put(SClinek, totalScore);
				
			}
			//HashMap contains final results
			HashMap<String, Double> sortedTemp=MiscUtility.sortByValues(temp);
			//MiscUtility.showResult(10, sortedTemp);
			//Now compare with truthset and compute % and return %
			Double matched=resultPercentage(bugId, TrueSetResults, sortedTemp);
			//Store aplha beta values and %
			if(matched>0)
			{
				 //System.out.println("BugID: "+bugId+" "+" a= "+a+" b= "+b+" matched= "+matched);
				 m=1;
				 String albtKey=a+"-"+b;
				 if(hmMatchedbug.containsKey(albtKey))
				 {
					 Integer Value=hmMatchedbug.get(albtKey);
					 Value+=1;
					 hmMatchedbug.put(albtKey, Value);
				 }
				 else
				 {
					 hmMatchedbug.put(albtKey, 1);
				 }
			}
			
			
			a+=increment;
			
		}
		System.out.println();
		MiscUtility.showResult(hmMatchedbug.size(), hmMatchedbug);
		return m;
	}
	
	public static Double resultPercentage(String bugId, HashMap<String, ArrayList<String>> TrueSetResults, HashMap<String, Double> sortedTemp)
	{
		Double matched=0.0;
		
		//Retrieved list of truth sets for bugId from TrueSetResults
		if(TrueSetResults.containsKey(bugId))
		{
			ArrayList<String> truthSCLinks=TrueSetResults.get(bugId);
		
			int count=0;
	   
			for(String key:sortedTemp.keySet())
			{
				
				//For comparing only top 10 
				count++;
				if(count>10) break;
				if(truthSCLinks.contains(key))
				{
					System.out.println(key);
					matched++;
					
				}
				
			}
			
		}
		return matched;
	}
	
	public static void ReadData(TuningParamMain objTP, String truthFilePath)
	{
		
		CalculateLocalizationPerformance obj=new CalculateLocalizationPerformance(objTP.TrueSetResults);	
		objTP.TrueSetResults=obj.RetrieveTrueSetsType2(truthFilePath);
		
		
	}
	
	public static HashMap<String, ArrayList<String>> listFilesForFolder(final File folder, TuningParamMain objTP, String base) {
	    
		HashMap<String, ArrayList<String>> hm = new HashMap<String, ArrayList<String>>();
		for (final File fileEntry : folder.listFiles()) {
	        if (fileEntry.isDirectory()) {
	            listFilesForFolder(fileEntry, objTP, base);
	        } else {
	        	if(fileEntry.getName().equals(".DS_Store")==false)
	        	{
	        		//System.out.println(base+fileEntry.getName());
	        		String bugId=fileEntry.getName().substring(0, fileEntry.getName().length()-4);
	        		ArrayList<String> bugIdContent=ContentLoader.readContent(base+fileEntry.getName());
	        		hm.put(bugId, bugIdContent);
	        	}
	        }
	    }
	    return hm;
	}
}
