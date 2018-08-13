package corpus.maker;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import utility.ContentLoader;
import utility.ContentWriter;

public class SourceCodeMethodLevelStemm {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
       new SourceCodeMethodLevelStemm().PreprocessMethodLevelFiles("/Users/user/Documents/workspace-Sep16/QueryReformulation/data/ExampleSourceCodeFilesSortedContents/");
	}

	
	public void PreprocessMethodLevelFiles(String folderPath)
	{
		File[] files = new File(folderPath).listFiles();
		HashMap<String, ArrayList<String>> docMap = new HashMap<>();
		for (File file : files) {
			if(!file.getName().equals(".DS_Store")){
			ArrayList<String> lines = ContentLoader.getDocTokensAll(file
					.getAbsolutePath());
			if(lines.size()>2){
			//line 1 contains full path
			//line 2 contains no. of methods
			//line 3 to line n contain method contents.
			String filePathOld=lines.get(0);
			
			System.out.println(filePathOld);
			int index = nthOccurrence(filePathOld, '/', 7);
			 
			filePathOld = filePathOld.substring(index+1, filePathOld.length());
			String filePathNew=filePathOld.replaceAll("/", ".");
			
			System.out.println(filePathNew);
			//ContentWriter.writeContent(outFolder+filePathNew, content);
			}}
		}
	}
	
	public static int nthOccurrence(String s, char c, int occurrence) {
	    return nthOccurrence(s, 0, c, 0, occurrence);
	}

	public static int nthOccurrence(String s, int from, char c, int curr, int expected) {
	    final int index = s.indexOf(c, from);
	    if(index == -1) return -1;
	    return (curr + 1 == expected) ? index : 
	        nthOccurrence(s, index + 1, c, curr + 1, expected);
	}

}
