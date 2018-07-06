package corpus.maker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import utility.ContentLoader;
import utility.ContentWriter;

public class BugLoader {

	/**
	 * @param args
	 */
    static ArrayList<String> trueSet;
    String trueSetPath;
    String bugPath;
    
    
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		BugLoader obj=new BugLoader("./data/Results/trueResult2.txt", "/Users/user/Dropbox/WorkinginHome/SCAM/Implementation/DataCollection/BugReports/BugReportsTitleAndDescription/bugCorpus.txt");
		obj.retrieveExistingBugs("./data/BugCorpus/allBug.txt");
		
	}
	
	public BugLoader(String trueSetPath, String bugPath)
	{
		this.trueSet=new ArrayList<>();
		this.trueSetPath=trueSetPath;
		this.bugPath=bugPath;
		
		this.TrueBugLoad();
	}

	public void TrueBugLoad()
	{
		ArrayList<String> list=ContentLoader.readContent(trueSetPath);
		for(String line:list)
		{
			String [] spilter=line.split(":");
			String bugID=spilter[0];
		    this.trueSet.add(bugID.trim());
		}
	}
	
	public void retrieveExistingBugs(String outFile)
	{
		ArrayList<Integer> result=new ArrayList<>();
		ArrayList<String> bugList=ContentLoader.readContent(bugPath);
		for(String line:bugList)
		{
			String [] spilter=line.split(" ");
			String bugIDwithText=spilter[0];
			String bugID=bugIDwithText.substring(0, bugIDwithText.length()-5);
			if(trueSet.contains(bugID))
			{
				System.out.println(bugIDwithText+" : "+bugID+ " yes");
				result.add(Integer.valueOf(bugID));
				//result.add("\r\n");
			}
			else
			{
				System.out.println(bugIDwithText+" : "+bugID+ " no");
				
			}
		}
		
		Collections.sort(result);
		ContentWriter.writeContentInt(outFile, result);
	}


	
}
