package corpus.maker;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import term.filtration.CommonTermFiltration;
import utility.ContentLoader;
import utility.ContentWriter; 
import utility.MiscUtility;

import config.StaticData;

public class BugExtractorTermsFilteration {
	
	String bugPPFolder;
	String bugFile;
	String singleQuery;
	String outFileAdj;
	String outFileMap;
	
	ArrayList <String> frequentKeywordList;
	ArrayList<String> allAcceptedBugs;
	
	
	
	public BugExtractorTermsFilteration(String bugPPFolder, String bugFile, String outFileAdj,String outFileMap)
	{
		this.bugPPFolder=bugPPFolder;
		this.bugFile=bugFile;
		this.outFileAdj=outFileAdj;
		this.outFileMap=outFileMap;
		this.singleQuery="";
		//this.bugPPFolder=StaticData.BUGDIR+"/BugAllContent/ProcessedData/";
		this.frequentKeywordList=new ArrayList<String>();
		LoadFrequentTerms();
		this.allAcceptedBugs=new ArrayList<>();
		LoadFrequentTerms();
		LoadAllAcceptedBugs();
	}
	
	public BugExtractorTermsFilteration(String bugPPFolder, String bugFile, String outFileMap)
	{
		this.bugPPFolder=bugPPFolder;
		this.bugFile=bugFile;
		this.outFileMap=outFileMap;
		this.frequentKeywordList=new ArrayList<String>();
		LoadFrequentTerms();
		this.allAcceptedBugs=new ArrayList<>();
		LoadFrequentTerms();
		LoadAllAcceptedBugs();
	}
	
	
	private void LoadAllAcceptedBugs() {
		// TODO Auto-generated method stub
		this.allAcceptedBugs=ContentLoader.readContent(bugFile);
	}
	
	public HashMap<Integer, String> PerFormFilterationForAllBugsOnce(int totalNoOfBugs, int noOfBugsForMap)
	{
		HashMap<Integer, String> hm=new HashMap<>();
		ArrayList <String> listForMapping=new ArrayList<String>();
		File[] files=new File(bugPPFolder).listFiles();
		int count=0;
	
	    int isFinish=0;
	    for(String bugID:allAcceptedBugs)
		{
			if(isFinish==0)
			{
			for(File f: files)
			{
				String bugIDwithText=f.getName();
				String bugIDfromfile=bugIDwithText.substring(0, bugIDwithText.length()-4);
				
				
				if(bugIDfromfile.equalsIgnoreCase(bugID))
				{
					//System.out.println(bugID+" "+bugIDfromfile);
					count++;
					if(count>totalNoOfBugs)
					{
						isFinish=1;
						break;
					}
					String fileredContent="";
					String content=ContentLoader.readContentSimple(f.getAbsolutePath());
					String[] spilter=content.split(" ");
					String bugIDfromContent=spilter[0]+"\n";
		    	
					for(String term:spilter)
					{
						if(frequentKeywordList.contains(term.trim()))
						{
							//Skip this keyword
						}
						else if(term.equalsIgnoreCase("java")||term.equalsIgnoreCase("main"))
						{
							//Skip this keyword
						}
						else
						{
							fileredContent+=term.trim()+" ";
						}
					}
					
					
					fileredContent=fileredContent.trim()+"\n";
					
				    
				    //System.out.println(count+"\n"+fileredContent);
				    hm.put(count, fileredContent);
				    if(count<=noOfBugsForMap)listForMapping.add(fileredContent);
					
				}
			}
		}
			
		}
	    ContentWriter.writeContent(this.outFileMap, listForMapping);
		return hm;
	}
	
	
	public void PerFormFilerationForTesting(int no_of_bugToMap)
	{
		File[] files=new File(bugPPFolder).listFiles();
		int count=0;
		ArrayList <String> list=new ArrayList<String>();
		ArrayList <String> listForMapping=new ArrayList<String>();
		int isFinish=0;
		for(String bugID:allAcceptedBugs)
		{
			if(isFinish==0)
			{
			for(File f: files)
			{
				String bugIDwithText=f.getName();
				String bugIDfromfile=bugIDwithText.substring(0, bugIDwithText.length()-4);
				
				
				if(bugIDfromfile.equalsIgnoreCase(bugID))
				{
					//System.out.println(bugID+" "+bugIDfromfile);
					count++;
					if(count>(no_of_bugToMap+1))
						{
							isFinish=1;
							break;
						}
					String fileredContent="";
					String content=ContentLoader.readContentSimple(f.getAbsolutePath());
					//System.out.println(content);
					String[] spilter=content.split(" ");
					String bugIDfromContent=spilter[0]+"\n";
		    	
					for(String term:spilter)
					{
						if(frequentKeywordList.contains(term.trim()))
						{
							//Skip this keyword
						}
						else if(term.equalsIgnoreCase("java")||term.equalsIgnoreCase("main"))
						{
							//Skip this keyword
						}
						else
						{
							fileredContent+=term.trim()+" ";
						}
					}
					//System.out.println(count+"\n"+fileredContent);
					
					fileredContent=fileredContent.trim()+"\n";
					
					if(count<=no_of_bugToMap)
					{
						listForMapping.add(fileredContent);
						list.add(fileredContent);
					}
					
					if(count>no_of_bugToMap)
					{
						singleQuery=fileredContent.trim();
						list.add(fileredContent);
					}
				}
			}
		}
		ContentWriter.writeContent(outFileAdj, list);
		ContentWriter.writeContent(outFileMap, listForMapping);
		}
	}
  
	public String getSingleQuery()
	{
		return this.singleQuery;
	}
	
	protected void PerformFilteration()
	{
		File[] files=new File(bugPPFolder).listFiles();
		//String allInOne="";
		ArrayList <String> list=new ArrayList<String>();
		ArrayList <String> bugTitleList=new ArrayList<String>();
 		int count=0;
		for(File f:files){
			//System.out.println(files.length);
			String bugIDwithText=f.getName();
			String bugID1=bugIDwithText.substring(0, bugIDwithText.length()-4);
			if(!f.getName().equalsIgnoreCase(".DS_Store"))
			{
		
				
				String fileredContent="";
				String content=ContentLoader.readContentSimple(f.getAbsolutePath());
				//System.out.println(content);
				String[] spilter=content.split(" ");
				String bugID=spilter[0]+"\n";
	    	
				for(String term:spilter)
				{
					if(frequentKeywordList.contains(term.trim()))
					{
						//Skip this keyword
					}
					else
					{
						fileredContent+=term.trim()+" ";
					}
				}
				//System.out.println(count+"\n"+fileredContent);
				fileredContent=fileredContent.trim()+"\n";
				list.add(fileredContent);
				bugTitleList.add(bugID);
				
			
				count++;
				if(count==2000)
				{
					String outFile=StaticData.BUGDIR+"/BugReportsTitleAndDescription/bugCorpusFiltered200NOTstem.txt";
					ContentWriter.writeContent(outFile, list);
					list.clear();
					ContentWriter.writeContent(StaticData.BUGDIR+"/BugReportsTitleAndDescription/titleInfo200ns.txt", bugTitleList);
					bugTitleList.clear();
					//break;
				}
			
				//if(count==300)break;
				if(count==250)
				{
					String outFile=StaticData.BUGDIR+"/BugReportsTitleAndDescription/bugCorpusFiltered250NOTstem.txt";
					ContentWriter.writeContent(outFile, list);
					//list.clear();
					ContentWriter.writeContent(StaticData.BUGDIR+"/BugReportsTitleAndDescription/titleInfo250ns.txt", bugTitleList);
					//bugTitleList.clear();
					break;
				}
				else if(count==1000)
				{
					String outFile=StaticData.BUGDIR+"/BugReportsTitleAndDescription/bugCorpusFiltered1000.NOTstem.txt";
					ContentWriter.writeContent(outFile, list);
					//list.clear();
					ContentWriter.writeContent(StaticData.BUGDIR+"/BugReportsTitleAndDescription/titleInfo1000.txt", bugTitleList);
					//bugTitleList.clear();
					break;
			
				}
			}
		}
		//String outFile=StaticData.BUGDIR+"/BugAllContent/bugCorpusFiltered11000.txt";
		//ContentWriter.writeContent(outFile, list);
		//ContentWriter.writeContent(StaticData.BUGDIR+"/BugAllContent/titleInfo11000.txt", bugTitleList);
	}
	protected void LoadFrequentTerms()
	{
		//Discard most frequent terms that appear more than 25% of the documents
		CommonTermFiltration obj=new CommonTermFiltration(bugPPFolder);
		obj.identifyCommonTerms();
		this.frequentKeywordList=obj.returnFrequentKeywords();
		//System.out.println("............."+MiscUtility.list2Str(frequentKeywordList));
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
         new BugExtractorTermsFilteration(StaticData.BUGDIR+"/BugReportsTitleAndDescription/ProcessedData/","./data/BugCorpus/allBug.txt","./data/BugCorpus/BugsForAdjac.txt","./data/BugCorpus/BugsForMap.txt").PerFormFilerationForTesting(30);
	}

	public void createBugsForMap() {
		// TODO Auto-generated method stub
		
	}

}
