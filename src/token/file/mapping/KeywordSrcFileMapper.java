package token.file.mapping;

import java.util.ArrayList;
import java.util.HashMap;
import adjacent.list.AdjacentListMaker;
import utility.ContentLoader;

public class KeywordSrcFileMapper {

	String changesetFile;
	String bugTitleFile;
	HashMap<Integer, ArrayList<String>> changeset;
	public HashMap<String, ArrayList<String>> token2SrcMap;
	public HashMap<String, ArrayList<String>> Src2tokenMap;
	public AdjacentListMaker maker;

	public KeywordSrcFileMapper(String changesetFile, String bugTitleFile) {
		// default constructor
		this.changesetFile = changesetFile;
		this.bugTitleFile = bugTitleFile;
		this.changeset = new HashMap<>();
		this.token2SrcMap = new HashMap<>();
		this.Src2tokenMap=new HashMap<>();
		this.loadChangeSet();
	}

	public void loadChangeSet() {
		// loading the change set
		ArrayList<String> lines = ContentLoader
				.getAllLinesList(this.changesetFile);
		int bugID=0;
		int filecount=0;
		for (int i = 0; i < lines.size();) {
			String currentLine = lines.get(i);
			
			System.out.println(currentLine+"i= "+i+" total lenghth= "+lines.size()+" bugID: "+bugID+" filecount: "+filecount);
			String[] items = currentLine.split("\\s+");
			if (items.length == 2) {
				 bugID = Integer.parseInt(items[0].trim());
				 filecount = Integer.parseInt(items[1].trim());
				System.out.println("Bug ID: "+bugID+" and Count: "+filecount);
				ArrayList<String> tempList = new ArrayList<>();
				for (int currIndex = i + 1; currIndex <= i + filecount; currIndex++) {
					if(!tempList.contains(lines.get(currIndex)))tempList.add(lines.get(currIndex));
				}
				// now store the change set to bug
				this.changeset.put(bugID, tempList);
				// now update the counter
				i = i + filecount;
				i++;
			}
		}
		System.out.println("Changeset reloaded successfully for :"
				+ this.changeset.size());
	}
    
	public HashMap<Integer, ArrayList<String>> getChangeSet()
	{
		return this.changeset;
	}
	
	public HashMap<String, ArrayList<String>> getKeywordAdjacencyList() {
		return this.maker.adjacentMap;
	}
    
	public HashMap<String, ArrayList<String>> getSrcFile2KeywordMap() {
		return this.Src2tokenMap;
	}
	
	public HashMap<String, ArrayList<String>> getKeyword2SrcFileMap() {
		return this.token2SrcMap;
	}
	
	public HashMap<String, ArrayList<String>> updateKeyword2SrcFile(){
		HashMap<String, ArrayList<String>> hm=new HashMap<>();
		return hm;
	}
	

	//public void mapKeyword2SrcFile() {
	public HashMap<String, ArrayList<String>> mapKeyword2SrcFile() {
		// mapping the bug keywords to source file
		// collecting adjacency token and token-bugID mapping
		this.maker = new AdjacentListMaker(bugTitleFile, true);
		maker.makeAdjacentList();
		for (String token : maker.token2BugMap.keySet()) {
			ArrayList<Integer> bugIDs = maker.token2BugMap.get(token);
			ArrayList<String> tempList = new ArrayList<>();
			for (int bugID : bugIDs) {
				if (this.changeset.containsKey(bugID)) {
					if(!tempList.contains(this.changeset.get(bugIDs)))tempList.addAll(this.changeset.get(bugID));
				}
			}
			if(!tempList.isEmpty())this.token2SrcMap.put(token, tempList);
			// now create source file 2 keywords mapping
			for (String file : tempList) {
				if (this.Src2tokenMap.containsKey(file)) {
					ArrayList<String> tokens = this.Src2tokenMap
							.get(file);
					if(!tokens.contains(token))tokens.add(token);
					this.Src2tokenMap.put(file, tokens);
				} else {
					ArrayList<String> tokens = new ArrayList<>();
					if(!tokens.contains(token))tokens.add(token);
					this.Src2tokenMap.put(file, tokens);
				}
			}
		}
		System.out.println("Created map:" + this.token2SrcMap.keySet().size());
		this.showBipartiteGraph();
		return this.token2SrcMap;
	}

	
	
	public void showBipartiteGraph() {
		// showing the bi-partite graph (for keyword file map)
		int c=0;
		for (String keyword : this.token2SrcMap.keySet()) {
			//if(!keyword.equalsIgnoreCase("maven"))
				System.out.println((++c)+" "+this.token2SrcMap.keySet().size()+" "+keyword + ":"
					+ this.token2SrcMap.get(keyword));
		//if(c>10)break;
		}
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String bugTitleFile = "./data/BugCorpus/bugCorpusFiltered500TandD.txt";
		String gitInfoFile = "./data/GitInfoFile2.txt";
		KeywordSrcFileMapper mapper = new KeywordSrcFileMapper(gitInfoFile,
				bugTitleFile);
		mapper.mapKeyword2SrcFile();
		mapper.showBipartiteGraph();
	}

	public void UpdateMaps(String previousQuery,
			HashMap<String, ArrayList<String>> keywordFileMap,
			HashMap<String, ArrayList<String>> fileKeywordMap) {
		// TODO Auto-generated method stub
		String [] spilter=previousQuery.split(" ");
		String bugIDwithText=spilter[0];
		String bugID=bugIDwithText.substring(0, bugIDwithText.length()-5);
		
		HashMap<Integer, ArrayList<String>> changeSet=this.changeset;
		if(spilter.length>=2)
		{
		if(changeSet.containsKey(Integer.valueOf(bugID)))
		{
			ArrayList<String> tempList=changeSet.get(Integer.valueOf(bugID));
			if(tempList.size()>0)
			{
			for(int i=1;i<spilter.length;i++)
			{
				String keyword=spilter[i];
				keyword=keyword.trim();
				if(token2SrcMap.containsKey(keyword))
				{
					ArrayList<String> tempListFromT2SMap=token2SrcMap.get(keyword);
					for(String file:tempList)
					{
						if(!tempListFromT2SMap.contains(file))
						{
							tempListFromT2SMap.add(file);
							
						}
					}
					token2SrcMap.put(keyword, tempListFromT2SMap);
				}
				else
				{
					ArrayList<String> tempListFromT2SMap=token2SrcMap.get(keyword);
					token2SrcMap.put(keyword, tempList);
				}
			}
			}
		}
		}
	}
}
