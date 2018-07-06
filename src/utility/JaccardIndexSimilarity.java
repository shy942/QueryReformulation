package utility;

import java.util.ArrayList;
import java.util.Arrays;

public class JaccardIndexSimilarity {

	/**
	 * @param args
	 */
	
	public ArrayList<String> keywordList;
	public ArrayList <String> listOfReformedQueryWords;
	
	public JaccardIndexSimilarity(ArrayList<String> keywordList, ArrayList <String> listOfReformedQueryWords)
	{
		this.keywordList=keywordList;
		this.listOfReformedQueryWords=listOfReformedQueryWords;
	}
	
	public Double ComputeJaccardIndexSimilarity()
	{
		System.out.println("[[[[[[[[[[[[[["+keywordList);
		double similarity=0.0;
	    int common=0;
		
		for(String keyword:keywordList)
		{
			if(listOfReformedQueryWords.contains(keyword.trim()))
					{
						common++;
					}
		}
		int keywordLength=keywordList.size();
		int queryWordLength=listOfReformedQueryWords.size();
		int maxLenth=keywordLength;
		if(keywordLength<queryWordLength)
		{
			maxLenth=queryWordLength;
		}
		similarity=Double.valueOf(common)/Double.valueOf(maxLenth);
		
		return similarity;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
        
		ArrayList<String> keywordList=new ArrayList<>();
		keywordList.add("times");
		keywordList.add("tool");
		keywordList.add("dirty");
		ArrayList<String> listOfReformedQueryWords=new ArrayList<>();
		listOfReformedQueryWords.add("imes");
		listOfReformedQueryWords.add("tool");
		listOfReformedQueryWords.add("dirty");
		
		JaccardIndexSimilarity objJSim=new JaccardIndexSimilarity(keywordList, listOfReformedQueryWords);
        System.out.println(objJSim.ComputeJaccardIndexSimilarity());   
	}

}
