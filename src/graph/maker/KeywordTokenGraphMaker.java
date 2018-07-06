package graph.maker;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import file.token.mapping.FileTokenMapper;
import token.file.mapping.KeywordSrcFileMapper;

public class KeywordTokenGraphMaker {

	String bugTitleFile;
	String changesetFile;
	String srcCodeDir;
	HashMap<String, ArrayList<String>> fileTokenMap;
	HashMap<String, ArrayList<String>> tokenFileMap;
	HashMap<String, ArrayList<String>> keywordFileMap;
	HashMap<String, ArrayList<String>> fileKeywordMap;

	public KeywordTokenGraphMaker(String bugTitleFile, String changesetFile,
			String srcCodeDir) {
		this.bugTitleFile = bugTitleFile;
		this.changesetFile = changesetFile;
		this.srcCodeDir = srcCodeDir;
		this.fileTokenMap = new HashMap<>();
		this.tokenFileMap = new HashMap<>();
		this.keywordFileMap=new HashMap<>();
		this.fileKeywordMap=new HashMap<>();
	}

	protected String getFileName(String completePath) {
		return new File(completePath).getName();
	}

	public void UpdateMaps(String previousQuery,
			HashMap<String, ArrayList<String>> keywordFileMap,
			HashMap<String, ArrayList<String>> fileKeywordMap) 
	{
		KeywordSrcFileMapper mapper = new KeywordSrcFileMapper(
				this.changesetFile, this.bugTitleFile);
		mapper.UpdateMaps(previousQuery, keywordFileMap, fileKeywordMap);
	}
	
	
	public void developBipartiteGraph() {
		// develop bipartite graph between keywords and source code tokens
		KeywordSrcFileMapper mapper = new KeywordSrcFileMapper(
				this.changesetFile, this.bugTitleFile);
		HashMap<String, ArrayList<String>> key2FileMap = mapper
				.mapKeyword2SrcFile();
		keywordFileMap = key2FileMap;
		HashMap<String, ArrayList<String>> file2KeyMap=mapper.getSrcFile2KeywordMap();
		fileKeywordMap=file2KeyMap;
		/*HashMap<String, ArrayList<String>> adjacentList = mapper
				.getKeywordAdjacencyList();
		FileTokenMapper fTMapper = new FileTokenMapper(this.srcCodeDir);
		HashMap<String, ArrayList<String>> fileTokenMap1 = fTMapper
				.mappFile2Tokens();
		fileTokenMap=fileTokenMap1;
		HashMap<String, ArrayList<String>> tokenFileMap1 = fTMapper
				.getTokenFileMap();
		tokenFileMap=tokenFileMap1;*/
		//mapper.showBipartiteGraph();
		// now develop the map
		/*for (String keyword : key2FileMap.keySet()) {
			HashSet<String> srcTokens = new HashSet<>();
			ArrayList<String> fileList = key2FileMap.get(keyword);
			if(!fileList.isEmpty())this.keywordFileMap.put(keyword, fileList);
			System.out.println(keyword+": "+fileList);
			for (String fileURL : fileList) {
				String fileKey = getFileName(fileURL);
				if (fileTokenMap.containsKey(fileKey)) {
					srcTokens.addAll(fileTokenMap.get(fileKey));
				}
			}
			// now add the linking
			this.keywordTokenMap.put(keyword, new ArrayList<>(srcTokens));
			//this.keywordFileMap.put(keyword, fileList);
		}*/
	}

	public void showBipartiteGraph() {
		// showing the bi-partite graph (for keyword token map)
		/*for (String keyword : this.keywordTokenMap.keySet()) {
			System.out.println(keyword + ":"
					+ this.keywordTokenMap.get(keyword));
		}*/
		
		// showing the bi-partite graph (for keyword file map)
		int track=0;
		for (String keyword : this.keywordFileMap.keySet()) {
			System.out.println((++track)+" "+keyword + ":"
					+ this.keywordFileMap.get(keyword));
		}
	}
	

	
	public HashMap<String, ArrayList<String>> getKeywordFileMap() {
		return this.keywordFileMap;
	}
	
	public HashMap<String, ArrayList<String>> getFileKeywordMap() {
		return this.fileKeywordMap;
	}

	public HashMap<String, ArrayList<String>> gettokenFileMap() {
		return this.tokenFileMap;
	}
	
	public HashMap<String, ArrayList<String>> getfileTokenMap() {
		return this.fileTokenMap;
	}
	
	public static void main(String[] args) {
		String bugTitleFile = "./data/bugCorpus.txt";
		String gitInfoFile = "./data/GitInfoFile2.txt";
		String srcCodeDir = "./data/ExampleSourceCodeFiles";
		KeywordTokenGraphMaker gmaker = new KeywordTokenGraphMaker(
				bugTitleFile, gitInfoFile, srcCodeDir);
		gmaker.developBipartiteGraph();
		//gmaker.showBipartiteGraph();
	}
}
