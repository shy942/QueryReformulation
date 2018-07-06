package bug.keyword.maker;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import config.StaticData;
import utility.ContentLoader;
import utility.ContentWriter;
import utility.MiscUtility;

public class BugReportKeywordPointerMaker {

	String bugReportDir;
	HashMap<String, Integer> keywordIDMap;
	HashMap<Integer, String> bugIDKeywordMap;

	public BugReportKeywordPointerMaker(String bugReportDir) {
		this.bugReportDir = bugReportDir;
		this.keywordIDMap = new HashMap<>();
		this.bugIDKeywordMap = new HashMap<>();
	}

	protected HashMap<String, Integer> developKeywordPointer() {
		File[] files = new File(this.bugReportDir).listFiles();
		int index = 0;
		HashMap<Integer, String> tempFileMap = new HashMap<>();
		for (File f : files) {
			ArrayList<String> keywordList = ContentLoader.getAllKeywords(f
					.getAbsolutePath());
			for (String keyword : keywordList) {
				if (!keywordIDMap.containsKey(keyword)) {
					keywordIDMap.put(keyword, ++index);
					tempFileMap.put(index, keyword);
				}
			}
		}

		// saving the map
		this.saveIDKeywordMap(tempFileMap);

		return keywordIDMap;
	}

	protected void saveIDKeywordMap(HashMap<Integer, String> tempFileIDMap) {
		ArrayList<String> tempList = new ArrayList<>();
		int size = tempFileIDMap.size();
		for (int key = 1; key <= size; key++) {
			tempList.add(key + ": " + tempFileIDMap.get(key));
		}
		String outputFile = "./data/bug-keyword-pointer/ID-Keyword.txt";
		ContentWriter.writeContent(outputFile, tempList);
		System.out.println("Done!");
	}

	protected void developBugKeywordPointer() {
		HashMap<String, Integer> keywordIDMap = developKeywordPointer();
		File[] files = new File(this.bugReportDir).listFiles();
		ArrayList<String> bugSrcList = new ArrayList<>();
		for (File f : files) {
			int bugID = Integer.parseInt(f.getName().split("\\.")[0]);
			ArrayList<String> keywords = ContentLoader.getAllKeywords(f
					.getAbsolutePath());
			ArrayList<Integer> tempIDs = new ArrayList<>();
			for (String keyword : keywords) {
				if (keywordIDMap.containsKey(keyword)) {
					tempIDs.add(keywordIDMap.get(keyword));
				}
			}
			bugSrcList.add(bugID + ":" + MiscUtility.listInt2Str(tempIDs));
		}
		String outputFile = "./data/bug-keyword-pointer/Bug-ID-Keyword-ID-Mapping.txt";
		ContentWriter.writeContent(outputFile, bugSrcList);
		System.out.println("Done!");
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String bugReportDir = "./data/BugCorpus/ProcessedData";
		new BugReportKeywordPointerMaker(bugReportDir).developBugKeywordPointer();
	}
}
