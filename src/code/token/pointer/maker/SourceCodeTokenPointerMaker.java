package code.token.pointer.maker;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import config.StaticData;
import utility.ContentLoader;
import utility.ContentWriter;
import utility.FileMapLoader;
import utility.MiscUtility;

public class SourceCodeTokenPointerMaker {

	String sourceDir;
	HashMap<String, Integer> tokenIDMap;
	HashMap<Integer, String> srcIDTokendMap;
	String changesetIDSrcFile;
	HashMap<Integer, String> keyFileMap = new HashMap<>();

	public SourceCodeTokenPointerMaker(String sourceDir,
			String changesetIDSrcFile) {
		this.sourceDir = sourceDir;
		this.tokenIDMap = new HashMap<>();
		this.srcIDTokendMap = new HashMap<>();
		this.changesetIDSrcFile = changesetIDSrcFile;
		this.keyFileMap = FileMapLoader
				.loadIDSrcFileMap(this.changesetIDSrcFile);
	}

	protected HashMap<String, Integer> developKeywordPointer() {
		HashMap<String, Integer> tokenMap = new HashMap<>();
		HashMap<Integer, String> idTokenMap = new HashMap<>();
		int index = 0;
		for (int key : keyFileMap.keySet()) {
			String fileURL = this.keyFileMap.get(key);
			String fileName = new File(fileURL).getName();
			String srcFile = this.sourceDir + "/" + fileName;
			ArrayList<String> lines = ContentLoader.getAllLinesOptList(srcFile);
			// String canonicalURL = lines.get(0);
			if (lines.size() < 2)
				continue;
			ArrayList<String> tokenList = MiscUtility.str2List(lines.get(1));
			for (String token : tokenList) {
				if (!tokenMap.containsKey(token)) { 
					tokenMap.put(token, ++index);
					idTokenMap.put(index, token);
				}
			}
		}

		// saving the map
		this.saveIDKeywordMap(idTokenMap);

		return tokenMap;
	}

	protected void saveIDKeywordMap(HashMap<Integer, String> tempFileIDMap) {
		ArrayList<String> tempList = new ArrayList<>();
		int size = tempFileIDMap.size();
		for (int key = 1; key <= size; key++) {
			tempList.add(key + ": " + tempFileIDMap.get(key));
		}
		String outputFile = "./data/source-token-pointer/ID-Token.txt";
		ContentWriter.writeContent(outputFile, tempList);
		System.out.println("Done!");
	}

	protected void developFileKeywordPointer() {
		HashMap<String, Integer> keywordIDMap = developKeywordPointer();
		// File[] files = new File(this.sourceDir).listFiles();
		ArrayList<String> bugSrcList = new ArrayList<>();
		for (int fileKey : this.keyFileMap.keySet()) {
			String fileURL = this.keyFileMap.get(fileKey);
			String fileName = new File(fileURL).getName();
			String filePath = this.sourceDir + "/" + fileName;
			ArrayList<String> keywords = ContentLoader.getAllKeywords(filePath);
			ArrayList<Integer> tempIDs = new ArrayList<>();
			for (String keyword : keywords) {
				if (keywordIDMap.containsKey(keyword)) {
					tempIDs.add(keywordIDMap.get(keyword));
				}
			}
			bugSrcList.add(fileKey + ":" + MiscUtility.listInt2Str(tempIDs));
		}
		String outputFile = "./data/source-token-pointer/SrcFile-ID-Token-ID-Mapping.txt";
		ContentWriter.writeContent(outputFile, bugSrcList);
		System.out.println("Done!");
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String sourceDir = "/Users/user/Documents/Ph.D/2018/Data/ProcessedSourceForBL";
				//"./data/ExampleSourceCodeFilesFiltered";
		String idSrcFile = "./data/changeset-pointer/ID-SourceFile.txt";
		new SourceCodeTokenPointerMaker(sourceDir, idSrcFile)
				.developFileKeywordPointer();
	}
}
