package performance.calculator;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import utility.ContentLoader;
import utility.ContentLoaderQR;
import utility.ContentWriter;
import utility.MiscUtility;

public class CalculatePerformanceForBench4BL {
  public ArrayList<String> listAllTestBugs;
    public HashMap<String, ArrayList<String>> gitResultsMap;
    public HashMap<String, ArrayList<String>> resultsMap;
    public String gitPath;
    public String resultPath;
    public String allBugPath;
    //public HashMap<String, String> bestRankListHM;
    //static HashMap<String, ArrayList<String>> finalRankedResult; 
    
    
    public CalculatePerformanceForBench4BL(String gitPath, String resultPath, String allBugPath)
    {
        this.gitPath=gitPath;
        this.resultPath=resultPath;
        
        this.resultsMap=new HashMap<>();
        this.gitResultsMap=new HashMap<>();
        this.listAllTestBugs=ContentLoader.getAllLinesOptList(allBugPath);
        //this.bestRankListHM=new HashMap<>();
        //this.finalRankedResult=new HashMap();
    }
    
    
    public CalculatePerformanceForBench4BL() {
        // TODO Auto-generated constructor stub
        
        
        
    }


    public static void main(String[] args) {
        // TODO Auto-generated method stub
        
        String [] methodList={"BugLocator","BLUiR","BRTracer","Amalgam","BLIA", "Locus"};
        for(int index=0;index<methodList.length;index++){
            System.out.print("Result For: "+methodList[index]);
            new CalculatePerformanceForBench4BL().getAvgPerformance("Spring","ROO", "1_1_0", methodList[index]);
        }
       //new CalculatePerformanceForBench4BL().getAvgPerformance("Apache","HIVE", "1_2_1", "Locus");
       
    }

   
    
    public static void getAvgPerformance( String corpus, String project, String version,  String technique)
    {
        HashMap <String, HashMap<String, Double>> resultContainer=new HashMap<>();
        CalculatePerformanceForBench4BL obj=new CalculatePerformanceForBench4BL();
        
        //String corpus="Apache";
        //String project="CAMEL";
       // String version="1_6_0";
        String resultoutput=technique+"_"+project+"_"+project+"_"+version+"_output.txt";
        String expName="exp"+corpus+project+"_"+version;
        String base= "E:\\PhD\\Repo\\"+corpus+"\\"+project+"\\"+version;
       
       // String alltestBugs=base+"/data/"+"allTestBugs.txt";
        String alltestBugs=base+"/data/"+"BugIDs.txt";
        ArrayList<String> listBRall=new ArrayList<>();
        
       
        
        

      
            
            obj=new CalculatePerformanceForBench4BL(base+"\\data\\gitInfo.txt",base+"/Bench4BLresults/"+expName+"/"+corpus+"/"+project+"/"+resultoutput, alltestBugs);       
                        
            obj.gitResultsMap=obj.getGitOutput(obj.gitPath);
            HashMap<String, ArrayList<String>> resultsMap=obj.getResults(obj.resultPath); 
         
            String key=obj.resultPath;
           // System.out.println(key);
            //System.out.println(resultsMap);
           // System.out.println(resultsMap.size());
            
            HashMap<String, Double> resultHM=obj.getResultForTopK(resultsMap);
            resultContainer.put(key, resultHM);
            
       
        
        //Now get the averageResult
       // getAverageResult(resultContainer);
       
        //MiscUtility.showResult(resultContainer.size(), resultContainer);
  
    }
    
    
    
  
    
   
    public static void getAverageResult(HashMap <String, HashMap<String, Double>> resultContainer)
    {
        double top1=0.0;
        double top5=0.0;
        double top10=0.0;
        double MRR=0.0;
        double MAP=0.0;
        double MRR10=0.0;
        double MAP10=0.0;
        double bugid=0.0;
        for(String key: resultContainer.keySet())
        {
            HashMap<String, Double> resultHM=resultContainer.get(key);
            //System.out.println(resultHM.get("bugid"));
            //System.out.println(resultHM.get("T1"));
            top1+=resultHM.get("T1");
            top5+=resultHM.get("T5");
            top10+=resultHM.get("T10");
            //MRR+=resultHM.get("MRR@1")+resultHM.get("MRR@5")+resultHM.get("MRR@10");
            MRR10+=resultHM.get("MRR@10");
            //MAP+=resultHM.get("MAP@1")+resultHM.get("MAP@5")+resultHM.get("MAP@10");
            MAP10+=resultHM.get("MAP@10");
            //System.out.println(top1+" "+top5+" "+top10+" ");
        }
        
        System.out.println();
        System.out.println(top1+" "+top1);
        System.out.println(top5+" "+top5);
        System.out.println(top10+" "+top10);
        System.out.println("MRR @ 10 "+MRR10);
        System.out.println("MAP @ 10 "+MAP10);
    }
    public HashMap<String, Double> getResultForTopK(HashMap<String, ArrayList<String>> resultsMap)
    {
        
        boolean emptybug=false;
        HashMap<String, Double> resultHM=new HashMap<>();
        int TOP_K=1;
        int count=0;
        //System.out.println("Result for Top-"+TOP_K);
        HashMap<String, ArrayList<String>> resultTop1=ComputePerformancePercent(TOP_K, resultsMap);
        System.out.println("============================="+resultTop1.size()+" "+this.gitResultsMap.size());


       double percentageT1=Double.valueOf(resultTop1.size())/this.gitResultsMap.size()*100;
       //double percentageT1=Double.valueOf(resultTop1.size())/resultsMap.size()*100;
       System.out.println(percentageT1);
   
       resultHM.put("T1", percentageT1); 
       //resultHM.put("MAP@1", ComputeMAP(resultTop1));
            
         //   resultHM.put("MRR@1", ComputeMRR(resultTop1, TOP_K));

        TOP_K=5;
        HashMap<String, ArrayList<String>> resultTop5=ComputePerformancePercent(TOP_K, resultsMap);
        System.out.println("============================="+resultTop5.size()+" "+this.gitResultsMap.size());
       double percentageT5=Double.valueOf(resultTop5.size())/this.gitResultsMap.size()*100;
        //double percentageT5=Double.valueOf(resultTop5.size())/resultsMap.size()*100;
        System.out.println(percentageT5);
        //double total_size=Double.valueOf(obj.resultsMap.size());
        
       // double percentageT5=(p5/total_size)*100;
       
           // resultHM.put("T5", percentageT5);
           //
          //  resultHM.put("MAP@5", ComputeMAP(resultTop5)); 
            
            //resultHM.put("MRR@5", ComputeMRR(resultTop5,TOP_K));
            
        TOP_K=10;
        HashMap<String, ArrayList<String>> resultTop10=ComputePerformancePercent(TOP_K, resultsMap);
        System.out.println("============================="+resultTop10.size()+" "+this.gitResultsMap.size());
        double percentageT10=Double.valueOf(resultTop10.size())/this.gitResultsMap.size()*100;
        //double percentageT10=Double.valueOf(resultTop10.size())/resultsMap.size()*100;
        System.out.println(percentageT10);
           // resultHM.put("T10", percentageT10);
            
          //  resultHM.put("MAP@10", ComputeMAP(resultTop10));
          
          //  resultHM.put("MRR@10", ComputeMRR(resultTop10 TOP_K));
            
        return resultHM;
    }
    
    
    
    
    public static double ComputeMAP(HashMap<String, ArrayList<String>> finalRankedResult)
    {
        double averagePrecision=0.0;
        for(String queryID: finalRankedResult.keySet())
        {
            ArrayList<String> rankList=finalRankedResult.get(queryID);
            //averagePrecision+=getAvgPrecisionEachQuery(rankList, queryID);
            //System.out.println(rankList);
            //System.out.println(getAvgPrecisionEachQuery(rankList));
        }
      //  int totalQuery=.size();
        //System.out.println("averagePrecision: "+averagePrecision);
      //  double MAP=averagePrecision/Double.valueOf(totalQuery);
        //System.out.println("Total Query: "+totalQuery+" MAP: "+MAP);
        return 0.0;
    }
    
    public double getAvgPrecisionEachQuery(ArrayList<String> rankList, String queryID)
    {
        double Precision=0.0;
        int count =0;
        for(String rankStr:rankList)
        {
            count++;
            int rank=Integer.valueOf(rankStr);
            Precision+=Double.valueOf(count)/Double.valueOf(rank);
        }
        int length=this.gitResultsMap.get(queryID).size();
        //double AvgPrecision=Precision/Double.valueOf(count);
        double AvgPrecision=Precision/Double.valueOf(length);
        return AvgPrecision;
        
    }
    
    public static double ComputeMRR(HashMap<String, ArrayList<String>> finalRankedResult, CalculatePerformanceForBench4BL obj, int TOP_K)
    {
        double averageRecall=0.0;
        for(String queryID: finalRankedResult.keySet())
        {
            ArrayList<String> rankList=finalRankedResult.get(queryID);
            averageRecall+=get1stRecall(rankList,TOP_K);
            //System.out.println(rankList);
            //System.out.println(get1stRecall(rankList,TOP_K));
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
        
        recall1st=1/Double.valueOf(rankList.get(0)+1);
        
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
                    //System.out.println(bugID+"                        "+file);
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
    
    private HashMap<String, ArrayList<String>> ComputePerformancePercent(int TOP_K, HashMap<String, ArrayList<String>> resultsMap) {
        // TODO Auto-generated method stub
        
        HashMap<String, ArrayList<String>> finalRankedResultlocal=new HashMap<>();
        int no_of_bug_matched=0;
        
        int total_found=0;
       
        for(String bugID:resultsMap.keySet())
        {
          //  if(this.listAllTestBugs.contains(bugID))
            {
            ArrayList<String> list=new ArrayList<String>();
            int found=0;
            ArrayList <String> resultList=resultsMap.get(bugID); //Get the experimented results
            for(int i=0;i<resultList.size();i++)
                {
                    for(int j=0;j<TOP_K;j++)
                    {
                        if(Integer.valueOf(resultList.get(i))==j)
                            {
                                
                                list.add(resultList.get(i));
                                found++;
                                break;
                            }
                    }
                        
                }
                if(list.size()>0){
                    
                    total_found++;
                }
                
                if(found>0)finalRankedResultlocal.put(bugID, list);
                
            
        }
            //else System.out.println("Not in Git?                              "+bugID);
        }
        
        //System.out.println("Total found: "+total_found);
        //System.out.println("Total bug: "+resultsMap.size());
        //System.out.println("No. of Matched Bug: "+no_of_bug_matched);
        //System.out.println("Top "+TOP_K+" %: "+(Double.valueOf(total_found)/Double.valueOf(no_of_bug_matched))*100);
        //System.out.print((Double.valueOf(total_found)/Double.valueOf(no_of_bug_matched))*100+" ");
        return finalRankedResultlocal;
      
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
                System.out.println("\nChangeset reloaded successfully for :"
                        + hm.size());
                return hm;
    
    }

    
    

    private HashMap<String, ArrayList<String>> getResults(String resultPath) {
        // TODO Auto-generated method stub
        HashMap<String, ArrayList<String>> hm=new HashMap<>();
        ArrayList <String> list =new ArrayList<String>();
        
        //list=ContentLoader.readContent(resultPath);
        list=ContentLoaderQR.readContent(resultPath);
        if(list.size()>1){
        for(String line: list)
        {
           // System.out.println(line);
            String [] spilter=line.split("\\s");
            String bugID=spilter[0];
           if(this.listAllTestBugs.contains(bugID))
            {
            String rankInfo=spilter[2];
            int rank=Integer.valueOf(rankInfo);
           // rank=rank+1;
            rankInfo=String.valueOf(rank);
           
            ArrayList<String> rankList=new ArrayList<>();
            if(rank<10)
            {
                if(hm.containsKey(bugID))
                {
                    rankList=hm.get(bugID);
                    rankList.add(rankInfo);
                }
                else
                {
                    rankList.add(rankInfo);
                }
                hm.put(bugID, rankList);
            }
            
        }
        }
        }
        return hm;
    }

    
    protected HashMap<String, ArrayList<String>> extractResultsForOwn() {
        ArrayList<String> lines = ContentLoader
                .getAllLinesList(this.resultPath);
        // .getAllLinesOptList(this.resultFile);
        HashMap<String, ArrayList<String>> resultMap = new HashMap<>();
        HashMap<String, ArrayList<String>> hm=new HashMap<>();
        for (String line : lines) {
            String[] parts = line.trim().split(" ");
            String bugID = (parts[0]);
            String file=parts[2];
            // int rank = Integer.parseInt(parts[2].trim());
            // if (rank >= 0 && rank < TOPK) {
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
        // System.out.println(repoName + ": Results:" + resultMap.size() + "\t"
        // + selectedBugs.size());
        return hm;
    }

}

