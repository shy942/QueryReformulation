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
	//static HashMap<String, ArrayList<String>> finalRankedResult; 
	
	
	public PerformanceCalculatorPerfect(String gitPath, String resultPath)
	{
		this.gitPath=gitPath;
		this.resultPath=resultPath;
		this.resultsMap=new HashMap<>();
		this.gitResultsMap=new HashMap<>();
		//this.finalRankedResult=new HashMap();
	}
	
	
	public PerformanceCalculatorPerfect() {
		// TODO Auto-generated constructor stub
	}


	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		//new PerformanceCalculatorPerfect().getSingleResult("rVSM");
		new PerformanceCalculatorPerfect().getAvgPerformance(10, 0.4, "rVSMandMe");
	}


	
	public static void getAvgPerformance(int no_of_fold, double alpha, String baseNamePart)
	{
		HashMap <String, HashMap<String, Double>> resultContainer=new HashMap<>();
		
		for(int i=1;i<=no_of_fold;i++)
		{
			int test=i;
			PerformanceCalculatorPerfect obj=new PerformanceCalculatorPerfect("./data/gitInfoNew.txt","./data/Results/Sep7"+baseNamePart+alpha+"-"+test+".txt");		
			//PerformanceCalculatorPerfect obj=new PerformanceCalculatorPerfect("./data/gitInfoNew.txt","./data/buglocator/eclipseoutput.txt");	
			obj.gitResultsMap=obj.getGitOutput(obj.gitPath);
		
			System.out.println("Test#"+i);
			obj.resultsMap=obj.getResults(obj.resultPath); 
			
			String key=obj.resultPath;
			HashMap<String, Double> resultHM=getResultForTopK(obj);
			resultContainer.put(key, resultHM);
		}
		//Now get the averageResult
		getAverageResult(resultContainer, no_of_fold);
		MiscUtility.showResult(resultContainer.size(), resultContainer);
	}
	public static void getSingleResult(String baseNamePart)
	{
		PerformanceCalculatorPerfect obj=new PerformanceCalculatorPerfect("./data/gitInfoNew.txt","./data/Results/Sep7"+baseNamePart+"0.0-10.txt");	
		//PerformanceCalculatorPerfect obj=new PerformanceCalculatorPerfect("./data/gitInfoNew.txt","E:\\Data&Tool\\output.txt");	
		obj.gitResultsMap=obj.getGitOutput(obj.gitPath);
		
		System.out.println();
		obj.resultsMap=obj.getResults(obj.resultPath); 	
		
		//System.out.println(obj.gitResultsMap.size());
		//System.out.println(obj.resultsMap.size());
		getResultForTopK(obj);
		//MiscUtility.showResult(10, finalRankedResult);
	}
	public static void getAverageResult(HashMap <String, HashMap<String, Double>> resultContainer, int no_of_fold)
	{
		double top1=0.0;
		double top5=0.0;
		double top10=0.0;
		double MRR=0.0;
		double MAP=0.0;
		for(String key: resultContainer.keySet())
		{
			HashMap<String, Double> resultHM=resultContainer.get(key);
			top1+=resultHM.get("T1");
			top5+=resultHM.get("T5");
			top10+=resultHM.get("T10");
			MRR+=resultHM.get("MRR");
			MAP+=resultHM.get("MAP");
		}
		System.out.println(top1/Double.valueOf(no_of_fold));
		System.out.println(top5/Double.valueOf(no_of_fold));
		System.out.println(top10/Double.valueOf(no_of_fold));
		System.out.println("MRR "+MRR/Double.valueOf(no_of_fold));
		System.out.println("MAP "+MAP/Double.valueOf(no_of_fold));
	}
	public static HashMap<String, Double> getResultForTopK(PerformanceCalculatorPerfect obj)
	{
		
		
		HashMap<String, Double> resultHM=new HashMap<>();
		int TOP_K=1;
		
		System.out.println("Result for Top-"+TOP_K);
		HashMap<String, ArrayList<String>> resultTop1=ComputePerformancePercent(TOP_K, obj);
		//MiscUtility.showResult(30, resultTop1);
		double percentageT1=Double.valueOf(resultTop1.size())/Double.valueOf(obj.resultsMap.size())*100;
		resultHM.put("T1", percentageT1);
	
		//System.out.println("MRR at "+TOP_K+" "+ComputeMRR(resultTop1,obj, TOP_K));
		//System.out.println("MAP at "+TOP_K+" "+ComputeMAP(resultTop1,obj));
		
		//finalRankedResult.clear();
		System.out.println("=============================================================================");
		TOP_K=5;
		System.out.println("Result for Top-"+TOP_K);
		HashMap<String, ArrayList<String>> resultTop5=ComputePerformancePercent(TOP_K, obj);
		double percentageT5=Double.valueOf(resultTop5.size())/Double.valueOf(obj.resultsMap.size())*100;
		resultHM.put("T5", percentageT5);
		//resultHM.put("T5", ComputePerformancePercent(TOP_K, obj));
		//MiscUtility.showResult(resultTop5.size(), resultTop5);
		//System.out.println("=================="+finalRankedResult.size());
		//System.out.println("MRR at "+TOP_K+" "+ComputeMRR(finalRankedResult,obj, TOP_K));
		//System.out.println("MAP at "+TOP_K+" "+ComputeMAP(finalRankedResult,obj));
		
		//finalRankedResult.clear();
		System.out.println("=============================================================================");
		TOP_K=10;
		System.out.println("Result for Top-"+TOP_K);
		HashMap<String, ArrayList<String>> resultTop10=ComputePerformancePercent(TOP_K, obj);
		double percentageT10=Double.valueOf(resultTop10.size())/Double.valueOf(obj.resultsMap.size())*100;
		resultHM.put("T10", percentageT10);
		//resultHM.put("T10", ComputePerformancePercent(TOP_K, obj));
		MiscUtility.showResult(resultTop10.size(), resultTop10);
		//System.out.println("=================="+finalRankedResult.size());
		//System.out.println("MRR at "+TOP_K+" "+ComputeMRR(resultTop10,obj, TOP_K));
		//System.out.println("MAP at "+TOP_K+" "+ComputeMAP(resultTop10,obj));
		
		resultHM.put("MAP", ComputeMAP(resultTop10,obj));
		resultHM.put("MRR", ComputeMRR(resultTop10,obj, TOP_K));
		MiscUtility.showResult(100, resultHM);
		return resultHM;
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
		//System.out.println("averagePrecision: "+averagePrecision);
		double MAP=averagePrecision/Double.valueOf(totalQuery);
		//System.out.println("Total Query: "+totalQuery+" MAP: "+MAP);
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
			averageRecall+=get1stRecall(rankList,TOP_K);
		}
		int totalQuery=obj.resultsMap.size();
		int foundQuery=finalRankedResult.size();
		double MRR=averageRecall/Double.valueOf(totalQuery);
		//System.out.println("averageRecall: "+averageRecall);
		return MRR;
	}
	
	public static double get1stRecall(ArrayList<String> rankList, int TopK)
	{
		double recall1st=0.0;
		int count =0;
		int length=rankList.size();
		
		//System.out.println(rankList);
	
		for(int r=0;r<rankList.size();r++)
		{ 
			if(count>1)break;
			count++;
			recall1st=1/Double.valueOf(rankList.get(r));
			//System.out.println("recallfirst   "+recall1st);
			
		}
		
		
		
		return recall1st;
		
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
	
	private static boolean IsMatched(String file, ArrayList <String> gitList, String bugID, int TOP_K)
	{
			int found=0;
			for(String GoldFile:gitList){
				if(GoldFile.equalsIgnoreCase(file.trim())){
					found=1;
				}	
			}
			
		if(found==1) return true;
		else return false;
		
	}
	
	public static ArrayList<String> getRankedResult(ArrayList <String> resultList, ArrayList <String> gitList, String bugID, int TOP_K)
	{
		int count=0;
	    ArrayList<String> list=new ArrayList<>();
		for(String file:resultList){
			count++;
			if(count>TOP_K)break;
			if(IsMatched(file, gitList,bugID,TOP_K))
			{
				list.add(String.valueOf(count));
			}
			//count++;
		}
		return list;
	}
	
	private static HashMap<String, ArrayList<String>> ComputePerformancePercent(int TOP_K, PerformanceCalculatorPerfect obj) {
		// TODO Auto-generated method stub
		
		HashMap<String, ArrayList<String>> finalRankedResultlocal=new HashMap<>();
		int no_of_bug_matched=0;
		
		int total_found=0;
	
		for(String bugID:obj.resultsMap.keySet())
		{
			
			ArrayList <String> resultList= obj.resultsMap.get(bugID); //Get the experimented results
	        if(obj.gitResultsMap.containsKey(bugID))// Truth set contains the bug
	        {
	        	ArrayList <String> gitList=obj.gitResultsMap.get(bugID);
	        	no_of_bug_matched++;
	        	ArrayList<String> list=getRankedResult(resultList,gitList, bugID, TOP_K);
	        
	        
	        	if(list.size()>0){
	        		
	        		total_found++;
	        		finalRankedResultlocal.put(bugID, list);
	        	}
	        }
	       
	    }
	    //System.out.println(finalRankedResult.size());
	    //System.out.println("Total bug: "+no_of_bug_matched);
	   // System.out.println("Total found: "+total_found);
	    System.out.println("Total found: "+finalRankedResultlocal.size());
	    System.out.println("Total bug: "+obj.resultsMap.size());
	    //System.out.println("Top "+TOP_K+" %: "+(Double.valueOf(total_found)/Double.valueOf(no_of_bug_matched))*100);
	    System.out.print((Double.valueOf(total_found)/Double.valueOf(no_of_bug_matched))*100+" ");
	    return finalRankedResultlocal;
	   // return (Double.valueOf(total_found)/Double.valueOf(no_of_bug_matched))*100;
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
