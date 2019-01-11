package performance.calculator;

import java.util.ArrayList;
import java.util.HashMap;

import javax.print.attribute.HashAttributeSet;

import utility.ContentLoader;
import utility.ContentWriter;
import utility.MiscUtility;

public class BugLocatorOutputConverter {
    
    String inFile;
    public String gitresultPath;
    public HashMap<String, ArrayList<String>> gitResultsMap;
    
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        String corpus="Eclipse";
        String base="E:\\PhD\\Repo\\"+corpus; 
        BugLocatorOutputConverter obj=new BugLocatorOutputConverter("E:/BugLocator/output/EclipseoutputJan04.txt",base+"\\data\\gitInfo"+corpus+".txt");
        obj.gitResultsMap=obj.getGitOutput(obj.gitresultPath);
        HashMap<String, ArrayList<String>> resultRankedHM= obj.ConverterHelper();
        System.out.println(ComputeTopK(1,resultRankedHM));
        System.out.println(ComputeTopK(5,resultRankedHM));
        System.out.println(ComputeTopK(10,resultRankedHM));
        ComputeMAP(resultRankedHM, obj);
        ComputeMRR(resultRankedHM);
    }

   
    public static double ComputeTopK(int TopK, HashMap<String, ArrayList<String>> resultRankedHM)
    {
        int count=0;
        for(String key:resultRankedHM.keySet())
        {
            ArrayList<String> list=resultRankedHM.get(key);
            for(String line:list)
            {
               
                int rank=Integer.valueOf(line);
                if(rank<TopK) 
                    {
                        count++;
                        //System.out.println(key);
                        break;
                    }
            }
        }
        double TopKResult=Double.valueOf(count)/Double.valueOf(resultRankedHM.size());
        System.out.println("TopK= "+count);
        return TopKResult;
    }
    
    public BugLocatorOutputConverter(String inFile,  String gitresultPath) {
        // TODO Auto-generated constructor stub
        this.inFile=inFile;
        this.gitresultPath=gitresultPath;
        this.gitResultsMap=new HashMap<>();
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
    public static double ComputeMRR(HashMap<String, ArrayList<String>> resultRankedHM)
    {
        double averageRecall=0.0;
        for(String queryID: resultRankedHM.keySet())
        {
            ArrayList<String> rankList=resultRankedHM.get(queryID);
            averageRecall+=get1stRecall(rankList);
            //System.out.println(rankList);
            //System.out.println(get1stRecall(rankList,TOP_K));
        }
        int totalQuery=resultRankedHM.size();
        
        double MRR=averageRecall/Double.valueOf(totalQuery);
        System.out.println("averageRecall: "+averageRecall+" MRR: "+MRR);
        return MRR;
    }
    public static double get1stRecall(ArrayList<String> rankList)
    {
        double recall1st=0.0;
        int count =0;
        int length=rankList.size();
        ArrayList<String> newRankList=new ArrayList<>();
        for(String line:rankList)
        {
            int rank=Integer.valueOf(line);
            if(rank<10) newRankList.add(line);
        }
        //System.out.println(newRankList.size());
        //System.out.println(newRankList);
        if(newRankList.size()>0){
            double Rank1st=Double.valueOf(newRankList.get(0))+1.0;
            recall1st=1/Rank1st;
        }
        return recall1st;
        
    }
    public static double ComputeMAP(HashMap<String, ArrayList<String>> resultRankedHM, BugLocatorOutputConverter obj)
    {
        double averagePrecision=0.0;
        for(String queryID: resultRankedHM.keySet())
        {
            ArrayList<String> rankList=resultRankedHM.get(queryID);
            averagePrecision+=getAvgPrecisionEachQuery(rankList, queryID, obj);
            //System.out.println(rankList);
            //System.out.println(getAvgPrecisionEachQuery(rankList));
        }
        int totalQuery=resultRankedHM.size();
        System.out.println("averagePrecision: "+averagePrecision);
        double MAP=averagePrecision/Double.valueOf(totalQuery);
        System.out.println("Total Query: "+totalQuery+" MAP: "+MAP);
        return MAP;
    }
    public static double getAvgPrecisionEachQuery(ArrayList<String> rankList, String queryID, BugLocatorOutputConverter obj)
    {
        double Precision=0.0;
        double AvgPrecision=0.0;
        int count =0;
        for(String rankStr:rankList)
        {
           
            int rank=Integer.valueOf(rankStr);
            if(rank<10){
                count++;
                double upperPart=Double.valueOf(count);
                double lowerPart=Double.valueOf(rank)+1.00;
                //Precision+=Double.valueOf(count)/(Double.valueOf(rank)+1.00);
                Precision+=upperPart/lowerPart;
            }
        }
        
        if(Precision>0.0){
            
            int length=obj.gitResultsMap.get(queryID).size();
            //double AvgPrecision=Precision/Double.valueOf(count);
            AvgPrecision=Precision/Double.valueOf(length);
        }
        return AvgPrecision;
        
    }
    
    
    public HashMap<String, ArrayList<String>> ConverterHelper()
    {
        //Read the input
        ArrayList<String> inputList=ContentLoader.readContent(this.inFile);
        //System.out.println(inputList);
        //Do the converstion
        HashMap<String, ArrayList<String>> outputHM=Converter(inputList);
        
        //System.out.println(outputHM.size());
        return outputHM;
    }
    public HashMap<String, ArrayList<String>> Converter(ArrayList<String> inputList)
    {
        HashMap<String, ArrayList<String>> hm=new HashMap<>();
        ArrayList<String> outputList=new ArrayList<>();
        for(String line:inputList)
        {
            String[] spilter=line.split(",");
            String bugID=spilter[0];
            int rank=Integer.valueOf(spilter[2]);
            String convertedLine="";
            //if(rank<10)
            {
                convertedLine=spilter[2];
                
            }
            if(convertedLine!=""){
            if(hm.containsKey(bugID)){
                outputList=hm.get(bugID);
                outputList.add(convertedLine);
                hm.put(bugID, outputList);
            }
            else
            {
                outputList=new ArrayList<>();
                outputList.add(convertedLine);
                hm.put(bugID, outputList);
            }
           }
        }
        
        
        return hm;
    }
}
