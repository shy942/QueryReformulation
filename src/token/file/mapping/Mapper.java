package token.file.mapping;

import java.util.ArrayList;
import java.util.HashMap;

import utility.ContentLoader;
import utility.ContentWriter;
import utility.MiscUtility;

public class Mapper {

	String bugKeywordFile;
	static HashMap<Integer, ArrayList<Integer>> bugKeywordMap;
	static HashMap<Integer, ArrayList<Integer>> bugSourceMap;
	static HashMap<Integer, ArrayList<Integer>> KeywordSourceMap;
	static HashMap<Integer, ArrayList<Integer>> tokenBugMap;
	
	public Mapper(String bugKeywordFile)
	{
		this.bugKeywordFile=bugKeywordFile;
		this.bugKeywordMap=new HashMap<Integer, ArrayList<Integer>>();
		this.bugSourceMap=new HashMap<Integer, ArrayList<Integer>>();
		this.KeywordSourceMap=new HashMap<Integer, ArrayList<Integer>>();
		this.tokenBugMap=new HashMap<>();
	}
	
	
	//Load file 
	public HashMap<Integer, ArrayList<Integer>> LoadMap(String filePath)
	{
		HashMap<Integer, ArrayList<Integer>> hm= new HashMap<>();
		ArrayList<String> lines = ContentLoader
				.getAllLinesOptList(filePath);
		
		for (String bkLine : lines) {
			String[] parts = bkLine.split(":");
				if (parts.length <2)
				continue;
				Integer BugID=Integer.parseInt(parts[0]);
				ArrayList<Integer> intKeyword=new ArrayList<Integer>();
				String[] strKeyword=parts[1].split("\\s+");
				for(String key: strKeyword)
				{
					intKeyword.add(Integer.parseInt(key));
				}
			
			hm.put(BugID, intKeyword);
			//System.out.println(BugID);
		}
		
		//MiscUtility.showResult(100, bugKeywordMap);
		return hm;
		
	}
	
	public  HashMap<Integer, ArrayList<Integer>> CreateTokenBugMap(HashMap<Integer, ArrayList<Integer>> bugKeywordMap )
	{
		
		
		for (int bugID : bugKeywordMap.keySet()) {
			ArrayList<Integer> keywords = bugKeywordMap.get(bugID);
			
			for (int token : keywords) {
				if (tokenBugMap.containsKey(token)) {
					ArrayList<Integer> tempList=tokenBugMap.get(token);
					if(!tempList.contains(bugID))
					{
						tempList.add(bugID);
						tokenBugMap.put(token, tempList);
						//System.out.println("Bug ID: "+bugID+" token: "+token);
					}
				}
				else
				{
				   ArrayList<Integer> tempList=new ArrayList<Integer>();
				   tempList.add(bugID);
				   tokenBugMap.put(token, tempList);
				   //System.out.println("Bug ID: "+bugID+" token: "+token);
				}
			}
	
		}
		
		//MiscUtility.showResult(10, tokenBugMap);
		return tokenBugMap;
	}
	
	public  HashMap<Integer, ArrayList<Integer>> CreateTokenSouceFileMap(HashMap<Integer, ArrayList<Integer>> tokenBugMap, HashMap<Integer, ArrayList<Integer>> bugSourceMap, String tokenSourceFile)
	{
		HashMap<Integer, ArrayList<Integer>> tokenSourceMap=new HashMap<>();
		
		for (int keyword : tokenBugMap.keySet()) {
			ArrayList<Integer>  bugIDs= tokenBugMap.get(keyword);
			ArrayList<Integer> tokenSourceList=new ArrayList<>();
			
			for (int ID : bugIDs) {
				if (bugSourceMap.containsKey(ID)) {
					ArrayList<Integer> sorceList=bugSourceMap.get(ID);
					for(Integer s:sorceList){
						if(tokenSourceList.contains(s)) continue;
						else tokenSourceList.add(s);
					}
					
				}
			}
			
			if(tokenSourceList.size()>0)
			{
				//System.out.println(tokenSourceList.size());
				String slist="";
				for(int i=0;i<tokenSourceList.size();i++)slist+=tokenSourceList.get(i)+" ";
				//System.out.println(keyword+"\n"+ slist);
				
			
			tokenSourceMap.put(keyword, tokenSourceList);
			}
		}
		System.out.println();
		int max=0;
		int min=100;
		int count200=0;
		for(int token:tokenSourceMap.keySet())
		{
			int len=tokenSourceMap.get(token).size();
			if(len<100)count200++;
			if(len>max)max=len;
			else if(len<min)min=len;
			System.out.println(tokenSourceMap.get(token).size()+" ");
			ContentWriter.appendContent(tokenSourceFile, token+":\n"+tokenSourceMap.get(token));
		}
		//ContentWriter.writeContent(tokenSourceFile, List);
		//MiscUtility.showResult(10, tokenSourceMap);
		System.out.println(min+" "+max+" length<100 "+count200);
		return tokenSourceMap;
	}
	
	public  void CreateSourceToTokenMap(HashMap<Integer, ArrayList<Integer>> tokenSourceMap, String SourceTokenFile)
	{
		ArrayList<String> SourceTokenList=new ArrayList<>();
		HashMap<Integer, ArrayList<Integer>> SourceTokenMap=new HashMap<>();
		for(int key:tokenSourceMap.keySet())
		{
			ArrayList<Integer> sourceList=tokenSourceMap.get(key);
			for(int Sid:sourceList)
			{
				ArrayList<Integer> list=new ArrayList<>();
				if(SourceTokenMap.containsKey(Sid))
				{
					list=SourceTokenMap.get(Sid);
					list.add(key);
				}
				else
				{
					list.add(key);
				}
				if(!list.isEmpty())SourceTokenMap.put(Sid, list);
			}
		}
		for(int Sid:SourceTokenMap.keySet())
		{
			SourceTokenList.add(Sid+":"+MiscUtility.listInt2Str(SourceTokenMap.get(Sid)));
		}
		ContentWriter.writeContent(SourceTokenFile, SourceTokenList);
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		long start=System.currentTimeMillis();
		int tatoaltrain=20;
		for(int i=1;i<=tatoaltrain;i++)
		{
			/*
			int train=i;
		Mapper obj=new Mapper("./data/trainset/Train"+train+".txt");
		obj.bugKeywordMap=obj.LoadMap("./data/trainset/Train"+train+".txt");
		obj.CreateTokenBugMap(obj.bugKeywordMap);
		long end=System.currentTimeMillis();
		System.out.println("Time elapsed:"+(end-start)/1000+" s");
		
		
		obj.bugSourceMap=obj.LoadMap("./data/changeset-pointer/Bug-ID-SrcFile-ID-Mapping.txt");
		obj.KeywordSourceMap=obj.CreateTokenSouceFileMap(obj.tokenBugMap,obj.bugSourceMap, "./data/FinalMap/TokenSourceMapTrainset"+train+".txt");*/
			int train=i;
			String base="E:\\PhD\\Repo\\Zxing";
			Mapper obj=new Mapper(base+"\\data\\trainset\\Train"+train+".txt");
			obj.bugKeywordMap=obj.LoadMap(base+"\\data\\trainset\\Train"+train+".txt");
			obj.tokenBugMap=obj.CreateTokenBugMap(obj.bugKeywordMap);
			//long end=System.currentTimeMillis();
			//System.out.println("Time elapsed:"+(end-start)/1000+" s");
			
			
			obj.bugSourceMap=obj.LoadMap(base+"\\data\\changeset-pointer\\Bug-ID-SrcFile-ID-Mapping.txt");
			obj.KeywordSourceMap=obj.CreateTokenSouceFileMap(obj.tokenBugMap,obj.bugSourceMap, base+"\\data\\FinalMap\\TokenSourceMapTrainset"+train+".txt");
			//obj.CreateSourceToTokenMap(obj.tokenSourceMap, "./data/FinalMap/SourceTokenMapTrainset"+train+".txt");
		}
	}

	
	
}
