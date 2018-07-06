package bug.localization;

import java.util.ArrayList;
import java.util.HashMap;

import token.file.mapping.KeywordSrcFileMapper;
import utility.ContentLoader;
import utility.ContentWriter;
import utility.CosineSimilarity2;
import utility.MiscUtility;
import corpus.maker.BugExtractorTermsFilteration;

public class SourceFilesCosineSimilarity {

	String bugFile;
	String gitInfoFile;
	String srcCodeDir;
	static HashMap<String, ArrayList<String>> reformedQueries;
	static HashMap<Integer, String> bugContentsAll;
	static HashMap<Integer, ArrayList<String>> changeSet;
	static HashMap<String, ArrayList<String>> keywordFileMap;
	static HashMap<String, ArrayList<String>> fileKeywordMap;
	static HashMap<String, ArrayList<String>> fileTokenMap;
	static HashMap<String, ArrayList<String>> tokenFileMap;
	
	static ArrayList<String> noOfSearchedFiles=new ArrayList<String>();;
	String singleBugID;
	static ArrayList<String> bugTitleList;
	
	static double alpha=0.5;
	public SourceFilesCosineSimilarity(String bugFile,String gitInfoFile,String srcCodeDir)
	{
		//default constructor
		this.bugFile=bugFile;
		this.gitInfoFile=gitInfoFile;
		this.srcCodeDir=srcCodeDir;
		this.reformedQueries=new HashMap<>();
		this.keywordFileMap=new HashMap<>();
		this.fileKeywordMap=new HashMap<>();
		this.fileTokenMap = new HashMap<>();
		this.tokenFileMap = new HashMap<>();
		this.bugTitleList=new ArrayList<String>();
		//this.noOfSearchedFiles=new ArrayList<String>();
		this.singleBugID="";
		this.bugContentsAll=new HashMap<>();
		this.changeSet=changeSet;
	}

	
	public void UploadBugData(int totalNoOfBugs, int noOfBugsForMap)
	{
		//Create a file contains contents for making adjacency list
		//Create a file that contains a query
		BugExtractorTermsFilteration obj=new BugExtractorTermsFilteration("./data/BugCorpus/ProcessedData/","./data/BugCorpus/allBug.txt","./data/BugCorpus/BugsForMap.txt");
		bugContentsAll=obj.PerFormFilterationForAllBugsOnce(totalNoOfBugs, noOfBugsForMap);
		//MiscUtility.showResult(10, bugContentsAll);
		//obj.createBugsForMap();
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String bugFileforMap = "./data/BugCorpus/BugsForMap.txt";
		String gitInfoFile = "./data/GitInfoFile2.txt";
		String srcCodeDir = "./data/ExampleSourceCodeFilesMethodLevel";
		SourceFilesCosineSimilarity SFCobj;
		
		ArrayList<String> totalResult=new ArrayList<>();
		
		int totalNoOfBugs=1000;
		int noOfBugsForMap=100;
		int upperlimit=1000;
		
		//For first time
		System.out.println("--------------------"+totalNoOfBugs+"-------------------"+noOfBugsForMap);
		
		SFCobj=new SourceFilesCosineSimilarity(bugFileforMap, gitInfoFile, srcCodeDir);
	    //Data Preparatiom
		SFCobj.UploadBugData(totalNoOfBugs, noOfBugsForMap);
		String firstquery=bugContentsAll.get(noOfBugsForMap+1);
		System.out.println(".........................+++++++++++++++++."+firstquery);
		//Maps Creation
		KeywordSrcFileMapper mapper = new KeywordSrcFileMapper(
				gitInfoFile, bugFileforMap);
		HashMap<String, ArrayList<String>> key2FileMap = mapper
				.mapKeyword2SrcFile();
		SFCobj.changeSet=mapper.getChangeSet();
		keywordFileMap = key2FileMap;
		HashMap<String, ArrayList<String>> file2KeyMap=mapper.getSrcFile2KeywordMap();
		fileKeywordMap=file2KeyMap;
		
		//For 1st query
		System.out.println(firstquery);
		String result=SFCobj.BLutlizingSourceCodeCosineRelationships(firstquery,keywordFileMap, fileKeywordMap, bugTitleList, noOfBugsForMap,upperlimit);
		totalResult.add("\r\n\r\n"+result);
		String previousQuery=firstquery;
		//For 2nd to last query
		for(int i=noOfBugsForMap+2;i<=upperlimit;i++)
		{
			System.out.println("--------------------"+i+"-------------------"+i);
			
			String currentQuery=bugContentsAll.get(i);
			System.out.println("currentQuery: "+" "+currentQuery);
			//Update KeywordFileMap and FileKeywordMap
			mapper.UpdateMaps(previousQuery,keywordFileMap, fileKeywordMap);
			
			System.out.println(currentQuery);
			//Bug Localization
			result=SFCobj.BLutlizingSourceCodeCosineRelationships( currentQuery,keywordFileMap, fileKeywordMap, bugTitleList,noOfBugsForMap,upperlimit);
			totalResult.add("\r\n\r\n"+result);
			previousQuery=currentQuery;
		}
		
		
		
		
		ContentWriter.writeContent("./data/Results/June21-"+noOfBugsForMap+"-"+totalNoOfBugs+"_NoAlpha.txt", totalResult);
	}

	public String BLutlizingSourceCodeCosineRelationships(String query, HashMap<String, ArrayList<String>> hmKeywordFileMap,HashMap<String, ArrayList<String>> hmfileKeywordMap, ArrayList<String> bugTitleList, int x, int y)
	{
		
		int no_of_bug=0;
		String content = null;
		String[] spilter=query.split(" ");
		String bugIDwithText=spilter[0];
		ArrayList<String> tempList=new ArrayList<String>();
		
		String bugID=bugIDwithText.substring(0, bugIDwithText.length()-5);
		System.out.println(bugIDwithText+" : "+bugID);
		content = bugID;
		singleBugID=bugID;
		for(int i=1;i<spilter.length;i++)
		{
			String QueryWord=spilter[i];
					if (hmKeywordFileMap.containsKey(QueryWord)) 
					{
						ArrayList<String> listOfFiles = hmKeywordFileMap
								.get(QueryWord);
						if (listOfFiles.size() > 1) 
						{
							for(String file:listOfFiles)
							{
							
								if(!tempList.contains(file))
								{
									tempList.add(file);
								}
							}
						}
						else if (listOfFiles.size() == 1) 
						{
							if(!tempList.contains(listOfFiles.get(0)))
							{
								tempList.add(listOfFiles.get(0));
								
							}
							
						}
						
					
					
				}
				
		}
				//Now compare the cosine similarity between query keywords and keywords associated with each source files in "temList"
				HashMap <String, Double> hmSortedforSourceCodeContent=CompareCosineSim(query,tempList,hmKeywordFileMap,hmfileKeywordMap);
				
		        //This score is not helping much....discard this asap.
				//HashMap<String, Double> hmSortedforMapping=CompareCosineSimQvsF(query, tempList, hmKeywordFileMap, hmfileKeywordMap);
				
				//HashMap<String, Double> hmSorted = CombinedHashMaps(hmSortedforSourceCodeContent,hmSortedforMapping,x, y);
				HashMap<String, Double> hmSorted=hmSortedforSourceCodeContent;
				ArrayList<String> tempResults=new ArrayList<String>();
				int count=0;
					
				for(String srcFile: hmSorted.keySet())
				{
					if(!tempResults.contains(srcFile))
					{
						tempResults.add(srcFile);
						content+=":"+srcFile;
						count++;
						if(count>10)break;
					}
				}
			
		
		
		return content;
	}
	private HashMap<String, Double> CompareCosineSim(String query, ArrayList<String> tempList,
			HashMap<String, ArrayList<String>> hmKeywordFileMap, HashMap<String, ArrayList<String>> hmfileKeywordMap) {
		// TODO Auto-generated method stub
		//Create a hashmap to cotain each file as key and associated keywords as values stroed in arraylist
				HashMap <String, Double> hm=new HashMap<>();
				for(String srcFile:tempList)
				{
					if(srcFile.contains(".java"))
					{
					String[] spilter=srcFile.split("/");
					String javaFileName=spilter[spilter.length-1];
					String fullFileName="./data/ExampleSourceCodeFilesMethodLevel/"+javaFileName;
					String content=ContentLoader.readContentProcessedSourceCode(fullFileName);
					String [] spilter2=content.split("\n");
					double maxCSvalue=0;
					for(int i=1;i<spilter2.length;i++)
					{
							//{String s1Text=content;
							String s1Text=spilter2[i];
							String s2Text=query.substring(0, query.length()-1);
							
							double cosineVal=0.0;
							CosineSimilarity2 cs1 = new CosineSimilarity2();
							cosineVal=cs1.Cosine_Similarity_Score(s1Text, s2Text);
							if(cosineVal>maxCSvalue)maxCSvalue=cosineVal;
							
					}
					hm.put(srcFile, maxCSvalue);
				    
					}
					
				}
			
				int xn=0;
				
				HashMap <String, Double> hmSorted=MiscUtility.sortByValues(hm);
				MiscUtility.showResult(10, hmSorted);
				
				return hmSorted;
			}
	

	private HashMap CombinedHashMaps(HashMap<String, Double> hmSortedforSourceCodeContent,
			HashMap<String, Double> hmSortedforMapping, int x, int y) {
		// TODO Auto-generated method stub
		int no_of_searched_source_files=0;
		HashMap<String, Double> hmTotalResult = new HashMap<>();
		String contentBothScoresWuthbugId="";
		for(String source:hmSortedforSourceCodeContent.keySet())
		{
			if(hmSortedforMapping.containsKey(source))
			{
				double valueForSourceCodeCoentent=hmSortedforSourceCodeContent.get(source);
				double valueForMapping=hmSortedforMapping.get(source);
				
				//double total=(alpha*valueForSourceCodeCoentent)+((1-alpha)*valueForMapping);
				double total=valueForSourceCodeCoentent+valueForMapping;
				no_of_searched_source_files++;
				hmTotalResult.put(source, total);
				//System.out.println("--------------------------------------------------------------------------------------------------------------------");
				if(valueForSourceCodeCoentent>0)
					{
					//System.out.println(source+": "+(valueForSourceCodeCoentent)+" "+valueForMapping+" = "+total);
				contentBothScoresWuthbugId=contentBothScoresWuthbugId+source+":"+ valueForSourceCodeCoentent+":"+valueForMapping+"\n";
					}
			}
		}
		
	    utility.ContentWriter.appendContent("./data/Results/ChangeSetResultFor-"+x+"-"+y+"_NoAlpha.txt", singleBugID+" "+no_of_searched_source_files);
	    
	    
	    
	    utility.ContentWriter.appendContent("./data/ParamTuning/"+x+"-"+y+"/"+singleBugID+".txt", contentBothScoresWuthbugId);
		HashMap <String, Double> hm=MiscUtility.sortByValues(hmTotalResult);
		
		
		return hm;
	}

	public HashMap<String, Double> CompareCosineSimQvsF(ArrayList <String> listOfReformedQueryWords,ArrayList<String> tempList, HashMap<String, ArrayList<String>> hmKeywordFileMap, HashMap<String, ArrayList<String>> hmfileKeywordMap) {
		// TODO Auto-generated method stub
		
		//Create a hashmap to cotain each file as key and associated keywords as values stroed in arraylist
		HashMap <String, Double> hm=new HashMap<>();
		for(String srcFile:tempList)
		{
			ArrayList<String> keywordList=hmfileKeywordMap.get(srcFile);
			if(hmfileKeywordMap.containsKey(srcFile)){
				if(keywordList.size()>1){
					String s1Text=MiscUtility.list2Str(keywordList);
					String s2Text=MiscUtility.list2Str(listOfReformedQueryWords);
					
					double cosineVal=0.0;
					CosineSimilarity2 cs1 = new CosineSimilarity2();
					cosineVal=cs1.Cosine_Similarity_Score(s1Text, s2Text);
					//System.out.println(srcFile+" "+s1Text+" + "+s2Text+" = "+cosineVal);
					hm.put(srcFile, cosineVal);
					
				}
			}
		}
	
		HashMap <String, Double> hmSorted=MiscUtility.sortByValues(hm);
		
		
		return hmSorted;
	}
	
	
	public HashMap<String, Double> CompareCosineSimQvsF(String query,ArrayList<String> tempList, HashMap<String, ArrayList<String>> hmKeywordFileMap, HashMap<String, ArrayList<String>> hmfileKeywordMap) {
		// TODO Auto-generated method stub
		
		//Create a hashmap to cotain each file as key and associated keywords as values stroed in arraylist
		HashMap <String, Double> hm=new HashMap<>();
		for(String srcFile:tempList)
		{
			ArrayList<String> keywordList=hmfileKeywordMap.get(srcFile);
			if(hmfileKeywordMap.containsKey(srcFile)){
				if(keywordList.size()>1){
					String s1Text=MiscUtility.list2Str(keywordList);
					String s2Text=query.substring(1, query.length()-1);
					
					double cosineVal=0.0;
					CosineSimilarity2 cs1 = new CosineSimilarity2();
					cosineVal=cs1.Cosine_Similarity_Score(s1Text, s2Text);
					//System.out.println(srcFile+" "+s1Text+" + "+s2Text+" = "+cosineVal);
					hm.put(srcFile, cosineVal);
					
				}
			}
		}
	
		HashMap <String, Double> hmSorted=MiscUtility.sortByValues(hm);
		
		
		return hmSorted;
	}
	
	
	public HashMap<String, Double> CompareCosineSim(ArrayList <String> listOfReformedQueryWords,ArrayList<String> tempList, HashMap<String, ArrayList<String>> hmKeywordFileMap, HashMap<String, ArrayList<String>> hmfileKeywordMap) {
		// TODO Auto-generated method stub
		
		//Create a hashmap to cotain each file as key and associated keywords as values stroed in arraylist
		HashMap <String, Double> hm=new HashMap<>();
		for(String srcFile:tempList)
		{
			if(srcFile.contains(".java"))
			{
			String[] spilter=srcFile.split("/");
			String javaFileName=spilter[spilter.length-1];
			String fullFileName="./data/ExampleSourceCodeFilesMethodLevel/"+javaFileName;
			String content=ContentLoader.readContentProcessedSourceCode(fullFileName);
			String [] spilter2=content.split("\n");
			double maxCSvalue=0;
			for(int i=1;i<spilter2.length;i++)
			{
					//{String s1Text=content;
					String s1Text=spilter2[i];
					String s2Text=MiscUtility.list2Str(listOfReformedQueryWords);
					
					double cosineVal=0.0;
					CosineSimilarity2 cs1 = new CosineSimilarity2();
					cosineVal=cs1.Cosine_Similarity_Score(s1Text, s2Text);
					if(cosineVal>maxCSvalue)maxCSvalue=cosineVal;
					
			}
			hm.put(srcFile, maxCSvalue);
		    // hm.put(srcFile, cosineVal);
		    // System.out.println(srcFile+": "+maxCSvalue);
			}
			
		}
	
		int xn=0;
		//System.out.println(xn);
		HashMap <String, Double> hmSorted=MiscUtility.sortByValues(hm);
		//MiscUtility.showResult(10, hmSorted);
		
		return hmSorted;
	}

}


