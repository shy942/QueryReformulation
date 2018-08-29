package performance.calculator;

import java.util.ArrayList;
import java.util.HashMap;

import utility.ContentLoader;
import utility.MiscUtility;

public class PerformanceCalculatorPerfect {

	public HashMap<String, ArrayList<String>> gitResultsMap;
	public HashMap<String, ArrayList<String>> resultsMap;
	public String gitPath;
	public String resultPath;
	static HashMap<String, ArrayList<String>> finalRankedResult; 
	
	
	public PerformanceCalculatorPerfect(String gitPath, String resultPath)
	{
		this.gitPath=gitPath;
		this.resultPath=resultPath;
		this.resultsMap=new HashMap<>();
		this.gitResultsMap=new HashMap<>();
		this.finalRankedResult=new HashMap();
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		PerformanceCalculatorPerfect obj=new PerformanceCalculatorPerfect("./data/gitInfoNew.txt","./data/Results/Aug24TFbasedTest10.txt");		
		obj.gitResultsMap=obj.getGitOutput(obj.gitPath);
		
		System.out.println();
		obj.resultsMap=obj.getResults(obj.resultPath); 	
		
		//System.out.println(obj.gitResultsMap.size());
		//System.out.println(obj.resultsMap.size());
		showResult(obj);
		
		
	}

	
	
	public static void showResult(PerformanceCalculatorPerfect obj)
	{
		
		int TOP_K=1;
		System.out.println("Result for Top-"+TOP_K);
		ComputePerformancePercent(TOP_K, obj);
		//MiscUtility.showResult(100, finalRankedResult);
		System.out.println("MAP at "+TOP_K+" "+ComputeMAP(finalRankedResult,obj));
		System.out.println("MRR at "+TOP_K+" "+ComputeMRR(finalRankedResult,obj, TOP_K));
		
		System.out.println("=============================================================================");
		TOP_K=5;
		System.out.println("Result for Top-"+TOP_K);
		ComputePerformancePercent(TOP_K, obj);
		//MiscUtility.showResult(100, finalRankedResult);
		System.out.println("MAP at "+TOP_K+" "+ComputeMAP(finalRankedResult,obj));
		System.out.println("MRR at "+TOP_K+" "+ComputeMRR(finalRankedResult,obj, TOP_K));
		
		System.out.println("=============================================================================");
		TOP_K=10;
		System.out.println("Result for Top-"+TOP_K);
		ComputePerformancePercent(TOP_K, obj);
		//MiscUtility.showResult(100, finalRankedResult);
		System.out.println("MAP at "+TOP_K+" "+ComputeMAP(finalRankedResult,obj));
		System.out.println("MRR at "+TOP_K+" "+ComputeMRR(finalRankedResult,obj, TOP_K));
	}
	
	
	
	
	public static double ComputeMAP(HashMap<String, ArrayList<String>> finalRankedResult, PerformanceCalculatorPerfect obj)
	{
		double averagePrecision=0.0;
		for(String queryID: finalRankedResult.keySet())
		{
			ArrayList<String> rankList=finalRankedResult.get(queryID);
			averagePrecision+=getAvgPrecisionEachQuery(rankList);
		}
		int totalQuery=obj.resultsMap.size();
		double MAP=averagePrecision/Double.valueOf(totalQuery);
		return MAP;
	}
	
	public static double getAvgPrecisionEachQuery(ArrayList<String> rankList)
	{
		double Precision=0.0;
		int count =0;
		for(String rankStr:rankList)
		{
			count++;
			int rank=Integer.valueOf(rankStr);
			Precision+=Double.valueOf(count)/Double.valueOf(rank);
		}
		
		double AvgPrecision=Precision/Double.valueOf(count);
		
		return AvgPrecision;
		
	}
	
	public static double ComputeMRR(HashMap<String, ArrayList<String>> finalRankedResult, PerformanceCalculatorPerfect obj, int TOP_K)
	{
		double averageRecall=0.0;
		for(String queryID: finalRankedResult.keySet())
		{
			ArrayList<String> rankList=finalRankedResult.get(queryID);
			averageRecall+=getAvgRecallEachQuery(rankList,TOP_K);
		}
		int totalQuery=obj.resultsMap.size();
		double MRR=averageRecall/Double.valueOf(totalQuery);
		return MRR;
	}
	
	public static double getAvgRecallEachQuery(ArrayList<String> rankList, int TopK)
	{
		double Recall=0.0;
		int count =0;
		int length=rankList.size();
		String curRankStr=rankList.get(0);
		int rankCur=Integer.valueOf(curRankStr);
		//System.out.println(rankList);
	
		for(int r=1;r<rankList.size();r++)
		{ 
			String nextRankStr=rankList.get(r);
			count++;
			int rankNext=Integer.valueOf(nextRankStr);
			Recall+=getRecall(rankCur, rankNext, length, count);
			//System.out.println(rankCur+" "+rankNext+" "+length+" "+count);
			//System.out.println(getRecall(rankCur, rankNext, length, count));
			rankCur=rankNext;
		}
		Recall+=getRecall(rankCur, TopK+1, length, ++count);
		//System.out.println(getRecall(rankCur, TopK, length, count));
		double AvgPrecision=Recall/TopK;
		//System.out.println("Avg: "+AvgPrecision);
		return AvgPrecision;
		
	}
	
	public static double getRecall(int currentRank, int nextRank, int length, int count)
	{
		//System.out.println(currentRank+" "+nextRank+" "+length+" "+count);
		double recall=0.0;
		for(int i=1;i<=nextRank-currentRank;i++)
		{
			recall+=Double.valueOf(count)/Double.valueOf(length);
			//System.out.println(i+" Recall: "+Double.valueOf(count)/Double.valueOf(length));
		}
		return recall;
	}
	
	private static boolean IsMatched(ArrayList <String> resultList, ArrayList <String> gitList, String bugID, int TOP_K)
	{
	
		ArrayList<String> list=new ArrayList<>();
		int found=0;
		int count=0;
		for(String file:resultList){
			count++;
			if(count>TOP_K)break;
			for(String GoldFile:gitList){
				if(GoldFile.equalsIgnoreCase(file.trim())){
					found=1;
					if(finalRankedResult.containsKey(bugID)){
						list=finalRankedResult.get(bugID);
						list.add(String.valueOf(count));
					}
					else{
						list=new ArrayList<>();
						list.add(String.valueOf(count));
					}
					finalRankedResult.put(bugID, list);
				}
			}
		}
		if(found==1) return true;
		else return false;
	}
	
	private static HashMap<String, ArrayList<String>> ComputePerformancePercent(int TOP_K, PerformanceCalculatorPerfect obj) {
		// TODO Auto-generated method stub
		
		
		int no_of_bug_matched=0;
		
		int total_found=0;
	
		for(String bugID:obj.resultsMap.keySet())
		{
			
			ArrayList <String> resultList= obj.resultsMap.get(bugID); //Get the experimented results
	        if(obj.gitResultsMap.containsKey(bugID))// Truth set contains the bug
	        {
	        	ArrayList <String> gitList=obj.gitResultsMap.get(bugID);
	        	no_of_bug_matched++;
	        	
	        	if(IsMatched(resultList,gitList, bugID, TOP_K))total_found++;
	        }
	       
	    }
	    //System.out.println(finalRankedResult.size());
	    System.out.println("Total bug: "+no_of_bug_matched);
	    System.out.println("Total found: "+total_found);
	    System.out.println("Top "+TOP_K+" %: "+(Double.valueOf(total_found)/Double.valueOf(no_of_bug_matched))*100);
	    return finalRankedResult;
	    //ContentWriter.writeContent("./data/Results/test1-rankedResult.txt", finalRankedResult);
	}

	

	private HashMap<String, ArrayList<String>> getGitOutput (String gitPath) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
				HashMap<String, ArrayList<String>>hm=new HashMap<>();
				ArrayList<String> lines = ContentLoader
						.readContent(gitPath);
				for (int i = 0; i < lines.size();) {
					String currentLine = lines.get(i);
					String[] items = currentLine.split("\\s+");
					if (items.length == 2) {
						String bugID = items[0].trim();
						int filecount = Integer.parseInt(items[1].trim());
						if(filecount>0)
						{
						ArrayList<String> tempList = new ArrayList<>();
						for (int currIndex = i + 1; currIndex <= i + filecount; currIndex++) {
							if(!tempList.contains(lines.get(currIndex)))tempList.add(lines.get(currIndex));
						}
						// now store the change set to bug
						hm.put(bugID, tempList);
						}
						// now update the counter
						i = i + filecount;
						i++;
					}
				}
				System.out.println("Changeset reloaded successfully for :"
						+ hm.size());
				return hm;
	
	}


	private HashMap<String, ArrayList<String>> getResults(String resultPath) {
		// TODO Auto-generated method stub
		HashMap<String, ArrayList<String>> hm=new HashMap<>();
		ArrayList <String> list =new ArrayList<String>();
		list=ContentLoader.readContent(resultPath);
	    for(String line: list)
	    {
	    	//System.out.println(line);
	    	String [] spilter=line.split(",");
	    	String bugID=spilter[0];
	    	String file=spilter[1];
	    	ArrayList<String> fileAddress=new ArrayList<String>();
	    	if(hm.containsKey(bugID))
	    	{
	    		fileAddress=hm.get(bugID);
	    		fileAddress.add(file);
	    	}
	    	else
	    	{
	    		fileAddress.add(file);
	    	}
	    	hm.put(bugID, fileAddress);
	    }
		return hm;
	}

}
