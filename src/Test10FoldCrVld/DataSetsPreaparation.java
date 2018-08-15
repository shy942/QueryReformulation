package Test10FoldCrVld;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import utility.ContentLoader;
import utility.ContentWriter;
import utility.MiscUtility;

public class DataSetsPreaparation {

	private ArrayList<String> bugIDlist;
	ArrayList<String> bugKeywordLines;
	private String bugInFolder;
	private HashMap<String, String> bugContentHM;
	public DataSetsPreaparation(String bugIDFile, String bugKeywordFile, String bugInFolder)
	{
		this.bugIDlist=bugIDlist;
		this.bugKeywordLines=bugKeywordLines;
		bugIDlist=ContentLoader.getAllLinesList(bugIDFile);
		bugKeywordLines= ContentLoader.getAllLinesList(bugKeywordFile);
		this.bugInFolder=bugInFolder;
		this.bugContentHM=new HashMap<>();
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//Divide data sets
		//For MAc
		//DataSetsPreaparation obj=new DataSetsPreaparation("./data/bugIDs.txt","./data/Bug-ID-Keyword-ID-Mapping.txt","/Users/user/Documents/Ph.D/2018/Data/ProcessedBugData/");
		//For Windows
		DataSetsPreaparation obj=new DataSetsPreaparation("./data/bugIDs.txt","./data/Bug-ID-Keyword-ID-Mapping.txt","E:\\PhD\\Data\\BugDataNew\\");
		
		//Dont do this now//obj.DataPreparation("./data/BugCorpus/allBug.txt","./data/GitInfoFile2.txt","./data/bugIDs.txt");
		
		
		obj.bugContentHM=obj.LoadBugData();
		ArrayList<String> foldList=obj.FoldPreparation();
		obj.TrainAndTestSetPrep(foldList);
	}
      
	public HashMap<String, String> LoadBugData()
	{
		File[] files = new File(this.bugInFolder).listFiles();
		
		for (File file : files) {
			String content = ContentLoader.readContentSimple(file
					.getAbsolutePath());
			String fileName=file.getName().substring(0,file.getName().length()-4);
			this.bugContentHM.put(fileName, content);
		
		}
		return this.bugContentHM;
		//MiscUtility.showResult(10, this.bugContentHM);
	}
	
	public void CreateTestSetForBL(String bugOutFolder, ArrayList<String> testIDs, int step)
	{
		for(String bugID: testIDs)
		{
			if(this.bugContentHM.containsKey(bugID)){
			String content=this.bugContentHM.get(bugID);
			String outFile=bugOutFolder+"test"+step+"/"+bugID+".txt";
			ContentWriter.writeContent(outFile, content);
			}
		}
	}
	
    public void TrainAndTestSetPrep(ArrayList<String> foldList)
    {
    	System.out.println(bugIDlist.size());
    	System.out.println(foldList.size());
    	
    	
    	String line=foldList.get(0);
		String[]spilter=line.split(" ");
		int trainP1start=Integer.valueOf(spilter[0]);
		int trainP1end=0;
		int trainP2start=0;
		int trainP2end=0;
    	int n=10;
    	int teststart=0;
    	int testend=0;
    	for(int i=1;i<=9;i++)
    	{
    		System.out.println("index: "+i);
    		int Fj=n-i;
    		//Create training sets part-1
    		
    		line=foldList.get(Fj-1);
    		spilter=line.split(" ");
    		trainP1end=Integer.valueOf(spilter[1]);
    		CreateTrainSet(i, trainP1start,trainP1end,trainP2start,trainP2end,"./data/trainset/");
    		
    		//Create testing sets
    		line=foldList.get(Fj);
    		spilter=line.split(" ");
    		teststart=Integer.valueOf(spilter[0]);
    		testend=Integer.valueOf(spilter[1]);
    		CreateTestSet(i, teststart, testend, "./data/testset/");
    		
    		//Create training sets part-2
    		trainP2start=teststart;
    		trainP2end=bugIDlist.size();
    		
    		
    	}
    	//Create last training set
    	CreateTrainSet(10, 0, 0, teststart+1, trainP2end,"./data/trainset/");
    	
    	//Create last testing set
    	CreateTestSet(10, 1,trainP2start-1 , "./data/testset/");
    	
    	
    }
	
    public void CreateTestSet(int step, int teststart, int testend, String outfilepath)
    {
    	ArrayList<String> testID=new ArrayList<>();
    	System.out.println(teststart+" "+testend);
    	for(int i=teststart;i<=testend;i++)
    	{
    		testID.add(bugIDlist.get(i-1));
    	}
    	
    	
    	//For Creating test set for Bug Locator
    	CreateTestSetForBL("./Data/testsetForBL/", testID, step);
    	
    	ArrayList<String> createdTestData=new ArrayList<>();
    	//System.out.println(bugKeywordLines);
    
    	for(String ID:testID)
    	{
    		for(String line:bugKeywordLines)
    		{
    			
    			String[] spilt=line.split(":");
    			if(spilt.length>1)
    			{
    				String bugID=spilt[0];
    				String content=spilt[1];
    				if(ID.equals(bugID))
    				{
    					createdTestData.add(bugID+":"+content);
    					break;
    				}
    				
    			}
    		}
    	}
    	ContentWriter.writeContent(outfilepath+"test"+step+".txt", createdTestData);
    	System.out.println(createdTestData);
    	
    }
    
    
    public void CreateTrainSet(int step, int p1start, int p1end, int p2start, int p2end, String outfilepath)
    {
    	ArrayList<String> trainID=new ArrayList<>();
    	System.out.println(p1start+" "+p1end+" "+p2start+" "+p2end);
    	if(p1start>0)
    	{
    		for(int i=p1start;i<=p1end;i++)
    		{
    			//System.out.println(bugIDlist.get(i-1));
    			trainID.add(bugIDlist.get(i-1));
    			
    		}
    	}
    	if(p2start>0)
    	{
    		for(int i=p2start;i<=p2end;i++)
    		{
    			//System.out.println(bugIDlist.get(i-1));
    			trainID.add(bugIDlist.get(i-1));
    		}
    	}
    	
    	
    	
    	//Load bugKeywordFile
    	
    	
    	ArrayList<String> createdTrainData=new ArrayList<>();
    	//System.out.println(bugKeywordLines);
    
    	for(String ID:trainID)
    	{
    		for(String line:bugKeywordLines)
    		{
    			
    			String[] spilt=line.split(":");
    			if(spilt.length>1)
    			{
    				String bugID=spilt[0];
    				String content=spilt[1];
    				if(ID.equals(bugID))
    				{
    					createdTrainData.add(bugID+":"+content);
    					break;
    				}
    				
    			}
    		}
    	}
    	
    	ContentWriter.writeContent(outfilepath+"Train"+step+".txt", createdTrainData);
    	System.out.println(createdTrainData);
    	
    }
    
    
	public ArrayList<String> FoldPreparation()
	{
		
		System.out.println(bugIDlist.size());
		
		int N=bugIDlist.size();
		
		int k=Integer.valueOf(N/10);
		System.out.println("k="+k);
		int start=1;
		int end=k;
		System.out.println("=====================");
		//10 fold creation and storing them in a ArrayList
		ArrayList<String> foldList=new ArrayList<String>();
		for(int i=2;i<=10;i++)
		{
			 System.out.println(start+" "+end);
		     foldList.add(start+" "+end);
		    
		     start=end+1;
		     end=i*k;
		    
		}
		
		//For the last fold
		end=N;
		foldList.add(start+" "+end);
		System.out.println(foldList);
		
		return foldList;
	}
	
	
	public void DataPreparation(String bugIDFile, String gitFile, String outFile)
	{
		ArrayList<String> bugIDlist=ContentLoader.getAllLinesList(bugIDFile);
		
		
		System.out.println(bugIDlist.size());
		
		//Read Git file
		
		ArrayList<String> gitLines=ContentLoader.getAllLinesList(gitFile);
		
		System.out.println(gitLines.size());
		
		ArrayList<String> gitBugIDList=new ArrayList<String>();
		
		for(String line:gitLines)
		{
			String[] spilter=line.split(" ");
			if(spilter.length==1) continue;
			else 
			{
				String bugID=spilter[0];
				gitBugIDList.add(bugID);
			}
		}
		
		System.out.println(gitBugIDList.size());
		
		
		//Create bugId files who are in both git and database
		
		ArrayList<String> finalBugIDList=new ArrayList<String>();
		
		for(String ID:bugIDlist)
		{
			if(gitBugIDList.contains(ID))
			{
				finalBugIDList.add(ID);
			}
		}
		
		System.out.println("Total bug IDs: "+finalBugIDList.size());
		ContentWriter.appendContent(outFile, finalBugIDList);
		
	}
}
