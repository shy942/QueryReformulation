package corpus.maker;

import java.io.File;
import java.util.ArrayList;

import term.filtration.CommonTermFiltration;
import utility.ContentLoader;
import utility.ContentWriter;
import utility.MiscUtility;
import config.StaticData;

public class BugReportCorpusBuilderTitleAndDescription {

	int year;
	String corpus;
	String bugFolder;
	String bugPPFolder;
	int noOfBugReports;
	ArrayList <String> frequentKeywordList=new ArrayList<String>();
	public BugReportCorpusBuilderTitleAndDescription(int year)
	{
		this.year=year;
		this.bugFolder=StaticData.BUGDIR+"/new/"+year;
		this.bugPPFolder=StaticData.PROCESSEDBUGREPORTS+"/new/"+year;
		this.noOfBugReports=noOfBugReports;
	}
	
	public BugReportCorpusBuilderTitleAndDescription(String corpus)
	{
	    this.corpus=corpus;
		this.bugFolder="E:\\PhD\\Repo\\"+this.corpus+"\\BugDataExtracted\\";
		this.bugPPFolder="E:\\PhD\\Repo\\"+this.corpus+"\\BugData\\";
		this.noOfBugReports=noOfBugReports;
	}
	protected void createPreprocessedRepo()
	{
	    ArrayList<String> bugIDs=ContentLoader.getAllLinesList("E:\\PhD\\Repo\\"+this.corpus+"\\data\\BugIDdateBased.txt");
	    System.out.println(bugIDs);
		File[] files=new File(bugFolder).listFiles();
		//String allInOne="";
		ArrayList <String> list=new ArrayList<String>();
		noOfBugReports=files.length;
		ArrayList<String> listofFiles=new ArrayList<>();
		for(File f:files){
			if(!f.getName().equalsIgnoreCase(".DS_Store"))
			{
			    //System.out.println(f.getName());
			    String f_id=f.getName().substring(0,f.getName().length()-4);
			    //System.out.println(f_id);
				if(bugIDs.contains(f_id))
			    {
					System.out.println(f.getName()+" "+f.length());
					//if(f.length()<=1024)
					{
					    String fileName=f.getName();
					    String content=ContentLoader.readContentSimple(f.getAbsolutePath());
					    BugReportPreprocessor bpp=new BugReportPreprocessor(content);
					    String preprocessed=bpp.performNLPforAllContent();
					
					    preprocessed=preprocessed.trim()+"\n";
					    String outFile=this.bugPPFolder+fileName;
					    ContentWriter.writeContent(outFile, preprocessed);
					    if(!listofFiles.contains(outFile))listofFiles.add(outFile);
					    list.add(preprocessed);
					}
					//else
					{
					    System.out.println(f.getName());
					}
			    }
			}
		}
		//ContentWriter.writeContent("E:\\PhD\\Repo\\Eclipse\\data\\SourceFileNames.txt", listofFiles);
	}
	

	protected int getNoOFSourceCodes()
	{
		return noOfBugReports;
	}
	
	protected static void PutAll2gether()
	{
		String inFile=StaticData.PROCESSEDBUGREPORTS+"/new/AllinOne/";
		String outFile=StaticData.PROCESSEDBUGREPORTS+"/new/"+"inputAll.txt";
		File[] files=new File(inFile).listFiles();
		String content="";
		ArrayList<String> list=new ArrayList<String>();
		for(File f:files)
		{
			String contentFromEachFile=ContentLoader.readContentSimple(f.getAbsolutePath());
			//content+=contentFromEachFile+"\n";
			list.add(contentFromEachFile);
			//System.out.println(f.getAbsolutePath());
		}
		//String conetntStr=MiscUtility.list2Str(list);
		ContentWriter.appendContent(outFile, list);
	}
	
	protected static void IndividualYearProcessing()
	{
		int year=2015;
		new BugReportCorpusBuilderTitleAndDescription(year).createPreprocessedRepo();
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//IndividualYearProcessing();
		//PutAll2gether();
	    String corpus="Spring\\ROO\\1_1_0";
		new BugReportCorpusBuilderTitleAndDescription(corpus).createPreprocessedRepo();
	}

}
