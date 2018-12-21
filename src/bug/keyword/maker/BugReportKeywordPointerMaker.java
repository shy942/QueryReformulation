package bug.keyword.maker;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import config.StaticData;
import utility.ContentLoader;
import utility.ContentLoaderQR;
import utility.ContentWriter;
import utility.MiscUtility;

public class BugReportKeywordPointerMaker {

	String bugIDsAddress;
	String bugReportDir;
	HashMap<String, Integer> keywordIDMap;
	HashMap<Integer, String> bugIDKeywordMap;
    ArrayList<String> bugIDsList;
    String base;
	public BugReportKeywordPointerMaker(String bugReportDir, String bugIDsAddress, String base) {
		this.bugReportDir = bugReportDir;
		this.bugIDsAddress=bugIDsAddress;
		this.bugIDsAddress=ContentLoader.readContentSimple(this.bugIDsAddress);
		this.keywordIDMap = new HashMap<>();
		this.bugIDKeywordMap = new HashMap<>();
		this.base=base;
	}

	protected HashMap<String, Integer> developKeywordPointer() {
		File[] files = new File(this.bugReportDir).listFiles();
		int index = 0;
		HashMap<Integer, String> tempFileMap = new HashMap<>();
		for (File f : files) {
			//System.out.println(f.getName());
			ArrayList<String> keywordList = ContentLoader.getAllKeywords(f
					.getAbsolutePath());
			//ArrayList<String> keywordList = ContentLoader.getAllLinesList(f
					//.getAbsolutePath());
			//System.out.println(keywordList);
			for (String keyword : keywordList) {
				System.out.println(keyword);
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
		String outputFile = this.base+"\\data\\ID-Keyword.txt";
		ContentWriter.writeContent(outputFile, tempList);
		System.out.println("Done!");
	}

	protected void developBugKeywordPointer() {
		HashMap<String, Integer> keywordIDMap = developKeywordPointer();
		File[] files = new File(this.bugReportDir).listFiles();
		ArrayList<String> bugSrcList = new ArrayList<>();
		for (File f : files) {
			if(!f.getName().equals(".DS_Store"))
			{
				if(this.bugIDsAddress.contains(f.getName().split("\\.")[0].trim())){
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
			}
		}
		String outputFile = this.base+"\\data\\Bug-ID-Keyword-ID-Mapping.txt";
		ContentWriter.writeContent(outputFile, bugSrcList);
		System.out.println("Done!");  
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String base="E:PhD\\Repo\\SWT";
		String bugReportDir = base+"\\BugData\\";  
		new BugReportKeywordPointerMaker(bugReportDir,base+"\\data\\allBug.txt",base).developBugKeywordPointer();
	}
}
