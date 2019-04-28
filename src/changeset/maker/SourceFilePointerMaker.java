package changeset.maker;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import config.StaticData;
import utility.ContentLoader;
import utility.ContentWriter;
import utility.MiscUtility;

public class SourceFilePointerMaker {

	String changeDir;
	String corpus;
	String allFileInfo;
	HashMap<String, Integer> fileIDMap;
	HashMap<Integer, String> bugIDFileMap;
	ArrayList<String> totalFileList;

	public SourceFilePointerMaker(String changeDir, String corpus, String allFileInfo) {
		this.changeDir = changeDir;
		this.corpus=corpus;
		this.allFileInfo=allFileInfo;
		this.fileIDMap = new HashMap<>();
		this.bugIDFileMap = new HashMap<>();
		this.totalFileList=new ArrayList<>();
	}

	protected HashMap<String, Integer> developSourceFilePointer() {
		this.totalFileList=ContentLoader.getAllLinesList(this.allFileInfo);
		File[] files = new File(this.changeDir).listFiles();
		int index = 0;
		HashMap<Integer,String> tempFileMap=new HashMap<>();
		for (File f : files) {
			ArrayList<String> srcFiles = ContentLoader.getAllLinesOptList(f
					.getAbsolutePath());
			int count=0;
			
			for (String srcFile : srcFiles) {
				
				
				if (!fileIDMap.containsKey(srcFile)) {
					fileIDMap.put(srcFile, ++index);
					tempFileMap.put(index, srcFile);
				}
			}
			
		}
		
		// saving the map
		this.saveIDSourceMap(tempFileMap,"E:\\PhD\\Repo\\"+this.corpus+"\\data\\changeset-pointer\\ID-SourceFile.txt");
		
        for(String file:this.totalFileList)
        {
            if(!tempFileMap.containsValue(file))
            {
                tempFileMap.put(++index, file);
            }
        }
        this.saveIDSourceMap(tempFileMap, "E:\\PhD\\BugLocatorP2\\FileKeyMap\\"+this.corpus+"FileKeyMap.txt");
		return fileIDMap;
	}

	protected boolean IsFileExist(String file, ArrayList<String> list)
	{
		if(list.contains(file)) return true;
		else return false;
	}
	
	protected void saveIDSourceMap(HashMap<Integer,String> tempFileIDMap, String outputFile) {
		ArrayList<String> tempList = new ArrayList<>();
		int size = tempFileIDMap.size();
		for (int key = 1; key <= size; key++) {
			tempList.add(key + ": " + tempFileIDMap.get(key));
		}
		
		//String outputFile = "./data/changeset-pointer/ID-SourceFile.txt";
		ContentWriter.writeContent(outputFile, tempList);
		System.out.println("Done!");
	}

	protected void developBugSrcFilePointer(String corpus) {
		HashMap<String, Integer> fileIDMap = developSourceFilePointer();
		File[] files = new File(this.changeDir).listFiles();
		ArrayList<String> bugSrcList = new ArrayList<>();
		for (File f : files) {
			int bugID = Integer.parseInt(f.getName().split("\\.")[0]);
			ArrayList<String> srcFiles = ContentLoader.getAllLinesOptList(f
					.getAbsolutePath());
			ArrayList<Integer> tempIDs = new ArrayList<>();
			for (String srcURL : srcFiles) {
				if (fileIDMap.containsKey(srcURL)) {
					tempIDs.add(fileIDMap.get(srcURL));
				}
			}
			bugSrcList.add(bugID + ":" + MiscUtility.listInt2Str(tempIDs));
		}
		String outputFile = "E:/PhD/Repo/"+corpus+"/data/changeset-pointer/Bug-ID-SrcFile-ID-Mapping.txt";
		ContentWriter.writeContent(outputFile, bugSrcList);
		System.out.println("Done!");
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//String changeDir="/Users/user/Documents/Ph.D/2018/Data/changeset/";
	    String corpus="ZXing";
		String changeDir="E:\\PhD\\Repo\\"+corpus+"\\data\\changeset\\";
		String allFileInfo="E:\\PhD\\Repo\\"+corpus+"\\data\\allFilesName.txt\\";
		new SourceFilePointerMaker(changeDir,corpus,allFileInfo).developBugSrcFilePointer(corpus);
	}
}
