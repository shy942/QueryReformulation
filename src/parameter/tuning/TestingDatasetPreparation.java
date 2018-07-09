package parameter.tuning;

import java.util.ArrayList;
import java.util.HashMap;

import performance.calculator.CalculateLocalizationPerformance;

public class TestingDatasetPreparation {

	
	public static HashMap<String, ArrayList<String>> retTrueSetResults;
	
	
	public TestingDatasetPreparation(HashMap<String, ArrayList<String>> retTrueSetResults)
	{
		this.retTrueSetResults=retTrueSetResults;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//Step 1: Create a file from git that contains bugs for which 10 source code files have been fixed.
		String gitInfoFile="./data/GitInfoFile2.txt";
		int howManybugs=100;
		TestingDatasetPreparation TDPobj=new TestingDatasetPreparation(retTrueSetResults);
		TDPobj.PrepareTruthSets(gitInfoFile, TDPobj);
		
	}


	
	
	
	public void PrepareTruthSets(String gitInfoFile, TestingDatasetPreparation TDPobj)
	{
		//Step 1: Create a file from git that contains bugs for which 10 source code files have been fixed.
	
				
		CalculateLocalizationPerformance obj=new CalculateLocalizationPerformance(retTrueSetResults);
				
		TDPobj.retTrueSetResults=obj.RetrieveTrueSetsType2(gitInfoFile);
		int i=0;
		for(String key:TDPobj.retTrueSetResults.keySet())
		{
			ArrayList <String> listFromTrueSets=retTrueSetResults.get(key);
			
			if(listFromTrueSets.size()>=0){
				utility.ContentWriter.writeContent("./data/TruthSetFromGit.txt", key+" "+listFromTrueSets.size());
				utility.ContentWriter.writeContent("./data/TruthSetFromGit.txt", listFromTrueSets);
				System.out.println(++i+" "+key+": "+listFromTrueSets.get(0));
			}
					
		}
	}
}
