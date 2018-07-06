package performance.calculator;

import java.util.ArrayList;

import utility.ContentLoader;

public class ChangeSetAnalyzer {

	/**
	 * @param args
	 */
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String inFile="/Users/user/Documents/workspace-2016/QueryReformulation/data/Results/ChangeSetResult";
		ArrayList<String> changeSetList=LoadChangedSets(inFile);
		AnalyzeChangeSet(changeSetList);
	}

	private static void AnalyzeChangeSet(ArrayList<String> changeSetList) {
		// TODO Auto-generated method stub
		int noOfBugs=changeSetList.size();
		System.out.println("Total No. of Bugs: "+noOfBugs);
		int total=0;
		for(String line: changeSetList)
		{
			String [] spilter=line.split(" ");
			int noOfChangedFiles=Integer.valueOf(spilter[1]);
			System.out.println(noOfChangedFiles);
			total+=noOfChangedFiles;
		}
		int average=total/noOfBugs;
		System.out.println("Average: "+average);
	}

	private static ArrayList<String> LoadChangedSets(String inFile) {
		// TODO Auto-generated method stub
		ArrayList<String> list=ContentLoader.getAllLinesList(inFile);
		return list;
	}

}
