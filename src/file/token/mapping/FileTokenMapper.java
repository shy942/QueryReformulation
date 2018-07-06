package file.token.mapping;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import utility.ContentLoader;

public class FileTokenMapper {

	String codeDir;
	HashMap<String, ArrayList<String>> fileTokenMap;
	HashMap<String, ArrayList<String>> tokenFileMap;

	public FileTokenMapper(String codeDir) {
		this.codeDir = codeDir;
		this.fileTokenMap = new HashMap<>();
		this.tokenFileMap = new HashMap<>(); 
	}

	public HashMap<String, ArrayList<String>> mappFile2Tokens() {
		// map tokens to code files
		File dir = new File(this.codeDir);
		if (dir.isDirectory()) {
			File[] files = dir.listFiles();
			for (File file : files) {
				String[] srcTokens = ContentLoader.getAllTokens(file
						.getAbsolutePath());
				ArrayList<String> tempList = new ArrayList<>(
						Arrays.asList(srcTokens));
				ArrayList<String> list=ContentLoader.readContent(file.getAbsolutePath());
				String fullFile=list.get(0);
				String base = "/Users/user/eclipse.platform.ui/";
				String fileName=fullFile.substring(base.length());
				String key=fileName;
				//String key = file.getName();
				this.fileTokenMap.put(key, tempList);

				// now create token 2 File mapping
				for (String token : tempList) {
					if (this.tokenFileMap.containsKey(token)) {
						ArrayList<String> fileNames = this.tokenFileMap
								.get(token);
						fileNames.add(key);
						this.tokenFileMap.put(token, fileNames);
					} else {
						ArrayList<String> fileNames = new ArrayList<>();
						fileNames.add(key);
						this.tokenFileMap.put(token, fileNames);
					}
				}
			}
			//System.out.println("File mapped to Tokens: "
					//+ this.fileTokenMap.keySet().size());
			//System.out.println("Token mapped to Files: "
					//+ this.tokenFileMap.keySet().size());

		}
		//showBipartiteGraph();
		return this.fileTokenMap;
	}

	public HashMap<String, ArrayList<String>> getTokenFileMap() {
		return this.tokenFileMap;
	}
	public HashMap<String, ArrayList<String>> getFileTokenMap() {
		return this.fileTokenMap;
	}
	public void showFileTokenMap() {
		// showing file token map
		int c=0;
		for (String keyword : this.fileTokenMap.keySet()) {
			//if(!keyword.equalsIgnoreCase("maven"))
				System.out.println((++c)+" "+this.fileTokenMap.keySet().size()+" "+keyword + ":"
					+ this.fileTokenMap.get(keyword));
		//if(c>10)break;
		}
	}

	public void showTokenFileMap() {
		//showing token file map	
		int count=0;
		for (String keyword : this.tokenFileMap.keySet()) {
				//if(!keyword.equalsIgnoreCase("maven"))
				System.out.println((++count)+" "+this.tokenFileMap.keySet().size()+" "+keyword + ":"
							+ this.tokenFileMap.get(keyword));
		
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String srcCodeDir = "./data/ExampleSourceCodeFiles";
		new FileTokenMapper(srcCodeDir).mappFile2Tokens();
	}
}
