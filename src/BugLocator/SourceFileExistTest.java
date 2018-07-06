package BugLocator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import source.visitor.CommentFilterer;
import utility.ContentLoader;

public class SourceFileExistTest {

	ArrayList<String> listOFFilesExist;
	
	
	public SourceFileExistTest(String folderPath)
	{
		this.listOFFilesExist=new ArrayList<>();
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		/*String folderPath="/Users/user/Downloads/buglocator1/Src/eclipse.platform.ui-master";
		SourceFileExistTest obj=new SourceFileExistTest(folderPath);
		File currentDir = new File(folderPath); // current directory
		obj.displayDirectoryContents(currentDir,obj);
		ArrayList<String> list=new ArrayList<>();
		list=obj.returnList();
		for(int i=0;i<list.size();i++)
		{
			if(list.get(i).equals("dynamic_classes/browser/org/eclipse/ui/dynamic/DynamicBrowserSupport.java"))System.out.println(list.get(i));
		}*/
		
		
		//2nd Stratagy
		
		String folderPath="/Users/user/Downloads/buglocator1/Src/eclipse.platform.ui-master";
		SourceFileExistTest obj=new SourceFileExistTest(folderPath);
		File currentDir = new File(folderPath); // current directory
		obj.displayDirectoryContents(currentDir,obj,1);
		
		
		
	}

	
	
	public static void displayDirectoryContents(File dir, SourceFileExistTest obj, int s) {
		try {
			File[] files = dir.listFiles();
			for (File file : files) {
				if (file.isDirectory()) {
					//System.out.println("directory:" + 
				    String folder=file.getCanonicalPath();
					if(s==1)displayDirectoryContents(file,obj,1);
					else displayDirectoryContents(file, obj, 0);
				} else {
					//System.out.println("     file:" + file.getAbsolutePath());
					if(s==1)obj.createExistingFileList(file);
					else obj.getPackageName(file);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void getPackageName(File file)
	{
		ArrayList<String> content=ContentLoader.getAllLinesList(file.getAbsolutePath());
		String pacakgeName=content.get(1);
		
		if(pacakgeName.contains("package"))
			{
			    String proStr=this.processPackageName(pacakgeName);
				//System.out.println(proStr);
				String wholeStr=proStr+"."+file.getName();
				//System.out.println(wholeStr);
				listOFFilesExist.add(wholeStr);
			}
	}
	
	public String processPackageName(String packageName)
	{
		String subStr=packageName.substring(8, packageName.length()-1);
		//System.out.println(subStr);
		return subStr;
	}
	
	public void createExistingFileList(File file)
	{
		//ArrayList<String> savedInfo=new ArrayList<>();
		
		String filePath=file.getAbsolutePath();
		int index = nthOccurrence(filePath, '/', 10);
	    
		
		filePath=filePath.substring(index+1, filePath.length());
		
		//System.out.println(filePath);
		
		if(filePath.endsWith(".java"))
			{
				this.listOFFilesExist.add(filePath.replaceAll("/", "."));
			}
	}
	
     
	public ArrayList<String> returnList()
	{
		return this.listOFFilesExist;
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
